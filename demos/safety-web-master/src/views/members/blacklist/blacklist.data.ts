import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import { identityInfoQuerySelect } from './blacklist.api';
import dayjs from 'dayjs';
// import { filterDictTextByCache } from '/@/utils/dict/JDictSelectUtil';
const stateList = [
  {
    label: '禁止入厂',
    value: 1,
    key: 1,
  },
  {
    label: '特批进厂',
    value: 2,
    key: 2,
  },
];
export const columns: BasicColumn[] = [
  {
    title: '公司',
    dataIndex: 'companyName',
    width: 120,
  },
  {
    title: '姓名',
    dataIndex: 'empName',
    width: 100,
  },
  {
    title: '卡号',
    dataIndex: 'cardNo',
    width: 150,
  },
  {
    title: '手机号',
    dataIndex: 'phoneNo',
    width: 150,
  },
  {
    title: '照片类型',
    dataIndex: 'photoType',
    width: 150,
  },
  {
    title: '照片',
    dataIndex: 'photo',
    width: 80,
    customRender: ({ text }) => {
      if (!text) {
        return text ||'/';
      } else {
        return render.renderImage2({ text });
      }
    },
  },
  {
    title: '身份证号',
    dataIndex: 'idCardNo',
    width: 160,
  },
  {
    title: '人员类型',
    dataIndex: 'empType',
    width: 100,
  },
  {
    title: '车辆',
    dataIndex: 'carNo',
    width: 100,
  },
  {
    title: '身份信息',
    dataIndex: 'positionType',
    width: 100,
    customRender: ({ text }) => {
      return text?text:"/";
    },
  },
  {
    title: '状态',
    dataIndex: 'state',
    width: 100,
    customRender: ({ text }) => {
      return render.renderDictNative(text, stateList, false);
    },
  },
  {
    title: '特批授权截止日期',
    dataIndex: 'authorizationDate',
  },
  {
    title: '操作员',
    dataIndex: 'operator',
    width: 100,
  },
  {
    title: '数据来源',
    dataIndex: 'source',
    width: 100,
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 150,
  },

  // {
  //   title: '模板类型',
  //   dataIndex: 'templateType',
  //   width: 100,
  //   customRender: ({ text }) => filterDictTextByCache('msgType', text),
  // },
  // {
  //   title: '是否应用',
  //   dataIndex: 'useStatus',
  //   width: 90,
  //   customRender: function ({ text }) {
  //     if (text == '1') {
  //       return '是';
  //     } else {
  //       return '否';
  //     }
  //   },
  // },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '公司',
    field: 'companyName',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
  },
  {
    label: '卡号',
    field: 'cardNo',
    component: 'Input',
  },
  {
    label: '手机号',
    field: 'phoneNo',
    component: 'Input',
  },
  {
    label: '身份证号',
    field: 'idCardNo',
    component: 'Input',
  },
  
  {
      label: '身份信息', //显示label
      field: 'positionType', //查询字段
      component: 'ApiSelect', //渲染的组件l
      componentProps: {
        mode: 'multiple',
        showSearch: false, // 开启搜索
        api: identityInfoQuerySelect,
        resultField: 'data',
        immediate: true, // 组件加载后是否立即请求接口
        // labelField: 'itemName',
        // valueField: 'itemCode',
        // optionFilterProp: 'label', // 搜索按label字段搜索
        showArrow: true,
      },
  },
  {
    label: '车辆',
    field: 'carNo',
    component: 'Input',
  },
  {
    label: '操作员',
    field: 'operator',
    component: 'Input',
  },
  {
    label: '人员类型',
    field: 'empType',
    component: 'Select',
    componentProps: {
      options: [
        {
          label: '外部人员',
          value: '外部人员',
          key: '1',
        },
        {
          label: '离职人员',
          value: '离职人员',
          key: '2',
        },
      ],
    },
  },
  {
    label: '状态',
    field: 'state',
    component: 'Select',
    componentProps: {
      options: stateList,
    },
  },
  {
    label: '数据来源',
    field: 'source',
    component: 'Select',
    componentProps: {
      options: [
        {
          label: '本地添加',
          value: '本地添加',
          key: '1',
        },
        {
          label: '中台导入',
          value: '中台导入',
          key: '2',
        },
        {
          label: 'EHR导入',
          value: 'EHR导入',
          key: '3',
        },
      ],
    },
  },
  {
    label: '更新时间',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
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
    label: '公司',
    field: 'companyName',
    component: 'Input',
    required: true,
    rules: [
      {
        required: true,
        message: '请输入公司',
      },
    ],
    componentProps: {
      maxlength: 40,
    },
  },
  {
    label: '卡号',
    field: 'cardNo',
    component: 'Input',
    componentProps: {
      maxlength: 11,
    },
    dynamicDisabled: (params) => !!params.values.id,
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
    required: true,
    rules: [
      {
        required: true,
        message: '请输入姓名',
      },
    ],
    componentProps: {
      maxlength: 16,
    },
    dynamicDisabled: (params) => !!params.values.id,
  },
  {
    label: '手机号',
    field: 'phoneNo',
    component: 'Input',
    required: true,
    rules: [
      {
        required: true,
        message: '请输入手机号',
      },
      { pattern: /^1[3456789]\d{9}$/, message: '手机号码格式有误', trigger: 'blur' },
    ],
    componentProps: {
      maxlength: 11,
    },
  },
  {
    label: '身份证号',
    field: 'idCardNo',
    component: 'Input',
    required: true,
    rules: [
      {
        required: true,
        message: '请输入身份证号',
      },
    ],
    componentProps: {
      maxlength: 18,
    },
    dynamicDisabled: (params) => !!params.values.id,
  },
  // {
  //   label: '卡号',
  //   field: 'cardNo',
  //   component: 'Input',
  //   required: true,
  // },
  {
    label: '人员类型',
    field: 'empType',
    component: 'Select',
    required: true,
    componentProps: {
      options: [
        {
          label: '外部人员',
          value: '外部人员',
          key: '1',
        },
        {
          label: '离职人员',
          value: '离职人员',
          key: '2',
        },
      ],
    },
  },
  {
    label: '身份信息',
    field: 'positionType',
    component: 'ApiSelect', //渲染的组件
      componentProps: {
        // mode: 'multiple',
        showSearch: false, // 开启搜索
        api: identityInfoQuerySelect,
        resultField: 'data',
        immediate: true, // 组件加载后是否立即请求接口
        // labelField: 'itemName',
        // valueField: 'itemCode',
        // optionFilterProp: 'label', // 搜索按label字段搜索
        showArrow: true,
      },
  },
  {
    label: '车辆',
    field: 'carNo',
    component: 'Input',
    componentProps: {
      maxlength: 11,
    },
  },
  {
    label: '状态',
    field: 'state',
    component: 'Select',
    componentProps: {
      options: [
        {
          label: '禁止入厂',
          value: 1,
          key: 1,
        },
        {
          label: '特批进厂',
          value: 2,
          key: 2,
        },
      ],
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
    componentProps: {
      maxlength: 100,
    },
  },
];
