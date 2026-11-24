import { FormSchema } from '/@/components/Form';
import { reactive ,unref,toRaw} from 'vue';
import dayjs from 'dayjs';
import {  BasicColumn } from '/@/components/Table';

// 定义表单Schema
export const formSchema: FormSchema[] = [
  {
    field: 'processSubject',
    label: '流程主题',
    component: 'Input',
    required: true,
    show: false,
  },
  {
    field: 'createDeptCode',
    label: '申请人部门全路径',
    component: 'Input',
    required: true,
    show: false,
  },
  {
    field: 'createDeptName',
    label: '申请人部门全路径',
    component: 'Input',
    required: true,
    show: false,
  },
  {
    field: 'createEmployeeName',
    label: '姓名',
    component: 'Input',
    required: false,
    componentProps: {
      placeholder: '默认带出',
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
      placeholder: '默认带出',
      disabled: true,
    },
    colProps: { span: 12 },
  },
  {
    field: 'createDeptName',
    label: '部门',
    component: 'Input',
    required: false,
    show:true,
    componentProps: {
      placeholder: '默认带出',
      disabled: true,
    },
    colProps: { span: 12 },
  },
  {
    label: '类型',
    field: 'powerType',
    component: 'CheckboxGroup',
    colProps: { span: 24 },
    componentProps:{
      options: [{label: "开通", value: '1'}, {label: '关闭', value: '2'}],
      
    },
    
    // slot: 'shortBargeCar',
    // componentProps: {
    //   options: [{label: "是", value: '是'}, {label: '否', value: '否'}],
    // },
  },
  {
    field: 'doorAccessTips',
    label: '',
    component: 'Input',
    slot: 'doorAccessTips',
    colProps: { span: 24 },
    ifShow: ({values}) => values.powerType?.includes('1'),
    dynamicDisabled: true,
    defaultValue: '',
    
  },
  {
    field: 'openDOList',
    label: '',
    component: 'Input',
    slot: 'openDOList',
    colProps: { span: 24 },
    ifShow: ({values}) => values.powerType?.includes('1'),
  },

  {
    field: 'doorAccessCloseTips',
    label: '',
    component: 'Input',
    slot: 'doorAccessCloseTips',
    colProps: { span: 24 },
    ifShow:({values}) => values.powerType?.includes('2'),
  },
  {
    field: 'closeList',
    label: '',
    component: 'Input',
    slot: 'closeList',
    colProps: { span: 24 },
    ifShow:({values}) => values.powerType?.includes('2'),
  },
  // {
  //   field: 'applyDateRange',
  //   label: '申请期限',
  //   component: 'RangePicker',
  //   required: true,
  //   // defaultValue: [dayjs(), dayjs().add(1, 'month')], // 默认为当前日期到一个月后
  //   componentProps: {
  //     format: 'YYYY-MM-DD',
  //     valueFormat: 'YYYY-MM-DD',
  //     placeholder: ['开始日期', '结束日期'],
  //     style: {
  //       width: '100%',
  //     },
  //   },
  //   slot: 'applyDateRangeSlot',
  //   colProps: { span: 12 },
  // },
  {
    label: '事由',
    field: 'applyReason',
    component: 'InputTextArea',
    required: true,
    //属性
    componentProps: {
      maxlength: 200,
      //可以点击清除图标删除内容
      allowClear: true,
      //是否展示字数
      // showCount: true,
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
   show: false
  },
];

function buildingFun(labelName): FormSchema {
  return {
    field: 'empowerAreaName',
    label: labelName,
    component: 'Select',
    required: true,
    slot: 'empowerAreaName',
    colProps: { span: 24 },
    componentProps: {
      placeholder: '请选择',
    },
  };
}
export const scmSchema: FormSchema[] =
  [
    {
      label: "门禁",
      field: "doorAccessSelection",
      component: "Input",
      show:true,
      required:true,
      slot:'doorAccessSelection',
      componentProps:{
        valueType: 'array',
      }
    },
    {
      field: 'empowerType',
      label: '类型',
      component: 'Select',
      required: true,
      colProps: { span: 24 },
      defaultValue: '2',
      componentProps: ({ formActionType, formModel }) => {
        return {
          placeholder: '请选择',
          options: [
            { label: '按人员', value: '1' },
            { label: '按组织', value: '2' },
          ],
          onChange: (val) => {
            const empowerAreaName = buildingFun(val == 1 ? '人员名单' : '授权范围');
            formActionType.updateSchema([empowerAreaName]);
            formModel['empowerAreaName'] = undefined;
            formModel['empowerAreaCode'] = undefined;
          },
        };
      },
    },

    {
      field: 'empowerAreaName',
      label: '授权范围',
      component: 'Select',
      required: true,
      slot: 'empowerAreaName',
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请选择',
      },
    },

    {
      field: 'empowerAreaCode',
      label: '授权范围',
      component: 'Select',
      required: true,
      show: false,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请选择对接部门',
      },
    },
    
  ]

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
  doorManager: [{ required: true, message: '请选择本部门门禁长', trigger: 'change' }],
};

