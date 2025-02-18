#!/bin/bash
set -eo pipefail
#
# Copyright (c) 2024 Jiang RongJun
# Jiang Mall is licensed under Mulan PSL v2.
# You can use this software according to the terms and conditions of the Mulan
# PSL v2.
# You may obtain a copy of Mulan PSL v2 at:
#          http://license.coscl.org.cn/MulanPSL2
# THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
# KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
# NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
# See the Mulan PSL v2 for more details.
#

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # 重置颜色

# 配置参数
DOCKER_COMPOSE_VERSION="v2.29.7"
DOWNLOAD_URL="https://raw.githubusercontent.com/ab1213home/mall/develop"
PROJECT_DIR="mall"

# 通用安装函数
package_install() {
    local pkg_name=$1
    local install_cmd=$2
    local validate_cmd=$3

    echo -e "${BLUE}正在尝试安装 ${pkg_name}...${NC}"

    if eval "${validate_cmd}" &>/dev/null; then
        echo -e "${GREEN}${pkg_name} 已安装，跳过安装。${NC}"
        return 0
    fi

    if ! eval "${install_cmd}"; then
        echo -e "${RED}自动安装 ${pkg_name} 失败，请手动执行以下命令安装：${NC}"
        echo -e "${YELLOW}${install_cmd}${NC}"
        exit 1
    fi

    if ! eval "${validate_cmd}" &>/dev/null; then
        echo -e "${RED}${pkg_name} 安装后验证失败，请检查依赖关系。${NC}"
        exit 1
    fi
}

# Docker 安装
install_docker() {
    echo -e "${BLUE}正在安装 Docker...${NC}"
    if ! curl -fsSL https://get.docker.com | sh; then
        echo -e "${RED}Docker 自动安装失败，请参考官方文档手动安装。${NC}"
        exit 1
    fi
    systemctl enable --now docker
    echo -e "${GREEN}Docker 安装完成。${NC}"
}

# Docker Compose 安装
install_docker_compose() {
    local compose_url
    compose_url="https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)"

    echo -e "${BLUE}正在安装 Docker-Compose...${NC}"
    if ! curl -L "${compose_url}" -o /usr/local/bin/docker-compose; then
        echo -e "${RED}下载 Docker-Compose 失败，请检查网络连接。${NC}"
        exit 1
    fi

    chmod +x /usr/local/bin/docker-compose
    if ! docker-compose --version &>/dev/null; then
        echo -e "${RED}Docker-Compose 安装验证失败。${NC}"
        exit 1
    fi
    echo -e "${GREEN}Docker-Compose 安装完成。${NC}"
}

# 清理旧容器
clean_container() {
    local container_name=$1
    if docker ps -a --format '{{.Names}}' | grep -qw "${container_name}"; then
        echo -e "${YELLOW}正在清理容器 ${container_name}...${NC}"
        docker stop "${container_name}" >/dev/null || true
        docker rm "${container_name}" >/dev/null || true
    fi
}

# 检查 root 权限
if [[ $EUID -ne 0 ]]; then
    echo -e "${RED}错误：此脚本必须由 root 用户执行。${NC}" >&2
    exit 1
fi

# 依赖安装检查
declare -A packages=(
    ["git"]="command -v git"
    ["maven"]="command -v mvn"
    ["java"]="command -v java"
)

for pkg in "${!packages[@]}"; do
    case $pkg in
        "git")      install_cmd="apt-get install -y git || yum install -y git || dnf install -y git || zypper install -y git || pacman -Syu --noconfirm git" ;;
        "maven")    install_cmd="apt-get install -y maven || yum install -y maven || dnf install -y maven || zypper install -y maven || pacman -Syu --noconfirm maven" ;;
        "java")     install_cmd="apt-get install -y openjdk-17-jdk || yum install -y java-17-openjdk || dnf install -y java-17-openjdk || zypper install -y java-17-openjdk || pacman -Syu --noconfirm jdk-openjdk" ;;
    esac

    package_install "$pkg" "eval ${install_cmd}" "${packages[$pkg]}"
done

# Docker 环境检查
if ! command -v docker &>/dev/null; then
    install_docker
else
    echo -e "${GREEN}Docker 已安装 (版本: $(docker --version | awk '{print $3}'))${NC}"
fi

if ! command -v docker-compose &>/dev/null; then
    install_docker_compose
else
    echo -e "${GREEN}Docker-Compose 已安装 (版本: $(docker-compose --version | awk '{print $4}'))${NC}"
fi

# 清理旧环境
clean_container "mall-core"
clean_container "mall-redis"
clean_container "mall-mysql"

# 项目部署
echo -e "${BLUE}正在准备项目代码...${NC}"
if [[ -d "${PROJECT_DIR}" ]]; then
    echo -e "${YELLOW}检测到已有项目目录，尝试更新代码...${NC}"
    cd "${PROJECT_DIR}"
    git reset --hard
    git pull origin develop || { echo -e "${RED}代码更新失败，请手动处理。${NC}"; exit 1; }
else
    git clone https://github.com/ab1213home/mall.git "${PROJECT_DIR}" || exit 1
    cd "${PROJECT_DIR}"
fi

# 编译项目
echo -e "${BLUE}正在编译项目...${NC}"
mvn dependency:resolve || exit 1
if ! mvn clean package -DskipTests; then
    echo -e "${RED}项目编译失败，请检查 Maven 输出日志。${NC}"
    exit 1
fi

# 构建镜像
echo -e "${BLUE}正在构建 Docker 镜像...${NC}"
if ! docker build -t mall .; then
    echo -e "${RED}镜像构建失败，请检查 Dockerfile。${NC}"
    exit 1
fi

# 配置文件处理
CONFIG_FILE="application.properties"
if [[ ! -f "${CONFIG_FILE}" ]]; then
    if ! wget -q "${DOWNLOAD_URL}/application-template.properties" -O "${CONFIG_FILE}"; then
        echo -e "${YELLOW}配置文件下载失败，使用空白模板...${NC}"
        touch "${CONFIG_FILE}"
    fi
fi

# Docker Compose 配置
if ! wget -q "${DOWNLOAD_URL}/docker-compose-local.yml" -O docker-compose.yml; then
    echo -e "${RED}Docker Compose 配置文件下载失败！${NC}"
    exit 1
fi

# 启动服务
echo -e "${BLUE}正在启动服务...${NC}"
docker-compose up -d

echo -e "\n${GREEN}部署成功！请访问 http://localhost:8080 访问系统。${NC}"
echo -e "${YELLOW}可以使用以下命令查看服务状态：docker-compose ps${NC}"
exit 0