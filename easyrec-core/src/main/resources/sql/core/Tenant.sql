###############################################################
# $Author: sat-rsa $
# $Revision: 113 $
# $Date: 2011-08-12 16:46:14 +0200 (Fr, 12 Aug 2011) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE tenant (
  id INT(11) unsigned NOT NULL,
  stringId VARCHAR(100) NOT NULL,
  description VARCHAR(250),
  ratingRangeMin INT(11) unsigned,
  ratingRangeMax INT(11) unsigned,
  ratingRangeNeutral DOUBLE,
  active TINYINT(1) NOT NULL DEFAULT '1',
  operatorid varchar(250) DEFAULT NULL,
  url varchar(250) DEFAULT NULL,
  creationdate datetime DEFAULT NULL,
  tenantConfig mediumblob,
  tenantStatistic mediumblob,
  PRIMARY KEY (id),
  UNIQUE KEY (stringId) 
)ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table containing tenants';
