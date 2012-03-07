###############################################################
# $Author: szavrel $
# $Revision: 14846 $
# $Date: 2009-10-21 16:38:35 +0200 (Mi, 21 Okt 2009) $
###############################################################

-- This script contains changes to the db schema from Rec v1.3 to Rec v1.4 and needs to be
-- applied to an exisiting database to work with the new recommender version

-- add profile table to database
CREATE TABLE profile (
  profileId INT(11) unsigned NOT NULL AUTO_INCREMENT,
  tenantId INT(11) NOT NULL,
  itemId INT(11)NOT NULL,
  itemTypeId INT(11) NOT NULL,
  profileData MEDIUMBLOB,
  changeDate TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (profileId),
  UNIQUE KEY (tenantId, itemId, itemTypeId)
) COMMENT='Table containing item profiles';

-- add columns for profile XMLSchema and matcher XSLTs to itemType table
ALTER TABLE itemtype ADD COLUMN profileSchema MEDIUMBLOB;
ALTER TABLE itemtype ADD COLUMN profileMatcher MEDIUMBLOB;

-- The Magic Keys: 
-- Enhance Database Performance for sending actions 
-- and getting recommendations.
ALTER TABLE Action ADD KEY action_reader (tenantId,userId,actionTypeId,itemTypeId); 
ALTER TABLE itemassoc ADD KEY recommender (itemFromId,itemFromTypeId,itemToTypeId,assocTypeId,tenantId,active);
