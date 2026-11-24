import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { fieldChildTree, selectGroupTree} from './field.api';
import { facList } from '../../vehicle/vehiclelist/api';
import { render } from '/@/utils/common/renderUtils';

export const userTypeList =   [
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

export const columns: BasicColumn[] = [
  {
    title: "员工号",
    dataIndex: "empNo",
    width: 120
  },
  {
    title: "姓名",
    dataIndex: "empName",
    width: 120
  },
  {
    title: "组织",
    dataIndex: "orgName",
    width: 120
  },
  {
    title: "人员类型",
    dataIndex: "userType",
    width: 120,
    customRender: ({ text }) => {
      return render.renderDictNative(text, userTypeList, false);
    },
  },
  {
    title: "员工车辆",
    dataIndex: "carInfos",
    width: 120
  },
  {
    title: "锁定车辆",
    dataIndex: "lockCars",
    width: 120
  },
  {
    title: "授权厂区",
    dataIndex: "factoryName",
    width: 120,
    customRender: ({ text }) => {
      return render.renderTip(text);
    },
  },
  {
    title: "授权门禁",
    dataIndex: "deviceName",
    width: 120,
    customRender: ({ text }) => {
      return render.renderTip(text);
    },
    // slots:{ customRender: 'deviceName' }
  },
  {
    title: "备注",
    dataIndex: "remark",
    width: 120
  },
  
]

export const searchFormSchema: FormSchema[] =
  [
    
    { label: '员工号', field: 'empNo', component: 'Input',componentProps:{
      placeholder: "请输入",
    } },
    { label: '姓名', field: 'empName', component: 'Input' ,componentProps:{
      placeholder: "请输入",
    }},
    
    {
      label: "人员类型",
      field: "userType",
      component: 'Select',
      colProps: { span: 8 },
      componentProps: {
        placeholder: "请选择",
        options: userTypeList
      }
    },
    {
      label: "车牌号",
      field: "carNo",
      component: 'Input',
      colProps: { span: 8 },
      componentProps: {
        placeholder: "请输入"
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
    },
    {
      label: "授权门禁",
      field: "buildName",
      component: 'Input',
      colProps: { span: 8 },
      slot:'building',
      componentProps: {
        placeholder: "请选择"
      }
    },
    {
      label: "授权门禁",
      field: "buildCode",
      component: 'Input',
      show:false,
      colProps: { span: 8 },
      componentProps: {
        placeholder: "请选择"
      }
    },
    {
      label: "锁定车辆",
      field: "lockCar",
      component: 'Input',
      colProps: { span: 8 },
      componentProps: {
        placeholder: "请输入"
      }
    },
    {
        label: '组织',
        field: 'deptCode',
        component: 'ApiTreeSelect',
        componentProps: () => {
          return {
            api: () => selectGroupTree(),
            resultField: 'dataList', // 返回数据的字段名
            valueField: 'key', // 唯一标识字段
            labelField: 'title', // 显示名称字段
            treeNodeFilterProp: 'title', // 搜索过滤字段
            showSearch: true, // 开启搜索
            optionFilterProp: 'label', // 用于过滤的字段
            immediate: true,
            // multiple: true,
            //请求参数
            params: {},
            //返回结果字段
          }
        },
      },
      
  ]


export const scmSchema: FormSchema[] =
  [
    {
      label: "员工号",
      field: "empNo",
      component: "Input",
      colProps: { "span": 24 },
      componentProps: {
        placeholder: "请输入",
        disabled:true,
      },
    },
    {
      label: "姓名",
      field: "empName",
      component: "Input",
      colProps: { "span": 24 },
      componentProps: {
        placeholder: "请输入",
        disabled:true,
      },
    },
    
    {
      label: "员工车辆",
      field: "carInfos",
      component: "Input",
      colProps: { "span": 24 },
      componentProps: {
        placeholder: "请输入",
        disabled:true,
      }
    },
    
    {
      label: '',
      field: 'factoryPower',
      component: 'ApiSelect',
      required:true,
      slot: 'factoryName',
    },
   
  ]

  export const schemTablecolumns: BasicColumn[] = [
    {
      title: "厂区名称",
      dataIndex: "factoryName",
      width: 120
    },
    {
      title: "授权期限",
      dataIndex: "powerTime",
      width: 120
    },
   
  ]
