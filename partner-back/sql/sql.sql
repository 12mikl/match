
# 用户表
CREATE TABLE `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userName` varchar(50) DEFAULT NULL COMMENT '昵称',
  `userAccount` varchar(20) DEFAULT NULL COMMENT '登录账号',
  `userPassword` varchar(50) DEFAULT NULL COMMENT '密码',
  `userGender` int DEFAULT NULL COMMENT '性别0男1女2保密',
  `userPhone` varchar(30) DEFAULT NULL COMMENT '电话',
  `userEmail` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `userRole` varchar(1) DEFAULT '0' COMMENT '角色权限 0用户 1管理员',
  `avatarUrl` varchar(128) DEFAULT NULL COMMENT '用户头像',
  `tags` varchar(1024) DEFAULT NULL COMMENT '标签',
  `isDelete` int DEFAULT '0' COMMENT '是否删除 0 1（逻辑删除）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


# 标签表
CREATE TABLE `t_tag` (
   `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
   `userId` bigint DEFAULT NULL COMMENT '用户id',
   `parentId` int DEFAULT NULL COMMENT '父id',
   `isParent` int DEFAULT '0' COMMENT '是否有父Id，0无,1有',
   `tagName` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标签名称',
   `isDelete` int DEFAULT '0' COMMENT '是否删除,0否,1是',
   `create_time` datetime DEFAULT NULL COMMENT '创建时间',
   `update_time` datetime DEFAULT NULL COMMENT '修改时间',
   PRIMARY KEY (`id`),
   KEY `idx_tagName` (`tagName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
