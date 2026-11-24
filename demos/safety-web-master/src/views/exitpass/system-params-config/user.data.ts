import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    "title": "参数编码",
    "dataIndex": "paramCode",
    "width": 120
  },
  {
    "title": "参数名",
    "dataIndex": "paramName",
    "width": 120
  },
  {
    "title": "值",
    "dataIndex": "paramValue",
    "width": 120
  },
  {
    "title": "描述",
    "dataIndex": "paramDesc",
    "width": 120
  },
  {
    "title": "备注",
    "dataIndex": "remark",
    "width": 120
  }
]


export const scmSchema: FormSchema[] =
  [
    {
      "label": "参数编码",
      "field": "paramCode",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入参数编码"
      }
    },
    {
      "label": "参数名",
      "field": "paramName",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入参数名"
      }
    },
    {
      "label": "值",
      "field": "paramValue",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入参数值"
      }
    },
    {
      "label": "描述",
      "field": "paramDesc",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入描述"
      }
    },
    {
      "label": "备注",
      "field": "remark",
      "component": "Input",
      "colProps": { "span": 24 },
      "componentProps": {
        "placeholder": "请输入备注"
      }
    },
  ]
