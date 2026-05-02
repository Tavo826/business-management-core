#!/usr/bin/env bash
# One-time bootstrap for Business Manager Core on a fresh Amazon Linux EC2.
# Re-runnable: each step checks current state before acting.
#
# Prerequisites:
#   - EC2 has IAM role with s3:GetObject on $S3_BUCKET
#   - Run as a user with sudo (e.g. ec2-user)
#   - Repo already cloned and cwd is the repo root:
#       sudo yum install -y git && git clone <repo> && cd business-management-core
#   - .env file present in the repo root (see application.yaml for required vars)
#   - DNS A record for $DOMAIN already points at this EC2's public IPv4
#
# Usage:
#   ./deployment/setup.sh
#   DOMAIN=foo.com CERTBOT_EMAIL=me@foo.com ./deployment/setup.sh

set -euo pipefail

# -------- Configuration (override via env vars) --------
DOMAIN="${DOMAIN:-customermanagement.top}"
DOMAIN_WWW="${DOMAIN_WWW:-www.${DOMAIN}}"
CERTBOT_EMAIL="${CERTBOT_EMAIL:-9gagigor816@gmail.com}"
S3_BUCKET="${S3_BUCKET:-business-manager-core-jar-container}"
JAR_NAME="${JAR_NAME:-BusinessManagerCore.jar}"
JAR_DEST_DIR="applications/app-service/build/libs"
SSL_CERT_PATH="certbot/conf/live/${DOMAIN}/fullchain.pem"
# -------------------------------------------------------

log()  { printf '\n\033[1;34m==>\033[0m %s\n' "$*"; }
warn() { printf '\033[1;33m[warn]\033[0m %s\n' "$*"; }
err()  { printf '\033[1;31m[error]\033[0m %s\n' "$*" >&2; exit 1; }

[[ -f "docker-compose.yml" ]] || err "Run from repo root (docker-compose.yml not found)."
[[ -f ".env" ]] || err ".env not found — create it before running this script."

# 1. System packages
log "Installing system packages (docker, git)..."
sudo yum update -y
sudo yum install -y docker git

sudo systemctl enable --now docker

# 2. docker compose plugin
if ! sudo docker compose version >/dev/null 2>&1; then
  log "Installing docker compose plugin..."
  DOCKER_COMPOSE_VERSION="$(curl -fsSL https://api.github.com/repos/docker/compose/releases/latest | grep tag_name | cut -d '"' -f 4)"
  sudo mkdir -p /usr/local/lib/docker/cli-plugins
  sudo curl -fsSL "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-linux-$(uname -m)" \
    -o /usr/local/lib/docker/cli-plugins/docker-compose
  sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
else
  log "docker compose already installed — skipping."
fi

# 3. docker buildx plugin
if ! sudo docker buildx version >/dev/null 2>&1; then
  log "Installing docker buildx plugin..."
  BUILDX_VERSION="$(curl -fsSL https://api.github.com/repos/docker/buildx/releases/latest | grep tag_name | cut -d '"' -f 4)"
  sudo curl -fsSL "https://github.com/docker/buildx/releases/download/${BUILDX_VERSION}/buildx-${BUILDX_VERSION}.linux-amd64" \
    -o /usr/local/lib/docker/cli-plugins/docker-buildx
  sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-buildx
else
  log "docker buildx already installed — skipping."
fi

# 4. Pull jar from S3
log "Downloading ${JAR_NAME} from s3://${S3_BUCKET}/ ..."
mkdir -p "${JAR_DEST_DIR}"
aws s3 cp "s3://${S3_BUCKET}/${JAR_NAME}" "${JAR_DEST_DIR}/${JAR_NAME}"

# 5. Nginx + certbot directories
log "Preparing nginx / certbot directories..."
mkdir -p nginx/conf.d certbot/www certbot/conf
sudo chmod -R 777 certbot/www certbot/conf

# 6. Write nginx config matching current SSL state.
#    First run (no cert yet) → HTTP-only config so certbot can answer ACME challenge.
#    Subsequent runs → full SSL config.
if [[ -f "${SSL_CERT_PATH}" ]]; then
  log "Writing nginx config (SSL enabled)..."
  write_ssl_config=1
else
  log "Writing nginx config (HTTP-only, first run)..."
  write_ssl_config=0
fi

if [[ "${write_ssl_config}" -eq 0 ]]; then
  cat > nginx/conf.d/default.conf <<EOF
server {
    listen 80;
    server_name ${DOMAIN} ${DOMAIN_WWW};

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        proxy_pass http://business-core-app:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection keep-alive;
        proxy_set_header Host \$host;
        proxy_cache_bypass \$http_upgrade;
    }
}
EOF
else
  cat > nginx/conf.d/default.conf <<EOF
server {
    listen 80;
    server_name ${DOMAIN} ${DOMAIN_WWW};

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://\$host\$request_uri;
    }
}

server {
    listen 443 ssl;
    http2 on;
    server_name ${DOMAIN} ${DOMAIN_WWW};

    ssl_certificate     /etc/letsencrypt/live/${DOMAIN}/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/${DOMAIN}/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers off;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384;
    ssl_session_timeout 1d;
    ssl_session_cache   shared:SSL:10m;
    ssl_stapling        on;
    ssl_stapling_verify on;

    location / {
        proxy_pass http://business-core-app:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_cache_bypass \$http_upgrade;
        proxy_buffering off;
    }
}
EOF
fi

# 7. Bring the stack up
log "Starting stack (docker compose up -d --build)..."
sudo docker compose up -d --build

# 8. Wait for postgres and run idempotent DDL
log "Waiting for postgres-db healthcheck..."
for _ in $(seq 1 30); do
  status="$(sudo docker inspect -f '{{.State.Health.Status}}' postgres-db 2>/dev/null || echo starting)"
  [[ "${status}" == "healthy" ]] && break
  sleep 2
done
[[ "${status}" == "healthy" ]] || err "postgres-db did not become healthy in time."

log "Running bootstrap.sql..."
set -a; . ./.env; set +a
sudo docker exec -i postgres-db psql -U "${CORE_DB_USERNAME}" -d "${CORE_DB_NAME}" \
  < deployment/bootstrap.sql

# 9. First-run SSL provisioning
if [[ ! -f "${SSL_CERT_PATH}" ]]; then
  warn "DNS for ${DOMAIN} must already resolve to this EC2's public IPv4."
  log "Requesting Let's Encrypt certificate..."
  sudo docker run --rm \
    -v "$(pwd)/certbot/conf:/etc/letsencrypt" \
    -v "$(pwd)/certbot/www:/var/www/certbot" \
    certbot/certbot certonly --webroot -w /var/www/certbot \
    -d "${DOMAIN}" -d "${DOMAIN_WWW}" \
    --email "${CERTBOT_EMAIL}" --agree-tos --no-eff-email --non-interactive

  log "Re-running setup to apply SSL config..."
  exec bash "$0" "$@"
fi

log "Setup complete."
echo "  curl -I http://${DOMAIN}"
echo "  curl -vI https://${DOMAIN} 2>&1 | grep 'SSL certificate'"
echo "  sudo docker logs business-core-app -f"