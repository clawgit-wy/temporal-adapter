import { BasicColumn, FormSchema } from '/@/components/Table';

// 授权状态选项：0-审批中，1-正常，2-注销
export const authStatusOptions = [
  { value: '1', label: '正常', key: '1' },
  { value: '2', label: '注销', key: '2' },
];

export const columns: BasicColumn[] = [
  {
    title: '员工号',
    dataIndex: 'granteeNo',
    width: 100,
  },
  {
    title: '被授权人姓名',
    dataIndex: 'granteeName',
    width: 100,
  },
  {
    title: '组织架构',
    dataIndex: 'granteeOrgName',
    width: 120,
  },
  {
    title: '岗位',
    dataIndex: 'granteePosition',
    width: 100,
  },
  {
    title: '职种',
    dataIndex: 'granteeJobName',
    width: 100,
  },
  {
    title: '授权原因',
    dataIndex: 'authorizationReason',
    width: 120,
  },
  {
    title: '授权时间-始',
    dataIndex: 'authorizationBeginTime',
    width: 140,
  },
  {
    title: '授权时间-终',
    dataIndex: 'authorizationEndTime',
    width: 140,
  },
  {
    title: '授权状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ text }) => {
      const item = authStatusOptions.find((opt) => opt.value === String(text));
      return item ? item.label : text;
    },
  },
  {
    title: '注销原因',
    dataIndex: 'cancelReason',
    width: 120,
  },
  {
    title: '授权人员工号',
    dataIndex: 'grantorNo',
    width: 110,
  },
  {
    title: '授权人姓名',
    dataIndex: 'grantorName',
    width: 100,
  },
  {
    title: '授权人组织架构',
    dataIndex: 'grantorOrgName',
    width: 130,
  },
  {
    title: '授权人岗位',
    dataIndex: 'grantorPosition',
    width: 110,
  },
  {
    title: '授权人职种',
    dataIndex: 'grantorJobName',
    width: 110,
  },
  {
    title: '授权流程',
    dataIndex: 'bpmFlowId',
    width: 100,
    slots: { customRender: 'bpmFlow' },
  },
];

// 操作列
export const getTableAction = (hasPermission) => {
  return [
    {
      label: '编辑',
      ifShow: () => hasPermission('registration:visitorApproval:add'),
    },
  ];
};

// 弹窗表单配置
export const modalFormSchema: FormSchema[] = [
  {
    label: '被授权人员工号',
    field: 'granteeNo',
    component: 'Input',
    colProps: { span: 24 },
    slot: 'granteeNoSlot',
    componentProps: { placeholder: '请输入被授权人员工号' },
    helpMessage: '输入员工号自动带出人员姓名和组织。',
  },
  {
    label: '被授权人姓名',
    field: 'granteeName',
    component: 'Input',
    colProps: { span: 24 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
  {
    label: '被授权人组织',
    field: 'granteeOrgName',
    component: 'Input',
    colProps: { span: 24 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
  {
    label: '授权人员工号',
    field: 'grantorNo',
    component: 'Input',
    colProps: { span: 24 },
    slot: 'grantorNoSlot',
    componentProps: { placeholder: '请输入授权人员工号' },
    helpMessage: '输入员工号自动带出人员姓名和组织。',
  },
  {
    label: '授权人姓名',
    field: 'grantorName',
    component: 'Input',
    colProps: { span: 24 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
  {
    label: '授权人组织',
    field: 'grantorOrgName',
    component: 'Input',
    colProps: { span: 24 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
  {
    label: '授权时间',
    field: 'authorizationTime',
    component: 'RangePicker',
    colProps: { span: 24 },
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      placeholder: ['开始日期', '结束日期'],
    },
  },
  {
    label: '授权原因',
    field: 'authorizationReason',
    component: 'InputTextArea',
    colProps: { span: 24 },
    componentProps: {
      placeholder: '请输入',
      maxLength: 500,
      showCount: true,
      rows: 4,
    },
    helpMessage: '自定义输入，500字以内',
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '被授权人',
    field: 'granteeInfo',
    component: 'Input',
    colProps: { span: 6 },
    componentProps: {
      placeholder: '请输入被授权人姓名或员工号',
    },
  },
  {
    label: '授权人',
    field: 'grantorInfo',
    component: 'Input',
    colProps: { span: 6 },
    componentProps: {
      placeholder: '请输入授权人姓名或员工号',
    },
  },
  {
    label: '授权状态',
    field: 'status',
    component: 'Select',
    colProps: { span: 6 },
    defaultValue: '1',
    componentProps: {
      options: authStatusOptions,
      placeholder: '请选择授权状态',
    },
  },
];
