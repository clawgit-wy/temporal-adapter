import { h } from 'vue';
import { FormSchema } from '/@/components/Table';
import { BasicColumn } from '/@/components/Table';
import { selectLinkageList } from '/@/api/common/api';
import dayjs from 'dayjs';
import { render } from '/@/utils/common/renderUtils';
// ==================== 表单配置 ====================
//审批状态
const approvalStatusOptions = [
  {
    label: '待审批',
    value: '0',
  },
  {
    label: '审批通过',
    value: '1',
  },
  {
    label: '驳回',
    value: '2',
  },
];
// 黑名单入厂申请表单配置
export const formSchemas: FormSchema[] = [
  {
    label: '提交人信息',
    field: 'submitterInfoDivider',
    component: 'Divider',
    componentProps: {
      type: 'horizontal',
    },
  },
  {
    label: '姓名',
    field: 'createEmpName',
    component: 'Input',
    componentProps: {
      placeholder: '默认带出',
      disabled: true,
    },
    colProps: { span: 12 },
  },
  {
    label: '员工号',
    field: 'createEmpNo',
    component: 'Input',
    componentProps: {
      placeholder: '默认带出',
      disabled: true,
    },
    colProps: { span: 12 },
  },
  {
    label: 'eip登录名',
    field: 'createEmpLogin',
    component: 'Input',
    componentProps: {
      placeholder: '默认带出',
      disabled: true,
    },
    colProps: { span: 12 },
    show: false,
  },
  {
    label: '部门',
    field: 'department',
    component: 'Input',
    componentProps: {
      placeholder: '默认带出',
      disabled: true,
    },
    colProps: { span: 24 },
  },
  {
    label: '受限人员信息',
    field: 'blacklistDivider',
    component: 'Divider',
    componentProps: {
      type: 'horizontal',
    },
  },
  {
    label: '',
    field: 'detailList',
    component: 'Input',
    colProps: { span: 24 },
    slot: 'blacklistDataSlot',
    valueType: 'array',
    required: true,
    defaultValue: [],
    rules: [
      {
        required: true,
        validator: (_, value) => {
          if (!value || (Array.isArray(value) && value.length === 0)) {
            return Promise.reject('请至少添加一条受限人员信息');
          }
          return Promise.resolve();
        },
        trigger: ['blur', 'submit'],
      },
    ],
  },
  {
    label: '其他信息',
    field: 'otherInfoDivider',
    component: 'Divider',
    componentProps: {
      type: 'horizontal',
    },
  },
  {
    label: '本部门部门长',
    field: 'departmentHead',
    component: 'PersonSelect',
    colProps: { span: 24 },
    required: true,
    ifShow: ({ values }) => {
      console.log("黑名单表单==",values);
      return values.detailList.length>0 && values.detailList.some(item=>item.blankType == 0)
    },
  },
  {
    label: '进厂原因',
    field: 'reason',
    component: 'InputTextArea',
    colProps: { span: 24 },
    componentProps: {
      placeholder: '请输入，最多200字',
      maxlength: 200,
      rows: 4,
    },
    required: true,
  },
  {
    label: ' ',
    field: 'tips',
    component: 'Input',
    slot: 'tipsSlot',
    itemProps: {
      colon: false,
    },
    colProps: { span: 24 },
  },
];

// ==================== 表格列配置 ====================

