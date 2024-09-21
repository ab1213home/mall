$(document).ready(function(){
	isAdminUser();
	queryMyUserInfo()
})
document.addEventListener('DOMContentLoaded', function() {
    var editButton = document.getElementById('edit');

    editButton.addEventListener('click', function(e) {
        e.preventDefault(); // 阻止默认行为
        window.location.href = '/user/modify/info.html';
    });
});