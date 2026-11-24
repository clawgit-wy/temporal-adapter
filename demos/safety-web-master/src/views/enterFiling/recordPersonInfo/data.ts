import { BasicColumn, FormSchema } from '/@/components/Table';
import { render ,defaultMonthValue} from '/@/utils/common/renderUtils';
import { fieldChildTree,getDictEnterType} from './api';
import { StatusOptions, StatusEnumMap,entryMethodEnumMap } from './const'
import dayjs from 'dayjs';
export const columns: BasicColumn[] = [
  {
    title: '状态',
    dataIndex: 'status',
    customRender: ({ text }) => {
      // console.log(text, String(text), 'text');
      return StatusEnumMap[String(text)];
    },
    width:100
  },
  {
    title: '对接人员工号',
    dataIndex: 'buttJointCode',
    width:100
  },
  {
    title: '对接人姓名',
    dataIndex: 'buttJointName',
    width:100
  },
  {
    title: '备案类型',
    dataIndex: 'recordType',
    width:120
  },
  {
    title: '姓名',
    dataIndex: 'name',
    width:80
  },
  {
    title: '公司名称',
    dataIndex: 'companyName',
  },
  {
    title: '身份证号',
    dataIndex: 'cardNo',
    width:180
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    width:100
  },
  {
    title: '准入厂区',
    dataIndex: 'enterFactory',
  },
  {
    title: '入厂方式',
    dataIndex: 'entryMethod',
    customRender:({ text }) => {
      return String(text)&&String(text)!="null"?entryMethodEnumMap[String(text)]:"/";
    },
  },
  {
    title: '准入时间-始',
    dataIndex: 'enterStartTime',
  },
  {
    title: '准入时间-终',
    dataIndex: 'enterEndTime',
  },
  {
    title: '是否夜间作业',
    dataIndex: 'nightFlag',
    customRender: ({ text }) => {
      console.log(text, String(text), 'text');
      return String(text)=='0'?'否':'是';
    },
    width:50
  },
  {
    title: '备注',
    dataIndex: 'remark',
  },
  {
    title: '证件照',
    dataIndex: 'photoUrl',
    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return render.renderImage2({ text });
      }
    },
  },
  {
    title: '未进出天数',
    dataIndex: 'unEnterNum',
    ifShow:false
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '提交人手机号',
    dataIndex: 'submitPhone',
    width:100
  },

  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    fixed: 'right',
  },
];

//表单搜索字段
export const formSchemas: FormSchema[] = [
  {
    label: '对接人', //显示label
    field: 'buttJoint', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
      label: '备案类型',
      field: 'recordTypeCode',
      component: 'ApiSelect',
      colProps: { span: 8 },
      componentProps: {
        api: getDictEnterType,
        valueField: 'itemCode',
        labelField: 'itemName',
      },
    },
    {
      label: "准入厂区",
      field: "factoryCode",
      component: 'ApiSelect',
      colProps: { span: 8 },
      componentProps: {
        api: fieldChildTree,
        params: {
        fieldType: 'factory',
        },
        valueField: 'id',
        labelField: 'fieldName'
      }
    },
  {
    label: '准入时间',
    field: 'enterTime',
    component: 'RangePicker',
    defaultValue: [dayjs().subtract(1, 'month').format('YYYY-MM-DD'), dayjs().add(1, 'month').format('YYYY-MM-DD')],
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      placeholder: ['准入时间-开始', '准入时间-截止'],
    },
  },
  {
    label: '状态',
    field: 'status',
    component: 'Select',
    componentProps: {
          placeholder: '请选择状态',
          options: StatusOptions,
        },
  },
  {
    label: '姓名', //显示label
    field: 'name', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '身份证号', //显示label
    field: 'cardNo', //查询字段
    component: 'InputTextArea', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '手机号', //显示label
    field: 'phone', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '公司名称', //显示label
    field: 'companyName', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '备注', //显示label
    field: 'remark', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '未进出天数', //显示label
    field: 'itemName', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
    show:false
  },
];

export const editSchema:FormSchema[]=[
  {
    label: '', //显示label
    field: 'reason', //查询字段
    component: 'InputTextArea',
    slot:'secondTitle'
  },
  {
    label: '备案厂区', //显示label
    field: 'enterFactoryCode', //查询字段
    required: true,
    component: 'JSelectMultiple',
    componentProps: {
      
    }
  },
  {
    label: '备案时间', //显示label
    field: 'enterTime', //查询字段
    required: true,
    component: 'RangePicker',
    componentProps: {
      showTime: true,
      format: 'YYYY-MM-DD HH:mm',
      valueFormat: 'YYYY-MM-DD HH:mm',
      placeholder: ['准入时间-开始', '准入时间-截止'],
      valueType: 'Date',
    },
  },
]
export const deleteFormSchema:FormSchema[]=[
  {
    label: '原因', //显示label
    field: 'reason', //查询字段
    required: true,
    component: 'InputTextArea',
    componentProps: {
      rows: 2,
      maxlength: 20,
      showCount: true,
    }
  },
]
export const configFormSchemas:FormSchema[]=[
  
  {
    field: 'entryMethod',
    label: '入厂方式',
    component: 'Select',
    required: true,
    defaultValue: 0,
    componentProps: {
      options: [
        { label: '刷脸+二维码', value: 0 },
        { label: '仅刷脸', value: 1 },
        { label: '仅二维码', value: 2 },
      ],
      placeholder: '请选择',
    },
    colProps: { span: 24 },
  },
  {
    field: 'tip',
    label: '',
    component: 'Select',
    slot:'tip',
    colProps: { span: 24 },
  },
]


