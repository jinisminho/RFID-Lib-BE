ALTER TABLE `library_rfid`.`book_copy_position` 
ADD COLUMN `rfid` VARCHAR(80) NULL AFTER `line`,
ADD UNIQUE INDEX `rfid_UNIQUE` (`rfid` ASC) VISIBLE;