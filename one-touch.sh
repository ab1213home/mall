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
DOWNLOAD_URL="https://download.jiangrongjun.top/"

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

# 检查是否为root用户
if [ "$(id -u)" != "0" ]; then
    echo "请使用root用户运行此脚本。"
    exit 1
fi

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

# 清理旧容器函数
clean_container() {
    local container_name=$1
    if docker ps -a --format '{{.Names}}' | grep -qw "$container_name"; then
        echo "正在停止并删除现有的 $container_name 容器..."
        docker stop "$container_name" >/dev/null
        docker rm "$container_name" >/dev/null
    else
        echo "未找到现有的 $container_name 容器。"
    fi
}

# 清理旧容器
clean_container "mall-core"
clean_container "mall-redis"
clean_container "mall-mysql"

# 配置文件处理
CONFIG_FILE="application.properties"
if [[ ! -f "${CONFIG_FILE}" ]]; then
    if ! wget -q "${DOWNLOAD_URL}/application-template.properties" -O "${CONFIG_FILE}"; then
        echo -e "${YELLOW}配置文件下载失败，使用空白模板...${NC}"
        touch "${CONFIG_FILE}"
    fi
fi

# Docker Compose 配置
if ! wget -q "${DOWNLOAD_URL}/docker-compose.yml" -O docker-compose.yml; then
    echo -e "${RED}Docker Compose 配置文件下载失败！${NC}"
    exit 1
fi

# 启动服务
echo -e "${BLUE}正在启动服务...${NC}"
docker-compose up -d

echo -e "\n${GREEN}部署成功！请访问 http://localhost:8080 访问系统。${NC}"
echo -e "${YELLOW}可以使用以下命令查看服务状态：docker-compose ps${NC}"
exit 0