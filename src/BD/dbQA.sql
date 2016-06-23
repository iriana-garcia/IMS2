# SQL Manager Lite for MySQL 5.5.3.46192
# ---------------------------------------
# Host     : 10.1.10.169
# Port     : 3306
# Database : ibo-ims-qa


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES latin1 */;

SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE `ibo-ims-qa`
    CHARACTER SET 'latin1'
    COLLATE 'latin1_swedish_ci';

USE `ibo-ims-qa`;

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
  CONSTRAINT `FKroles478874` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKroles492862` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
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
  CONSTRAINT `FKusers362280` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`) ON DELETE SET NULL,
  CONSTRAINT `FKusers376268` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`) ON DELETE SET NULL,
  CONSTRAINT `FKusers570115` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`rol_id`)
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
# Structure for the `profile` table :
#

CREATE TABLE `profile` (
  `use_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `grp_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `ity_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_corporation_name` VARCHAR(150) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_princ_owner` VARCHAR(150) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_princ_email` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_ein` CHAR(9) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_princ_title` VARCHAR(150) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_region` CHAR(1) COLLATE utf8_general_ci DEFAULT 'D',
  `ist_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `pro_update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_amount` DECIMAL(19,2) DEFAULT NULL,
  `pro_id_pipkins` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `pro_created_date` TIMESTAMP(6) NULL DEFAULT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `pro_supplier_number` VARCHAR(30) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_need_update` TINYINT(1) NOT NULL COMMENT 'Save if the user need to be updated in oracle',
  `pro_number` VARCHAR(20) COLLATE utf8_general_ci DEFAULT NULL,
  `pro_total_submit` INTEGER(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`pro_id`) USING BTREE,
  UNIQUE KEY `use_id` (`use_id`) USING BTREE,
  UNIQUE KEY `pro_number` (`pro_number`) USING BTREE,
  KEY `FKprofile945334` (`use_id`) USING BTREE,
  KEY `FKprofile177080` (`grp_id`) USING BTREE,
  KEY `FKprofile697747` (`ity_id`) USING BTREE,
  KEY `FKprofile978795` (`ist_id`) USING BTREE,
  KEY `FKprofile371464` (`use_id_updated`) USING BTREE,
  KEY `use_id_created` (`use_id_created`) USING BTREE,
  CONSTRAINT `FKprofile177080` FOREIGN KEY (`grp_id`) REFERENCES `groups` (`grp_id`),
  CONSTRAINT `FKprofile371464` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKprofile697747` FOREIGN KEY (`ity_id`) REFERENCES `ibo_type` (`ity_id`),
  CONSTRAINT `FKprofile945334` FOREIGN KEY (`use_id`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKprofile978795` FOREIGN KEY (`ist_id`) REFERENCES `ibo_state` (`ist_id`),
  CONSTRAINT `profile_fk1` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`)
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
  `pro_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `cou_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `sta_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `add_state` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `add_created_date` TIMESTAMP(6) NULL DEFAULT NULL,
  `add_update_date` TIMESTAMP(6) NULL DEFAULT NULL,
  `use_id_created` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `add_need_update` TINYINT(1) NOT NULL COMMENT 'Save if the user need to be updated in oracle',
  PRIMARY KEY (`add_id`) USING BTREE,
  KEY `FKaddress754320` (`pro_id`) USING BTREE,
  KEY `FKaddress927071` (`cou_id`) USING BTREE,
  KEY `FKaddress789439` (`sta_id`) USING BTREE,
  KEY `address_fk1` (`use_id_created`) USING BTREE,
  KEY `address_fk2` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKaddress754320` FOREIGN KEY (`pro_id`) REFERENCES `profile` (`pro_id`),
  CONSTRAINT `FKaddress789439` FOREIGN KEY (`sta_id`) REFERENCES `states` (`sta_id`),
  CONSTRAINT `FKaddress927071` FOREIGN KEY (`cou_id`) REFERENCES `country` (`cou_id`),
  CONSTRAINT `address_fk1` FOREIGN KEY (`use_id_created`) REFERENCES `users` (`use_id`),
  CONSTRAINT `address_fk2` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `bank_information` table :
#

CREATE TABLE `bank_information` (
  `pro_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ban_name` VARCHAR(255) COLLATE utf8_general_ci DEFAULT NULL,
  `ban_number` VARCHAR(255) COLLATE utf8_general_ci NOT NULL,
  `ban_routing` VARCHAR(255) COLLATE utf8_general_ci NOT NULL,
  `ban_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `ban_need_update` TINYINT(1) NOT NULL,
  PRIMARY KEY (`ban_id`) USING BTREE,
  UNIQUE KEY `pro_id` (`pro_id`) USING BTREE,
  KEY `FKbank_infor294310` (`pro_id`) USING BTREE,
  CONSTRAINT `FKbank_infor294310` FOREIGN KEY (`pro_id`) REFERENCES `profile` (`pro_id`)
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
  CONSTRAINT `category_fk2` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
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
  PRIMARY KEY (`sys_id`) USING BTREE,
  KEY `FKconfigurat575895` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKconfigurat575895` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`) ON DELETE SET NULL
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
  PRIMARY KEY (`ite_id`) USING BTREE,
  UNIQUE KEY `ite_user_name` (`ite_user_name`) USING BTREE,
  KEY `FKibo_temp538580` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKibo_temp538580` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`)
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
  `ivt_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
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
  `inv_send_oracle` CHAR(1) COLLATE utf8_general_ci NOT NULL DEFAULT '0',
  `inv_date_oracle` TIMESTAMP NULL DEFAULT '0000-00-00 00:00:00',
  `pro_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `inv_start` TIMESTAMP NULL DEFAULT NULL,
  `inv_end` TIMESTAMP NULL DEFAULT NULL,
  `ivt_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
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
  `ski_id_pip` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `ski_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `cli_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`ski_id`) USING BTREE,
  UNIQUE KEY `ski_name` (`ski_name`) USING BTREE,
  UNIQUE KEY `ski_id_pip` (`ski_id_pip`) USING BTREE,
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
  PRIMARY KEY (`inc_id`) USING BTREE,
  KEY `ski_id` (`ski_id`) USING BTREE,
  KEY `inv_id` (`inv_id`) USING BTREE,
  CONSTRAINT `incentive_fk1` FOREIGN KEY (`inv_id`) REFERENCES `invoice` (`inv_id`),
  CONSTRAINT `incentive_fk2` FOREIGN KEY (`ski_id`) REFERENCES `skill` (`ski_id`)
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
  `inv_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `iwo_update_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `use_id_updated` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `cli_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `cli_name` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  `iwo_amount` DECIMAL(19,6) DEFAULT NULL,
  `skill_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `skill_name` VARCHAR(50) COLLATE utf8_general_ci NOT NULL,
  `iwo_sch_start_time` TIMESTAMP NULL DEFAULT NULL,
  `iwo_sch_end_time` TIMESTAMP NULL DEFAULT NULL,
  `iwo_total_not_ready_time` INTEGER(11) DEFAULT NULL COMMENT 'Save in seconds',
  `iwo_hours_add` DECIMAL(19,6) DEFAULT NULL,
  `iwo_actual_service` DECIMAL(19,6) DEFAULT NULL,
  `iwo_service_revenue` DECIMAL(19,6) DEFAULT NULL,
  `iwo_hours_buffer` INTEGER(11) DEFAULT NULL COMMENT 'save hours in actual service by buffer',
  `iwo_buffer` INTEGER(11) DEFAULT NULL COMMENT 'save buffer used',
  `iwo_total_ready_time` INTEGER(11) DEFAULT NULL COMMENT 'Save in seconds',
  PRIMARY KEY (`iwo_id`) USING BTREE,
  KEY `FKinvoice_wo353699` (`inv_id`) USING BTREE,
  KEY `FKinvoice_wo336438` (`use_id_updated`) USING BTREE,
  CONSTRAINT `FKinvoice_wo336438` FOREIGN KEY (`use_id_updated`) REFERENCES `users` (`use_id`),
  CONSTRAINT `FKinvoice_wo353699` FOREIGN KEY (`inv_id`) REFERENCES `invoice` (`inv_id`)
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
  PRIMARY KEY (`log_id`) USING BTREE,
  KEY `FKlog_system859548` (`use_id`) USING BTREE,
  CONSTRAINT `FKlog_system859548` FOREIGN KEY (`use_id`) REFERENCES `users` (`use_id`)
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
  `per_const` VARCHAR(20) COLLATE utf8_general_ci NOT NULL,
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
  CONSTRAINT `FKpermission367077` FOREIGN KEY (`men_id`) REFERENCES `menu` (`men_id`) ON UPDATE CASCADE,
  CONSTRAINT `FKpermission3976` FOREIGN KEY (`per_id`) REFERENCES `permissions` (`per_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
;

#
# Structure for the `phone_system_detail` table :
#

