const urlParams = new URLSearchParams(window.location.search);
const keyword = urlParams.get('keyword');
const message = urlParams.get('message');

function renderCategory(category_arr) {
    let html = `<div class="row mt-3"><p>已找到`+ category_arr.length +`件符合条件的商品。</p></div>`;
    if(category_arr.length == 0){
        document.getElementById('goodList').innerHTML = '<div class="col-md-12 mt-4"><div class="alert alert-warning" role="alert">未找到相关商品</div></div>';
    }else{
        html+=
        `<div>
            <ul>`
        let count = 0;
        for(let good of category_arr){
            if(count==0){
                html+=`<li class="glifirst">`
            }else{
                html+=`<li class="gli">`
            }
            count++;
            if(count==5){
                count=0;
            }
            html+=
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
                        <p class="gmoney price-tag">
                            `+ good.price +`			
                        </p>
                    </div>
                </a>
            </li>`
        }
        html+=
                `<li class="cls"></li>
            </ul>
        </div>`
    }
    document.getElementById('goodList').innerHTML = html;
}

function search(){
    let keyword = document.getElementById("keyword").value;
    if(keyword == ""){
		openModal("警告","请输入关键字");
		return;
	}
    const data = {
      name: keyword,
    };
    $.ajax({
      url: "/product/getList",
      type: "GET",
      data: data,
      dataType:"json",
      success: function(res) {
        // 跳转到搜索结果页面
          if(res.code == 200){
              document.getElementById('mid-swiper').style.display = 'none';
              category_arr = res.data;
              renderCategory(res.data);
          }else{
              openModal('错误','搜索失败，请联系管理员！');
          }
      },
      fail: function(xhr, status, error) {
        // 显示错误信息给用户
        openModal('错误','搜索失败，请联系管理员！'+error);
      }
    });
}