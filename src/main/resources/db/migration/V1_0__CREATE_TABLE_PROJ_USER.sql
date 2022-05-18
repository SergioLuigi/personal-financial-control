create table proj_user(
    id uuid not null,
    username varchar(20) not null,
    cpf varchar(11) not null,
    email varchar(120) not null,
    "name" varchar(100) not null,
    last_name varchar(120) not null,
    create_date timestamp not null,
    update_date timestamp not null,
    constraint pk_proj_user primary key(id),
    constraint un_proj_user_username unique(username),
    constraint un_proj_user_cpf unique(cpf),
    constraint un_proj_user_email unique(email)
);