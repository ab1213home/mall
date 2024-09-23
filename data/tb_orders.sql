create table tb_orders
(
    id             int auto_increment comment '订单ID'
        primary key,
    address_id     int                  null comment '地址ID',
    user_id        int                  null comment '用户ID',
    date           datetime             null comment '下单日期',
    total_amount   decimal(10, 2)       null comment '总金额',
    status         int                  null comment '订单状态',
    payment_method int                  null comment '支付方式',
    created_at     datetime             null comment '创建时间',
    updated_at     datetime             null comment '更新时间',
    is_del         tinyint(1) default 0 null comment '逻辑删除标志'
)
    comment '订单表';

