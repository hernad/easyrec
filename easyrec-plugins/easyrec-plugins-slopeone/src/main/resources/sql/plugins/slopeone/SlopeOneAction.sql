###############################################################
# $Author: szavrel $
# $Revision: 18552 $
# $Date: 2011-07-28 17:14:25 +0200 (Do, 28 Jul 2011) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE `so_action` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
	`tenantId` INT(11) NOT NULL,
	`userId` INT(11) NOT NULL,
	`itemId` INT(11) NOT NULL,
	`itemTypeId` INT(11) NOT NULL,
	`ratingValue` INT(11) NOT NULL,
	`actionTime` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `unique_action` (`tenantId`, `userId`, `itemId`, `itemTypeId`),
	INDEX `rating_reader` (`tenantId`, `itemTypeId`, `actionTime`)
) ENGINE=InnoDb DEFAULT CHARSET=latin1 COMMENT='Actions (usually only ratings) used by Slope One';