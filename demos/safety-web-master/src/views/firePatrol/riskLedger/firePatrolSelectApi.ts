import {defHttp} from '/@/utils/http/axios';

export enum firePatrolSelectApi {
  getInspectionMethod = '/fireInspection/safety/select/getInspectionMethod', // 检查方法下拉框接口
  getRiskLevel = '/fireInspection/safety/select/getRiskLevel', // 风险等级下拉框接口
  getTaskObject = '/fireInspection/safety/select/getTaskObject', // 任务对象下拉框接口
  getWorkStep = '/fireInspection/safety/select/getWorkStep', // 作业环节下拉框接口
}

/*检查方法下拉框接口*/
export const getInspectionMethod = () => {
  return defHttp.get({url: firePatrolSelectApi.getInspectionMethod});
};
/*风险等级下拉框接口*/
export const getRiskLevel = () => {
  return defHttp.get({url: firePatrolSelectApi.getRiskLevel});
};
/*任务对象下拉框接口*/
export const getTaskObject = () => {
  return defHttp.get({url: firePatrolSelectApi.getTaskObject});
};

/* 作业环节下拉框接口 */
export const getWorkStep = () => {
  return defHttp.get({url: firePatrolSelectApi.getWorkStep}).then((response) => {
    // 使用 map 方法改造数据
    return response.map((item) => ({
      ...item,
      realName: item.workStepName,
      workStepName: `${item.workStepName} (${item.workStepCode})`, // 自定义 label 字段
      workStepCode: item.workStepCode, // 保持 value 字段不变
    }));
  });
};
