#!/bin/bash

IS_APP1=$(docker ps | grep container1)
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
  echo "### App3 App4 => APP1 App2 ###"

  echo "1. App1 이미지 받기"
  docker-compose pull app1

  echo "2. App1 App2 컨테이너 실행"
  docker-compose up -d app1 app2

  echo "3. health check"
  if ! check_service "http://127.0.0.1:8081" || ! check_service "http://127.0.0.1:8082"; then
    echo "APP1 또는 APP2 health check 가 실패했습니다."
    exit 1
  fi

  echo "4. APP3 APP4 컨테이너 내리기"
  docker-compose stop app3 app4
  docker-compose rm -f app3 app4

else
  echo "### App1 App2 => App3 App4 ###"

  echo "1. App3 이미지 받기"
  docker-compose pull app3

  echo "2. App3 App4 컨테이너 실행"
  docker-compose up -d app3 app4

  echo "3. health check"
  if ! check_service "http://127.0.0.1:8083" || ! check_service "http://127.0.0.1:8084"; then
    echo "APP3 또는 APP4 health check 가 실패했습니다."
    exit 1
  fi

  echo "4. APP1 APP2 컨테이너 내리기"
  docker-compose stop app1 app2
  docker-compose rm -f app1 app2
fi