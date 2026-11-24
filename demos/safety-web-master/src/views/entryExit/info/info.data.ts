import { BasicColumn, FormSchema } from '/@/components/Table';
export const permissionTypeList = [
  {
    value: '查询车辆信息',
    label: '查询车辆信息',
    key: '1',
  },
  {
    value: '车辆通行记录',
    label: '车辆通行记录',
    key: '2',
  },
  {
    value: '人行通行记录',
    label: '人行通行记录',
    key: '3',
  },
  {
    value: '车辆/人行通行记录',
    label: '车辆/人行通行记录',
    key: '4',
  }
];

export const columns: BasicColumn[] = [
  {
    title: '员工号',
    dataIndex: 'empCode',
    width: 150,
  },
  {
    title: '姓名',
    dataIndex: 'empName',
    width: 150,
  },
  {
    title: '厂区',
    dataIndex: 'factoryName',
    width: 150,
  },
  {
    title: '权限类型',
    dataIndex: 'permissionType',
    width: 150,
  },
  {
    title: '查询时间',
    dataIndex: 'searchDay',
    width: 150,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '员工号',
    field: 'empCode',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
  },
  {
    label: '厂区',
    field: 'factoryId',
    component: 'Input',
    slot: 'factoryName',
  },
  {
    label: '权限类型',
    field: 'permissionType',
    component: 'Select',
    componentProps: {
      options: [...permissionTypeList],
    },
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
    label: '员工号',
    field: 'empCode',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 11,
    },
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 6,
    },
  },
  {
    label: '厂区',
    field: 'factoryId',
    component: 'Select',
    required: true,
    slot: 'factoryName',
  },
  {
    label: '权限',
    field: 'permissionType',
    component: 'Select',
    required: true,
    componentProps: {
      options: [...permissionTypeList],
    },
  },
  {
    label: '查询时间',
    field: 'searchDay',
    component: 'InputNumber',
    required: true,
    componentProps: {
      max: 30,
      min: 1,
    },
  },
];
