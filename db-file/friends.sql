/*
 Navicat Premium Dump SQL

 Source Server         : mysql5.7
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44)
 Source Host           : 192.168.31.90:3306
 Source Schema         : im

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44)
 File Encoding         : 65001

 Date: 22/01/2025 18:33:17
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for friends
-- ----------------------------
DROP TABLE IF EXISTS `friends`;
CREATE TABLE `friends`
(
    `id`        int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `from_id`   int(11) NOT NULL,
    `dest_id`   int(11) NOT NULL,
    `dialog_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `status`    tinyint(4) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2003 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_info
-- ----------------------------
DROP TABLE IF EXISTS `group_info`;
CREATE TABLE `group_info`
(
    `id`        int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `dialog_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `name`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `owner`     int(11) NULL DEFAULT NULL,
    `time`      datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_user
-- ----------------------------
DROP TABLE IF EXISTS `group_user`;
CREATE TABLE `group_user`
(
    `id`        int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `dialog_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `user_id`   int(11) NOT NULL,
    `time`      datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for msg
-- ----------------------------
DROP TABLE IF EXISTS `msg`;
CREATE TABLE `msg`
(
    `id`        bigint(20) NOT NULL,
    `from_id`   int(11) NULL DEFAULT NULL,
    `dest_id`   int(11) NULL DEFAULT NULL,
    `dialog_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `client_id` bigint(20) NULL DEFAULT NULL,
    `cmd`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `msg_type`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `chat_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `state`     varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `body`      text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
    `time`      datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for msg_reached_info
-- ----------------------------
DROP TABLE IF EXISTS `msg_reached_info`;
CREATE TABLE `msg_reached_info`
(
    `id`        int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`   int(11) NOT NULL,
    `dialog_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `msg_id`    bigint(20) NULL DEFAULT NULL,
    `time`      datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2203 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`   int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1003 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS = 1;
