--// create users table
-- Migration SQL that makes the change goes here.


--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_user_id` int(11) DEFAULT NULL,
  `email` varchar(128) NOT NULL,
  `first_name` varchar(64) DEFAULT NULL,
  `last_name` varchar(64) DEFAULT NULL,
  `encrypted_password` varchar(256) DEFAULT NULL,
  `salt` varchar(128) DEFAULT NULL,
  `verification_code` varchar(64) DEFAULT NULL,
  `expiration_at` datetime NOT NULL,
  `account_enabled` tinyint(1) NOT NULL DEFAULT '0',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `last_login_at` datetime DEFAULT NULL,
  `failed_login_attempts` tinyint(4) NOT NULL DEFAULT '0',
  `accepted_terms` tinyint(1) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `main_phone` varchar(30) DEFAULT NULL,
  `validated_email` tinyint(1) NOT NULL DEFAULT '0',
  `validated_phone` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=58 ;


--//@UNDO
-- SQL to undo the change goes here.

DROP TABLE `users`;

