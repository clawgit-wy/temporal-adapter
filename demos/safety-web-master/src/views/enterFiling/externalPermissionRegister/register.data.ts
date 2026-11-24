import { BasicColumn, FormSchema } from '/@/components/Table';
import dayjs from 'dayjs';
import { render } from '/@/utils/common/renderUtils';
const stateOptions = [
  { label: '驳回', value: 10 },
  { label: '待审批', value: 20 },
  { label: '通过', value: 30 },
];
// 表格列配置
export const columns: BasicColumn[] = [
  {
    title: '姓名',
    dataIndex: 'fdName',
    width: 100,
  },
  {
    title: '手机号',
    dataIndex: 'fdPhone',
    width: 130,
  },
  {
    title: '所属公司',
    dataIndex: 'fdCompany',
    width: 200,
  },
  {
    title: '对接人姓名',
    dataIndex: 'buttJointName',
    width: 100,
  },
  {
    title: '对接人手机号',
    dataIndex: 'buttJointPhone',
    width: 130,
  },
  {
    title: '申请时间',
    dataIndex: 'createTime',
    width: 170,
  },
  {
    title: '事由',
    dataIndex: 'reason',
    width: 200,
    customRender: ({ text }) => {
      return render.renderTip(text, 20);
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ text }) => {
      return render.renderDictNative(text, stateOptions);
    },
  },
];

// 搜索表单配置
export const searchFormSchema: FormSchema[] = [
  {
    label: '姓名',
    field: 'fdName',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  // {
  //   label: '身份证号',
  //   field: 'fdIdCard',
  //   component: 'Input',
  //   componentProps: {
  //     placeholder: '请输入',
  //   },
  //   colProps: { span: 6 },
  // },
  {
    label: '手机号',
    field: 'fdPhone',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  {
    label: '所属公司',
    field: 'fdCompany',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  {
    label: '对接人',
    field: 'buttJoint',
    component: 'Input',
    componentProps: {
      placeholder: '请输入姓名或员工号',
    },
    colProps: { span: 6 },
  },
  {
    label: '状态',
    field: 'status',
    component: 'Select',
    componentProps: {
      placeholder: '请选择',
      options: stateOptions,
    },
    colProps: { span: 6 },
  },
];
