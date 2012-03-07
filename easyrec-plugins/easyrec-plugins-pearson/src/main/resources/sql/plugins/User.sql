###############################################################
# $Author$
# $Revision$
# $Date$
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE p_user (
  id int(11) NOT NULL AUTO_INCREMENT,
  tenantId int(11) NOT NULL,
  userId int(11) NOT NULL,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_user (tenantId,userId)
) Comment='Users used for calculating Pearson';