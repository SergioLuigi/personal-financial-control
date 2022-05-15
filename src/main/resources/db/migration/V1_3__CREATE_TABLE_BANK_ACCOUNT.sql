create table bank_account(
    id uuid not null,
    bank_id varchar(3) not null,
    user_id uuid not null,
    "number" varchar(25) not null,
    number_digit varchar(3) not null,
    branch varchar(4) not null,
    branch_digit varchar(3) not null,
    "type" varchar(100) not null,
    "limit" decimal(8,2) default 0.00,
    balance decimal(8,2) default 0.00,
    create_date timestamp not null,
    update_date timestamp not null,
    constraint fk_bank_account_user_id foreign key (user_id) references proj_user(id),
    constraint fk_bank_account_bank_id foreign key (bank_id) references bank(id),
    constraint pk_bank_account primary key (id)
);