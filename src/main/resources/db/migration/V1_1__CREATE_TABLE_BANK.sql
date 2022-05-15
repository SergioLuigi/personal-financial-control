create table bank(
    id varchar(3) not null,
    "name" varchar(200) not null,
    constraint pk_bank primary key(id),
    constraint un_bank_name unique(name)
);