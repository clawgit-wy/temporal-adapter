import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import { factoryList, getRoles } from './users.api';
import dayjs from 'dayjs';

export const columns: BasicColumn[] = [
  {
    title: '姓名',
    dataIndex: 'empName',
    width: 80,
  },
  {
    title: 'eip用户名',
    dataIndex: 'userName',
    width: 80,
  },
  {
    title: '员工号',
    dataIndex: 'empNo',
    width: 100,
  },
  {
    title: '角色',
    dataIndex: 'roleName',
    width: 150,
  },
  {
    title: '访客发卡厂区',
    dataIndex: 'factoryName',
    width: 250,
  },
  {
    title: '授权厂区',
    dataIndex: 'powerFactoryName',
    width: 250,
  },
  {
    title: '修改人',
    dataIndex: 'modifyEmp',
    width: 150,
  },
  {
    title: '修改时间',
    dataIndex: 'updateTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '员工号',
    field: 'empNo',
    component: 'Input',
  },
  {
    label: '姓名',
    field: 'empName',
    component: 'Input',
  },
  {
    label: '角色',
    field: 'roleId',
    component: 'ApiSelect',
    componentProps: {
      api: ()=> getRoles({}),
      labelField: 'name',
      valueField: 'id',
    }
  },
  {
    label: '访客发卡厂区',
    field: 'factoryId',
    component: 'Select',
    slot: 'factoryName',
  },
  {
    label: '授权厂区',
    field: 'powerFactoryId',
    component: 'Select',
    slot: 'powerFactoryName',
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
    label: '姓名',
    field: 'empName',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 10,
    },
  },
  {
    label: 'eip用户名',
    field: 'userName',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 50,
    },
  },
  {
    label: '员工号',
    field: 'empNo',
    component: 'Input',
    required: true,
    componentProps: {
      maxlength: 16,
    },
  },
  {
    label: '角色',
    field: 'roleId',
    component: 'Select',
    required: true,
    slot: 'role',
  },
  {
    label: '访客发卡厂区',
    field: 'factoryId',
    component: 'Select',
    slot: 'factoryName',
  },
  {
    label: '授权厂区',
    field: 'powerFactoryId',
    component: 'Select',
    slot: 'powerFactoryName',
  },
];
