use library_rfid;


insert into role (id, name) values
(1, 'ROLE_ADMIN'),
(2, 'ROLE_LIBRARIAN'),
(3, 'ROLE_PATRON');

insert into patron_type (id, name) values
(1, 'STUDENT'),
(2, 'LECTURER');

insert into book_copy_type (id, name) values
(1, 'TEXTBOOK'),
(2, 'REFERENCE'),
(3, 'THESIS');

/*******/

/*admin*/
insert into account (id, email, password, rfid, avatar, isActive, created_at, updated_at, created_by, updated_by, role_id) values
(1, 'tuongnt1@fpt.edu.vn', '123', '123', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),1, 1, 2);

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
('Phan Hoang Oanh', '0916741000', 'M', 9);


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



insert into book (id, ISBN, title, subtitle, publisher, publish_year, edition, language, page_number, call_number, number_of_copy, status, created_at, updated_at, created_by, updated_by) values
(1, '0345339681', 'The hobit', 'an expected journey', 'Recorded Books', 1966, 1, 'English', 306, 'A1-1', 2, 'IN_CIRCULATION', now(), now(), 2,2),
(2, '9780307887436', 'Ready player one', 'a novel', 'Crown Publishers', 2011, 1, 'English', 306, 'A2-1', 2, 'IN_CIRCULATION', now(), now(), 2,2),
(3, '9780911116304', 'The story of the Acadians', '', 'Gretna [La.] Pelican Pub', 1971, 1, 'English', 32, 'A2-1', 1, 'IN_CIRCULATION', now(), now(), 2,2),
(4, '9780785190219', 'Ms. Marvel', 'no normal', 'Marvel Worldwide', 2014, 1, 'English', 102, 'A2-1', 2, 'IN_CIRCULATION', now(), now(), 2,2),
(5, '9781491534663', 'Capital', 'in the Twenty-First Century', 'Brilliance Audio', 2014, 1, 'English', 102, 'A2-1', 3, 'IN_CIRCULATION', now(), now(), 2,2),
(6, '9780743256315', 'First Man', 'The Life of Neil A. Armstrong', 'Simon & Schuster', 2005, 1, 'English', 200, 'A2-1', 2, 'IN_CIRCULATION', now(), now(), 2,2);


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
    max_number_copy_borrow,
    max_extend_time,
    extend_due_duration,
    overdue_fine_per_day,
	created_at,
	updated_at,
    patron_type_id,
    book_copy_type_id
    ) values
(1, 7, 4, 2, 7, 2000, now(), now(), 1, 1 ),
(2, 7, 4, 2, 7, 2000, now(), now(), 1, 2 ),
(3, 7, 4, 2, 7, 2000, now(), now(), 1, 3 ),
(4, 7, 4, 2, 7, 2000, now(), now(), 2, 1 ),
(5, 7, 4, 2, 7, 2000, now(), now(), 2, 2 ),
(6, 7, 4, 2, 7, 2000, now(), now(), 2, 3 );

insert into book_copy_position (id, floor, shelf, from_call_numer, to_call_number, book_copy_type) values
(1, 1, 'Henry', 'A1', 'B1', 1),
(1, 1, 'Lucy', 'A1', 'B1', 2),
(1, 1, 'Mars', 'A1', 'B1', 3);








