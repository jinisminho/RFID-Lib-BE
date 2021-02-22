
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
    reason varchar(100) not null,
    fine double precision not null,
    
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
	status varchar(30) not null,
	created_at datetime not null default now(),
	updated_at datetime not null default now() on update now(),
    
    book_copy_type_id int, /*new*/
    created_by int,
    updated_by int,
    book_id int,
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
	primary key (id)
);

create table genre(
	id int not null auto_increment,
	name varchar(50) not null,
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



/*new */

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
    floor int not null,
    shelf varchar(50) not null,
    from_call_number varchar(50) not null,
    
    book_copy_type_id int,
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

alter table book_copy
	add constraint FK_copy_copyType
	foreign key (book_copy_type_id) references book_copy_type (id)
    ON DELETE RESTRICT ON UPDATE CASCADE; 


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
    
alter table book_genre
	add constraint FK_bookgenre_book
	foreign key (book_id) references book (id)
     ON DELETE CASCADE ON UPDATE CASCADE; 

alter table book_genre
	add constraint FK_bookgenre_genre
	foreign key (genre_id) references genre (id)
     ON DELETE CASCADE ON UPDATE CASCADE; 

alter table borrow_policy
	add constraint FK_borrowPolicy_patronType
    foreign key (patron_type_id) references patron_type (id)
	ON DELETE CASCADE ON UPDATE CASCADE; 

alter table borrow_policy
	add constraint FK_borrowPolicy_copyType
    foreign key (book_copy_type_id) references book_copy_type (id)
	ON DELETE CASCADE ON UPDATE CASCADE; 
    
alter table book_copy_position
	add constraint FK_position_copyType
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

/*insert import */

use library_rfid;


insert into role (id, name) values
(1, 'ROLE_ADMIN'),
(2, 'ROLE_LIBRARIAN'),
(3, 'ROLE_PATRON');

insert into patron_type (id, name, max_borrow_number) values
(1, 'STUDENT', 6),
(2, 'LECTURER', 10);

insert into book_copy_type (id, name) values
(1, 'TEXTBOOK'),
(2, 'REFERENCE'),
(3, 'THESIS');

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

insert into author(id, name) values
(1, 'J.R.R Tolkien'),
(2, 'Cline'),
(3, 'Ernest'),
(4, 'Boudreau'),
(5, 'Amy'),
(6, 'Wilson'),
(7, 'G. Willow'),
(8, 'Thomas Piketty'),
(9, 'Hansen');



insert into book (id, ISBN, title, subtitle, publisher, publish_year, edition, language, page_number, call_number, number_of_copy, status, created_at, updated_at, created_by, updated_by, img) values
(1, '0345339681', 'The hobit', 'an expected journey', 'Recorded Books', 1966, 1, 'English', 306, '000.123', 2, 'IN_CIRCULATION', now(), now(), 2,2, "https://i.pinimg.com/originals/a4/aa/c1/a4aac1f3d86869bcfd2833e8be768014.jpg"),
(2, '9780307887436', 'Ready player one', 'a novel', 'Crown Publishers', 2011, 1, 'English', 306, '124.564', 2, 'IN_CIRCULATION', now(), now(), 2,2, "https://i.pinimg.com/originals/a4/aa/c1/a4aac1f3d86869bcfd2833e8be768014.jpg"),
(3, '9780911116304', 'The story of the Acadians', '', 'Gretna [La.] Pelican Pub', 1971, 1, 'English', 32, '299.895', 1, 'IN_CIRCULATION', now(), now(), 2,2, "https://i.pinimg.com/originals/a4/aa/c1/a4aac1f3d86869bcfd2833e8be768014.jpg"),
(4, '9780785190219', 'Ms. Marvel', 'no normal', 'Marvel Worldwide', 2014, 1, 'English', 102, '200.986', 2, 'IN_CIRCULATION', now(), now(), 2,2, "https://i.pinimg.com/originals/a4/aa/c1/a4aac1f3d86869bcfd2833e8be768014.jpg"),
(5, '9781491534663', 'Capital', 'in the Twenty-First Century', 'Brilliance Audio', 2014, 1, 'English', 102, '300.999', 3, 'IN_CIRCULATION', now(), now(), 2,2, "https://i.pinimg.com/originals/a4/aa/c1/a4aac1f3d86869bcfd2833e8be768014.jpg"),
(6, '9780743256315', 'First Man', 'The Life of Neil A. Armstrong', 'Simon & Schuster', 2005, 1, 'English', 200, '301.574', 2, 'IN_CIRCULATION', now(), now(), 2,2, "https://i.pinimg.com/originals/a4/aa/c1/a4aac1f3d86869bcfd2833e8be768014.jpg");


insert into genre (id, name) values
(1, 'Fiction'),
(2,'Engineering'),
(3, 'Economics'),
(4, 'Science');

insert into book_author (id, book_id, author_id) values
(1, 1, 1),
(2, 2, 2),
(3, 2, 3),
(4, 3, 4),
(5, 3, 5),
(6, 4, 6),
(7, 4, 7),
(8, 5, 8),
(9, 6, 9);

insert into book_genre (id, book_id, genre_id) values 
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1),
(5, 5, 3),
(6, 6, 2),
(7, 6, 4);

