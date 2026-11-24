import { selectGroupTree } from './personAccessDetail.api';
import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

/**
 * 人员门禁授权明细记录类型
 */
export interface PersonAccessDetailRecord {
  id: string;
  employeeCode?: string; // 员工号
  employeeName?: string; // 姓名
  deviceCode?: string; // 门禁编码
  deviceName?: string; // 门禁权限
  beginTime?: string; // 门禁授权期限-起
  endTime?: string; // 门禁授权期限-止
  sourceType?: string; // 来源类型
  processId?: string; // 权限调整流程主键
  bpmFlowId?: string; // BPM流程ID
  remark?: string; // 事由
  dutyDeptCode?: string; // 责任部门编码
  dutyDeptName?: string; // 责任部门
  dutyEmpLogin?: null; // 门禁管理员-登录名
  dutyEmpName?: null; // 门禁管理员
  dutyEmpNo?: null; // 门禁管理员-员工号
  anFactoryCode?: null; // 厂区编码
  anFactoryName?: string; // 厂区权限
  anBuildingCode?: null; // 楼栋编码
  anBuilding?: string; // 楼栋权限
  deptName?: null; // 部门
  deptCode?: null; // 部门编码
  isTopSecurity?: null; // 是否绝密区
}

/**
 * 查询参数类型
 */
export interface QueryParams {
  creator?: string; // 姓名/员工号
  anFactoryName?: string; // 厂区
  anBuildName?: string; // 楼栋
  anDeviceName?: string; // 设备
  deptName?: string; // 部门
  dutyDept?: string; // 责任部门
  dutyPerson?: string; // 门禁管理员
  isTop?: string; // 是否绝密区
  powerTimeBegin?: string; // 授权期限-起
  powerTimeEnd?: string; // 授权期限-止
  pageNum?: number;
  pageSize?: number;
}

/**
 * 表格列定义 - 根据原型图调整顺序
 */
export const columns: BasicColumn[] = [
  {
    title: '员工号',
    dataIndex: 'employeeCode',
    align: 'center',
  },
  {
    title: '姓名',
    dataIndex: 'employeeName',
    align: 'center',
  },
  {
    title: '部门',
    dataIndex: 'deptName',
    align: 'center',
  },
  {
    title: '厂区权限',
    dataIndex: 'anFactoryName',
    align: 'center',
  },
  {
    title: '楼栋权限',
    dataIndex: 'anBuilding',
    align: 'center',
  },
  {
    title: '门禁权限',
    dataIndex: 'deviceName',
    align: 'center',
  },
  {
    title: '门禁授权期限',
    dataIndex: 'authPeriod',
    customRender: ({ record }) => {
      const detailRecord = record as unknown as PersonAccessDetailRecord;
      if (detailRecord.beginTime && detailRecord.endTime) {
        return `${detailRecord.beginTime} 至 ${detailRecord.endTime}`;
      }
      return '-';
    },
    align: 'center',
    width: 180,
  },
  {
    title: '是否涉及绝密区',
    dataIndex: 'isTopSecurity',
    align: 'center',
    // customRender: ({ text }) => {
    //   return text === '1' ? '是' : '否';
    // },
  },
  {
    title: '门禁管理员',
    dataIndex: 'dutyEmpName',
    align: 'center',
  },
  {
    title: '责任部门',
    dataIndex: 'dutyDeptName',
    align: 'center',
    width: 200,
  },
  {
    title: '来源',
    dataIndex: 'processId',
    align: 'center',
    slots: {
      customRender: 'source',
    },
  },
  {
    title: '事由',
    dataIndex: 'remark',
    align: 'center',
    width: 150,
  },

];

/**
 * 搜索表单定义 - 根据原型图调整
 */
export const searchFormSchema: FormSchema[] = [
  {
    field: 'creator',
    label: '姓名/员工号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'anFactoryName',
    label: '厂区',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'anBuildName',
    label: '楼栋',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'anDeviceName',
    label: '设备',
    component: 'Input',
    colProps: { span: 6 },
  },
  // {
  //   field: 'deptName',
  //   label: '部门',
  //   component: 'Input',
  //   colProps: { span: 6 },
  // },
  {
     label: '部门',
     field: 'deptCode',
     component: 'ApiTreeSelect',
     componentProps: () => {
      return {
       api: () => selectGroupTree(),
        resultField: 'dataList', // 返回数据的字段名
        valueField: 'key', // 唯一标识字段
        labelField: 'title', // 显示名称字段
        treeNodeFilterProp: 'title', // 搜索过滤字段
        showSearch: true, // 开启搜索
        optionFilterProp: 'label', // 用于过滤的字段
        immediate: true,
        // multiple: true,
         //请求参数
        params: {},
        //返回结果字段
      }
    },
  },
  {
    field: 'dutyDept',
    label: '责任部门',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'dutyPerson',
    label: '门禁管理员',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'isTop',
    label: '是否绝密区',
    component: 'Select',
    componentProps: {
      options: [
        { label: '全部', value: '' },
        { label: '是', value: '1' },
        { label: '否', value: '0' },
      ],
    },
    colProps: { span: 6 },
  },
  {
    field: 'timeRange',
    label: '授权期限',
    component: 'RangePicker',
    componentProps: {
      showTime: false,
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
    },
    colProps: { span: 6 },
  },
  
]; 

