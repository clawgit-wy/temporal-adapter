<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <div style="max-height: 500px; overflow-y: auto">
      <a-tree checkable :tree-data="treeData" :height="500" v-model:checkedKeys="checkedKeys" />
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { setPerm } from './roles.api';
  const tree = [];
  const treeData = ref(tree);
  const checkedKeys = ref<string[]>([]);
  const type = ref<string>('');
  const record = ref({});
  // watch(checkedKeys, () => {
  //   console.log('expandedKeys watch', checkedKeys);
  // });

  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);

  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    console.log(data.zTreeRoot);
    
    treeData.value = unref(data.zTreeRoot);
    checkedKeys.value = unref(data.permIds);
    type.value = unref(data.type);
    record.value = unref(data.record);
    // await resetFields();
    // await setFieldsValue({ ...data.record });
    // 隐藏底部时禁用整个表单
    // setProps({ disabled: !data?.showFooter })
  });

  //表单提交事件
  async function onSubmit() {
    try {
      // const values = await validate()
      const params = record.value;
      if (type.value == 'permIds') {
        setModalProps({ confirmLoading: true });
        // 提交表单;
        await setPerm({ roleId: params.id, permIds: checkedKeys.value.join(',') });
      }
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success', checkedKeys.value);
    } finally {
      // setModalProps({ confirmLoading: false });
    }
  }
</script>
