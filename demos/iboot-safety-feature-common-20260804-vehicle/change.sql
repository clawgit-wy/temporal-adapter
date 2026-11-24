1.数据库表（safety_bpm_model）字段新增
ALTER TABLE `safety_bpm_model`
ADD COLUMN `is_show` CHAR(1) NULL DEFAULT '1' COMMENT '是否显示 0 否  1是' ;
2.数据库表（safety_bpm_model）字段新增
ALTER TABLE `ytsafety_summary_process`
ADD COLUMN `is_nick_name` CHAR(1) NULL DEFAULT '0' COMMENT '是否匿名 0 否  1是' ,
ADD COLUMN `nick_name` VARCHAR(45) NULL COMMENT '昵称' ,
ADD COLUMN `nick_account` VARCHAR(45) NULL COMMENT '昵称账号',
ADD COLUMN `is_synchro` CHAR(1) NULL COMMENT '是否异常';

3.新增表（safety_process_abandon_config）
CREATE TABLE `safety_process_abandon_config` (
  `id` bigint(20) NOT NULL,
  `model_id` varchar(45) DEFAULT NULL COMMENT '模版id',
  `model_name` varchar(150) DEFAULT NULL COMMENT '模版名称',
  `form_id` varchar(50) DEFAULT NULL COMMENT '表单唯一标识',
  `form_name` varchar(150) DEFAULT NULL COMMENT '表单名称',
  `parameter` varchar(45) DEFAULT NULL COMMENT '参数',
  `status_value` varchar(45) DEFAULT NULL COMMENT '废弃状态值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;