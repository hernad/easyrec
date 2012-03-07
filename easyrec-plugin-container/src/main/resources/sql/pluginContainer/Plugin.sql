###############################################################
# $Author: $
# $Revision: $
# $Date:  $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE plugin (
  id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  displayname VARCHAR(150) DEFAULT NULL,
  pluginid VARCHAR(500) NOT NULL,
  version VARCHAR(50) NOT NULL,
  origfilename VARCHAR(150) DEFAULT '',
  state VARCHAR(50) NOT NULL,
  file LONGBLOB,
  changeDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY pluginId (pluginid, version)
) ENGINE=INNODB DEFAULT CHARSET=latin1 COMMENT='Table containing plugins';