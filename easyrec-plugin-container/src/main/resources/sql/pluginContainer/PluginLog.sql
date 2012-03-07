###############################################################
# $Author: $
# $Revision: $
# $Date:  $
###############################################################

-- ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE plugin_log (
  id int(11) NOT NULL AUTO_INCREMENT,
  tenantId int(11) unsigned NOT NULL,
  pluginId varchar(500) NOT NULL,
  pluginVersion varchar(50) NOT NULL,
--   need to add default value or else a on update set to today is auto inserted by mysql
  startDate timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  endDate timestamp NULL DEFAULT NULL,
  assocTypeId int(11) NOT NULL,
  configuration text NOT NULL,
  statistics text,
  PRIMARY KEY (id),
--   need to use subset of pluginId otherwise maximum key length would be exhausted
  UNIQUE unique_plugin_log (tenantId,pluginId(255),pluginVersion,assocTypeId,startDate),
  KEY idx_tenantId (tenantId),
  KEY idx_endDate (endDate)
) COMMENT = 'store runs of plugins';