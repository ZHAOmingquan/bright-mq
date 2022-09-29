

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for current_lock
-- ----------------------------
DROP TABLE IF EXISTS `current_lock`;
CREATE TABLE `current_lock`  (
  `lock_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`lock_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for default_message
-- ----------------------------
DROP TABLE IF EXISTS `default_message`;
CREATE TABLE `default_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(16000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `biz_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务唯一标识',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '消费状态.0未消费默认；1已消费；2消费失败；',
  `failed_times` int NOT NULL DEFAULT 0 COMMENT '消费失败次数',
  `created_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `updated_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1721 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for demo_message
-- ----------------------------
DROP TABLE IF EXISTS `demo_message`;
CREATE TABLE `demo_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(16000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `biz_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务唯一标识',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '消费状态.0未消费默认；1已消费；2消费失败；',
  `failed_times` int NOT NULL DEFAULT 0 COMMENT '消费失败次数',
  `created_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `updated_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
