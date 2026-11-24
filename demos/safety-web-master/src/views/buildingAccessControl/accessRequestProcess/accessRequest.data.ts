import { BasicColumn, FormSchema } from '/@/components/Table';
import dayjs from 'dayjs';
import { getNewDictItems } from '/@/api/common/api';
import { render } from '/@/utils/common/renderUtils';
import { ref } from 'vue';
import { useRangePickerWithLimit } from '/@/utils/datePicker';

// 申请状态
const requestStatusOptions = [
  { label: '草稿', value: '10' },
  { label: '待审批', value: '20' },
  { label: '发布', value: '30' },
  { label: '废弃', value: '00' },

];

// 申请类型
const requestTypeOptions = [
  { label: '临时权限', value: '1' },
  { label: '长期权限', value: '2' },
];

// 定义表格列
export const columns: BasicColumn[] = [
  {
    title: '流程编号',
    dataIndex: 'processCode',
    slots: { customRender: 'processCode' },
  },
  {
    title: '提交时间',
    dataIndex: 'createTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : ''),
  },
  {
    title: '申请人',
    dataIndex: 'createEmployeeName',
    width: 100,
  },
  {
    title: '角色',
    dataIndex: 'roleType',
    width: 80,
    customRender: ({ text }) => {
      const roleMap = {
        '1': '员工',
        '2': '管理员',
        '3': '门禁管理员',
      };
      return roleMap[text] || text;
    },
  },
  {
    title: '申请人部门',
    dataIndex: 'createDeptName',
    resizable: true,
    customRender: ({ text }) => {
      return render.renderTip(text);
    },
  },
  {
    title: '厂区',
    dataIndex: 'montageFactoryNameStr',
    customRender: ({ text }) => {
      return render.renderTip(text);
    },
  },
  {
    title: '楼栋',
    dataIndex: 'montageBuildNameStr',
    width: 100,
  },
  {
    title: '申请门禁',
    dataIndex: 'montageDeviceNameStr',
    customRender: ({ text }) => {
      return render.renderTip(text);
    },
  },
  {
    title: '对接部门',
    dataIndex: 'montageDeptStr',
    customRender: ({ text }) => {
      return render.renderTip(text);
    },
  },
  {
    title: '申请授权期限',
    dataIndex: 'montageTimeStr',
    width: 200,
  },
  {
    title: '流程状态',
    dataIndex: 'status',
    width: 100,
    customRender: ({ text }) => {
      const status = requestStatusOptions.find((item) => item.value === text);
      return status ? status.label : text;
    },
  },
  {
    title: '当前责任人',
    dataIndex: 'curDutyPerson',
    width: 120,
  },
];

// 定义搜索表单
export const searchFormSchema: FormSchema[] = [
  {
    label: '申请人',
    field: 'creator',
    component: 'Input',
    componentProps: {
      placeholder: '姓名/员工号',
    },
    colProps: { span: 6 },
  },
  {
    label: '流程状态',
    field: 'status',
    component: 'Select',
    componentProps: {
      placeholder: '全部',
      options: requestStatusOptions,
    },
    colProps: { span: 6 },
  },
  {
    label: '当前责任人',
    field: 'curDutyPerson',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  {
    label: '申请人部门',
    field: 'createDept',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  {
    label: '对接部门',
    field: 'acrossDept',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  {
    label: '厂区',
    field: 'factoryName',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  {
    label: '楼栋',
    field: 'buildingName',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  {
    label: '门禁',
    field: 'deviceName',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  {
    label: '提交时间',
    field: 'createTimeRange',
    component: 'RangePicker',
    defaultValue: [dayjs().subtract(30, 'days').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')],
    componentProps: useRangePickerWithLimit(30),
  },
];

// 定义表单
export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '申请编号',
    field: 'requestNo',
    component: 'Input',
    show: false,
  },
  {
    label: '申请类型',
    field: 'requestType',
    component: 'RadioGroup',
    defaultValue: '1',
    componentProps: {
      options: requestTypeOptions,
    },
    rules: [{ required: true, message: '请选择申请类型' }],
  },
  {
    label: '使用日期',
    field: 'useDateRange',
    component: 'RangePicker',
    componentProps: ({ formModel }) => {
      return {
        format: 'YYYY-MM-DD',
        placeholder: ['开始日期', '结束日期'],
        disabled: false,
        onChange: (dates) => {
          if (dates) {
            formModel.startDate = dates[0].format('YYYY-MM-DD');
            formModel.endDate = dates[1].format('YYYY-MM-DD');
          } else {
            formModel.startDate = null;
            formModel.endDate = null;
          }
        },
      };
    },
    rules: [{ required: true, message: '请选择使用日期' }],
  },
  {
    label: '开始日期',
    field: 'startDate',
    component: 'Input',
    show: false,
  },
  {
    label: '结束日期',
    field: 'endDate',
    component: 'Input',
    show: false,
  },
  {
    label: '申请门禁',
    field: 'doorAccessList',
    component: 'Input',
    slot: 'doorAccessList',
    rules: [{ required: true, message: '请选择申请门禁' }],
  },
  {
    label: '申请说明',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: {
      placeholder: '请输入申请说明',
      rows: 4,
      maxlength: 500,
      showCount: true,
    },
  },
];
