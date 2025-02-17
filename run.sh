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
# @Author: Jiang RongJun
# @Date: 2024-07-09 09:09:09

# 检查是否为root用户
if [ "$(id -u)" != "0" ]; then
    echo "请使用root用户运行此脚本。"
    exit 1
fi

# 默认模式
MODE="one-touch"

# 解析参数
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -mod=*)
            MODE="${key#*=}" # 提取等号后面的内容作为模式
            shift # 移动到下一个参数
            ;;
        *)
            echo "未知参数: $key"
            echo "用法: $0 -mod=<mode>"
            exit 1
            ;;
    esac
done

# 自动安装 wget 的函数
install_wget() {
    echo "正在尝试自动安装 wget..."

    # 根据包管理器类型安装 wget
    if command -v apt-get &>/dev/null; then
        echo "检测到 apt-get 包管理器（如 Ubuntu/Debian）。"
        sudo apt-get update && sudo apt-get install -y wget
    elif command -v yum &>/dev/null; then
        echo "检测到 yum 包管理器（如 CentOS/RHEL 6/7）。"
        sudo yum install -y wget
    elif command -v dnf &>/dev/null; then
        echo "检测到 dnf 包管理器（如 Fedora/CentOS 8+）。"
        sudo dnf install -y wget
    elif command -v zypper &>/dev/null; then
        echo "检测到 zypper 包管理器（如 openSUSE）。"
        sudo zypper install -y wget
    elif command -v pacman &>/dev/null; then
        echo "检测到 pacman 包管理器（如 Arch Linux/Manjaro）。"
        sudo pacman -Syu --noconfirm wget
    else
        echo "无法识别的包管理器，请手动安装 wget。"
        exit 1
    fi

    # 检查安装是否成功
    if command -v wget &>/dev/null; then
        echo "wget 安装成功！"
    else
        echo "wget 安装失败，请手动安装。"
        exit 1
    fi
}

# 检查是否有 wget
if ! command -v wget &>/dev/null; then
    echo "wget 未安装，尝试自动安装..."
    install_wget
else
    echo "wget 已安装，继续执行后续操作..."
fi

# 根据模式执行不同逻辑
if [ "$MODE" = "one-touch" ]; then
    echo "一键Docker部署"
    wget https://raw.githubusercontent.com/ab1213home/mall/refs/heads/develop/one-touch.sh -O install.sh
    sh install.sh
    rm -rf install.sh
    exit 0
elif [ "$MODE" = "compile" ]; then
    echo "编译Docker部署"
    wget https://raw.githubusercontent.com/ab1213home/mall/refs/heads/develop/compile.sh -O install.sh
    sh install.sh
    rm -rf install.sh
    exit 0
else
    echo "不支持的模式: $MODE"
    echo "请使用 -mod=one-touch 或其他支持的模式。"
    exit 1
fi