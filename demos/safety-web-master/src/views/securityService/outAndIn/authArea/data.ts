import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '厂区',
    dataIndex: 'creatorName',
  },
  {
    title: '区域',
    dataIndex: 'creatorNo',
  },
  {
    title: '授权分类',
    dataIndex: 'empNo',
  },
  {
    title: '授权设备号',
    dataIndex: 'empName',
  },
  {
    title: '更新时间',
    dataIndex: 'empOrg',
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    fixed: 'right',
  },
];

export const addFormSchemas: FormSchema[] = [];
