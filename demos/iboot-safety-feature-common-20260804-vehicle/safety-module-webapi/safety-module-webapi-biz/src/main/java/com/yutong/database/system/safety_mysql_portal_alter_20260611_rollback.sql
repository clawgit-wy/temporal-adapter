/*
    mysql
    数据库服务器IP  10.66.139.219  safety
*/
use safety;

DROP TABLE safety.ytsafety_sys_user_factory;

DROP TABLE safety.ytsafety_sys_user_depart;

DROP TABLE safety.ytsafety_sys_user_data_rule;

DROP TABLE safety.ytsafety_sys_responsible;

DROP TABLE safety.ytsafety_sys_job_role_mapping;

DROP TABLE safety.ytsafety_sys_org_factory;

DROP TABLE safety.ytsafety_sys_log;

DROP TABLE safety.ytsafety_sys_inteface_log;

DROP TABLE safety.ytsafety_api_route_config;

-- 还原表
DROP TABLE IF EXISTS safety.safety_sys_perm;
RENAME TABLE safety.safety_sys_perm_bak_20260611 TO safety.safety_sys_perm;

DROP TABLE IF EXISTS safety.safety_sys_role;
RENAME TABLE safety.safety_sys_role_bak_20260611 TO safety.safety_sys_role;

DROP TABLE IF EXISTS safety_sys_role_perm;
RENAME TABLE safety.safety_sys_role_perm_bak_20260611 TO safety.safety_sys_role_perm;

DROP TABLE IF EXISTS safety.safety_sys_user;
RENAME TABLE safety.safety_sys_user_bak_20260611 TO safety.safety_sys_user;

DROP TABLE IF EXISTS safety.safety_sys_user_role;
RENAME TABLE safety.safety_sys_user_role_bak_20260611 TO safety.safety_sys_user_role;

DROP TABLE IF EXISTS safety.safety_dict_item;
RENAME TABLE safety.safety_dict_item_bak_20260611 TO safety.safety_dict_item;

DROP TABLE IF EXISTS safety.safety_dict_supper;
RENAME TABLE safety.safety_dict_supper_bak_20260611 TO safety.safety_dict_supper;

commit;