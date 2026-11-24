import { BasicColumn, FormSchema } from '/@/components/Table';
import { selectLinkageList } from '/@/api/common/api';
import { render } from '/@/utils/common/renderUtils';

// 申请状态
const statusDic = {
  '10': '草稿',
  '20': '待审批',
  '21': '驳回',
  '30': '已审批',
  '31': '手动添加',
  '00': '废弃',
  '50': '已过期',
  '60': '超时结束',
};

// 定义表格列
export const columns: BasicColumn[] = [
  {
    title: '流程ID',
    dataIndex: 'processCode',
    width: 220,
    slots: { customRender: 'processCode' },
  },
  {
    title: '提交时间',
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: '员工号',
    dataIndex: 'createEmployeeNo',
  },

  {
    title: '姓名',
    dataIndex: 'createEmployeeName',
  },
  {
    title: '组织',
    dataIndex: 'createEmployeeDept',
    width: 150,
  },
  {
    title: '类型',
    dataIndex: 'powerType',
    width: 100,
    customRender: ({ text }) => {
      const tpyeMap = {
        '1': '开通',
        '2': '关闭',
      };
      return tpyeMap[text];
    },
  },
  {
    title: '申请厂区',
    dataIndex: 'montageFactoryName',
    customRender: ({ text }) => {
      return render.renderTip(text);
    },
  },
  {
    title: '审核人',
    dataIndex: 'curHandlerName',
    width: 150,
  },
  {
    title: '流程状态',
    dataIndex: 'status',
    customRender: ({ text }) => {
      return statusDic[text];
    },
  },
];

// 定义搜索表单
export const searchFormSchema: FormSchema[] = [
  {
    label: '员工号',
    field: 'createEmployeeNo',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 6 },
  },
  {
    label: '姓名',
    field: 'createEmployeeName',
    component: 'Input',

    colProps: { span: 6 },
  },
  {
    label: '流程状态',
    field: 'status',
    component: 'Select',
    componentProps: {
      placeholder: '请选择',
      options: Object.entries(statusDic).map(([value, label]) => ({
        value,
        label,
      })),
    },
    colProps: { span: 6 },
  },
  {
    label: '提交时间',
    field: 'createTimeRange',
    component: 'RangePicker',
    componentProps: {
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      placeholder: ['开始日期', '结束日期'],
    },
  },
  {
    label: '申请厂区',
    field: 'montageFactoryCode',
    component: 'ApiSelect',
    componentProps: () => {
      return {
        api: async () => {
          const res = await selectLinkageList();
          return res;
        },
        placeholder: '请选择厂区',
        valueField: 'id',
        labelField: 'fieldName',
      };
    },
    colProps: { span: 24 },
  },
];
