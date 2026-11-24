import { FormSchema } from '/@/components/Form';
import { BasicColumn } from '/@/components/Table';

// 表单Schema
export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: 'ID',
    defaultValue: '',
    component: 'Input',
    componentProps: {
      placeholder: '请输入ID',
    },
    show: false,
  },
  {
    field: 'accessNum',
    label: '门禁长期未进出天数',
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入天数',
      min: 0,
      precision: 0,
    },
    helpMessage: '超过X天未进出授权门禁的人员自动进入人员变更权限确认列表',
  },
  {
    field: 'cancelAccessNum',
    label: '门禁长期未进出取消授权天数',
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入天数',
      min: 0,
      precision: 0,
    },
    helpMessage: '超过X天未进出授权门禁的人员自动取消门禁权限',
  },
  {
    field: 'offRemindUser',
    label: '门禁长期掉线提醒人',
    component: 'PersonSelect',
    componentProps: {
      multiple: true,
      valueType: 'array',
    },
    helpMessage: '门禁长期(常驻一周)接线提醒人',
  },
  {
    field: 'buildGrantNum',
    label: '门禁一次性授权人数',
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入人数',
      min: 0,
      precision: 0,
    },
    helpMessage: '一次性授权人数超过X人，提醒相应角色',
  },
  {
    field: 'departGrantNum',
    label: '责任部门可授权白名单人数',
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入人数',
      min: 0,
      precision: 0,
    },
    helpMessage: '责任部门可以授权白名单人数',
  },
  {
    field: 'departManageNum',
    label: '责任部门可管理门禁数',
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入数量',
      min: 0,
      precision: 0,
    },
    helpMessage: '责任部门可管理门禁数量',
  },
  {
    field: 'notAccountBuildId',
    label: '不纳入长期未进出门禁',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    helpMessage: '门禁不在监控工作人员或者其他长期未进出工区人员',
  },
];

// ====== 访客授权-组织 列配置 ======
export const orgAuthColumns: BasicColumn[] = [
  { title: '组织', dataIndex: 'empOrgNamePath', width: 450, ellipsis: true },
  { title: '最高被授权人数', dataIndex: 'intervieweeNum', width: 140 },
];

// ====== 访客授权-人 列配置 ======
export const personAuthColumns: BasicColumn[] = [
  { title: '员工号', dataIndex: 'empNo', width: 120 },
  { title: '姓名', dataIndex: 'empName', width: 100 },
  { title: '组织', dataIndex: 'empOrgNamePath', width: 300, ellipsis: true },
  { title: '最高被授权人数', dataIndex: 'intervieweeNum', width: 140 },
];

// ====== 访客授权-组织 弹窗表单 ======
export const orgAuthFormSchema: FormSchema[] = [
  {
    label: '组织',
    field: 'orgSelect',
    component: 'DepartmentSelect',
    slot: 'orgSelect',
    colProps: { span: 24 },
    required: true,
    componentProps: {
      placeholder: '请选择组织',
    },
  },
  {
    label: '',
    field: 'empOrgNamePath',
    component: 'Input',
    colProps: { span: 24 },
    componentProps: { disabled: true },
    ifShow: false,
  },
  {
    label: '',
    field: 'empOrgCodePath',
    component: 'Input',
    colProps: { span: 24 },
    componentProps: { disabled: true },
    ifShow: false,
  },
  {
    label: '最高被授权人数',
    field: 'intervieweeNum',
    component: 'InputNumber',
    colProps: { span: 24 },
    defaultValue: 4,
    componentProps: { placeholder: '请输入', min: 1 },
    required: true,
  },
];

// ====== 访客授权-人 弹窗表单 ======
export const personAuthFormSchema: FormSchema[] = [
  {
    label: '员工号',
    field: 'empNo',
    component: 'Input',
    colProps: { span: 24 },
    slot: 'empNoSlot',
    componentProps: { placeholder: '请输入员工号' },
    required: true,
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
    colProps: { span: 24 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
  {
    label: '组织',
    field: 'empOrgNamePath',
    component: 'Input',
    colProps: { span: 24 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
  {
    label: '最高被授权人数',
    field: 'intervieweeNum',
    component: 'InputNumber',
    colProps: { span: 24 },
    defaultValue: 4,
    componentProps: { placeholder: '请输入', min: 1 },
    required: true,
  },
];