CREATE TABLE `phone_system_detail` (
  `pho_id` CHAR(36) COLLATE utf8_general_ci NOT NULL,
  `pro_id` CHAR(36) COLLATE utf8_general_ci DEFAULT NULL,
  `pho_date_start` TIMESTAMP(6) NOT NULL DEFAULT '0000-00-00 00:00:00.000000',
  `pho_date_end` TIMESTAMP(6) NOT NULL DEFAULT '0000-00-00 00:00:00.000000',
  `pho_event_type` INTEGER(1) NOT NULL,
  `pho_reason_code` INTEGER(11) DEFAULT NULL,
  `pho_email` VARCHAR(150) COLLATE utf8_general_ci DEFAULT NULL,
  `pho_agent_id` VARCHAR(50) COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`pho_id`) USING BTREE,
  KEY `pro_id` (`pro_id`) USING BTREE,
  CONSTRAINT `phone_system_detail_fk1` FOREIGN KEY (`pro_id`) REFERENCES `profile` (`pro_id`)
) ENGINE=InnoDB
CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
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
  CONSTRAINT `FKroles_perm306962` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`rol_id`) ON DELETE CASCADE,
  CONSTRAINT `FKroles_perm926506` FOREIGN KEY (`per_id`) REFERENCES `permissions` (`per_id`)
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
  ''  AS `number`
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
  `p`.`pro_corporation_name` AS `corporation_name`,
  `a`.`add_description` AS `add_description`,
  `a`.`add_zip_code` AS `add_zip_code`,
  `a`.`add_city` AS `add_city`,
  `a`.`cou_id` AS `COU_ID`,
  `t`.`cou_name` AS `cou_name`,
  `a`.`sta_id` AS `STA_ID`,
  `st`.`sta_name` AS `STA_NAME`,
  `a`.`add_state` AS `ADD_STATE`,
  0 AS `temporal`,
  `p`.`pro_number` AS `number`
from
  (((((`users` `u`
  join `profile` `p` on ((`p`.`use_id` = `u`.`use_id`)))
  join `ibo_state` `i` on ((`i`.`ist_id` = `p`.`ist_id`)))
  left join `address` `a` on ((`a`.`pro_id` = `p`.`pro_id`)))
  left join `states` `st` on ((`st`.`sta_id` = `a`.`sta_id`)))
  left join `country` `t` on ((`t`.`cou_id` = `a`.`cou_id`)))
where
  (`u`.`typ_id` = '2');

#
# Data for the `user_type` table  (LIMIT 0,500)
#

INSERT INTO `user_type` (`typ_id`, `typ_name`) VALUES
  ('1','User'),
  ('2','IBO'),
  ('3','PA');
COMMIT;

#
# Data for the `roles` table  (LIMIT 0,500)
#

INSERT INTO `roles` (`rol_id`, `rol_name`, `rol_created_date`, `rol_update_date`, `use_id_created`, `use_id_updated`, `rol_description`, `rol_state`) VALUES
  ('07b2f8e8-05ed-4176-967f-95db9d847832','Finance','2016-03-10 12:21:03',NULL,'1',NULL,NULL,'1'),
  ('2fff203d-bdd3-4128-b2dd-e8ba88cebde6','Test','2016-04-02 16:43:30',NULL,'1',NULL,NULL,'1'),
  ('4556ed01-4f92-42f9-8d65-b55e5a0d1b12','IBO','2016-03-10 12:13:30','2016-03-20 13:56:25','1','1',NULL,'1'),
  ('841fba6b-4ae3-4c97-9921-d3175ea35b55','System Administrator','2016-02-18 15:53:26','2016-04-02 16:43:27','1','1',NULL,'1'),
  ('a6cdfc9f-5c35-434d-ba1d-53810562d2f3','PA / IBOC','2016-03-10 12:22:01',NULL,'1',NULL,NULL,'1');
COMMIT;

#
# Data for the `users` table  (LIMIT 0,500)
#

INSERT INTO `users` (`use_id`, `use_name`, `use_first_name`, `use_middle_name`, `use_last_name`, `use_email`, `use_state`, `use_created_date`, `use_update_date`, `use_last_login`, `rol_id`, `use_id_created`, `use_id_updated`, `use_phone`, `use_super`, `grp_id`, `use_password`, `typ_id`, `use_need_update`, `use_date_state`) VALUES
  ('1','admin_ims','admin',NULL,'admin','admin_ims','1','2016-02-05 09:30:56','2016-02-18 16:52:07','2016-04-02 16:47:06','841fba6b-4ae3-4c97-9921-d3175ea35b55',NULL,NULL,NULL,1,NULL,'ib2y1MeW/DsJ03K5v93RyA==','1',0,NULL),
  ('1cb9eaea-3e57-4c5d-aca2-34ba42a8a99d','mtoro','Martin',NULL,'Toro','mtoro@gretahealthworks.com','1','2016-02-15 16:56:15','2016-03-26 10:51:42','2016-03-31 15:07:40','841fba6b-4ae3-4c97-9921-d3175ea35b55','1','1','',0,NULL,NULL,'1',0,NULL),
  ('438bb8de-589b-4183-acee-048a93d91abe','zborges','Zully',NULL,'Borges','zborges@greathelathworks.com','1','2016-02-10 15:51:03','2016-03-31 15:06:42','2016-03-31 15:07:51','841fba6b-4ae3-4c97-9921-d3175ea35b55','1','1','',0,NULL,NULL,'1',0,'2016-03-31 15:04:32.000000'),
  ('7ae0111e-19a9-4085-bf2f-114d2ae69e38','ssimon','Shaneda',NULL,'Simon','ssimon','1','2016-02-10 18:09:59','2016-03-31 15:06:02','2016-03-31 15:07:06','841fba6b-4ae3-4c97-9921-d3175ea35b55','1','1','',0,NULL,NULL,'1',0,'2016-03-31 15:04:32.000000');
COMMIT;

#
# Data for the `ibo_type` table  (LIMIT 0,500)
#

INSERT INTO `ibo_type` (`ity_id`, `ity_name`) VALUES
  ('1','IBO'),
  ('2','Retention'),
  ('3','UIBO');
COMMIT;

#
# Data for the `ibo_state` table  (LIMIT 0,500)
#

INSERT INTO `ibo_state` (`ist_id`, `ist_name`) VALUES
  ('1','Email Sent'),
  ('2','Error to send email'),
  ('3','New'),
  ('4','Ok'),
  ('5','Sending email');
COMMIT;

#
# Data for the `country` table  (LIMIT 0,500)
#

