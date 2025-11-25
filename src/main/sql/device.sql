/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : mydatabase

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 25/11/2025 10:39:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device`  (
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备序列号',
  `auto_oxygenation` bit(1) NULL DEFAULT NULL COMMENT '自动供氧开关',
  `config_priority` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '阈值优先级（DEVICE/REGION/GLOBAL）',
  `last_seen` datetime(6) NULL DEFAULT NULL COMMENT '最近上报时间',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备名称',
  `oxy_max` double NULL DEFAULT NULL COMMENT '设备级氧气阈值上限',
  `oxy_min` double NULL DEFAULT NULL COMMENT '设备级氧气阈值下限',
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属区域',
  `temp_max` double NULL DEFAULT NULL COMMENT '设备级温度阈值上限',
  `temp_min` double NULL DEFAULT NULL COMMENT '设备级温度阈值下限',
  PRIMARY KEY (`device_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT='设备信息表';

-- ----------------------------
-- Table structure for device_control_log
-- ----------------------------
DROP TABLE IF EXISTS `device_control_log`;
CREATE TABLE `device_control_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备ID',
  `device_status` int NOT NULL COMMENT '设备状态（1=启动，0=停止）',
  `raw_command` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '原始指令',
  `sent_at` datetime NOT NULL COMMENT '发送时间',
  `triggering_data_id` bigint NOT NULL COMMENT '触发此指令的传感器数据日志ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 218 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT='设备控制日志表';

-- ----------------------------
-- Table structure for region_config
-- ----------------------------
DROP TABLE IF EXISTS `region_config`;
CREATE TABLE `region_config`  (
  `region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区域编码',
  `oxy_max` double NULL DEFAULT NULL COMMENT '区域氧气阈值上限',
  `oxy_min` double NULL DEFAULT NULL COMMENT '区域氧气阈值下限',
  `temp_max` double NULL DEFAULT NULL COMMENT '区域温度阈值上限',
  `temp_min` double NULL DEFAULT NULL COMMENT '区域温度阈值下限',
  PRIMARY KEY (`region`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT='区域阈值配置表';

-- ----------------------------
-- Table structure for sensor_data_log
-- ----------------------------
DROP TABLE IF EXISTS `sensor_data_log`;
CREATE TABLE `sensor_data_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `device_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备ID',
  `oxygen_concentration` double NULL DEFAULT NULL COMMENT '氧气浓度',
  `raw_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '原始数据',
  `received_at` datetime NOT NULL COMMENT '接收时间',
  `temperature` double NULL DEFAULT NULL COMMENT '温度值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 222 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT='传感器数据日志表';

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主账号ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希（BCrypt）',
  `display_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '显示名称',
  `max_members` int NULL DEFAULT 5 COMMENT '成员上限',
  `created_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_account_username` (`username`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT='主账号表';

DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成员ID',
  `account_id` bigint NULL DEFAULT NULL COMMENT '所属账号ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '成员用户名',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '成员密码哈希',
  `display_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '成员显示名称',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_member_account` (`account_id`),
  CONSTRAINT `fk_member_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT='成员表';

ALTER TABLE `sensor_data_log` ADD INDEX `idx_sensor_device_time` (`device_id`,`received_at`);
ALTER TABLE `device_control_log` ADD INDEX `idx_control_device_time` (`device_id`,`sent_at`);
ALTER TABLE `device` ADD INDEX `idx_device_region` (`region`), ADD INDEX `idx_device_name` (`name`);

INSERT INTO `account` (`id`,`username`,`password_hash`,`display_name`,`max_members`,`created_at`) VALUES
(1,'admin','$2a$10$tNS53XyQd5JR5v86dn5OL.kRduHqHef1RGsJYfnO/T4mK4GWEIOOe','Admin',5,'2025-11-25 10:00:00');

INSERT INTO `member` (`account_id`,`username`,`password_hash`,`display_name`) VALUES
(1,'worker1','$2a$10$tNS53XyQd5JR5v86dn5OL.kRduHqHef1RGsJYfnO/T4mK4GWEIOOe','Worker One'),
(1,'worker2','$2a$10$tNS53XyQd5JR5v86dn5OL.kRduHqHef1RGsJYfnO/T4mK4GWEIOOe','Worker Two');

INSERT INTO `region_config` (`region`,`oxy_max`,`oxy_min`,`temp_max`,`temp_min`) VALUES
('Este01',10,5,32,20),
('Este02',10,5,32,20),
('Este03',10,5,32,20),
('Oeste01',10,5,32,20),
('Oeste02',10,5,32,20);

INSERT INTO `device` (`device_id`,`auto_oxygenation`,`config_priority`,`last_seen`,`name`,`oxy_max`,`oxy_min`,`region`,`temp_max`,`temp_min`) VALUES
('AOBIN-434-123',b'1','REGION',NULL,'Engorde estanque 1',NULL,NULL,'Este02',NULL,NULL),
('AOBIN-434-124',b'0','GLOBAL',NULL,'Engorde estanque 2',NULL,NULL,'Este02',NULL,NULL);

SET FOREIGN_KEY_CHECKS = 1;
