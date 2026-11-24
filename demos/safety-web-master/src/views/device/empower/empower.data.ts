import { BasicColumn, FormSchema } from '/@/components/Table';
const deviceType = [
  {
    value: 0,
    label: '读卡器',
    key: '1',
  },
  {
    value: 1,
    label: '人脸平板',
    key: '2',
  },
  // {
  //   value: 2,
  //   label: '二维码识别',
  //   key: '3',
  // },
  {
    value: 3,
    label: '车道',
    key: '4',
  },
];

export const columns: BasicColumn[] = [
  {
    title: '厂区',
    dataIndex: 'factoryName',
    width: 150,
  },
  {
    title: '区域',
    dataIndex: 'regionName',
    width: 150,
  },
  {
    title: '授权分类',
    dataIndex: 'empowerType',
    width: 150,
  },
  {
    title: '授权设备号',
    dataIndex: 'empowerNo',
    width: 150,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 150,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '厂区',
    field: 'factoryId',
    component: 'Input',
    slot: 'factoryName',
  },
  {
    label: '区域',
    field: 'regionId',
    component: 'Input',
  },
  {
    label: '授权设备号',
    field: 'empowerNo',
    component: 'Input',
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
    field: 'factoryId',
    component: 'Select',
    required: true,
    slot: 'factoryName',
  },
  {
    label: '区域',
    field: 'regionId',
    component: 'Select',
    required: true,
    slot: 'regionName',
  },
  {
    label: '授权分类',
    field: 'empowerType',
    component: 'Select',
    required: true,
    slot: 'empowerType',
  },
  {
    label: '授权设备号',
    field: 'empowerNo',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
];

export const deviceTypeList = [...deviceType];
