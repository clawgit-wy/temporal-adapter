<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <div class="scm-modal">
      <BasicForm @register="registerForm">
        <template #empNo="{ model, field }">
          <a-input v-model:value="model[field]" placeholder="请输入" @blur="onBlur" />
        </template>
      </BasicForm>
    </div>
  </BasicModal>
</template>
<script lang="ts" setup name="ScmModal">
  import { ref, reactive, computed, unref } from 'vue';
  import { debounce } from 'lodash-es';
  import { AutoComplete } from 'ant-design-vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStore } from '/@/store/modules/user';
  import { scmSchema } from './config';
  import { saveItem, getEmployeeByWorkNo } from './field.api';
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
  const [registerForm, { resetFields, setFieldsValue, clearValidate, validate, setProps, updateSchema, getFieldsValue }] = useForm({
    schemas: scmSchema,
    showActionButtonGroup: false,
  });
  // 表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    console.log('this modal data', data);
    // 分别设置编辑状态和新增表单状态的字段内容

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
    await setFieldsValue({
      ...data.record,
    });
    // 清除验证状态
    clearValidate();
  });

  //获取标题
  const getTitle = computed(() => (!unref(isUpdate) ? '新增' : '编辑'));

  function onBlur(e) {
    if (e.target.value) {
      getEmployeeByWorkNo({ workNo: e.target.value }).then((res) => {
        if (!res.positionName || !res.positionCode) {
          createMessage.info('当前人员没有岗位信息，无法添加');
        }
        setFieldsValue({
          employeeName: res.empName,
          positionCode: res.positionCode,
          positionName: res.positionName,
        });
      });
    }
  }

  //表单提交事件
  async function handleSubmit() {
    try {
      let values = await validate();
      console.log('验证表单', values);
      setModalProps({ confirmLoading: true });
      //提交表单
      // values.id = unref(rowId);
      const res = await saveItem(values, unref(isUpdate));
      console.log('新增结果==', res);
      createMessage.info({ content: res });
      // 2 由于表单获取的是ids, 无法回显到列表，统一刷新列表，不再进行行更新
      isUpdated.value = 1;
      console.log('编辑应用成功');
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
