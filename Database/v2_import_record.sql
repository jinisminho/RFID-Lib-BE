use library_rfid;


insert into role (id, name) values
(1, 'ROLE_ADMIN'),
(2, 'ROLE_LIBRARIAN'),
(3, 'ROLE_PATRON');

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

/*student*/
insert into account (id, email, password, rfid, avatar, isActive, created_at, updated_at, created_by, updated_by, role_id, pin) values
(6, 'tramphse130038@fpt.edu.vn', '123', '1234', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),2, 2, 3, '1111'),
(7, 'hoangpmse130054@fpt.edu.vn', '123', '5678', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),2, 2, 3, '1111'),
(8, 'kienntse130154@fpt.edu.vn', '123', '9635', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', true, now(), now(),3, 3, 3, '1111'),
(9, 'khangndnse130148@fpt.edu.vn', '123', '7854', 'https://st2.depositphotos.com/1009634/7235/v/600/depositphotos_72350117-stock-illustration-no-user-profile-picture-hand.jpg', false, now(), now(),4, 4, 3, '1111');

insert into profile (fullname, phone, gender, account_id, department) values
('Phan Hoang Tram', '0965457000', 'F', 6, 'Software Engineering'),
('Pham Minh Hoang', '0657111000', 'M', 7, 'Software Engineering'),
('Nguyen Trung Kien', '0948634000', 'M', 8, 'Software Engineering'),
('Phan Hoang Oanh', '0916741000', 'M', 9, 'Software Engineering');

/********/
insert into book_position (id, floor, shelf, line) values
(1, 1, 'A1', 1),
(2, 1, 'A1', 2),
(3, 1, 'A1', 3),
(4, 1, 'A1', 4);

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



insert into book (id, ISBN, title, subtitle, publisher, publish_year, edition, language, page_number, ddc, number_of_copy, status, created_at, updated_at, created_by, updated_by, position_id) values
(1, '0345339681', 'The hobit', 'an expected journey', 'Recorded Books', 1966, 1, 'English', 306, '791.4372', 2, 'ACCEPTED', now(), now(), 2,2,1),
(2, '9780307887436', 'Ready player one', 'a novel', 'Crown Publishers', 2011, 1, 'English', 306, '823.6', 2, 'ACCEPTED', now(), now(), 2,2,2),
(3, '9780911116304', 'The story of the Acadians', '', 'Gretna [La.] Pelican Pub', 1971, 1, 'English', 32, '811.54', 1, 'ACCEPTED', now(), now(), 2,2,1),
(4, '9780785190219', 'Ms. Marvel', 'no normal', 'Marvel Worldwide', 2014, 1, 'English', 102, '741.5973', 2, 'ACCEPTED', now(), now(), 2,2,1),
(5, '9781491534663', 'Capital', 'in the Twenty-First Century', 'Brilliance Audio', 2014, 1, 'English', 102, '332.041', 3, 'ACCEPTED', now(), now(), 2,2,1),
(6, '9780743256315', 'First Man', 'The Life of Neil A. Armstrong', 'Simon & Schuster', 2005, 1, 'English', 200, '629.450092', 2, 'ACCEPTED', now(), now(), 2,2,1);


insert into category (id, name) values
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

insert into book_category (id, book_id, category_id) values 
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1),
(5, 5, 3),
(6, 6, 2),
(7, 6, 4);

/********/

insert into book_copy (id, barcode, rfid, price, status, note, created_at, updated_at, created_by, updated_by, book_id) values
(1, 'I0000001', '1', 23000, 'AVAILABLE', '', now(), now(), 2,2,1 ),
(2, 'I0000002', '2', 23000, 'AVAILABLE', '', now(), now(), 2,2,1 ),
(3, 'I0000003', '3', 23000, 'AVAILABLE', '', now(), now(), 2,2,2 ),
(4, 'I0000004', '4', 23000, 'AVAILABLE', '', now(), now(), 2,2,2 ),
(5, 'I0000005', '5', 23000, 'AVAILABLE', '', now(), now(), 2,2,3 ),
(6, 'I0000006', '6', 23000, 'AVAILABLE', '', now(), now(), 2,2,4 ),
(7, 'I0000007', '7', 23000, 'AVAILABLE', '', now(), now(), 2,2,4 ),
(8, 'I0000008', '8', 23000, 'AVAILABLE', '', now(), now(), 2,2,5),
(9, 'I0000009', '9', 23000, 'AVAILABLE', '', now(), now(), 2,2,5 ),
(10, 'I0000010', '10', 23000, 'AVAILABLE', '', now(), now(), 2,2,5 ),
(11, 'I0000011', '11', 23000, 'AVAILABLE', '', now(), now(), 2,2,6 ),
(12, 'I0000012', '12', 23000, 'AVAILABLE', '', now(), now(), 2,2,6 );



/********/
insert into borrow_policy (	id,
    due_duration,
    max_number_copy_borrow,
    max_extend_time,
    extend_due_duration,
    overdue_fine_per_day,
	created_at,
	updated_at) values
(1, 7, 4, 2, 7, 2000, now(), now() );







