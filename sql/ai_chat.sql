-- ----------------------------
-- Table structure for ai_chat
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat`;
CREATE TABLE `ai_chat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '对话ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_input` varchar(2000) NOT NULL COMMENT '用户输入内容',
  `ai_response` text COMMENT 'AI回复内容',
  `timestamp` bigint(20) DEFAULT NULL COMMENT '对话时间戳',
  `status` char(1) DEFAULT '0' COMMENT '对话状态（0成功 1失败）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='AI对话记录表';