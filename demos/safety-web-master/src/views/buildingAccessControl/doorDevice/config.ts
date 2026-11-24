import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { fieldChildTree,Api} from './field.api';
import { facList } from '../../vehicle/vehiclelist/api';

// // 获取厂区列表
// const factoryList = async ()=> {
//   const res = await fieldChildByFieldType({fieldType:'factory'});
//   console.log(factoryList, 'factoryListsfactoryLists');
//   return res
// }
// // const buildingList = async (id)=> {
// //   const res = await fieldChildByFieldType({fieldType:'factory'});
// //   console.log(factoryList, 'factoryListsfactoryLists');
// //   return res
// // }
// // factoryList();

export const columns: BasicColumn[] = [
  {
    "title": "门禁名称",
    "dataIndex": "deviceName",
    "width": 120
  },
  {
    "title": "门禁名称(AN)",
    "dataIndex": "anDeviceName",
    "width": 120
  },
  {
    "title": "门禁编码",
    "dataIndex": "deviceCode",
    "width": 120
  },
  {
    "title": "厂区(AN)",
    "dataIndex": "anFactoryName",
    "width": 120
  },
  {
    "title": "楼栋(AN)",
    "dataIndex": "anBuilding",
    "width": 120
  },
  {
    "title": "状态",
    "dataIndex": "deviceStatus",
    "width": 120,
    customRender: ({ text }) => {
      return text==0?'在线':text==1?'离线':'';
    },
  },
  {
    "title": "备注",
    "dataIndex": "anRemark",
    "width": 120
  },
 
]

export const searchFormSchema: FormSchema[] =
  [
    {
      label: '厂区',
      field: 'factoryCode',
      component: 'ApiSelect',
      componentProps: (params) => {
        const { formActionType } = params;
        const { setFieldsValue } = formActionType;
        return {
          api: async () => {
            const res = await fieldChildTree({fieldType:'factory'});
            return res;
          },
          mode: 'default',
          placeholder: '请选择厂区',
          valueField: 'id',
          labelField: 'fieldName',
          onChange: () => {
            setFieldsValue({ building: '' });
          },
        };
      },
    },
    {
      label: '楼栋',
      field: 'building',
      component: 'ApiSelect',
      componentProps: (params) => {
        console.log('楼栋字段 componentProps 参数:', params);
        const { formModel } = params;
        return {
          api: async () => {
            // 如果没有选择厂区，返回空数组
            if (!formModel.factoryCode) {
              console.log('未选择厂区，楼栋返回空数组');
              return [];
            }
            const res = await fieldChildTree({fieldType:formModel.factoryCode});
            console.log('楼栋数据获取成功:', res);
            return res;
          },
          mode: 'default',
          placeholder: '请选择楼栋',
          valueField: 'fieldCode',
          labelField: 'fieldName',
          disabled: !formModel.factoryCode, // 如果没有选择厂区，禁用楼栋选择
        };
      },
    },
   
    {
      label: "门禁名称",
      field: "deviceName",
      component: 'Input',
      colProps: { span: 8 },
      getLabel:true,
      componentProps: {
        placeholder: "请输入门禁名称"
      }
    },
    {
      label: '状态',
      field: 'deviceStatus',
      component: "Input",
      componentProps: {
        placeholder: "请输入"
      }
    },
    {
      label: '门禁编码',
      field: 'extendId',
      component: "Input",
      componentProps: {
        placeholder: "请输入门禁编码"
      }
    },
  ]


export const scmSchema: FormSchema[] =
  [
    {
      label: "门禁名称",
      field: "deviceName",
      component: "Input",
      colProps: { "span": 24 },
      componentProps: {
        placeholder: "请输入门禁名称",
        disabled:true,
      },
      
    },
    {
      label: "门禁名称(AN)",
      field: "anDeviceName",
      component: "Input",
      required:true,
      colProps: { "span": 24 },
      componentProps: {
        
        placeholder: "请输入门禁名称(AN)"
      }
    },
    {
      label: '厂区(AN)',
      field: 'anFactoryName',
      show: false,
      component: 'Input',
    },
    {
      label: "厂区(AN)",
      field: "anFactoryCode",
      labelField:'anFactoryName',
      component: "Select",
      required:true,
      colProps: { "span": 24 },
      slot:'anFactoryCode',
      componentProps: {
        placeholder: "请选择"
      },
      
    },
    
    {
      label: '楼栋(AN)',
      field: 'anBuilding',
      show: false,
      component: 'Input',
    },
    {
      label: "楼栋(AN)",
      field: "anBuildingCode",
      labelField:'anBuilding',
      required:true,
      colProps: { "span": 24 },
      component: "Select",
      slot:"anBuildingCode",
      componentProps: {
        placeholder: "请选择"
      },
    },
    {
      label: "序号",
      field: "anShort",
      component: "Input",
      required:true,
      colProps: { "span": 24 },
      componentProps: {
        placeholder: "请输入厂区"
      }
    },
    {
      label: "备注",
      field: "anRemark",
      component: "InputTextArea",
      colProps: { "span": 24 },
      componentProps: {
        placeholder: "请填写物资出场权限",
        maxlength: 50,
      }
    },
    
    
  ]

  export const cloneScmSchema: FormSchema[] =
  [
    {
      label: "克隆设备",
      field: "cloneDeviceName",
      component: "YTSearchSelect",
      slot:"cloneDeviceName",
      colProps: { "span": 24 },
      // componentProps: (params) => {
      //   console.log('输入参数==:', params);
      //   // const { formModel } = params;
      //   return {
      //     loadData: async () => {
      //       // 如果没有选择厂区，返回空数组

      //       const res = await fieldChildTree({fieldType:''});
      //       console.log('楼栋数据获取成功:', res);
      //       return res;
      //     },
      //     mode: 'default',
      //     placeholder: '请选择要克隆的设备',
      //     async:true,
      //     pageSize:50,
      //   };
      // },
      componentProps: () => ({
        placeholder: '请选择要克隆的设备',
        async: true,               // 打开异步
        pageSize: 50,              // 每页条数
        url:Api.getCopyDeviceInfo,
        labelField:"anDeviceName",
        valueField:'deviceCode',
        api: async ({ keyword, pageNo = 1, pageSize = 50 }) => {
          // keyword 就是输入框里的内容
          const res = await fieldChildTree({
            deviceNameOrCode:keyword,
            pageNo,
            pageSize,
          });
          console.log("返回结果==",res)
          // 必须符合 JEECG 分页格式
          return {
            records: res.records || res, // 兼容不分页接口
            total: res.total || res.length,
          };
        },
        
      }),
      
    },
  ]