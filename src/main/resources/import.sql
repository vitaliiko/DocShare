INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('John', 'McClane', '111', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Bruce', 'Willis', 'bruce', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('John', 'Travolta', 'john', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Nicolas', 'Cage', 'nik', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Harrison', 'Ford', 'harry', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Denzel', 'Washington', 'den', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Hugh', 'Laurie', 'house', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Keanu', 'Reeves', 'matrix', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Arnold', 'Schwarzenegger', 'arny', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Sylvester', 'Stallone', 'rocky', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Mel', 'Gibson', 'patriot', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Chuck', 'Norris', 'killyou', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Liam', 'Neeson', 'schindler', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Bob', 'Odenkirk', 'soul', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Aaron', 'Paul', 'bitch', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');
INSERT INTO `doc_share_db`.`user` (`firstName`, `lastName`, `login`, `password`) VALUES ('Bryan', 'Cranston', 'ww', '6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2');

INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Parents', '1');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Colleagues', '1');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Classmates', '1');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Colleagues', '2');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Classmates', '2');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Parents', '3');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Classmates', '4');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Parents', '5');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Colleagues', '7');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Parents', '8');
INSERT INTO `doc_share_db`.`friends_group` (`name`, `owner_id`) VALUES ('Colleagues', '8');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('1', '2');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('2', '1');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('1', '3');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('3', '1');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('1', '4');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('4', '1');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('1', '5');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('5', '1');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('1', '6');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('6', '1');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('1', '7');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('7', '1');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('1', '8');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('8', '1');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('2', '4');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('4', '2');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('2', '6');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('6', '2');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('2', '7');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('7', '2');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('2', '10');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('10', '2');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('2', '12');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('12', '2');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('3', '16');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('16', '3');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('3', '15');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('15', '3');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('3', '14');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('14', '3');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('3', '13');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('13', '3');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('3', '12');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('12', '3');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('3', '11');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('11', '3');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('3', '10');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('10', '3');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('3', '9');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('9', '3');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('4', '8');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('8', '4');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('4', '7');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('7', '4');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('4', '5');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('5', '4');

INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('4', '10');
INSERT INTO `doc_share_db`.`user_to_friend_relation` (`user_id`, `friend_id`) VALUES ('10', '4');

INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('1', '2');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('1', '3');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('1', '4');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('2', '3');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('2', '4');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('2', '8');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('3', '2');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('3', '6');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('3', '7');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('3', '8');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('4', '4');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('4', '10');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('6', '16');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('6', '15');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('6', '13');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('6', '10');
INSERT INTO `doc_share_db`.`user_to_group_relation` (`groupId`, `userId`) VALUES ('6', '1');


