<template>
  <div class="lens_wrap">
    <div class="org-tree">
      <div class="search-bar">
        <a-input-search v-model:value="searchText" placeholder="请输入" style="width: 100%" @search="onSearch" autocomplete="off" />
        <a-button type="link" @click="clearSearch" class="clear-search">清除搜索</a-button>
      </div>

      <!-- 树内容区域 -->
      <div class="tree-content">
        <!-- 树组件 -->
        <BasicTree
          :treeData="treeData"
          :checkable="true"
          v-model:checkedKeys="checkedKeys"
          clickRowToExpand
          @check="handleCheck"
          ref="treeRef"
          :defaultExpandAll="true"
        >
          <template #title="{ checkable, title, workCompany, principalName, principalNum }">
            <!-- <div @click="printAction(data)"> {{ data }}</div> -->
            <div v-if="!checkable">{{ title }}</div>
            <a-tooltip v-else placement="top">
              <template #title>{{ getFullText(title, workCompany, principalName, principalNum) }}</template>
              <div class="ellipsis-text">{{ getEllipsisText(title, workCompany, principalName, principalNum) }}</div>
            </a-tooltip>
          </template>
        </BasicTree>
      </div>
    </div>
    <div class="lens_video">
      <div class="video-header">
        <a-radio-group :value="currentCategory" @change="categoryChange">
          <a-radio-button value="2">实时</a-radio-button>
          <a-radio-button value="1">回放</a-radio-button>
        </a-radio-group>
      </div>
      <div class="video-content">
        <VideoPlayer :urls="urls" :playMode="currentCategory" :splitNum="splitNum" ref="videoPlayerRef" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  //ts语法
  import { ref, unref, onMounted, nextTick, computed } from 'vue';
  import { cloneDeep } from 'lodash-es';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getFireApproveTree, getMonitorVideoUrl } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { useRouter, useRoute } from 'vue-router';
  import type { DataNode } from 'ant-design-vue/es/tree';
  import { BasicTree } from '/@/components/Tree';
  import type { TreeActionType } from '/@/components/Tree';
  import VideoPlayer from './VideoPlayer.vue';
  import dayjs from 'dayjs';
  const route = useRoute();
  const router = useRouter();
  // 消息
  const { createMessage } = useMessage();
  const userStore = useUserStore();
  const userInfo = userStore.getUserInfo;
  const { hasPermission } = usePermission();
  // 播放地址
  const urls = ref([]);
  const searchText = ref<string>('');
  const checkedKeys = ref<string[]>([]);
  const treeData = ref<DataNode[]>([]);
  const selectedItems = ref<any[]>([]);
  const currentCategory = ref('2');
  const treeRef = ref<Nullable<TreeActionType>>(null);
  const videoPlayerRef = ref<Nullable<InstanceType<typeof VideoPlayer>>>(null);
  //获取镜头数据
  const getLensData = async () => {
    let result = await getFireApproveTree({
      empNo: userInfo.workNo,
      commonParam: searchText.value,
    });

    treeData.value = result;

    // 数据加载完成后手动展开所有节点
    nextTick(() => {
      treeRef.value?.expandAll(true);
    });
  };
  const splitNum = computed(() => {
    if (checkedKeys?.value?.length == 0) {
      return 1;
    }
    return checkedKeys?.value?.length > 1 ? 2 : 1;
  });
  onMounted(() => {
    getLensData();
  });
  function printAction(item) {
    console.log('节点的值是', item);
  }
  // 搜索方法
  const onSearch = async () => {
    console.log('搜索', !searchText.value);
    if (searchText.value.trim()) {
      getLensData();
      return;
    }
  };
  // 清除搜索
  const clearSearch = () => {
    searchText.value = '';
    getLensData();
  };
  // 处理复选框选中事件
  const handleCheck = async (keys: string[], { checked, node, event }) => {
    console.log('点击复选', node.dataRef.key);

    // 限制最多选择4个节点
    if (keys.length > 4 && checked) {
      createMessage.warning('最多只能选择4个节点');
      ``;
      // 恢复到之前的选择状态，只保留前4个选中的节点
      checkedKeys.value = keys.slice(0, 4);
      return;
    }
    handleVideoUrl(checked, node);
    // 更新选中的节点
    checkedKeys.value = keys;
  };
  //处理视频链接
  const handleVideoUrl = async (checked: boolean, node: any) => {
    let key = node.dataRef.key;
    if (checked) {
      const res = await getMonitorVideoUrlDetail(key);
      //检测urls中是否存在key,如果存在则更新，否则新增
      const index = urls.value.findIndex((item) => item.key === key);
      const url = res.back;
      const urlParams = new URLSearchParams(url?.split('?')[1]) || {};
      const beginTime = urlParams.get('beginTime') || '';
      const endTime = urlParams.get('endTime') || '';

      const item = {
        realplay: res.preview,
        talk: '',
        playback: res.back || '',
        startTime: beginTime ? dayjs(beginTime).format('YYYY-MM-DDTHH:mm:ss') : '',
        endTime: endTime ? dayjs(endTime).format('YYYY-MM-DDTHH:mm:ss') : '',
        playbackSpeed: 1,
        key: key,
      };
      if (index !== -1) {
        urls.value[index] = item;
        if (currentCategory.value === '1') {
          nextTick(() => {
            videoPlayerRef.value?.playbackStart(index);
          });
        } else {
          nextTick(() => {
            videoPlayerRef.value?.realplay(index);
          });
        }
      } else {
        urls.value.push(item);
        if (currentCategory.value === '1') {
          nextTick(() => {
            videoPlayerRef.value?.playbackStart(urls.value.length - 1);
          });
        } else {
          nextTick(() => {
            videoPlayerRef.value?.realplay(urls.value.length - 1);
          });
        }
      }
    } else {
      //检测urls中是否存在key,如果存在则删除
      const index = urls.value.findIndex((item) => item.key === key);
      if (index !== -1) {
        urls.value.splice(index, 1);
      }
      if (currentCategory.value === '1') {
        nextTick(async () => {
          await videoPlayerRef.value?.stopAllPlay();
          await videoPlayerRef.value?.playAllPlayback();
        });
      } else {
        nextTick(async () => {
          await videoPlayerRef.value?.stopAllPlay();
          await videoPlayerRef.value?.playAllRealplay();
        });
      }
    }
  };
  // 切换分类
  function categoryChange(target: any) {
    currentCategory.value = target.target.value;
  }
  //获取视频链接信息
  const getMonitorVideoUrlDetail = async (key: string) => {
    console.log('key************', key);
    let keyArray = key.split('-') || [];
    const lensNum = keyArray[0];
    const id = keyArray[1];
    const result = await getMonitorVideoUrl({
      id,
      lensNum,
      videoType: '',
      streamType: checkedKeys.value?.length > 1 ? '1' : '0',
    });
    return JSON.parse(result);
  };

  // 获取完整文本
  const getFullText = (title, workCompany, principalName, principalNum) => {
    return `${title}(${workCompany}-${principalName}${principalNum})`;
  };

  // 获取带省略号的文本，最多显示8个字符
  const getEllipsisText = (title, workCompany, principalName, principalNum) => {
    const fullText = getFullText(title, workCompany, principalName, principalNum);
    return fullText.length > 8 ? `${fullText.substring(0, 8)}...` : fullText;
  };
