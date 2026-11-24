import { BasicColumn, FormSchema } from '/@/components/Table';
import dayjs from 'dayjs';

export const columns: BasicColumn[] = [
  {
    title: '角色名称',
    dataIndex: 'roleName',
    width: 150,
  },
  {
    title: '修改人',
    dataIndex: 'lastUser',
    width: 100,
  },
  {
    title: '修改时间',
    dataIndex: 'lastTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '角色名称',
    field: 'roleName',
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
    label: '角色名称',
    field: 'roleName',
    component: 'Input',
    required: true,
  },
  {
    label: '权限管理',
    field: 'permIds',
    component: 'Input',
    required: false,
    show: false,
  },
  {
    label: '权限管理',
    field: 'state',
    component: 'Input',
    required: true,
    slot: 'permIds',
    rules: [
      {
        required: true,
        message: '请选择对应权限',
      },
    ],
  },
];
