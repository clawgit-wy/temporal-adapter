<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm">
      <template #enterTimeRange="{ model, field, schema }">
        <a-range-picker
          v-model:value="model[field]"
          :disabled-date="disabledDate"
          :disabled-time="disabledRangeTime"
          :show-time="showTimeConfig"
          format="YYYY-MM-DD HH:mm"
          @calendarChange="onCalendarChange"
          @change="onRangeChange"
        />
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup name="ApplicationModal">
  import { ref, unref, computed } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { addFormSchemas, createEnterTimeRangeConfig, createRangeEnterTimeRangeConfig } from './form.data.ts';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { saveOrUpdate, checkAuthEmpInfo, ftList } from './blacklist.api';
  import dayjs, { Dayjs } from 'dayjs';
  import { useUserStore } from '/@/store/modules/user';
  const userInfo = useUserStore().getUserInfo;
  const { createMessage } = useMessage();

  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  const id = ref<string>('');

  // 注册表单
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, updateSchema }] = useForm({
    schemas: addFormSchemas,
    showActionButtonGroup: false,
    labelWidth: 120,
    validateTrigger: 'blur', // 设置为失去焦点时才触发校验
    fieldMapToTime: [['enterTimeRange', ['enterStartTime', 'enterEndTime'], 'YYYY-MM-DD HH:mm']],
  });

  /**
   * 生成选项列表
   * @param data 对象数据 { code: name }
   * @returns 选项数组
   */
  const generateOptions = (data: Record<string, string> = {}) => {
    return Object.entries(data).map(([code, name]) => ({
      label: name,
      value: code,
    }));
  };

  /**
   * 创建日期禁用函数
   * @param limitDay 可备案天数
   */
  const createDisabledDate = (limitDay: number) => {
    const today = dayjs().startOf('day');
    const maxDate = today.add(limitDay, 'day');

    return (current: Dayjs) => {
      if (!current) return false;
      const currentDay = dayjs(current).startOf('day');
      return currentDay.isBefore(today) || currentDay.isAfter(maxDate);
    };
  };

  /**
   * 创建日期变更处理函数
   * @param limitDay 可备案天数
   */
  const createDateChangeHandler = (limitDay: number) => {
    return (value: Dayjs) => {
      if (value) {
        const endDate = dayjs(value).add(limitDay, 'day');
        setFieldsValue({ enterEndTime: endDate.format('YYYY-MM-DD HH:mm:ss') });
      }
    };
  };

  /**
   * 更新表单配置
   * @param recordTypeOptions 类型选项
   * @param enterFactoryOptions 厂区选项
   * @param limitDay 可备案天数
   */
  const updateFormSchemas = (recordTypeOptions: any[], enterFactoryOptions: any[], limitDay: number) => {
    const schemaUpdaters: Record<string, (schema: any) => any> = {
      recordTypeCode: (schema) => ({
        ...schema,
        componentProps: {
          ...schema.componentProps,
          options: recordTypeOptions,
        },
      }),
      enterFactoryCode: (schema) => ({
        ...schema,
        componentProps: {
          ...schema.componentProps,
          options: enterFactoryOptions,
        },
      }),
      // enterTimeRange: (schema) => ({
      //   ...schema,
      //   componentProps: {
      //     ...schema.componentProps,
      //     ...createEnterTimeRangeConfig(limitDay), // 动态更新时间限制
      //   },
      // }),
    };

    setProps({
      schemas: addFormSchemas.map((schema) => {
        const updater = schemaUpdaters[schema.field];
        return updater ? updater(schema) : schema;
      }),
    });
  };

  /**
   * 加载对接人信息并初始化表单
   */
  const loadButtJointInfo = async () => {
    try {
      console.log('用户信息==', userInfo);
      // 准备自动填充的值
      const autoFillValues: Record<string, any> = {
        buttJointCode: userInfo.workNo,
        buttJointName: userInfo.realName,
        buttJointPhone: userInfo.phone,
      };

      const ftListData = await ftList({});
      // if (!buttJointData) {
      //   throw new Error('获取对接人信息为空');
      // }

      // 生成选项列表
      // const limitDay = Number(buttJointData.fdLimitDay) || 0;
      const enterFactoryOptions = ftListData?.p4?.map((item, index) => {
        return {
          label: item.itemName,
          value: item.itemCode,
        };
      });
      const recordTypeOptions = ftListData?.p2?.map((item, index) => {
        return {
          label: item.itemName,
          value: item.itemCode,
        };
      });

      // // 如果类型只有一个选项，自动选中
      // if (recordTypeOptions.length === 1) {
      //   autoFillValues.recordTypeCode = recordTypeOptions[0].value;
      // }

      // 如果准入厂区只有一个选项，自动选中
      if (enterFactoryOptions.length === 1) {
        autoFillValues.enterFactoryCode = enterFactoryOptions[0].value;
      }
      // 设置对接人基本信息和自动选中的值
      await setFieldsValue(autoFillValues);
      // 更新表单配置
      updateFormSchemas(recordTypeOptions, enterFactoryOptions);
    } catch (error) {
      console.error('获取对接人信息失败:', error);
      // createMessage.error('获取对接人信息失败，请重试');
      throw error;
    }
  };

  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({
      confirmLoading: false,
      showCancelBtn: !!data?.showFooter,
      showOkBtn: !!data?.showFooter,
    });

    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    if (isUpdate.value) {
      id.value = unref(data.record.id);
    }

    // 重置表单
    await resetFields();

    // 如果是新增，加载对接人信息
    if (!data.isUpdate) {
      await loadButtJointInfo();
    }

    // 设置表单值（编辑时）
    if (data.record) {
      const factoryNameList = data.record.enterFactory?.split(',');
      const factoryCodeList = data.record.enterFactoryCode.split(',');
      const factoryOptions = factoryNameList?.map((item, index) => {
        return {
          label: item,
          value: factoryCodeList[index],
        };
      });
      const recordTypeOptions = [{ label: data.record.recordType, value: data.record.recordTypeCode }];
      updateSchema([
        {
          field: 'recordTypeCode',
          componentProps: {
            options: recordTypeOptions,
          },
        },
        {
          field: 'enterFactoryCode',
          componentProps: {
            options: factoryOptions,
          },
        },
        {
          field: 'enterTimeRange',
          componentProps: {
            ...createRangeEnterTimeRangeConfig(data.enterEndTime),
          },
        },
      ]);
      await setFieldsValue({
        ...data.record,
        enterTimeRange: [dayjs(data.record.enterStartTime).format('YYYY-MM-DD HH:mm'), dayjs(data.record.enterEndTime).format('YYYY-MM-DD HH:mm')],
      });
    }

    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  //表单提交事件
  async function onSubmit() {
    try {
      // 验证表单
      const values = await validate();
      console.log('表单验证===', values);
      setModalProps({ confirmLoading: true });
      if (isUpdate.value) {
        values.id = id.value;
      }
      // 调用接口保存数据
      const res = await saveOrUpdate({ ...values });
      console.log('添加人员==', res);
      if (!res) {
        createMessage.error(res || '操作失败');
      } else {
        createMessage.success('添加成功');
        //关闭弹窗
        closeModal();
        //刷新列表
        emit('success', res);
      }
      // 显示成功消息
    } catch (error) {
      console.error('提交失败:', error);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }

  // ============ 响应式数据 ============
  const startDate = ref(null);
  const maxRangeDays = ref(7); // 默认7天，从接口获取后更新

  // 可用的分钟选项
  const AVAILABLE_MINUTES = [0, 30, 59];

  // ============ 工具函数 ============

  // 获取默认分钟
  const getDefaultMinute = (currentHour) => {
    const now = dayjs();
    if (currentHour > now.hour()) return 0;

    for (let minute of AVAILABLE_MINUTES) {
      if (minute > now.minute()) return minute;
    }
    return 59;
  };

  // ============ 修复：disabledDate ============
  const disabledDate = (current, info) => {
    if (!current) return false;
    const now = dayjs();
    const limitDaysLater = now.add(maxRangeDays.value, 'day');

    // 获取 from 参数（Ant Design 在 RangePicker 中提供）
    const from = info?.from;

    // 如果是结束时间选择，且已选开始时间，则限制不能早于开始时间
    if (from) {
      // 确保存储的是 dayjs 对象
      startDateValue = dayjs(from);
      return current.isBefore(from, 'day') || current.isBefore(now, 'day') || current.isAfter(limitDaysLater, 'day');
    }

    // 开始时间选择：限制在 now ~ limitDaysLater 范围内
    return current.isBefore(now, 'day') || current.isAfter(limitDaysLater, 'day');
  };

  // ============ 修复：disabledRangeTime ============
  const disabledRangeTime = (date, type, info) => {
    // 防御性检查
    // if (!info) return {};
    const now = dayjs();
    if (type === 'start') {
      const currentHour = now.hour();
      const currentMinute = now.minute();
      return {
        // disabledMinutes: () => {
        //   const allMinutes = Array.from({ length: 60 }, (_, i) => i);
        //   return allMinutes.filter((m) => !AVAILABLE_MINUTES.includes(m));
        // },
        disabledMinutes: (selectedHour) => {
          const allMinutes = Array.from({ length: 60 }, (_, i) => i);
          if (selectedHour === currentHour) {
            return allMinutes.filter((m) => m < currentMinute || !AVAILABLE_MINUTES.includes(m));
          }
          if (selectedHour < currentHour) {
            return allMinutes;
          }
          return allMinutes.filter((m) => !AVAILABLE_MINUTES.includes(m));
        },
      };
    }

    if (type === 'end' && startDate.value) {
      const start = dayjs(startDate.value);
      const end = dayjs(date);
      const diffDays = Math.abs(end.diff(start, 'day'));

      // 超出限制时，结束时间时分与开始时间一致
      if (diffDays >= maxRangeDays.value) {
        const startHour = start.hour();
        const startMinute = start.minute();

        return {
          disabledHours: () => Array.from({ length: 24 }, (_, i) => i).filter((h) => h !== startHour),
          disabledMinutes: () => Array.from({ length: 60 }, (_, i) => i).filter((m) => m !== startMinute),
        };
      }

      return {
        disabledMinutes: () => {
          const allMinutes = Array.from({ length: 60 }, (_, i) => i);
          return allMinutes.filter((m) => !AVAILABLE_MINUTES.includes(m));
        },
      };
    }

    return {};
  };

  const onCalendarChange = (dates) => {
    if (dates?.[0]) {
      startDate.value = dates[0];
    }
  };

  const onRangeChange = (dates) => {
    if (!dates || dates.length === 0) {
      startDate.value = null;
    }
  };

  const showTimeConfig = computed(() => {
    const now = dayjs();
    const defaultMinute = getDefaultMinute(now.hour());

    return {
      format: 'HH:mm',
      defaultValue: [now.minute(defaultMinute), dayjs().hour(23).minute(59)],
      hideDisabledOptions: true,
    };
  });
</script>
