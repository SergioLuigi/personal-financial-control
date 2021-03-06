create table "card"(
    id uuid not null,
    bank_account_id uuid not null,
    "number" varchar(16) not null,
    valid_thru date not null,
    "type" varchar(100) not null,
    "limit" decimal(8,2) default 0.00,
    owner_user_id uuid not null,
    create_date timestamp not null,
    update_date timestamp not null,
    constraint un_card unique(number),
    constraint fk_card_bank_account_id foreign key (bank_account_id) references bank_account(id),
    constraint fk_card_owner_user_id foreign key (owner_user_id) references proj_user(id),
    constraint pk_card primary key (id)
);