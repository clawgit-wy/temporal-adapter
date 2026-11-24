import { BasicColumn, FormSchema } from '/@/components/Table';
import { fieldTree } from './list.api'
import {ref} from 'vue'

export const columns: BasicColumn[] = [
  {
    title: '项目',
    dataIndex: 'teamName',
    width: 150,
  },
  {
    title: '巡检标准类目',
    dataIndex: 'ruleName',
    width: 100,
  },
  {
    title: '巡检标准',
    dataIndex: 'ruleInfo',
    width: 100,
  },
  {
    title: '设备正常是否上传照片',
    dataIndex: 'normalImgRequire',
    slots: { customRender: 'normalImgRequire' },
    width: 130,
  },
  {
    title: '设备异常是否上传照片',
    dataIndex: 'exceptionImgRequire',
    slots: { customRender: 'exceptionImgRequire' },
    width: 130,
  },
  {
    title: '当场整改是否上传照片',
    dataIndex: 'reformImgRequire',
    slots: { customRender: 'reformImgRequire' },
    width: 130,
  },
  {
    title: '整改验证是否上传照片',
    dataIndex: 'validateImgRequire',
    slots: { customRender: 'validateImgRequire' },
    width: 130,
  },
  {
    title: '排序',
    dataIndex: 'ruleSort',
    width: 100,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 100,
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
    label: '项目名称',
    field: 'teamName',
    component: 'Input',
    show: false,
  },
  {
    label: '项目',
    field: 'teamId',
    labelField: 'teamName',
    component: 'Select',
    required: true,
  },
  {
    label: '巡检标准类目',
    field: 'ruleName',
    component: 'Input',
    required: true,
  },
  {
    label: '巡检标准描述',
    field: 'ruleInfo',
    component: 'InputTextArea',
    componentProps: {
      maxlength: 1000,
      rows: 5
    },
  },
  {
    label: '设备正常是否上传照片',
    field: 'normalImgRequire',
    component: 'Checkbox',
    renderComponentContent: '必填',
  },
  {
    label: '设备异常是否上传照片',
    field: 'exceptionImgRequire',
    component: 'Checkbox',
    renderComponentContent: '必填',
  },
  {
    label: '当场整改是否上传照片',
    field: 'reformImgRequire',
    component: 'Checkbox',
    renderComponentContent: '必填',
  },
  {
    label: '整改验证是否上传照片',
    field: 'validateImgRequire',
    component: 'Checkbox',
    renderComponentContent: '必填',
  },
  {
    label: '排序',
    field: 'ruleSort',
    component: 'InputNumber',
    componentProps: {
      min: 1
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: {
      maxlength: 500,
      rows: 3
    },
  },
];
export const searchFormSchema: FormSchema[] =[
  
  {
    label: '项目',
    field: 'teamName',
    component: 'Select',
    componentProps: {
      showSearch: true,
    },
    // componentProps: {
    //   api: (params) => fieldTree(params),
    //   labelField: 'fieldName',
    //   valueField: 'id',
    //   fieldNames: {
    //     label: 'fieldName',
    //     value: 'id',
    //     options: 'regionList'
    //   },
    //   params: {fieldType: 'spotTeam', topFlag: true},
    // },
  },
  {
    label: '巡检标准类目',
    field: 'ruleName',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'remark',
    component: 'Input',
  },

] 