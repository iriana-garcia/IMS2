# SQL Manager Lite for MySQL 5.5.3.46192
# ---------------------------------------
# Host     : 10.1.10.169
# Port     : 3306
# Database : ibo-ims


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE `ibo-ims`
    CHARACTER SET 'utf8'
    COLLATE 'utf8_general_ci';

USE `ibo-ims`;

#
# Structure for the `user_type` table :
#

CREATE TABLE `user_type` (
  `typ_id` CHAR(1) COLLATE utf8_general_ci NOT NULL,
  `typ_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`typ_id`) USING BTREE,
  UNIQUE KEY `typ_name` (`typ_name`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `groups` table :
#

CREATE TABLE `groups` (
  `grp_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `grp_state` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '1',
  `grp_created_date` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `grp_update_date` TIMESTAMP(6) NULL DEFAULT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `use_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `grp_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`grp_id`) USING BTREE,
  UNIQUE KEY `grp_name` (`grp_name`) USING BTREE,
  UNIQUE KEY `use_id_2` (`use_id`) USING BTREE,
  KEY `groups_fk1` (`use_id_created`) USING BTREE,
  KEY `use_id` (`use_id`) USING BTREE,
  KEY `groups_fk2` (`use_id_updated`) USING BTREE,
  CONSTRAINT `groups_fk1` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `groups_fk2` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`),
  CONSTRAINT `groups_fk3` FOREIGN KEY (`use_id`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `roles` table :
#

CREATE TABLE `roles` (
  `rol_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `rol_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `rol_created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `rol_update_date` TIMESTAMP NULL DEFAULT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `rol_description` VARCHAR(150) COLLATE utf8_general_ci DEFAULT NULL,
  `rol_state` CHAR(1) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`rol_id`) USING BTREE,
  UNIQUE KEY `rol_name` (`rol_name`) USING BTREE,
  KEY `FKroles478874` (`use_id_created`) USING BTREE,
  KEY `FKroles492862` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKroles492862` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`) ON DELETE SET NULL,
  CONSTRAINT `roles_fk1` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `users` table :
#

