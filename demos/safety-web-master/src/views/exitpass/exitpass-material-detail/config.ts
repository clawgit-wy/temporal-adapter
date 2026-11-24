import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import {fieldTree} from './field.api'

export const columns: BasicColumn[] = [
  {
    "title": "流程编号",
    "dataIndex": "flowNo",
    "width": 120,
    slots: { customRender: 'flowNo' }
  },
  {
    "title": "主题",
    "dataIndex": "subject",
    "width": 120
  },
  {
    "title": "出门类型",
    "dataIndex": "outTypeName",
    "width": 120
  },
  {
    "title": "物资编号",
    "dataIndex": "goodsCode",
    "width": 120
  },
  {
    "title": "物资名称",
    "dataIndex": "goodsName",
    "width": 120
  },
  {
    "title": "单位",
    "dataIndex": "goodsUnit",
    "width": 120
  },
  {
    "title": "数量",
    "dataIndex": "goodsNum",
    "width": 120
  },
  {
    "title": "车工号",
    "dataIndex": "vehicleNo",
    "width": 120
  },
  {
    "title": "VIN号",
    "dataIndex": "vinNo",
    "width": 120
  },
  {
    "title": "提交时间",
    "dataIndex": "createTimeStr",
    "width": 120
  },
  {
    "title": "出门人",
    "dataIndex": "gooutEmpName",
    "width": 120
  },
  {
    "title": "车牌号",
    "dataIndex": "processVehicelNo",
    "width": 120
  },
  {
    "title": "出厂厂区",
    "dataIndex": "outFactoryName",
    "width": 120
  },
  {
    "title": "审核人",
    "dataIndex": "examineEmpName",
    "width": 120
  },
  {
    "title": "状态",
    "dataIndex": "status",
    "width": 120
  },
  {
    "title": "备注",
    "dataIndex": "remark",
    "width": 120
  },

]


export const searchFormSchema: FormSchema[] = 
[

  {
    "label": "物资编号",
    "field": "goodsCode",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入物资编号"
    }
  },
  {
    "label": "出门人",
    "field": "gooutEmpName",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "姓名/联系电话"
    }
  },
  {
    "label": "审核人",
    "field": "examineEmpName",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入审核人"
    }
  },
  {
    "label": "状态",
    "field": "status",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入状态",
    }
  },
  {
    "label": "出门类型",
    "field": "outTypeName",
    "component": "ApiSelect",
    "colProps": { "span": 8 },
    "componentProps": {
      api:fieldTree,
      params: {
          fieldType: 'goodsOut_dict_0_type',
          topFlag: false,
      },
      valueField:"fieldName",
      labelField:"fieldName"
    }
  },
  {
    "label": "是否异常",
    "field": "isHistoryExce",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
    }
  },
  {
    "label": "出厂厂区",
    "field": "outFactory",
    "component": "Factory",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入备注"
    }
  },
  {
    "label": "提交时间",
    "field": "createTime",
    "component": "RangePicker",
    "colProps": { "span": 8 },
    "componentProps": {
    }
  },
  {
    "label": "车牌号/车工号",
    "field": "vehicleNo",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入车牌号"
    }
  },
  {
    "label": "VIN号",
    "field": "vinNo",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入VIN号"
    }
  }
]


export const scmSchema: FormSchema[] = 
[
  {
    "label": "流程编号",
    "field": "processNumber",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入流程编号"
    }
  },
  {
    "label": "主题",
    "field": "subject",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入主题"
    }
  },
  {
    "label": "物资编号",
    "field": "materialNumber",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入物资编号"
    }
  },
  {
    "label": "物资名称",
    "field": "materialName",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入物资名称"
    }
  },
  {
    "label": "单位",
    "field": "unit",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入单位"
    }
  },
  {
    "label": "数量",
    "field": "quantity",
    "component": "InputNumber",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入数量",
      "min": 0
    }
  },
  {
    "label": "车工号",
    "field": "vehicleNumber",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入车工号"
    }
  },
  {
    "label": "VIN号",
    "field": "vinNumber",
    "component": "Input",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入VIN号"
    }
  },
  {
    "label": "备注",
    "field": "remarks",
    "component": "Textarea",
    "colProps": { "span": 8 },
    "componentProps": {
      "placeholder": "请输入备注"
    }
  }
]