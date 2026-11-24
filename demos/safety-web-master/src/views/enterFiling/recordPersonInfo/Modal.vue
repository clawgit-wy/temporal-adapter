<template>
  <div ref="refModal">
    <BasicModal @register="registerModal" :title="title" :width="600" v-bind="$attrs" @ok="onSubmit(30)" :getContainer="() => $refs.refModal">
      <BasicForm @register="registerForm">
        <template #secondTitle="{ model, field }" v-if="title != '注销'">
          <span>{{ secondTitle }}</span>
        </template>
      </BasicForm>
      <template #centerFooter>
        <a-button type="primary" danger class="custom-button" v-if="title == '审核' || title == '批量审核'" @click="onSubmit(10)">废弃</a-button>
      </template>
    </BasicModal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { editSchema, deleteFormSchema } from './data';
  import { fieldChildTree, checkAuthEmpInfo, batchUpdate, batchAudit, batchExtension, deleteItem } from './api.ts';
  import { createEnterTimeRangeConfig, createRangeEnterTimeRangeConfig } from './const';
  import { useUserStore } from '/@/store/modules/user';
  import dayjs from 'dayjs';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();
  const userInfo = useUserStore().getUserInfo;
  const range = (start, end) => {
    const result = [];
    for (let i = start; i < end; i++) {
      result.push(i);
    }
    return result;
  };
  const timeInt = (time) => {
    let timeNum = 0;
    try {
      timeNum = parseInt(time, 10);
    } catch (e) {
      throw new Error('小时或分钟不是有效数字!');
    }
    return timeNum;
  };
  const props = defineProps<{
    selectKeys: number[];
    selectRow: [];
    query: object | null;
    updateType: number; // 1 查询修改 2 选择修改
  }>();

  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const secondTitle = ref<string>('');
  const modelType = ref<string>('');
  const isUpdate = ref<boolean>(false);
  const id = ref<string>('');
  // 注册 form
  const [registerForm, { resetFields, setFieldsValue, resetSchema, validate, setProps, updateSchema }] = useForm({
    schemas: [...editSchema, ...deleteFormSchema],
    showActionButtonGroup: false,
    fieldMapToTime: [['enterTime', ['enterStartTime', 'enterEndTime'], 'YYYY-MM-DD HH:mm']],
  });
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter, okText: '确定', minHeight: 400 });
    isUpdate.value = unref(data.isUpdate);
    id.value = unref(data.record.id);
    await resetSchema([...editSchema, ...deleteFormSchema]);
    let titleStr = '';
    await resetFields();
    modelType.value = data.type;
    if (data.type == 'singleReview') {
      titleStr = '审核';
      secondTitle.value = '对当前未审批的入厂申请进行审核';
      setModalProps({ okText: '审核通过' });
      await resetSchema(editSchema);
      setFiledData(data.record, 'normal');
    } else if (data.type == 'singleEdit') {
      titleStr = '编辑';
      secondTitle.value = '对当前审批通过的入厂申请进行编辑';
      await resetSchema(editSchema);
      setFiledData(data.record, 'normal');
    } else if (data.type == 'singleRenewal') {
      titleStr = '续期';
      secondTitle.value = '对当前审批通过的入厂申请进行续期';
      await resetSchema(editSchema);
      setFiledData(data.record, 'renewal');
    } else if (data.type == 'singleDelete') {
      titleStr = '注销';
      setModalProps({ minHeight: 100 });
      await resetSchema(deleteFormSchema);
    } else if (data.type == 'edit') {
      titleStr = '批量编辑';
      secondTitle.value = `对选中的${props.selectKeys.length}个审批通过的入厂申请进行批量编辑`;
      await resetSchema(editSchema);
      setFiledData(data.record, 'normal');
    } else if (data.type == 'review') {
      titleStr = '批量审核';
      secondTitle.value = `对选中的${props.selectKeys.length}个未审批的入厂申请进行批量审核`;
      setModalProps({ okText: '审核通过' });
      await resetSchema(editSchema);
      setFiledData(data.record, 'normal');
    } else {
      titleStr = '批量续期';
      secondTitle.value = `对选中的${props.selectKeys.length}个审批通过的入厂申请进行批量续期`;
      await resetSchema(editSchema);
      setFiledData(data.record, 'renewal');
    }
    title.value = unref(titleStr);
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  const setFiledData = async (data, type) => {
    if (type == 'renewal') {
      const res = await checkAuthEmpInfo({ empNo: userInfo.workNo, phone: userInfo.phone });
      console.log('登录人授权信息==', res);
      if (res) {
        const factoryOptions = res.factoryList.map((item, index) => {
          return {
            label: item.factory,
            value: item.factoryCode,
          };
        });
        const limitDay = Number(res.fdLimitDay);
        updateSchema([
          {
            field: 'enterFactoryCode',
            componentProps: {
              options: factoryOptions,
            },
          },
          {
            field: 'enterTime',
            componentProps: {
              ...createEnterTimeRangeConfig(limitDay),
            },
          },
        ]);
      }
    } else {
      const factoryNameList = data.enterFactory?.split(',');
      const factoryCodeList = data.enterFactoryCode.split(',');
      const factoryOptions = factoryNameList?.map((item, index) => {
        return {
          label: item,
          value: factoryCodeList[index],
        };
      });
      const minDate = data.enterStartTime.split(' ');
      const maxDate = data.enterEndTime.split(' ');
      const minTime = data.enterStartTime.split(' ').length <= 1 ? '00:00' : data.enterStartTime.split(' ')[1];
      const maxTime = data.enterEndTime.split(' ').length <= 1 ? '23:59' : data.enterEndTime.split(' ')[1];
      updateSchema([
        {
          field: 'enterFactoryCode',
          componentProps: {
            options: factoryOptions,
          },
        },
        {
          field: 'enterTime',
          componentProps: {
            // disabledDate: (current) => {
            //   return current < dayjs(minDate).startOf('day') || current > dayjs(maxDate).endOf('day');
            // },
            // disabledTime: (curemt) => {
            //   return {
            //     disabledHours: () => range(0, timeInt(minTime.split(':')[0])) || range(timeInt(maxTime.split(':')[0]), 24),
            //     disabledMinutes: () => range(0, timeInt(minTime.split(':')[1])) || range(timeInt(maxTime.split(':')[1]), 60),
            //   };
            // },
            ...createRangeEnterTimeRangeConfig(data.enterEndTime),
          },
        },
      ]);
      await setFieldsValue({
        enterFactoryCode: data.enterFactoryCode,
        enterTime: [data.enterStartTime, data.enterEndTime],
      });
    }
  };

  //表单提交事件
  async function onSubmit(status) {
    try {
      const values = await validate();
      console.log('提交表单===', values);
      setModalProps({ confirmLoading: true });
      let res = {};
      if (modelType.value == 'singleReview') {
        res = await batchAudit({
          idStr: id.value,
          status: status,
          ...values,
        });
      } else if (modelType.value == 'singleEdit') {
        res = await batchUpdate({
          idStr: id.value,
          ...values,
        });
      } else if (modelType.value == 'singleRenewal') {
        res = await batchExtension({
          idStr: id.value,
          ...values,
        });
      } else if (modelType.value == 'singleDelete') {
        res = await deleteItem({
          id: String(id.value),
          ...values,
        });
      } else if (modelType.value == 'edit') {
        res = await batchUpdate({
          idStr: props.selectKeys.join(','),
          ...values,
        });
      } else if (modelType.value == 'review') {
        res = await batchAudit({
          idStr: props.selectKeys.join(','),
          status: status,
          ...values,
        });
      } else {
        res = await batchExtension({
          idStr: props.selectKeys.join(','),
          ...values,
        });
      }
      console.log('提交表单结果==', res);
      // 提交表单
      // await saveOrUpdateDict({ ...values, supperId: 2, supperCode: 'enter_per_type', isUse: 1 });
      createMessage.success('操作成功');
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } catch (er) {
      console.log('错误===', er);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
<style lang="less" scoped>
  ::v-deep(.ant-modal-content) {
    border-radius: 4px !important;
    padding: 20px !important;
  }

  ::v-deep(.ant-modal-footer) {
    text-align: center;
    .ant-btn:nth-child(1) {
      width: 80px;
      border-color: #797979;
      border-radius: 4px;
    }
    .ant-btn:nth-child(2) {
      width: 80px;

      border-radius: 4px;
      margin-left: 20px;
    }
    .ant-input-affix-wrapper {
      border-color: #797979;
      border-radius: 4px;
    }
  }
</style>