// 黑名单人员表格列配置
export const columns: BasicColumn[] = (detailInfo: any) => {
  return [
    {
      title: '序号',
      dataIndex: 'index',
      width: 60,
      customRender: ({ index }) => {
        return index + 1;
      },
    },
    {
      title: '姓名',
      dataIndex: 'name',
      width: 100,
    },
    {
      title: '禁止入厂原因',
      dataIndex: 'blankType',
      width: 120,
      customRender: ({ text, record }) => {
        return String(text)=='1'?'集团非友商客户':'已离职人员';
      },
      
    },
    {
      title: '公司名称',
      dataIndex: 'companyName',
      width: 200,
    },
    {
      title: '离职前任职部门',
      dataIndex: 'orgNamePath',
      width: 160,
      resizable:true,
      customRender: ({ text, record }) => {
        return text?text:"/";
      },
    },
    {
      title: '离职前人员类型',
      dataIndex: 'positionType',
      width: 120,
      customRender: ({ text, record }) => {
        return text?text:"/";
      },
    },
    {
      title: '备案时间',
      dataIndex: 'filingTime',
      width: 160,
      customRender: ({ text, record }) => {
        return `${record.enterStartTime} - ${record.enterEndTime}`;
      },
    },
    {
      title: '备案厂区',
      dataIndex: 'enterFactory',
      width: 200,
    },
    {
      title: '备案类型',
      dataIndex: 'recordType',
      width: 120,
      customRender:({ text }) => {
            return text?text:"/";
      },
    },
    {
      title: '身份证号',
      dataIndex: 'cardNo',
      width: 180,
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      width: 130,
    },
    {
      title: '照片',
      dataIndex: 'photoUrl',
      width: 80,
      customRender: ({ text }) => {
        if (!text) {
          return text;
        } else {
          return render.renderImage2({ text });
        }
      },
    },
    {
      title: '是否夜间作业',
      dataIndex: 'nightFlag',
      width: 80,
      customRender: ({ text }) => {
        // console.log('是否夜间作业', text);

        return text == '1' ? '是' : '否';
      },
    },
    {
      title: '审批状态',
      dataIndex: 'applyStatus',
      width: 80,
      fixed: 'right',
      customRender: ({ text, record }) => {
        console.log('审批状态', record);
        return render.renderDictNative(text, approvalStatusOptions);
      },
      ifShow: () => {
        return detailInfo?.status == '30';
      },
    },
  ];
};
// 准入厂区选项（将从接口动态获取）
const enterFactoryOptions: any[] = [];
// 表单配置（合并后的单一表单）
export const addFormSchemas: FormSchema[] = [
  // ==================== 对接人信息 ====================
  // {
  //   field: 'divider1',
  //   component: 'Divider',
  //   label: '对接人信息',
  //   componentProps: {
  //     orientation: 'left',
  //     style: {
  //       color: '#1890ff',
  //       fontWeight: 500,
  //       fontSize: '16px',
  //     },
  //   },
  //   colProps: { span: 24 },
  // },
  // {
  //   field: 'buttJointCode',
  //   label: '员工号',
  //   component: 'Input',
  //   required: true,
  //   componentProps: {
  //     placeholder: '请输入',
  //     disabled: true,
  //   },
  //   colProps: { span: 24 },
  // },
  // {
  //   field: 'buttJointName',
  //   label: '姓名',
  //   component: 'Input',
  //   required: true,
  //   componentProps: {
  //     placeholder: '请输入',
  //     disabled: true,
  //   },
  //   colProps: { span: 24 },
  // },
  // {
  //   field: 'buttJointPhone',
  //   label: '对接人手机号',
  //   component: 'Input',
  //   required: true,
  //   componentProps: {
  //     placeholder: '请输入',
  //     disabled: true,
  //   },
  //   colProps: { span: 24 },
  //   show: false,
  // },
  // // ==================== 备案人信息 ====================
  // {
  //   field: 'divider2',
  //   component: 'Divider',
  //   label: '备案人信息',
  //   componentProps: {
  //     orientation: 'left',
  //     style: {
  //       color: '#1890ff',
  //       fontWeight: 500,
  //       fontSize: '16px',
  //     },
  //   },
  //   colProps: { span: 24 },
  // },
  {
    field: 'name',
    label: '姓名',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      maxlength: 10,
      showCount: true,
    },
    colProps: { span: 24 },
    rules: [
      { required: true, message: '请输入姓名', trigger: 'blur' },
      { pattern: /^[\u4e00-\u9fa5]+$/, message: '姓名只能输入中文', trigger: 'blur' },
    ],
  },
  {
    field: 'companyName',
    label: '公司名称',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      // disabled: true,
    },
    colProps: { span: 24 },
  },
  {
    field: 'companyCode',
    label: '公司名称',
    component: 'Input',
    required: false,
    componentProps: {
      placeholder: '请输入',
    },
    colProps: { span: 24 },
    show: false,
  },
  {
    field: 'cardNo',
    label: '身份证号',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      maxlength: 18,
    },
    colProps: { span: 24 },
  },
  {
    field: 'phone',
    label: '手机号',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入',
      maxlength: 11,
    },
    colProps: { span: 24 },
  },
  
  {
    field: 'recordTypeCode',
    label: '备案类型',
    component: 'Select',
    componentProps: {
      placeholder: '请选择对应类型',
    },
    colProps: { span: 24 },
    rules: [{ required: false, message: '请选择类型', trigger: 'change' }],
    itemProps: {
      extra: h('span', { style: { color: '#e34d59' } }, '若受限人员后续通过访客/物流车入厂，备案类型不选。'),
    },
  },

  
  {
    field: 'enterFactoryCode',
    label: '准入厂区',
    component: 'JSelectMultiple',
    componentProps: {
      placeholder: '请选择',
      options: enterFactoryOptions,
    },
    colProps: { span: 24 },
    rules: [{ required: true, message: '请选择厂区', trigger: 'change' }],
  },
  {
    field: 'enterTimeRange',
    label: '准入时间',
    component: 'RangePicker',
    required: true,
    componentProps: {
      showTime: { format: 'HH:mm' ,hideDisabledOptions: true,},
      format: 'YYYY-MM-DD HH:mm',
      valueFormat: 'YYYY-MM-DD HH:mm:ss', // 设置值的格式，返回格式化后的字符串数组
      valueType: 'array', // 保持数组格式，不转换为逗号分隔的字符串
      placeholder: ['开始时间', '结束时间'],
      style: { width: '100%' },
      // ...createEnterTimeRangeConfig(7), // 默认7天，会被 ApplicationModal 动态更新
    },
    slot:"enterTimeRange",
    colProps: { span: 24 },
    rules: [{ required: true, message: '请选择准入时间', trigger: 'change' }],
    // defaultValue: ['2025-10-24 00:00:00', '2025-10-31 23:59:59'],
  },
  {
    field: 'nightFlag',
    label: '是否夜间作业',
    component: 'Select',
    required: true,
    defaultValue: 0,
    componentProps: {
      options: [
        { label: '否', value: 0 },
        { label: '是', value: 1 },
      ],
      placeholder: '请选择',
    },
    colProps: { span: 24 },
  },
  
];