INSERT INTO `country` (`cou_id`, `cou_name`) VALUES
  ('01481495-a69b-4886-af60-fe9557e33596','Uzbekistan'),
  ('0199df8f-e666-4a20-9876-d660d4d216ae','Nicaragua'),
  ('029b823e-d87b-4f70-8cd4-bf0c98585beb','Greece'),
  ('02b4ae29-6d8b-4e61-8f43-885b8ea9fe16','Equatorial Guinea'),
  ('02cf25ac-d237-4a01-adca-3975d86ea09a','Spain'),
  ('04edaeb5-8840-4059-8d04-ce7e7fe4cd6e','Nigeria'),
  ('09525e77-e448-46fa-bdb4-2400f05c28eb','Guyana'),
  ('0984ea89-fc7d-4f15-b149-1e8c95e85622','Portugal'),
  ('0a85c1e0-0d47-426c-9ffe-a0efea07c691','Kuwait'),
  ('0c30f25d-3d75-4e0c-8734-89b4df262375','Ukraine'),
  ('0e7d5e31-dada-49ab-95bd-ae0373f74957','South Africa'),
  ('117531f4-9c3d-41d2-b0b9-6fcf0cc73048','Republic of Korea (South Korea)'),
  ('119ab3ec-529d-4828-9c15-f74b42122bab','Palau'),
  ('130a2bc5-0903-4666-b45f-a33a62e821fe','Guinea'),
  ('13d51dbb-04ac-446c-9e01-b1f270a7dd9b','Lesotho'),
  ('13d62b29-fa76-48c8-999f-f309de579dbc','Somalia'),
  ('147a3af2-c235-4f56-a1ef-66c02d758c5e','Liberia'),
  ('15c538d2-e479-48bb-a20d-2aed01b9a16b','Jamaica'),
  ('1695beb5-bb5e-466f-89d7-2206aef095ab','Philippines'),
  ('1880e4cb-f8c8-4056-9aa7-03b07c8cbbfd','Haiti'),
  ('199b6e73-944f-4636-a5f4-e18f223ddcf6','Ghana'),
  ('1a2a5b5d-5606-4efc-81db-522d11d058d9','Luxembourg'),
  ('1a46cc6a-a4bf-47ff-9ec7-1bb618853bfa','Sweden'),
  ('1a4c0c88-ff11-4488-9f9d-6e9b82b0312f','Iraq'),
  ('1bb9a308-ff03-4ff6-ad5d-38d04aee0b2e','Romania'),
  ('20080111-5a03-4758-9f4a-128538d1d5f4','Turkey'),
  ('21071652-71b8-4dfe-9a28-d97391a3da17','Japan'),
  ('2309daf2-b55e-4236-bf1e-5647c46c0a13','Iran'),
  ('2381b3e5-9308-4dee-b93a-26bab18c0afb','Eritrea'),
  ('263270ff-be26-4f64-8556-5d71371a1a18','New Zealand'),
  ('26c3465f-4c1a-40d6-9c21-132ed353cfdc','Netherlands'),
  ('29e5f9bc-289b-4c8e-a992-b5c1ad4d5dba','Democratic People''s Republic of Korea (North Korea)'),
  ('2aec9533-7d87-4928-88f6-049951f02bd2','Suriname'),
  ('2bf748ba-d0d9-4c46-a99f-aefb59feb9ae','Benin'),
  ('2dca4fcc-b7e9-4ed3-a23e-d05fc176c32d','Cyprus'),
  ('2f5e4657-54b0-4ca0-9ad3-32ec831f3913','Cambodia'),
  ('2fbf19e2-1e9f-468c-b483-00d2eef130b9','Pakistan'),
  ('32ed4610-b59c-4fa6-8dd1-258bf56f810a','Zimbabwe'),
  ('3497c431-9067-404c-857d-12485450ddc0','Togo'),
  ('376b9038-271a-4d08-ae88-54368f33c5dd','Rwanda'),
  ('37acfada-8d02-4601-8483-0208b4cfb123','Georgia'),
  ('399e0341-f157-46b5-808b-9b455813fa97','Chad'),
  ('3a4f9ba9-6e46-490c-977f-336df46066f2','Nepal'),
  ('3b5647e0-805d-412d-8dfa-0c12000d821c','Sudan'),
  ('3cb4c936-9156-4bfd-b633-0de250957902','Argentina'),
  ('3daa04ba-f6fd-46d5-804e-2c7c4e7c806f','Mongolia'),
  ('3e3b2b04-d541-4c26-b67d-baf012a41556','Slovakia'),
  ('3f756b20-a605-472a-84d2-a32368f21b1b','Lebanon'),
  ('4189d301-fdff-4599-996b-8364e724d265','Iceland'),
  ('439acf1a-c0ae-4361-a8b4-cbe291c992f0','Bhutan'),
  ('4487bb19-3e5a-4587-908c-10f89e1e3066','Ecuador'),
  ('45b1aa0d-87c3-451d-a6d6-99ec2ca9c968','China'),
  ('47c68e6d-39f3-49c0-9710-7ed8fa03bfc8','Gabon'),
  ('4d4399df-b57a-47f4-b7b0-9846a9c4e63c','Libya'),
  ('4e2a5085-d4eb-4e1b-94b6-68188133dac7','Angola'),
  ('518652e6-ee54-4b64-b09f-687a023fff7a','Norway'),
  ('5219dbe1-06b5-4bda-8852-6a0def4a8a02','Samoa'),
  ('52780d3e-eaf7-4a38-8159-ecc51105b174','Belarus'),
  ('52b63afa-4fac-42e5-9a9c-58cdeec39d06','Algeria'),
  ('52f03556-162b-4963-8ea1-62fe2c373ede','United States of America'),
  ('5505e303-f3a8-4283-8eba-746e49bbd18a','Venezuela'),
  ('559291e2-1c55-428e-9e96-7f08296705b0','Croatia'),
  ('58d4da44-3b79-427a-8cf6-7bfd948db739','Côte d''Ivoire'),
  ('61922228-db98-4bb4-827b-f14a9d03389e','Guinea-Bissau'),
  ('61c395b1-c112-4303-bb58-13cb5cbfc028','Botswana'),
  ('621fe799-dc26-4750-8cf3-5694f6a21658','Australia'),
  ('63ea170b-5876-483e-ac64-958846695956','Seychelles'),
  ('64027827-1b72-4e2f-9f48-3157399e6960','Papua New Guinea'),
  ('6556e683-5d1e-439b-b13c-e947213cd02c','Finland'),
  ('66270715-4c6a-483b-a967-fdc6f5759ec5','Brunei Darussalam'),
  ('662991b3-fad0-497d-8e6d-58627340e83a','Mexico'),
  ('66dca52f-62dc-4502-94d1-ba07ffb5a7fe','Kyrgyzstan'),
  ('66f6422c-6766-4ae0-b64e-044458663a66','Solomon Islands'),
  ('67ea020e-82db-442f-9cef-e94c4879f7ac','Honduras'),
  ('68f54186-a7ea-4192-a324-efd6fe9ca9a9','Sri Lanka'),
  ('6a6aadfe-316b-4dd6-b39d-11ccb4ded805','Albania'),
  ('6b5e5b92-314d-41f1-88a3-0495688db291','Gambia'),
  ('6c55225a-77ab-4511-b658-2fbf4b13010c','Monaco'),
  ('6d4958c6-1bfd-461a-b4ef-a1614e33d212','Tunisia'),
  ('6d834b3f-9ccd-4bac-aaa4-5a40278da0fd','San Marino'),
  ('6e9f7194-d55a-4dcd-8fb1-1bb19a7b66a1','Lithuania'),
  ('708df7a2-45a3-4933-bfa2-82e6afe0dc7c','Costa Rica'),
  ('7199bca8-2c83-48ad-acad-428fd440e2ad','Madagascar'),
  ('72536cd9-5ba3-4bdc-9539-fd8605525568','Trinidad and Tobago'),
  ('733bd870-f67d-4ae7-b0a5-e4d9eeec34e5','Yemen'),
  ('73d65820-0967-4bd9-b720-0e580dd85780','Kenya'),
  ('7413ddce-90b8-4693-84de-7c95d121240d','Tonga'),
  ('74495502-fe23-4c2d-b398-a7c71df56dec','Micronesia (Federated States of)'),
  ('754de15a-90bc-452e-9c8f-01a44ce2a498','Dominican Republic'),
  ('755dab47-8b51-40e1-9949-f04641911693','Singapore'),
  ('7c063d4d-d8bc-4043-b722-af5cecee2f0b','Ireland'),
  ('7c1365c6-2053-4d67-aaa5-670086b68b72','Malta'),
  ('7cf30d48-9681-4d05-9e0f-d5a8b600d22b','Montenegro'),
  ('7d12861a-0c13-442a-a9ea-565dede3ebec','Chile'),
  ('7efa9a93-0d57-452a-ab86-01bbd17e4d13','Marshall Islands'),
  ('80f56403-4453-43ea-8337-5ee892d27538','Mauritius'),
  ('8432df4a-b25a-4ecb-8a38-ceb335d21da6','Syrian Arab Republic'),
  ('8519ad09-cbf7-4fe7-b5d1-f7e6a0a584d4','Zambia'),
  ('85dc47a3-803b-4c94-9b8e-b60b05de688e','Bulgaria'),
  ('86fd0ea7-459e-4203-8b8f-3f6cdcf621e2','Cuba'),
  ('871fb990-0e16-4f24-a43b-4f1f5ea1447f','Bahrain'),
  ('87a81734-ce7f-4756-a88c-feacd9922077','Belgium'),
  ('87cec92f-5943-42f9-beef-79362ec34b79','Malaysia'),
  ('880d6400-9a44-452b-a411-cd66cb0d8cab','Belize'),
  ('88de0cac-f7e8-422f-9a2d-236f0d71dfd9','Uruguay'),
  ('8953a69a-c7e9-4b1d-bfda-b7c14eb705a8','Andorra'),
  ('8b20ca84-2230-4442-959a-5ca30114d3c5','Russian Federation'),
  ('8c9d3d33-8ee3-4522-9c1b-e47fc3c52c2b','Switzerland'),
  ('8db0e85b-1394-4793-982e-be91f8ed3120','Cabo Verde'),
  ('8e6fe4c0-8485-4025-bf7d-27899ccc3e62','Liechtenstein'),
  ('90052a7d-843b-453e-a394-bb433ad1e6db','Colombia'),
  ('90c8657c-2a8b-4879-9557-f21697977a19','Thailand'),
  ('91157102-4d48-4254-b90a-6e1352ffc2e2','Macedonia'),
  ('91c79c97-d523-455a-91ea-b149f6f3519b','Serbia'),
  ('92d5f769-02dd-4607-b32f-94f09dc0ea5f','Austria'),
  ('92e4ed47-72fb-4111-9b1a-07c3fa695055','Bangladesh'),
  ('93b2905d-251e-44bb-a1bb-34f6f228f1d6','Hungary'),
  ('9438269f-8d33-4559-9781-67f8b37f2297','Kiribati'),
  ('948d7af8-2758-4f38-b974-d3e1043a385b','Bolivia'),
  ('967c0d04-304d-4260-9cff-8c2d535bb49f','Central African Republic'),
  ('969ed033-e4ce-4e16-a19e-2195592a9298','Azerbaijan'),
  ('985dcac5-2777-4605-a47a-813ec216b469','Lao People''s Democratic Republic (Laos)'),
  ('9ae69ba1-4123-4f61-9ce5-f4e8c837081a','Niger'),
  ('9b0a2756-43b7-49b1-8bbe-17db668d400d','Qatar'),
  ('9b5b5a45-ac7e-4785-8283-47d3313297f8','Burkina Faso'),
  ('9b63c6c6-90fd-46be-b1d1-39ed7600ab13','United Republic of Tanzania'),
  ('9df5e474-cce5-408f-9d4f-c20aecd22c06','Congo'),
  ('a30f3b6e-b03d-4e3f-bbee-7da4712f8809','India'),
  ('a4e7497d-c533-4184-a795-54005ef72c92','Canada'),
  ('a77f9030-dab3-4a6f-8ef0-223f91bdc306','Comoros'),
  ('a831a9e8-cdd8-4b99-9b18-471a8323addf','Tuvalu'),
  ('a8b00a3c-eded-4b1d-9c94-d2fda9d6795c','Israel'),
  ('a9bedf91-97f9-471d-81e5-54cdcb871b87','Saint Lucia'),
  ('aca5eb78-2ed2-4bfd-b286-6108ba657ecb','Democratic Republic of the Cong'),
  ('ad2d9ca7-2a2f-463e-9492-b0d813686cef','Republic of Moldova'),
  ('ad85e410-9a7d-4347-8a4d-5af6406ff974','Barbados'),
  ('b015f521-8e2a-456f-a85f-2faec6b00024','Timor-Leste'),
  ('b4b94644-0634-4a3e-9421-dc3c661f9017','Swaziland'),
  ('b61b2206-8578-4cce-a384-69fd72139154','Egypt'),
  ('b6216347-3f58-4262-873c-6a59901a0955','Oman'),
  ('b7c3fd42-7f94-4745-a2b6-6b0fb46e3236','Malawi'),
  ('b96423a2-d4b1-4eaa-9275-cd1024d5bef5','Ethiopia'),
  ('ba1e12de-0182-4f8a-b2cf-ebee996a02fd','Antigua and Barbuda'),
  ('ba595893-87be-4e35-8ac9-7fb1f967c101','Fiji'),
  ('bc3929fd-6803-4a48-8e87-f8d7f668d952','Italy'),
  ('bc63d491-7b09-483c-998a-8f1cc200a6b8','Nauru'),
  ('bc8e3817-3a42-4d4b-970b-ec1e46e0a03c','Guatemala'),
  ('bcbd3f08-1b90-4a51-8ae4-8d68931c795a','Panama'),
  ('c203912b-734d-436a-9f9e-13317f65a175','Vanuatu'),
  ('c2c29c98-0a74-43ec-b98f-87f4b3619384','Grenada'),
  ('c2c66019-1e20-489e-a74d-e350db7bd84b','Paraguay'),
  ('c5c3f8d4-41f3-47dc-81c1-18967b3511d9','Morocco'),
  ('c67fdc65-e029-4a98-8cfc-3c88e71e2f89','South Sudan'),
  ('c6cf0b52-822a-45be-8229-4a13b7943269','Djibouti'),
  ('cbbd8867-1fbb-4438-a29e-561442c87586','Mozambique'),
  ('cc7d69b3-21e7-4117-88bf-2fd33b7bdfe3','France'),
  ('cd87e164-ecee-4072-a324-88971be7e7f0','Burundi'),
  ('cf7a304d-01fd-441a-be40-29e2303296c7','United Arab Emirates'),
  ('d077d42d-ae8a-4f34-8213-52142464091e','Cameroon'),
  ('d1b00d99-f9e2-4ec3-94ce-13a7208aca1a','Sierra Leone'),
  ('d299c5a2-4be2-44a8-8fc8-694399e31b30','Uganda'),
  ('d3cd1f99-fb8b-4d2b-8b4f-d8566bb4150b','Saint Vincent and the Grenadines'),
  ('d43c921e-9fee-4e61-b0d0-523d47287475','El Salvador'),
  ('d5921c51-8e73-49b7-98a1-a2b4a9376233','Namibia'),
  ('d5cefece-c794-4845-8a8f-7edd9d9cdfa2','Latvia'),
  ('d65b04b8-befa-4987-8866-6d4142095bfc','Jordan'),
  ('d7431fae-b970-4410-9e23-5fa987bd7772','Saint Kitts and Nevis'),
  ('d797dd55-f792-41a2-a00e-2811714b3592','Maldives'),
  ('d8dee627-60b2-4127-88cd-53e957705f58','Estonia'),
  ('d8ecc8b6-40d9-4490-9227-ee984e28dfc8','Denmark'),
  ('da221718-2e2d-4b0e-b7f4-ab4229778c1b','Senegal'),
  ('de203277-b7ba-4662-a305-0926e5f611f9','Mali'),
  ('e184ebe6-f814-4f2d-ae1e-c4d591790331','Bosnia and Herzegovina'),
  ('e274812c-01bd-478e-969c-6b35c1e867d8','Peru'),
  ('e4f7424d-6a20-4921-9d95-7a7a990566d6','Sao Tome and Principe'),
  ('e687846f-e3d9-491f-bcc9-ec27a9249c46','Slovenia'),
  ('e744e7b0-dcae-42ee-9d6e-38e2d3e68617','Tajikistan'),
  ('e7656039-d894-42ff-ab98-6d68f5910974','Armenia'),
  ('e99e783f-8af6-447a-95e8-fb341833000c','Vietnam'),
  ('e9ed110c-b3df-422f-8479-f0565322d951','Saudi Arabia'),
  ('eca155a4-9479-43df-9656-01ff47dc4f1e','Afghanistan'),
  ('ecca0919-3661-4a74-bcf0-e56f4e87fcc6','Kazakhstan'),
  ('ecf2e610-487c-4c62-9d5e-b3903e4adf1c','United Kingdom of Great Britain and Northern Ireland'),
  ('ee62db5a-7497-4e23-8fbc-26edfc735d34','Poland'),
  ('ee812f69-6ef9-4760-a66e-7af1cf26294f','Germany'),
  ('ee86d8d1-a4d3-462d-aada-0badba77399a','Indonesia'),
  ('ee8fb624-8e88-4ac9-a61b-c09a09e398de','Bahamas'),
  ('ef10bb37-ce7c-4142-a8a2-310d84ea2b5d','Czech Republic'),
  ('f00b9df0-ad13-4024-8fc1-51854f0d5a5b','Mauritania'),
  ('f035aa4a-1779-4b17-b466-7e295c4d0b4c','Dominica'),
  ('f6d11513-59bd-4ed5-8427-23029185ef05','Turkmenistan'),
  ('f90262ea-4143-4f1e-812e-3428ac0ab541','Brazil'),
  ('fe41a29a-1e0c-45b8-a804-6a7a39288d70','Myanmar');
