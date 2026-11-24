import { FormSchema } from '/@/components/Form';
import { BasicColumn } from '/@/components/Table';

import { reactive } from 'vue';
import dayjs from 'dayjs';
import { createFixedStartDateField, useFixedStartDateRangePicker } from '/@/utils/datePicker';
// 定义表单Schema
export const formSchema: FormSchema[] = [
  {
    field: 'createEmployeeName',
    label: '姓名',
    component: 'Input',
    required: false,
    componentProps: {
      placeholder: '',
      disabled: true,
    },
    colProps: { span: 12 },
  },
  {
    field: 'createEmployeeNo',
    label: '员工号',
    component: 'Input',
    required: false,
    componentProps: {
      placeholder: '',
      disabled: true,
    },
    colProps: { span: 12 },
  },
  {
    field: 'createDeptName',
    label: '部门',
    component: 'Input',
    slot: 'createDeptName',
    required: false,
    componentProps: {
      placeholder: '',
      disabled: true,
    },
    colProps: { span: 12 },
  },
  {
    field: 'authorizedDoors',
    label: '已授权门禁',
    component: 'Input',
    required: false,
    componentProps: {
      placeholder: '',
      disabled: true,
    },
    colProps: { span: 12 },
  },
  {
    field: 'doorAccessTips',
    label: '',
    component: 'Input',
    slot: 'doorAccessTips',
    colProps: { span: 24 },
    ifShow: () => true,
    dynamicDisabled: true,
    defaultValue: '',
    show: true,
  },
  {
    field: 'doorAccessSelection',
    label: '',
    component: 'Input',
    slot: 'doorAccessSelection',
    colProps: { span: 24 },
    show: true,
  },
  {
    field: 'applyDateRange',
    label: '申请期限',
    component: 'RangePicker',
    required: true,
    defaultValue: [dayjs()], // 默认为当前日期到一个月后
    componentProps: () => {
      return {
        ...useFixedStartDateRangePicker(),
      };
    },
    slot: 'applyDateRangeSlot',
    colProps: { span: 12 },
    dynamicRules: () => {
      return [
        {
          required: true,
          validator: (_, value) => {
            // console.log('校验日期:', value[0]);
            if (!value || !value[1]) {
              return Promise.reject('请选择结束日期');
            }
            return Promise.resolve();
          },
          trigger: ['change', 'blur'],
        },
      ];
    },
  },
  {
    label: '事由',
    field: 'applyReason',
    component: 'InputTextArea',
    required: true,
    //属性
    componentProps: {
      //可以点击清除图标删除内容
      allowClear: true,
      //是否展示字数
      // showCount: true,
      maxLength: 200,
      //自适应内容高度，可设置为 true | false 或对象：{ minRows: 2, maxRows: 6 }
      autoSize: {
        //最小显示行数
        minRows: 2,
        //最大显示行数
        maxRows: 3,
      },
    },
  },
  {
    label: '本部门科长',
    field: 'chiefEmployeeName',
    component: 'Input',
    slot: 'secretAreaManagerSlot',
    required: true,
    componentProps: {
      placeholder: '请选择本部门科长',
    },
  },
  {
    label: '本部门科长登录名',
    field: 'chiefEmployeeLogin',
    component: 'Input',
    componentProps: {
      placeholder: '请选择',
    },
    show: false,
  },
];

// 门禁项数据结构
export const createDoorItem = () => {
  return {
    key: '', // 门禁编码
    title: '', // 门禁名称
    targetDept: '', // 对接部门
    deviceDOList: [], // 关联的设备列表
  };
};

// 表单校验规则
export const formRules = {
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  reason: [{ required: true, message: '请输入事由', trigger: 'blur' }],
  doorManager: [{ required: true, message: '请选择本部门门禁长', trigger: ['change', 'blur'] }],
};

export const tabelColumns: BasicColumn[] = [
  {
    title: '门禁',
    dataIndex: 'deviceName',
    editRow: false,
    editComponentProps: {},
  },
  {
    title: '对接部门',
    dataIndex: 'dockingDeptCode',
    editRow: true,
    editComponent: 'Select',
    editComponentProps: {
      options: [
        {
          label: 'Option1',
          value: '1',
        },
        {
          label: 'Option2',
          value: '2',
        },
        {
          label: 'Option3',
          value: '3',
        },
      ],
    },
    slots: {
      customRender: 'dockingDeptCode',
    },
  },
  {
    title: '对接部门门禁管理员',
    dataIndex: 'acrossEmployeeName',
    editRow: false,
    // 默认必填校验
    editRule: true,
    width: 250,
  },
  {
    title: '绝密区域负责人',
    dataIndex: 'topSecretEmpName',
    editRow: false,
    customRender: ({ record }) => {
      return `${record.topSecretEmpName || ''}${record.topSecretEmpCode || ''}` || '非公司绝密区';
    },
  },
  {
    title: '对应开通公共区域门禁',
    dataIndex: 'publicDeviceCode',
    editRow: false,
    editRule: true,
    editComponent: 'InputNumber',
    slots: {
      customRender: 'publicDeviceCode',
    },

    width: 300,
  },
  {
    title: '审批过程记录',
    dataIndex: 'refuseReason',
    editRow: false,
    customRender: ({ record }) => {
      return record.refuseReason||'/'
    },
  }
];
