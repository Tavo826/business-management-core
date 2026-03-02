# Business Manager Core

## Propósito

## Instrucciones de despliegue

### Ambiente AWS

* Nombre instancia: business-manager-core

#### Deploy config

sudo yum update -y
sudo dnf install
sudo yum install docker git -y
sudo systemctl start docker
sudo systemctl enable docker

DOCKER_COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep tag_name | cut -d '"' -f 4)
sudo mkdir -p /usr/local/docker/cli-plugins
sudo curl -SL "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-linux-$(uname -m)" -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

exit

sudo dnf install -y java-17-amazon-coretto-devel

java -version

git clone https://github.com/Tavo826/business-management-communication

chmod +x gradlew
./gradlew build -x test

nano .env
docker-compose up -d --force-recreate business-core-app

sudo docker-compose up -d --build

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