COMMIT;

#
# Data for the `states` table  (LIMIT 0,500)
#

INSERT INTO `states` (`sta_id`, `sta_name`, `cou_id`, `sta_abbreviation`) VALUES
  ('0059146d-65ba-412b-8d3c-f5abecaa8ab5','Ohio','52f03556-162b-4963-8ea1-62fe2c373ede','OH'),
  ('016da946-46a5-4d1d-b04f-720a98db4b21','Alaska','52f03556-162b-4963-8ea1-62fe2c373ede','AK'),
  ('0a839ed1-3cfb-47d7-b5d7-2e193bb1b3dc','Michigan','52f03556-162b-4963-8ea1-62fe2c373ede','MI'),
  ('0bedbec1-5312-4180-826f-d6d1a217f0e5','New York','52f03556-162b-4963-8ea1-62fe2c373ede','NY'),
  ('121f1ace-4b6d-4c25-a0d8-47b4eb7f4874','Prince Edward Island','a4e7497d-c533-4184-a795-54005ef72c92','PE'),
  ('13cc78c3-4999-490c-850f-9986f8019977','Washington','52f03556-162b-4963-8ea1-62fe2c373ede','WA'),
  ('184c2d76-22e8-45df-a354-3ed67d7596d8','Montana','52f03556-162b-4963-8ea1-62fe2c373ede','MT'),
  ('18fa6085-25d5-4015-9610-f08bed9607a5','Missouri','52f03556-162b-4963-8ea1-62fe2c373ede','MO'),
  ('19742f90-6519-4b1d-987b-02ab3f01b361','North Dakota','52f03556-162b-4963-8ea1-62fe2c373ede','ND'),
  ('1c7dd8af-b87e-433f-a29e-ae9f60d3d21a','Wisconsin','52f03556-162b-4963-8ea1-62fe2c373ede','WI'),
  ('1fa0798b-13f7-4f98-896b-f176b632a9ec','Alberta','a4e7497d-c533-4184-a795-54005ef72c92','AB'),
  ('230b225b-e937-4169-bead-158ddb6668b3','Connecticut','52f03556-162b-4963-8ea1-62fe2c373ede','CT'),
  ('25cc549f-cf2a-40da-a15f-5b155dc495ce','Virginia','52f03556-162b-4963-8ea1-62fe2c373ede','VA'),
  ('2790a43b-4525-4bf1-a1ad-188b1b904bf2','Tennessee','52f03556-162b-4963-8ea1-62fe2c373ede','TN'),
  ('27f99adf-0eea-40b5-9922-ffbc5cd26c59','California','52f03556-162b-4963-8ea1-62fe2c373ede','CA'),
  ('33c388a0-0eaf-439a-8c72-84310f991b16','South Carolina','52f03556-162b-4963-8ea1-62fe2c373ede','SC'),
  ('343082e6-fdcc-4a01-96c6-f6d5a7e47ad1','Alabama','52f03556-162b-4963-8ea1-62fe2c373ede','AL'),
  ('3907b86c-9ffb-4f5f-8335-55f12eec5721','Oklahoma','52f03556-162b-4963-8ea1-62fe2c373ede','OK'),
  ('3f7f6d7e-f974-4082-8ad2-20f37d03ad66','Wyoming','52f03556-162b-4963-8ea1-62fe2c373ede','WY'),
  ('455daa60-8c9f-4eb7-9e20-40068dc3aaf6','Idaho','52f03556-162b-4963-8ea1-62fe2c373ede','ID'),
  ('482ed3be-a03a-4cd0-b18e-b4599c8396b5','Florida','52f03556-162b-4963-8ea1-62fe2c373ede','FL'),
  ('4f07ee6e-3a66-4548-9ec1-30f7e5a59db5','Massachusetts','52f03556-162b-4963-8ea1-62fe2c373ede','MA'),
  ('666d1bc8-d19b-44c6-9967-9a7566c5d4fc','New Mexico','52f03556-162b-4963-8ea1-62fe2c373ede','NM'),
  ('70f11c67-2e6c-49b9-b9fc-c5eed14b093a','West Virginia','52f03556-162b-4963-8ea1-62fe2c373ede','WV'),
  ('7493d682-68c6-4c7a-b4d0-f4552fd58d4b','Delaware','52f03556-162b-4963-8ea1-62fe2c373ede','DE'),
  ('762459ae-830d-4b6f-81fe-d9867273e0ab','Manitoba','a4e7497d-c533-4184-a795-54005ef72c92','MB'),
  ('77a59bc9-3ffb-4b26-be9e-a146e320bbf4','Arizona','52f03556-162b-4963-8ea1-62fe2c373ede','AZ'),
  ('87780c11-ea35-46ba-8156-3b246b1e3653','Maine','52f03556-162b-4963-8ea1-62fe2c373ede','ME'),
  ('877c5667-e311-4b82-acbf-157fdfca207e','Ontario','a4e7497d-c533-4184-a795-54005ef72c92','ON'),
  ('8aa83df6-c1ea-42d3-80db-4ea4fe6f8383','New Hampshire','52f03556-162b-4963-8ea1-62fe2c373ede','NH'),
  ('8c67b708-7da8-4428-98ba-1ba3373bf808','British Columbia','a4e7497d-c533-4184-a795-54005ef72c92','BC'),
  ('8d5a5839-090f-460b-acdb-30e9a8a9ccf0','South Dakota','52f03556-162b-4963-8ea1-62fe2c373ede','SD'),
  ('8daf6c23-4dc1-4d85-9a10-90bd8b403af9','North Carolina','52f03556-162b-4963-8ea1-62fe2c373ede','NC'),
  ('95c3c5f6-5d56-4e2f-8101-74143b884fca','Louisiana','52f03556-162b-4963-8ea1-62fe2c373ede','LA'),
  ('99830172-1e6f-41d7-af7a-37699e7eacfe','New Jersey','52f03556-162b-4963-8ea1-62fe2c373ede','NJ'),
  ('99d4239d-074e-4080-a8ba-edcf5187b1a9','Arkansas','52f03556-162b-4963-8ea1-62fe2c373ede','AR'),
  ('9fbc5b24-2621-4349-9668-f7f818c0a04d','Mississippi','52f03556-162b-4963-8ea1-62fe2c373ede','MS'),
  ('a2381d98-006b-4a03-9715-bbf6a94027e1','Kansas','52f03556-162b-4963-8ea1-62fe2c373ede','KS'),
  ('a566e738-a034-47e3-9cb9-1759cd57d220','Utah','52f03556-162b-4963-8ea1-62fe2c373ede','UT'),
  ('aa4102df-e0a3-4b33-8ceb-08ad01099fb5','Colorado','52f03556-162b-4963-8ea1-62fe2c373ede','CO'),
  ('acf26d74-7c92-408a-86e5-510a9cc4b4c9','Vermont','52f03556-162b-4963-8ea1-62fe2c373ede','VT'),
  ('b1312c31-d046-4eb7-a19d-af4fba727af7','Hawaii','52f03556-162b-4963-8ea1-62fe2c373ede','HI'),
  ('b33910c5-dde4-4fc8-8b74-db022327b2b9','Iowa','52f03556-162b-4963-8ea1-62fe2c373ede','IA'),
  ('b3f48f1c-4ba9-44df-bf6d-597c70e4fb72','Nebraska','52f03556-162b-4963-8ea1-62fe2c373ede','NE'),
  ('bb6252d2-5d54-4b36-9afe-67ddd84a1a49','Pennsylvania','52f03556-162b-4963-8ea1-62fe2c373ede','PA'),
  ('bcb5c714-63bd-4e3a-b08a-b95c4025b842','New Brunswick','a4e7497d-c533-4184-a795-54005ef72c92','NB'),
  ('bd8031bf-8620-4137-89c4-664a71fea7d5','Nova Scotia','a4e7497d-c533-4184-a795-54005ef72c92','NS'),
  ('be694c63-db7c-43aa-a3db-f0b80afe1216','Kentucky','52f03556-162b-4963-8ea1-62fe2c373ede','KY'),
  ('c7ba86b5-f8bd-4d60-8c6d-b60eaac12def','Quebec','a4e7497d-c533-4184-a795-54005ef72c92','QC'),
  ('c9fe271f-1688-4c68-bbf4-427fe5b77da4','Texas','52f03556-162b-4963-8ea1-62fe2c373ede','TX'),
  ('cdcd6ec6-bbd4-48d2-bf6d-2abc02ab2998','Illinois','52f03556-162b-4963-8ea1-62fe2c373ede','IL'),
  ('d8cae2e8-76e1-48d7-ba68-ff0adfb1ffe0','Newfoundland and Labrador','a4e7497d-c533-4184-a795-54005ef72c92','NL'),
  ('dfd8ad17-da13-4d45-b951-e7d0135039fd','Nevada','52f03556-162b-4963-8ea1-62fe2c373ede','NV'),
  ('e232d02a-0925-4960-9bda-1062ff5f81f5','Minnesota','52f03556-162b-4963-8ea1-62fe2c373ede','MN'),
  ('f3afbeef-ab13-413b-b6ed-3384430780a7','Maryland','52f03556-162b-4963-8ea1-62fe2c373ede','MD'),
  ('f887e1a9-96bb-49f7-8171-f7d517ae547f','Indiana','52f03556-162b-4963-8ea1-62fe2c373ede','IN'),
  ('fa00f05b-abd9-4b9a-82ee-ae26b9ed35c4','Oregon','52f03556-162b-4963-8ea1-62fe2c373ede','OR'),
  ('fa533368-c564-4bc1-b240-d99643f684ee','Rhode Island','52f03556-162b-4963-8ea1-62fe2c373ede','RI'),
  ('fc3b11a8-1c5c-47bd-9b52-da3e78db3a0a','Saskatchewan','a4e7497d-c533-4184-a795-54005ef72c92','SK'),
  ('fc80b232-9c94-49fa-8bb7-bec97abd848a','Georgia','52f03556-162b-4963-8ea1-62fe2c373ede','GA');
