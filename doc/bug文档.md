1. 当商品下架或不能正常购买是，用户收藏和购物车中该商品会一直存在
2. 邮件接口重发待完成
3. 使用bootstrap+jQuery写个html
4. 支付接口完善
5. 通知接口完善
6. CREATE TABLE notice_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    channels VARCHAR(50) COMMENT '支持渠道（逗号分隔，如：0,1,2）',
    purposes VARCHAR(255) COMMENT '适用场景（逗号分隔，如：1,3,5）',
    template_content TEXT NOT NULL,
    variables VARCHAR(255) COMMENT '变量列表（逗号分隔）',
    region_config JSON COMMENT '区域配置（JSON格式）',
    priority INT DEFAULT 0 COMMENT '优先级',
    status TINYINT DEFAULT 1,
    content_type VARCHAR(20) DEFAULT 'TEXT'
);