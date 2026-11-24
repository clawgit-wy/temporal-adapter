import { BasicColumn, FormSchema } from '/@/components/Table';
import dayjs from 'dayjs';

export const columns: BasicColumn[] = [
  {
    title: '车牌号',
    dataIndex: 'cardNo',
    width: 80,
  },
  {
    title: '登记时间',
    dataIndex: 'powerSartTime',
    width: 100,
  },
  {
    title: '访客姓名',
    dataIndex: 'visitorName',
    width: 150,
  },
  {
    title: '访客身份证号',
    dataIndex: 'visitorIdCard',
    width: 150,
  },
  {
    title: '访客手机号',
    dataIndex: 'visitorPhone',
    width: 150,
  },
  {
    title: '授权厂区',
    dataIndex: 'empowerFactory',
    width: 150,
  },
  {
    title: '授权通道号',
    dataIndex: 'empowerNo',
    width: 150,
  },
  {
    title: '开始时间',
    dataIndex: 'checkStartTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '结束时间',
    dataIndex: 'checkEndTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 150,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '访客姓名',
    field: 'visitorName',
    component: 'Input',
  },
  {
    label: '访客手机号',
    field: 'visitorPhone',
    component: 'Input',
  },
  {
    label: '访客身份证号',
    field: 'visitorIdCard',
    component: 'Input',
  },
  {
    label: '授权通道号',
    field: 'empowerNo',
    component: 'Input',
  },
  {
    label: '授权厂区',
    field: 'empowerFactory',
    component: 'Select',
    slot: 'factoryName',
  },
  {
    label: '车牌号',
    field: 'cardNo',
    component: 'Input',
  },
  {
    label: '登记时间',
    field: 'checkStartTime',
    component: 'DatePicker',
  },
  {
    label: '授权时间',
    field: 'powerSartTime',
    component: 'DatePicker',
  },
];
