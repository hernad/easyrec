###############################################################
# $Author: sat-rsa $
# $Revision: 6 $
# $Date: 2010-02-16 15:53:29 +0100 (Tue, 16 Feb 2010) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE recommendeditem (
  id INT(11) unsigned NOT NULL AUTO_INCREMENT,
  itemId INT(11) NOT NULL,
  itemTypeId INT(11) NOT NULL,
  recommendationId INT(11) NOT NULL,
  predictionValue DOUBLE NOT NULL DEFAULT '0',
  itemAssocId INT(11),
  explanation VARCHAR(255),
  PRIMARY KEY (id),
  UNIQUE KEY unique_recommended_item (itemId, itemTypeId, recommendationId)
) COMMENT='Table containing all recommended items (ever)';
