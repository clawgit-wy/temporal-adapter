import { BasicColumn, FormSchema } from '/@/components/Table';
import { getEmpPost, getGroupTree, getDropDownBox } from './api';
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';
// import { filterDictTextByCache } from '/@/utils/dict/JDictSelectUtil';
const userTypeList =   [
        {
          label: '正式员工',
          value: 'ZS',
          key: '1',
        },
        {
          label: '绿都员工',
          value: 'LD',
          key: '2',
        },
        {
          label: '外协员工',
          value: 'WX',
          key: '3',
        },
      ]
// 职种枚举：这7种显示原值，其余均显示为"员工"
const jobTypeMapping = ['高层管理', '科级管理', '部级管理', '处级管理', '室级管理', '班组级管理', '模块'];

export const columns: BasicColumn[] = [
  {
    title: '员工号',
    dataIndex: 'empNo',
    width: 100,
  },
  {
    title: '姓名',
    dataIndex: 'empName',
    width: 100,
  },
  {
    title: '手机号',
    dataIndex: 'phoneNo',
    width: 120,
  },
  {
    title: '照片',
    dataIndex: 'photo',
    width: 80,
    customRender: ({ text }) => {
      if (!text) {
        return '无';
      }
      return render.renderImage2({ text });
    },
  },
  {
    title: '人员类型',
    dataIndex: 'userType',
    customRender: ({ text }) => {
      return render.renderDictNative(text, userTypeList, false);
    },
    width: 100,
  },
  {
    title: '职类',
    dataIndex: 'empType',
    width: 100,
  },
  {
    title: '职种',
    dataIndex: 'jobTypeName',
    width: 120,
    customRender: ({ text }) => {
      if (!text) {
        return text;
      }
      return jobTypeMapping.includes(text) ? text : '员工';
    },
  },
  {
    title: '岗位',
    dataIndex: 'positionName',
    width: 150,
  },
  {
    title: '类别',
    dataIndex: 'positionType',
    width: 160,
  },
  {
    title: '电子邮箱',
    dataIndex: 'email',
    width: 160,
  },
  {
    title: '备注',
    edit: true,
    editComponent: 'Input',
    dataIndex: 'remark',
    width: 150,
  },
  {
    title: '组织架构',
    dataIndex: 'orgNamePath',
    align: 'left',
    width: 900,
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
    label: '手机号',
    field: 'phoneNo',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },
  {
    label: '人员类型',
    field: 'userType',
    component: 'Select',
    componentProps: {
      options: userTypeList
    },
  },
  {
    label: '岗位',
    field: 'positionName',
    component: 'Input',
  },
  {
    label: '类别',
    field: 'positionType',
    component: 'Select',
    componentProps: {
      options: [
        { label: '外包岗位', value: '外包岗位'},
        { label: '直接岗位', value: '直接岗位'},
        { label: '间接岗位', value: '间接岗位' },
        { label: '空', value: '空' },
      ],
    },
  },
  {
    label: '组织架构',
    field: 'orgCode',
    component: 'DeptTreeSelect',
    componentProps: {
      api: () => getGroupTree,
      invertIdsField: 'orgCodePath'
    },
  },
  {
    label: '职类',
    field: 'empTypes',
    component: 'ApiSelect',
    componentProps: {
      mode: 'multiple',
      showSearch: false,
      api: getDropDownBox,
      resultField: 'empTypeList',
      labelField: 'itemName',
      valueField: 'itemCode',
      immediate: true,
      showArrow: true,
    },
  },
  {
    label: '职种',
    field: 'jobTypeNames',
    component: 'ApiSelect',
    componentProps: {
      mode: 'multiple',
      showSearch: false,
      api: getDropDownBox,
      resultField: 'jobTypeList',
      labelField: 'itemName',
      valueField: 'itemCode',
      immediate: true,
      showArrow: true,
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
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: {
      maxlength: 100,
    },
  },
];
