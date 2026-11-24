import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '访客信息',
    dataIndex: 'visitor',
    width: 80,
  },
  {
    title: 'EIP对应字段',
    dataIndex: 'eipId',
    width: 150,
  },
  {
    title: '必填设置',
    dataIndex: 'mustSet',
    width: 150,
    slots: { customRender: 'mustSet' },
  },
  {
    title: '前端是否生效',
    dataIndex: 'takeEffect',
    width: 150,
    slots: { customRender: 'takeEffect' },
  },
  {
    title: '访客人数',
    dataIndex: 'info',
    width: 150,
  },
];

export const searchFormSchema: FormSchema[] = [
  // {
  //   label: '员工号',
  //   field: 'empNo',
  //   component: 'Input',
  // },
  // {
  //   label: '用户名',
  //   field: 'empName',
  //   component: 'Input',
  // },
];

export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: 'code',
    field: 'code',
    component: 'Input',
    show: false,
  },
  {
    label: '访客信息',
    field: 'visitor',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 10,
    },
  },
  {
    label: 'EIP对应字段',
    field: 'eipId',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 30,
    },
    ifShow: ({values}) => {
      return values.code !== 'personNum';
    },
  },
  {
    label: '访客人数',
    field: 'info',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 10,
    },
    rules: [
      {
        required: true,
        message: '请输入访客人数',
      },
      { pattern: /^[1-9]\d*$/, message: '请输入大于0的数字', trigger: 'blur' },
    ],
    ifShow: ({values}) => {
      return values.code === 'personNum';
    },
  },
];
