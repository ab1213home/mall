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
 * 查询轮播图信息并动态生成轮播图内容
 * 该函数通过发送GET请求获取轮播图数据，然后根据返回的数据动态生成轮播图的HTML代码
 */
function queryBanner(){
    // 使用jQuery的ajax方法发送异步请求
    $.ajax({
        // 请求方式为GET
        type:"GET",
        // 请求的URL为获取轮播图列表的API地址
        url:"/banner/getList",
        // 请求数据为空
        data:"",
        // 期望的返回数据类型为JSON
        dataType:"json",
        // 请求成功时的回调函数
        success:function(res){
            // 判断返回的响应码是否为200，表示请求成功
            if(res.code == '200'){
                // 初始化计数器，用于跟踪轮播图的索引
                let count = 0;
                // 初始化字符串变量，用于拼接轮播图指示器的HTML代码
                let s1 = "";
                // 初始化字符串变量，用于拼接轮播图项的HTML代码
                let s2 = "";
                // 遍历返回的轮播图数据数组
                for (let val of res.data) {
                    // 判断当前项是否为第一项，以确定是否需要添加active类
                    if(count == 0){
                        // 拼接第一个指示器按钮，包含active类
                        s1 += `<button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>`;
                        // 拼接第一个轮播图项，包含active类和自动播放间隔
                        s2 +=
                        `<div class="carousel-item active" data-bs-interval="2000">
                            <a href="`+ val.url +`" target="_blank">
                                <img src="`+ val.img +`" class="d-block w-100" alt="`+ val.description +`">
                            </a>
                            <div class="carousel-caption d-none d-md-block">
                            <h5>`+val.description+`</h5>
                            </div>
                        </div>`;
                    }else{
                        // 拼接非第一个指示器按钮
                        s1 +=`<button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="` + count + `" aria-label="Slide ` + count + `"></button>`;
                        // 拼接非第一个轮播图项
                        s2 +=
                        `<div class="carousel-item">
                              <a href="`+ val.url +`" target="_blank">
                                  <img src="`+ val.img +`" class = "d-block w-100" alt="`+ val.description +`">
                              </a>
                              <div class="carousel-caption d-none d-md-block">
                                 <h5>`+ val.description +`</h5>
                              </div>
                         </div>`;
                    }
                    // 计数器递增，为下一项做准备
                    count++;
                }
                // 将生成的指示器HTML代码追加到指定的轮播图指示器容器中
                $("#carouselExampleAutoplaying .carousel-indicators").append(s1);
                // 将生成的轮播图项HTML代码追加到指定的轮播图容器中
                $("#carouselExampleAutoplaying .carousel-inner").append(s2);
            }
        }
    })
}

/**
 * 查询轮播图数量
 * 通过发送GET请求到服务器，获取轮播图的数量，并更新页面上的数量显示
 */
function queryBannerNum(){
	$.ajax({
		// 设置请求方式为GET
        type:"GET",
        // 设置请求的URL地址
		url:"/banner/getNum",
        // 本次请求无需发送数据
		data:"",
        // 预期服务器返回的数据类型为json
		dataType:"json",
        // 当请求成功时，处理服务器返回的数据
		success:function(res){
			// 如果返回的状态码表示成功
			if(res.code == '200'){
                // 更新轮播图数量
				banner_num= res.data;
			}
		}
	})
}
