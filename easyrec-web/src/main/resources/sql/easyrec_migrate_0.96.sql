
CREATE TABLE plugin_log (
  id INT(11) NOT NULL AUTO_INCREMENT,
  tenantId INT(11) UNSIGNED NOT NULL,
  pluginId VARCHAR(500) NOT NULL,
  pluginVersion VARCHAR(50) NOT NULL,
-- need to add default value or else a on update set to today is auto inserted by mysql
  startDate TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  endDate TIMESTAMP NULL DEFAULT NULL,
  assocTypeId INT(11) NOT NULL,
  configuration TEXT NOT NULL,
  statistics TEXT,
  PRIMARY KEY (`id`),
--  need to use subset of pluginId otherwise maximum key length would be exhausted
  UNIQUE unique_plugin_log (tenantId,pluginId(255),pluginVersion,assocTypeId,startDate),
  KEY idx_tenantId (tenantId),
  KEY idx_endDate (endDate)
) ENGINE=INNODB DEFAULT CHARSET=latin1;

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
) ENGINE=InnoDb DEFAULT CHARSET=latin1 COMMENT='store plugin configurations for tenants';

-- support for IPv6 addresses
ALTER TABLE operator MODIFY ip VARCHAR(39);
ALTER TABLE assoctype ADD COLUMN visible BIT(1) NOT NULL DEFAULT b'1';
ALTER TABLE itemtype ADD COLUMN visible BIT(1) NOT NULL DEFAULT b'1';

ALTER TABLE profile MODIFY profileData text;

DROP TABLE IF EXISTS so_action, so_deviation, so_log;

TRUNCATE TABLE easyrec;
INSERT INTO easyrec (version) VALUES (0.96);