COMMIT;

#
# Data for the `category` table  (LIMIT 0,500)
#

INSERT INTO `category` (`cat_id`, `cat_name`, `cat_state`, `cat_created_date`, `cat_update_date`, `use_id_created`, `use_id_updated`) VALUES
  ('2','IT Issues',1,'2016-03-22 14:08:52.400000','2016-03-31 09:32:53.484000','1','1'),
  ('3','Missing Hours',1,'2016-03-22 14:08:54.660000','2016-03-28 20:34:05.554000','1','1');
COMMIT;

#
# Data for the `configuration_email` table  (LIMIT 0,500)
#

INSERT INTO `configuration_email` (`ema_id`, `ema_type`, `ema_subject`, `ema_bcc`, `ema_html`, `ema_content`, `ema_to`, `ema_update_date`, `use_id_updated`) VALUES
  ('1428fc66-82ac-4c06-af6e-9551ba3d34d1','W','Welcome email','','1','<br>','','2016-02-17 10:05:24','1'),
  ('86f68528-4dcc-4545-8106-3f212151c3e8','F','Finance email','','1','<br>','ifernandez@greathealthworks.com','2016-02-17 10:05:25','1'),
  ('f0878974-a215-4bab-adde-75f67902dd5c','I','Invoice email','','1','<br>','ifernandez@greathealthworks.com','2016-02-17 06:04:11',NULL);
COMMIT;

#
# Data for the `configuration_general` table  (LIMIT 0,500)
#

INSERT INTO `configuration_general` (`con_id`, `con_ldap_authenti`, `con_ldap_server`, `con_ldap_port`, `con_ldap_dn`, `con_ldap_dn_ibo`, `con_ldap_user`, `con_ldap_pass`, `con_ldap_ssl`, `con_ldap_sec_type`, `con_last_pipkins`, `con_update_date`, `use_id_updated`, `con_path_pipkins`, `con_path_oracle`, `con_retention`, `con_email_host`, `con_email_port`, `con_email_protocol`, `con_email_from`, `con_email_auth`, `con_email_max_size`, `con_ldap_dn_sme`, `con_email_system`, `con_email_user`, `con_email_pass`) VALUES
  ('7a7970d7-6684-42c1-8f9f-ecd13fac5a1f','sAMAccountName','172.16.0.22','389','DC=testAd,DC=com','OU=Homebase Agents,DC=testAd,DC=com','ifernandez@testad.com','DkMRx6IzP71gRYoyqvgtwS+TM9vMv6xf','0','simple',NULL,'2016-04-02 16:40:34','1','tyurt','\\\\Ghwlaptop6910\\oraclefolder',20,'smtp.greathealthworks.com',25,'smtp','ifernandez@greathealthworks.com','0',2,'OU=Performance Advisors,OU=Great VirtualWorks,DC=testAd,DC=com','http://localhost:8082/ims','','5hlWGjmbPvrO5JEF7Bvfhw==');
COMMIT;

#
# Data for the `configuration_system` table  (LIMIT 0,500)
#

INSERT INTO `configuration_system` (`sys_id`, `sys_admin_fee`, `sys_type_year`, `sys_begin_month`, `sys_invoice_frecuency`, `sys_day_pay`, `use_id_updated`, `sys_update_date`, `sys_buffer`, `sys_color`, `sys_font_color`, `sys_decimal_place`, `sys_date_start`, `sys_date_end`, `sys_total_submit`) VALUES
  ('65c5fdaa-2d3e-43c5-adc9-cd6a03350224',8.950000,'C',1,'W','M','1','2016-04-02 16:40:40',30,'f5c7c7','5e485e',2,'1970-01-06 23:00:00.000000','1970-01-07 23:00:00.000000',2);
COMMIT;

#
# Data for the `invoice_state` table  (LIMIT 0,500)
#

INSERT INTO `invoice_state` (`ivt_id`, `ivt_name`) VALUES
  ('1','Pending'),
  ('2','Submitted'),
  ('3','Approved'),
  ('4','Canceled');
