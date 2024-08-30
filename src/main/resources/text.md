<div class="password-box">
    <div class="card">
        <div class="card-body login-card-body">
            <form action="/user/change-password" method="post">
                <div class="form-group has-feedback">
                    <span class="fa fa-lock form-control-feedback"></span>
                        <label for="oldPassword"></label>
                        <input type="password" id="oldPassword" name="oldPassword" class="form-control" placeholder="请输入当前密码"
                                                             required="required">
                </div>
                <div class="form-group has-feedback">
                    <span class="fa fa-lock form-control-feedback"></span>
                        <label for="newPassword"></label>
                        <input type="password" id="newPassword" name="newPassword" class="form-control" placeholder="请输入新密码"
                                                            required="required">
                </div>
                <div class="form-group has-feedback">
                    <span class="fa fa-lock form-control-feedback"></span>
                        <label for="confirmPassword"></label>
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="请再次输入新密码"
                                                             required="required">
                </div>
                <div class="row">
                    <div class="col-12">
                        <button type="submit" class="btn btn-primary btn-block btn-flat">修改密码</button>
                    </div>
                </div>
            </form>
        </div>
        <!-- /.login-card-body -->
    </div>
</div>