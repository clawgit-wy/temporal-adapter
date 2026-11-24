import { message } from 'ant-design-vue';
import { defHttp } from '/@/utils/http/axios';


export enum Api {
  list = '/safety/outsiderPromise/findList',
  Looklist = '/safety/outsiderPromise/findListNoPower',
  exportXls = '/safety/outsiderPromise/exportData', // 导出
  createFlow = '/safety/outsiderPromise/createFlow',
  audit = '/safety/outsiderPromise/audit',
  view = '/safety/outsiderPromise/view',
  infoForNo = '/safety/outsiderPromise/promise',
  factoryList = '/safety/field/selectLinkageList',
  employeeInfo = '/safety/employee/list',
  selectGroupTree = '/safety/group/selectGroupTree',
  getEipNo = '/safety/spot/plan/employeeConvertWorkNo',
  import = '/safety/outsiderPromise/import',
  templateUrl = '/safety/outsiderPromise/download',
  getApproverList = '/safety/process/getApproverList', // 获取当前审批人
  getCurrentNodesInfo = '/safety/process/getCurrentNodesInfo', // 获取当前节点
  findPerson = '/safety/outsiderPromise/promiseByName',
  saveOrUpdate = '/safety/outsiderPromise/updateCreator',
  getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 查询员工
  validProcess = '/safety/outsiderPromise/validProcess',
  getEmployeeInfo = '/safety/base/getEmployeeInfo'
}

/**
 * 获取列表
 * @param params
 * 
 */
export const list = (params) =>{
  return defHttp.post({ url: Api.list, params })
};
/**
 * 获取列表无权限限制
 * @param params
 * 
 */
export const Looklist = (params) =>{
  return defHttp.post({ url: Api.Looklist, params })
};
// 创建流程
export const createFlow = (params) => {
  return defHttp.post({url: Api.createFlow, params})
}
// 审批流程
export const audit = (params) => {
  return defHttp.post({url: Api.audit, params})
}
// 流程详情
export const view = (params) => {
  return defHttp.post({url: Api.view, params})
}
// 根据员工号查询个人信息
export const infoForNo = (params) => {
  return defHttp.post({url: Api.infoForNo, params})
}
// 查询个人信息
// 根据员工号查询个人信息
export const finedInfos = (params) => {
  return new Promise((resolve, reject) => {
    defHttp.post({url: Api.infoForNo, params}).then(res => {
      console.log(res, 'resolve');
      if (res.status == 'F') {
        message.error(res.msg)
        resolve([])
      }
      resolve(res.list)
    })
  })
}
// 厂区
export const factoryList = (params = {}) => {
  return defHttp.post({url: Api.factoryList, params})
}
// 工号查询信息
export const employeeInfo = (params = {}) => {
  return defHttp.post({url: Api.employeeInfo, params})
}
// 获取组织架构
export const selectGroupTree = (params = {}) => {
  return defHttp.post({url: Api.selectGroupTree, params})
}
// 获取eip员工号
export const getEipNo = (params = {}) => {
  return defHttp.post({url: Api.getEipNo, params})
}
// 导入
export const importExcel = (params = {}) => {
  
  return defHttp.post({url: Api.import, params})
}
export const download = (params: {}) =>{
  return defHttp.get({ url: Api.templateUrl, responseType: 'blob', params })
};
// 获取当前审批人
export const getApproverList = (params) => {
  return defHttp.get({url: Api.getApproverList, params})
}
// 获取当前流程节点
export const getCurrentNodesInfo = (params) => {
  return defHttp.get({url: Api.getCurrentNodesInfo, params})
}
// 人员搜索
export const findPerson = (params = {}) => {
  return new Promise((resolve, reject) => {
    defHttp.post({url: Api.findPerson, params}).then(res => {
      console.log(res, 'resolve');
      if (res.status == 'F') {
        message.error(res.msg)
        resolve([])
      }
      const list = res.list.map(item => {
        if (item.email) {
          return {
            empName: item.name,
            empNo: item.empNo,
            empOrg: item.orgPath,
            email: item.email,
            factory: '',
          }
        }else {
          return item
        }
      })
      resolve(list)
    })
  })
}
export const saveOrUpdate = (params) => {
  return defHttp.get({url: Api.saveOrUpdate, params})
}

export const getEmployeeByWorkNo = (params) => {
  return defHttp.get({url: Api.getEmployeeByWorkNo, params})
}
export const validProcess =  (params) => {
  return defHttp.post({url: Api.validProcess, params})
}
export const getEmployeeInfo =  (params) => {
  return defHttp.post({url: Api.getEmployeeInfo, params})
}