/**
 * 创建准入时间范围选择器配置
 * @param limitDay 限制天数，默认7天 
 */
export const createEnterTimeRangeConfig = (limitDay: number = 7) => {
  // 允许的分钟值
  const allowedMinutes = [0, 30, 59];

  // 使用闭包变量存储开始时间，用于结束时间联动
  let startDateValue: any = null;


  return {
    
    // 日期禁用逻辑
    disabledDate: (current, info) => {
      if (!current) return false;
      const now = dayjs();
      const limitDaysLater = now.add(limitDay, 'day');

      // 获取 from 参数（Ant Design 在 RangePicker 中提供）
      const from = info?.from;

      // 如果是结束时间选择，且已选开始时间，则限制不能早于开始时间
      if (from) {
        // 确保存储的是 dayjs 对象
        startDateValue = dayjs(from);
        return current.isBefore(from, 'day') || 
               current.isBefore(now, 'day') || 
               current.isAfter(limitDaysLater, 'day');
      }

      // 开始时间选择：限制在 now ~ limitDaysLater 范围内
      return current.isBefore(now, 'day') || current.isAfter(limitDaysLater, 'day');
    },

    // 时间禁用逻辑 - 注意：只有两个参数 (current, type)
    disabledTime: (current, type) => {
      const now = dayjs();
      const limitDaysLater = now.add(limitDay, 'day');
      const targetDate = current || now;

      // 如果是开始时间选择，记录当前值（确保是 dayjs 对象）
      if (type === 'start' && current) {
        startDateValue = dayjs(current);
      }

      // ========== 开始时间选择逻辑 ==========
      if (type === 'start') {
        // 如果是今天，禁用当前时间之前的时分
        if (targetDate.isSame(now, 'day')) {
          const currentHour = now.hour();
          const currentMinute = now.minute();

          return {
            disabledHours: () => Array.from({ length: currentHour }, (_, i) => i),
            disabledMinutes: (selectedHour) => {
              const allMinutes = Array.from({ length: 60 }, (_, i) => i);

              if (selectedHour === currentHour) {
                return allMinutes.filter(m => m < currentMinute || !allowedMinutes.includes(m));
              }
              if (selectedHour < currentHour) {
                return allMinutes;
              }
              return allMinutes.filter(m => !allowedMinutes.includes(m));
            },
          };
        }

        // 如果是限制天数后的那一天
        if (targetDate.isSame(limitDaysLater, 'day')) {
          const limitHour = limitDaysLater.hour();
          const limitMinute = limitDaysLater.minute();

          return {
            disabledHours: () => Array.from({ length: 24 - limitHour - 1 }, (_, i) => limitHour + 1 + i),
            disabledMinutes: (selectedHour) => {
              const allMinutes = Array.from({ length: 60 }, (_, i) => i);

              if (selectedHour === limitHour) {
                return allMinutes.filter(m => m > limitMinute || !allowedMinutes.includes(m));
              }
              if (selectedHour > limitHour) {
                return allMinutes;
              }
              return allMinutes.filter(m => !allowedMinutes.includes(m));
            },
          };
        }

        // 其他日期：只保留 00, 30, 59 分钟
        return {
          disabledMinutes: () => {
            const allMinutes = Array.from({ length: 60 }, (_, i) => i);
            return allMinutes.filter(m => !allowedMinutes.includes(m));
          },
        };
      }

      // ========== 结束时间选择逻辑 ==========
      if (type === 'end') {
        // 如果没有开始时间，按默认限制处理（最晚 23:59）
        if (!startDateValue) {
          return {
            disabledMinutes: () => {
              const allMinutes = Array.from({ length: 60 }, (_, i) => i);
              return allMinutes.filter(m => !allowedMinutes.includes(m));
            },
          };
        }

        // 确保 startDateValue 是 dayjs 对象
        const startDate = dayjs(startDateValue);

        // 如果结束日期与开始日期同一天，限制不能早于开始时间
        if (targetDate.isSame(startDate, 'day')) {
          const startHour = startDate.hour();
          const startMinute = startDate.minute();

          return {
            disabledHours: () => Array.from({ length: startHour }, (_, i) => i),
            disabledMinutes: (selectedHour) => {
              const allMinutes = Array.from({ length: 60 }, (_, i) => i);

              if (selectedHour === startHour) {
                // 禁用开始分钟之前的，且只保留允许的分钟
                return allMinutes.filter(m => m < startMinute || !allowedMinutes.includes(m));
              }
              if (selectedHour < startHour) {
                return allMinutes;
              }
              // 大于开始小时：只禁用不在允许列表中的分钟
              return allMinutes.filter(m => !allowedMinutes.includes(m));
            },
          };
        }

        // 如果是限制天数后的最后一天，限制不能超过限制时间
        if (targetDate.isSame(limitDaysLater, 'day')) {
          const limitHour = limitDaysLater.hour();
          const limitMinute = limitDaysLater.minute();

          return {
            disabledHours: () => Array.from({ length: 24 - limitHour - 1 }, (_, i) => limitHour + 1 + i),
            disabledMinutes: (selectedHour) => {
              const allMinutes = Array.from({ length: 60 }, (_, i) => i);

              if (selectedHour === limitHour) {
                return allMinutes.filter(m =>m>startDate.minute() ||!allowedMinutes.includes(m));
              }
              if (selectedHour > limitHour) {
                return allMinutes;
              }
              return allMinutes.filter(m => !allowedMinutes.includes(m));
            },
          };
        }

        // 其他日期：只保留 00, 30, 59 分钟，无额外限制
        return {
          disabledMinutes: () => {
            const allMinutes = Array.from({ length: 60 }, (_, i) => i);
            return allMinutes.filter(m => !allowedMinutes.includes(m));
          },
        };
      }

      return {};
    },

    // 当结束时间超出限制时，与开始时间一致
    onCalendarChange: (dates) => {
      if (dates && dates[0] && dates[1]) {
        const start = dayjs(dates[0]);
        const end = dayjs(dates[1]);
        const limitDaysLater = start.add(limitDay, 'day');

        // 更新开始时间记录（确保是 dayjs 对象）
        startDateValue = dayjs(dates[0]);

        // 如果结束时间超过限制，重置为开始时间
        if (end.isAfter(limitDaysLater)) {
          return [dates[0], dates[0]];
        }
      } else if (dates && dates[0]) {
        console.log("kaishi日期变化==",dates)
        // 只选了开始时间，更新记录
        startDateValue = dayjs(dates[0]);
        const start = dayjs(dates[0]);
        const limitDaysLater = start.add(limitDay, 'day');
       return [dates[0] , limitDaysLater.hour(23).minute(59)]
      }
      return dates;
    },

    // 面板关闭时重置
    onOpenChange: (open) => {
      if (!open) {
        startDateValue = null;
      }
    },
    
  };
};
/**
 * 创建准入时间范围选择器配置
 * @param limitDay 限制天数，默认7天
 */
