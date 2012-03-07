###############################################################
# $Author: szavrel $
# $Revision: 18552 $
# $Date: 2011-07-28 17:14:25 +0200 (Do, 28 Jul 2011) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE `so_log` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
	`tenantId` INT(11) UNSIGNED NOT NULL,
	`execution` DATETIME NOT NULL,
	`configuration` MEDIUMTEXT NULL,
	`statistics` MEDIUMTEXT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `unique_log` (`tenantId`, `execution`)
) ENGINE=InnoDb DEFAULT CHARSET=latin1 COMMENT='Logs for Slope One runs.';