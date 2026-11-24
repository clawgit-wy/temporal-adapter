import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
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
    title: '授权类型',
    dataIndex: 'phoneNo',
  },
  {
    title: '厂区',
    dataIndex: 'userName',
  },
  {
    title: '备案类型',
    dataIndex: 'phoneNo',
  },
  {
    title: '通道号',
    dataIndex: 'phoneNo',
  },
  {
    title: '通道名称',
    dataIndex: 'phoneNo',
  },
  {
    title: '更新时间',
    dataIndex: 'phoneNo',
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    fixed: 'right',
  },
];

//表单搜索字段
export const searchFormSchema: FormSchema[] = [
  {
    label: '授权类型', //显示label
    field: 'jk_name', //查询字段
    component: 'Select', //渲染的组件
    slot: 'souQuanType',
  },
  {
    label: '备案类型', //显示label
    field: 'jk_name', //查询字段
    component: 'Select', //渲染的组件
    slot: 'banType',
  },
  {
    label: '人员类型', //显示label
    field: 'jk_name', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '通道号', //显示label
    field: 'jk_name', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
];
export const formSchemas: FormSchema[] = [
  {
    label: '授权类型', //显示label
    field: 'jk_name', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '厂区',
    field: 'emp',
    component: 'Input',
  },
  {
    label: '备案类型',
    field: 'emp',
    component: 'Input',
  },
  {
    label: '通道号',
    field: 'emp',
    component: 'Input',
  },
  {
    label: '通道名称',
    field: 'emp',
    component: 'Input',
  },
];

// export const addFormSchema: FormSchema[] = [{}];
