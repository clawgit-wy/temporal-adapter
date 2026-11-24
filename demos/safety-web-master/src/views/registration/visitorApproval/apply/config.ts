import { FormSchema } from '/@/components/Form';
import dayjs from 'dayjs';

/** 提交人信息表单 */
export const submitterFormSchema: FormSchema[] = [
  {
    label: '姓名',
    field: 'submitterName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: { disabled: true },
  },
  {
    label: '员工号',
    field: 'submitterNo',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: { disabled: true },
  },
  {
    label: '部门',
    field: 'submitterOrgName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: { disabled: true },
  },
];

/** 授权人信息表单 */
export const authorizerFormSchema: FormSchema[] = [
  {
    label: '授权人员工号',
    field: 'grantorNo',
    component: 'Input',
    colProps: { span: 12 },
    slot: 'grantorNoSlot',
    componentProps: { placeholder: '点击选择授权人', readonly: true },
    rules: [{ required: true, message: '请选择授权人' }],
  },
  {
    label: '授权人姓名',
    field: 'grantorName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
  {
    label: '岗位',
    field: 'grantorPosition',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
  {
    label: '职种',
    field: 'grantorJobName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
  {
    label: '组织架构',
    field: 'grantorOrgName',
    component: 'Input',
    colProps: { span: 12 },
    componentProps: { disabled: true, placeholder: '自动带出' },
  },
];

/** 其他信息表单 */
export const otherFormSchema: FormSchema[] = [
  {
    label: '授权时间范围',
    field: 'authorizationTime',
    component: 'RangePicker',
    colProps: { span: 24 },
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      placeholder: ['开始日期', '结束日期'],
      disabledDate: (current: dayjs.Dayjs) => {
        return current && current < dayjs().startOf('day');
      },
    },
    rules: [{ required: true, message: '请选择授权时间范围' }],
  },
  {
    label: '授权人科长',
    field: 'sectionChiefName',
    component: 'Input',
    colProps: { span: 24 },
    slot: 'sectionChiefSlot',
    componentProps: { placeholder: '点击选择科长', readonly: true },
  },
  {
    label: '科长员工号',
    field: 'sectionChiefNo',
    component: 'Input',
    colProps: { span: 0 },
    ifShow: () => false,
  },
  {
    label: '授权原因',
    field: 'authorizationReason',
    component: 'InputTextArea',
    slot: 'authorizationReasonSlot',
    colProps: { span: 24 },
    componentProps: {
      placeholder: '请输入',
      maxLength: 500,
      showCount: true,
      rows: 4,
    },
    rules: [{ required: true, message: '请输入授权原因' }],
    helpMessage: '自定义输入，500字以内',
  },
];
