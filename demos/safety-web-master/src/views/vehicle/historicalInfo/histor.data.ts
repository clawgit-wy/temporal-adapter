import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';
import { h } from 'vue';
// import { filterDictTextByCache } from '/@/utils/dict/JDictSelectUtil';
import { fieldTree, ftList } from './api';

export const columns: BasicColumn[] = [
  {
    title: '状态',
    dataIndex: 'fdState',
    width: 80,
  },
  {
    title: '备案类型',
    dataIndex: 'fdFilingType',
    width: 100,
  },

  {
    title: '车牌号',
    dataIndex: 'fdCarNo',
    width: 120,
  },
  {
    title: '入厂类型',
    dataIndex: 'fdEnterType',
    width: 100,
  },
  {
    title: '申请备案厂区',
    dataIndex: 'fdFilingFactory',
    width: 120,
  },
  {
    title: '备案时间-始',
    dataIndex: 'fdFilingBegin',
    width: 120,
  },
  {
    title: '备案时间-终',
    dataIndex: 'fdFilingEnd',
    width: 100,
  },
  {
    title: '审批后备案厂区',
    dataIndex: 'approveFilingFactory',
    width: 120,
  },
  {
    title: '审批后备案时间',
    dataIndex: 'approveFilingEnd',
    width: 120,
  },

  {
    title: '人员类型',
    dataIndex: 'perList',
    width: 100,
    slots: { customRender: 'fdPer' },
  },
  {
    title: '姓名',
    dataIndex: 'fdPerName',
    width: 100,
    slots: { customRender: 'fdName' },
  },
  {
    title: '身份证号',
    dataIndex: 'fdPerCard',
    width: 150,
    slots: { customRender: 'fdCard' },
  },
  {
    title: '手机号',
    dataIndex: 'fdPerPhone',
    width: 150,
    slots: { customRender: 'fdphone' },
  },
  {
    title: '车辆类型',
    dataIndex: 'fdFilingCarTypeName',
    width: 100,
  },
  // {
  //   title: '证件照',
  //   dataIndex: 'fdPerImg',
  //   width: 100,
  //   slots: { customRender: 'fdImg' },
  // },
  {
    title: '排放标准',
    dataIndex: 'fdProtectGrade',
    width: 150,
  },
  {
    title: '随车清单',
    dataIndex: 'fdDetailedImg',
    width: 150,
    customRender: ({ text }) => {
      if (text) {
        return h('a', { href: text, target: '_blank', style: { 'text-decoration': 'underline' } }, '点击查看');
      } else {
        return text;
      }
    },
  },
  {
    title: '供应商名称',
    dataIndex: 'fdSupplierName',
    width: 150,
  },
  {
    title: '供应商代码',
    dataIndex: 'fdSupplierCode',
    width: 150,
  },
  {
    title: '运输货物名称',
    dataIndex: 'fdGoodsName',
    width: 150,
  },
  {
    title: '货物装载量（吨）',
    dataIndex: 'fdGoodsWeight',
    width: 150,
  },
  {
    title: '行驶证正面照片',
    dataIndex: 'fdCarImg',
    width: 150,
    customRender: ({ text }) => {
      if (text) {
        return h('a', { href: text, target: '_blank', style: { 'text-decoration': 'underline' } }, '点击查看');
      } else {
        return text;
      }
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '接口人员工号',
    dataIndex: 'fdDockNo',
    width: 150,
  },
  {
    title: '接口人姓名',
    dataIndex: 'fdDockName',
    width: 150,
  },
  {
    title: '接口人部门',
    dataIndex: 'fdDockDepartment',
    width: 150,
  },
  {
    title: '备注',
    dataIndex: 'fdRemark',
    width: 150,
  },
  {
    title: '操作时间',
    dataIndex: 'fdOperationTime',
    width: 100,
  },
  {
    title: '操作人',
    dataIndex: 'fdOperationUserName',
    width: 100,
  },

  {
    title: '操作人员工号',
    dataIndex: 'fdOperationUserCode',
    width: 120,
  },
  {
    title: '操作方式',
    dataIndex: 'fdOperation',
    width: 100,
  },
];

//表单搜索字段
export const searchFormSchema: FormSchema[] = [
  {
    label: '接口人姓名', //显示label
    field: 'fdDockName', //查询字段
    component: 'Input', //渲染的组件
  },
  {
    label: '接口人工号',
    field: 'fdDockNo',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'fdPerName',
    component: 'Input',
  },
  {
    label: '身份证号',
    field: 'fdPerCard',
    component: 'Input',
  },
  {
    label: '手机号',
    field: 'fdPerPhone',
    component: 'Input',
  },
  {
    label: '处理人员工号',
    field: 'handler',
    component: 'Input',
  },
  {
    label: '车牌号',
    field: 'fdCarNo',
    component: 'Input',
  },
  {
    label: '备案厂区',
    field: 'fdFilingFactory',
    component: 'ApiSelect',
    componentProps: {
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p1',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },

  {
    label: '备案类型',
    field: 'fdFilingType',
    component: 'ApiSelect',
    componentProps: {
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p2',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },

  {
    label: '人员类型',
    field: 'fdPerType',
    component: 'ApiSelect',
    componentProps: {
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p3',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },
  {
    label: '车辆类型',
    field: 'fdFilingCarTypeName',
    component: 'ApiSelect',
    componentProps: {
      api: fieldTree,
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'fieldName',
      valueField: 'fieldName',
    }
  },
  {
    label: '创建时间',
    field: 'createTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '供应商名称',
    field: 'fdSupplierName',
    component: 'Input',
  },
  {
    label: '运输货物名称',
    field: 'fdGoodsName',
    component: 'Input',
  },
  {
    label: '车主姓名/运输公司名称',
    field: 'fdDriverName',
    component: 'Input',
    disabledLabelWidth: true,
    itemProps: {
      labelCol: { span: 9 },
      wrapperCol: { span: 15 },
    },
  },
  {
    label: '操作方式',
    field: 'fdOperation',
    component: 'ApiSelect',
    componentProps: {
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p5',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemName',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },
];

// export const addFormSchema: FormSchema[] = [{}];
