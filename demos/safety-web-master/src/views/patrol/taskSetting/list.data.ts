import { BasicColumn, FormSchema } from '/@/components/Table';
import { fieldTree, selectRoleList, fieldManageTree, fieldChildByFieldType } from './list.api'
import { ref } from 'vue';

export const columns: BasicColumn[] = [
  {
    title: '任务名称',
    dataIndex: 'planName',
    width: 150,
  },
  {
    title: '应用厂区',
    dataIndex: 'factoryName',
    width: 100,
  },
  {
    title: '应用角色',
    dataIndex: 'cycleRoleName',
    width: 100,
  },
  {
    title: '任务周期',
    dataIndex: 'cycle',
    width: 100,
  },
  {
    title: '次数',
    dataIndex: 'frequency',
    width: 100,
  },
  {
    title: '事件类型',
    dataIndex: 'typeName',
    width: 100,
  },
  {
    title: '应用项目',
    dataIndex: 'teamName',
    width: 160,
  },
  {
    title: '休眠开始日期',
    dataIndex: 'dormancyBegin',
    width: 100,
  },
  {
    title: '休眠结束日期',
    dataIndex: 'dormancyEnd',
    width: 100,
  },
  {
    title: '任务开始日期',
    dataIndex: 'beginDate',
    width: 100,
  },
  {
    title: '任务结束日期',
    dataIndex: 'endDate',
    width: 100,
  },
  {
    title: '任务详情',
    dataIndex: 'remark',
    width: 100,
  },
];
const teamOptions:any = ref([])
export const onPlanTypeChange = (val) => {
  console.log('planChange', val);
  
  const params = {
    topFlag: false
  }
  if (val == 1) {
    params['fieldType'] = 'inspection001_zaxclx_0'
  } else if(val == 2) {
    params['fieldType'] = 'inspect_002_ywyzlx_0'
  } else {
    params['fieldType'] = 'inspect_003'
  }
  if (val == 3) {
    fieldChildByFieldType(params).then(res => {
      teamOptions.value = res.map(item => ({label: item.fieldName, value: item.id}))
    })
  }else{
    fieldTree(params).then(res => {
      teamOptions.value = res.map(item => ({label: item.fieldName, options: item.regionList.map(child => ({label: child.fieldName, value: child.id}))}))
    })
  }
}
export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '任务名称',
    field: 'planName',
    component: 'Input',
    required: true,
  },
  {
    label: '应用厂区',
    field: 'factoryName',
    show: false,
    component: 'Input',
  },
  {
    label: '应用厂区',
    field: 'factoryId',
    labelField: 'factoryName',
    component: 'FactoryId',
    required: true,
    componentProps: {
      mode:'multiple',
    }
  },
  {
    label: '应用角名',
    field: 'cycleRoleName',
    component: 'Input',
    show:false,
  },
  {
    label: '应用角色',
    field: 'cycleRoleId',
    labelField: 'cycleRoleName',
    component: 'ApiSelect',
    componentProps: {
      api: ()=> selectRoleList({}),
        //数值转成String
        numberToString: false,
        //标题字段
        labelField: 'name',
        //值字段
        valueField: 'id',
    }
  },
  {
    label: '任务周期',
    field: 'cycle',
    component: 'InputNumber',
    required: true,
  },
  {
    label: '次数',
    field: 'frequency',
    component: 'InputNumber',
    required: true,
  },
  {
    label: '任务类型',
    field: 'planType',
    component: 'Select',
    componentProps: {
      options: [
        {label: '治安巡查', value: 1},
        {label: '业务验证', value: 2},
        {label: '岗位验证', value: 3},
      ],
      onChange: onPlanTypeChange
    },
    required: true,
  },
  {
    label: '应用项目',
    field: 'teamName',
    component: 'Input',
    show: false
  },
  {
    label: '应用项目',
    field: 'teamId',
    labelField: 'teamName',
    component: 'Select',
    componentProps: {
      options: teamOptions
    },
    required: true,
  },
  {
    label: '任务日期',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },
  {
    label: '任务详情',
    field: 'remark',
    component: 'InputTextArea',
    componentProps:{
      //可以点击清除图标删除内容
      allowClear: true,
      //是否展示字数
      showCount: true,
      //自适应内容高度，可设置为 true | false 或对象：{ minRows: 2, maxRows: 6 }
      autoSize:{
        //最小显示行数
        minRows: 4,
        //最大显示行数
        maxRows: 6,
        maxlength: 400
      },
    }
  },
];
export const searchFormSchema: FormSchema[] =[
  
  {
    label: '任务名称',
    field: 'planName',
    component: 'Input',
  },
  {
    label: '厂区',
    field: 'factoryId',
    component: 'FactoryId',
  },
  {
    label: '应用项目',
    field: 'teamName',
    component: 'Input',
  },
  {
    label: '任务时间',
    field: 'date',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
    },
  },

] 