$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo()
})



document.addEventListener('DOMContentLoaded', function() {
        var lockButton = document.getElementById('button_lock');

        lockButton.addEventListener('click', function(e) {
            e.preventDefault(); // 阻止默认行为
            lock_user();
        });
});

function lock_user() {
    $.ajax({
        url: '/modify/self-lock',
        type: 'POST',
        data: {},
        dataType: 'json',
        success: function(rea){
            if(rea.code == 200){
                openModal('提示',"操作成功！")
                logout();
            }else{
                openModal('错误',"操作失败！"+rea.msg)
            }
        }
    })
}