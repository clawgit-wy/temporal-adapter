import { fieldManageTree } from './list.api';
import { BasicColumn, FormSchema } from '/@/components/Table';
const eventLevelDic = {
  'EVENT_LEVEL_01': '一般隐患/违规',
  'EVENT_LEVEL_02': '较大隐患/违规',
  'EVENT_LEVEL_03': '重大隐患/违规',
}

export const columns: BasicColumn[] = [
  { title: '验证类型',  dataIndex: 'businessType',  width: 100 },
  { title: '主题', dataIndex: 'subject', width: 150, slots: { customRender: 'subject' }},
  { title: '时间', dataIndex: 'createTime', width: 150 },
  { title: '位置/岗位', dataIndex: 'location', width: 150 },
  { title: '车牌号', dataIndex: 'carNumber', width: 100 },
  { title: '出门人', dataIndex: 'gooutEmpName', width: 160},
  { title: '联系电话', dataIndex: 'gooutPhone', width: 110},
  { title: '单位', dataIndex: 'gooutEmpDept', width: 100 },
  { title: '厂区', dataIndex: 'factory', width: 120 },
  { title: '物资类别', dataIndex: 'goodsType', width: 100 },
  { title: '原因类别', dataIndex: 'reasonType', width: 120},
  { title: '原因说明', dataIndex: 'reasonDesc', width: 100 },
  { title: '出厂厂区', dataIndex: 'outFactoryName', width: 120},
  { title: '入厂厂区', dataIndex: 'targetFactory', width: 150 },
  { title: '货物明细', dataIndex: 'goodsDetail', width: 120, slots: { customRender: 'goodsDetail' }},
  { title: '是否验证通过', dataIndex: 'validAccess', width: 130, customRender: ({text, record}) => text ? text : record.exceptionType ? '否' : '是'},
  { title: '磅单重量', dataIndex: 'weightListWeight', width: 100 },
  { title: '复磅重量', dataIndex: 'recheckWeight', width: 100 },
  { title: '排放标准', dataIndex: 'emissionStandard', width: 120},
  { title: '是否加入黑名单', dataIndex: 'blackList', width: 100, customRender: ({text}) => text == 1 ? '是' : '否'  },
  { title: '异常类型', dataIndex: 'exceptionType', width: 130 },
  { title: '备注', dataIndex: 'remark', width: 150 },
  { title: '抽查人姓名+员工号', dataIndex: 'checkPerson', width: 120, customRender: ({text, record}) => `${text}/${record.checkPersonNo}` },
];
export const searchFormSchema: FormSchema[] =[
  { label: '位置', field: 'location', component: 'Input'},
  { label: '车牌号', field: 'carNumber', component: 'Input'},
  { label: '司机', field: 'gooutEmpName', component: 'Input'},
  { label: '司机联系电话', field: 'gooutPhone', component: 'Input'},
  { label: '单位名称', field: 'gooutEmpDept', component: 'Input'},
  { label: '厂区', field: 'factoryId', component: 'FactoryId'},
  { label: '物资类别', field: 'goodsType', component: 'ApiSelect', componentProps: {
    api: (params) => fieldManageTree(params),
    labelField: "fieldName",
    valueField: "fieldName",
    params: {fieldType: 'goodsOut_goods_type', topFlag: false},
  }},
  // 
  { label: '原因类别', field: 'reasonType', component: 'Select', componentProps: {
    options: [
      {label: '外部电脑退网', value: '外部电脑退网'},
      {label: '公司以外区域办公', value: '公司以外区域办公'},
      {label: '公司内跨厂区办公', value: '公司内跨厂区办公'},
      {label: 'IT返厂维修', value: 'IT返厂维修'},
      {label: '跨厂区转运', value: '跨厂区转运'},
      {label: '供应商物资出厂', value: '供应商物资出厂'},
      {label: '外购件质量问题退换货', value: '外购件质量问题退换货'},
      {label: '外出代加工', value: '外出代加工'},
      {label: '外出维修', value: '外出维修'},
      {label: '外出售卖', value: '外出售卖'},
      {label: '施工物资出厂', value: '施工物资出厂'},
      {label: '外销', value: '外销'},
      {label: '其他', value: '其他'},
    ]
  }},
  { label: '出厂厂区', field: 'outFactoryId', component: 'FactoryId',componentProps:{
    mode: 'multiple',
  }},
  { label: '入厂厂区', field: 'targetFactoryId', component: 'FactoryId',},
  { label: '排放标准', field: 'emissionStandard', component: 'Select', componentProps: {
    options: [
      {label: '国三', value: '国三'},
      {label: '国四', value: '国四'},
      {label: '国五', value: '国五'},
      {label: '国六', value: '国六'},
    ]
  },},
  { label: '是否加入黑名单', field: 'blackList', component: 'Select', componentProps: {options: [{label: '是', value: 1}, {label: '否', value: 0}]}},
  { label: '是否验证通过', field: 'validAccess', component: 'Select', componentProps: {
    options: [{label: '是', value: '是'}, {label: '否', value: '否'}]
  }},
  { label: '异常类型', field: 'exceptionType', component: 'ApiSelect', componentProps: {
    api: (params) => fieldManageTree(params),
    labelField: "fieldName",
    valueField: "fieldName",
    params: {fieldType: 'goodsOut_dict_0', topFlag: false},
  }},
  { label: '抽查人姓名', field: 'checkPerson', component: 'Input'},
  { label: '抽查人员工号', field: 'checkPersonNo', component: 'Input'},
  { label: '抽查时间', field: 'RangeDate', component: 'RangeDate',componentProps: {
    valueType: 'date'
  }},
  
]
export const formSchemas: FormSchema[] = [
  { label: 'ID', field: 'id', component: 'Input', show: false},
  { label: '是否验证通过', field: 'validAccess', component: 'Select', required: true, componentProps: {
    options: [{label: '是', value: '是'}, {label: '否', value: '否'}]
  }},
  { label: '异常类型', field: 'exceptionType', required: true, component: 'ApiSelect', ifShow: ({values}) => {
    return values.validAccess == '否'
  }, componentProps: {
    api: (params) => fieldManageTree(params),
    labelField: "fieldName",
    valueField: "fieldName",
    params: {fieldType: 'goodsOut_exec_type', topFlag: false},
  }},
  { label: '备注', field: 'remark', required: true, component: 'InputTextArea', componentProps: {
    autoSize:{minRows: 2, maxRows: 3}
  }},
]