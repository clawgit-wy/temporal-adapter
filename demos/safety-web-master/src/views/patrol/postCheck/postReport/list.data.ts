import { BasicColumn, FormSchema } from '/@/components/Table';
import { getGroupTree } from './list.api'

export const columns: BasicColumn[] = [
  {
    title: '厂区',
    dataIndex: 'orgNamePath',
    width: 150,
  },
  {
    title: '类型',
    dataIndex: 'remark',
    width: 100,
  },
  {
    title: '项目',
    dataIndex: 'priority',
    width: 100,
  },
  {
    title: '位置',
    dataIndex: 'check1',
    width: 160,
  },
  {
    title: '设备编号',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '管理责任人',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '点检时间',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '设备状态',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '异常描述',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '设备照片',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '实际点检人',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '点检人员工号',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '点检人部门',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '预计整改完成时间',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '整改次数',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '整改状态',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '整改责任人',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '整改验证结果',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '验证时间',
    dataIndex: 'checkFlag',
    width: 100,
  },
  {
    title: '验证人',
    dataIndex: 'checkFlag',
    width: 100,
  },
];

export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '厂区',
    field: 'factory',
    component: 'FactoryId',
    required: true,
  },
  {
    label: '类型',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
    required: true,
  },
  {
    label: '项目',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
    required: true,
  },
  {
    label: '位置',
    field: 'type',
    component: 'Input',
  },
  {
    label: '应用项目',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
    required: true,
  },
  {
    label: '任务日期',
    field: 'type',
    component: 'RangeDate',
  },
];
export const searchFormSchema: FormSchema[] =[
  
  {
    label: '厂区',
    field: 'factory',
    component: 'FactoryId',
  },
  {
    label: '类型',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
  },
  {
    label: '项目',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
  },
  {
    label: '位置',
    field: 'type',
    component: 'Input',
  },
  {
    label: '设备编号',
    field: 'type',
    component: 'Input',
  },
  {
    label: '管理责任人',
    field: 'type',
    component: 'SearchPerson',
  },
  {
    label: '点检时间',
    field: 'type',
    component: 'DatePicker',
  },
  {
    label: '设备状态',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
  },
  {
    label: '点检人',
    field: 'type',
    component: 'SearchPerson',
  },
  {
    label: '点检员工号',
    field: 'type',
    component: 'SearchPerson',
  },
  {
    label: '点检人部门',
    field: 'type',
    component: 'FactoryId',
  },
  {
    label: '预计整改完成时间',
    field: 'type',
    component: 'DatePicker',
  },
  {
    label: '整改次数',
    field: 'type',
    component: 'Input',
  },
  {
    label: '整改状态',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
  },
  {
    label: '整改责任人',
    field: 'type',
    component: 'SearchPerson',
  },
  {
    label: '整改验证结果',
    field: 'type',
    component: 'Select',
    componentProps: {
      options: [{label: '1', value: 1}]
    },
  },
  {
    label: '验证时间',
    field: 'type',
    component: 'DatePicker',
  },
  {
    label: '验证人',
    field: 'type',
    component: 'SearchPerson',
  },
] 