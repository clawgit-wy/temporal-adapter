import { BasicColumn, FormSchema } from '/@/components/Table';
import dayjs from 'dayjs';

export const columns: BasicColumn[] = [
  {
    title: '操作页面',
    dataIndex: 'operatePage',
    width: 120,
  },
  {
    title: '功能',
    dataIndex: 'ability',
    width: 100,
  },
  {
    title: '信息',
    dataIndex: 'message',
    width: 150,
  },
  {
    title: '操作人',
    dataIndex: 'operator',
    width: 150,
  },
  {
    title: '操作时间',
    dataIndex: 'createTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '操作页面',
    field: 'operatePage',
    component: 'Input',
  },
  {
    label: '操作人',
    field: 'operator',
    component: 'Input',
  },
  {
    label: '信息',
    field: 'message',
    component: 'Input',
  },
  {
    label: '更新时间',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
];
