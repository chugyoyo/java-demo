create schema chug_java_yo;

drop table if exists chug_java_yo.chug_user;
create table if not exists chug_java_yo.chug_user
(
    id        bigint                 not null comment '用户id'
        primary key,
    user_name varchar(20) default '' not null comment '用户名',
    password  varchar(64) default '' not null comment '密码',
    user_status    tinyint     default 0  not null comment '状态，0: 异常 1：正常'
)
    comment '用户';

insert into chug_java_yo.chug_user (id, user_name, password, user_status)
values (1, 'admin', '123456', 1);
