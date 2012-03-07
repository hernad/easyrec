UPDATE sourcetype SET name='ARM' WHERE name='UM';

ALTER TABLE tenant ADD COLUMN tenantConfig MEDIUMBLOB;

ALTER TABLE itemassoc DROP KEY unique_itemassoc;
ALTER TABLE itemassoc ADD UNIQUE KEY unique_itemassoc (tenantId,itemFromId,itemFromTypeId,itemToId,itemToTypeId,assocTypeId,sourceTypeId);
ALTER TABLE itemassoc MODIFY sourceInfo VARCHAR(250);