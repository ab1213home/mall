/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

/**
 * 打开模态框并设置其标题和内容
 * 
 * @param {string} title - 模态框的标题
 * @param {string} content - 模态框的内容
 */
function openModal(title, content) {
      // 获取模态框的标题和内容元素
      const modalTitle = document.getElementById('staticBackdropLabel');
      const modalBody = document.getElementById('modal-body');
      const modalBtn = document.getElementById('confirm-btn');
      modalBtn.style.display = 'none';
      modalBtn.onclick = function(){
          modal.hide();
      }
      // 设置新的标题和内容
      modalTitle.textContent = title;
      modalBody.textContent = content;

      // 显示模态框
      const modal = new bootstrap.Modal(document.getElementById('staticBackdrop'));
      modal.show();
}
let spinner
/**
 * 打开加载中模态框
 * 该函数用于显示一个带有加载指示器的模态框，以指示正在进行中的后台操作
 */
function openLoadingModal() {
  // 创建模态框实例
  const modal = new bootstrap.Modal(document.getElementById('loadingModal'));

  // 配置加载指示器的选项
  let opts = {
      lines: 13, // 要绘制的线数
      length: 20, // 每行的长度
      width: 10, // 线条粗细
      radius: 30, // 内圈的半径
      corners: 1, // 角圆度 (0..1)
      rotate: 0, // 旋转偏移
      direction: 1, // 1：顺时针，-1：逆时针
      color: '#3f51b5', // 线条的颜色
      speed: 1, // 每秒转数
      trail: 60, // 余辉百分比
      shadow: false, // 是否渲染阴影
      hwaccel: false, // 是否使用硬件加速
      className: 'spinner', // 要分配给微调器的CSS类
      zIndex: 2e9, // z索引（默认为200000000）
      top: '50%', // 相对于父级的最高位置
      left: '50%' // 相对于父级的左位置
  }

  // 显示加载指示器
  let target = document.getElementById('spinnerContainer');
  spinner = new Spinner(opts).spin(target);
  target.style.display = 'block';

  // 显示模态框
  modal.show();
}

/**
 * 关闭加载模态框
 * 该函数用于隐藏当前显示的加载模态框，停止加载动画并隐藏加载容器
 */
function closeLoadingModal() {
  // 创建bootstrap模态框实例
  const modal = new bootstrap.Modal(document.getElementById('loadingModal'));
  // 停止加载动画
  spinner.stop();
  // 获取加载容器元素
  let target = document.getElementById('spinnerContainer');
  // 隐藏加载容器
  target.style.display = 'none';
  // 隐藏模态框
  modal.hide();
}