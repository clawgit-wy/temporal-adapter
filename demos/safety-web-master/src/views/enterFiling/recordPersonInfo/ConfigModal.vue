<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm">
      <template #tip>
        <div class="createDeptName">
          <p style="padding-left: 50px; color: #ff4d4f"
            >注意：当前给备案人员下发权限的方式，包括备案人员申请及批量导入或黑名单申请。修改后仅对后续审核通过的人员生效。</p
          >
        </div>
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup name="ApplicationModal">
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { configFormSchemas } from './data.ts';
  import { createEnterTimeRangeConfig, createRangeEnterTimeRangeConfig } from './const';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { updateEntryMethod } from './api';
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
    schemas: configFormSchemas,
    showActionButtonGroup: false,
    labelWidth: 120,
    validateTrigger: 'blur', // 设置为失去焦点时才触发校验
    fieldMapToTime: [['enterTimeRange', ['enterStartTime', 'enterEndTime'], 'YYYY-MM-DD HH:mm']],
  });

  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({
      confirmLoading: false,
      showCancelBtn: !!data?.showFooter,
      showOkBtn: !!data?.showFooter,
    });

    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    // 重置表单
    await resetFields();
    await setFieldsValue({ ...data.record });
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

      // 调用接口保存数据
      const res = await updateEntryMethod({ ...values });
      console.log('添加人员==', res);
      if (res) {
        createMessage.error(res || '操作失败');
      } else {
        createMessage.success('操作成功');
        //关闭弹窗
        closeModal();
        //刷新列表
        emit('success');
      }
      // 显示成功消息
    } catch (error) {
      console.error('操作失败:', error);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
  .createDeptName {
  }
  ::v-deep .ant-form-item-control {
    max-width: 100%;
    padding-right: 50px;
  }
</style>
