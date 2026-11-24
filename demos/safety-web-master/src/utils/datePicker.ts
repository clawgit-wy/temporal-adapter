import dayjs from 'dayjs';
import { ref } from 'vue';
import { FormSchema } from '/@/components/Table';

/**
 * 创建日期范围选择器的配置，支持限制最大选择范围
 * @param maxDays 最大可选天数范围
 * @param showRanges 是否显示预设范围选项
 * @param format 日期格式
 * @returns RangePicker组件所需的配置对象
 */
export function useRangePickerWithLimit(maxDays = 30, showRanges = false, format = 'YYYY-MM-DD') {
  // 创建局部状态来跟踪日期选择
  const dates = ref<any[]>([]);
  
  // 预设时间范围选项
  const rangeOptions = showRanges ? {
    '今天': [dayjs(), dayjs()],
    '最近3天': [dayjs().subtract(2, 'days'), dayjs()],
    '近一周': [dayjs().subtract(6, 'days'), dayjs()],
    '近一月': [dayjs().subtract(29, 'days'), dayjs()],
  } : undefined;
  
  return {
    format,
    valueFormat: format,
    placeholder: ['开始日期', '结束日期'],
    ranges: rangeOptions,
    
    // 限制日期范围
    disabledDate: (current) => {
      if (!dates.value || dates.value.length === 0) {
        return false;
      }
      
      // 安全地处理日期比较，确保使用dayjs对象
      let tooLate = false;
      let tooEarly = false;
      
      // 检查第一个日期并计算差值
      if (dates.value[0]) {
        // 确保日期是 dayjs 对象
        const startDate = dayjs(dates.value[0]);
        tooLate = current.diff(startDate, 'days') > (maxDays - 1);
      }
      
      // 检查第二个日期并计算差值
      if (dates.value[1]) {
        // 确保日期是 dayjs 对象
        const endDate = dayjs(dates.value[1]);
        tooEarly = endDate.diff(current, 'days') > (maxDays - 1);
      }
      
      return tooEarly || tooLate;
    },
    
    // 在日历面板日期变化时记录选择的日期
    onCalendarChange: (val) => {
      dates.value = val;
    },
    
    // 在打开关闭面板时处理
    onOpenChange: (open) => {
      if (open) {
        // 打开面板时重置记录的日期
        dates.value = [];
      }
    },
  };
}

/**
 * 创建使用日期范围的表单字段配置
 * @param fieldName 字段名称
 * @param maxDays 最大可选天数
 * @param isRequired 是否必填
 * @returns 表单字段配置对象
 */
export function createRangePickerField(fieldName: string, maxDays = 30, isRequired = false): Partial<FormSchema> {
  return {
    field: fieldName,
    component: 'RangePicker',
    componentProps: () => useRangePickerWithLimit(maxDays),
    rules: isRequired ? [{ required: true, message: '请选择日期范围' }] : undefined,
  };
}

/**
 * 创建开始日期固定为今天且不可编辑的日期范围选择器
 * @param format 日期格式
 * @returns RangePicker组件所需的配置对象
 */
export function useFixedStartDateRangePicker(format = 'YYYY-MM-DD') {
  // 获取今天的日期
  const today = dayjs();
  const formattedToday = today.format(format);
  
  return {
    format,
    valueFormat: format,
    placeholder: ['开始日期', '结束日期'],
    defaultValue: [formattedToday, null],
    
    // 禁用今天之前的日期
    disabledDate: (current) => {
      return current && current < dayjs().startOf('day');
    },
    
    // 禁用第一个输入框，第二个可用
    disabled: [true, false],
  };
}

/**
 * 创建开始日期固定为今天的表单字段配置
 * @param fieldName 字段名称
 * @param isRequired 是否必填
 * @returns 表单字段配置对象
 */
export function createFixedStartDateField(fieldName: string, isRequired = true): Partial<FormSchema> {
  return {
    field: fieldName,
    component: 'RangePicker',
    componentProps: () => useFixedStartDateRangePicker(),
    defaultValue: () => [dayjs().format('YYYY-MM-DD'), null],
    rules: isRequired ? [{ required: true, message: '请选择结束日期' }] : undefined,
  };
} 
