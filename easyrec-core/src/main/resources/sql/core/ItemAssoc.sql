###############################################################
# $Author: sat-rsa $
# $Revision: 28 $
# $Date: 2010-05-11 17:48:58 +0200 (Di, 11 Mai 2010) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE itemassoc (
  id INT(11) unsigned NOT NULL AUTO_INCREMENT,
  tenantId INT(11) NOT NULL DEFAULT '0',
  itemFromId INT(11) NOT NULL DEFAULT '0',
  itemFromTypeId INT(11) unsigned NOT NULL DEFAULT '0',
  assocTypeId INT(11) unsigned NOT NULL DEFAULT '0',
  assocValue DOUBLE NOT NULL DEFAULT '0',
  itemToId INT(11) NOT NULL DEFAULT '0',
  itemToTypeId INT(11) unsigned NOT NULL DEFAULT '0',
  sourceTypeId INT(11) NOT NULL DEFAULT '0',
  sourceInfo VARCHAR(250) DEFAULT '0',
  viewTypeId INT(11) unsigned NOT NULL DEFAULT '0',
  active TINYINT(1) NOT NULL DEFAULT '1',
  changeDate DATETIME NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_itemassoc (tenantId, itemFromId, itemFromTypeId, itemToId, itemToTypeId, assocTypeId, sourceTypeId),
  KEY idFrom_assoc (itemFromId, itemFromTypeId, assocTypeId, tenantId),
  KEY recommender (itemFromId,itemFromTypeId,itemToTypeId,assocTypeId,tenantId,active)
) COMMENT='Table containing item associations';
