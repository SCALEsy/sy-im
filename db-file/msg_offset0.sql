/*
 Navicat Premium Dump SQL

 Source Server         : mysql5.7
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44)
 Source Host           : 192.168.31.90:3306
 Source Schema         : im0

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44)
 File Encoding         : 65001

 Date: 19/01/2025 07:23:33
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for msg_offset0
-- ----------------------------
DROP TABLE IF EXISTS `msg_offset0`;
CREATE TABLE `msg_offset0`
(
    `id`        int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`   int(11) NOT NULL,
    `dialog_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `msg_id`    bigint(20) NULL DEFAULT NULL,
    `time`      datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2205 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for msg_offset1
-- ----------------------------
DROP TABLE IF EXISTS `msg_offset1`;
CREATE TABLE `msg_offset1`
(
    `id`        int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`   int(11) NOT NULL,
    `dialog_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `msg_id`    bigint(20) NULL DEFAULT NULL,
    `time`      datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2205 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for msg_offset2
-- ----------------------------
DROP TABLE IF EXISTS `msg_offset2`;
CREATE TABLE `msg_offset2`
(
    `id`        int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`   int(11) NOT NULL,
    `dialog_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `msg_id`    bigint(20) NULL DEFAULT NULL,
    `time`      datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2205 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

SET
FOREIGN_KEY_CHECKS = 1;
