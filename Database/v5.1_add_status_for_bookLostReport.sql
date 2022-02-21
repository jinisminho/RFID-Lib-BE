use library_rfid;

alter table book_lost_report
add column status varchar(30) not null;

ALTER TABLE book_lost_report 
RENAME COLUMN reported_by TO confirmed_by;