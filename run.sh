#!/bin/bash

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

# 检查是否存在名为 mall-core 的容器，并停止和删除它
if docker container ls -a | grep -q mall-core; then
    echo "正在停止并删除现有的 mall-core 容器..."
    docker stop mall-core
    docker rm mall-core
else
    echo "未找到现有的mall-core容器。"
fi

# 检查是否存在名为 mall-redis 的容器，并停止和删除它
if docker container ls -a | grep -q mall-redis; then
    echo "正在停止并删除现有的 mall-redis 容器..."
    docker stop mall-redis
    docker rm mall-redis
else
    echo "未找到现有的 mall-redis 容器。"
fi

# 检查是否存在名为 mall-mysql 的容器，并停止和删除它
if docker container ls -a | grep -q mall-mysql; then
    echo "正在停止并删除现有的 mall-mysql 容器..."
    docker stop mall-mysql
    docker rm mall-mysql
else
    echo "未找到现有的mall-mysql容器。"
fi

# 编译jar包
echo "正在编译jar包..."
mvn dependency:resolve
mvn clean package -DskipTests

# 检查编译是否成功
# shellcheck disable=SC2181
if [ $? -eq 0 ]; then
    echo "jar包编译成功。"
else
    echo "jar包编译时出错。"
    exit 1
fi

# 构建 Docker 镜像
echo "正在构建 Docker 镜像..."
docker build -t mall .

# 检查构建是否成功
# shellcheck disable=SC2181
if [ $? -eq 0 ]; then
    echo " Docker 镜像构建成功。"
else
    echo "构建 Docker 映像时出错。"
    exit 1
fi

if [ ! -d "data" ]; then
    echo "data目录不存在，正在创建数据目录..."
    mkdir -p data
    echo "数据目录创建成功。"
else
    echo "data目录已存在。"
fi

if [ ! -f "data/config.properties" ]; then
    touch data/config.properties
    echo "mall配置文件创建成功"
else
    echo "mall配置文件已存在。"
fi

if [ ! -f "application.properties" ]; then
    cp application-template.properties application.properties
    echo "mall-core配置文件创建成功。"
else
    echo "mall-core配置文件已存在。"
fi

# 启动 Docker Compose 服务
echo "正在启动 Docker Compose 服务..."
docker-compose -f docker-compose-local.yml up -d