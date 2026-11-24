import { BasicColumn, FormSchema, Search } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import { getGroupTree } from './list.api';

export const columns: BasicColumn[] = [
  {
    title: '车牌号',
    dataIndex: 'carNo',
    width: 100,
  },
  {
    title: '员工号',
    dataIndex: 'empNo',
    width: 100,
  },
  {
    title: '姓名',
    dataIndex: 'empName',
    width: 100,
  },
  {
    title: '部门',
    dataIndex: 'dept',
    width: 600,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 100,
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
    label: '车牌号',
    field: 'carNo',
    required: true,
    component: 'Input',
  },
  {
    label: '员工号',
    field: 'empNo',
    required: true,
    component: 'Input',
    slot: 'empNo'
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
    componentProps: {
      disabled: true,
    }
  },
  {
    label: '部门',
    field: 'dept',
    component: 'Input',
    componentProps: {
      disabled: true,
    }
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: {
      maxlength: 500,
      rows: 5
    },
  }
];
export const searchFormSchema: FormSchema[] =[
  {
    label: '车牌号',
    field: 'carNo',
    component: 'Input',
  },
  {
    label: '员工号',
    field: 'empNo',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
  },
] 