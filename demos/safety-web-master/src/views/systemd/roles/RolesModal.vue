<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm">
      <template #permIds="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <div class="RolesModal-box">
          <a-input class="rolesInputdisable"  v-model:value="model[field]" placeholder="请选择" disabled />
          <div class="RolesModal-box-click" @click="onAddTree"></div>
        </div>
      </template>
    </BasicForm>
    <TreeModal @register="registerTreeModal" @success="getTreeSelect" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useModal } from '/@/components/Modal';
  import TreeModal from './TreeModal.vue';
  import { formSchemas } from './roles.data';
  import { saveOrUpdate } from './roles.api';

  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  const [registerTreeModal, treeModal] = useModal();
  let zTreeRoot = [];
  // let quanxian = [];

  // 注册 form
  //update-begin---author:wangshuai ---date:20221123  for：[VUEN-2807]消息模板加一个查看功能------------
  const [registerForm, { getFieldsValue, resetFields, setFieldsValue, validate, setProps }] = useForm({
    //update-end---author:wangshuai ---date:20221123  for：[VUEN-2807]消息模板加一个查看功能--------------z
    schemas: formSchemas,
    showActionButtonGroup: false,
  });

  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    zTreeRoot = unref(data.zTreeRoot);
    await resetFields();
    await setFieldsValue({ ...data.record, state: data.record.permIds && data.record.permIds.length > 0 ? '已设置' : '' });
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  // 显示tree组件
  function onAddTree() {
    const formData = getFieldsValue();
    treeModal.openModal(true, {
      title: '选择菜单',
      showFooter: true,
      record: {},
      permIds: formData.permIds ? formData.permIds.split(',') : [],
      zTreeRoot,
    });
  }

  function getTreeSelect(res) {
    const formData = getFieldsValue();
    let quanxian = res ? res : [];
    setFieldsValue({ ...formData, state: quanxian.length > 0 ? '已设置' : '', permIds: quanxian.join(',') });
  }

  //表单提交事件
  async function onSubmit() {
    try {
      const values = await validate();
      const params = { ...values };
      delete params.state;
      setModalProps({ confirmLoading: true });
      // 提交表单
      await saveOrUpdate(params, isUpdate);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
<style lang="less">
.RolesModal-box {
  position: relative;
  .RolesModal-box-click {
    position: absolute;
    width: 100%;
    height: 100%;
    left: 0;
    top: 0;
    z-index: 2;
    cursor: pointer;
  }
}
</style>
