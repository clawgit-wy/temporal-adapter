import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { fieldChildTree} from './field.api';
import { facList } from '../../vehicle/vehiclelist/api';


export const columns: BasicColumn[] = [
  {
    "title": "组织架构",
    "dataIndex": "orgName",
    "width": 120
  },
  {
    "title": "岗位名称",
    "dataIndex": "postName",
    "width": 120
  },
  {
    "title": "备注",
    "dataIndex": "remark",
    "width": 120
  },
  {
    "title": "授权厂区",
    "dataIndex": "factoryNamePower",
    "width": 120
  },
  {
    "title": "优先级",
    "dataIndex": "priority",
    "width": 120
  },
]

export const searchFormSchema: FormSchema[] =
  [
    
    { label: '组织名称', field: 'orgName', component: 'Input' ,show:false},
    { label: '组织名称', field: 'orgCode', component: 'Input' ,slot:'deptName',componentProps:{
      placeholder: "请选择",
    }},
    
    {
      label: "岗位名称",
      field: "postName",
      component: 'Input',
      colProps: { span: 8 },
      getLabel:true,
      componentProps: {
        placeholder: "请输入门禁名称"
      }
    },

    {
      label: "授权厂区",
      field: "factoryCode",
      component: 'ApiSelect',
      colProps: { span: 8 },
      componentProps: {
        api: fieldChildTree,
        params: {
          fieldType: 'factory',
        },
        valueField: 'id',
        labelField: 'fieldName'
      }
    }
  ]


export const scmSchema: FormSchema[] =
  [
    {
      label: "组织架构",
      field: "orgName",
      component: "Input",
      colProps: { "span": 24 },
      slot:'depart',
      required: ({ values }) => !values.postName,
      componentProps: {
        placeholder: "请选择组织架构",
        disabled:true,
      },
    },
    {
      label: "组织架构",
      field: "orgCode",
      component: "Input",
      colProps: { "span": 24 },
      show:false,
      componentProps: {
        placeholder: "请选择组织架构",
        disabled:true,
      },
    },
    
    {
      label: "岗位名称",
      field: "postName",
      component: "Input",
      required: ({ values }) => !values.orgCode,
      colProps: { "span": 24 },
      componentProps: {
        placeholder: "请输入门禁名称(AN)"
      }
    },
    
    {
      label: '授权厂区',
      field: 'factoryPower',
      component: 'ApiSelect',
      required:true,
      slot: 'factoryName',
      // componentProps:{
      //   mode:'multiple',
      //   api: fieldChildTree,
      //   params: {
      //     fieldType: 'factory',
      //   },
      //   valueField: 'id',
      //   labelField: 'fieldName'
      // }
    },
    {
      label: "优先级",
      field: "priority",
      component: "Input",
      required:true,
      colProps: { "span": 24 },
      componentProps: {
        placeholder: "请输入"
      },
      
    },
    
    {
      label: '备注',
      field: 'remark',
      component: 'Input',
      required:true,
      componentProps: {
        placeholder: "请输入"
      },
    },
  ]