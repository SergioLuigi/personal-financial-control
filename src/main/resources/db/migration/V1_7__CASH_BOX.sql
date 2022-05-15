create table cash_box(
	id uuid not null,
	user_id uuid not null,
	"description" varchar(150) not null,
	"value" decimal(8,2) not null,
	balance decimal(8,2) not null,
    create_date timestamp not null,
    update_date timestamp not null,
	constraint pk_cash_box primary key(id),
	constraint fk_cash_box_user_id foreign key(user_id) references proj_user(id)
);