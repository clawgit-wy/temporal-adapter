import {BasicColumn, FormSchema} from "/@/components/Table";
import dayjs from "dayjs";
import {getRiskLevel} from "/@/views/firePatrol/riskLedger/firePatrolSelectApi";
import {selectGroupTree} from "/@/views/firePatrol/riskLedger/components/workPhaseApi";

export const taskDetailsColumns: BasicColumn[] = [
  {title: '任务名称', dataIndex: 'taskName', key: 'taskName'},
  {title: '作业环节名称', dataIndex: 'workStepName', key: 'workStepName'},
  {
    title: '作业部门', dataIndex: 'orgNamePath', key: 'orgNamePath',
    customRender: ({text}) => text && text.length > 0 && text.split(',').length > 0 ? text.split(',').reverse().join('<<') : text,
    width: 300,
  },
  {title: '风险等级', dataIndex: 'riskLevel', key: 'riskLevel', width: 80},
  {title: '检查级别', dataIndex: 'inspectionLevel', key: 'inspectionLevel', width: 80},
  {
    title: '检查周期',
    dataIndex: 'inspectionCycle',
    key: 'inspectionCycle',
    width: 80
  },
  {
    title: '任务周期',
    dataIndex: 'taskCycle',
    key: 'taskCycle',
    customRender: ({
                     text,
                     record
                   }) => `${dayjs(record.taskCycleBeginDate).format('YYYY.MM.DD')}-${dayjs(record.taskCycleEndDate).format('YYYY.MM.DD')}`
  },
  {title: '任务下发时间', dataIndex: 'taskIssuedTime', key: 'taskIssuedTime'},
  {title: '巡检人', dataIndex: 'empName', key: 'empName', width: 80},
  {
    title: '所属部门',
    dataIndex: 'empOrgNamePath',
    key: 'empOrgNamePath',
    customRender: ({text}) => text && text.length > 0 && text.split(',').length > 0 ? text.split(',').reverse().join('<<') : '/',
    width: 300,
  },
  {
    title: '任务状态',
    dataIndex: 'taskStatus',
    key: 'taskStatus',
    slots: {customRender: 'taskStatus'},
    width: 80
  },
  {
    title: '任务完成时间', dataIndex: 'taskCompleteTime', key: 'taskCompleteTime',
    customRender: ({text}) => text && text.length > 0 ? text : '/'
  },
  {
    title: '检查结果', dataIndex: 'inspectionResult', key: 'inspectionResult',
    slots: {customRender: 'inspectionResult'},
    customRender: ({text}) => text && text.length > 0 ? text : '/',
    width: 80
  },
  {
    title: '整改结果', dataIndex: 'rectificationResult', key: 'rectificationResult',
    slots: {customRender: 'rectificationResult'},
    customRender: ({text}) => text && text.length > 0 ? text : '/',
    width: 80
  },
];


export const taskDetailsSearchFormSchema: FormSchema[] = [
  {
    label: '作业环节名称',
    field: 'workStepName',
    component: 'Input',
  },
  {
    label: '检查项目',
    field: 'inspectionItem',
    component: 'Input',
  },
  {
    label: '巡检人',
    field: 'empName',
    component: 'Input',
  },
  {
    label: '风险等级',
    field: 'riskLevel',
    component: 'ApiSelect',
    componentProps: ({formActionType}) => {
      const {setFieldsValue} = formActionType;
      return {
        labelField: 'riskLevel',
        valueField: 'riskLevel',
        api: () => getRiskLevel(),
        onChange: (id, item) => {
          if (item) {
            setFieldsValue({
              inspectionLevel: item.inspectionLevel,
              inspectionCycle: item.inspectionCycle
            })
          } else {
            setFieldsValue({
              inspectionLevel: '',
              inspectionCycle: ''
            })
          }
        }
      }
    }
  },
  {
    label: '任务状态',
    field: 'taskStatus',
    component: 'Select',
    componentProps: {
      options: [
        {label: '已完成', value: '已完成'},
        {label: '未完成', value: '未完成'}
      ],
    },
  },
  {
    label: '作业部门',
    field: 'workDeptCode',
    component: 'ApiTreeSelect',
    componentProps: () => {
      return {
        api: () => selectGroupTree(),
        resultField: 'dataList', // 返回数据的字段名
        valueField: 'key', // 唯一标识字段
        labelField: 'title', // 显示名称字段
        treeNodeFilterProp: 'title', // 搜索过滤字段
        showSearch: true, // 开启搜索
        immediate: true,
        // multiple: true,
        //请求参数
        params: {},
        //返回结果字段
      }
    },
  },
  {
    label: '任务周期',
    field: 'date',
    component: 'RangeDate',
    componentProps: ({formActionType}) => {
      const {setFieldsValue} = formActionType;
      return {
        //日期格式化
        format: 'YYYY-MM-DD',
        //范围文本描述用集合
        placeholder: '请选择开始日期,请选择结束日期',
        onChange: (value) => {
          let valuesArr = value.split(',')
          if (valuesArr.length === 2) {
            setFieldsValue({
              taskCycleBeginDate: valuesArr[0],
              taskCycleEndDate: valuesArr[1],
            })
          } else {
            setFieldsValue({
              taskCycleBeginDate: '',
              taskCycleEndDate: '',
            })
          }
        }
      }
    },
  },
  {
    field: 'taskCycleBeginDate',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
  {
    field: 'taskCycleEndDate',
    label: '',
    component: 'Input',
    show: false, // 不显示，但会提交
  },
]
