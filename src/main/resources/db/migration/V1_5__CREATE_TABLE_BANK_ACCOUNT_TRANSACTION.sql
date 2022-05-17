create table bank_account_transaction(
	id uuid not null,
	user_id uuid not null,
	bank_account_id uuid not null,
	"description" varchar(150) not null,
	"value" decimal(8,2) not null,
	recurrent bool default false,
	frequency_in_days int4 default 0,
	operation varchar(100) not null,
    create_date timestamp not null,
    update_date timestamp not null,
	constraint pk_bank_account_income primary key(id),
	constraint fk_bank_account_transaction_user_id foreign key (user_id) references proj_user(id),
	constraint fk_bank_account_income_bank_account_id foreign key (bank_account_id) references bank_account(id)
);