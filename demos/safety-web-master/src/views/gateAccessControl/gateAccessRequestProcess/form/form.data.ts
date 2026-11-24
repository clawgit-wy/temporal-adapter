import { FormSchema } from '/@/components/Table';
import { selectLinkageList } from '/@/api/common/api';
import { Api, getListByUserCode } from './form.api';
import { useFixedStartDateRangePicker } from '/@/utils/datePicker';
import dayjs from 'dayjs';
// 定义类型
export interface EmployeeItem {
  id: string;
  name: string;
  employeeId: string;
}

export interface FactoryItem {
  id: string;
  name: string;
  endDate: string;
  expired: boolean;
}

export interface GateItem {
  id: string;
  title: string;
  value: string;
  pId: string | number;
}

// 格式化日期为YYYY-MM-DD格式
const formatDate = (dateString) => {
  if (!dateString) return '';
  return dayjs(dateString).format('YYYY-MM-DD');
};

// 定义管理员表单
export const manageFormSchemas: FormSchema[] = [
  {
    label: '姓名',
    field: 'createEmployeeName',
    component: 'Input',
    componentProps: {
      placeholder: '',
    },
    dynamicDisabled: ({ values }) => {
      return true;
    },
    colProps: { span: 12 },
  },
  {
    label: '员工号',
    field: 'createEmployeeNo',
    component: 'Input',
    componentProps: {
      placeholder: '',
    },
    dynamicDisabled: ({ values }) => {
      return true;
    },
    colProps: { span: 12 },
  },

  {
    label: '部门',
    field: 'createDeptName',
    component: 'Input',
    componentProps: {
      placeholder: '',
    },
    dynamicDisabled: ({ values }) => {
      return true;
    },
    colProps: { span: 24 },
  },

  {
    label: '类型',
    field: 'powerType',
    component: 'RadioGroup',
    componentProps: {
      options: [
        { label: '开通', value: '1' },
        { label: '关闭', value: '2' },
      ],
    },
    defaultValue: '',
    colProps: { span: 24 },
    rules: [{ required: true }],
  },
  {
    label: '新增厂区权限',
    field: 'addDivisionLine',
    component: 'Divider',
    componentProps: {
      type: 'horizontal',
    },
    ifShow: ({ values }) => {
      return values.powerType && values.powerType == '1';
    },
  },

  {
    label: '新增厂区权限',
    field: 'addUserList',
    component: 'ExportExcelEmp',
    colProps: { span: 24 },
    // required: true,
    componentProps: () => {
      return {
        tips: '流程审批通过后将同步变更员工车辆厂区权限',
        valueType: 'array',
        columns: [
          { title: '员工号', dataIndex: 'employeeCode', key: 'employeeCode' },
          { title: '姓名', dataIndex: 'employeeName', key: 'employeeName' },
          { title: '车牌号', dataIndex: 'vehicleInfo', key: 'vehicleInfo' },
        ],
        mapDataFn: async (dataArr) => {
          console.log('**********导入的数据', dataArr);
          const res: any[] = await getListByUserCode(dataArr.map((item) => item.employeeCode));
          console.log('导入的数据', res);

          return res.map((item) => {
            return {
              employeeCode: item.employeeNo,
              employeeName: item.employeeName,
              vehicleInfo: item.carNo,
            };
          });
        },
        downloadUrl: Api.downloadEmpTemplate,
      };
    },
    ifShow: ({ values }) => {
      return values.powerType && values.powerType == '1';
    },
    dynamicRules: ({ values }) => {
      return [
        {
          required: true,
          message: '请导入人员',
          trigger: 'change',
        },
      ];
    },
  },
  {
    label: '关闭厂区权限',
    field: 'closeDivisionLine',
    component: 'Divider',
    componentProps: {
      type: 'horizontal',
    },
    ifShow: ({ values }) => {
      return values.powerType && values.powerType == '2';
    },
  },
  {
    label: '关闭厂区权限',
    field: 'closeUserList',
    component: 'ExportExcelEmp',
    colProps: { span: 24 },
    ifShow: ({ values }) => {
      return values.powerType && values.powerType == '2';
    },
    componentProps: {
      tips: '关闭厂区权限后，厂区内楼宇门禁权限同步关闭',
      valueType: 'array',
      columns: [
        { title: '员工号', dataIndex: 'employeeCode', key: 'employeeCode' },
        { title: '姓名', dataIndex: 'employeeName', key: 'employeeName' },
        { title: '车牌号', dataIndex: 'vehicleInfo', key: 'vehicleInfo' },
      ],
      mapDataFn: async (dataArr) => {
        console.log('**********导入的数据', dataArr);
        const res: any[] = await getListByUserCode(dataArr.map((item) => item.employeeCode));
        console.log('导入的数据', res);
        return res.map((item) => {
          return {
            employeeCode: item.employeeNo,
            employeeName: item.employeeName,
            vehicleInfo: item.carNo,
          };
        });
      },
      downloadUrl: Api.downloadEmpTemplate,
    },

    dynamicRules: ({ values }) => {
      return [
        {
          required: true,
          message: '请导入人员',
          trigger: 'change',
        },
      ];
    },
  },
  {
    label: '选择厂区',
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
        mode: 'multiple',
        labelInValue: true,
      };
    },
    colProps: { span: 24 },
    required: true,
  },
  {
    label: '选择授权期限',
    field: 'useDateRange',
    component: 'RangePicker',
    slot: 'authorizeDateRangeSlot',

    componentProps: () => {
      return {
        ...useFixedStartDateRangePicker(),
      };
    },
    defaultValue: () => {
      return [dayjs().format('YYYY-MM-DD')];
    },
    dynamicRules: () => {
      return [
        {
          required: true,
          validator: (_, value) => {
            console.log('校验日期:', value);
            if (!value || !value[1]) {
              return Promise.reject('请选择结束日期');
            }
            return Promise.resolve();
          },
          trigger: ['change', 'blur'],
        },
      ];
    },
    ifShow: ({ values }) => {
      return values.powerType && values.powerType == '1';
    },
  },
  {
    label: '事由',
    field: 'applyReason',
    component: 'InputTextArea',
    colProps: { span: 24 },
    componentProps: {
      valueType: 'array',
    },
    required: true,
  },
  {
    label: '本部门科长',
    field: 'chiefEmployee',
    getLabel: true,
    component: 'PersonSelect',
    colProps: { span: 12 },
    required: true,
  },
];

