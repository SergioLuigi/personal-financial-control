create table cash_box_transaction(
	id uuid not null,
	user_id uuid not null,
	cash_box_id uuid not null,
	operation varchar(100) not null,
	"description" varchar(150) not null,
	"value" decimal(8,2) not null,
    create_date timestamp not null,
    update_date timestamp not null,
	constraint pk_cash_box_transaction primary key(id),
	constraint fk_cash_box_transaction_user_id foreign key (user_id) references proj_user(id),
	constraint fk_cash_box_transaction_cash_box_id foreign key (cash_box_id) references cash_box(id)
);