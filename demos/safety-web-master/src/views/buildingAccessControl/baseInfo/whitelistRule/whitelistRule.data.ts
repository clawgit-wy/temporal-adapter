import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

// 表格列定义
export const columns: BasicColumn[] = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    align: 'center',
  },
  {
    title: '岗位',
    dataIndex: 'postName',
    width: 120,
    align: 'center',
  },
  {
    title: '组织',
    dataIndex: 'orgNamePath',
    width: 200,
  },
  {
    title: '纳入白名单人数',
    dataIndex: 'whiteCount',
    width: 150,
    align: 'center',
  },
  {
    title: '修改人',
    dataIndex: 'updateUser',
    width: 100,
    align: 'center',
  },
  {
    title: '修改时间',
    dataIndex: 'updateTime',
    width: 150,
    align: 'center',
  },
  {
    title: '原因',
    dataIndex: 'remark',
    width: 180,
  },
];

// 搜索表单定义
export const searchFormSchema: FormSchema[] = [
  {
    field: 'searchName',
    label: '岗位或组织名称',
    component: 'Input',
    colProps: { span: 8 },
  },
  {
    field: 'idStr',
    label: '规则ID',
    component: 'Input',
    colProps: { span: 8 },
  },
];

// 统一的表单模式
export const formSchema: FormSchema[] = [
  {
    field: 'ruleType',
    label: '类别',
    component: 'Select',
    defaultValue: '0',
    componentProps: {
      options: [
        { label: '按岗位', value: '0' },
        { label: '按组织', value: '1' },
      ],
    },
    required: true,
  },
  {
    field: 'postName',
    label: '岗位名称',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
      maxLength: 50,
    },
    ifShow: ({ values }) => values.ruleType === '0',
    required: ({ values }) => values.ruleType === '0',
  },
  {
    field: 'organization',
    label: '组织',
    component: 'DepartmentSelect',
    componentProps: {
    },
    ifShow: ({ values }) => values.ruleType === '1',
    required: ({ values }) => values.ruleType === '1',
  },
  {
    field: 'remark',
    label: '原因',
    component: 'InputTextArea',
    componentProps: {
      placeholder: '请输入',
      rows: 4,
      maxLength: 50,
    },
    required: true,
  },
]; 
