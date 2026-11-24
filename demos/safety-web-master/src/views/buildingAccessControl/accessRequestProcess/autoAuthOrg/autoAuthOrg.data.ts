/**
 * 自动授权组织记录类型
 */
export interface AutoAuthOrgRecord {
  id: string;
  anFactoryName?: string; // 厂区
  anFactoryCode?: string; // 厂区编码
  anBuilding?: string; // 楼栋
  anBuildingCode?: string; // 楼栋编码
  anDeviceName?: string; // 授权门禁
  deviceCode?: string; // 授权门禁编码
  deptName?: string; // 授权部门
  deptCode?: string; // 授权部门编码
  createEmployeeName?: string; // 申请人
  createEmployeeNo?: string; // 申请人工号
  createEmployeeLogin?: string; // 申请人登录名
  createDeptCode?: string; // 申请人部门编码
  createDeptName?: string; // 申请人部门
  dockingDeptCode?: string; // 对接部门编码
  dockingDeptName?: string; // 对接部门
  beginTime?: string; // 申请授权开始时间
  endTime?: string; // 申请授权截止时间
  processId?: string; // 来源
  autoFlag?: string;
  createTime?: string; // 创建时间
  updateTime?: string;
  updateBy?: string;
  deleteFlag?: number;
}

/**
 * 查询参数类型
 */
export interface QueryParams {
  anFactoryName?: string;
  anBuilding?: string;
  anDeviceName?: string;
  deptName?: string;
  dockingDeptName?: string;
  beginTime?: string;
  endTime?: string;
  pageNum?: number;
  pageSize?: number;
}

import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

/**
 * 表格列定义
 */
export const columns: BasicColumn[] = [
  {
    title: '厂区',
    dataIndex: 'anFactoryName',
    align: 'center',
  },
  {
    title: '楼栋',
    dataIndex: 'anBuilding',
    align: 'center',
  },
  {
    title: '授权门禁',
    dataIndex: 'anDeviceName',
    align: 'center',
  },
  {
    title: '授权部门',
    dataIndex: 'deptName',
    align: 'center',
    customRender: ({text}) => text && text.length > 0 && text.split(',').length > 0 ? text.split(',').reverse().join('<<') : text,
    width: 300,
  },
  {
    title: '申请人',
    dataIndex: 'createEmployeeName',
    align: 'center',
  },
  {
    title: '申请人部门',
    dataIndex: 'createDeptName',
    align: 'center',
    width: 300,
  },
  {
    title: '对接部门',
    dataIndex: 'dockingDeptName',
    customRender: ({text}) => text && text.length > 0 && text.split(',').length > 0 ? text.split(',').reverse().join('<<') : text,
    align: 'center',
  },
  {
    title: '申请授权期限',
    dataIndex: 'authPeriod',
    customRender: ({ record }) => {
      const autoRecord = record as unknown as AutoAuthOrgRecord;
      if (autoRecord.beginTime && autoRecord.endTime) {
        return `${autoRecord.beginTime} 至 ${autoRecord.endTime}`;
      }
      return '-';
    },
    align: 'center',
    width: 200,
  },
  {
    title: '来源',
    dataIndex: 'processId',
    align: 'center',
    slots: { customRender: 'processLink' },
  },
];

/**
 * 搜索表单定义
 */
export const searchFormSchema: FormSchema[] = [
  {
    field: 'anFactoryName',
    label: '厂区',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'anBuilding',
    label: '楼栋',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'anDeviceName',
    label: '门禁',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'deptName',
    label: '授权部门',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    label: '申请人',
    field: 'create',
    component: 'Input',
    componentProps: {
      placeholder: '姓名/员工号',
    },
    colProps: { span: 6 },
  },
  {
    field: 'dockingDeptName',
    label: '对接部门',
    component: 'Input',
    colProps: { span: 6 },
  },

  // {
  //   field: 'timeRange',
  //   label: '授权期限',
  //   component: 'RangePicker',
  //   componentProps: {
  //     showTime: false,
  //     format: 'YYYY-MM-DD',
  //     valueFormat: 'YYYY-MM-DD',
  //   },
  //   colProps: { span: 6 },
  // },
];
