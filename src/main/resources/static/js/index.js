var category_arr = [];
var goodsMap = {};
let banner_num = 0;
$(document).ready(function(){
	isLogin();
	queryCategory();
	queryBannerNum();
	queryBanner();
	getCartNum();
})

function queryCategory(){
	$.ajax({
		type:"GET",
		url:"/category/getList",
		data:"",
		dataType:"json",
		success:function(res){
			if(res.code == '200'){
				console.log(res)
				category_arr = res.data;
				let s1 = "";
				let s2 = "";
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
				$("#cg_p").append(s1);
				$("#goodlist").append(s2)

				for (let val of category_arr) {
					queryGoodsByCategoryId(val.id, val.name, 1, 60);
				}

			}
		}
	})
}

function queryGoodsByCategoryId(cgId, cgName, pn, ps){
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
			if(res.code == '200'){
				goodsMap[cgName] = res.data;
				console.log(goodsMap);
				let s1 =
				`<div class="cg_div">
					<div class="category_detail">`;
				let i = 0;
				for (let val of goodsMap[cgName]) {
					if(i==0){
						s1 += "<ul>";
					}
					s1 += `<li class="cd_li">
								<a href=`+ val.id +`"/product.html?id=">`+ val.title + `</a>
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
				$("div[cgId='" + cgId +"']").append(s1);
				$("div[cgId='" + cgId +"']").on("mouseover",function(){
					$("div[cgId='" + cgId +"'] .cg_a").css("background-color", "#e9e9e9");
					$("div[cgId='" + cgId +"'] .cg_div").show();
				});

				$("div[cgId='" + cgId +"']").on("mouseleave", function(){
					$("div[cgId='" + cgId +"'] .cg_a").css("background-color", "white");
					$("div[cgId='" + cgId +"'] .cg_div").hide();
				});

				listGoods(cgId, cgName);
			}
		}
	})
}
function queryBannerNum(){
	$.ajax({
		type:"GET",
		url:"/banner/getNum",
		data:"",
		dataType:"json",
		success:function(res){
			if(res.code == '200'){
				banner_num= res.data;
			}
		}
	})
}
function queryBanner(){
	$.ajax({
		type:"GET",
		url:"/banner/getList",
		data:"",
		dataType:"json",
		success:function(res){
			if(res.code == '200'){
				let count = 0;
				let s1 = "";
				let s2 = "";
				for (let val of res.data) {
					if(count == 0){
						s1 += `<button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>`;
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
						s1 +=`<button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="` + count + `" aria-label="Slide ` + count + `"></button>`;
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
					count++;
				}
				$("#carouselExampleAutoplaying .carousel-indicators").append(s1);
				$("#carouselExampleAutoplaying .carousel-inner").append(s2);
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
		`	<div class="glid1">
				<div class="glid2">
					<a href="./product.html?id=`+ good.id +`" target="_blank">
						<img src="`+ good.img +`" alt="">
					</a>	
				</div>
				<a href="./good.html?id=`+ good.id +`" target="_blank" class="ga">
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
				</a>	
				<div>
					<p class="gmoney">
						<i>￥</i>`+ good.price +`			
					</p>
				</div>
			</div>
		</li>`
	}
	s+=
			`<li class="cls"></li>
		</ul>
	</div>`

	$("div[cgdid='"+ categoryId +"']").append(s);
}

function isLogin(){
	let result = false;
	$.ajax({
		type:"GET",
		url:"/user/isLogin",
		data:{},
		async:false,	//设置同步请求
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				//已登录
				$("#username").html("<a href='./user/index.html'>"+"你好! " + res.data.username);
				document.getElementById('register').style.display = 'none';
				document.getElementById('register_spacer').style.display = 'none';
				sessionStorage.setItem("userId", res.data.id);
				result = true;
			}else{
				//未登录
				$("#cartNoLogin").show();
				$("#cartLogin").hide();
				result = false;
			}
		}
	});
	return result;
}

function getCartNum(){
	$.ajax({
		type:"GET",
		url:"/cart/getNum",
		data:{},
		dataType:"json",
		success:function(res){
			if(res.code == 200){
				if (res.data<99){
					$("#cartnum").html(res.data);
				}else{
					$("#cartnum").html("99+");
				}
			}
		}
	})
}