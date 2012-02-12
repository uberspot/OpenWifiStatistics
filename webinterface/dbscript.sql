
SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

DROP TABLE IF EXISTS `scan_results`;
		
CREATE TABLE `scan_results` (
  `id` INTEGER NOT NULL AUTO_INCREMENT,
  `bssid` VARCHAR(50) NOT NULL,
  `ssid` VARCHAR(50) NOT NULL,
  `capabilities` VARCHAR(80) NOT NULL,
  `frequency` INTEGER NOT NULL,
  `level` INTEGER NOT NULL,
  `provider` VARCHAR(10) NOT NULL,
  `latitude` DOUBLE NOT NULL,
  `longitude` DOUBLE NOT NULL,
  `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

ALTER TABLE `scan_results` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- INSERT INTO `scan_results` (`id`,`bssid`,`ssid`,`capabilities`,`frequency`,`level`,`provider`,`latitude`,`longitude`,`timestamp`) VALUES
-- ('','','','','','','','','','');

