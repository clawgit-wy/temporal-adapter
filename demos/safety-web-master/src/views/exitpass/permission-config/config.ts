import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { getFieldChildTree, getGroupTree } from './field.api';

export const columns: BasicColumn[] = [
  {
    "title": "审核人员工号",
    "dataIndex": "empNo",
    "width": 120
  },
  {
    "title": "审核人姓名",
    "dataIndex": "empName",
    "width": 120
  },
  {
    "title": "审核人eip账号",
    "dataIndex": "empEip",
    "width": 120
  },
  {
    "title": "审核部门",
    "dataIndex": "empDept",
    "width": 120
  },
  {
    "title": "厂区",
    "dataIndex": "powerFactory",
    "width": 120
  },
  {
    "title": "物资出场权限",
    "dataIndex": "powerDesc",
    "width": 120
  },
  {
    "title": "出门类型",
    "dataIndex": "outType",
    "width": 120
  },
  {
    "title": "物资类别",
    "dataIndex": "goodsType",
    "width": 120
  }
]

export const searchFormSchema: FormSchema[] =
  [
    {
      "label": "审核人",
      "field": "empStr",
      "component": "Input",
      "colProps": { "span": 8 },
      "componentProps": {
        "placeholder": "姓名/员工号/eip号"
      }
    },
    {
      label: '审核部门',
      field: 'empDept',
      "component": "Input",
      "componentProps": {
        "placeholder": "请输入审核部门"
      }
    },
    {
      "label": "出门类型",
      "field": "outType",
      component: 'ApiSelect',
      colProps: { span: 8 },
      getLabel:true,
      componentProps: {
        api: getFieldChildTree,
        params: {
          fieldType: 'goodsOut_dict_0_type',
          topFlag: false,
        },
        valueField: 'fieldName',
        labelField: 'fieldName'
      }
    }
  ]


export const scmSchema: FormSchema[] =
  [
    {
      "label": "审核人员工号",
      "field": "empNo",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入审核人员工号"
      },
      slot: 'empNo',
    },
    {
      "label": "审核人姓名",
      "field": "empName",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "disabled": true,
        "placeholder": "请输入审核人姓名"
      }
    },
    {
      "label": "审核人eip账号",
      "field": "empEip",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入审核人eip账号"
      }
    },
    {
      "label": "审核部门",
      "field": "empDept",
      "colProps": { "span": 24 },
      "component": "Input",
      "componentProps": {
        "placeholder": "请输入审核部门"
      },
      slot: 'empDept',
    },
    {
      "label": "厂区",
      "field": "powerFactoryId",
      "component": "FactoryId",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入厂区"
      }
    },
    {
      "label": "物资出场权限",
      "field": "powerDesc",
      "component": "InputTextArea",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请填写物资出场权限",
      }
    },
    {
      "label": "出门类型",
      "field": "outType",
      "component": "ApiSelect",
      "colProps": { "span": 24 },
      componentProps: {
        api: getFieldChildTree,
        params: {
          fieldType: 'goodsOut_dict_0_type',
          topFlag: false,
        },
        valueField: 'fieldName',
        labelField: 'fieldName'
      }
    },
    {
      "label": "物资类别",
      "field": "goodsType",
      "component": "ApiSelect",
      "colProps": { "span": 24 },
      componentProps: {
        api: getFieldChildTree,
        params: {
          fieldType: 'goodsOut_goods_type',
          topFlag: false,
        },
        valueField: 'fieldName',
        labelField: 'fieldName'
      }
    }
  ]