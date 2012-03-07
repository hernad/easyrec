###############################################################
# $Author: sat-rsa $
# $Revision: 6 $
# $Date: 2010-02-16 15:53:29 +0100 (Di, 16 Feb 2010) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE action (
  id INT(11) unsigned NOT NULL AUTO_INCREMENT,
  tenantId INT(11) NOT NULL,
  userId INT(11),
  sessionId VARCHAR(50),
  ip VARCHAR(45),
  itemId INT(11),
  itemTypeId INT(11) NOT NULL,
  actionTypeId INT(11) NOT NULL,
  ratingValue INT(11),
  searchSucceeded TINYINT(1),
  numberOfFoundItems INT(11),
  description VARCHAR(250),
  actionTime DATETIME NOT NULL,
  PRIMARY KEY (id),
  KEY action_reader (tenantId,userId,actionTypeId,itemTypeId)
) COMMENT='Table containing user actions';
    
