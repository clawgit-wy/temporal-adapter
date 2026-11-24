import { BasicColumn, FormSchema } from '/@/components/Table';
import { ftList } from './api';
import { h } from 'vue';
import { usePermission } from '/@/hooks/web/usePermission';
const { hasPermission } = usePermission();
export const columns: BasicColumn[] = [
  {
    title: '车牌号',
    dataIndex: 'fdCarNo',
    width: 100,
  },
  {
    title: '车辆归属',
    dataIndex: 'fdCarBelong',
    width: 100,
  },
  {
    title: '车主姓名/运输公司名称',
    dataIndex: 'fdDriverName',
    width: 180,
  },
  {
    title: '排放标准',
    dataIndex: 'fdProtectGrade',
    width: 100,
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
    title: 'VIN号',
    dataIndex: 'fdVin',
    width: 100,
  },
  {
    title: '发动机号码',
    dataIndex: 'fdEngine',
    width: 100,
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
    title: '行驶证注册日期',
    dataIndex: 'fdCarDate',
    width: 100,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 100,
  },

  {
    title: '车辆是否在厂',
    dataIndex: 'fdInFactory',
    width: 100,
  },

  {
    title: '操作',
    key: 'action',
    width: 100,
    slots: { customRender: 'action' },
    fixed: 'right',
    ifShow: () => {
      return hasPermission('VehicleManagement:environ:delete');
    },
  },
];

//表单搜索字段
export const searchFormSchema: FormSchema[] = [
  {
    label: '车牌号', //显示label
    field: 'fdCarNo', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '排放标准',
    field: 'fdProtectGrade',
    component: 'ApiSelect', //渲染的组件
    slot: '',
    componentProps: {
      showArrow: true,
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p1',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemName',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },
  {
    label: 'VIN号',
    field: 'fdVin',
    component: 'Input',
  },
  {
    label: '发动机号',
    field: 'fdEngine',
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
