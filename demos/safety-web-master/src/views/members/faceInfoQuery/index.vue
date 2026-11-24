<template>
  <div class="face-query-page">
    <section
      class="face-query-upload"
      :class="{ 'is-dragover': isDragover, 'has-image': Boolean(uploadPreview) }"
      @dragenter.prevent="handleDragEnter"
      @dragover.prevent="handleDragEnter"
      @dragleave.prevent="handleDragLeave"
      @drop.prevent="handleDrop"
    >
      <button v-if="uploadPreview" class="face-query-clear" type="button" aria-label="删除图片" @click="clearUpload">×</button>

      <div v-if="uploadPreview" class="face-query-preview">
        <img :src="uploadPreview" alt="已上传的人脸图片" />
        <a-button class="face-query-submit" type="primary" :loading="loading" @click="handleSearch">查询</a-button>
      </div>

      <div v-else class="face-query-empty" @click="triggerFileSelect">
        <div class="face-query-empty-icon" aria-hidden="true">
          <img :src="uploadIcon" alt="" />
        </div>
        <div class="face-query-empty-text"><strong>拖拽</strong> 本地图片到此，或点击上传</div>
        <a-button class="face-query-upload-btn">上传图片</a-button>
      </div>

      <div class="face-query-rule">
        <div class="face-query-rule-item">
          <div class="rule-icon rule-format" aria-hidden="true">
            <img :src="imageIcon" alt="" />
          </div>
          <div>
            <div class="rule-title">支持格式</div>
            <div class="rule-desc">上传支持PNG、JPG、JPEG图片格式</div>
          </div>
        </div>
        <div class="face-query-rule-item">
          <div class="rule-icon rule-size" aria-hidden="true">
            <img :src="photoIcon" alt="" />
          </div>
          <div>
            <div class="rule-title">照片大小</div>
            <div class="rule-desc">上传的图片大小在10KB - 200MB之间</div>
          </div>
        </div>
      </div>

      <input ref="fileInputRef" class="face-query-file" type="file" accept="image/png,image/jpeg" @change="handleFileChange" />
    </section>

    <section class="face-query-result">
      <h2>检索结果</h2>
      <a-table :columns="tableColumns" :data-source="resultList" :loading="loading" :pagination="false" rowKey="id" bordered class="face-query-table">
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'facePhoto'">
            <div class="result-photo-cell">
              <img
                v-if="record.facePhoto"
                class="result-photo"
                :src="record.facePhoto"
                alt="人脸照片"
                @click="handlePreview(record.facePhoto)"
              />
              <span v-else>无</span>
            </div>
          </template>
        </template>
      </a-table>
    </section>

    <a-modal v-model:visible="previewVisible" title="人脸照片" :footer="null" width="auto" centered>
      <img :src="previewUrl" class="preview-image" alt="人脸照片大图" />
    </a-modal>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'MembersFaceInfoQuery',
  };
</script>