</script>

<style lang="less" scoped>
  .lens_wrap {
    display: flex;
    height: 100%;
    background: #fff;

    .ellipsis-text {
      max-width: 100%;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      cursor: pointer;
    }

    .org-tree {
      // flex: 1;
      width: 250px;
      flex-shrink: 0; // 添加不可压缩属性
      border: 1px solid #d9d9d9;
      display: flex;
      flex-direction: column;
      height: 100%;

      .search-bar {
        position: sticky;
        top: 0;
        z-index: 10;
        background-color: #fff;
        padding: 8px;
        border-bottom: 1px solid #f0f0f0;
        display: flex;
        align-items: center;

        .clear-search {
          margin-left: 8px;
          padding: 0;
        }
      }

      .tree-content {
        flex: 1;
        overflow: auto;
        padding: 0 8px 8px 8px;
        background: white;
      }
    }
    .lens_video {
      flex: 1;
      display: flex;
      flex-direction: column;
      .video-header {
        padding: 8px;
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
      .video-content {
        padding: 8px;
        flex: 1;
        overflow: hidden;
      }
    }

    .tree-node-title {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;

      .ant-checkbox-wrapper {
        margin-left: 8px;
      }
    }

    .person-item {
      padding: 8px;
      border-bottom: 1px solid #f0f0f0;

      &.selected-person {
        color: #1890ff;
      }

      .person-info {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .remove-button {
          padding: 0;
          color: #ff4d4f;

          &:hover {
            color: #ff7875;
          }
        }
      }

      .person-dept {
        display: block;
        font-size: 12px;
        color: #999;
        margin-top: 4px;
      }
    }

    .search-results {
      .search-title {
        font-weight: bold;
        margin-bottom: 8px;
        padding-top: 8px;
      }

      .no-results {
        color: #999;
        padding: 12px 0;
        text-align: center;
      }
    }
  }
</style>
