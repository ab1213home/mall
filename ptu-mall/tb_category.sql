create table tb_category
(
    id         bigint               not null
        primary key,
    code       varchar(50)          null comment '编码，需唯一',
    name       varchar(255)         null comment '分类名称',
    creator    varchar(255)         null comment '创建人',
    created_at datetime             null comment '创建时间',
    updater    bigint               null comment '更新人',
    updated_at datetime             null comment '更新时间',
    is_del     tinyint(1) default 0 null comment '是否删除'
)
    row_format = DYNAMIC;

