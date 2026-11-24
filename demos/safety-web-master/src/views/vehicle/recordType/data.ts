import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '人员类型',
    dataIndex: 'itemName',
  },
  {
    title: '人员类型编号',
    dataIndex: 'itemCode',
  },

  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    fixed: 'right',
  },
];

//表单搜索字段
export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '人员类型', //显示label
    field: 'itemName', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '人员类型编号',
    field: 'itemCode',
    component: 'Input',
  },
];

// export const addFormSchema: FormSchema[] = [{}];
