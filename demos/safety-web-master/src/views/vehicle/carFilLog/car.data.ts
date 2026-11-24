import { BasicColumn, FormSchema } from '/@/components/Table';
import { render, defaultWeekValue } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';
// import { filterDictTextByCache } from '/@/utils/dict/JDictSelectUtil';
const stateList = [
  {
    label: '禁止入厂',
    value: 1,
    key: 1,
  },
  {
    label: '特批进厂',
    value: 2,
    key: 2,
  },
];
export const columns: BasicColumn[] = [
  {
    title: '备案类型',
    dataIndex: 'fdEnterType',
    width: 100,
  },
  {
    title: '姓名/车牌号',
    dataIndex: 'fdName',
    width: 100,
  },
  {
    title: '手机号',
    dataIndex: 'fdPerPhone',
    width: 100,
  },
  {
    title: '身份证号',
    dataIndex: 'fdCardNo',
    width: 100,
  },
  {
    title: '准入时间-始',
    dataIndex: 'enterStartTime',
    width: 100,
  },
  {
    title: '准入时间-终',
    dataIndex: 'enterEndTime',
    width: 100,
  },
  {
    title: '证件照',
    dataIndex: 'fdImg',
    width: 150,
    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return render.renderImage2({ text });
      }
    },
  },

  {
    title: '备案厂区',
    dataIndex: 'enterFactory',
    width: 150,
  },
  {
    title: '下发时间',
    dataIndex: 'issuanceTime',
    width: 120,
  },
  {
    title: '下发结果',
    dataIndex: 'issuanceResult',
    slots: { customRender: 'issuanceResult' },
    // customRender: ({text}) => text == "true" ? '成功' : '失败',
    width: 100,
  },
];

//表单搜索字段
export const searchFormSchema: FormSchema[] = [
  {
    label: '姓名/车牌号', //显示label
    field: 'fdName', //查询字段
    component: 'Input', //渲染的组件
  },
  {
    label: '身份证号',
    field: 'fdCardNo',
    component: 'Input',
  },
  {
    label: '下发结果',
    field: 'issuanceResult',
    component: 'Select',
    componentProps: {
      options: [
        { label: '成功', value: 'true' },
        { label: '失败', value: 'false' },
      ],
    },
  },
  {
    label: '备案类型',
    field: 'fdEnterTypeCode',
    component: 'Select',
    componentProps: {
      options: [
        { label: '物流司机', value: 10 },
        { label: '随行人员', value: 20 },
      ],
    },
  },
  {
    label: '提交时间',
    field: 'submissionTime',
    component: 'RangeDate',
    colProps: { span: 8 },
    defaultValue: defaultWeekValue(),
    componentProps: {
      // placeholder: '请选择提交时间',
      // disabledDate:(current)=>{
      //   return( current && current < dayjs().endOf('day') ) || current > dayjs().add(1,"week");
      // },
      // placeholder: ['请选择开始时间', '结束时间自动生成'],
      // onOpenChange:(open)=>{
      // }
    },
  },
];

// export const addFormSchema: FormSchema[] = [{}];
