import { BasicColumn, FormSchema } from '/@/components/Table';
import { fieldChildTree} from './list.api';

export const columns: BasicColumn[] = [
  {
    title: '类型',
    dataIndex: 'issuedType',
    width: 100,
  },
  {
    title: '类型说明',
    dataIndex: 'issuedTypeDesc',
    width: 110,
  },
  {
    title: '操作人',
    dataIndex: 'operateUser',
    width: 100,
  },
  {
    title: '时间',
    dataIndex: 'operateTime',
    width: 100,
  },
  {
    title: '是否下发异常',
    dataIndex: 'exceptionStatus',
  },
  {
    title: '下发清单',
    dataIndex: 'factory',
    width: 200,
    slots: { customRender: 'distributionList' },
  },
  {
    title: '下发异常员工号',
    dataIndex: 'employeeCode',
    width: 100,
  },
  {
    title: '异常日志',
    dataIndex: 'serviceEndDate',
    width: 110,
    slots: { customRender: 'exceptionLog' },
  },
  
];
export const searchFormSchema: FormSchema[] =[
  {
    label: '下发异常员工号',
    field: 'employeeCode',
    component: 'Input',
  },
  {
    label: '类型',
    field: 'issuedTypeCode',
    component: 'ApiSelect',
    componentProps: {
      api: (params) => fieldChildTree(params),
      labelField: "fieldName",
      valueField: "fieldCode",
      params: {fieldType:'logIssueType'},
    },
    // componentProps:{
    //   options: [
    //     {
    //       label:'手动重新下发',
    //       value:'手动重新下发'
    //     },
    //     {
    //       label:'外协外包入厂权限流程',
    //       value:'外协外包入厂权限流程'
    //     },
    //     {
    //       label:'外协外包ehr推送',
    //       value:'外协外包ehr推送'
    //     },
    //     {
    //       label:'生活照更新定时任务',
    //       value:'生活照更新定时任务'
    //     },
    //     {
    //       label:'人脸凭证定时任务',
    //       value:'人脸凭证定时任务'
    //     },
    //     {
    //       label:'人员权限注销',
    //       value:'人员权限注销'
    //     },
    //   ],
      
    // }
  },
  {
    label: '操作人',
    field: 'operateUser',
    component: 'Input',
  },
  
  {
    label: '操作时间',
    field: 'updateTime',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },

]