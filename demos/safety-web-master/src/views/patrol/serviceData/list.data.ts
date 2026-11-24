import { BasicColumn, FormSchema } from '/@/components/Table';
const eventLevelDic = {
  'EVENT_LEVEL_01': '一般隐患/违规',
  'EVENT_LEVEL_02': '较大隐患/违规',
  'EVENT_LEVEL_03': '重大隐患/违规',
}

export const columns: BasicColumn[] = [
  { title: '事件编号', dataIndex: 'flowNo', width: 150, slots: { customRender: 'flowNo' }},
  { title: '厂区', dataIndex: 'factory', width: 150 },
  { title: '任务名称', dataIndex: 'planName', width: 150 },
  { title: '事件类型',  dataIndex: 'eventType',  width: 100 },
  { title: '事项', dataIndex: 'teamName', width: 100 },
  { title: '是否存在异常', dataIndex: 'isException', width: 160, customRender: ({text}) => text == 1 ? '是' : '否' },
  { title: '事件等级', dataIndex: 'eventLevel', width: 110, customRender: (({text}) => eventLevelDic[text]) },
  { title: '责任部门', dataIndex: 'dutyDept', width: 100 },
  { title: '事件发生时间', dataIndex: 'occurTime', width: 120 },
  { title: '地理位置', dataIndex: 'eventAddress', width: 100 },
  { title: '是否重点部位', dataIndex: 'isKeynote', width: 120, customRender: ({text}) => text == 1 ? '是' : '否' },
  { title: '问题描述', dataIndex: 'problemDesc', width: 100 },
  { title: '事件现场图片', dataIndex: 'problemImage', width: 120, slots: { customRender: 'problemImage' } },
  { title: '预计处理完成日期', dataIndex: 'planDealTime', width: 150 },
  { title: '计划处理措施', dataIndex: 'planMeasure', width: 120 },
  { title: '是否当场处理', dataIndex: 'isScene', width: 120, customRender: ({text}) => text == 1 ? '是' : '否' },
  { title: '是否转派外部门', dataIndex: 'isTransfer', width: 130, customRender: ({text, record}) => (text == 1 || record.isDealTransfer == 1 || record.isDealTransfer == 2) ? '是' : '否' },
  { title: '转派部门', dataIndex: 'transferDept', width: 100 },
  { title: '处理人', dataIndex: 'dealPerson', width: 100 },
  { title: '处理人部门班组', dataIndex: 'dealPersonGroup', width: 120},
  { title: '保卫联系人', dataIndex: 'guardPerson', width: 100 },
  { title: '处理完成时间', dataIndex: 'dealTime', width: 130 },
  { title: '事件处理过程及结果', dataIndex: 'dealRecord', width: 150 },
  { title: '处理完成照片', dataIndex: 'dealImage', width: 120, slots: { customRender: 'dealImage' } },
  { title: '事件处理结果及过程评分', dataIndex: 'resultScore', width: 180 },
  { title: '得分原因', dataIndex: 'resultDesc', width: 100 },
  { title: '评分人', dataIndex: 'resultPerson', width: 100 },
  { title: '是否延期处理', dataIndex: 'isDelay', width: 120, customRender: ({text}) => text == 1 ? '是' : '否' },
  { title: '事件上报人', dataIndex: 'submitName', width: 100 },
  { title: '事件上报时间', dataIndex: 'createTime', width: 120 },
  { title: '是否指挥中心派单', dataIndex: 'isAssign', width: 150, customRender: ({text}) => text == 1 ? '是' : '否' },
  { title: '指派人', dataIndex: 'assignPerson', width: 100 },
  { title: '是否抢单', dataIndex: 'isGrab', width: 100, customRender: ({text}) => text == 1 ? '是' : '否' },
  { title: '抢单人', dataIndex: 'grabPerson', width: 100 },
  { title: '接单时间', dataIndex: 'grabTime', width: 100 },
  { title: '返工次数', dataIndex: 'reworkNum', width: 100 },
];
export const searchFormSchema: FormSchema[] =[
  { label: '厂区', field: 'factoryId', component: 'FactoryId'},
  { label: '事件编号', field: 'flowNo', component: 'Input'},
  { label: '事件类型', field: 'eventType', component: 'Input'},
  { label: '事项', field: 'teamName', component: 'Input'},
  { label: '事件等级', field: 'eventLevel', component: 'Select', componentProps: {
    options: [
      {label: '一般隐患/违规', value: 'EVENT_LEVEL_01'},
      {label: '较大隐患/违规', value: 'EVENT_LEVEL_02'},
      {label: '重大隐患/违规', value: 'EVENT_LEVEL_03'},
    ]
  }},
  { label: '地理位置', field: 'eventAddress', component: 'Input'},
  { label: '是否当场处理', field: 'isScene', component: 'Select', componentProps: {
    options: [{label: '是', value: 1}, {label: '否', value: 0}]
  }},
  { label: '事件发生时间', field: 'occur', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    }
  },
  { label: '是否转派外部门', field: 'isTransfer', component: 'Select', componentProps: {
    options: [{label: '是', value: 1}, {label: '否', value: 0}]
  }},
  { label: '任务名称', field: 'planName', component: 'Input'},
  { label: '转派部门', field: 'transferDept', component: 'Input' },
  { label: '处理人', field: 'dealPerson', component: 'Input'},
  { label: '处理人部门班组', field: 'dealPersonGroup', component: 'Input'},
 
  { label: '预计处理完成日期', field: 'planDate', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    }
  },
  { label: '是否重点部位', field: 'isKeynote', component: 'Select', componentProps: {
    options: [{label: '是', value: 1}, {label: '否', value: 0}]
  }},
  { label: '是否存在异常', field: 'isException', component: 'Select', componentProps: {
    options: [{label: '是', value: 1}, {label: '否', value: 0}]
  }},
  { label: '保卫联系人', field: 'guardPerson', component: 'Input'},
  { label: '是否延期处理', field: 'isDelay', component: 'Select',
    componentProps: {
      options: [{label: '是', value: 1}, {label: '否', value: 0}]
    },
  },
  { label: '处理完成时间', field: 'deal', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    }
  },
  { label: '事件上报人', field: 'submitName', component: 'Input'},
  { label: '上报人部门班组', field: 'submitDept', component: 'Input'},
  { label: '是否指挥中心派单', field: 'isAssign', component: 'Select',
    componentProps: {
      options: [{label: '是', value: 1}, {label: '否', value: 0}]
    },
  },
  { label: '事件上报时间', field: 'create', component: 'RangeDate',
    componentProps: {
      valueType: 'date'
    }
  },
  { label: '是否抢单', field: 'isGrab', component: 'Select',
    componentProps: {
      options: [{label: '是', value: 1}, {label: '否', value: 0}]
    },
  },
] 