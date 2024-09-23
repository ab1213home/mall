create table tb_banner
(
    id          bigint               not null
        primary key,
    img         varchar(255)         not null comment '轮播图图片地址',
    url         varchar(255)         not null comment '跳转URL',
    description varchar(255)         not null comment '描述信息',
    creator     varchar(255)         null comment '创建人',
    created_at  datetime             null comment '创建时间',
    updater     bigint               null comment '更新人',
    updated_at  datetime             null comment '更新时间',
    is_del      tinyint(1) default 0 not null comment '是否删除'
)
    row_format = DYNAMIC;

