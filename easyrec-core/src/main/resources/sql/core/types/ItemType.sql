###############################################################
# $Author: sat-rsa $
# $Revision: 113 $
# $Date: 2011-08-12 16:46:14 +0200 (Fri, 12 Aug 2011) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE itemtype (
  tenantId INT(11) unsigned NOT NULL,
  name VARCHAR(50) NOT NULL,
  id INT(11) NOT NULL,
  profileSchema MEDIUMBLOB,
  profileMatcher MEDIUMBLOB,
  visible BIT(1) NOT NULL DEFAULT b'1',
  UNIQUE KEY (tenantId, name),
  UNIQUE KEY (tenantId, id)
) COMMENT='Table containing itemtypes';
