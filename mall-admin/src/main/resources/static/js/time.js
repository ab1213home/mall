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

function startIntervalTimer(duration) {
    let timer = duration, minutes, seconds;
    const interval = setInterval(function () {

        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        document.querySelector('#sendmail').disabled = true;
        document.querySelector('#sendmail').textContent = minutes + ":" + seconds;

        if (--timer < 0) {
            clearInterval(interval);
            document.querySelector('#sendmail').disabled = false;
            document.querySelector('#sendmail').textContent = "重新发送";
            // 这里可以添加倒计时结束后需要执行的代码
        }
    }, 1000);
}