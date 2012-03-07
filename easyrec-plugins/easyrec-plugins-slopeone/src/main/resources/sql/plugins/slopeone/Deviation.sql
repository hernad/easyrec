###############################################################
# $Author: szavrel $
# $Revision: 18552 $
# $Date: 2011-07-28 17:14:25 +0200 (Do, 28 Jul 2011) $
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

CREATE TABLE `so_deviation` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
	`tenantId` INT(11) NOT NULL,
	`item1Id` INT(11) NOT NULL,
	`item1TypeId` INT(11) NOT NULL,
	`item2Id` INT(11) NOT NULL,
	`item2TypeId` INT(11) NOT NULL,
	`numerator` DOUBLE NOT NULL DEFAULT 0,
	`denominator` BIGINT(11) NOT NULL DEFAULT 0,
    `deviation` DOUBLE NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`),
    UNIQUE INDEX `unique_deviation` (`tenantId`, `item1TypeId`, `item2TypeId`, `item1Id`, `item2Id`),
	#INDEX `sort_deviation` (`deviation`),
	INDEX `key_deviation` (`tenantId`, `item1TypeId`, `item1Id`, `deviation`),
	# the reverse key is needed for generating the non-personalized recommendations because
	# the query selects item2Id -> without the index query time is 300+s, with the index 1s
	INDEX `key_deviation_reverse` (`tenantId`, `item2TypeId`, `item2Id`, `deviation`)
) ENGINE=InnoDb DEFAULT CHARSET=latin1 COMMENT='Contains data needed to calculate the average deviations.';