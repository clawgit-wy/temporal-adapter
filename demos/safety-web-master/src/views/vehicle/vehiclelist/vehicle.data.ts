import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';
import { h } from 'vue';
import { fieldTree, ftList } from './api';
/* 备案厂区列表字段 */
export interface factoryType {
  itemName: string;
  itemCode: string;
}
/* 人员信息数据 */
export interface perLists {
  fdPerCard: string;
  fdPerImg: string;
  fdPerName: string;
  fdPerPhone: string;
  fdPerType: string;
  fdPerTypeCode: string;
  filingId: string;
}
/* 接口人信息字段 */
export interface dockList {
  fdDockDepartment: string;
  fdDockName: string;
  fdDockNo: string;
  fdDockPhone: string;
}

export const columns: BasicColumn[] = [
  {
    title: '状态',
    dataIndex: 'fdState',
    width: 60,
  },
  {
    title: '备案类型',
    dataIndex: 'fdFilingType',
    width: 80,
  },
  {
    title: '入厂类型',
    dataIndex: 'fdEnterType',
    width: 80,
  },
  {
    title: '车牌号',
    dataIndex: 'fdCarNo',
    width: 80,
  },
  {
    title: '备案厂区',
    dataIndex: 'fdFilingFactory',
    width: 120,
  },
  {
    title: '备案时间-始',
    dataIndex: 'fdFilingBegin',
    width: 100,
  },
  {
    title: '备案时间-终',
    dataIndex: 'fdFilingEnd',
    width: 100,
  },
  {
    title: '审批后备案厂区',
    dataIndex: 'approveFilingFactory',
    ellipsis: false,
    width: 200,
  },
  {
    title: '审批后备案时间',
    dataIndex: 'approveFilingEnd',
    width: 200,
  },

  {
    title: '人员类型',
    dataIndex: 'perList',
    width: 80,
    slots: { customRender: 'fdPer' },
  },
  {
    title: '姓名',
    dataIndex: 'fdPerName',
    width: 70,
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
    width: 100,
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
    width: 80,
  },
  {
    title: '随车清单',
    dataIndex: 'fdDetailedImg',
    width: 80,
    customRender: ({ text }) => {
      if (text) {
        return h('a', { href: text, target: '_blank', style: { 'text-decoration': 'underline' } }, '点击查看');
      }
      return text;
    },
  },
  {
    title: '供应商名称',
    dataIndex: 'fdSupplierName',
    width: 150,
  },
  // {
  //   title: '供应商代码',
  //   dataIndex: 'fdSupplierCode',
  //   width: 150,
  // },
  {
    title: '运输货物名称',
    dataIndex: 'fdGoodsName',
    width: 110,
  },
  {
    title: '货物装载量（吨）',
    dataIndex: 'fdGoodsWeight',
    width: 130,
  },
  {
    title: '行驶证正面照片',
    dataIndex: 'fdCarImg',
    width: 120,
    customRender: ({ text }) => {
      if (text) {
        return h('a', { href: text, target: '_blank', style: { 'text-decoration': 'underline' } }, '点击查看');
      }
      return text;
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    ellipsis: false,
    width: 95,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '审批时间',
    dataIndex: 'approveTime',
    ellipsis: false,
    width: 95,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '接口人员工号',
    dataIndex: 'fdDockNo',
    width: 100,
  },
  {
    title: '接口人姓名',
    dataIndex: 'fdDockName',
    width: 100,
  },
  {
    title: '接口人部门',
    dataIndex: 'fdDockDepartment',
    ellipsis: false,
    width: 110,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 150,
  },
  {
    title: '操作',
    dataIndex: 'action',
    width: 100,
    slots: { customRender: 'action' },
    fixed: 'right',
  },
];

//表单搜索字段
export const searchFormSchema: FormSchema[] = [
  {
    label: '接口人姓名', //显示label
    field: 'fdDockName', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
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
    defaultValue: [dayjs().subtract(1, 'M'),dayjs()],
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '状态',
    field: 'fdState',
    component: 'ApiSelect',
    componentProps: {
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p4',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
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
];

// export const addFormSchema: FormSchema[] = [{}];
export const chColumns = [
  {
    dataIndex: 'fdPerType',
  },
];
