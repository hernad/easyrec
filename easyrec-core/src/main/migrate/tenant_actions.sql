# 07-04-2010

# Action gets index to retrieve charts more quick
alter table action add index charts (tenantId,actionTypeId,actionTime,itemId,itemTypeId);

# Tenants get a blob to store tenantstatistics
alter table tenant  add column tenantStatistic mediumblob after tenantConfig;