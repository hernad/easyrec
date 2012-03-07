###############################################################
# $Author$
# $Revision$
# $Date$
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE p_userassoc (
  id int(11) NOT NULL AUTO_INCREMENT,
  tenantId int(11) NOT NULL,
  userFromId int(11) NOT NULL,
  assocValue double DEFAULT NULL,
  itemToId int(11) NOT NULL,
  itemToTypeId int(11) NOT NULL,
  sourceTypeId int(11) NOT NULL,
  changeDate datetime NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_userassoc (tenantId,userFromId,itemToId,itemToTypeId,sourceTypeId)
) Comment='Store associations between users';