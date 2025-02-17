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

# 自动安装 Git 的函数
install_git() {
    echo "正在尝试自动安装 Git..."

    # 根据包管理器类型安装 git
    if command -v apt-get &>/dev/null; then
        echo "检测到 apt-get 包管理器（如 Ubuntu/Debian）。"
        sudo apt-get update && sudo apt-get install -y git
    elif command -v yum &>/dev/null; then
        echo "检测到 yum 包管理器（如 CentOS/RHEL 6/7）。"
        sudo yum install -y git
    elif command -v dnf &>/dev/null; then
        echo "检测到 dnf 包管理器（如 Fedora/CentOS 8+）。"
        sudo dnf install -y git
    elif command -v zypper &>/dev/null; then
        echo "检测到 zypper 包管理器（如 openSUSE）。"
        sudo zypper install -y git
    elif command -v pacman &>/dev/null; then
        echo "检测到 pacman 包管理器（如 Arch Linux/Manjaro）。"
        sudo pacman -Syu --noconfirm git
    else
        echo "无法识别的包管理器，请手动安装 git。"
        exit 1
    fi

    # 检查安装是否成功
    if command -v git &>/dev/null; then
        echo "Git 安装成功！"
    else
        echo "Git 安装失败，请手动安装。"
        exit 1
    fi
}

# 自动安装 Maven 的函数
install_mvn() {
    echo "正在尝试自动安装 Maven..."

    # 根据包管理器类型安装 Maven
    if command -v apt-get &>/dev/null; then
        echo "检测到 apt-get 包管理器（如 Ubuntu/Debian）。"
        sudo apt-get update && sudo apt-get install -y mvn
    elif command -v yum &>/dev/null; then
        echo "检测到 yum 包管理器（如 CentOS/RHEL 6/7）。"
        sudo yum install -y mvn
    elif command -v dnf &>/dev/null; then
        echo "检测到 dnf 包管理器（如 Fedora/CentOS 8+）。"
        sudo dnf install -y mvn
    elif command -v zypper &>/dev/null; then
        echo "检测到 zypper 包管理器（如 openSUSE）。"
        sudo zypper install -y mvn
    elif command -v pacman &>/dev/null; then
        echo "检测到 pacman 包管理器（如 Arch Linux/Manjaro）。"
        sudo pacman -Syu --noconfirm mvn
    else
        echo "无法识别的包管理器，请手动安装 mvn。"
        exit 1
    fi

    # 检查安装是否成功
    if command -v mvn &>/dev/null; then
        echo "Maven 安装成功！"
    else
        echo "Maven 安装失败，请手动安装。"
        exit 1
    fi
}

# 自动安装 Java 的函数
install_java() {
    echo "正在尝试自动安装 Java 环境..."
    if command -v apt-get &>/dev/null; then
        echo "检测到 apt-get 包管理器（如 Ubuntu/Debian）。"
        sudo apt-get update && sudo apt-get install -y openjdk-17-jdk
    elif command -v yum &>/dev/null; then
        echo "检测到 yum 包管理器（如 CentOS/RHEL 6/7）。"
        sudo yum install -y java-17-openjdk
    elif command -v dnf &>/dev/null; then
        echo "检测到dnf 包管理器（如 Fedora/CentOS 8+）。"
        sudo dnf install -y java-17-openjdk
    elif command -v zypper &>/dev/null; then
        echo "检测到 zypper 包管理器（如 openSUSE）。"
        sudo zypper install -y java-17-openjdk
    elif command -v pacman &>/dev/null; then
        echo "检测到 pacman 包管理器（如 Arch Linux/Manjaro）。"
        sudo pacman -Syu --noconfirm java-17-openjdk
    else
        echo "无法识别的包管理器，请手动安装 Java 环境。"
        exit 1
    fi

    if command -v java &>/dev/null; then
        echo "Java 环境安装成功！"
    else
        echo "Java 环境安装失败，请手动安装。"
        exit 1
    fi

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

# 检查是否有git
if ! command -v git &>/dev/null; then
    echo "Git 未安装，尝试自动安装..."
    install_git
fi

# 检查是否有maven
if ! command -v mvn &>/dev/null; then
    echo "Maven 未安装，尝试自动安装..."
    install_mvn
fi

# 检查是否有Java
if ! command -v java &>/dev/null; then
    echo "Java 未安装，尝试自动安装..."
    install_java
fi

# 克隆仓库
echo "正在克隆仓库..."
rm -rf mall
git clone https://github.com/ab1213home/mall.git
cd mall

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

if [ ! -f "application.properties" ]; then
    wget ${DownloadUrl}/application-template.properties -O application.properties
    echo "mall-core配置文件创建成功。"
else
    echo "mall-core配置文件已存在。"
fi

# 下载 Docker Compose 配置文件
wget ${DownloadUrl}/docker-compose-local.yml -O docker-compose.yml

# 启动 Docker Compose 服务
echo "正在启动 Docker Compose 服务..."
docker-compose up -d

echo "Docker Compose 服务启动完成。"
echo "请访问 http://localhost:8080 查看 Jiang Mall。"

exit 0