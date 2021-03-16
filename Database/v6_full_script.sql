/*rework for search and sort: change genre and book_copy_position*/
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
    
    patron_type_id int, 
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
    status varchar(30) not null,
    
    book_id int, 
    wish_by int, 
	primary key (id)
);

create table book_lost_report(
	id int not null auto_increment,
	lost_at datetime not null default now(),
    reason varchar(100),
    fine double precision not null,
    status varchar(30) not null,
    
    borrow_id int,
    confirmed_by int,
	primary key (id)
);

create table book_copy( 
	id int not null auto_increment,
    barcode varchar(100) not null ,
	rfid varchar(80) ,
	price double precision not null,
	status varchar(30) not null,
	created_at datetime not null default now(),
	updated_at datetime not null default now() on update now(),
    
    book_copy_type_id int,
    created_by int,
    updated_by int,
    book_id int,
    book_copy_position_id int,
	primary key (id)
);

create table borrowing (
	id int not null auto_increment,
	borrowed_at datetime not null,
	note varchar(500),
    
	borrowed_by int,
    primary key (id)
);

create table book_borrowing(
	id int not null auto_increment,
    returned_at datetime,
    due_at date not null,
    extended_at datetime,
    extend_index int,
    lost_at datetime,
    note varchar(500),
    fine double precision default 0,
    
    borrow_id int,
    returned_by int,
    issued_by int,
    book_copy_id int,
    fee_policy_id int, 
	primary key (id)
);


create table extend_history(
	id int not null auto_increment,
	borrowed_at datetime not null,
    extended_at datetime,
    extend_index int default 0,
    due_at date not null,
	note varchar(500),
    
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
    language varchar(30),
    page_number int default 0,
    call_number varchar(50) not null,
	number_of_copy int not null,
    status varchar(30) not null,
    created_at datetime not null default now(),
	updated_at datetime not null default now() on update now(),
    img varchar(500) not null,

    created_by int,
    updated_by int,
    primary key (id)
);


create table author(
	id int not null auto_increment,
	name varchar(50) not null,
	country varchar(50) not null,
	birth_year int,
	primary key (id)
);

create table genre (
	id int not null auto_increment,
    ddc  double precision not null,
	name varchar(100) not null,
	primary key (id)
);

create table book_author(
	id int not null auto_increment,
	
    book_id int,
    author_id int,
	primary key (id)
);


create table patron_type (
	id int not null auto_increment,
    name varchar(100) not null,
    max_borrow_number int not null,
	primary key (id)
);

create table book_copy_type (
	id int not null auto_increment,
    name varchar(100) not null,
	primary key (id)
);

create table borrow_policy(
	id int not null auto_increment,
    due_duration int not null,
    max_borrow_number int not null,
    max_extend_time int not null,
    extend_due_duration int not null,
	created_at datetime not null default now(),
	updated_at datetime not null default now() on update now(),
    
    patron_type_id int,
    book_copy_type_id int,
    primary key (id)
);

create  table book_copy_position (
	id int not null auto_increment,
    shelf varchar(100) not null,
    line int not null,
    
	primary key (id)
); 

