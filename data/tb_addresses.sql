create table tb_addresses
(
    id             bigint auto_increment comment '主键，唯一标识每一条记录'
        primary key,
    user_id        bigint               not null comment '用户ID，关联用户表',
    first_name     varchar(255)         not null comment '收件人名字（名）',
    last_name      varchar(255)         not null comment '收件人名字（姓）',
    phone          varchar(20)          not null comment '收件人联系电话',
    country        varchar(255)         not null comment '地址所在的国家',
    province       varchar(255)         not null comment '地址所在的省或州',
    city           varchar(255)         not null comment '地址所在的城市',
    district       varchar(255)         not null comment '地址所在的区或县',
    address_detail text                 not null comment '具体的街道、小区、门牌号等信息',
    postal_code    varchar(20)          not null comment '邮政编码',
    created_at     datetime             null comment '创建的时间',
    updated_at     datetime             null comment '更新的时间',
    is_del         tinyint(1) default 0 not null comment '是否删除 '
)
    comment '用户地址表';

