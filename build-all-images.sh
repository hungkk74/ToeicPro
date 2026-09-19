#!/usr/bin/env bash
# =============================================================================
# Build All Docker Images - TOEIC Pro Platform (Linux / macOS)
# =============================================================================
# Cách sử dụng:
#   chmod +x build-all-images.sh
#   ./build-all-images.sh
#
# Script này đóng gói 7 Docker Images cho toàn bộ Microservices
# bằng Jib Maven Plugin. Yêu cầu JDK 21 được cài đặt sẵn.
# =============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# --- Kiểm tra Java version ---
if ! command -v java &> /dev/null; then
    echo "ERROR: Java khong duoc cai dat. Vui long cai JDK 21."
    exit 1
fi

JAVA_MAJOR=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_MAJOR" != "21" ]; then
    echo "WARNING: Dang dung Java $JAVA_MAJOR. JHipster yeu cau JDK 21."
    echo "Tiep tuc build voi Java hien tai..."
fi

echo "============================================="
echo "  TOEIC Pro - Build All Docker Images"
echo "  JAVA_HOME: ${JAVA_HOME:-system default}"
echo "============================================="

# --- Danh sách 7 services ---
SERVICES=(
    "gateway"
    "userservice"
    "subscriptionservice"
    "paymentservice"
    "examservice"
    "courseservice"
    "notificationservice"
)

FAILED=()

for SERVICE in "${SERVICES[@]}"; do
    SERVICE_PATH="${SCRIPT_DIR}/${SERVICE}"
    if [ ! -f "${SERVICE_PATH}/pom.xml" ]; then
        echo "SKIP: ${SERVICE} (khong tim thay pom.xml)"
        continue
    fi

    echo ""
    echo ">>> Building Docker image: ${SERVICE} ..."

    cd "${SERVICE_PATH}"
    if ./mvnw verify -DskipTests -Pprod jib:dockerBuild -B; then
        echo "<<< ${SERVICE} - BUILD SUCCESS"
    else
        echo "<<< ${SERVICE} - BUILD FAILED"
        FAILED+=("${SERVICE}")
    fi
done

# --- Build Docker image: frontend (Next.js) ---
FRONTEND_PATH="${SCRIPT_DIR}/frontend"
if [ -f "${FRONTEND_PATH}/Dockerfile" ]; then
    echo ""
    echo ">>> Building Docker image: frontend (Next.js) ..."
    cd "${FRONTEND_PATH}"
    if docker build -t toeic-frontend:latest .; then
        echo "<<< frontend - BUILD SUCCESS"
    else
        echo "<<< frontend - BUILD FAILED"
        FAILED+=("frontend")
    fi
fi

# --- Kết quả ---
echo ""
echo "============================================="
echo "  KET QUA BUILD"
echo "============================================="

TOTAL=${#SERVICES[@]}
FAIL_COUNT=${#FAILED[@]}
SUCCESS_COUNT=$((TOTAL - FAIL_COUNT))

if [ ${FAIL_COUNT} -eq 0 ]; then
    echo "  Tat ca ${TOTAL} services THANH CONG!"
    echo ""
    echo "  Buoc tiep theo:"
    echo "    docker compose -f docker-compose-all.yml up -d"
else
    echo "  Thanh cong: ${SUCCESS_COUNT}/${TOTAL}"
    echo "  That bai:   ${FAILED[*]}"
fi

echo ""
