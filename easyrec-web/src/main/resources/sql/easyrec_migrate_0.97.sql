ALTER TABLE item MODIFY description VARCHAR(500) CHARACTER SET utf8;
ALTER TABLE item MODIFY url VARCHAR(500) CHARACTER SET utf8;
ALTER TABLE item MODIFY imageurl VARCHAR(500) CHARACTER SET utf8;

ALTER TABLE idmapping MODIFY stringId VARCHAR(250) CHARACTER SET utf8;

ALTER TABLE action MODIFY description VARCHAR(500) CHARACTER SET utf8;

ALTER TABLE actiontype ADD COLUMN hasvalue BIT(1) NOT NULL DEFAULT b'0';
UPDATE actiontype SET hasvalue=b'1' WHERE NAME="RATE";

-- strange string operations necessary because SqlScriptParser interprets double / as comment
UPDATE plugin_configuration SET pluginVersion="0.97" WHERE SUBSTRING(pluginId,8)="www.easyrec.org/plugins/ARM" AND pluginVersion="0.96";
UPDATE plugin_configuration SET pluginVersion="0.97" WHERE SUBSTRING(pluginId,8)="www.easyrec.org/plugins/slopeone" AND pluginVersion="0.96";

UPDATE plugin_log SET pluginVersion="0.97" WHERE SUBSTRING(pluginId,8)="www.easyrec.org/plugins/ARM" AND pluginVersion="0.96";
UPDATE plugin_log SET pluginVersion="0.97" WHERE SUBSTRING(pluginId,8)="www.easyrec.org/plugins/slopeone" AND pluginVersion="0.96";

UPDATE sourcetype SET NAME=CONCAT(SUBSTRING_INDEX(NAME,"/",5),"/0.97") WHERE SUBSTRING(NAME,8)="www.easyrec.org/plugins/ARM/0.96";
UPDATE sourcetype SET NAME=CONCAT(SUBSTRING_INDEX(NAME,"/",5),"/0.97") WHERE SUBSTRING(NAME,8)="www.easyrec.org/plugins/slopeone/0.96";

TRUNCATE TABLE easyrec;
INSERT INTO easyrec (version) VALUES (0.97); 