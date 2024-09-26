#!/bin/bash

# 检查是否存在名为 mall 的容器，并停止和删除它
if docker container ls -a | grep -q mall; then
    docker stop mall
    docker rm mall
fi

# 构建 Docker 镜像
docker build -t mall .

# 启动 Docker Compose 服务
docker-compose up
