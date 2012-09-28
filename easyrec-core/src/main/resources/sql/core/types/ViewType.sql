###############################################################
# $Author: sat-rsa $
# $Revision: 6 $
# $Date: 2010-02-16 15:53:29 +0100 (Tue, 16 Feb 2010) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE viewtype (
  tenantId INT(11) unsigned NOT NULL,
  name VARCHAR(50) NOT NULL,
  id INT(11) NOT NULL,
  UNIQUE KEY (tenantId, name),
  UNIQUE KEY (tenantId, id)
) COMMENT='Table containing viewtypes';
