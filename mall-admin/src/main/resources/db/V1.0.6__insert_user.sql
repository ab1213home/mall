SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Records of tb_user_groups
-- ----------------------------
INSERT INTO `tb_user_groups` VALUES (1, '普通用户组', '基础用户权限', NULL, 0, -1, '2025-02-26 02:46:48', -1, '2025-02-26 02:46:48', 0);
INSERT INTO `tb_user_groups` VALUES (2, '商城员工组', '订单处理和客服权限', NULL, 0, -1, '2025-02-26 02:46:48', -1, '2025-02-26 02:46:48', 0);
INSERT INTO `tb_user_groups` VALUES (3, '商城管理员组', '商城运营管理权限', NULL, 0, -1, '2025-02-26 02:46:48', -1, '2025-02-26 02:46:48', 0);
INSERT INTO `tb_user_groups` VALUES (4, '超级管理员组', '系统全局管理权限', 'banner:list,banner:create,banner:update,banner:delete,system:banner,category:list,category:create,category:update,category:delete,system:category,system:email,email:list,system:file,file:list,system,system:data,system:redis,system:pay,product:list,user:list,user:lock,order:list', 0, -1, '2025-02-26 02:46:48', -1, '2025-03-22 02:45:59', 0);

SET FOREIGN_KEY_CHECKS = 1;