###############################################################
# $Author: $
# $Revision: $
# $Date:  $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE plugin_configuration (
  id int(11) NOT NULL AUTO_INCREMENT,
  tenantId int(11) NOT NULL,
  assocTypeId int(11) NOT NULL,
  pluginId varchar(500) NOT NULL,
  pluginVersion varchar(50) NOT NULL,
  name varchar(255) NOT NULL,
  configuration text NOT NULL,
  active bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (id),
  UNIQUE KEY unique_configuration (tenantId,assocTypeId,pluginId(250),pluginVersion,name(250)),
  KEY idx_tenantAssoc (tenantId,assocTypeId)
) COMMENT='store plugin configurations for tenants';
