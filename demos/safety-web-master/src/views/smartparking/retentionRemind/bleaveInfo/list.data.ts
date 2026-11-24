import { BasicColumn, FormSchema, Search } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import { getGroupTree } from './list.api';

export const columns: BasicColumn[] = [
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
    title: '部门',
    dataIndex: 'staffGroup',
    width: 600,
  },
  {
    title: '公出开始日期',
    dataIndex: 'beginDate',
    width: 100,
  },
  {
    title: '公出结束日期',
    dataIndex: 'endDate',
    width: 100,
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
    label: '员工号',
    field: 'staffNo',
    required: true,
    component: 'Input',
    slot: 'staffNo'
  },
  {
    label: '姓名',
    field: 'staffName',
    component: 'Input',
    componentProps: {
      disabled: true,
    }
  },
  {
    label: '部门',
    field: 'staffGroup',
    component: 'Input',
    componentProps: {
      disabled: true,
    }
  },
  {
    label: '公出开始时间',
    field: 'beginDate',
    component: 'DatePicker',
    required: true,
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '公出结束时间',
    field: 'endDate',
    component: 'DatePicker',
    required: true,
    componentProps: {
      valueType: 'Date',
    },
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
    label: '员工号',
    field: 'staffNo',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'staffName',
    component: 'Input',
  },

  {
    label: '公出开始时间',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
] 