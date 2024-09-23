create table tb_products
(
    id          bigint               not null
        primary key,
    code        varchar(50)          not null comment '商品编码，不可重复',
    title       varchar(255)         null comment '商品标题',
    category_id bigint               null comment '商品分类id',
    img         varchar(255)         null comment '商品图片',
    price       decimal(10, 2)       null comment '商品价格',
    stocks      bigint               null comment '商品库存',
    description varchar(255)         null comment '商品描述',
    creator     bigint               null comment '创建人',
    created_at  datetime             null comment '创建时间',
    updater     bigint               null comment '更新人 ',
    updated_at  datetime             null comment '更新时间',
    is_del      tinyint(1) default 0 not null comment '是否删除'
)
    row_format = DYNAMIC;

