<template>
  <div ref="refModal">
    <BasicModal
      @register="registerModal"
      :width="600"
      :title="title"
      :canFullscreen="false"
      :height="150"
      :min-height="150"
      @ok="onAdopt(1)"
      okText="审核通过"
      :getContainer="() => $refs.refModal"
    >
      {{ con }}

      <template #centerFooter>
        <a-button type="primary" @click="onAdopt(2)" danger>驳回</a-button>
      </template>
    </BasicModal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, unref, watch, reactive, onMounted } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { batchApproval } from './api';
import { message } from 'ant-design-vue';

  const title = ref<string>('');
  const ids = ref([]);
  const con = ref();

  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ loading: false, confirmLoading: false });
    title.value = data.title;
    ids.value = data.ids;
    con.value = `对选中的${ids.value.length}个未审批的物流车入厂申请进行批量审核`;
  });

  const emit = defineEmits(['success'])
  async function onAdopt(e) {
    const res = await batchApproval({ ids: ids.value.join(';'), approveType: e });
    emit('success');
    message.success('操作成功')
    closeModal();
  }

  onMounted(() => {});
</script>
<style lang="less" scoped>
  ::v-deep(.ant-modal-content) {
    border-radius: 4px !important;
    padding: 20px !important;
  }
  ::v-deep(.ant-modal-body) {
    display: flex;
    align-items: center; /* 垂直居中 */
    justify-content: center; /* 水平居中 */
    font-size: 20px;
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

    .ant-btn:nth-child(3) {
      width: 80px;
      margin-left: 20px;
      border-radius: 4px;
    }
  }
</style>