export const empowerTypeOptions=[
  { label: '按人员', value: '1' },
  { label: '按组织', value: '2' },
]
export const tableOpenColumns: BasicColumn[] = [
  {
    title: '类型',
    dataIndex: 'empowerType',
    editComponent: 'Select',
    slots: { customRender: 'empowerType' },
    // editComponentProps: {
    //   defaultValue: '2',
    //   placeholder: '请选择',
    //     options: [
    //       { label: '按人员', value: '1' },
    //       { label: '按组织', value: '2' },
    //     ],
    // },
    // customRender: ({ text }) => {
    //   if (text === '1') {
    //     return '按人员';
    //   }
    //   if (text === '2') {
    //     return '按组织';
    //   }
    //   return '';
    // },
  },
  {
    title: '授权范围',
    dataIndex: 'empowerAreaNameView',
    editRow: true,
    // 默认必填校验
    editRule: true,
    slots: { customRender: 'empowerAreaNameView' },
  },
  {
    title: '授权范围',
    dataIndex: 'empowerAreaName',
    ifShow:false,
  },
  {
    title: '授权范围',
    dataIndex: 'empowerAreaCode',
    ifShow:false,
  },
  {
    title: '开通期限',
    dataIndex: 'powerTime',
    editComponent: 'DatePicker',
    slots: { customRender: 'powerTime' },
    width:250,
  },
  {
    title: '开通门禁',
    dataIndex: 'deviceName',
  },
  {
    title: '开通门禁',
    dataIndex: 'deviceCode',
    ifShow:false,
  },
  
  {
    title: '对接部门',
    dataIndex: 'buildSysManagerRole',
    editComponent: 'Select',
    ifShow:false,
    
  },
  {
    title: '对接部门',
    dataIndex: 'dockingDeptCode',
    editComponent: 'Select',
    slots: { customRender: 'dockingDeptCode' },
    
  },
  {
    title: '对接部门门禁管理员',
    dataIndex: 'acrossEmployeeName',
    editRow: false,
    // 默认必填校验
    editRule: true,
  },
  {
    title: '绝密区域负责人',
    dataIndex: 'topSecretEmpName',
    editRow: false,
    customRender: ({ record }) => {
      return (record.topSecretEmpName||record.topSecretEmpCode)?`${record.topSecretEmpName || ''}${record.topSecretEmpCode || ''}`:"非公司绝密区";
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

export const tableCloseColumns: BasicColumn[] = [
  {
    title: '类型',
    dataIndex: 'empowerType',
    editComponent: 'Select',
    slots: { customRender: 'empowerType' },
    // editComponentProps: {
    //   defaultValue: '2',
    //   placeholder: '请选择',
    //     options: [
    //       { label: '按人员', value: '1' },
    //       { label: '按组织', value: '2' },
    //     ],
    // },
  },
  {
    title: '组织/人员',
    dataIndex: 'empowerAreaNameView',
    editRow: true,
    // 默认必填校验
    editRule: true,
    slots: { customRender: 'empowerAreaNameView' },
  },
  {
    title: '组织/人员',
    dataIndex: 'empowerAreaName',
    ifShow:false,
  },
  {
    title: '组织/人员',
    dataIndex: 'empowerAreaCode',
    ifShow:false,
  },
  {
    title: '门禁',
    dataIndex: 'deviceName',
  },
  {
    title: '门禁',
    dataIndex: 'deviceCode',
    ifShow:false,
  },
  
];

