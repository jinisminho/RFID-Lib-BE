use library_rfid;

create table security_deactivated_copy (
	rfid int not null,
    primary key (rfid)
);

Delimiter $$

create trigger TR_INSERT_BORROWING_BOOK
    after update on book_copy
    for each row
begin
	DECLARE copy_rfid varchar(80);
	DECLARE copy_status varchar(20);
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