/********/

insert into book_copy (id, barcode, rfid, price, status, created_at, updated_at, created_by, updated_by, book_id, book_copy_type_id) values
(1, 'I0000001', '1', 23000, 'AVAILABLE', now(), now(), 2,2,1, 1 ),
(2, 'I0000002', '2', 23000, 'AVAILABLE', now(), now(), 2,2,1, 1 ),
(3, 'I0000003', '3', 23000, 'AVAILABLE', now(), now(), 2,2,2, 1 ),
(4, 'I0000004', '4', 23000, 'AVAILABLE', now(), now(), 2,2,2 , 1),
(5, 'I0000005', '5', 23000, 'AVAILABLE', now(), now(), 2,2,3, 2 ),
(6, 'I0000006', '6', 23000, 'AVAILABLE', now(), now(), 2,2,4, 2 ),
(7, 'I0000007', '7', 23000, 'AVAILABLE', now(), now(), 2,2,4, 2 ),
(8, 'I0000008', '8', 23000, 'AVAILABLE', now(), now(), 2,2,5, 3),
(9, 'I0000009', '9', 23000, 'AVAILABLE', now(), now(), 2,2,5, 3 ),
(10, 'I0000010', '10', 23000, 'AVAILABLE', now(), now(), 2,2,5, 3 ),
(11, 'I0000011', '11', 23000, 'AVAILABLE', now(), now(), 2,2,6, 1 ),
(12, 'I0000012', '12', 23000, 'AVAILABLE', now(), now(), 2,2,6, 1 );



/********/
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
(3, 7, 4, 2, 7,  now(), now(), 1, 3 ),
(4, 7, 4, 2, 7,  now(), now(), 2, 1 ),
(5, 7, 4, 2, 7,  now(), now(), 2, 2 ),
(6, 7, 4, 2, 7, now(), now(), 2, 3 );

insert into book_copy_position (id, floor, shelf, from_call_number, book_copy_type_id) values
(1, 1, 'A1', '000', 1),
(2, 1, 'A2', '100', 1),
(3, 1, 'A3', '200', 1),
(4, 1, 'A4', '300', 1),
(5, 2, 'B1', '000', 2),
(6, 2, 'B2', '100', 2),
(7, 2, 'B3', '200', 2),
(8, 2, 'B4', '300', 2),
(9, 3, 'C1', '000', 3),
(10, 3, 'C2', '100', 3),
(11, 3, 'C3', '200', 3),
(12, 3, 'C4', '300', 3);


insert into fee_policy (id,
    overdue_fine_per_day,
    max_percentage_overdue_fine,
    document_processing_fee,
    missing_doc_multiplier,
    created_at
) values
(1, 2000, 100, 30000, 5, now());

/* add trigger*/

use library_rfid;

create table security_deactivated_copy (
	rfid varchar(80) not null,
    primary key (rfid)
);

Delimiter $$

create trigger TR_INSERT_BORROWING_BOOK
    after update on book_copy
    for each row
begin
	DECLARE copy_rfid varchar(80);
	DECLARE copy_status varchar(30);
    DECLARE existing_rfid_security varchar(80);
	
    SET copy_rfid = new.rfid;
    SET copy_status = new.status;
    
    
    if(copy_status = 'BORROWED') then
		insert into security_deactivated_copy (rfid) value (copy_rfid);
	elseif (copy_status = 'AVAILABLE') then
		set existing_rfid_security := (select rfid from security_deactivated_copy where rfid = copy_rfid);
        if (existing_rfid_security is not null) then
			delete from security_deactivated_copy where rfid = copy_rfid;
        end if;
	end if;
    
end $$











