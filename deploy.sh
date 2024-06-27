#!/bin/bash

IS_APP1=$(docker ps | grep container1)
DEFAULT_CONF=" /etc/nginx/nginx.conf"
MAX_RETRIES=30

check_service() {
  local RETRIES=0
  local URL=$1
  while [ $RETRIES -lt $MAX_RETRIES ]; do
    echo "Checking service at $URL... (attempt: $((RETRIES+1)))"
    sleep 3

    REQUEST=$(curl $URL)
    if [ -n "$REQUEST" ]; then
      echo "health check success"
      return 0
    fi

    RETRIES=$((RETRIES+1))
  done;

  echo "Failed to check service after $MAX_RETRIES attempts."
  return 1
}

if [ -z "$IS_APP1" ];then
  echo "### App2 => APP1 ###"

  echo "1. App1 이미지 받기"
  docker-compose pull app1

  echo "2. App1 컨테이너 실행"
  docker-compose up -d app1

  echo "3. health check"
  if ! check_service "http://127.0.0.1:8081"; then
    echo "APP1 health check 가 실패했습니다."
    exit 1
  fi

#  echo "4. nginx 재실행"
#  sudo cp /etc/nginx/nginx.app1.conf /etc/nginx/nginx.conf
#  sudo cp /etc/nginx/sites-available/nginx.app1.conf /etc/nginx/sites-available/default
#  sudo nginx -s reload

  echo "4. APP2 컨테이너 내리기"
  docker-compose stop app2
  docker-compose rm -f app2

else
  echo "### App1 => App2 ###"

  echo "1. App2 이미지 받기"
  docker-compose pull app2

  echo "2. App2 컨테이너 실행"
  docker-compose up -d app2

  echo "3. health check"
  if ! check_service "http://127.0.0.1:8082"; then
      echo "App2 health check 가 실패했습니다."
      exit 1
    fi

#  echo "4. nginx 재실행"
#  sudo cp /etc/nginx/nginx.app2.conf /etc/nginx/nginx.conf
#  sudo cp /etc/nginx/sites-available/nginx.app2.conf /etc/nginx/sites-available/default
#  sudo nginx -s reload

  echo "4. APP2 컨테이너 내리기"
  docker-compose stop app1
  docker-compose rm -f app2
fi