// 定义普通员工表单
export const empFormSchemas: FormSchema[] = [
  {
    label: '姓名',
    field: 'createEmployeeName',
    component: 'Input',
    componentProps: {
      placeholder: '',
    },
    dynamicDisabled: ({ values }) => {
      return true;
    },
    colProps: { span: 12 },
  },
  {
    label: '员工号',
    field: 'createEmployeeNo',
    component: 'Input',
    componentProps: {
      placeholder: '',
    },
    dynamicDisabled: ({ values }) => {
      return true;
    },
    colProps: { span: 12 },
  },

  {
    label: '部门',
    field: 'createDeptName',
    component: 'Input',
    componentProps: {
      placeholder: '',
    },
    dynamicDisabled: ({ values }) => {
      return true;
    },
    colProps: { span: 24 },
  },
  {
    label: '类型',
    field: 'powerType',
    component: 'RadioGroup',
    show: false,
    defaultValue: '1',
    colProps: { span: 24 },
    rules: [{ required: true }],
  },
  {
    label: '当前授权厂区',
    field: 'curFactory',
    component: 'ShowTableOnly',
    componentProps: {
      valueType: 'array',
      columns: [
        {
          title: '厂区名称',
          dataIndex: 'factoryName',
          key: 'factoryName',
        },
        {
          title: '授权期限',
          dataIndex: 'powerBeginTime',
          key: 'powerTime',
          customRender: ({ text, record }) => {
            return formatDate(record.powerBeginTime) + ' 至 ' + formatDate(record.powerEndTime);
          },
        },
      ],
    },
    colProps: { span: 24 },
  },
  {
    label: '新增厂区大门权限',
    field: 'divisionLine',
    component: 'Divider',
    componentProps: {
      type: 'horizontal',
    },
  },
  {
    label: ' ',
    field: 'newFactoryTips',
    component: 'Input',
    slot: 'newFactoryTips',
    itemProps: {
      colon: false,
    },
  },
  {
    label: '选择厂区',
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
        mode: 'multiple',
        labelInValue: true,
      };
    },
    colProps: { span: 24 },
    required: true,
  },
  {
    field: 'useDateRange',
    label: '申请期限',
    component: 'RangePicker',
    required: true,
    defaultValue: [dayjs().format('YYYY-MM-DD')], // 默认为当前日期到一个月后
    componentProps: () => {
      return {
        ...useFixedStartDateRangePicker(),
      };
    },
    slot: 'applyDateRangeSlot',
    colProps: { span: 12 },
    dynamicRules: () => {
      return [
        {
          required: true,
          validator: (_, value) => {
            // console.log('校验日期:', value[0]);
            if (!value || !value[1]) {
              return Promise.reject('请选择结束日期');
            }
            return Promise.resolve();
          },
          trigger: ['change', 'blur'],
        },
      ];
    },
  },
  {
    label: '事由',
    field: 'applyReason',
    component: 'InputTextArea',
    colProps: { span: 24 },
    componentProps: {
      valueType: 'array',
    },
    required: true,
  },
  {
    label: '本部门科长',
    field: 'chiefEmployee',
    component: 'PersonSelect',
    colProps: { span: 12 },
    required: true,
  },
];
