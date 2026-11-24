A<template>
  <div ref="refModal">
    <BasicModal @register="registerModal" :title="title" :footer="null" :canFullscreen="false" :width="500" :getContainer="() => $refs.refModal">
      <div>
        <a-image :src="src" :width="100"></a-image>
      </div>
      <div class="up">
        <a-upload :multiple="true" :headers="headers" :showUploadList="false" @change="handleChangeUpload" :customRequest="customRequest">
          <a-button class="bt2" type="primary" :danger="danger" :loading="loading" :disabled="disabled" preIcon="ant-design:import-outlined">{{
            btnName
          }}</a-button>
        </a-upload>
      </div>
    </BasicModal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, unref, watch, reactive, onMounted } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { Image as AImage } from 'ant-design-vue';
  import { getToken } from '/@/utils/auth';
  const danger = ref<boolean>(false);
  const btnName = ref<string>('重新上传');
  const disabled = ref<boolean>(false);
  const loading = ref<boolean>(false);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  const src = ref<string>();
  const refModal = ref<any>();
  let token = getToken();
  const headers = {
    Authorization: token,
    'X-Access-Token': token,
  };
  console.log(headers, 'token');
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ loading: false, confirmLoading: false });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    src.value = unref(data.record.src);
    console.log(src.value, '图片地址');
  });

  function handleChangeUpload(info) {
    const { file } = info;
    console.log(file, '文件信息===========');
    if (file.status == 'error') {
    }
  }
  const customRequest = ({ onError, onSuccess, file }) => {
    danger.value = false;
    btnName.value = '正在上传';
    disabled.value = true;
    loading.value = true;
    const formData = new FormData();
    formData.append('file', file);
    upload(formData)
      .then((response) => {
        onSuccess(response);
        btnName.value = '重新上传';
        disabled.value = false;
        loading.value = false;
      })
      .catch((error) => {
        onError(error);
        btnName.value = '上传失败';
        disabled.value = false;
        loading.value = false;
        danger.value = true;
      })
      .finally(() => {
        setTimeout(() => {}, 1000);
      });
  };
  onMounted(() => {
    btnName.value = '重新上传';
    disabled.value = false;
    loading.value = false;
    danger.value = false;
  });
</script>
<style lang="less" scoped>
  ::v-deep(.ant-modal-content) {
    border-radius: 4px !important;
    padding: 20px !important;
    .up .ant-btn {
      width: 100px;
      margin-top: 10px;
      .app-iconify {
        margin-left: -5px !important;
      }
    }
    .up .ant-btn-primary[disabled],
    .ant-btn-primary[disabled]:hover,
    .ant-btn-primary[disabled]:focus,
    .ant-btn-primary[disabled]:active {
      color: #fff;
      border-color: #40a9ff;
      background: #40a9ff;
      text-shadow: none;
      box-shadow: none;
      cursor: not-allowed;
    }
  }
</style>
