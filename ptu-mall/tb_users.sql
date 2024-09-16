create table tb_users
(
    id                 bigint auto_increment comment '用户唯一编号'
        primary key,
    username           varchar(32)          not null comment '用户名',
    password           varchar(32)          not null comment '密码',
    email              varchar(255)         not null comment '用户的电子邮件地址',
    phone              varchar(15)          null comment '用户的电话号码',
    first_name         varchar(100)         null comment '用户的名字',
    last_name          varchar(100)         null comment '用户的姓氏',
    birth_date         date                 null comment '用户的生日',
    created_at         datetime             null comment '创建的时间戳',
    updater            bigint               null comment '更新人',
    updated_at         datetime             null comment '更新的时间戳',
    is_active          tinyint(1)           not null comment '用户账户的状态，是否激活',
    role_id            int        default 1 not null comment '用户的角色ID，用于权限管理',
    default_address_id bigint               null comment '默认地址编号',
    is_del             tinyint(1) default 0 not null comment '是否删除'
)
    comment '用户表';

