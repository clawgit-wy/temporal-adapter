import { defHttp } from '/@/utils/http/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

export enum Api {
  // save='/safety/goodsOut/auth/add',
  
  // delete='/safety/goodsOut/auth/batchDelete',
  // listPage='/safety/goodsOut/auth/listPage',
  // importXls = '/safety/goodsOut/auth/importData',
  
  // getEmployeeByWorkNo = '/safety/employee/getEmployeeByWorkNo', // 根据工号获取人员信息
  
  // groupTree= '/safety/group/getDepartName', // 获取组织架构

  // getFactoryList = '/safety/field/selectLinkageListNewName', // 获取工厂列表
  // tableList = '/build/safety/buildDevice/listForAn', // 获取列表
  // update='/build/safety/buildDevice/updateForAn',
  // exportXls = '/build/safety/buildDevice/exportForAn', // 下载
  // fieldChildTree = '/safety/field/fieldChildTree', // 岗位验证下查询子项目单独接口

  export = '/safety/device/regionDevice/export',//导出
  pageList = '/safety/device/regionDevice/pageList',//列表
  updateAndSaveToCrk = '/safety/device/regionDevice/updateAndSaveToCrk',//手动更新
  updateDeviceType = '/safety/device/regionDevice/updateDeviceType',//提交
  
}

// 获取列表
export const tableList = (params) => {
  return defHttp.get({ url: Api.pageList, params })
}

// 手动更新
export const updateAndSaveToCrk = (params) => {
  return defHttp.post({ url: Api.updateAndSaveToCrk, params })
}; 


// 提交
export const updateDeviceType = (params) => {
  return defHttp.post({ url: Api.updateDeviceType, params ,headers: {
    'Content-Type': 'application/json'
  }})
}; 


