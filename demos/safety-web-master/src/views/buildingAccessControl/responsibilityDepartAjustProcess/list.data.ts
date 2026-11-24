import { fieldChildTree } from './list.api';
import { BasicColumn, FormSchema } from '/@/components/Table';
const statusDic = {
  '10': '草稿',
  '20': '审批中',
  '21': '驳回',
  '30': '已审批',
  '31': '手动添加',
  '40': '废弃',
  '50': '已过期',
  '60': '超时结束',
  '00': '废弃'
}
export const columns: BasicColumn[] = [
  { title: '流程编号',
   dataIndex: 'processCode', 
   slots: { customRender: 'flowId' },
   
  },
  { title: '提交时间', 
    dataIndex: 'createTime', 
    width: 150,
   
  },
  { title: '申请人姓名', 
    dataIndex: 'createEmployeeName', 
    width: 150 ,
    
  },
  { title: '责任部门', 
    dataIndex: 'dutyDeptName', 
    width: 150 ,
    
  },
  { title: '厂区', 
    dataIndex: 'combFactoryName', 
    width: 150 ,
    
  },
  { title: '楼栋', 
  dataIndex: 'combBuild', 
  width: 150 ,
  
  },
  { title: '申请门禁', 
    dataIndex: 'newDeviceName', 
    width: 150 ,
    
  },
  { title: '门禁管理员', 
    dataIndex: 'newDepartEmployeeName', 
    width: 150,
   
  },
  { title: '流程状态', 
    dataIndex: 'processStatus',
     width: 150,
     customRender: ({ text }) => statusDic[text] ,
     
  },
  { title: '是否涉及绝密区', 
    dataIndex: 'topSecretFlag', 
    width: 150 ,
    customRender: ({text}) => text == 1 ? '是' : '否',
    
  },
  
  // { title: '设备状态', 
  //   dataIndex: 'deviceStatus', 
  //   width: 100, 
  //   customRender: ({text}) => text == 1 ? '异常' : '正常' ,
    
  // },
  
];

export const searchFormSchema: FormSchema[] =[
 
  { label: '申请人', field: 'createEmployeeNo', component: 'Input'},
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
  
  { label: '流程状态', field: 'processStatus', component: 'Select',
  componentProps: {
    options: [
      {label: '草稿', value: '10'},
      {label: '审批中', value: '20'},
      {label: '驳回', value: '21'},
      {label: '已审批', value: '30'},
      {label: '手动添加', value: '31'},
      {label: '废弃', value: '40'},
      {label: '已过期', value: '50'},
      {label: '超时结束', value: '60'},
    ]
  }
  },
  { label: '责任部门', field: 'dutyDeptCode', component: 'Input' ,show:false},
  { label: '责任部门', field: 'dutyDeptName', component: 'Input' ,slot:'dutyDeptName',componentProps:{
    placeholder: "请选择",
  }},

  { label: '门禁', field: 'newDeviceCode', component: 'Input',show:false},
  { label: '门禁', field: 'newDeviceName', component: 'Input',slot:'newDeviceName',componentProps:{
    placeholder: "请选择",
  }},
  { label: '提交时间', field: 'submitDate', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    }
  },
] 