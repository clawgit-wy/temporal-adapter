import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import dayjs from 'dayjs';
// 表格列定义
export const columns: BasicColumn[] = [
  {
    title: '消息类型',
    dataIndex: 'infoType',
    width: 120,
    align: 'center',
    customRender: ({ text }) => {
      const typeMap = {
        '1': '部门人员变动',
        '2': '长期未进出',
        '3': '全部',
      };
      return typeMap[text] || text;
    },
  },
  {
    title: '内容',
    dataIndex: 'infoContent',
    align: 'center',

  },
  {
    title: '时间',
    dataIndex: 'infoTime',
    width: 180,
    align: 'center',
    customRender: ({ text }) => {
      return text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '';
    },
  },

];

// 搜索表单定义
export const searchFormSchema: FormSchema[] = [
  {
    field: 'infoType',
    label: '类型',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 8 },
  },
  {
    label: '发送时间',
    field: 'createTimeRange',
    component: 'RangePicker',
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      placeholder: ['开始日期', '结束日期'],
    },
    colProps: { span: 8 },

  },
];

