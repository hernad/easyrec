###############################################################
# $Author$
# $Revision$
# $Date$
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE ii_itemassoccount (
  id int(11) NOT NULL AUTO_INCREMENT,
  tenantId int(11) NOT NULL,
  itemFromId int(11) NOT NULL,
  itemFromTypeId int(11) unsigned NOT NULL,
  itemToId int(11) NOT NULL,
  itemToTypeId int(11) unsigned NOT NULL,
  count int(11) unsigned NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_similarity (tenantId,itemFromId,itemFromTypeId,itemToId,itemToTypeId),
  KEY idx_item1Tenant (tenantId,itemFromId,itemFromTypeId),
  KEY idx_item2Tenant (tenantId,itemToId,itemToTypeId)
) COMMENT = 'Adds a count to itemassoc';