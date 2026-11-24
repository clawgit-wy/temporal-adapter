<template>
  <div class="clearfix">
    <a-upload
      :listType="listType"
      accept="image/*"
      :multiple="multiple"
      :action="uploadUrl"
      :headers="headers"
      :data="{ biz: bizPath }"
      v-model:fileList="uploadFileList"
      :beforeUpload="beforeUpload"
      :disabled="disabled"
      @change="handleChange"
      :max-count="fileMax"
    >
      <div v-if="uploadVisible">
        <div v-if="listType === 'picture-card'">
          <LoadingOutlined v-if="loading"/>
          <UploadOutlined v-else/>
          <div class="ant-upload-text">{{ text }}</div>
        </div>
        <a-button v-if="listType === 'picture'" :disabled="disabled">
          <UploadOutlined/>
          {{ text }}
        </a-button>
      </div>
    </a-upload>
    <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
      <img alt="预览图片" style="width: 100%" :src="previewImage"/>
    </a-modal>
  </div>
</template>
<script lang="ts">
import {defineComponent, PropType, ref, watch, computed} from 'vue';
import {LoadingOutlined, UploadOutlined} from '@ant-design/icons-vue';
import {useRuleFormItem} from '/@/hooks/component/useFormItem';
import {propTypes} from '/@/utils/propTypes';
import {useAttrs} from '/@/hooks/core/useAttrs';
import {useMessage} from '/@/hooks/web/useMessage';
import {getFileAccessHttpUrl, getRandom} from '/@/utils/common/compUtils';
import {uploadUrl} from '/@/api/common/api';
import {getToken} from '/@/utils/auth';

const {createMessage} = useMessage();
export default defineComponent({
  name: 'JCustomImageUpload',
  components: {LoadingOutlined, UploadOutlined},
  inheritAttrs: false,
  props: {
    // 绑定值
    value: propTypes.oneOfType([propTypes.string, propTypes.arrayOf(propTypes.string)]),
    // 列表类型
    listType: {
      type: String as PropType<'picture-card' | 'picture' | 'text'>,
      required: false,
      default: 'picture-card',
    },
    // 按钮文本
    text: {
      type: String,
      required: false,
      default: '上传',
    },
    // 业务路径
    bizPath: {
      type: String,
      required: false,
      default: 'temp',
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      required: false,
      default: false,
    },
    // 最大上传数量
    fileMax: {
      type: Number,
      required: false,
      default: 1,
      validator: (value) => value > 0,
    },
  },
  emits: ['change', 'update:value'],
  setup(props, {emit}) {
    const attrs = useAttrs();
    const [state] = useRuleFormItem(props, 'value', 'change');

    // 获取文件名
    const getFileName = (path: string) => {
      if (!path) return '';
      // 统一替换为 '/' 处理
      const normalizedPath = path.replace(/\\/g, '/');
      return normalizedPath.substring(normalizedPath.lastIndexOf('/') + 1);
    };

    // 请求头
    const headers = ref({
      Authorization: getToken(),
      'X-Access-Token': getToken(),
    });

    // 上传状态
    const loading = ref(false);
    // 是否是初始化加载
    const initTag = ref(true);
    // 文件列表
    const uploadFileList = ref<any[]>([]);
    // 预览图
    const previewImage = ref<string>('');
    // 预览框状态
    const previewVisible = ref(false);

    // 计算是否开启多图上传
    const multiple = computed(() => props.fileMax > 1);

    // 计算是否可以继续上传
    const uploadVisible = computed(() => uploadFileList.value.length < props.fileMax);

    /**
     * 监听value变化，初始化文件列表
     */
    watch(
      () => props.value,
      (val) => {
        if (initTag.value) {
          initFileList(val);
          initTag.value = false;
        }
      },
      {immediate: true, deep: true}
    );

    /**
     * 初始化文件列表
     */
    function initFileList(paths: string | string[] | undefined) {
      if (!paths || (Array.isArray(paths) && paths.length === 0) || (typeof paths === 'string' && paths.trim() === '')) {
        uploadFileList.value = [];
        return;
      }

      const pathArray = Array.isArray(paths) ? paths : paths.split(',').filter(Boolean);
      const files = pathArray.map((value) => {
        const url = getFileAccessHttpUrl(value);
        return {
          uid: getRandom(10),
          name: getFileName(value),
          status: 'done',
          url,
          response: {
            status: 'history',
            message: value,
          },
        };
      });

      uploadFileList.value = files;
    }

    /**
     * 上传前校验
     */
    function beforeUpload(file: File) {
      loading.value = true;
      console.log('beforeUpload>>>>>>>>>', file)
      const fileType = file.type;
      if (!fileType.includes('image')) {
        createMessage.error('请上传图片文件');
        loading.value = false;
        return false;
      }
      return true;
    }

    /**
     * 文件上传结果回调
     */
    function handleChange({file, fileList}) {
      uploadFileList.value = fileList;

      // 处理上传状态
      if (file.status === 'uploading') {
        loading.value = true;
        return;
      }

      if (file.status === 'done') {
        file.url = file.response.data || file.thumbUrl;
        createMessage.success(`${file.name} 上传成功`);
      } else if (file.status === 'error') {
        createMessage.error(`${file.name} 上传失败`);
      }

      loading.value = false;

      // 收集文件URL
      const fileUrls = fileList
        .filter(f => f.status === 'done')
        .map(f => f.response?.data || f.response?.message || f.url);

      // 更新绑定值
      const newValue = multiple.value ? fileUrls : fileUrls[0] || '';
      state.value = newValue;
      emit('update:value', newValue);
    }

    /**
     * 预览图片
     */
    function handlePreview(file: any) {
      console.log('handlePreview-more',file)

      previewImage.value = file.url || file.response.data || file.thumbUrl;
      previewVisible.value = true;
    }

    /**
     * 关闭预览
     */
    function handleCancel() {
      previewVisible.value = false;
    }

    return {
      state,
      attrs,
      previewImage,
      previewVisible,
      uploadFileList,
      multiple,
      headers,
      loading,
      uploadUrl,
      beforeUpload,
      uploadVisible,
      handlePreview,
      handleCancel,
      handleChange,
    };
  },
});
</script>
<style scoped>
.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
</style>
