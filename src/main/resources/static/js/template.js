(function($) {
  'use strict';
  $(function() {
    var body = $('body');
    var sidebar = $('.sidebar');

    //根据url动态添加活动类到导航链接
    //活动类也可以根据需要直接硬编码在html文件中

    // function addActiveClass(element) {
    //   if (current === "") {
    //     //for root url
    //     if (element.attr('href') === "index.html") {
    //       element.parents('.nav-item').last().addClass('active');
    //       if (element.parents('.sub-menu').length) {
    //         element.closest('.collapse').addClass('show');
    //         element.addClass('active');
    //       }
    //     }
    //   } else {
    //     //for other url
    //     if (element.attr('href').indexOf(current) !== -1) {
    //       element.parents('.nav-item').last().addClass('active');
    //       if (element.parents('.sub-menu').length) {
    //         element.closest('.collapse').addClass('show');
    //         element.addClass('active');
    //       }
    //       if (element.parents('.submenu-item').length) {
    //         element.addClass('active');
    //       }
    //     }
    //   }
    // }

    var current = location.pathname.split("/").slice(-1)[0].replace(/^\/|\/$/g, '');
    $('.nav li a', sidebar).each(function() {
      var $this = $(this);
      addActiveClass($this);
    })

    //打开任何子菜单时，关闭侧边栏中的其他子菜单

    sidebar.on('show.bs.collapse', '.collapse', function() {
      sidebar.find('.collapse.show').collapse('hide');
    });


    //更改侧边栏

    $('[data-toggle="minimize"]').on("click", function() {
      body.toggleClass('sidebar-icon-only');
    });

    //复选框和单选按钮
    $(".form-check label,.form-radio label").append('<i class="input-helper"></i>');

  });

})(jQuery);