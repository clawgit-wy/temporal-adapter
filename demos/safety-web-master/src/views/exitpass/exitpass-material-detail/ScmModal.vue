<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <div class="scm-modal">
      <BasicForm @register="registerForm" />
    </div>
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, reactive, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStore } from '/@/store/modules/user';
  import { scmSchema } from './config';
  import { save, update } from './field.api';
  // 声明Emits
  const emit = defineEmits(['success', 'register']);
  const isUpdate = ref(true); // 新增/编辑
  const isUpdated = ref(0); // 操作表示：0 无操作 1 新增/新增加编辑 2 编辑
  // 页面消息
  const { createMessage } = useMessage();
  // 用户信息
  const username = ref('');
  const userStore = useUserStore();
  username.value = userStore.getUserInfo.username;
  // 表单配置
  const rowId = ref('');
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, updateSchema }] = useForm({
    schemas: scmSchema,
    showActionButtonGroup: false,
  });
  // 表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    console.log('this modal data', data);
    //重置表单
    await resetFields();
    await setProps({
      labelCol: {
        sm: { span: 5 },
      },
      wrapperCol: {
        sm: { span: 17 },
      },
      baseRowStyle: { width: 50 },
    });
    await setModalProps({ confirmLoading: false, canFullscreen: false });
    isUpdate.value = !!data?.isUpdate;
    if (unref(isUpdate)) {
      rowId.value = data.record.id;
      // updateSchema([
      // {
      //     field: 'signDate',
      //     rules: [
      //         {
      //             required: false,
      //         },
      //     ],
      // },
      // ]);
    }
    //表单赋值
    if (typeof data.record === 'object') {
      setFieldsValue({
        ...data.record,
      });
    }
  });

  //获取标题
  const getTitle = computed(() => (!unref(isUpdate) ? '新增字典类型' : '编辑字典类型'));

  //表单提交事件
  async function handleSubmit() {
    try {
      let values = await validate();
      console.log('验证表单', values);
      setModalProps({ confirmLoading: true });
      //提交表单
      if (unref(isUpdate)) {
        values.id = unref(rowId);
        await update(values, unref(isUpdate));
        // 2 由于表单获取的是ids, 无法回显到列表，统一刷新列表，不再进行行更新
        isUpdated.value = 1;
        console.log('编辑应用成功');
      } else {
        delete values.id;
        await save(values, unref(isUpdate));
        isUpdated.value = 1;
        console.log('新增应用成功');
      }
      closeModal();
      emit('success', { isUpdate: unref(isUpdated), values });
    } catch (err) {
      console.log('subsystem modal err', typeof err, JSON.stringify(err), err);
      if (err && err instanceof Error && err.message) {
        createMessage.error({ content: err.message });
      }
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
<style lang="less">
  .scm-modal {
    .ant-row {
      .ant-col {
        // width: 100%;
        .ant-calendar-picker {
          display: block;
        }
      }
    }
  }
</style>
