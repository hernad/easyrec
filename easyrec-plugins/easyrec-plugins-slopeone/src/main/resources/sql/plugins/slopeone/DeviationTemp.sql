###############################################################
# $Author$
# $Revision$
# $Date$
###############################################################

# ATTENTION: do not add other sql statements than the CREATE TABLE statement

# 400MB cache allows for ~7.9M rows (~53 byte per row)
SET max_heap_table_size = 400 * 1024 * 1024;

CREATE TABLE so_deviation_temp (
  tenantId INT (11) NOT NULL,
  item1Id INT(11) NOT NULL,
  item2Id INT(11) NOT NULL,
  item1TypeId INT(11) NOT NULL,
  item2TypeId INT(11) NOT NULL,
  numerator DOUBLE NOT NULL,
  denominator BIGINT(11) NOT NULL
)
ENGINE=MEMORY
COMMENT='temp table for loading deviations with LOAD DATA INFILE.';