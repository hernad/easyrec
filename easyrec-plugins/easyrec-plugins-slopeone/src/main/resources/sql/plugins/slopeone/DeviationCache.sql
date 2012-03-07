###############################################################
# $Author$
# $Revision$
# $Date$
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

# 400MB cache allows for ~7.9M rows (~53 byte per row)
SET max_heap_table_size = 400 * 1024 * 1024;

CREATE TABLE `so_deviation_cache` (
	`tenantId` INT(11) NOT NULL,
	`item1Id` INT(11) NOT NULL,
	`item1TypeId` INT(11) NOT NULL,
	`item2Id` INT(11) NOT NULL,
	`item2TypeId` INT(11) NOT NULL,
	`numerator` DOUBLE NOT NULL DEFAULT '0',
	`denominator` BIGINT(11) NOT NULL DEFAULT '0',
	`written` BIT(1) NOT NULL DEFAULT b'0',
	PRIMARY KEY (`tenantId`, `item1Id`, `item2Id`, `item1TypeId`, `item2TypeId`) USING HASH,
	# written is hash because only equality tests are used
	INDEX `key_written` (`written`) USING HASH #,
	# denom is btree because its used for sorting
	# INDEX `key_denominator` (`denominator`) USING BTREE
)
ENGINE=MEMORY
COMMENT='Cache for so_deviation.';