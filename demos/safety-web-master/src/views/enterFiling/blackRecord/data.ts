import { BasicColumn, FormSchema } from '/@/components/Table';
import { render ,defaultMonthValue} from '/@/utils/common/renderUtils';
import { StatusOptions, StatusEnumMap, PeopleStatusEnumMap,PeopleStatusOptions} from './const'
import { format } from 'path';
export const columns: BasicColumn[] = [
  {
    title: '流程状态',
    dataIndex: 'status',
     customRender: ({ text }) => {
          console.log(text, String(text), 'text');
          return StatusEnumMap[String(text)];
        },
  },
  {
    title: '人员状态',
    dataIndex: 'applyStatus',
    customRender: ({ text, record }) => {
      console.log(text, record);
      return record.status == '10'?"/":PeopleStatusEnumMap[String(text)];
    },
  },
  {
    title: '审核人员工号',
    dataIndex: 'applyEmpNo',
  },
  {
    title: '审核人岗位',
    dataIndex: 'applyEmpPosition',
  },
  {
    title: '姓名',
    dataIndex: 'name',
  },
  {
    title: '公司名称',
    dataIndex: 'companyName',
  },
  {
    title: '身份证号',
    dataIndex: 'cardNo',
  },
  {
    title: '手机号',
    dataIndex: 'phone',
  },
  {
    title: '备案开始时间',
    dataIndex: 'enterStartTime',
  },
  {
    title: '备案截止时间',
    dataIndex: 'enterEndTime',
  },
  {
    title: '备案厂区',
    dataIndex: 'enterFactory',
  },
  {
    title: '对接人员工号',
    dataIndex: 'buttJointCode',
  },
  {
    title: '对接人姓名',
    dataIndex: 'buttJointName',
  },
  {
    title: '提交时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    fixed: 'right',
    width:80
  },
];

//表单搜索字段
export const formSchemas: FormSchema[] = [
  {
    label: '对接人', //显示label
    field: 'buttJoint', //查询字段
    component: "Input",
      componentProps: {
        placeholder: "请输入"
      }
  },
  {
    label: '身份证号',
    field: 'cardNo',
    component: "Input",
  },
  {
    label: '手机号',
    field: 'phone',
    component: "Input",
  },
  {
    label: '公司名称',
    field: 'companyName',
    component: "Input",
  },
  {
    label: '提交日期',
    field: 'createTime',
    component: 'DatePicker',
    // defaultValue: defaultMonthValue(),
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD'
    },
  },
  {
    label: '审核人',
    field: 'applyEmpInfo',
    component: "Input",
  },
  {
    label: '人员状态', //显示label
    field: 'applyStatus', //查询字段
    component: 'Select', //渲染的组件
    componentProps: {
      placeholder: '请选择状态',
      options: PeopleStatusOptions,
    },
    
  },
  
];



// export const addFormSchema: FormSchema[] = [{}];
