create table card_transaction(
	id uuid not null,
	card_id uuid not null,
	"description" varchar(150) not null,
	recurrent bool default false,
	installments decimal(2) default 1,
	"value" decimal(8,2) not null,
	"limit" decimal(8,2) default 0.00,
    create_date timestamp not null,
    update_date timestamp not null,
	constraint pk_card_transaction primary key(id),
	constraint fk_card_transaction_card_id foreign key (card_id) references card(id)
);