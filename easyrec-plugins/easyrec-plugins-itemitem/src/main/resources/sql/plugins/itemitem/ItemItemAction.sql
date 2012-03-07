###############################################################
# $Author$
# $Revision$
# $Date$
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE ii_action (
  id int(11) NOT NULL,
  tenantId int(11) NOT NULL,
  userId int(11) NOT NULL,
  itemId int(11) NOT NULL,
  itemTypeId int(11) NOT NULL,
  actionTypeId int(11) NOT NULL,
  ratingValue int(11) NOT NULL,
  actionTime datetime NOT NULL,
  previousRatingValue int(11) DEFAULT NULL,
  previousActionTime datetime DEFAULT NULL,
  PRIMARY KEY (tenantId,userId,itemId,itemTypeId,actionTypeId),
  KEY idx_usertenant (userId,tenantId),
  KEY idx_usertenantitemtype (tenantId,userId,itemTypeId),
  KEY idx_prevActionTenant (tenantId,previousActionTime)
) Comment='Stores latest action done by user';