COMMIT;

#
# Data for the `skill` table  (LIMIT 0,500)
#

INSERT INTO `skill` (`ski_id`, `ski_id_pip`, `ski_name`, `cli_id`) VALUES
  ('05731778-a1ad-4c33-a440-e85cf2a64a56',NULL,'Morgan & Morgan InvoKana',NULL),
  ('069b0e79-6609-440f-afa4-e8d92dee2db9',NULL,'All Other SP',NULL),
  ('0a7947ca-5553-4674-b634-64508ddc4abe',NULL,'Volkswagen',NULL),
  ('0cfb404c-8bca-407e-88f7-37d180d32405',NULL,'Morgan & Morgan Volkswagen',NULL),
  ('1','1','Morgan Morgan',NULL),
  ('13b41dbc-6275-49dd-93c2-9fd7c5694b79',NULL,'Recovery',NULL),
  ('13dc4365-1d04-4074-aca4-a283e483ce5d',NULL,'TRAINING_Live Person Agent (Customer Service)',NULL),
  ('1c52219a-a76e-41cc-940f-ec5a6a5e74ae',NULL,'Cancellations EN',NULL),
  ('21','21','jjjjj',NULL),
  ('22','2','Probiotic',NULL),
  ('2336e780-8b94-4065-97b5-4aa45c1ff947',NULL,'Edu2 EN',NULL),
  ('27f63937-c228-46e4-ab66-184b3993be09',NULL,'TRAINING_Edu2 EN',NULL),
  ('29fba0bc-744a-4001-a647-b8085ab3bb91',NULL,'TRAINING_Morgan & Morgan Xarelto',NULL),
  ('3','3','Otro ma',NULL),
  ('30929458-fd0b-4580-b4af-2128ea760eeb',NULL,'Probiotic XL',NULL),
  ('30a76df4-52a9-4de1-9a25-c2502a6e71de',NULL,'Live Person Manager',NULL),
  ('4','4','El ultimo',NULL),
  ('444d3b9b-a2a3-4e9e-97d2-3ed2552ef4d2',NULL,'TRAINING_Morgan & Morgan Mass Tort',NULL),
  ('4c72704b-c714-406b-bc13-f01604e11edb',NULL,'TRAINING_Morgan & Morgan_Labor Dispute',NULL),
  ('4d344af0-96fc-4dfd-933d-619083b16e73',NULL,'Morgan & Morgan Mass Tort',NULL),
  ('5c69b9d9-7717-413d-b060-278fc4231551',NULL,'TRAINING_Morgan & Morgan InvoKana',NULL),
  ('5dc36ac0-168c-46e8-be0e-89f163dfbe9d',NULL,'QA',NULL),
  ('6','ljkkljk','lkkljlkj',NULL),
  ('692d72c8-f440-41fe-aec7-dea74fe871f7',NULL,'Inbound/InboundSalesSP',NULL),
  ('7c4afea5-4ed3-4f34-bc04-784bac688df1',NULL,'TRAINING_Morgan & Morgan_Consumer Protection',NULL),
  ('7cc2f12f-0517-43b3-bcf2-2a409ae7a520',NULL,'Validation IBO',NULL),
  ('81f73037-834a-42db-83fd-e2926b171475',NULL,'Live Person Agent (Customer Service)  ',NULL),
  ('89303d90-256d-4c9a-8d64-60ca252391cf',NULL,'Live Person Agent (Sales)',NULL),
  ('8e7e4ef5-b9cc-4fb7-aa0b-5e6cb03eaa34',NULL,'Morgan & Morgan Xarelto',NULL),
  ('9','lkj','lkj',NULL),
  ('96d7fbd7-7abf-4d86-b197-867d07a76d4a',NULL,'TRAINING_Live Person Agent (Sales)',NULL),
  ('98','kljlk','ljklkjlk11111',NULL),
  ('a30d97b1-2578-4441-8df5-6ca7c6ae7826',NULL,'All Other EN',NULL),
  ('aa2f7b56-5b75-44e4-a2ab-98c983f2c214',NULL,'GVWTRAINING_Inbound Sales',NULL),
  ('b89439e6-7fd1-4e46-b0a3-1fd16f8996d2',NULL,'EduEN1',NULL),
  ('bf78280f-0d8b-4e54-b16b-08fa861f34c0',NULL,'TRAINING_Validation IBO',NULL),
  ('cb77c34d-a2c0-42e2-8ea1-23a30b8b8cc7',NULL,'Morgan & Morgan_Labor Dispute',NULL),
  ('cdbc67ce-0734-4e56-963b-62bfccf1431a',NULL,'Morgan & Morgan_Consumer Protection',NULL),
  ('d95d5f2b-e808-4436-8405-4cc90bb234b9',NULL,'Inbound/InboundSalesEN',NULL),
  ('e06576ed-053b-48d9-beff-92f15a000402',NULL,'TRAINING_Recovery',NULL),
  ('ecbd9469-14df-42a0-b9ce-1fb654c32f37',NULL,'REIthatworks',NULL),
  ('f0672cbc-4190-4b7e-aaa4-c82b50443abd',NULL,'TRAINING_Probiotic XL',NULL),
  ('f5d3567c-24ce-4277-9ac2-26f17b384875',NULL,'TRAINING_Edu1 EN',NULL),
  ('fcee0957-0c6d-403c-9811-605335bc90bd',NULL,'Cancellations SP',NULL),
  ('kk','kj','kl',NULL),
  ('s','s','s',NULL);
COMMIT;

#
# Data for the `menu` table  (LIMIT 0,500)
#

INSERT INTO `menu` (`men_id`, `men_descripcion`, `men_action`, `men_action_listener`, `men_father`) VALUES
  ('1','configuration',NULL,NULL,NULL),
  ('10','groups','groups',NULL,'8'),
  ('11','client_applications','client_application',NULL,'8'),
  ('12','users','user',NULL,'8'),
  ('13','on_boarding','onboarding',NULL,'8'),
  ('14','current_invoice','current_invoice',NULL,NULL),
  ('15','invoices','invoices',NULL,'8'),
  ('16','message_board','message_board',NULL,'8'),
  ('17','invoices_history','invoices_history',NULL,NULL),
  ('4','general_conf','general_configuration',NULL,'1'),
  ('5','system_conf','system_configuration',NULL,'1'),
  ('51','roles','rol',NULL,'1'),
  ('6','email_conf','email_configuration',NULL,'1'),
  ('7','thresholds','threshold',NULL,'1'),
  ('71','category','category',NULL,'1'),
  ('72','logsystem','logsystem',NULL,'1'),
  ('8','maintenance',NULL,NULL,NULL);
COMMIT;

#
# Data for the `permissions` table  (LIMIT 0,500)
#

INSERT INTO `permissions` (`per_id`, `per_name`, `per_description`, `per_const`) VALUES
  ('1','User Maintenance',NULL,'ROL_USER'),
  ('10','On Boarding',NULL,'ROL_BOARD'),
  ('11','Current Invoice Maintenance',NULL,'ROL_CURRENT_INV'),
  ('12','Invoices Maintenance',NULL,'ROL_INVOICES'),
  ('13','Message Board Maintenance',NULL,'ROL_MESSAGE'),
  ('14','Invoices History Maintenance',NULL,'ROL_INVOICES_HIST'),
  ('15','Hours Category Maintenance',NULL,'ROL_CATEGORY'),
  ('2','Role Maintenance',NULL,'ROL_ROL'),
  ('3','Log System',NULL,'ROL_LOG'),
  ('4','Configuration General Maintenance',NULL,'ROL_CONF_GN'),
  ('5','Email Maintenance',NULL,'ROL_EMAIL'),
  ('6','System Configuration Maintenance',NULL,'ROL_CONF_SYS'),
  ('7','Thresholds Maintenance',NULL,'ROL_THRES'),
  ('8','Client Application Maintenance',NULL,'ROL_CA'),
  ('9','Groups Maintenance',NULL,'ROL_GROUP');
COMMIT;

#
# Data for the `permissions_menu` table  (LIMIT 0,500)
#

INSERT INTO `permissions_menu` (`pme_id`, `per_id`, `men_id`) VALUES
  ('1','3','72'),
  ('10','10','13'),
  ('11','11','14'),
  ('12','12','15'),
  ('13','13','16'),
  ('14','14','17'),
  ('15','15','71'),
  ('2','2','51'),
  ('3','4','4'),
  ('4','5','6'),
  ('5','6','5'),
  ('6','7','7'),
  ('7','8','11'),
  ('8','9','10'),
  ('9','1','12');
COMMIT;

#
# Data for the `report_group` table  (LIMIT 0,500)
#

INSERT INTO `report_group` (`gro_id`, `gro_description`, `gro_class_service`, `gro_filtro_bean`) VALUES
  ('1','Log system','logSystemService','logSystemFilter'),
  ('10','Message Board','messageBoardService','messageBoardFilter'),
  ('2','Role','rolService','filterBase'),
  ('3','Groups','groupsService','filterBase'),
  ('4','Client application','clientApplicationService','filterBase'),
  ('5','User','userService','userFilter'),
  ('6','On boarding','userUtilService','filterBase'),
  ('7','Invoices','invoiceService','invoiceFilter'),
  ('8','Invoices list','invoiceService','invoiceFilter'),
  ('9','Invoices history','invoiceService','invoiceFilter');
COMMIT;

#
# Data for the `report` table  (LIMIT 0,500)
#

