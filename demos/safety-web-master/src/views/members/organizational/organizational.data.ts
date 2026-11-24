import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';

export const columns: BasicColumn[] = [
  {
    title: '员工号',
    dataIndex: 'empNo',
    width: 120,
  },
  {
    title: '员工姓名',
    dataIndex: 'empName',
    width: 100,
  },
  {
    title: '公司名称',
    dataIndex: 'companyName',
    width: 150,
  },
  {
    title: '部门',
    dataIndex: 'deptName',
    width: 150,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 160,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '员工号',
    field: 'empNo',
    component: 'Input',
  },
  {
    label: '员工姓名',
    field: 'empName',
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
    label: '员工号',
    field: 'empNo',
    component: 'Input',
    required: true,
    rules: [
      {
        required: true,
        message: '请输入员工号',
      },
    ],
    componentProps: {
      maxlength: 11,
    },
  },
  {
    label: '员工姓名',
    field: 'empName',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 11,
    },
    rules: [
      {
        required: true,
        message: '请输入员工姓名',
      },
    ],
  },
  {
    label: '公司名称',
    field: 'companyName',
    component: 'Input',
    required: false,
    componentProps: {
      maxlength: 16,
    },
  },
  {
    label: '部门',
    field: 'deptName',
    component: 'Input',
    required: false,
    componentProps: {
      maxlength: 50,
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
    required: false,
    componentProps: {
      maxlength: 100,
    },
  },
];
