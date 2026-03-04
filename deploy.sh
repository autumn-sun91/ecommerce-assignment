#!/bin/bash

set -e  # 에러 발생 시 즉시 종료

echo "==============================="
echo "🚀 Shop App Deploy Script Start"
echo "==============================="

COMPOSE_FILE="docker-compose/docker-compose.yml"
PROJECT_NAME="shop"

# ==============================
# 기본 설정
# ==============================
PRODUCER_SCALE=${1:-2}   # 첫 번째 인자: producer scale (default 2)
CONSUMER_SCALE=${2:-1}   # 두 번째 인자: consumer scale (default 1)
LOG=${3:-false}          # 세 번째 인자: 로그 출력 여부

echo "📦 Producer scale: $PRODUCER_SCALE"
echo "📦 Consumer scale: $CONSUMER_SCALE"

# ==============================
# Gradle Build + Jib Docker Build
# ==============================
echo "🔨 Building Docker image via Jib..."
./gradlew clean jibDockerBuild

echo "✅ Image build completed"

# ==============================
# 기존 컨테이너 종료
# ==============================
echo "🛑 Stopping existing containers..."
docker-compose -p $PROJECT_NAME -f $COMPOSE_FILE down --remove-orphans --volumes

# ==============================
# 새 컨테이너 실행 (scale 적용)
# ==============================
echo "🚀 Starting containers..."
docker-compose -p $PROJECT_NAME -f $COMPOSE_FILE up -d \
    --scale app-producer=$PRODUCER_SCALE \
    --scale app-consumer=$CONSUMER_SCALE

echo "✅ Containers started"

# ==============================
# 컨테이너 상태 확인
# ==============================
echo "📊 Running containers:"
docker ps

# ==============================
# 로그 옵션
# ==============================
if [ "$LOG" = "true" ]; then
  echo "📜 Tailing logs..."
  docker-compose -p $PROJECT_NAME -f $COMPOSE_FILE logs -f app-producer app-consumer
fi

echo "==============================="
echo "🎉 Deploy Completed Successfully"
echo "==============================="