INSERT INTO `report` (`rep_id`, `rep_name`, `rep_description`, `rep_method`, `rep_defaults`, `rep_jrxml`, `gro_id`) VALUES
  ('1','Log system',NULL,NULL,1,'logsystem','1'),
  ('10','Message Board','Message Board',NULL,1,'message_board','10'),
  ('2','Rol',NULL,NULL,1,'rol','2'),
  ('3','Groups',NULL,NULL,1,'groups','3'),
  ('4','Client application',NULL,NULL,1,'clientapplication','4'),
  ('5','User',NULL,NULL,1,'users','5'),
  ('6','On boarding',NULL,NULL,1,'onboard','6'),
  ('7','Invoices',NULL,NULL,2,'invoice','7'),
  ('8','Invoices list','Invoices list',NULL,1,'invoice_list','8'),
  ('9','Invoices history','Invoices history',NULL,1,'invoice_history','9');
COMMIT;

#
# Data for the `roles_permissions` table  (LIMIT 0,500)
#

INSERT INTO `roles_permissions` (`rop_id`, `per_id`, `rol_id`, `rop_access`) VALUES
  ('0a31857f-6e55-4cbe-8d7d-b11d9b5b742b','2','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('14d9b358-eca0-4bf2-999b-516dec724141','1','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('284e62b3-de5c-43e4-82c7-eb0de4fac0c9','9','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('383766c5-e0c9-41f9-bc4a-add28b081cb8','12','07b2f8e8-05ed-4176-967f-95db9d847832','RAMC'),
  ('3e0f0bb9-6a3a-429f-a3d7-e12b4cb82e46','9','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('3f39652f-381d-4dca-9ac0-1cb5fafbf581','10','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('4104c335-649d-440a-a3f4-29741761d4dd','12','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('453a3d07-df2e-4f9a-aa2b-979178d8abb0','7','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('4f13d22f-2024-4f32-83c3-e91fefca5148','11','4556ed01-4f92-42f9-8d65-b55e5a0d1b12','RM'),
  ('59f585a5-da34-45cd-acdc-3d3159dbf6b7','8','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('6cf6fe34-e606-4720-a56d-a967c35947c9','2','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('6e801649-6250-4e7f-9adb-a90f29958b5d','7','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('757ce3bc-7800-4fe1-9943-a5f8a4660115','14','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('7a0b7602-f6e0-43dc-86ed-1b264f6d2c75','4','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('7a13de77-c72d-4bfe-8154-586a353c1f4e','15','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('8b043f51-b6df-4441-8e0e-a9d930838d0d','13','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('8b1835a6-6b24-4cee-873a-cf4038cdfa91','15','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('8ce25bc7-fd3d-4fba-97b4-0391834335d9','5','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('95d24e1d-48b2-4e2d-b2ee-e7c192291851','14','4556ed01-4f92-42f9-8d65-b55e5a0d1b12','R'),
  ('a0203b47-ceea-4d31-9f43-a2cb8a9e5da2','11','07b2f8e8-05ed-4176-967f-95db9d847832','RAMC'),
  ('a312247b-bfee-4887-8c11-3fdc2b03ce75','13','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('a49d45ef-1b6c-4b00-9e15-a942a1f6db41','11','a6cdfc9f-5c35-434d-ba1d-53810562d2f3','RAMC'),
  ('ae020a89-2f70-4447-891a-9c724b0c055a','8','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('b00e9a33-499a-4e77-a379-98bb5f114ab8','10','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('b05ed27f-3084-46d3-a0fa-fbf97b5623a0','12','a6cdfc9f-5c35-434d-ba1d-53810562d2f3','RAMC'),
  ('b64fe7d7-fe05-4ad2-9187-b430968915ee','6','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('c1147e91-be9f-47ee-9de2-57f3d6b627d8','4','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('c49f2330-9a15-47b5-9eaf-86db9eb8577f','11','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('c7b7e99e-9158-44f5-8e35-670bcc8ad3db','5','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('cd51ccad-df96-42e0-b036-fa31fa19fd32','1','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('d52b2a99-6166-47a1-94df-f7ea1f7dd02b','14','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('e3aa2406-8161-4bb4-a346-c15beda82f16','11','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('e499727a-169d-4555-b781-7e2a8bf7721e','6','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R'),
  ('ea3e1f66-85b6-4a25-acd6-c5685a38d6a0','3','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('ec1e138c-56fe-4eb6-a0a3-b9025e460e56','12','841fba6b-4ae3-4c97-9921-d3175ea35b55','RAMC'),
  ('fc58117d-a3aa-4734-9016-a5c7e93418be','3','2fff203d-bdd3-4128-b2dd-e8ba88cebde6','R');
COMMIT;

#
# Data for the `sequence_data` table  (LIMIT 0,500)
#

INSERT INTO `sequence_data` (`sequence_name`, `sequence_increment`, `sequence_min_value`, `sequence_max_value`, `sequence_cur_value`, `sequence_cycle`) VALUES
  ('sq_ibo_number',1,1,18446744073709551615,1,0),
  ('sq_invoice_id',1,1,18446744073709551615,1,0),
  ('sq_supplier_bank',1,1,18446744073709551615,1,0),
  ('sq_supplier_number',1,1,18446744073709551615,1,0);
COMMIT;

#
# Data for the `skill_phone_system` table  (LIMIT 0,500)
#

INSERT INTO `skill_phone_system` (`sps_id`, `sps_name`, `cli_id`, `sps_place`, `sps_phone_system`) VALUES
  ('006831ac-0e7c-44e3-9b2a-62f2877ef225','PBXL_Sales_EN','30929458-fd0b-4580-b4af-2128ea760eeb','Cisco',291),
  ('00c7cb51-4275-44ec-88bc-6a6389c7364b','Training Volkswagen','0a7947ca-5553-4674-b634-64508ddc4abe','inContact',357448),
  ('0225df7f-0a97-408d-9ffe-dc4f09e5dc8e','AllOthersSP','069b0e79-6609-440f-afa4-e8d92dee2db9','Cisco',157),
  ('02e27e4c-dd96-478e-acc2-d156a366e3f6','CancellationsSp','fcee0957-0c6d-403c-9811-605335bc90bd','Cisco',155),
  ('0747b7c3-4ceb-4577-a4dd-0a018c5f7cdc','TRAINING_InboundSales','aa2f7b56-5b75-44e4-a2ab-98c983f2c214','Cisco',77),
  ('0bfcdbe5-5de9-40d4-88d5-8e75c39fda14','Training Xarelto','29fba0bc-744a-4001-a647-b8085ab3bb91','inContact',357449),
  ('11371d3e-aaee-46f2-9edc-c939c4b88b91','Morgan and Morgan Volkswagen','0cfb404c-8bca-407e-88f7-37d180d32405','inContact',357362),
  ('17dfe0d0-890a-4daf-9e30-9f806fe84bea','Morgan and Morgan Xarelto','8e7e4ef5-b9cc-4fb7-aa0b-5e6cb03eaa34','inContact',357362),
  ('1f9cb5e6-0dd8-4a8d-9899-9f3c75979351','M and M Xarelto','8e7e4ef5-b9cc-4fb7-aa0b-5e6cb03eaa34','Cisco',260),
  ('21ee887b-5365-45c5-bcf1-354c98ad171b','EduEN1','b89439e6-7fd1-4e46-b0a3-1fd16f8996d2','Cisco',92),
  ('289b6576-323d-467a-a532-3f6eacb7cecc','M and M Mass Tort','4d344af0-96fc-4dfd-933d-619083b16e73','Cisco',261),
  ('2a2d19fe-f635-4e55-a4da-7763a61bdb9f','OXL Inbound Sales Spa','692d72c8-f440-41fe-aec7-dea74fe871f7','inContact',329164),
  ('2a2d47e7-c1b7-4f46-ad3d-4346a12a85d6','OXL All Others Spa','069b0e79-6609-440f-afa4-e8d92dee2db9','inContact',329186),
  ('2b2087c0-64ce-4775-a261-c5c0bc62e919','M and M Volkswagen','0cfb404c-8bca-407e-88f7-37d180d32405','Cisco',258),
  ('2eb5229e-1020-4085-a8f6-94134f3d1f8f','Training MM InvoKana','5c69b9d9-7717-413d-b060-278fc4231551','inContact',357446),
  ('30134f99-870a-41cd-8604-d9d8cc5ae53c','Morgan and Morgan InvoKana','05731778-a1ad-4c33-a440-e85cf2a64a56','inContact',357361),
  ('32a5163d-dfd1-4ce5-a3da-2f7664c63fa9','OXL Cancellations Eng','1c52219a-a76e-41cc-940f-ec5a6a5e74ae','inContact',329179),
  ('34ba6313-ab0a-4d91-aa10-0ec8ce7b644d','Training MM Mass Tort','444d3b9b-a2a3-4e9e-97d2-3ed2552ef4d2','inContact',357447),
  ('3a6f54ca-8634-4a1a-b732-635a3c3c3b03','CancellationsEN','1c52219a-a76e-41cc-940f-ec5a6a5e74ae','Cisco',150),
  ('3beb32a3-1459-4bd2-a6d7-9323b4df826f','Recovery','13b41dbc-6275-49dd-93c2-9fd7c5694b79','Cisco',5),
  ('3f89846e-b1d0-40e2-99ae-04c0222ca6b9','Training Sales','aa2f7b56-5b75-44e4-a2ab-98c983f2c214','inContact',347217),
  ('426ad557-16cd-44b7-9c80-5b4e92791cb9','Training-ProbioticXL Sales EN','f0672cbc-4190-4b7e-aaa4-c82b50443abd','inContact',358001),
  ('4961fdec-9436-4a84-8b88-1191ae17ce94','Training-ProbioticXL Sales SP','f0672cbc-4190-4b7e-aaa4-c82b50443abd','inContact',358002),
  ('50a042c3-1379-40e3-ba4e-6684c18d4742','Training EDU1','27f63937-c228-46e4-ab66-184b3993be09','inContact',363281),
  ('5d448b54-6f10-4c54-b342-ad54ab8f0599','EduEN2','2336e780-8b94-4065-97b5-4aa45c1ff947','Cisco',32),
  ('64e0a3d3-0839-4c29-8e47-1196efb1f6ab','AllOthersEn','a30d97b1-2578-4441-8df5-6ca7c6ae7826','Cisco',152),
  ('689281d3-a8bf-402a-928b-b175f668092d','InBound','d95d5f2b-e808-4436-8405-4cc90bb234b9','Cisco',1),
  ('79453f4c-65f1-409b-a4ba-d4f9db471649','Training EDU','f5d3567c-24ce-4277-9ac2-26f17b384875','inContact',363288),
  ('81cceb0a-e801-4ded-9965-42d26d259278','Training_EDU1','f5d3567c-24ce-4277-9ac2-26f17b384875','Cisco',243),
  ('82e72285-a8fe-46bf-8e41-bf098e166ed6','OXL All Others Eng','a30d97b1-2578-4441-8df5-6ca7c6ae7826','inContact',329185),
  ('850656d9-5333-4748-9661-3b27d7930c39','ProbioticXL Sales SP','30929458-fd0b-4580-b4af-2128ea760eeb','inContact',358000),
  ('9207aaff-b5c5-4c81-9f93-eeccd9e0fb3c','OXL EDU2 English','2336e780-8b94-4065-97b5-4aa45c1ff947','inContact',329210),
  ('9687061d-b02e-43ae-bf8d-bf5a378de3a0','ProbioticXL Sales EN','30929458-fd0b-4580-b4af-2128ea760eeb','inContact',357999),
  ('9ba6d123-8aea-41c1-b958-5cc6bd873bb8','InboundSalesSP','692d72c8-f440-41fe-aec7-dea74fe871f7','Cisco',17),
  ('9e9ef157-19ab-4bd4-9bc2-d4b3d4389c4e','Morgan and Morgan Mass Tort','4d344af0-96fc-4dfd-933d-619083b16e73','inContact',347219),
  ('cc8045bf-acb2-4dfd-89b8-4acb54483bcf','Training_EDU2','27f63937-c228-46e4-ab66-184b3993be09','Cisco',244),
  ('cf8e348e-15c6-42b1-8f0d-1bb11ebdb7f3','Morgan and Morgan TCPA','cdbc67ce-0734-4e56-963b-62bfccf1431a','inContact',357832),
  ('d20fc976-1ba4-4b7f-a83a-8a965aca20aa','OXL Cancellations Spa','fcee0957-0c6d-403c-9811-605335bc90bd','inContact',329180),
  ('d67854c8-b2f0-48f5-9e19-0c3223017c0f','OXL EDU1 English','b89439e6-7fd1-4e46-b0a3-1fd16f8996d2','inContact',329199),
  ('ddac77bf-a0c6-4196-af54-9e8fddf3be29','M and M Invokana','05731778-a1ad-4c33-a440-e85cf2a64a56','Cisco',259),
  ('e20784db-4f60-4aa4-a3c2-3338c7f8fe61','Training M and M TCPA','7c4afea5-4ed3-4f34-bc04-784bac688df1','inContact',357945),
  ('eb7ba6aa-1536-4ed8-9fc4-59db63d97d8e','Morgan and Morgan LaborDispute','cb77c34d-a2c0-42e2-8ea1-23a30b8b8cc7','inContact',357831),
  ('f4060fa9-bfa4-4aa0-98a0-4d03ca5794db','OXL Inbound Sales Eng','d95d5f2b-e808-4436-8405-4cc90bb234b9','inContact',329163),
  ('fc22861d-d064-49af-81fc-0facc2a0a0ce','Training M and M Labor Dispute','4c72704b-c714-406b-bc13-f01604e11edb','inContact',357946),
  ('sadasdasdgdh','Morgan Incontact','1','Cisco',123663),
  ('sdfsdf','Morgan Cisco','1','inContact',5454);
COMMIT;

#
# Data for the `thresholds` table  (LIMIT 0,500)
#

INSERT INTO `thresholds` (`thr_id`, `thr_metric`, `thr_min`, `thr_max`, `thr_update_date`, `use_id_updated`, `thr_description`, `thr_color`, `thr_font_color`) VALUES
  ('1','Service Revenue',0,50000,'2016-03-08 10:21:59','1','dfgsdfgsdfg','bf1caf','f0b30c'),
  ('2','Incentive Revenue',0,50000,'2016-03-08 08:17:20',NULL,'Incentive incorrect','ba4848','ffffff'),
  ('3','Total Revenue',0,1000000,'2016-03-08 08:17:08',NULL,'Total revenue incorrect','242023','ffffff'),
  ('4','Total Hours',0,55,'2016-03-08 08:17:16','1','Total hours incorrect','bcbcf7','000000'),
  ('5','0 Hours Qty. with Pay ',0,0,'2016-03-26 13:52:36',NULL,'IBO''s that are showing receiving payment with no hours','f59d9d','0d0c0d'),
  ('6','0 Pay with Hours Qty',0,0,'2016-03-26 13:52:36',NULL,'IBO''s with hours and no payment','ffb2b2','0a090a'),
  ('8','%NR ',0,3,'2016-02-25 17:52:56',NULL,'desc','166e06','ffffff');
COMMIT;



DELIMITER $$

CREATE DEFINER = 'root'@'%' TRIGGER `_before_ins_tr1` BEFORE INSERT ON `bank_information`
  FOR EACH ROW
BEGIN

END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'root'@'%' TRIGGER `incentive_after_ins` AFTER INSERT ON `incentive`
  FOR EACH ROW
BEGIN

/*update the fields in invoice when add some work*/
update invoice i
set inv_import_total = i.inv_import_total + NEW.inc_amount,
    inv_total_incentive = i.inv_total_incentive + NEW.inc_amount
where i.inv_id = NEW.inv_id;

END$$

CREATE DEFINER = 'root'@'%' TRIGGER `incentive_before_upd` BEFORE UPDATE ON `incentive`
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

CREATE DEFINER = 'root'@'%' TRIGGER `invoice_before_upd` BEFORE UPDATE ON `invoice`
  FOR EACH ROW
BEGIN

if(OLD.inv_admin_fee != NEW.inv_admin_fee)then
                     SET NEW.inv_import_total = (OLD.inv_import_total + OLD.inv_admin_fee - NEW.inv_admin_fee);
end if;

END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'root'@'%' TRIGGER `invoice_hours_added_after_ins` AFTER INSERT ON `invoice_hours_added`
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

CREATE DEFINER = 'root'@'%' TRIGGER `invoice_hours_added_before_upd` BEFORE UPDATE ON `invoice_hours_added`
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

CREATE DEFINER = 'root'@'%' TRIGGER `invoice_work_after_ins` AFTER INSERT ON `invoice_work`
  FOR EACH ROW
BEGIN

update invoice i
set i.inv_actual_service = i.inv_actual_service + (NEW.iwo_actual_service/3600),
    i.inv_service_revenue = i.inv_service_revenue + NEW.iwo_service_revenue,
    i.inv_import_total = i.inv_import_total + NEW.iwo_service_revenue
where i.inv_id = NEW.inv_id;

END$$

CREATE DEFINER = 'root'@'%' TRIGGER `invoice_work_before_upd` BEFORE UPDATE ON `invoice_work`
  FOR EACH ROW
BEGIN

DECLARE newServiceRevenue DECIMAL(19,6);
DECLARE hoursAdded DECIMAL(11,6);

/*if rate change update import total and service revenue in invoice*/
if OLD.iwo_amount !=  NEW.iwo_amount then
   SET newServiceRevenue = (NEW.iwo_actual_service/3600) * NEW.iwo_amount;

   /*search the hours added*/
   select IFNULL(SUM(h.iho_hours),0) INTO hoursAdded from invoice_hours_added h where h.iwo_id = NEW.iwo_id;


   /*update the invoice*/
   update invoice i
    set i.inv_import_total = i.inv_import_total - OLD.iwo_service_revenue - ( OLD.iwo_amount * hoursAdded) + ( NEW.iwo_amount * hoursAdded) + newServiceRevenue,
        i.inv_service_revenue = i.inv_service_revenue - OLD.iwo_service_revenue + newServiceRevenue
    where i.inv_id = NEW.inv_id;


   SET NEW.iwo_service_revenue = newServiceRevenue;
end if;

END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'root'@'%' TRIGGER `permissions_before_ins_tr1` BEFORE INSERT ON `permissions`
  FOR EACH ROW
BEGIN

END$$

DELIMITER ;

DELIMITER $$

CREATE DEFINER = 'root'@'%' TRIGGER `profile_before_ins_tr1` BEFORE INSERT ON `profile`
  FOR EACH ROW
BEGIN
DECLARE nextId BIGINT;
DECLARE yearString VARCHAR(2);
DECLARE number CHAR(5);

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

/*The IBO format number GVH+YEAR+00000*/
SET new.pro_number = CONCAT('GVW',yearString, number);

END$$

DELIMITER ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;