create table fee_policy(
    id int not null auto_increment,
    overdue_fine_per_day double precision not null,
    max_percentage_overdue_fine int not null,
    document_processing_fee  double precision not null,
    missing_doc_multiplier int not null,
    created_at datetime not null default now(),
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

alter table account
	add constraint FK_acc_patronType
	foreign key (patron_type_id) references patron_type (id)
	ON DELETE RESTRICT ON UPDATE CASCADE; 
 
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
	add constraint FK_lost_account_lib
	foreign key (confirmed_by) references account (id)
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

alter table book_copy
	add constraint FK_copy_copyType
	foreign key (book_copy_type_id) references book_copy_type (id)
    ON DELETE RESTRICT ON UPDATE CASCADE; 
    
alter table book_copy
	add constraint FK_copy_position
	foreign key (book_copy_position_id) references book_copy_position (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 


alter table borrowing
	add constraint FK_borrow_account_br
	foreign key (borrowed_by) references account (id)
    ON DELETE SET NULL ON UPDATE CASCADE; 
    
 alter table book_borrowing
	add constraint FK_borrow_book_borrow
	foreign key (borrow_id) references borrowing (id)
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
    
alter table book_borrowing
	add constraint FK_borrow_feePolicy
	foreign key (fee_policy_id) references fee_policy (id)
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
    

alter table borrow_policy
	add constraint FK_borrowPolicy_patronType
    foreign key (patron_type_id) references patron_type (id)
	ON DELETE CASCADE ON UPDATE CASCADE; 

alter table borrow_policy
	add constraint FK_borrowPolicy_copyType
    foreign key (book_copy_type_id) references book_copy_type (id)
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

ALTER TABLE patron_type
ADD CONSTRAINT UK_patronType_name UNIQUE (name);

ALTER TABLE book_copy_type
ADD CONSTRAINT UK_copyType_name UNIQUE (name);

ALTER TABLE book
ADD CONSTRAINT UK_book_isbn UNIQUE (ISBN);

ALTER TABLE book_copy_position
ADD CONSTRAINT UK_bookCopyPositioin_shelf UNIQUE (shelf);

ALTER TABLE genre
ADD CONSTRAINT UK_genre_ddc UNIQUE (ddc);

/*insert import */

insert into genre (id, ddc, name) values
(1, 000, 'Computer science, information & general works'),
(2, 100, 'Philosophy & psychology'),
(3, 200, 'Religion'),
(4, 300, 'Social sciences'),
(5, 400, 'Language'),
(6, 500, 'Pure Science'),
(7, 600, 'Technology'),
(8, 700, 'Arts & recreation'),
(9, 800, 'Literature'),
(10,900, 'History & geography');

insert into role (id, name) values
(1, 'ROLE_ADMIN'),
(2, 'ROLE_LIBRARIAN'),
(3, 'ROLE_PATRON');

insert into patron_type (id, name, max_borrow_number) values
(1, 'STUDENT', 6),
(2, 'LECTURER', 10);

insert into book_copy_type (id, name) values
(1, 'REGULAR'),
(2, 'REFERENCE'),
(3, 'RARE');

/*******/

/*admin*/
insert into account (id, email, password, rfid, avatar, isActive, created_at, updated_at, created_by, updated_by, role_id) values
(1, 'tuongnt1@fpt.edu.vn', '123', '123', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),1, 1, 1);

insert into profile (fullname, phone, gender, account_id) values
('Pham Minh Tuong', '0912685441', 'M', 1);


/*librarian*/
insert into account (id, email, password, rfid, avatar, isActive, created_at, updated_at, created_by, updated_by, role_id) values
(2, 'tramptn1@fpt.edu.vn', '123', '753', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),1, 1, 2),
(3, 'thuypt2@fpt.edu.vn', '123', '456', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),1, 1, 2),
(4, 'huynm3@fpt.edu.vn', '123', '789', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),1, 1, 2),
(5, 'oanhph4@fpt.edu.vn', '123', '657', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', false, now(), now(),1, 1, 2);

insert into profile (fullname, phone, gender, account_id) values
('Pham Thi Tram', '0965457361', 'F', 2),
('Pham Thi Thuy', '0657111491', 'F', 3),
('Nguyen Minh Huy', '0948634582', 'M', 4),
('Phan Hoang Oanh', '0916741852', 'F', 5);

/*patron*/
insert into account (id, email, password, rfid, avatar, isActive, created_at, updated_at, created_by, updated_by, role_id, patron_type_id) values
(6, 'tramphse130038@fpt.edu.vn', '123', '1234', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),2, 2, 3, 1),
(7, 'hoangpmse130054@fpt.edu.vn', '123', '5678', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),2, 2, 3, 1),
(8, 'kienntse130154@fpt.edu.vn', '123', '9635', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),3, 3, 3, 2),
(9, 'khangndnse130148@fpt.edu.vn', '123', '7854', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', false, now(), now(),4, 4, 3, 2);

insert into profile (fullname, phone, gender, account_id) values
('Phan Hoang Tram', '0965457000', 'F', 6),
('Pham Minh Hoang', '0657111000', 'M', 7),
('Nguyen Trung Kien', '0948634000', 'M', 8),
('Nguyen Do Nhat Khang', '0916741000', 'M', 9);


/********/

insert into author(id, name, country, birth_year) values
(1, 'J.R.R Tolkien', 'US', 1980),
(2, 'Cline', 'US', 1970),
(3, 'Ernest', 'US', 1970),
(4, 'Boudreau', 'US', 1970),
(5, 'Amy', 'US', 1970),
(6, 'Wilson', 'US', 1970),
(7, 'G. Willow', 'US', 1970),
(8, 'Thomas Piketty', 'US', 1970),
(9, 'Hansen', 'US', 1970);

insert into borrow_policy (	id,
    due_duration,
    max_borrow_number,
    max_extend_time,
    extend_due_duration,
	created_at,
	updated_at,
    patron_type_id,
    book_copy_type_id
    ) values
(1, 7, 4, 2, 7,  now(), now(), 1, 1 ),
(2, 7, 4, 2, 7,  now(), now(), 1, 2 ),
(3, 7, 4, 2, 7,  now(), now(), 2, 1 ),
(4, 7, 4, 2, 7,  now(), now(), 2, 2 ),
(5, 7, 4, 2, 7, now(), now(), 2, 3 );


insert into fee_policy (id,
    overdue_fine_per_day,
    max_percentage_overdue_fine,
    document_processing_fee,
    missing_doc_multiplier,
    created_at
) values
(1, 2000, 100, 30000, 5, now());

/* security*/

create table security_deactivated_copy (
	id int not null auto_increment,
	rfid varchar(80) not null,
    primary key (id)
);


create table security_gate_log (
	id int not null auto_increment,
	logged_at datetime not null default now(),
	book_copy_id int,
	primary key (id)
);

alter table security_gate_log
	add constraint FK_SCL_bookCopy
    foreign key (book_copy_id) references book_copy (id)
	ON DELETE CASCADE ON UPDATE CASCADE; 

ALTER TABLE security_deactivated_copy
ADD CONSTRAINT UK_SDCopy_rfid UNIQUE (rfid);


















