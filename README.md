# Business Manager Core

## Propósito

## Instrucciones de despliegue

### Ambiente AWS

* Nombre instancia: business-manager-core
* Asociar rol de IAM para permisos al EC2 sobre la S3 *

#### Deploy config

sudo docker stop $(sudo docker ps -aq) && sudo docker rm $(sudo docker ps -aq)
sudo docker system prune -a --volumes -f
sudo docker volume ls
sudo docker volume rm business-management-core_postgres_data
sudo rm -rf business-management-core

** - 1. Actualizar el .jar
./gradlew :app-service:bootJar

sudo yum update -y
sudo yum install docker git -y
sudo systemctl start docker
sudo systemctl enable docker

DOCKER_COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep tag_name | cut -d '"' -f 4)

sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-linux-$(uname -m)" -o /usr/local/lib/docker/cli-plugins/docker-compose

sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

exit

* Crear S3 business-manager-core-jar-container

aws s3 cp applications/app-service/build/libs/BusinessManagerCore.jar s3://business-manager-core-jar-container/

--------------------------------------------------------------------------
sudo dnf install -y java-21-amazon-corretto-devel

java -version
---------------------------------------------------------------------------

cd /home/$USER
git clone https://github.com/Tavo826/business-management-core

cd business-management-core

chmod +x gradlew
------------------------------------------------------------
./gradlew build -x test
------------------------------------------------------------

aws s3 cp s3://business-manager-core-jar-container/BusinessManagerCore.jar /home/ec2-user/business-management-core/applications/app-service/build/libs/

nano .env

mkdir -p nginx/conf.d certbot/www certbot/conf

nano nginx/conf.d/default.conf

´´´
server {
    listen 80;
    server_name customermanagement.top www.customermanagement.top;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        proxy_pass http://business-core:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection keep-alive;
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
´´´

BUILDX_VERSION=$(curl -s https://api.github.com/repos/docker/buildx/releases/latest | grep tag_name | cut -d '"' -f 4)
sudo curl -SL "https://github.com/docker/buildx/releases/download/${BUILDX_VERSION}/buildx-${BUILDX_VERSION}.linux-amd64" -o /usr/local/lib/docker/cli-plugins/docker-buildx
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-buildx

sudo docker compose up -d --build

sudo docker logs business-core-app -f
sudo docker logs nginx --tail=100

** Cambiar la ip de la EC2 en namecheap (public ipv4)

sudo chmod -R 777 certbot/www
sudo chmod -R 777 certbot/conf

------------------------------------------------------
sudo docker compose run --rm certbot certonly --webroot -w /var/www/certbot -d customermanagement.top -d www.customermanagement.top --email 9gagigor816@gmail.com --agree-tos --no-eff-email
------------------------------------------------------

sudo docker run --rm -it -v "$(pwd)/certbot/conf:/etc/letsencrypt" -v "$(pwd)/certbot/www:/var/www/certbot" certbot/certbot certonly --webroot -w /var/www/certbot -d customermanagement.top -d www.customermanagement.top --email 9gagigor816@gmail.com --agree-tos --no-eff-email

nano nginx/conf.d/default.conf

´´´
server {
    listen 80;
    server_name customermanagement.top www.customermanagement.top;
    
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }
    
    location / {
        return 301 https://$host$request_uri;
    }
}

server {
    listen 443 ssl;
    http2 on;
    server_name customermanagement.top www.customermanagement.top;
    
    ssl_certificate /etc/letsencrypt/live/customermanagement.top/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/customermanagement.top/privkey.pem;
    
    # Configuración SSL básica pero segura
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers off;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384;
    ssl_session_timeout 1d;
    ssl_session_cache shared:SSL:10m;
    ssl_stapling on;
    ssl_stapling_verify on;
    
    location / {
        proxy_pass http://business-core-app:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        proxy_buffering off;
    }
}
´´´

sudo docker compose restart nginx

Verificación

- Verificar que el certificado SSL es válido
curl -vI https://customermanagement.top 2>&1 | grep 'SSL certificate'

- Verificar que HTTP redirige a HTTPS
curl -I http://customermanagement.top

### DB

sudo docker exec -it postgres-db psql -U postgres
CREATE DATABASE business_manager_db;

\c business_manager_db

CREATE TABLE users (document_id VARCHAR(50) PRIMARY KEY, name VARCHAR(100) NOT NULL, surname VARCHAR(100) NOT NULL, email VARCHAR(150) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL, birthdate VARCHAR(20), created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE business (nit VARCHAR(50) PRIMARY KEY, name VARCHAR(150) NOT NULL, description TEXT, phone VARCHAR(20), email VARCHAR(150), address VARCHAR(255), owner_document_id VARCHAR(50) NOT NULL, social_media_list JSONB DEFAULT '[]'::jsonb, bank_account_list JSONB DEFAULT '[]'::jsonb, created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP, CONSTRAINT fk_owner_user FOREIGN KEY (owner_document_id) REFERENCES users(document_id) ON DELETE CASCADE);

\d users

CREATE TABLE messages (
    id VARCHAR(50) PRIMARY KEY,
    message_id VARCHAR(100) UNIQUE,
    received_message TEXT,
    sender_phone VARCHAR(20),
    response_message TEXT,
    sent_time TIMESTAMP WITHOUT TIME ZONE
);

-- Índice para búsquedas por messageId
CREATE INDEX idx_messages_message_id ON messages(message_id);


### Ambiente Meta

- Configurar webhook: https://customermanagement.top/webhook

#### Local (Ngrok)

```bash
ngrok -v
ngrok http 8080
```