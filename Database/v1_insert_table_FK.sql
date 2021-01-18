use library_rfid;

create table role(
	id int not null auto_increment,
	name varchar(20) not null,
	primary key (id)
);

create table account( 
	id int not null auto_increment,
	email varchar(100) not null,
	password varchar(100) not null,
	rfid varchar(80) ,
    avatar varchar(500),
    isActive bool not null,
    created_at datetime not null default now(),
	updated_at datetime not null default now() on update now(),
    
    created_by int,
    updated_by int,
    role_id int,
	primary key (id)
);

create table profile(
    fullname varchar(50) not null,
    phone varchar(10) not null,
    gender varchar(1) not null,
    
	account_id int not null,
	primary key (account_id)
);

create table wishlist_book( 
	id int not null auto_increment,
	created_at datetime not null default now(),
	email varchar(100) not null,
    status varchar(20) not null,
    
    book_id int, 
    wish_by int, 
	primary key (id)
);

create table book_lost_report(
	id int not null auto_increment,
	lost_at datetime not null default now(),
    reason varchar(100) not null,
    
    borrow_id int,
    lost_by int,
    reported_by int,
    book_copy_id int,
	primary key (id)
);

create table book_copy( 
	id int not null auto_increment,
    barcode varchar(100) not null ,
	rfid varchar(80) ,
	price double precision not null,
	status varchar(20) not null,
	created_at datetime not null default now(),
	updated_at datetime not null default now() on update now(),
    
    created_by int,
    updated_by int,
    book_id int,
	primary key (id)
);

create table book_borrowing(
	id int not null auto_increment,
	borrowed_at datetime not null,
    returned_at datetime,
    due_at date not null,
    extended_at datetime,
    extend_index int,
    overdue_fine_per_day double precision not null,
    
    returned_by int,
    borrowed_by int,
    issued_by int,
    book_copy_id int,
	primary key (id)
);


create table extend_history(
	id int not null auto_increment,
	borrowed_at datetime not null,
    extended_at datetime,
    extend_index int,
    due_at date not null,
    
    book_borrowing_id int,
    issued_by int,
	primary key (id)
);

create table book(
	id int not null auto_increment,
    ISBN varchar(20) not null,
    title varchar(255) not null,
    subtitle varchar (255),
    publisher varchar(255) not null,
    publish_year int not null,
    edition int not null,
    language varchar(20),
    page_number int,
    call_number varchar(20),
	number_of_copy int not null,
    status varchar(20) not null,
    created_at datetime not null default now(),
	updated_at datetime not null default now() on update now(),

    created_by int,
    updated_by int,
    primary key (id)
);


create table author(
	id int not null auto_increment,
	name varchar(100) not null,
	primary key (id)
);

create table genre(
	id int not null auto_increment,
	name varchar(100) not null,
	primary key (id)
);

create table book_author(
	id int not null auto_increment,
	
    book_id int,
    author_id int,
	primary key (id)
);

create table book_genre(
	id int not null auto_increment,
	
    book_id int,
    genre_id int,
	primary key (id)
);

create table borrow_policy(
	id int not null auto_increment,
    due_duration int not null,
    max_number_copy_borrow int not null,
    max_extend_time int not null,
    extend_due_duration int not null,
    overdue_fine_per_day double precision not null,
    policy_form_url varchar(500),
	created_at datetime not null default now(),
	updated_at datetime not null default now() on update now(),
    primary key (id)
);



/****ADD FK****/


alter table account
	add constraint FK_account_role
	foreign key (role_id) references role (id)
	ON DELETE SET NULL ON UPDATE CASCADE; 

    
alter table account
	add constraint FK_acc_acc_create
	foreign key (created_by) references account (id)
	ON DELETE SET NULL ON UPDATE CASCADE; 

  
alter table account
	add constraint FK_acc_acc_update
	foreign key (updated_by) references account (id)
	ON DELETE SET NULL ON UPDATE CASCADE; 

 
alter table profile
	add constraint FK_profile_account
	foreign key (account_id) references account (id)
	ON DELETE CASCADE ON UPDATE CASCADE; 
    
alter table wishlist_book
	add constraint FK_wishlist_book
	foreign key (book_id) references book (id)
    ON DELETE CASCADE ON UPDATE CASCADE; 
 
alter table wishlist_book
	add constraint FK_wishlist_account
	foreign key (wish_by) references account (id)
    ON DELETE CASCADE ON UPDATE CASCADE; 

alter table book_lost_report
	add constraint FK_lost_borrowing
	foreign key (borrow_id) references book_borrowing (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 
    

alter table book_lost_report
	add constraint FK_lost_account_st
	foreign key (lost_by) references account (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 
    
alter table book_lost_report
	add constraint FK_lost_account_lib
	foreign key (reported_by) references account (id)
	ON DELETE SET NULL ON UPDATE CASCADE; 


alter table book_lost_report
	add constraint FK_lost_copy
	foreign key (book_copy_id) references book_copy (id)
	ON DELETE SET NULL ON UPDATE CASCADE; 
    
alter table book_copy
	add constraint FK_copy_book
	foreign key (book_id) references book (id)
	ON DELETE RESTRICT ON UPDATE CASCADE; 
    
alter table book_copy
	add constraint FK_copy_acc_create
	foreign key (created_by) references account (id)
	ON DELETE SET NULL ON UPDATE CASCADE; 

alter table book_copy
	add constraint FK_copy_acc_update
	foreign key (updated_by) references account (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 
    
alter table book_borrowing
	add constraint FK_borrow_account_br
	foreign key (borrowed_by) references account (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 
    
alter table book_borrowing
	add constraint FK_borrow_account_is
	foreign key (issued_by) references account (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 
    
alter table book_borrowing
	add constraint FK_borrow_copy
	foreign key (book_copy_id) references book_copy (id)
    ON DELETE SET NULL ON UPDATE CASCADE;

alter table book_borrowing
	add constraint FK_borrow_acc_rt
	foreign key (returned_by) references account (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 


alter table extend_history
	add constraint FK_extend_borrow
	foreign key (book_borrowing_id) references book_borrowing (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 
    
alter table extend_history
	add constraint FK_extend_account
	foreign key (issued_by) references account (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 
    
    
alter table book
	add constraint FK_book_acc_create
	foreign key (created_by) references account (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 

alter table book
	add constraint FK_book_acc_update
	foreign key (updated_by) references account (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 

alter table book_author
	add constraint FK_bookauthor_book
	foreign key (book_id) references book (id)
    ON DELETE CASCADE ON UPDATE CASCADE; 
    
alter table book_author
	add constraint FK_bookauthor_author
	foreign key (author_id) references author (id)
	ON DELETE CASCADE ON UPDATE CASCADE; 
    
alter table book_genre
	add constraint FK_bookgenre_book
	foreign key (book_id) references book (id)
     ON DELETE CASCADE ON UPDATE CASCADE; 

alter table book_genre
	add constraint FK_bookgenre_genre
	foreign key (genre_id) references genre (id)
     ON DELETE CASCADE ON UPDATE CASCADE; 


/****ADD UNIQUE KEY****/

ALTER TABLE role
ADD CONSTRAINT UK_role_name UNIQUE (name);

ALTER TABLE account
ADD CONSTRAINT UK_account_email UNIQUE (email);

ALTER TABLE account
ADD CONSTRAINT UK_account_rfid UNIQUE (rfid);

ALTER TABLE book_copy
ADD CONSTRAINT UK_bookCopy_rfid UNIQUE (rfid);

ALTER TABLE book_copy
ADD CONSTRAINT UK_bookCopy_barcode UNIQUE (barcode);
