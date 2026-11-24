import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';
import { ref } from 'vue';
import { getNewDictItems } from '/@/api/common/api';

// 是否拆除
const isRemovedList = [
  {
    label: '是',
    value: '1',
  },
  {
    label: '否',
    value: '0',
  },
];

export const columns: BasicColumn[] = [
  {
    title: '门禁名称',
    dataIndex: 'deviceName',
    width: 150,
  },
  {
    title: '是否公共区域',
    dataIndex: 'isPublic',
    width: 120,
    customRender: ({ text }) => {
      if (text === '1') {
        return '是';
      }
      if (text === '0') {
        return '否';
      }
      return '';
    },
  },
  {
    title: '保密等级',
    dataIndex: 'securityLevelName',
    width: 100,
  },
  {
    title: '绝密区负责人',
    dataIndex: 'topSecretEmpName',
    width: 120,
  },
  {
    title: '厂区',
    dataIndex: 'anFactoryName',
    width: 120,
    customRender: ({ text, record }: { text: string; record: any }) => {
      if (record.anFactoryName) {
        return render.renderTip(text);
      }
      return render.renderTip(record.factoryName);
    },
  },
  {
    title: '楼栋',
    dataIndex: 'building',
    width: 120,
    customRender: ({ text }) => {
      return render.renderTip(text);
    },
  },
  {
    title: '责任部门',
    width: 150,
    dataIndex: 'dutyDeptName',
    customRender: ({ text, record }) => {
      console.log('record', record);
      // 获取当前行的责任部门信息
      const dutyDept = record.children?.map((item: any) => item.dutyDeptName).join('/');
      return dutyDept;
    },
  },
  {
    title: '门禁管理员',
    width: 120,
    dataIndex: 'employeeName',
    customRender: ({ text, record }) => {
      // 获取当前行的责任部门对应的员工信息
      const dutyDept = record.children?.map((item: any) => item.employeeName).join('/');
      return dutyDept;
    },
  },
  {
    title: '状态',
    dataIndex: 'deviceStatus',
    width: 100,
  },
  {
    title: '是否拆除',
    dataIndex: 'removeFlag',
    width: 100,
    customRender: ({ text }) => {
      return text === '0' ? '否' : text === '1' ? '是' : '';
    },
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 150,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    label: '厂区',
    field: 'factoryCode',
    component: 'ApiSelect',
    componentProps: (params) => {
      const { formActionType } = params;
      const { setFieldsValue } = formActionType;
      return {
        api: async () => {
          const res = await getNewDictItems('factory');
          return res;
        },
        mode: 'default',
        placeholder: '请选择厂区',
        valueField: 'id',
        labelField: 'fieldName',
        onChange: () => {
          setFieldsValue({ building: '' });
        },
      };
    },
  },
  {
    label: '楼栋',
    field: 'building',
    component: 'ApiSelect',
    componentProps: (params) => {
      console.log('楼栋字段 componentProps 参数:', params);
      const { formModel } = params;
      return {
        api: async () => {
          // 如果没有选择厂区，返回空数组
          if (!formModel.factoryCode) {
            console.log('未选择厂区，楼栋返回空数组');
            return [];
          }
          const res = await getNewDictItems(formModel.factoryCode);
          console.log('楼栋数据获取成功:', res);
          return res;
        },
        mode: 'default',
        placeholder: '请选择楼栋',
        valueField: 'fieldCode',
        labelField: 'fieldName',
        disabled: !formModel.factoryCode, // 如果没有选择厂区，禁用楼栋选择
      };
    },
  },
  {
    label: '门禁',
    field: 'deviceName',
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
  },
  {
    label: '保密等级',
    field: 'securityLevel',
    component: 'ApiSelect',
    componentProps: {
      api: async () => {
        const res = await getNewDictItems('secretLevel_bmdj_0');
        return res;
      },
      mode: 'default',
      placeholder: '请选择保密等级',
      valueField: 'fieldCode',
      labelField: 'fieldName',
    },
  },
  {
    label: '是否拆除',
    field: 'removeFlag',
    component: 'Select',
    componentProps: {
      placeholder: '请选择',
      options: isRemovedList,
    },
  },
  {
    label: '状态',
    field: 'deviceStatus',
    component: 'Select',
    componentProps: {
      placeholder: '请选择',
      options: [{
          label: '在线',
          value: '0',
        },
        {
          label: '离线',
          value: '1',
        },
      ],
    },
  },
];

export const formSchemas: FormSchema[] = [
  {
    label: 'ID',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '厂区',
    field: 'anFactoryName',
    component: 'Input',
    componentProps: {
      disabled: true,
      placeholder: '',
    },
  },
  {
    label: '楼栋',
    field: 'anBuilding',
    component: 'Input',
    componentProps: {
      disabled: true,
      placeholder: '',
    },
  },
  {
    label: '门禁名称',
    field: 'deviceName',
    component: 'Input',
    componentProps: {
      disabled: true,
      placeholder: '',
    },
  },
  {
    label: '是否公共区域',
    field: 'isPublic',
    component: 'Select',
    required: true,
    componentProps: {
      placeholder: '请选择',
      options: [
        {
          label: '是',
          value: '1',
        },
        {
          label: '否',
          value: '0',
        },
      ],
    },
  },

  {
    label: '绝密区负责人',
    field: 'topSecretEmpCode',
    component: 'Input',
    slot: 'secretAreaManagerSlot',
    componentProps: {
      placeholder: '请选择绝密区负责人',
    },
    dynamicRules: ({ values }) => {
      return [
        {
          required: values.securityLevel == '937_gsjm_2',
          message: '请选择',
        },
      ];
    },
    ifShow: ({ values }) => {
      return values.securityLevel == '937_gsjm_2';
    },
  },

  {
    label: '保密等级',
    field: 'securityLevel',
    component: 'ApiSelect',
    required: true,
    componentProps: {
      api: async () => {
        const res = await getNewDictItems('secretLevel_bmdj_0');
        return res;
      },
      mode: 'default',
      placeholder: '请选择保密等级',
      valueField: 'fieldCode',
      labelField: 'fieldName',
    },
  },

  {
    label: '绝密负责人登录名',
    field: 'topSecretEmpLogin',
    component: 'Input',
    show: false,
  },
  {
    label: '绝密负责人登录名',
    field: 'topSecretEmpName',
    component: 'Input',
    show: false,
  },
];
