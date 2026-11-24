import dayjs from 'dayjs';
import { h } from 'vue';
import { fieldTree } from './list.api';
import { BasicColumn, FormSchema } from '/@/components/Table';
const errorTypeDicArr = [ 
  {label: '流程超时', value: '流程超时'},
  {label: '放行异常', value: '放行异常'},
  {label: '抽查异常', value: '抽查异常'},
  {label: '超时未接收', value: '超时未接收'},
  {label: '超时接收', value: '超时接收'},
  {label: '未接收/部分接收', value: '未接收/部分接收'},
  {label: '超时未出厂', value: '超时未出厂'},
  {label: '手动标记异常', value: '手动标记异常'},
 ]

export const columns: BasicColumn[] = [
  { title: '流程编号', dataIndex: 'flowNo', width: 150},
  { title: '主题', dataIndex: 'subject', width: 150, slots: { customRender: 'subject' } },
  { title: '提交时间', dataIndex: 'createTime', width: 150, customRender: ({text}) => text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '' },
  { title: '异常类型',  dataIndex: 'exceptionType',  width: 100 },
  { title: '详细描述', dataIndex: 'detailedDescription', width: 100 },
  // { title: '操作人', dataIndex: 'operatorName', width: 160, customRender: ({text}) => text == 1 ? '是' : '否' },
  { title: '操作人', dataIndex: 'operatorName', width: 160},
  { title: '异常创建时间', dataIndex: 'eventCreatedTime', width: 110, customRender: ({text}) => text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '' },
  { title: '流程当前节点', dataIndex: 'curNodeName', width: 100 },
  { title: '申请人姓名', dataIndex: 'submitName', width: 120 },
  { title: '申请人员手机号', dataIndex: 'submitPhone', width: 100},
  { title: '申请人员工号', dataIndex: 'submitNo', width: 120 },
  { title: '申请人部门', dataIndex: 'submitDept', width: 120},
  { title: '出厂厂区', dataIndex: 'outFactoryName', width: 100 },
  { title: '出门类型', dataIndex: 'outTypeName', width: 120},
  { title: '审核人', dataIndex: 'examineEmpName', width: 150 },
  { title: '审核人员工号', dataIndex: 'examineEmpNo', width: 120 },
  { title: '审核人部门', dataIndex: 'examineDept', width: 120},
  { title: '出门人类型', dataIndex: 'outPersonType', customRender: ({text}) => ({'1': '公司内部人员', '2': '外部人员'}[text]) },
  { title: '出门人姓名', dataIndex: 'gooutEmpName', width: 100 },
  { title: '出门人员工号', dataIndex: 'gooutEmpNo', width: 100 },
  { title: '出门人手机号', dataIndex: 'gooutPhone', width: 100},
  { title: '出门人部门', dataIndex: 'gooutEmpDept', width: 120},
  { title: '放行人姓名', dataIndex: 'releaseEmp', width: 120 },
  { title: '放行时间', dataIndex: 'releaseTime', width: 120 },
  { title: '放行状态', dataIndex: 'releaseStatus', width: 120 ,customRender: ({ text, record }) => {
    //0-待放行,1-已放行,2-放行异常,3-废弃
    if(text == 0){
      return '待放行'
    }
    if(text == 1){
      return '已放行'
    }
    if(text == 2){
      return '放行异常'
    }
    if(text == 3){
      return '废弃'
    }
  }
},
  { title: '是否车辆运输', dataIndex: 'isVehicle', width: 100 },
  { title: '车牌号', dataIndex: 'vehicleNo', width: 130 },
  { title: '是否需要接收人确认', dataIndex: 'isSetAccept', width: 150 },
  { title: '接收人', dataIndex: 'receiveEmp', width: 120},
  { title: '现场照片', dataIndex: 'sceneImg', width: 180, customRender: ({text}) => h('a', {href: text, target: '_blank'}, '查看') },
  { title: '接收照片', dataIndex: 'receiveImg', width: 100, customRender: ({text}) => h('a', {href: text, target: '_blank'}, '查看') },
  { title: '物资是否接收', dataIndex: 'receiveStatus', width: 100, customRender: ({text}) => ({'1': '全部接收', '2': '部分接收', '0': '未接收'}[text]) },
  { title: '未接收/超时接收原因', dataIndex: 'unReceiveReason', width: 120},
  { title: '过磅单/证明材料', dataIndex: 'weightDataUrl', width: 100, customRender: ({text}) => h('a', {href: text, target: '_blank'}, '查看') },
  { title: '备注', dataIndex: 'remarks', width: 120 },
];
export const searchFormSchema: FormSchema[] =[
  { label: '申请人', field: 'submitName', component: 'Input', componentProps: {placeholder: '姓名/联系电话'}},
  { label: '出门人', field: 'gooutEmpName', component: 'Input', componentProps: {placeholder: '姓名/联系电话'}},
  { label: '审核人', field: 'examineEmpName', component: 'Input', componentProps: {placeholder: '姓名/员工号/eip账号'}},
  // { label: '状态', field: 'exceptionType', component: 'Input'},
  { label: '出门类型', field: 'outTypeName', component: 'ApiSelect', componentProps: {
    api: (params) => fieldTree(params),
    labelField: "fieldName",
    valueField: "fieldName",
    params: {fieldType: 'goodsOut_dict_0_type', topFlag: false},
  }},
  { label: '出厂厂区', field: 'factoryLocation', component: 'FactoryId'},
  
  { label: '提交时间', field: 'eventCreatedTime', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    },
    defaultValue:`${dayjs().subtract(7, 'day').format('YYYY-MM-DD')},${dayjs().format('YYYY-MM-DD')}`,

  },
] 
export const formSchemas: FormSchema[] = [
  { label: 'Id', show: false, field: 'id', component: 'Input', componentProps: {placeholder: '备注'}},
  { label: '异常类型', field: 'exceptionType', component: 'Select', componentProps: {
    options: errorTypeDicArr
  }},
  { label: '原因说明', field: 'detailedDescription', component: 'InputTextArea', componentProps: {placeholder: '原因说明'}},
  { label: '备注', field: 'remarks', component: 'InputTextArea', componentProps: {placeholder: '备注'}},
]