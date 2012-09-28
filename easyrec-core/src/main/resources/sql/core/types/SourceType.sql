###############################################################
# $Author: sat-rsa $
# $Revision: 64 $
# $Date: 2010-12-22 18:10:36 +0100 (Wed, 22 Dec 2010) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE sourcetype (
  tenantId INT(11) unsigned NOT NULL,
  name VARCHAR(250) NOT NULL,
  id INT(11) NOT NULL,
  UNIQUE KEY (tenantId, name),
  UNIQUE KEY (tenantId, id)
) COMMENT='Table containing sourcetypes';
