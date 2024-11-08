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
/**
 * 获取客户端IP地址
 * 通过发送Ajax请求到指定的API接口，解析响应头中包含的客户端IP信息
 * 使用了jQuery的Ajax方法，并设置async为true以异步方式请求数据
 *
 * @return {string} - 客户端IP地址
 */
function getClientIp() {
    // $.ajax({
    //     type: 'GET',
    //     url: 'https://webapi-pc.meitu.com/common/ip_location',
    //     dataType: "jsonp",
    //     jsonp: "callback",
    //     async: true,
    //     xhrFields: {
    //         withCredentials: true
    //     },
    //     complete: function(xhr) {
    //         // 获取响应头中的 x-request-ip
    //         console.log(xhr.getAllResponseHeaders());
    //         const ip = xhr.getResponseHeader("X-Request-Ip")||xhr.getResponseHeader("x-request-ip")||"127.0.0.1";
    //         console.log(ip);
    //         return ip;
    //     },
    // });

}

