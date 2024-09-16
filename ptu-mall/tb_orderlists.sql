create table tb_orderlists
(
    id         int auto_increment comment '订单明细ID'
        primary key,
    order_id   int                  null comment '订单ID',
    prod_id    int                  null comment '商品ID',
    num        int                  null comment '数量',
    price      decimal(10, 2)       null comment '小计',
    created_at datetime             null comment '创建时间',
    updated_at datetime             null comment '更新时间',
    is_del     tinyint(1) default 0 null comment '逻辑删除标志'
)
    comment '订单明细表';

