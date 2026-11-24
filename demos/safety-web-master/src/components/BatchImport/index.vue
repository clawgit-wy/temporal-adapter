<template>
  <div class="batch-import">
    <!-- 导入按钮 -->
    <a-button v-if="showButton" type="primary" :disabled="disabled || loading" :loading="loading" @click="handleOpenModal">
      <template #icon><import-outlined /></template>
      {{ buttonText }}
    </a-button>

    <!-- 导入弹窗 -->
    <a-modal v-model:visible="visible" title="提示" :width="modalWidth" :footer="null" @cancel="handleCancel">
      <div class="batch-import-modal">
        <p>
          请按照模板规范填写，否则无法正确导入
          <a-button @click="handleDownloadTemplate">下载模板</a-button>
        </p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="handleUploadFile">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { ImportOutlined } from '@ant-design/icons-vue';
  import JUploadButton from '/@/components/Button/src/JUploadButton.vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useMethods } from '/@/hooks/system/useMethods';
  import { downloadByData } from '/@/utils/file/download';
  import { defHttp } from '/@/utils/http/axios';

  interface BatchImportProps {
    showButton?: boolean;
    buttonText?: string;
    modalWidth?: string | number;
    disabled?: boolean;
    importUrl?: string;
    downloadUrl?: string;
    downloadParams?: Record<string, any>;
    templateName?: string; // 模板文件名
    loading?: boolean; // 是否正在导入中（禁用按钮并显示loading）
  }

  const props = withDefaults(defineProps<BatchImportProps>(), {
    showButton: true,
    buttonText: '批量导入',
    modalWidth: 500,
    disabled: false,
    importUrl: '',
    downloadUrl: '',
    templateName: '批量导入模板',
    downloadParams: {},
    loading: false,
  });

  const emit = defineEmits<{
    (e: 'start'): void;
    (e: 'success', data?: any): void;
    (e: 'error', error: any): void;
  }>();

  const { createMessage } = useMessage();
  const { handleImportXls } = useMethods();
  const visible = ref(false);

  const handleOpenModal = () => {
    visible.value = true;
  };

  // 下载模板
  const handleDownloadTemplate = async () => {
    if (!props.downloadUrl) {
      createMessage.warning('未配置模板下载地址');
      return;
    }

    try {
      const res = await defHttp.get({ url: props.downloadUrl, params: props.downloadParams, responseType: 'blob' }, { isTransformResponse: false });
      const fileName = props.templateName.endsWith('.xlsx') ? props.templateName : `${props.templateName}.xlsx`;
      downloadByData(res, fileName);
    } catch (error) {
      createMessage.error('下载模板失败');
    }
  };

  // 处理文件上传
  const handleUploadFile = async (file: any) => {
    const uploadFile = file.file;

    // 文件类型校验
    const isExcel =
      uploadFile.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || uploadFile.type === 'application/vnd.ms-excel';
    if (!isExcel) {
      createMessage.error('只能上传 Excel 文件！');
      return;
    }

    // 文件大小校验
    const isLt10M = uploadFile.size / 1024 / 1024 < 10;
    if (!isLt10M) {
      createMessage.error('文件大小不能超过 10MB！');
      return;
    }

    // 检查是否配置了导入URL
    if (!props.importUrl) {
      createMessage.error('未配置导入接口URL');
      return;
    }

    emit('start');

    try {
      // 调用后端接口上传文件
      await handleImportXls(
        file,
        props.importUrl,
        (fileInfo) => {
          // 导入成功回调
          if (fileInfo.code === '0' || fileInfo.code === 0) {
            visible.value = false;
            emit('success', fileInfo);
          } else {
            emit('error', fileInfo);
          }
        },
        false
      );
    } catch (error) {
      console.error('导入失败:', error);
      console.log('导入失败:', error);
      emit('error', error);
    }
  };

  // 取消导入
  const handleCancel = () => {
    visible.value = false;
  };

  // 暴露方法供父组件调用
  defineExpose({
    open: handleOpenModal,
  });
</script>

<style lang="less" scoped>
  .batch-import {
    display: inline-block;
  }

  .batch-import-modal {
    padding: 10px;

    p {
      margin-bottom: 16px;
      line-height: 32px;
    }

    .preview-section {
      margin-top: 20px;
    }
  }
</style>
