###############################################################
# $Author: sat-rsa $
# $Revision: 6 $
# $Date: 2010-02-16 15:53:29 +0100 (Tue, 16 Feb 2010) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE recommendation (
  id INT(11) unsigned NOT NULL AUTO_INCREMENT,
  tenantId INT(11) NOT NULL,
  userId INT(11),
  queriedItemId INT(11),
  queriedItemTypeId INT(11),
  queriedAssocTypeId INT(11),
  relatedActionTypeId INT(11),
  recommendationStrategy VARCHAR(50),
  explanation VARCHAR(255),
  recommendationTime DATETIME NOT NULL,
  PRIMARY KEY (id)
) COMMENT='Table containing the history of recommendations';


