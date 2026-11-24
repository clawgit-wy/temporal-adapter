
import dayjs from 'dayjs';
// 状态: status
export enum StatusEnum {
    PendingReview = '20', 
    Rejected = '10', 
    Approved = '30', 
    Unaccepted = '25', 
    cancleaccepted = '40', 

}

export const StatusEnumMap = {
    [StatusEnum.PendingReview]: '审批中',
    [StatusEnum.Rejected]: '驳回',
    [StatusEnum.Approved]: '审批通过',
    [StatusEnum.Unaccepted]: '超时未审批',
    [StatusEnum.cancleaccepted]: '已取消权限',
}
export const StatusOptions = [
    {
        label: '审批中',
        value: StatusEnum.PendingReview,
    },
    // {
    //     label: '驳回',
    //     value: StatusEnum.Rejected,
    // },
    {
        label: '审批通过',
        value: StatusEnum.Approved,
    },
    // {
    //     label: '超时未审批',
    //     value: StatusEnum.Unaccepted,
    // },
    
]

export enum entryMethodEnum {
  All = '0', 
  Face = '1', 
  ErCode = '2', 
}
export const entryMethodEnumMap = {
  [entryMethodEnum.All]: '刷脸+二维码',
  [entryMethodEnum.Face]: '仅刷脸',
  [entryMethodEnum.ErCode]: '仅二维码',
}

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

