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
    title: '厂区名称',
    dataIndex: 'phoneNo',
  },
  {
    title: '厂区编号',
    dataIndex: 'userName',
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
    label: '厂区名称', //显示label
    field: 'jk_name', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '厂区编号',
    field: 'emp',
    component: 'Input',
  },
 
];

// export const addFormSchema: FormSchema[] = [{}];
