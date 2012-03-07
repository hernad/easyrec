###############################################################
# $Author$
# $Revision$
# $Date$
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE p_weight (
  id int(11) NOT NULL AUTO_INCREMENT,
  tenantId int(11) unsigned NOT NULL,
  user1Id int(11) unsigned NOT NULL,
  user2Id int(11) unsigned NOT NULL,
  weight double NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_weight (tenantId,user1Id,user2Id),
  KEY idx_user1 (tenantId,user1Id)
) Comment='User-User weights for Pearson Correlation';