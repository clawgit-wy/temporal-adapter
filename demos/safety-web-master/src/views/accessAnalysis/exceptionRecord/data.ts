import { BasicColumn, FormSchema } from '/@/components/Table';
import { getAreaCascaderProps } from '../common/area';

export const columns: BasicColumn[] = [
  {
    title: '姓名',
    dataIndex: 'name',
    width: 100,
  },
  {
    title: '员工号/手机号',
    dataIndex: 'empNo',
    width: 140,
  },
  // {
  //   title: '异常进出类型',
  //   dataIndex: 'abnormalEnterType',
  //   width: 130,
  // },
  // {
  //   title: '人员类型',
  //   dataIndex: 'empType',
  //   width: 110,
  // },
  {
    title: '进厂方式',
    dataIndex: 'accessMethod',
    width: 100,
  },
  {
    title: '厂区',
    dataIndex: 'factoryName',
    width: 110,
  },
  {
    title: '区域（楼栋）',
    dataIndex: 'buildName',
    width: 120,
  },
  {
    title: '抓拍人脸',
    dataIndex: 'facePhoto',
    width: 120,
    slots: { customRender: 'facePhoto' },
  },
  {
    title: '抓拍设备',
    dataIndex: 'cameraName',
    width: 160,
  },
  {
    title: '设备编号',
    dataIndex: 'deviceIndexcode',
    width: 220,
  },
  {
    title: '抓拍时间',
    dataIndex: 'eventTime',
    width: 160,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '姓名',
    field: 'name',
    component: 'Input',
  },
  {
    label: '员工号/手机号',
    field: 'empNo',
    component: 'Input',
  },
  {
    label: '所属区域',
    field: 'areaCode',
    component: 'Cascader',
    componentProps: getAreaCascaderProps,
  },
  {
    label: '设备编号',
    field: 'cameraIndexcode',
    component: 'Input',
  },
  {
    label: '抓拍时间',
    field: 'eventTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
];