CREATE TABLE `users` (
  `use_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `use_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `use_first_name` VARCHAR(100) COLLATE utf8_general_ci NOT NULL,
  `use_middle_name` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `use_last_name` VARCHAR(100) COLLATE utf8_general_ci NOT NULL,
  `use_email` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `use_state` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '1',
  `use_created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `use_update_date` TIMESTAMP NULL DEFAULT NULL,
  `use_last_login` TIMESTAMP NULL DEFAULT NULL,
  `rol_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `use_phone` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `use_super` TINYINT(1) DEFAULT NULL,
  `grp_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `use_password` VARCHAR(255) COLLATE utf8_general_ci DEFAULT NULL,
  `typ_id` CHAR(1) COLLATE utf8_general_ci NOT NULL,
  `use_need_update` TINYINT(1) NOT NULL COMMENT 'Save if the user need to be updated in oracle',
  `use_date_state` DATETIME(6) DEFAULT NULL COMMENT 'Save the last date user was active or inactive',
  PRIMARY KEY (`use_id`) USING BTREE,
  UNIQUE KEY `use_name` (`use_name`) USING BTREE,
  UNIQUE KEY `use_email` (`use_email`) USING BTREE,
  KEY `FKusers570115` (`rol_id`) USING BTREE,
  KEY `FKusers362280` (`use_id_created`) USING BTREE,
  KEY `FKusers376268` (`use_id_updated`) USING BTREE,
  KEY `FKusers181884` (`grp_id`) USING BTREE,
  KEY `FK5userTypes` (`typ_id`) USING BTREE,
  CONSTRAINT `FK5userTypes` FOREIGN KEY (`typ_id`) REFERENCES `user_type` (`typ_id`),
  CONSTRAINT `FKusers181884` FOREIGN KEY (`grp_id`) REFERENCES `groups` (`grp_id`),
  CONSTRAINT `FKusers362280` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKusers376268` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`) ON DELETE SET NULL,
  CONSTRAINT `FKusers570115` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`rol_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `corporation` table :
#

CREATE TABLE `corporation` (
  `cor_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `cor_name` VARCHAR(150) COLLATE utf8_general_ci NOT NULL,
  `cor_supplier_number` VARCHAR(30) COLLATE utf8_general_ci DEFAULT NULL,
  `cor_need_update` TINYINT(1) NOT NULL,
  `cor_created_date` TIMESTAMP(6) NOT NULL DEFAULT '0000-00-00 00:00:00.000000',
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `cor_update_date` TIMESTAMP(6) NULL DEFAULT '0000-00-00 00:00:00.000000',
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `cor_ein` CHAR(9) COLLATE utf8_general_ci NOT NULL,
  `cor_type` ENUM('DOMESTIC','INTERNATIONAL') COLLATE utf8_general_ci NOT NULL DEFAULT 'DOMESTIC',
  `cor_pay_method` ENUM('NONE','PAYPAL','DIRECT_DEPOSIT') COLLATE utf8_general_ci NOT NULL DEFAULT 'DIRECT_DEPOSIT',
  PRIMARY KEY (`cor_id`) USING BTREE,
  UNIQUE KEY `pro_corporation_name` (`cor_name`) USING BTREE,
  UNIQUE KEY `pro_ein` (`cor_ein`) USING BTREE,
  UNIQUE KEY `pro_supplier_number` (`cor_supplier_number`) USING BTREE,
  KEY `use_id_created` (`use_id_created`) USING BTREE,
  KEY `use_id_updated` (`use_id_updated`) USING BTREE,
  CONSTRAINT `corporation_fk1` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `corporation_fk2` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `country` table :
#

CREATE TABLE `country` (
  `cou_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `cou_name` VARCHAR(100) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`cou_id`) USING BTREE,
  UNIQUE KEY `cou_name` (`cou_name`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `states` table :
#

CREATE TABLE `states` (
  `sta_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `sta_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `cou_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `sta_abbreviation` CHAR(2) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`sta_id`) USING BTREE,
  UNIQUE KEY `sta_name` (`sta_name`) USING BTREE,
  KEY `FKstates137325` (`cou_id`) USING BTREE,
  CONSTRAINT `FKstates137325` FOREIGN KEY (`cou_id`) REFERENCES `country` (`cou_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `address` table :
#

CREATE TABLE `address` (
  `add_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `add_type` CHAR(1) COLLATE utf8_general_ci NOT NULL,
  `add_description` VARCHAR(255) COLLATE utf8_general_ci NOT NULL,
  `add_zip_code` VARCHAR(10) COLLATE utf8_general_ci NOT NULL,
  `add_city` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `cor_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `cou_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `sta_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `add_state` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `add_created_date` TIMESTAMP(6) NULL DEFAULT NULL,
  `add_update_date` TIMESTAMP(6) NULL DEFAULT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `add_need_update` TINYINT(1) NOT NULL COMMENT 'Save if the user need to be updated in oracle',
  PRIMARY KEY (`add_id`) USING BTREE,
  KEY `FKaddress754320` (`cor_id`) USING BTREE,
  KEY `FKaddress927071` (`cou_id`) USING BTREE,
  KEY `FKaddress789439` (`sta_id`) USING BTREE,
  KEY `address_fk1` (`use_id_created`) USING BTREE,
  KEY `address_fk2` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKaddress754320` FOREIGN KEY (`cor_id`) REFERENCES `corporation` (`cor_id`),
  CONSTRAINT `FKaddress789439` FOREIGN KEY (`sta_id`) REFERENCES `states` (`sta_id`),
  CONSTRAINT `FKaddress927071` FOREIGN KEY (`cou_id`) REFERENCES `country` (`cou_id`),
  CONSTRAINT `address_fk1` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `address_fk2` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `agent_state_detail` table :
#

CREATE TABLE `agent_state_detail` (
  `asd_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `use_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `asd_date_start` DATETIME(3) NOT NULL,
  `asd_date_end` DATETIME(3) NOT NULL,
  `asd_event_type` INTEGER(1) NOT NULL,
  `asd_reason_code` INTEGER(11) DEFAULT NULL,
  `asd_duration` INTEGER(11) NOT NULL COMMENT 'save the data in miliseconds',
  `asd_place` SMALLINT(6) DEFAULT NULL,
  `asd_need_updated` TINYINT(1) NOT NULL,
  `iwo_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL COMMENT 'save to what invoice is associate',
  `asd_duration_pending` INTEGER(11) DEFAULT NULL,
  `iwo_id_pending` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL COMMENT 'save the invoice work that take the time pending',
  `asd_reason_code_description` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`asd_id`) USING BTREE,
  KEY `pro_id` (`use_id`) USING BTREE,
  KEY `agent_state_detail_idx2` (`use_id`) USING BTREE,
  KEY `agent_state_detail_idx1` (`asd_date_start`, `asd_date_end`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `agent_states` table :
#

CREATE TABLE `agent_states` (
  `as_id` INTEGER(11) NOT NULL AUTO_INCREMENT,
  `as_place` TINYINT(4) NOT NULL DEFAULT 1,
  `as_code` INTEGER(11) NOT NULL,
  `as_description` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`as_id`) USING BTREE,
  UNIQUE KEY `agent_states_idx1` (`as_place`, `as_code`) USING BTREE
) ENGINE=InnoDB
AUTO_INCREMENT=126 CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `bank` table :
#

CREATE TABLE `bank` (
  `bank_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `bank_name` VARCHAR(100) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`bank_id`) USING BTREE,
  UNIQUE KEY `bank_name` (`bank_name`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `bank_information` table :
#

CREATE TABLE `bank_information` (
  `cor_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ban_name` VARCHAR(255) COLLATE utf8_general_ci DEFAULT NULL,
  `ban_number` VARCHAR(255) COLLATE utf8_general_ci NOT NULL,
  `ban_routing` VARCHAR(255) COLLATE utf8_general_ci NOT NULL,
  `ban_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ban_need_update` TINYINT(1) NOT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `ban_created_date` TIMESTAMP(6) NULL DEFAULT '0000-00-00 00:00:00.000000',
  `ban_update_date` TIMESTAMP(6) NULL DEFAULT '0000-00-00 00:00:00.000000',
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`ban_id`) USING BTREE,
  UNIQUE KEY `pro_id` (`cor_id`) USING BTREE,
  KEY `FKbank_infor294310` (`cor_id`) USING BTREE,
  KEY `use_id_created` (`use_id_created`) USING BTREE,
  KEY `use_id_updated` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKbank_infor294310` FOREIGN KEY (`cor_id`) REFERENCES `corporation` (`cor_id`),
  CONSTRAINT `bank_information_fk3` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `bank_information_fk4` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `category` table :
#

CREATE TABLE `category` (
  `cat_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `cat_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `cat_state` TINYINT(1) NOT NULL,
  `cat_created_date` TIMESTAMP(6) NOT NULL DEFAULT '0000-00-00 00:00:00.000000',
  `cat_update_date` TIMESTAMP(6) NULL DEFAULT '0000-00-00 00:00:00.000000',
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL DEFAULT '1',
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`cat_id`) USING BTREE,
  UNIQUE KEY `cat_description` (`cat_name`) USING BTREE,
  KEY `use_id_created` (`use_id_created`) USING BTREE,
  KEY `use_id_updated` (`use_id_updated`) USING BTREE,
  CONSTRAINT `category_fk1` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `category_fk2` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`) ON DELETE SET NULL
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `client_applications` table :
#

CREATE TABLE `client_applications` (
  `cli_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `cli_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `cli_state` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '1',
  `cli_created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `cli_update_date` TIMESTAMP NULL DEFAULT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `cli_amount` DECIMAL(19,2) NOT NULL,
  PRIMARY KEY (`cli_id`) USING BTREE,
  UNIQUE KEY `cli_name` (`cli_name`) USING BTREE,
  KEY `FKclient_app493747` (`use_id_created`) USING BTREE,
  KEY `FKclient_app491831` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKclient_app491831` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKclient_app493747` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `configuration_email` table :
#

CREATE TABLE `configuration_email` (
  `ema_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ema_type` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '1',
  `ema_subject` VARCHAR(150) COLLATE utf8_general_ci NOT NULL,
  `ema_bcc` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `ema_html` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '1',
  `ema_content` MEDIUMTEXT COLLATE utf8_general_ci,
  `ema_to` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `ema_update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`ema_id`) USING BTREE,
  KEY `FKconfigurat300398` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKconfigurat300398` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`) ON DELETE SET NULL
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `configuration_general` table :
#

CREATE TABLE `configuration_general` (
  `con_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `con_ldap_authenti` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ldap_server` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ldap_port` VARCHAR(5) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ldap_dn` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ldap_dn_ibo` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ldap_user` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ldap_pass` VARCHAR(255) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ldap_ssl` CHAR(1) COLLATE utf8_general_ci DEFAULT '0',
  `con_ldap_sec_type` VARCHAR(15) COLLATE utf8_general_ci DEFAULT NULL,
  `con_last_pipkins` DATE DEFAULT NULL,
  `con_update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `con_path_pipkins` VARCHAR(200) COLLATE utf8_general_ci DEFAULT NULL,
  `con_path_oracle` VARCHAR(200) COLLATE utf8_general_ci DEFAULT NULL,
  `con_retention` INTEGER(10) DEFAULT NULL,
  `con_email_host` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `con_email_port` INTEGER(10) DEFAULT NULL,
  `con_email_protocol` VARCHAR(10) COLLATE utf8_general_ci DEFAULT NULL,
  `con_email_from` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `con_email_auth` CHAR(1) COLLATE utf8_general_ci DEFAULT '0',
  `con_email_max_size` INTEGER(10) DEFAULT NULL,
  `con_ldap_dn_sme` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `con_email_system` VARCHAR(300) COLLATE utf8_general_ci DEFAULT NULL COMMENT 'save the url from the system, use in welcome email',
  `con_email_user` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `con_email_pass` VARCHAR(255) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ftp_port` VARCHAR(10) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ftp_user` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ftp_password` VARCHAR(255) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ibo_intern_like_dome` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'This means that international IBO has a behavior different in system, but\r\n\t * if tomorrow is the same only need to change this value',
  `con_webservice` VARCHAR(300) COLLATE utf8_general_ci DEFAULT NULL,
  `con_ldap_dn_idc` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL COMMENT 'save the OU of International Independent Contract',
  `con_ldap_dn_inter` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`con_id`) USING BTREE,
  KEY `FKconfigurat435359` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKconfigurat435359` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`) ON DELETE SET NULL
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `configuration_system` table :
#

CREATE TABLE `configuration_system` (
  `sys_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `sys_admin_fee` DECIMAL(19,6) NOT NULL,
  `sys_type_year` CHAR(1) COLLATE utf8_general_ci DEFAULT NULL,
  `sys_begin_month` INTEGER(10) DEFAULT NULL,
  `sys_invoice_frecuency` CHAR(1) COLLATE utf8_general_ci DEFAULT NULL,
  `sys_day_pay` CHAR(1) COLLATE utf8_general_ci DEFAULT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `sys_update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sys_buffer` INTEGER(11) NOT NULL COMMENT 'in minutes',
  `sys_color` CHAR(6) COLLATE utf8_general_ci DEFAULT NULL,
  `sys_font_color` CHAR(6) COLLATE utf8_general_ci DEFAULT NULL,
  `sys_decimal_place` INTEGER(11) NOT NULL DEFAULT 2,
  `sys_date_start` TIMESTAMP(6) NOT NULL DEFAULT '0000-00-00 00:00:00.000000' COMMENT 'Date start to enabled submit invoice to IBO',
  `sys_date_end` TIMESTAMP(6) NOT NULL DEFAULT '0000-00-00 00:00:00.000000' COMMENT 'Date end to enabled submit invoice to IBO',
  `sys_total_submit` INTEGER(11) NOT NULL DEFAULT 2,
  `sys_paypal_file_limit` DECIMAL(19,2) DEFAULT NULL COMMENT 'Save the maximum per file that the PayPal accept',
  PRIMARY KEY (`sys_id`) USING BTREE,
  KEY `FKconfigurat575895` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKconfigurat575895` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`) ON DELETE SET NULL
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `event` table :
#

CREATE TABLE `event` (
  `eve_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `eve_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `cli_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `eve_id_pipkins` VARCHAR(36) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`eve_id`) USING BTREE,
  UNIQUE KEY `eve_name` (`eve_name`) USING BTREE,
  KEY `cli_id` (`cli_id`) USING BTREE,
  CONSTRAINT `event_fk1` FOREIGN KEY (`cli_id`) REFERENCES `client_applications` (`cli_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `ibo_state` table :
#

CREATE TABLE `ibo_state` (
  `ist_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ist_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`ist_id`) USING BTREE,
  UNIQUE KEY `ist_name` (`ist_name`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `ibo_temp` table :
#

CREATE TABLE `ibo_temp` (
  `ite_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ite_user_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `ite_first_name` VARCHAR(100) COLLATE utf8_general_ci NOT NULL,
  `ite_last_name` VARCHAR(100) COLLATE utf8_general_ci NOT NULL,
  `ite_state` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '1',
  `ite_update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `ite_created_date` TIMESTAMP NULL DEFAULT NULL,
  `ite_email` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `ite_middle_name` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `ite_type_contract` ENUM('DOMESTIC','INTERNATIONAL','INT_INDEP_CONTRACT') COLLATE utf8_general_ci NOT NULL DEFAULT 'DOMESTIC',
  PRIMARY KEY (`ite_id`) USING BTREE,
  UNIQUE KEY `ite_user_name` (`ite_user_name`) USING BTREE,
  KEY `FKibo_temp538580` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKibo_temp538580` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `ibo_type` table :
#

CREATE TABLE `ibo_type` (
  `ity_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ity_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`ity_id`) USING BTREE,
  UNIQUE KEY `ity_name` (`ity_name`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `profesional_skills` table :
#

CREATE TABLE `profesional_skills` (
  `prs_id` INTEGER(11) NOT NULL AUTO_INCREMENT,
  `prs_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`prs_id`) USING BTREE,
  UNIQUE KEY `prs_name` (`prs_name`) USING BTREE
) ENGINE=InnoDB
AUTO_INCREMENT=1 CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `profile` table :
#

CREATE TABLE `profile` (
  `use_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `grp_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `ity_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_region` CHAR(1) COLLATE utf8_general_ci DEFAULT 'D',
  `ist_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `pro_update_date` TIMESTAMP NULL DEFAULT '0000-00-00 00:00:00',
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_amount` DECIMAL(19,2) DEFAULT NULL,
  `pro_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `pro_created_date` TIMESTAMP(6) NULL DEFAULT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `pro_need_update` TINYINT(1) NOT NULL COMMENT 'Save if the user need to be updated in oracle',
  `pro_number` VARCHAR(20) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_total_submit` INTEGER(11) NOT NULL DEFAULT 0,
  `cor_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_princ_title` VARCHAR(150) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_owner_type` ENUM('PRINCIPAL','SECONDARY') COLLATE utf8_general_ci DEFAULT NULL,
  `cou_id` VARCHAR(36) COLLATE utf8_general_ci NOT NULL DEFAULT '52f03556-162b-4963-8ea1-62fe2c373ede',
  `pro_birthday` DATE DEFAULT NULL,
  `pro_gender` ENUM('FEMALE','MALE') COLLATE utf8_general_ci DEFAULT NULL,
  `prs_id` INTEGER(11) DEFAULT NULL,
  `pro_pay_method` ENUM('NONE','PAYPAL','DIRECT_DEPOSIT') COLLATE utf8_general_ci NOT NULL DEFAULT 'DIRECT_DEPOSIT',
  `pro_pay_type` ENUM('USER','CORPORATION','NONE') COLLATE utf8_general_ci NOT NULL DEFAULT 'CORPORATION',
  `pro_type_contract` ENUM('DOMESTIC','INTERNATIONAL','INT_INDEP_CONTRACT') COLLATE utf8_general_ci NOT NULL DEFAULT 'DOMESTIC',
  PRIMARY KEY (`pro_id`) USING BTREE,
  UNIQUE KEY `use_id` (`use_id`) USING BTREE,
  UNIQUE KEY `pro_number` (`pro_number`) USING BTREE,
  KEY `FKprofile945334` (`use_id`) USING BTREE,
  KEY `FKprofile177080` (`grp_id`) USING BTREE,
  KEY `FKprofile697747` (`ity_id`) USING BTREE,
  KEY `FKprofile978795` (`ist_id`) USING BTREE,
  KEY `FKprofile371464` (`use_id_updated`) USING BTREE,
  KEY `use_id_created` (`use_id_created`) USING BTREE,
  KEY `cor_id` (`cor_id`) USING BTREE,
  KEY `cou_id` (`cou_id`) USING BTREE,
  KEY `prs_id` (`prs_id`) USING BTREE,
  CONSTRAINT `FK_profile_corporation` FOREIGN KEY (`cor_id`) REFERENCES `corporation` (`cor_id`),
  CONSTRAINT `FKprofile177080` FOREIGN KEY (`grp_id`) REFERENCES `groups` (`grp_id`),
  CONSTRAINT `FKprofile371464` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKprofile697747` FOREIGN KEY (`ity_id`) REFERENCES `ibo_type` (`ity_id`),
  CONSTRAINT `FKprofile945334` FOREIGN KEY (`use_id`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKprofile978795` FOREIGN KEY (`ist_id`) REFERENCES `ibo_state` (`ist_id`),
  CONSTRAINT `profile_fk1` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `profile_fk2` FOREIGN KEY (`cor_id`) REFERENCES `corporation` (`cor_id`),
  CONSTRAINT `profile_fk3` FOREIGN KEY (`cou_id`) REFERENCES `country` (`cou_id`),
  CONSTRAINT `profile_fk4` FOREIGN KEY (`prs_id`) REFERENCES `profesional_skills` (`prs_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `ibos_client_applications` table :
#

CREATE TABLE `ibos_client_applications` (
  `icl_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `pro_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `cli_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`icl_id`) USING BTREE,
  UNIQUE KEY `ibos_client_applications_idx1` (`pro_id`, `cli_id`) USING BTREE,
  KEY `FKibos_clien207765` (`pro_id`) USING BTREE,
  KEY `FKibos_clien984903` (`cli_id`) USING BTREE,
  CONSTRAINT `FKibos_clien207765` FOREIGN KEY (`pro_id`) REFERENCES `profile` (`pro_id`),
  CONSTRAINT `FKibos_clien984903` FOREIGN KEY (`cli_id`) REFERENCES `client_applications` (`cli_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `invoice_state` table :
#

CREATE TABLE `invoice_state` (
  `ivt_id` CHAR(1) COLLATE utf8_general_ci NOT NULL,
  `ivt_name` VARCHAR(30) COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`ivt_id`) USING BTREE,
  UNIQUE KEY `ivt_name` (`ivt_name`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_swedish_ci'
;

#
# Structure for the `invoice` table :
#

CREATE TABLE `invoice` (
  `inv_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `inv_question` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '0',
  `inv_note` VARCHAR(500) COLLATE utf8_general_ci DEFAULT NULL,
  `inv_pay_processed` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '0',
  `inv_date_processed` TIMESTAMP NULL DEFAULT '0000-00-00 00:00:00',
  `pro_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `inv_start` TIMESTAMP NULL DEFAULT NULL,
  `inv_end` TIMESTAMP NULL DEFAULT NULL,
  `ivt_id` CHAR(1) COLLATE utf8_general_ci NOT NULL,
  `inv_date_submitted` TIMESTAMP NULL DEFAULT NULL,
  `inv_date_approval` TIMESTAMP NULL DEFAULT NULL,
  `inv_update_date` TIMESTAMP NULL DEFAULT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `inv_admin_fee` DECIMAL(11,6) DEFAULT NULL,
  `inv_actual_service` DECIMAL(19,6) DEFAULT NULL,
  `inv_import_total` DECIMAL(19,6) DEFAULT NULL,
  `inv_number` CHAR(50) COLLATE utf8_general_ci DEFAULT NULL COMMENT 'Invoice Number',
  `inv_pay_date` DATE DEFAULT NULL,
  `inv_hours_added` DECIMAL(19,6) NOT NULL DEFAULT 0.000000,
  `inv_total_incentive` DECIMAL(19,6) NOT NULL DEFAULT 0.000000,
  `inv_service_revenue` DECIMAL(19,6) NOT NULL DEFAULT 0.000000,
  PRIMARY KEY (`inv_id`) USING BTREE,
  UNIQUE KEY `invoice_idx4` (`pro_id`, `inv_start`, `inv_end`) USING BTREE,
  KEY `FKinvoice323161` (`pro_id`) USING BTREE,
  KEY `FKinvoice973148` (`use_id_updated`) USING BTREE,
  KEY `ivt_id` (`ivt_id`) USING BTREE,
  KEY `invoice_idx1` (`inv_start`) USING BTREE,
  KEY `invoice_idx2` (`inv_date_submitted`) USING BTREE,
  KEY `invoice_idx3` (`inv_date_approval`) USING BTREE,
  CONSTRAINT `FKinvoice323161` FOREIGN KEY (`pro_id`) REFERENCES `profile` (`pro_id`),
  CONSTRAINT `FKinvoice973148` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`),
  CONSTRAINT `invoice_fk1` FOREIGN KEY (`ivt_id`) REFERENCES `invoice_state` (`ivt_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `skill` table :
#

CREATE TABLE `skill` (
  `ski_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ski_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `cli_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `ski_id_pip` VARCHAR(20) COLLATE utf8_general_ci DEFAULT NULL,
  `ski_state` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`ski_id`) USING BTREE,
  UNIQUE KEY `ski_name` (`ski_name`) USING BTREE,
  KEY `FKskill_pipk300011` (`cli_id`) USING BTREE,
  CONSTRAINT `FKskill_pipk300011` FOREIGN KEY (`cli_id`) REFERENCES `client_applications` (`cli_id`) ON DELETE SET NULL
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `incentive` table :
#

CREATE TABLE `incentive` (
  `inc_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `inc_amount` DECIMAL(11,6) NOT NULL,
  `inc_date` DATE NOT NULL,
  `ski_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `inc_description` VARCHAR(250) COLLATE utf8_general_ci DEFAULT NULL,
  `inv_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `inc_type` CHAR(1) COLLATE utf8_general_ci DEFAULT 'I',
  `use_id_created` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `inc_created_date` TIMESTAMP(6) NULL DEFAULT '0000-00-00 00:00:00.000000',
  `inc_update_date` TIMESTAMP(6) NULL DEFAULT '0000-00-00 00:00:00.000000',
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`inc_id`) USING BTREE,
  KEY `ski_id` (`ski_id`) USING BTREE,
  KEY `inv_id` (`inv_id`) USING BTREE,
  KEY `use_id_created` (`use_id_created`) USING BTREE,
  KEY `use_id_updated` (`use_id_updated`) USING BTREE,
  CONSTRAINT `incentive_fk1` FOREIGN KEY (`inv_id`) REFERENCES `invoice` (`inv_id`),
  CONSTRAINT `incentive_fk2` FOREIGN KEY (`ski_id`) REFERENCES `skill` (`ski_id`),
  CONSTRAINT `incentive_fk3` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `incentive_fk4` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `invoice_history` table :
#

CREATE TABLE `invoice_history` (
  `ihi_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ibo_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ihi_import_total` DECIMAL(19,6) NOT NULL,
  `ihi_update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ihi_created_date` TIMESTAMP NULL DEFAULT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`ihi_id`) USING BTREE,
  KEY `FKinvoice_hi819804` (`ibo_id`) USING BTREE,
  KEY `FKinvoice_hi107947` (`use_id_created`) USING BTREE,
  KEY `FKinvoice_hi121935` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKinvoice_hi107947` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKinvoice_hi121935` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKinvoice_hi819804` FOREIGN KEY (`ibo_id`) REFERENCES `profile` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `invoice_work` table :
#

CREATE TABLE `invoice_work` (
  `iwo_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `inv_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL COMMENT 'Can be null in case exists work after sunday midnight and the invoice do not exists',
  `iwo_update_date` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `cli_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `cli_name` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `iwo_amount` DECIMAL(19,6) DEFAULT NULL,
  `skill_id` CHAR(36) COLLATE utf8_general_ci NOT NULL COMMENT 'save the event from pipkins',
  `skill_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL DEFAULT 'save the event from pipkins',
  `iwo_sch_start_time` TIMESTAMP NULL DEFAULT NULL,
  `iwo_sch_end_time` TIMESTAMP NULL DEFAULT NULL,
  `iwo_total_not_ready_time` DECIMAL(19,6) DEFAULT NULL COMMENT 'Save in seconds',
  `iwo_hours_add` DECIMAL(19,6) DEFAULT NULL,
  `iwo_actual_service` DECIMAL(19,6) DEFAULT NULL,
  `iwo_service_revenue` DECIMAL(19,6) DEFAULT NULL,
  `iwo_hours_buffer` DOUBLE(15,3) DEFAULT NULL COMMENT 'NO USED save hours in actual service by buffer',
  `iwo_buffer` INTEGER(11) DEFAULT NULL COMMENT 'save buffer used (minutes)',
  `iwo_total_ready_time` DECIMAL(19,6) DEFAULT NULL COMMENT 'NO USED Save in hours',
  `use_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `iwo_total_not_ready_time_mili` INTEGER(11) DEFAULT NULL,
  `iwo_actual_service_mili` INTEGER(11) DEFAULT NULL,
  `iwo_total_hours` DECIMAL(19,6) DEFAULT NULL COMMENT 'NO USED TEST',
  `iwo_import_total` DECIMAL(19,6) DEFAULT NULL COMMENT 'NO USED TEST',
  PRIMARY KEY (`iwo_id`) USING BTREE,
  KEY `FKinvoice_wo353699` (`inv_id`) USING BTREE,
  KEY `FKinvoice_wo336438` (`use_id_updated`) USING BTREE,
  KEY `use_id` (`use_id`) USING BTREE,
  CONSTRAINT `FKinvoice_wo336438` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKinvoice_wo353699` FOREIGN KEY (`inv_id`) REFERENCES `invoice` (`inv_id`),
  CONSTRAINT `invoice_work_fk1` FOREIGN KEY (`use_id`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `invoice_hours_added` table :
#

CREATE TABLE `invoice_hours_added` (
  `iho_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `iho_created_date` TIMESTAMP(6) NOT NULL DEFAULT '0000-00-00 00:00:00.000000',
  `iho_description` VARCHAR(250) COLLATE utf8_general_ci DEFAULT NULL,
  `iho_hours` DECIMAL(11,6) UNSIGNED ZEROFILL NOT NULL DEFAULT 00000.000000,
  `cat_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `iwo_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `iho_update_date` TIMESTAMP(6) NULL DEFAULT '0000-00-00 00:00:00.000000',
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`iho_id`) USING BTREE,
  KEY `cat_id` (`cat_id`) USING BTREE,
  KEY `iwo_id` (`iwo_id`) USING BTREE,
  KEY `use_id_created` (`use_id_created`) USING BTREE,
  KEY `invoice_hours_added_fk4` (`use_id_updated`) USING BTREE,
  CONSTRAINT `invoice_hours_added_fk1` FOREIGN KEY (`cat_id`) REFERENCES `category` (`cat_id`),
  CONSTRAINT `invoice_hours_added_fk2` FOREIGN KEY (`iwo_id`) REFERENCES `invoice_work` (`iwo_id`),
  CONSTRAINT `invoice_hours_added_fk3` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `invoice_hours_added_fk4` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `invoice_work_temp` table :
#

CREATE TABLE `invoice_work_temp` (
  `iwt_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `skill_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `skill_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `iwt_login` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `iwt_logout` TIMESTAMP NULL DEFAULT NULL,
  `iwt_sch_start_time` TIMESTAMP NULL DEFAULT NULL,
  `iwt_sch_end_time` TIMESTAMP NULL DEFAULT NULL,
  `iwt_no_show` INTEGER(10) NOT NULL,
  `iwt_non_posted` INTEGER(10) NOT NULL,
  `iwt_acw_time` INTEGER(10) DEFAULT NULL,
  `iwt_total_unavailable_timeibo_ims` INTEGER(10) DEFAULT NULL,
  `iwt_total_not_ready_time` INTEGER(10) DEFAULT NULL,
  `iwt_time_in_other_rc` INTEGER(10) DEFAULT NULL,
  `iwt_ibo_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `iwt_first_name` VARCHAR(100) COLLATE utf8_general_ci NOT NULL,
  `iwt_last_name` VARCHAR(100) COLLATE utf8_general_ci NOT NULL,
  `iwt_agent_id` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `iwt_email` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`iwt_id`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `log_system` table :
#

CREATE TABLE `log_system` (
  `log_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `log_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `log_class` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `log_method` VARCHAR(100) COLLATE utf8_general_ci NOT NULL,
  `log_detail` MEDIUMTEXT COLLATE utf8_general_ci,
  `log_error` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '0',
  `log_class_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `use_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `log_ip` VARCHAR(40) COLLATE utf8_general_ci DEFAULT NULL,
  `log_processed` TINYINT(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`log_id`) USING BTREE,
  KEY `FKlog_system859548` (`use_id`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `menu` table :
#

CREATE TABLE `menu` (
  `men_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `men_descripcion` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `men_action` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `men_action_listener` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `men_father` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`men_id`) USING BTREE,
  UNIQUE KEY `men_descripcion` (`men_descripcion`) USING BTREE,
  KEY `FKmenu937623` (`men_father`) USING BTREE,
  CONSTRAINT `FKmenu937623` FOREIGN KEY (`men_father`) REFERENCES `menu` (`men_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `message_board` table :
#

CREATE TABLE `message_board` (
  `mss_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `mss_date` TIMESTAMP(6) NOT NULL DEFAULT '0000-00-00 00:00:00.000000',
  `mss_message` MEDIUMTEXT COLLATE utf8_general_ci NOT NULL,
  `mss_reply_date` TIMESTAMP NULL DEFAULT '0000-00-00 00:00:00',
  `mss_reply` MEDIUMTEXT COLLATE utf8_general_ci,
  `use_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `inv_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`mss_id`) USING BTREE,
  KEY `use_id` (`use_id`) USING BTREE,
  KEY `inv_id` (`inv_id`) USING BTREE,
  CONSTRAINT `message_board_fk1` FOREIGN KEY (`use_id`) REFERENCES `users` (`use_id`),
  CONSTRAINT `message_board_fk2` FOREIGN KEY (`inv_id`) REFERENCES `invoice` (`inv_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `permissions` table :
#

CREATE TABLE `permissions` (
  `per_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `per_name` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `per_description` VARCHAR(255) COLLATE utf8_general_ci DEFAULT NULL,
  `per_const` VARCHAR(25) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`per_id`) USING BTREE,
  UNIQUE KEY `per_const` (`per_const`) USING BTREE,
  UNIQUE KEY `per_name` (`per_name`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `permissions_menu` table :
#

CREATE TABLE `permissions_menu` (
  `pme_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `per_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `men_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`pme_id`) USING BTREE,
  KEY `FKpermission3976` (`per_id`) USING BTREE,
  KEY `FKpermission367077` (`men_id`) USING BTREE,
  CONSTRAINT `FKpermission367077` FOREIGN KEY (`men_id`) REFERENCES `menu` (`men_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKpermission3976` FOREIGN KEY (`per_id`) REFERENCES `permissions` (`per_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `reason_code_pay` table :
#

CREATE TABLE `reason_code_pay` (
  `rc_id` INTEGER(11) NOT NULL AUTO_INCREMENT,
  `rc_place` TINYINT(4) NOT NULL,
  `rc_code` INTEGER(11) NOT NULL,
  PRIMARY KEY (`rc_id`) USING BTREE
) ENGINE=InnoDB
AUTO_INCREMENT=14 CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `report_group` table :
#

CREATE TABLE `report_group` (
  `gro_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `gro_description` VARCHAR(150) COLLATE utf8_general_ci DEFAULT NULL,
  `gro_class_service` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `gro_filtro_bean` VARCHAR(150) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`gro_id`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `report` table :
#

CREATE TABLE `report` (
  `rep_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `rep_name` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `rep_description` VARCHAR(255) COLLATE utf8_general_ci DEFAULT NULL,
  `rep_method` VARCHAR(30) COLLATE utf8_general_ci DEFAULT NULL,
  `rep_defaults` INTEGER(10) DEFAULT NULL,
  `rep_jrxml` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `gro_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`rep_id`) USING BTREE,
  KEY `FKreport418224` (`gro_id`) USING BTREE,
  CONSTRAINT `FKreport418224` FOREIGN KEY (`gro_id`) REFERENCES `report_group` (`gro_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `roles_permissions` table :
#

CREATE TABLE `roles_permissions` (
  `rop_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `per_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `rol_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `rop_access` VARCHAR(5) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`rop_id`) USING BTREE,
  KEY `FKroles_perm926506` (`per_id`) USING BTREE,
  KEY `FKroles_perm306962` (`rol_id`) USING BTREE,
  CONSTRAINT `FKroles_perm306962` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`rol_id`),
  CONSTRAINT `FKroles_perm926506` FOREIGN KEY (`per_id`) REFERENCES `permissions` (`per_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `routing_number` table :
#

CREATE TABLE `routing_number` (
  `rou_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `rou_number` CHAR(9) COLLATE utf8_general_ci NOT NULL,
  `bank_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `pro_need_update` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`rou_id`) USING BTREE,
  UNIQUE KEY `routing_number_idx1` (`rou_number`, `bank_id`) USING BTREE,
  KEY `bank_id` (`bank_id`) USING BTREE,
  CONSTRAINT `routing_number_fk1` FOREIGN KEY (`bank_id`) REFERENCES `bank` (`bank_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `schedule` table :
#

CREATE TABLE `schedule` (
  `sch_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `sch_date_start` DATETIME NOT NULL,
  `sch_date_end` DATETIME NOT NULL,
  `use_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `eve_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `cli_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `iwo_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`sch_id`) USING BTREE,
  KEY `sch_date_start` (`sch_date_start`) USING BTREE,
  KEY `sch_date_end` (`sch_date_end`) USING BTREE,
  KEY `pro_id` (`use_id`) USING BTREE,
  KEY `eve_id` (`eve_id`) USING BTREE,
  KEY `cli_id` (`cli_id`) USING BTREE,
  CONSTRAINT `schedule_fk1` FOREIGN KEY (`use_id`) REFERENCES `users` (`use_id`),
  CONSTRAINT `schedule_fk2` FOREIGN KEY (`eve_id`) REFERENCES `event` (`eve_id`),
  CONSTRAINT `schedule_fk3` FOREIGN KEY (`cli_id`) REFERENCES `client_applications` (`cli_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `sequence_data` table :
#

CREATE TABLE `sequence_data` (
  `sequence_name` VARCHAR(100) COLLATE latin1_swedish_ci NOT NULL,
  `sequence_increment` INTEGER(11) UNSIGNED NOT NULL DEFAULT 1,
  `sequence_min_value` INTEGER(11) UNSIGNED NOT NULL DEFAULT 1,
  `sequence_max_value` BIGINT(20) UNSIGNED NOT NULL DEFAULT 18446744073709551615,
  `sequence_cur_value` BIGINT(20) UNSIGNED DEFAULT 1,
  `sequence_cycle` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`sequence_name`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_swedish_ci'
;

#
# Structure for the `server_existing` table :
#

CREATE TABLE `server_existing` (
  `se_id` INTEGER(11) NOT NULL AUTO_INCREMENT,
  `se_host` VARCHAR(200) COLLATE latin1_swedish_ci NOT NULL,
  `se_priority` SMALLINT(6) NOT NULL,
  `se_url_webservice` VARCHAR(500) COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`se_id`) USING BTREE,
  UNIQUE KEY `se_name` (`se_host`) USING BTREE
) ENGINE=InnoDB
AUTO_INCREMENT=11 CHARACTER SET 'latin1' COLLATE 'latin1_swedish_ci'
;

#
# Structure for the `skill_phone_system` table :
#

CREATE TABLE `skill_phone_system` (
  `sps_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `sps_name` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `cli_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `sps_place` VARCHAR(45) COLLATE utf8_general_ci NOT NULL,
  `sps_phone_system` INTEGER(10) NOT NULL,
  PRIMARY KEY (`sps_id`) USING BTREE,
  UNIQUE KEY `sps_name` (`sps_name`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `thresholds` table :
#

CREATE TABLE `thresholds` (
  `thr_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `thr_metric` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `thr_min` DOUBLE DEFAULT NULL,
  `thr_max` DOUBLE DEFAULT NULL,
  `thr_update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `thr_description` VARCHAR(150) COLLATE utf8_general_ci NOT NULL DEFAULT 'desc',
  `thr_color` CHAR(6) COLLATE utf8_general_ci NOT NULL,
  `thr_font_color` CHAR(6) COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`thr_id`) USING BTREE,
  UNIQUE KEY `thr_metric` (`thr_metric`) USING BTREE,
  KEY `FKthresholds350172` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKthresholds350172` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `updated_register` table :
#

CREATE TABLE `updated_register` (
  `up_id` INTEGER(11) NOT NULL,
  `up_description` VARCHAR(50) COLLATE latin1_swedish_ci DEFAULT NULL,
  `up_date` DATETIME DEFAULT '0000-00-00 00:00:00',
  `up_state` TINYINT(1) NOT NULL DEFAULT 1,
  `up_make_copy` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`up_id`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `zip_codes` table :
#

CREATE TABLE `zip_codes` (
  `zip_id` CHAR(36) COLLATE latin1_swedish_ci NOT NULL,
  `zip_number` CHAR(5) COLLATE latin1_swedish_ci DEFAULT NULL,
  `sta_id` CHAR(36) COLLATE latin1_swedish_ci NOT NULL,
  `zip_city` VARCHAR(100) COLLATE latin1_swedish_ci DEFAULT NULL,
  PRIMARY KEY (`zip_id`) USING BTREE,
  UNIQUE KEY `zip_number` (`zip_number`) USING BTREE,
  KEY `sta_id` (`sta_id`) USING BTREE
) ENGINE=InnoDB
CHARACTER SET 'latin1' COLLATE 'latin1_swedish_ci'
;

#
# Definition for the `nextval` function :
#

DELIMITER $$

CREATE DEFINER = 'iboims'@'%' FUNCTION `nextval`(
        `seq_name` VARCHAR(100)
    )
    RETURNS BIGINT(20)
    NOT DETERMINISTIC
    CONTAINS SQL
    SQL SECURITY DEFINER
    COMMENT ''
BEGIN
 DECLARE cur_val bigint(20);

    SELECT sequence_cur_value INTO cur_val
    FROM sequence_data WHERE sequence_name = seq_name;

    IF cur_val IS NOT NULL THEN
        UPDATE
            sequence_data
        SET
            sequence_cur_value = IF (
                (sequence_cur_value + sequence_increment) > sequence_max_value,
                IF (
                    sequence_cycle = TRUE,
                    sequence_min_value,
                    NULL
                ),
                sequence_cur_value + sequence_increment
            )
        WHERE
            sequence_name = seq_name;
    END IF;

    RETURN cur_val;
END$$

DELIMITER ;

#
# Definition for the `invoice_problem` view :
#

CREATE ALGORITHM=UNDEFINED DEFINER='iboims'@'%' SQL SECURITY DEFINER VIEW `invoice_problem`
AS
select
  `w`.`iwo_id` AS `id`,
  `p`.`pro_number` AS `pro_number`,
  `u`.`use_first_name` AS `use_first_name`,
  `u`.`use_middle_name` AS `use_middle_name`,
  `u`.`use_last_name` AS `use_last_name`,
  `i`.`inv_number` AS `inv_number`,
  `w`.`iwo_sch_start_time` AS `iwo_sch_start_time`,
  `w`.`iwo_sch_end_time` AS `iwo_sch_end_time`,
  `w`.`skill_id` AS `skill_id`,
  `w`.`skill_name` AS `skill_name`,
  `w`.`cli_id` AS `cli_id`,
  `w`.`cli_name` AS `cli_name`,
  (case
     when isnull(`w`.`cli_id`) then 'A'
     when (`w`.`iwo_amount` = 0) then 'B'
     when isnull(`w`.`inv_id`) then 'D'
     when isnull(`ic`.`cli_id`) then 'C'
     else NULL
   end) AS `problem_id`,
  `u`.`use_id` AS `use_id`,
  `i`.`inv_id` AS `inv_id`
from
  ((((`invoice_work` `w`
  left join `invoice` `i` on ((`i`.`inv_id` = `w`.`inv_id`)))
  join `users` `u` on ((`u`.`use_id` = `w`.`use_id`)))
  join `profile` `p` on ((`p`.`use_id` = `u`.`use_id`)))
  left join `ibos_client_applications` `ic` on (((`ic`.`pro_id` = `p`.`pro_id`) and (`ic`.`cli_id` = `w`.`cli_id`))))
where
  ((`i`.`ivt_id` in ('1', '2')) and
  ((`w`.`iwo_amount` = 0) or
  isnull(`w`.`cli_id`) or
  isnull(`w`.`inv_id`) or
  isnull(`ic`.`cli_id`)));

#
# Definition for the `test` view :
#

CREATE ALGORITHM=UNDEFINED DEFINER='root'@'%' SQL SECURITY DEFINER VIEW `test`
AS
select
  `mb`.`mss_id` AS `msg_id`,
  max(`mb`.`mss_date`) AS `date`,
  max(`mb`.`mss_id`) AS `mss_id`,
  `mb`.`inv_id` AS `inv_id`
from
  `message_board` `mb`
where
  (`mb`.`mss_date` =
  (
    select
      max(`mb2`.`mss_date`)
    from
      `message_board` `mb2`
    where
      (`mb`.`inv_id` = `mb2`.`inv_id`)
  ))
group by
  `mb`.`inv_id`
order by
  `mb`.`mss_date` desc;

#
# Definition for the `vw_message_sub_query` view :
#

CREATE ALGORITHM=UNDEFINED DEFINER='iboims'@'%' SQL SECURITY DEFINER VIEW `vw_message_sub_query`
AS
select
  max(`mb`.`mss_date`) AS `mss_date`,
  `mb`.`inv_id` AS `inv_id`
from
  `message_board` `mb`
group by
  `mb`.`inv_id`;

#
# Definition for the `vw_message_board` view :
#

CREATE ALGORITHM=UNDEFINED DEFINER='iboims'@'%' SQL SECURITY DEFINER VIEW `vw_message_board`
AS
select
  `b`.`mss_id` AS `msg_id`,
  `b`.`mss_date` AS `mss_date`,
  `b`.`mss_id` AS `mss_id`,
  `b`.`mss_reply_date` AS `mss_reply_date`
from
  (`message_board` `b`
  join `vw_message_sub_query` `lastm` on (((`lastm`.`inv_id` = `b`.`inv_id`) and (`lastm`.`mss_date` = `b`.`mss_date`))));

#
# Definition for the `vw_onboarding` view :
#

CREATE ALGORITHM=UNDEFINED DEFINER='iboims'@'%' SQL SECURITY DEFINER VIEW `vw_onboarding`
AS
select
  `temp`.`ite_id` AS `id`,
  `temp`.`ite_user_name` AS `use_name`,
  `temp`.`ite_first_name` AS `first_name`,
  `temp`.`ite_middle_name` AS `middle_name`,
  `temp`.`ite_last_name` AS `last_name`,
  `temp`.`ite_state` AS `state`,
  `temp`.`ite_email` AS `email`,
  '3'  AS `ibo_state_id`,
  'New'  AS `ibo_state`,
  ''  AS `corporation_name`,
  ''  AS `add_description`,
  ''  AS `add_zip_code`,
  ''  AS `add_city`,
  ''  AS `cou_id`,
  ''  AS `cou_name`,
  ''  AS `sta_id`,
  ''  AS `sta_name`,
  ''  AS `add_state`,
  1 AS `temporal`,
  ''  AS `number`,
  0 AS `has_bank`,
  `temp`.`ite_type_contract` AS `type_contract`
from
  `ibo_temp` `temp`
union
select
  `u`.`use_id` AS `USE_ID`,
  `u`.`use_name` AS `USE_NAME`,
  `u`.`use_first_name` AS `USE_FIRST_NAME`,
  `u`.`use_middle_name` AS `USE_middle_NAME`,
  `u`.`use_last_name` AS `USE_LAST_NAME`,
  `u`.`use_state` AS `USE_STATE`,
  `u`.`use_email` AS `USE_EMAIL`,
  `i`.`ist_id` AS `ibo_state_id`,
  `i`.`ist_name` AS `ibo_state`,
  `cor`.`cor_name` AS `corporation_name`,
  `a`.`add_description` AS `add_description`,
  `a`.`add_zip_code` AS `add_zip_code`,
  `a`.`add_city` AS `add_city`,
  `a`.`cou_id` AS `COU_ID`,
  `t`.`cou_name` AS `cou_name`,
  `a`.`sta_id` AS `STA_ID`,
  `st`.`sta_name` AS `STA_NAME`,
  `a`.`add_state` AS `ADD_STATE`,
  0 AS `temporal`,
  `p`.`pro_number` AS `number`,
  (
    select
      (case
         when (count(0) > 0) then 1
         else 0
       end)
    from
      `bank_information` `b`
    where
      (`b`.`cor_id` = `cor`.`cor_id`)
  ) AS `has_bank`,
  `p`.`pro_type_contract` AS `type_contract`
from
  ((((((`users` `u`
  join `profile` `p` on ((`p`.`use_id` = `u`.`use_id`)))
  left join `corporation` `cor` on ((`cor`.`cor_id` = `p`.`cor_id`)))
  join `ibo_state` `i` on ((`i`.`ist_id` = `p`.`ist_id`)))
  left join `address` `a` on ((`a`.`cor_id` = `p`.`cor_id`)))
  left join `states` `st` on ((`st`.`sta_id` = `a`.`sta_id`)))
  left join `country` `t` on ((`t`.`cou_id` = `a`.`cou_id`)))
where
  (`u`.`typ_id` = '2');



DELIMITER $$

CREATE DEFINER = 'iboims'@'%' TRIGGER `_before_ins_tr1` BEFORE INSERT ON `bank_information`
  FOR EACH ROW
BEGIN

END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'iboims'@'%' TRIGGER `incentive_after_ins` AFTER INSERT ON `incentive`
  FOR EACH ROW
BEGIN

/*update the fields in invoice when add some work*/
update invoice i
set inv_import_total = i.inv_import_total + NEW.inc_amount,
    inv_total_incentive = i.inv_total_incentive + NEW.inc_amount
where i.inv_id = NEW.inv_id;

END$$

CREATE DEFINER = 'iboims'@'%' TRIGGER `incentive_before_upd` BEFORE UPDATE ON `incentive`
  FOR EACH ROW
BEGIN

/*update the fields in invoice when add some work*/
update invoice i
set inv_import_total = i.inv_import_total + NEW.inc_amount - OLD.inc_amount,
    inv_total_incentive = i.inv_total_incentive  + NEW.inc_amount - OLD.inc_amount
where i.inv_id = NEW.inv_id;

END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'iboims'@'%' TRIGGER `invoice_before_upd` BEFORE UPDATE ON `invoice`
  FOR EACH ROW
BEGIN

if(OLD.inv_admin_fee != NEW.inv_admin_fee)then
                     SET NEW.inv_import_total = (OLD.inv_import_total + OLD.inv_admin_fee - NEW.inv_admin_fee);
end if;

END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'iboims'@'%' TRIGGER `invoice_hours_added_after_ins` AFTER INSERT ON `invoice_hours_added`
  FOR EACH ROW
BEGIN
DECLARE rate DECIMAL;
DECLARE invid CHAR(36);

select i.iwo_amount, i.inv_id INTO rate, invid
from invoice_work i where i.iwo_id = NEW.iwo_id;

if rate > 0 then
   update invoice i
    set inv_import_total = i.inv_import_total + ( rate * NEW.iho_hours),
        inv_hours_added = i.inv_hours_added + NEW.iho_hours
    where i.inv_id = invid;
end if;


END$$

CREATE DEFINER = 'iboims'@'%' TRIGGER `invoice_hours_added_before_upd` BEFORE UPDATE ON `invoice_hours_added`
  FOR EACH ROW
BEGIN
DECLARE rate DECIMAL;
DECLARE invid CHAR(36);

select i.iwo_amount, i.inv_id INTO rate, invid
from invoice_work i where i.iwo_id = NEW.iwo_id;

if rate > 0 then
   update invoice i
    set i.inv_import_total = i.inv_import_total - ( rate * OLD.iho_hours) + ( rate * NEW.iho_hours),
        i.inv_hours_added = i.inv_hours_added + NEW.iho_hours - OLD.iho_hours
    where i.inv_id = invid;
end if;


END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'iboims'@'%' TRIGGER `invoice_work_after_ins` AFTER INSERT ON `invoice_work`
  FOR EACH ROW
BEGIN

/*
update invoice i
set i.inv_actual_service = i.inv_actual_service + (NEW.iwo_actual_service/3600),
    i.inv_service_revenue = i.inv_service_revenue + NEW.iwo_service_revenue,
    i.inv_import_total = i.inv_import_total + NEW.iwo_service_revenue
where i.inv_id = NEW.inv_id;
*/
update invoice i
set i.inv_actual_service = i.inv_actual_service + NEW.iwo_actual_service,
    i.inv_service_revenue = i.inv_service_revenue + NEW.iwo_service_revenue,
    i.inv_import_total = i.inv_import_total + NEW.iwo_service_revenue
where i.inv_id = NEW.inv_id;

END$$

CREATE DEFINER = 'iboims'@'%' TRIGGER `invoice_work_before_upd` BEFORE UPDATE ON `invoice_work`
  FOR EACH ROW
BEGIN

DECLARE newServiceRevenue DECIMAL(19,6);
DECLARE hoursAdded DECIMAL(11,6);

/*if rate change update import total and service revenue in invoice*/
if OLD.iwo_amount !=  NEW.iwo_amount and  NEW.inv_id is not null then
   /*SET newServiceRevenue = (NEW.iwo_actual_service/3600) * NEW.iwo_amount;*/

   SET newServiceRevenue = NEW.iwo_actual_service * NEW.iwo_amount;

   /*search the hours added*/
   select IFNULL(SUM(h.iho_hours),0) INTO hoursAdded from invoice_hours_added h where h.iwo_id = NEW.iwo_id;


   /*update the invoice*/
   update invoice i
    set i.inv_import_total = i.inv_import_total - OLD.iwo_service_revenue - ( OLD.iwo_amount * hoursAdded) + ( NEW.iwo_amount * hoursAdded) + newServiceRevenue,
        i.inv_service_revenue = i.inv_service_revenue - OLD.iwo_service_revenue + newServiceRevenue
    where i.inv_id = NEW.inv_id;


   SET NEW.iwo_service_revenue = newServiceRevenue;

ELSEIF  OLD.inv_id is null and NEW.inv_id is not null then
      /*if was assigned to a invoice and before was null add the import and the hours to the invoice*/
         update invoice i
          set i.inv_actual_service = i.inv_actual_service + NEW.iwo_actual_service,
              i.inv_service_revenue = i.inv_service_revenue + NEW.iwo_service_revenue,
              i.inv_import_total = i.inv_import_total + NEW.iwo_service_revenue
          where i.inv_id = NEW.inv_id;

ELSEIF OLD.iwo_actual_service <> NEW.iwo_actual_service then
       /*update the invoice with the change this case is when I add phone system record from 48 hours before*/
       update invoice i
        set i.inv_import_total = i.inv_import_total - OLD.iwo_service_revenue +  NEW.iwo_service_revenue,
            i.inv_service_revenue = i.inv_service_revenue - OLD.iwo_service_revenue +  NEW.iwo_service_revenue,
            i.inv_actual_service = i.inv_actual_service - OLD.iwo_actual_service + NEW.iwo_actual_service
        where i.inv_id = invid;

end if;

END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'iboims'@'%' TRIGGER `permissions_before_ins_tr1` BEFORE INSERT ON `permissions`
  FOR EACH ROW
BEGIN

END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'iboims'@'%' TRIGGER `profile_before_ins_tr1` BEFORE INSERT ON `profile`
  FOR EACH ROW
BEGIN
DECLARE nextId BIGINT;
DECLARE yearString VARCHAR(2);
DECLARE number CHAR(5);
DECLARE cont CHAR(3);

if(new.pro_number is null)then

       if(new.pro_type_contract = 'INT_INDEP_CONTRACT')then
             SET  cont = 'INT';
       elseif(new.pro_type_contract = 'INTERNATIONAL')then
             SET  cont = 'DRV';
       else
             SET  cont = 'GVW';
       end if;

      /*get the next number*/
      select nextval('sq_ibo_number') INTO nextId;

      /*get the actual year*/
      select SUBSTRING(YEAR(now()),3) INTO yearString;

      /*fill with cero the number*/
      SET number = '';
      SET @x = 0;
      REPEAT
          SET @x = @x + 1;
          SET number = CONCAT(number,'0');
          UNTIL @x > 4 - LENGTH(nextId)
      END REPEAT;

       SET number = CONCAT(number,nextId);

      /*The IBO format number GVW or IDC+YEAR+00000*/
      SET new.pro_number = CONCAT(cont,yearString, number);

END IF;

END$$

CREATE DEFINER = 'iboims'@'%' TRIGGER `profile_before_upd_tr1` BEFORE UPDATE ON `profile`
  FOR EACH ROW
BEGIN

DECLARE cont CHAR(3);

if(new.pro_type_contract <> old.pro_type_contract)then

      if(new.pro_type_contract = 'INT_INDEP_CONTRACT')then
             SET  cont = 'INT';
       elseif(new.pro_type_contract = 'INTERNATIONAL')then
             SET  cont = 'DRV';
       else
             SET  cont = 'GVW';
       end if;

       SET new.pro_number = CONCAT(cont,SUBSTRING(old.pro_number,4));
END IF;


END$$

DELIMITER ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;