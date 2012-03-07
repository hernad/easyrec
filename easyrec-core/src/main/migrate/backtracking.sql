CREATE TABLE backtracking (
	userId INT(10) UNSIGNED NOT NULL DEFAULT '0',
	tenantId INT(10) UNSIGNED NOT NULL,
	itemFromId INT(10) UNSIGNED NOT NULL,
	itemToId INT(10) UNSIGNED NOT NULL,
	assocType INT(10) UNSIGNED NOT NULL,
	timestamp DATETIME NOT NULL,	
	INDEX assoc (itemFromId, tenantId, assocType, itemToId)
)
COMMENT='Backtracking information about recommendations'
ENGINE=MyISAM
ROW_FORMAT=DEFAULT;