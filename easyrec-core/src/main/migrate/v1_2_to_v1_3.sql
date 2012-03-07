###############################################################
# $Author: szavrel $
# $Revision: 14846 $
# $Date: 2009-10-21 16:38:35 +0200 (Mi, 21 Okt 2009) $
###############################################################

-- This script contains changes to the db schema from Rec v1.2 to Rec v1.3 and needs to be
-- applied to an exisiting database to work with the new recommender version

-- adding 'active' field to the tenant table
ALTER TABLE tenant ADD COLUMN active TINYINT(1) NOT NULL DEFAULT '1';
-- allow for longer tenant names
ALTER TABLE tenant MODIFY stringId VARCHAR(100) NOT NULL;