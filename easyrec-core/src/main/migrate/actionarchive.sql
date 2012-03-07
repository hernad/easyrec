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