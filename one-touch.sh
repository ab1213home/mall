#!/bin/bash
set -e
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

DownloadUrl="https://raw.githubusercontent.com/ab1213home/mall/refs/heads/develop"

install_docker() {
    echo "正在安装 Docker..."
    wget -qO- https://get.docker.com | bash -s docker --mirror Aliyun
    echo "Docker 安装完成。"
}

install_docker_compose() {
    echo "正在安装 Docker-Compose..."
    wget "https://github.com/docker/compose/releases/download/v2.29.7/docker-compose-$(uname -s)-$(uname -m)" -O /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    echo "Docker-Compose 安装完成。"
}

# 检查是否为root用户
if [ "$(id -u)" != "0" ]; then
    echo "请使用root用户运行此脚本。"
    exit 1
fi

# 检查是否有Docker
if ! command -v docker &>/dev/null; then
    echo "Docker 未安装，尝试自动安装..."
    install_docker
else
    echo "Docker 已安装，继续执行后续操作..."
fi

# 检查是否有Docker-Compose
if ! command -v docker-compose &>/dev/null; then
    echo "Docker-Compose 未安装，尝试自动安装..."
    install_docker_compose
else
    echo "Docker-Compose 已安装，继续执行后续操作..."
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

if [ ! -f "application.properties" ]; then
    wget ${DownloadUrl}/application-template.properties -O application.properties
    echo "mall-core配置文件创建成功。"
else
    echo "mall-core配置文件已存在。"
fi

# 下载 Docker Compose 配置文件
wget ${DownloadUrl}/docker-compose.yml -O docker-compose.yml

# 启动 Docker Compose 服务
echo "正在启动 Docker Compose 服务..."
docker-compose up -d

echo "Docker Compose 服务启动完成。"
echo "请访问 http://localhost:8080 查看 Jiang Mall。"

exit 0