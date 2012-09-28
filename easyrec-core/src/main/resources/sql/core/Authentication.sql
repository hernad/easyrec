###############################################################
# $Author: sat-rsa $ 
# $Revision: 6 $ 
# $Date: 2010-02-16 15:53:29 +0100 (Tue, 16 Feb 2010) $ 
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE authentication (
  tenantId INT(11) unsigned NOT NULL,
  domainURL VARCHAR(250) NOT NULL DEFAULT '',
  UNIQUE KEY unique_authentication (tenantId,domainURL)
) COMMENT='Table containing valid access domains for tenants';