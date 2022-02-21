ALTER TABLE `library_rfid`.`book_copy` 
ADD COLUMN `price_note` VARCHAR(500) NULL DEFAULT '' AFTER `book_copy_position_id`;