<script lang="ts" setup>
  import { computed, onBeforeUnmount, ref } from 'vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getFileAccessHttpUrl } from '/@/utils/common/compUtils';
  import uploadIcon from '/@/assets/icons/faceQuery/icon_faceQuery_upload.png';
  import imageIcon from '/@/assets/icons/faceQuery/icon_faceQuery_img.png';
  import photoIcon from '/@/assets/icons/faceQuery/icon_faceQuery_photo.png';
  import { searchPhoto } from './api';

  interface FaceResultItem {
    id: string | number;
    empName: string;
    empNo: string;
    facePhoto: string;
    orgNamePath: string;
    similarity: string;
  }

  const { createMessage } = useMessage();
  const fileInputRef = ref<HTMLInputElement | null>(null);
  const uploadFile = ref<File | null>(null);
  const uploadPreview = ref('');
  const loading = ref(false);
  const isDragover = ref(false);
  const resultList = ref<FaceResultItem[]>([]);
  const previewVisible = ref(false);
  const previewUrl = ref('');

  const tableColumns = computed(() => [
    {
      title: '姓名',
      dataIndex: 'empName',
      align: 'center',
    },
    {
      title: '用户编号',
      dataIndex: 'empNo',
      align: 'center',
    },
    {
      title: '人脸照片',
      dataIndex: 'facePhoto',
      align: 'center',
    },
    {
      title: '人脸相似度',
      dataIndex: 'similarity',
      align: 'center',
    },
  ]);

  function triggerFileSelect() {
    fileInputRef.value?.click();
  }

  function handleFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];
    if (file) {
      setUploadFile(file);
    }
    target.value = '';
  }

  function handleDragEnter() {
    isDragover.value = true;
  }

  function handleDragLeave() {
    isDragover.value = false;
  }

  function handleDrop(event: DragEvent) {
    isDragover.value = false;
    const file = event.dataTransfer?.files?.[0];
    if (file) {
      setUploadFile(file);
    }
  }

  function setUploadFile(file: File) {
    const allowedTypes = ['image/png', 'image/jpeg'];
    if (!allowedTypes.includes(file.type)) {
      createMessage.warn('请上传PNG、JPG、JPEG格式图片');
      return;
    }
    if (file.size < 10 * 1024 || file.size > 200 * 1024 * 1024) {
      createMessage.warn('图片大小需在10KB - 200MB之间');
      return;
    }
    clearPreviewUrl();
    uploadFile.value = file;
    uploadPreview.value = URL.createObjectURL(file);
    resultList.value = [];
  }

  function clearPreviewUrl() {
    if (uploadPreview.value) {
      URL.revokeObjectURL(uploadPreview.value);
    }
  }

  function clearUpload() {
    clearPreviewUrl();
    uploadFile.value = null;
    uploadPreview.value = '';
    resultList.value = [];
  }

  function getFacePhoto(record) {
    const photo = record?.facePic?.faceData || '';
    if (!photo) {
      return '';
    }
    if (/^(data:image|blob:|https?:\/\/)/.test(photo)) {
      return photo;
    }
    // 后端返回纯 base64 字符串时，补全 data URI 前缀
    if (isBase64String(photo)) {
      return wrapBase64Image(photo);
    }
    return getFileAccessHttpUrl(photo);
  }

  function isBase64String(value: unknown): value is string {
    if (typeof value !== 'string') {
      return false;
    }
    const trimmed = value.trim();
    if (!trimmed) {
      return false;
    }
    return /^[A-Za-z0-9+/]+={0,2}$/.test(trimmed) && trimmed.length % 4 === 0;
  }

  function wrapBase64Image(base64: string): string {
    if (base64.startsWith('iVBORw0KGgo')) {
      return `data:image/png;base64,${base64}`;
    }
    if (base64.startsWith('/9j/')) {
      return `data:image/jpeg;base64,${base64}`;
    }
    return `data:image/jpeg;base64,${base64}`;
  }

  async function handleSearch() {
    if (!uploadFile.value) {
      createMessage.warn('请先上传图片');
      return;
    }
    loading.value = true;
    try {
      const res = await searchPhoto(uploadFile.value);
      const payload = res?.data || res?.result || res;
      const dataList = Array.isArray(payload)
        ? payload
        : payload?.dataList || payload?.records || payload?.list || payload?.rows || payload?.items || [];
      resultList.value = dataList.map((item, index) => ({
        id: item.indexCode || item.id || item.empNo || index,
        empName: item.faceInfo?.name || item.empName || item.name || '--',
        empNo: item.faceInfo?.certificateNum || item.indexCode || item.empNo || item.employeeCode || '--',
        facePhoto: getFacePhoto(item),
        orgNamePath:
          [item.faceInfo?.extendInfo1, item.faceInfo?.extendInfo2, item.faceInfo?.extendInfo3, item.faceInfo?.extendInfo4]
            .filter(Boolean)
            .join(' / ') ||
          item.orgNamePath ||
          item.orgName ||
          '--',
        similarity: formatSimilarity(item.similarity || item.faceSimilarity || item.score),
      }));
      if (!resultList.value.length) {
        createMessage.info('未检索到匹配结果');
      }
    } finally {
      loading.value = false;
    }
  }

  function formatSimilarity(value) {
    if (value === 0 || value) {
      return String(value).includes('%') ? String(value) : `${value}%`;
    }
    return '--';
  }

  function handlePreview(url: string) {
    previewUrl.value = url;
    previewVisible.value = true;
  }

  onBeforeUnmount(() => {
    clearPreviewUrl();
  });
</script>

<style lang="less" scoped>
  @import 'index';
</style>
