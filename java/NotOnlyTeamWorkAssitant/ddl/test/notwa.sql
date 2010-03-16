-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES ('1', 'notwa');
INSERT INTO `project` VALUES ('2', 'notwa fake');
INSERT INTO `project` VALUES ('3', 'person');
INSERT INTO `project` VALUES ('4', 'general');

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
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'mrneo', 'aaaa', 'Tom', 'St');
INSERT INTO `user` VALUES ('2', 'eter', 'bbbb', 'Je', 'mex');
INSERT INTO `user` VALUES ('3', 'michal', 'mmmmm', 'Mi', 'Ne');
INSERT INTO `user` VALUES ('4', 'new1', 'new2', 'User1', 'Last2');

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
-- Records of work_item_note
-- ----------------------------
INSERT INTO `work_item_note` VALUES ('2', '4', '1', 'Ok - Deleted');
INSERT INTO `work_item_note` VALUES ('1', '0', '0', null);
INSERT INTO `work_item_note` VALUES ('3', '6', '1', 'No uz aby to bylo ;-)');
INSERT INTO `work_item_note` VALUES ('4', '6', '2', 'Pracuju jak muzu :D');
INSERT INTO `work_item_note` VALUES ('5', '7', '1', 'tak ten tezko neco udela');
INSERT INTO `work_item_note` VALUES ('6', '7', '2', 'taky si myslim');
