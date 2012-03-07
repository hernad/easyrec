# $Author: sat-rsa $
# $Date: 2011-08-12 18:40:54 +0200 (Fr, 12 Aug 2011) $
# $Revision: 119 $
drop table if exists testtable;
CREATE TABLE testtable (
  id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  name varchar(100),
  PRIMARY KEY  (id)
);
