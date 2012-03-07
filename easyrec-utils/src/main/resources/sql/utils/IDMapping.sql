###############################################################
# $Author: pmarschik $ 
# $Revision: 17662 $ 
# $Date: 2011-02-11 11:30:46 +0100 (Fr, 11 Feb 2011) $ 
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE idmapping (
  intId INT(11) unsigned NOT NULL AUTO_INCREMENT,
  stringId VARCHAR(250) NOT NULL DEFAULT '0',
  PRIMARY KEY (intId),
  UNIQUE KEY unique_mapping (stringId)
) ENGINE=MyISAM COMMENT='Table containing id mapping';