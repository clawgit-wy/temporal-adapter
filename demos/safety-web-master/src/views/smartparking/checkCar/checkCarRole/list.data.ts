import { BasicColumn, FormSchema } from '/@/components/Table';
import { getGroupTree } from './list.api'

export const columns: BasicColumn[] = [
  {
    title: '岗位',
    dataIndex: 'postName',
    width: 120,
  },
  {
    title: '类别',
    dataIndex: 'postCategory',
    width: 150,
  },
  {
    title: '组织架构',
    dataIndex: 'orgNamePath',
    width: 150,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 100,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 100,
  },
  {
    title: '标记',
    dataIndex: 'checkFlag',
    width: 100,
  },
];

export const personColumns: BasicColumn[] = [
  {
    title: '员工号',
    dataIndex: 'empNo',
    width: 120,
  },
  {
    title: '姓名',
    dataIndex: 'empName',
    width: 150,
  },
  {
    title: '手机号',
    dataIndex: 'phoneNo',
    width: 150,
  },
  {
    title: '组织',
    dataIndex: 'orgName',
    width: 100,
  },
  {
    title: '岗位',
    dataIndex: 'positionName',
    width: 100,
  },
  {
    title: '标记',
    dataIndex: 'checkFlag',
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
    label: 'orgCodePath',
    field: 'orgCodePath',
    component: "Input",
    show: false,
  },
  {
    label: '组织架构',
    field: 'organName',
    component: 'Input',
    show: false,
  },
  {
    label: '岗位',
    field: 'postName',
    component: 'Input',
  },
  {
    label: '类别',
    field: 'postCategory',
    required: true,
    component: 'Select',
    componentProps: {
      options: [
        {label: '外包岗位', value: '外包岗位'},
        {label: '直接岗位', value: '直接岗位'},
        {label: '间接岗位', value: '间接岗位'},
        {label: '空', value: '空'},
      ]
    }
  },
  {
    label: '组织架构',
    field: 'organCode',
    component: 'DeptTreeSelect',
    labelField: 'organName',
    componentProps: {
      api: () => getGroupTree,
      invertIdsField: 'orgCodePath'
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
  },
  {
    label: '优先级',
    field: 'priority',
    required: true,
    component: 'Select',
    componentProps: {
      options: [
        {label: 1, value: 1},
        {label: 2, value: 2},
        {label: 3, value: 3},
        {label: 4, value: 4},
        {label: 5, value: 5},
        {label: 6, value: 6},
        {label: 7, value: 7},
        {label: 8, value: 8},
        {label: 9, value: 9},
        {label: 10, value: 10},
      ]
    }
  },
  {
    label: '标记',
    field: 'checkFlag',
    component: 'Select',
    required: true,
    componentProps: {
      options: [
        {label: '重点检查', value: '重点检查'},
        {label: '检查', value: '检查'},
        {label: '不检查', value: '不检查'},
      ]
    }
  },
];
export const perFormSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: 'employee_id',
    field: 'employeeId',
    component: 'Input',
    show: false,
  },
  {
    label: '员工号',
    field: 'empNo',
    required: true,
    slot:'empNo',
    component: 'Select'
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
    componentProps: {
      disabled: true
    }
  },
  {
    label: '手机号',
    field: 'phoneNo',
    component: 'Input',
    componentProps: {
      disabled: true
    }
  },
  {
    label: '组织',
    field: 'orgName',
    component: 'Input',
    componentProps: {
      disabled: true
    }
  },
  {
    label: '岗位',
    field: 'positionName',
    component: 'Input',
    componentProps: {
      disabled: true
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
  },
  {
    label: '标记',
    field: 'checkFlag',
    component: 'Select',
    required: true,
    componentProps: {
      options: [
        {label: '重点检查', value: '重点检查'},
        {label: '检查', value: '检查'},
        {label: '不检查', value: '不检查'},
      ]
    }
  },
];
export const searchFormSchema: FormSchema[] =[
  {
    label: '岗位',
    field: 'postName',
    component: 'Input',
  },
  {
    label: '类别',
    field: 'postCategory',
    component: 'Select',
    componentProps: {
      options: [
        {label: '外包岗位', value: '外包岗位'},
        {label: '直接岗位', value: '直接岗位'},
        {label: '间接岗位', value: '间接岗位'},
        {label: '空', value: '空'},
      ]
    }
  },
  {
    label: '标记',
    field: 'checkFlag',
    component: 'Select',
    componentProps: {
      options: [
        {label: '重点检查', value: '重点检查'},
        {label: '检查', value: '检查'},
        {label: '不检查', value: '不检查'},
      ]
    }
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
] 
export const perSearchFormSchema: FormSchema[] =[
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
  {
    label: '岗位',
    field: 'positionName',
    component: 'Input',
  },
  {
    label: '标记',
    field: 'checkFlag',
    component: 'Select',
    componentProps: {
      options: [
        {label: '重点检查', value: '重点检查'},
        {label: '检查', value: '检查'},
        {label: '不检查', value: '不检查'},
      ]
    }
  },
] 