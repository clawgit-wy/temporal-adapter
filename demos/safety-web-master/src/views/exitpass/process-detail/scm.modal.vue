<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <div class="scm-modal">
      <BasicForm @register="registerForm"> </BasicForm>
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
import { rejectProcess } from './user.api';
// 声明Emits
const emit = defineEmits(['success', 'register']);
// 页面消息
const { createMessage } = useMessage();
// 表单配置
const rowId = ref('');

const [registerForm, { resetFields, setFieldsValue, validate, setProps, updateSchema, getFieldsValue }] = useForm({
  schemas: [
    {
      label: '驳回理由',
      field: 'auditNote',
      component: 'Input',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请输入驳回理由',
      },
    },
    {
      label: '驳回人',
      field: 'curPersonLogin',
      show: false,
      component: 'Input',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请输入驳回人',
      },
    },
    {
      label: '流程ID',
      field: 'flowId',
      show: false,
      component: 'Input',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请输入流程ID',
      },
    },
    {
      label: 'ID',
      field: 'id',
      show: false,
      component: 'Input',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请输入ID',
      },
    },
    {
      label: '是否更新详情',
      field: 'isUpdateDetail',
      show: false,
      component: 'Input',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请输入是否更新详情',
      },
    },
    {
      label: '节点标识',
      field: 'nodeFlag',
      show: false,
      component: 'Input',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请输入节点标识',
      },
    },
    {
      label: '状态',
      field: 'status',
      show: false,
      component: 'Input',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请输入状态',
      },
    },
    {
      label: '跳转节点ID',
      field: 'toNodeId',
      show: false,
      component: 'Input',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请输入跳转节点ID',
      },
    },
  ],
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
  //表单赋值
  if (typeof data.record === 'object') {
    setFieldsValue({
      ...data.record,
    });
  }
});

//获取标题
const getTitle = '驳回';

//表单提交事件
async function handleSubmit() {
  try {
    let values = await validate();
    console.log('验证表单', values);
    setModalProps({ confirmLoading: true });
    //提交表单
    await rejectProcess(values);
    console.log('驳回成功');
    closeModal();
    emit('success', { values });
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
