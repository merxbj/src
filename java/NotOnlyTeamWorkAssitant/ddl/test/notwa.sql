/*
Navicat MySQL Data Transfer

Source Server         : integri
Source Server Version : 50137
Source Host           : 192.168.102.11:3306
Source Database       : notwa

Target Server Type    : MYSQL
Target Server Version : 50137
File Encoding         : 65001

Date: 2010-03-14 00:35:37
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `project`
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `project_id` decimal(19,0) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES ('1', 'notwa');
INSERT INTO `project` VALUES ('2', 'notwa fake');
INSERT INTO `project` VALUES ('3', 'person');
INSERT INTO `project` VALUES ('4', 'general');

-- ----------------------------
-- Table structure for `project_user_assigment`
-- ----------------------------
DROP TABLE IF EXISTS `project_user_assigment`;
CREATE TABLE `project_user_assigment` (
  `project_id` decimal(19,0) NOT NULL,
  `user_id` decimal(19,0) NOT NULL,
  KEY `FK_Project_User_Assigment_User_user_id` (`user_id`),
  KEY `FK_Project_User_Assigment_Project_project_id` (`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_user_assigment
-- ----------------------------
INSERT INTO `project_user_assigment` VALUES ('1', '1');
INSERT INTO `project_user_assigment` VALUES ('1', '2');
INSERT INTO `project_user_assigment` VALUES ('2', '3');
INSERT INTO `project_user_assigment` VALUES ('2', '4');
INSERT INTO `project_user_assigment` VALUES ('3', '1');
INSERT INTO `project_user_assigment` VALUES ('4', '1');
INSERT INTO `project_user_assigment` VALUES ('4', '2');
INSERT INTO `project_user_assigment` VALUES ('4', '3');
INSERT INTO `project_user_assigment` VALUES ('4', '4');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` decimal(19,0) NOT NULL,
  `login` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_nam` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'mrneo', 'aaaa', 'Tom', 'St');
INSERT INTO `user` VALUES ('2', 'eter', 'bbbb', 'Je', 'mex');
INSERT INTO `user` VALUES ('3', 'michal', 'mmmmm', 'Mi', 'Ne');
INSERT INTO `user` VALUES ('4', 'new1', 'new2', 'User1', 'Last2');

-- ----------------------------
-- Table structure for `work_item`
-- ----------------------------
DROP TABLE IF EXISTS `work_item`;
CREATE TABLE `work_item` (
  `work_item_id` decimal(19,0) NOT NULL,
  `assigned_user_id` decimal(19,0) DEFAULT NULL,
  `project_id` decimal(19,0) NOT NULL,
  `parent_work_item_id` decimal(19,0) DEFAULT NULL,
  `subject` varchar(255) NOT NULL,
  `status` smallint(6) NOT NULL,
  `working_priority` smallint(6) NOT NULL,
  `description` text,
  `expected_timestamp` datetime DEFAULT NULL,
  `last_modified_timestamp` datetime NOT NULL,
  PRIMARY KEY (`work_item_id`),
  KEY `FK_Work_Item_Project_assigned_project_id` (`assigned_user_id`),
  KEY `FK_Work_Item_User_parent_work_item_id` (`parent_work_item_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of work_item
-- ----------------------------
INSERT INTO `work_item` VALUES ('1', '1', '1', null, 'GUI', '3', '2', 'Implementation of GUI package', null, '2010-03-01 00:22:17');
INSERT INTO `work_item` VALUES ('2', '1', '1', '1', 'GUI - Show/Hide Button', '3', '2', 'On click shows or hide Workitem detail', null, '2010-03-14 00:22:27');
INSERT INTO `work_item` VALUES ('3', '1', '1', '1', 'GUI - SettingsDialog', '3', '2', 'Add all objects to settings dialog / buttons, TextFields, ...', null, '2010-03-14 00:22:31');
INSERT INTO `work_item` VALUES ('4', '1', '1', '1', 'GUI - TestDialog', '5', '0', 'Implementation of TestDialog (test WorkItem)', null, '2010-03-13 00:22:34');
INSERT INTO `work_item` VALUES ('5', '2', '1', null, 'DAL', '3', '1', 'Implementation of DAL package', null, '2010-03-01 00:23:37');
INSERT INTO `work_item` VALUES ('6', '2', '1', '5', 'DAL - FillWorkItemCollection', '3', '1', 'Complete implementation', '2010-03-19 00:25:46', '2010-03-01 00:25:50');
INSERT INTO `work_item` VALUES ('7', '3', '4', null, 'New class implementation', '5', '2', 'To nejde :D ', null, '2010-03-14 00:27:12');
INSERT INTO `work_item` VALUES ('8', '1', '3', null, 'Personalistika', '3', '1', '---', '2010-04-30 00:28:17', '2010-01-01 00:28:09');
INSERT INTO `work_item` VALUES ('9', '1', '3', '8', 'Personalistika - Zdravotni prohlidky', '3', '1', 'Main dialog redesign', null, '2010-03-14 00:29:18');
INSERT INTO `work_item` VALUES ('10', '1', '3', '9', 'Personalistika - subtask', '3', '1', 'create some subtask', '0000-00-00 00:00:00', '2010-03-14 00:30:09');

-- ----------------------------
-- Table structure for `work_item_note`
-- ----------------------------
DROP TABLE IF EXISTS `work_item_note`;
CREATE TABLE `work_item_note` (
  `note_id` decimal(19,0) NOT NULL,
  `work_item_id` decimal(19,0) NOT NULL,
  `author_user_id` decimal(19,0) NOT NULL,
  `note` text,
  PRIMARY KEY (`note_id`,`work_item_id`),
  KEY `FK_Work_Item_Note_Work_Item_work_item_id` (`work_item_id`),
  KEY `FK_Work_Item_Note_User_author_user_id` (`author_user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of work_item_note
-- ----------------------------
INSERT INTO `work_item_note` VALUES ('2', '4', '1', 'Ok - Deleted');
INSERT INTO `work_item_note` VALUES ('1', '0', '0', null);
INSERT INTO `work_item_note` VALUES ('3', '6', '1', 'No uz aby to bylo ;-)');
INSERT INTO `work_item_note` VALUES ('4', '6', '2', 'Pracuju jak muzu :D');
INSERT INTO `work_item_note` VALUES ('5', '7', '1', 'tak ten tezko neco udela');
INSERT INTO `work_item_note` VALUES ('6', '7', '2', 'taky si myslim');
