import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '应用名称',
    dataIndex: 'generalType',
    width: 120,
    customRender: () => {
      return '商务来访'
    },
  },
  {
    title: '是否可见',
    dataIndex: 'generalShow',
    width: 100,
    slots: { customRender: 'generalShow' },
  },
  {
    title: '不可见原因',
    dataIndex: 'content',
    width: 150,
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
    label: '是否可见',
    field: 'generalShow',
    component: 'Switch',
    componentProps: {
      checkedValue: 0,
      unCheckedValue: 1,
    },
  },
  {
    label: '不可见原因',
    field: 'content',
    component: 'Input',
    ifShow: ({ values }) => {
      return values.generalShow === 1 // 0为可见，1为不可见;
    },
    rules: [
      {
        required: true,
        message: '请输入不可见原因',
      },
    ],
    componentProps: {
      maxlength: 100,
    },
  },
];
