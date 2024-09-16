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
