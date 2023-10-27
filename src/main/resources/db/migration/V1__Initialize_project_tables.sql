create table product
(
    id    bigint         not null auto_increment,
    name  varchar(255)   not null,
    price decimal(19, 2) not null,
    primary key (id)
);


create table menu_group
(
    id   bigint       not null auto_increment,
    name varchar(255) not null,
    primary key (id)
);


create table menu
(
    id            bigint         not null auto_increment,
    name          varchar(255)   not null,
    price         decimal(19, 2) not null,
    menu_group_id bigint         not null,
    primary key (id)
);
alter table menu
    add constraint fk_menu_to_menu_group
        foreign key (menu_group_id)
            references menu_group (id);


create table menu_product
(
    id        bigint not null auto_increment,
    menu_id    bigint not null,
    product_id bigint not null,
    quantity   bigint not null,
    primary key (id)
);
alter table menu_product
    add constraint fk_menu_product_to_menu
        foreign key (menu_id)
            references menu (id);

alter table menu_product
    add constraint fk_menu_product_to_product
        foreign key (product_id)
            references product (id);


create table table_group
(
    id           bigint   not null auto_increment,
    created_date datetime not null,
    primary key (id)
);


create table order_table
(
    id               bigint  not null auto_increment,
    table_group_id   bigint,
    number_of_guests integer not null,
    empty            bit     not null,
    primary key (id)
);

alter table order_table
    add constraint fk_order_table_to_table_group
        foreign key (table_group_id)
            references table_group (id);


create table orders
(
    id             bigint       not null auto_increment,
    order_table_id bigint       not null,
    order_status   varchar(255) not null,
    ordered_time   datetime     not null,
    primary key (id)
);
alter table orders
    add constraint fk_orders_to_order_table
        foreign key (order_table_id)
            references order_table (id);


create table order_line_item
(
    id      bigint not null auto_increment,
    order_id bigint not null,
    menu_id  bigint not null,
    quantity bigint not null,
    primary key (id)
);
alter table order_line_item
    add constraint fk_order_line_item_to_menu
        foreign key (menu_id)
            references menu (id);

alter table order_line_item
    add constraint fk_order_line_item_to_orders
        foreign key (order_id)
            references orders (id);