export const createRangeEnterTimeRangeConfig = (maxDateTime) => {
    return {
      disabledDate: (current) => {
        if (!current) return false;
        const now = dayjs();
        // const limitDaysLater = now.add(limitDay, 'day');
        return current.isBefore(now, 'day') || current.isAfter(dayjs(maxDateTime), 'day');
      },
      disabledTime: (current, type) => {
        const now = dayjs();
        const limitDaysLater = dayjs(maxDateTime);
  
        // 如果没有选择日期，默认按今天处理
        const targetDate = current || now;
  
        // 如果是今天，禁用当前时间之前的时分
        if (targetDate.isSame(now, 'day')) {
          const currentHour = now.hour();
          const currentMinute = now.minute();
  
          return {
            disabledHours: () => {
              return Array.from({ length: currentHour }, (_, i) => i);
            },
            disabledMinutes: (selectedHour) => {
              if (selectedHour === currentHour) {
                return Array.from({ length: currentMinute + 1 }, (_, i) => i);
              }
              if (selectedHour < currentHour) {
                return Array.from({ length: 60 }, (_, i) => i);
              }
              return [];
            },
          };
        }
  
        // 如果是限制天数后的那一天，禁用超过限制时间的时分
        if (targetDate.isSame(limitDaysLater, 'day')) {
          const limitHour = limitDaysLater.hour();
          const limitMinute = limitDaysLater.minute();
  
          return {
            disabledHours: () => {
              return Array.from({ length: 24 - limitHour - 1 }, (_, i) => limitHour + 1 + i);
            },
            disabledMinutes: (selectedHour) => {
              if (selectedHour === limitHour) {
                return Array.from({ length: 60 - limitMinute }, (_, i) => limitMinute + i);
              }
              if (selectedHour > limitHour) {
                return Array.from({ length: 60 }, (_, i) => i);
              }
              return [];
            },
          };
        }
  
        return {};
      },
    };
  };