let ip = getClientIp();
let ipInfo;
// function getClientIp(){
//     let ip = "";
//
//     $.ajax({
//         type:"GET",
//         url:"https://webapi-pc.meitu.com/common/ip_location",
//         data:{},
//         dataType:"json",
//         async:false,
//         success:function(response){
//             ip = Object.keys(response.data)[0];
//             ipInfo = response.data[ip];
//         }
//     })
//     return ip;
// }
function getClientIp() {
    let ip = "";

    $.ajax({
        url: "https://webapi-pc.meitu.com/common/ip_location",
        dataType: "jsonp",
        jsonp: "callback",
        success: function (response) {
            ip = Object.keys(response.data)[0];
            ipInfo = response.data[ip];
        }
    });
    return ip;
}
