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

 Date: 18/01/2025 04:56:54
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for msg1
-- ----------------------------
DROP TABLE IF EXISTS `msg1`;
CREATE TABLE `msg1`
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
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for msg2
-- ----------------------------
DROP TABLE IF EXISTS `msg2`;
CREATE TABLE `msg2`
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
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for msg3
-- ----------------------------
DROP TABLE IF EXISTS `msg3`;
CREATE TABLE `msg3`
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
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

SET
FOREIGN_KEY_CHECKS = 1;
