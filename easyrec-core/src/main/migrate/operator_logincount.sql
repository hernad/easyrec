ALTER TABLE operator ADD lastlogin DATE NULL AFTER accesslevel;
ALTER TABLE operator ADD logincount INT UNSIGNED DEFAULT '0' NULL AFTER lastlogin;