let category_arr = [];
const goodsMap = {};
let banner_num = 0;
$(document).ready(function(){
	isLogin();
	queryCategory();
	queryBannerNum();
	queryBanner();
	getCartNum();
})

/**
 * 查询分类信息
 *
 * 本函数通过发送GET请求到服务器获取分类列表，并在页面上展示分类信息
 * 同时，根据获取的分类信息，调用queryGoodsByCategoryId函数查询每个分类下的商品
 */
function queryCategory(){
    // 使用jQuery的ajax方法发送异步请求
    $.ajax({
        type:"GET", // 请求类型为GET
        url:"/category/getList", // 请求的URL地址
        data:"", // 请求参数，此处无需额外参数
        dataType:"json", // 预期从服务器返回的数据类型为json
        success:function(res){ // 请求成功后的回调函数
            if(res.code == '200'){ // 检查服务器返回的状态码，200表示请求成功
                category_arr = res.data; // 提取分类数据，存储在category_arr变量中
                let s1 = "";
                let s2 = "";
                // 遍历分类数组，生成分类展示的HTML代码
                for (let val of category_arr) {
                    s1 +=
                    `<div class='category' cgId='`+ val.id +`'>
                        <div class='cg_a'><a href='javascript:;'>`+ val.name +`</a></div>
                    </div>`;
                    s2 +=
                    `<div class="cc" cgdid='`+ val.id +`'>
                        <div>
                            <h3 class="cc-title">`+ val.name +`</h3>
                        </div>
                    </div>`
                }
                $("#cg_p").append(s1); // 将生成的分类HTML代码添加到指定的DOM元素内
                $("#goodList").append(s2) // 将分类详情HTML代码添加到商品列表区域

                // 遍历分类数组，对每个分类调用queryGoodsByCategoryId查询商品
                for (let val of category_arr) {
                    queryGoodsByCategoryId(val.id, val.name, 1, 60); // 查询每个分类下的商品，每次查询60条
                }
            }
        }
    })
}

/**
 * 根据分类ID查询商品
 * @param {string} cgId - 分类ID
 * @param {string} cgName - 分类名称
 * @param {number} pn - 页码
 * @param {number} ps - 每页大小
 */
function queryGoodsByCategoryId(cgId, cgName, pn, ps){
    // 发起GET请求获取商品列表
    $.ajax({
        type:"GET",
        url:"/product/getList",
        data:{
            categoryId: cgId,
            pageNum:pn,
            pageSize:ps,
        },
        dataType:"json",
        success:function(res){
            // 请求成功处理
            if(res.code == '200'){
                // 将商品数据存储到goodsMap中
                goodsMap[cgName] = res.data;
                let s1 =
                `<div class="cg_div">
                    <div class="category_detail">`;
                //生成商品列表HTML
				let i = 0;
				for (let val of goodsMap[cgName]) {
					if(i==0){
						s1 += "<ul>";
					}
					s1 += `<li class="cd_li">
                                <a href="/product.html?id=`+ val.id +`">`+ val.title + `</a>
                           </li>`
					i++;
					if(i==6){
						s1 += "</ul>";
						i == 0;
					}
				}
				if(i != 0){
					s1 += "</ul>";
				}
                s1 +=
                    `</div>
                </div>`;
                // 将生成的商品HTML添加到页面中
                $("div[cgId='" + cgId +"']").append(s1);
                // 鼠标悬停事件，改变商品链接背景色并显示商品详情
                $("div[cgId='" + cgId +"']").on("mouseover",function(){
                    $("div[cgId='" + cgId +"'] .cg_a").css("background-color", "#e9e9e9");
                    $("div[cgId='" + cgId +"'] .cg_div").show();
                });

                // 鼠标移出事件，恢复商品链接背景色并隐藏商品详情
                $("div[cgId='" + cgId +"']").on("mouseleave", function(){
                    $("div[cgId='" + cgId +"'] .cg_a").css("background-color", "white");
                    $("div[cgId='" + cgId +"'] .cg_div").hide();
                });

                // 调用函数继续处理商品数据
                listGoods(cgId, cgName);
            } else if(res.code == '404') {
                // 请求返回404，显示暂无商品提示
                let s1 = "<div class=\"no-goods\">该分类下暂无商品</div>";
				 $("div[cgdid='"+ cgId +"']").append(s1);
				 let s2 = `<div class="cg_div">
                    				<div class="category_detail">
                    				 	<div class="no-goods">该分类下暂无商品</div>
                    				</div>
                				 </div>`;

				// 将生成的商品HTML添加到页面中
                $("div[cgId='" + cgId +"']").append(s2);
                // 鼠标悬停事件，改变商品链接背景色并显示商品详情
                $("div[cgId='" + cgId +"']").on("mouseover",function(){
                    $("div[cgId='" + cgId +"'] .cg_a").css("background-color", "#e9e9e9");
                    $("div[cgId='" + cgId +"'] .cg_div").show();
                });

                // 鼠标移出事件，恢复商品链接背景色并隐藏商品详情
                $("div[cgId='" + cgId +"']").on("mouseleave", function(){
                    $("div[cgId='" + cgId +"'] .cg_a").css("background-color", "white");
                    $("div[cgId='" + cgId +"'] .cg_div").hide();
                });
            }
        }
    })
}

function listGoods(categoryId, categoryName){
	let s="";
	let goodsArr = goodsMap[categoryName];
	s+=
	`<div>
		<ul>`
	let count = 0;
	for(let good of goodsArr){
		if(count==0){
			s+=`<li class="glifirst">`
		}else{
			s+=`<li class="gli">`
		}
		count++;
		if(count==5){
			count=0;
		}
		s+=
		`	<a href="./product.html?id=`+ good.id +`" target="_blank" class="glid1">
				<div class="glid2">
					<img src="`+ good.img +`" alt="">
				</div>
				<div>
					<div>
						<p class="gtitle">`+ good.title +`</p>
					</div>
					<div>
						<p class="gdetail">
						`+ good.description +`
						</p>	
					</div>
				</div>
				<div>
					<p class="gmoney">
						<i>￥</i>`+ good.price +`			
					</p>
				</div>
			</a>
		</li>`
	}
	s+=
			`<li class="cls"></li>
		</ul>
	</div>`

	$("div[cgdid='"+ categoryId +"']").append(s);
}