// 准入厂区选项（将从接口动态获取）
const enterFactoryOptions: any[] = [];
// 表单配置（合并后的单一表单）
export const addFormSchemas: FormSchema[] = [
  // ==================== 对接人信息 ====================
  {
    field: 'divider1',
    component: 'Divider',
    label: '对接人信息',
    componentProps: {
      orientation: 'left',
      style: {
        color: '#1890ff',
        fontWeight: 500,
        fontSize: '16px',
      },
    },
    colProps: { span: 24 },
  },
  {
    field: 'buttJointCode',
    label: '员工号',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      disabled: true,
    },
    colProps: { span: 24 },
  },
  {
    field: 'buttJointName',
    label: '姓名',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      disabled: true,
    },
    colProps: { span: 24 },
  },
  {
    field: 'buttJointPhone',
    label: '对接人手机号',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      disabled: true,
    },
    colProps: { span: 24 },
    show: false,
  },
  // ==================== 备案人信息 ====================
  {
    field: 'divider2',
    component: 'Divider',
    label: '备案人信息',
    componentProps: {
      orientation: 'left',
      style: {
        color: '#1890ff',
        fontWeight: 500,
        fontSize: '16px',
      },
    },
    colProps: { span: 24 },
  },
  {
    field: 'name',
    label: '姓名',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      maxlength: 10,
      showCount: true,
    },
    colProps: { span: 24 },
    rules: [
      { required: true, message: '请输入姓名', trigger: 'blur' },
      { pattern: /^[\u4e00-\u9fa5]+$/, message: '姓名只能输入中文', trigger: 'blur' },
    ],
  },
  {
    field: 'companyName',
    label: '公司名称',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      // disabled: true,
    },
    colProps: { span: 24 },
  },
  {
    field: 'companyCode',
    label: '公司代码',
    component: 'Input',
    required: false,
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 24 },
    show: false,
  },
  {
    field: 'cardNo',
    label: '身份证',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      maxlength: 18,
    },
    colProps: { span: 24 },
  },
  {
    field: 'phone',
    label: '手机号',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      maxlength: 11,
    },
    colProps: { span: 24 },
  },
  
  {
    field: 'recordTypeCode',
    label: '类型',
    component: 'Select',
    componentProps: {
      placeholder: '请选择对应类型',
    },
    colProps: { span: 24 },
    rules: [{ required: true, message: '请选择类型', trigger: 'change' }],
  },

  
  {
    field: 'enterFactoryCode',
    label: '准入厂区',
    component: 'JSelectMultiple',
    componentProps: {
      placeholder: '请选择',
      options: enterFactoryOptions,
    },
    colProps: { span: 24 },
    rules: [{ required: true, message: '请选择厂区', trigger: 'change' }],
  },
  {
    field: 'enterTimeRange',
    label: '准入时间',
    component: 'RangePicker',
    required: true,
    slot:"enterTimeRange",
    componentProps: {
      showTime: { format: 'HH:mm',hideDisabledOptions: true },
      format: 'YYYY-MM-DD HH:mm',
      valueFormat: 'YYYY-MM-DD HH:mm:ss', // 设置值的格式，返回格式化后的字符串数组
      valueType: 'array', // 保持数组格式，不转换为逗号分隔的字符串
      placeholder: ['开始时间', '结束时间'],
      style: { width: '100%' },
      // ...createEnterTimeRangeConfig(7), // 默认7天，会被 ApplicationModal 动态更新
    },
    colProps: { span: 24 },
    rules: [{ required: true, message: '请选择准入时间', trigger: 'change' }],
    // defaultValue: ['2025-10-24 00:00:00', '2025-10-31 23:59:59'],
  },
  {
    field: 'nightFlag',
    label: '是否夜间作业',
    component: 'Select',
    required: true,
    defaultValue: 0,
    componentProps: {
      options: [
        { label: '否', value: 0 },
        { label: '是', value: 1 },
      ],
      placeholder: '请选择',
    },
    colProps: { span: 24 },
  },
  {
    field: 'remark',
    label: '备注',
    component: 'InputTextArea',
    componentProps: {
      placeholder: '请输入',
      rows: 4,
      maxlength: 500,
      showCount: true,
    },
    colProps: { span: 24 },
    // defaultValue: '备注',
  },
];



