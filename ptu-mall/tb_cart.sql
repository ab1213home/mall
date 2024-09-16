create table tb_cart
(
    id      bigint not null
        primary key,
    prod_id bigint not null comment '商品id',
    num     bigint not null comment '商品数量',
    user_id bigint not null comment '用户id'
)
    row_format = DYNAMIC;

