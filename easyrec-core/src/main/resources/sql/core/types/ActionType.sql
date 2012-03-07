###############################################################
# $Author: sat-rsa $
# $Revision: 140 $
# $Date: 2012-02-23 16:01:50 +0100 (Do, 23 Feb 2012) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE actiontype (
  tenantId INT(11) unsigned NOT NULL,
  name VARCHAR(50) NOT NULL,
  id INT(11) NOT NULL,
  hasvalue BIT(1) NOT NULL DEFAULT b'0',
  UNIQUE KEY (tenantId, name),
  UNIQUE KEY (tenantId, id)
) COMMENT='Table containing actiontypes';
