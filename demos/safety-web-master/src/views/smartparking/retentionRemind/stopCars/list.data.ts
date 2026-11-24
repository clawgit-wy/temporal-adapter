import { BasicColumn, FormSchema, Search } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import {getFactoryList} from './list.api'
import dayjs from 'dayjs';

const informStatusList = [
  {label: '正常',value: 0},
  {label: '待发送',value: 1},
  {label: '已发送',value: 2},
]

export const columns: BasicColumn[] = [
  {
    title: '车牌号',
    dataIndex: 'carNo',
    width: 120,
  },
  {
    title: '车辆类型',
    dataIndex: 'carType',
    width: 150,
  },
  // {
  //   title: '车辆分类',
  //   dataIndex: 'carCategory',
  //   width: 100,
  // },
  {
    title: '员工号',
    dataIndex: 'staffNo',
    width: 100,
  },
  {
    title: '姓名',
    dataIndex: 'staffName',
    width: 100,
  },
  {
    title: '入厂大门',
    dataIndex: 'inDoor',
    width: 100,
  },
  {
    title: '入场时间',
    dataIndex: 'inTime',
    width: 100,
  },
  {
    title: '停车时长(小时)',
    dataIndex: 'inTime',
    customRender: ({text}) => dayjs(new Date()).diff(text, 'hour'),
    width: 100,
  },
  {
    title: '通知状态',
    dataIndex: 'informStatus',
    customRender: ({ text }) => {
      return render.renderDictNative(text, informStatusList, false);
    },
    width: 100,
  },
  {
    title: '厂区',
    dataIndex: 'factory',
    width: 100,
  },
  // {
  //   title: '车辆颜色',
  //   dataIndex: 'carColor',
  //   width: 100,
  // },
];

export const searchFormSchema: FormSchema[] =[
  
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '车牌号',
    field: 'carNo',
    component: 'Input',
  },
  {
    label: '车辆类型',
    field: 'carType',
    component: 'Select',
    componentProps: {
      options: [
        {
          label: '员工车辆',
          value: '员工车辆',
          key: '1',
        },
        {
          label: '外协车辆',
          value: '外协车辆',
          key: '2',
        },
      ],
    },
  },
  {
    label: '厂区',
    field: 'factory',
    component: 'Factory',

    // componentProps: {
    //   api: () => getFactoryList()
    // },
  },
  {
    label: '入场时间',
    field: 'inTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '员工号',
    field: 'staffNo',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'staffName',
    component: 'Input',
  },
] 