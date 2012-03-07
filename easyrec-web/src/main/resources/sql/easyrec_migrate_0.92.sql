-- add backtracking mechanism for storing clicks on recommendations
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

-- add archive table to move actions older than xxx days to archive to keep constant performance
CREATE TABLE actionarchive (
  id int(11) unsigned NOT NULL auto_increment,
  tenantId int(11) NOT NULL,
  userId int(11) default NULL,
  sessionId varchar(50) default NULL,
  ip varchar(45) default NULL,
  itemId int(11) default NULL,
  itemTypeId int(11) NOT NULL,
  actionTypeId int(11) NOT NULL,
  ratingValue int(11) default NULL,
  searchSucceeded tinyint(1) default NULL,
  numberOfFoundItems int(11) default NULL,
  description varchar(250) default NULL,
  actionTime datetime NOT NULL,
  PRIMARY KEY  (id),
  KEY action_reader (tenantId,userId,actionTypeId,itemTypeId),
  KEY tenantId (tenantId,actionTime)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Table containing archived actions';


-- Add login Counter and Date for last login
ALTER TABLE operator ADD lastlogin DATE NULL AFTER accesslevel;
ALTER TABLE operator ADD logincount INT UNSIGNED DEFAULT '0' NULL AFTER lastlogin;

UPDATE sourcetype SET name='ARM' WHERE name='UM';

-- extend sourceinfo for more info
ALTER TABLE itemassoc DROP KEY unique_itemassoc;
ALTER TABLE itemassoc ADD UNIQUE KEY unique_itemassoc (tenantId,itemFromId,itemFromTypeId,itemToId,itemToTypeId,assocTypeId,sourceTypeId);
ALTER TABLE itemassoc MODIFY sourceInfo VARCHAR(250);
-- Action gets index to retrieve charts more quick
ALTER TABLE action ADD INDEX charts (tenantId,actionTypeId,actionTime,itemId,itemTypeId);

-- Tenants get a blob to store tenantstatistics
ALTER TABLE tenant ADD COLUMN tenantStatistic MEDIUMBLOB AFTER tenantConfig;

-- Add a creation date to item for item statistics
ALTER TABLE item ADD COLUMN creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER active;
UPDATE item SET creationdate = NOW();

-- Add a table for versioning easyrec and set actual version number
CREATE TABLE easyrec (
  version float(9,3) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO easyrec (version) VALUES (0.92);

-- Adds a security token used when importing rules or item
ALTER TABLE operator ADD COLUMN token VARCHAR(32) NULL AFTER logincount;
