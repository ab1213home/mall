function startIntervalTimer(duration) {
    let timer = duration, minutes, seconds;
    const interval = setInterval(function () {

        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        document.querySelector('#time').textContent = minutes + ":" + seconds;

        if (--timer < 0) {

            clearInterval(interval);
            alert("时间到！");
            // 这里可以添加倒计时结束后需要执行的代码
        }
    }, 1000);
}