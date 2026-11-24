<template>
  <div class="person-select-tree">
    <a-modal
      v-model:visible="visible"
      title="选择门禁"
      @ok="handleOk"
      @cancel="handleCancel"
      width="800px"
      :destroyOnClose="true"
      :fullscreen="fullscreen"
      @fullscreenChange="fullscreen = $event"
    >
      <!-- 顶部已收藏门禁组合展示区域 -->
      <div class="favorite-groups-top" v-if="favoriteGroups.length > 0">
        <div class="favorite-tags">
          <a-tag
            v-for="(group, index) in favoriteGroups"
            :key="index"
            closable
            @close.prevent="removeFavoriteGroup(group)"
            class="favorite-tag"
            @click="loadFavoriteGroup(group)"
          >
            {{ group.collectName }}
          </a-tag>
        </div>
      </div>

      <div class="person-select-container">
        <!-- 左侧树形组织结构 -->
        <div class="org-tree" v-if="!props.readonly">
          <div class="search-bar">
            <a-input-search v-model:value="searchText" placeholder="请输入" style="width: 100%" @search="onSearch" autocomplete="off" />
            <a-button v-if="searchMode" type="link" @click="clearSearch" class="clear-search">清除搜索</a-button>
          </div>

          <!-- 树内容区域 -->
          <div class="tree-content">
            <!-- 搜索结果 -->
            <div v-if="searchMode" class="search-results">
              <div class="search-title">搜索结果 ({{ searchResults.length }})</div>
              <div v-if="searchResults.length === 0" class="no-results">未找到匹配门禁</div>
              <div v-else>
                <div
                  v-for="person in searchResults"
                  :key="person.key"
                  class="person-item"
                  :class="{ 'selected-person': isPersonSelected(person.id) }"
                >
                  <div class="person-info">
                    <span>{{ person.title }}</span>
                    <a-checkbox :checked="isPersonSelected(person.key)" @change="(e) => toggleSelectPerson(person, e.target.checked)" />
                  </div>
                  <span class="person-dept">{{ person.namePath || '' }}</span>
                </div>
              </div>
            </div>

            <!-- 树组件 -->
            <a-tree
              v-if="!searchMode"
              :loadedKeys="loadedKeys"
              :tree-data="treeData"
              :load-data="onLoadData"
              class="person-tree"
              checkable
              :autoExpandParent="true"
              :checkedKeys="checkedKeys"
              @check="handleCheck"
            />
          </div>
        </div>

        <!-- 右侧已选门禁列表 -->
        <div class="selected-persons">
          <div class="selected-content">
            <div class="selected-title">已选门禁 ({{ selectedPersons.length }})</div>

            <div class="selected-list-container">
              <div v-if="selectedPersons.length === 0" class="no-selected">未选择门禁</div>
              <div v-else class="selected-list">
                <div v-for="person in selectedPersons" :key="person.key || person.id" class="person-item">
                  <div class="person-info">
                    <span>{{ person.title }}</span>
                    <a-button v-if="props.showRemoveButton" type="link" size="small" @click="toggleSelectPerson(person, false)"> 移除 </a-button>
                  </div>
                  <span class="person-dept">{{ person.namePath }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="fixed-bottom" v-if="showFavoriteBtn">
            <!-- 收藏门禁组合按钮 -->
            <div class="favorite-button-container">
              <a-button type="primary" class="favorite-button" @click="saveFavorites" :disabled="selectedPersons.length === 0">
                收藏门禁组合
              </a-button>
            </div>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- 新增的收藏名称输入弹窗 -->
    <a-modal v-model:visible="favoriteModalVisible" title="收藏门禁组合" @ok="handleFavoriteOk" :maskClosable="false" :zIndex="3000">
      <div>
        <BasicForm ref="formRef" :schemas="formSchemas" :showActionButtonGroup="false" @register="registerForm" />
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, watch, onMounted, computed } from 'vue';
  import type { DataNode } from 'ant-design-vue/es/tree';
  import {
    getChildrenNodes,
    searchPersons,
    getDoorAccessByParentId,
    getDoorAccessGroup,
    saveBuildCollect,
    deleteBuildCollect,
    getDoorAccessByGroupId,
  } from './doorAccess.api';
  import { useUserStore } from '/@/store/modules/user';
  import { message } from 'ant-design-vue';
  import { BasicForm, useForm, FormSchema } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';
  interface Group {
    id: number;
    deviceIds: string;
    collectName: string;
    collectUserCode: string;
  }
  //
  // 定义组件属性
  const props = defineProps({
    value: { type: Boolean, default: false },
    selected: {
      type: Array as () => {
        key: string;
        title: string;
        [key: string]: any;
      }[],
      default: () => [],
    },
    showRemoveButton: { type: Boolean, default: true },
    readonly: { type: Boolean, default: false },
    title: { type: String, default: '选择门禁' },
    multipleNum: { type: Number, default: 0 },
  });

  // 定义事件
  const emit = defineEmits(['update:value', 'update:selected', 'ok', 'cancel', 'exceed']);

  // 组件状态
  const visible = ref(false);
  const fullscreen = ref(false);
  const searchText = ref('');
  const searchMode = ref(false);
  const searchResults = ref<any[]>([]);
  const loadedKeys = ref<string[]>([]);
  const loadingKeys = ref<string[]>([]);
  const checkedKeys = ref<string[]>([]);
  const indeterminateKeys = ref<string[]>([]);
  const treeData = ref<DataNode[]>([]);
  const selectedPersons = ref<any[]>([]);
  const favoriteGroups = ref<any[]>([]);
  const { userInfo } = useUserStore();
  const favoriteModalVisible = ref(false);

  // 表单配置
  const [registerForm, { resetFields, validate }] = useForm({
    labelWidth: 120,
    schemas: [
      {
        field: 'collectName',
        label: '门禁组合名称',
        component: 'Input',
        required: true,
        componentProps: {
          placeholder: '15个字以内',
          maxlength: 15,
        },
        rules: [{ required: true, message: '请输入门禁组合名称', trigger: 'blur' }],
      },
    ],
    showActionButtonGroup: false,
  });

  const formSchemas: FormSchema[] = [
    {
      field: 'collectName',
      label: '门禁组合名称',
      component: 'Input',
      required: true,
      componentProps: {
        placeholder: '15个字以内',
        maxlength: 15,
      },
      rules: [{ required: true, message: '请输入门禁组合名称', trigger: 'blur' }],
    },
  ];

  onMounted(async () => {});
  //是否显示收藏按钮
  const showFavoriteBtn = computed(() => {
    return favoriteGroups.value.length < 5 && selectedPersons.value.length > 0;
  });
  // 初始化数据
  const initData = async () => {
    try {
      clearData();
      selectedPersons.value = [...props.selected];
      const orgData = await getChildrenNodes('');
      treeData.value = orgData;
      if (userInfo?.userName) {
        await getFavorites();
      }
    } catch (err) {
      console.error('加载组织架构数据失败:', err);
    }
  };

  // 清除数据
  const clearData = () => {
    treeData.value = [];
    loadedKeys.value = [];
    loadingKeys.value = [];
    selectedPersons.value = [];
    checkedKeys.value = [];
    indeterminateKeys.value = [];
    searchText.value = '';
    searchMode.value = false;
    searchResults.value = [];
  };

  // 监听visible属性变化
  watch(
    () => props.value,
    (val) => {
      visible.value = val;
      if (val) initData();
    },
    { immediate: true }
  );

  //获取节点下所有的门禁
  const getAllDoorAccess = async (key: string) => {
    const res = await getDoorAccessByParentId(key);
    return res;
  };

  // 收藏门禁组合
  const saveFavorites = async () => {
    if (selectedPersons.value.length === 0) {
      message.warning('请先选择门禁');
      return;
    }

    resetFields();
    favoriteModalVisible.value = true;
  };

  // 处理收藏确认
  const handleFavoriteOk = async () => {
    try {
      const values = await validate();
      console.log('values', values);
      const diviceIds = selectedPersons.value.map((item) => item.key).join(',');
      const parmas = {
        deviceIds: diviceIds,
        collectName: values.collectName,
        collectUserCode: userInfo?.userName as string,
      };
      console.log('确定收藏-------', parmas);
      await saveBuildCollect(parmas);
      getFavorites();
      message.success('收藏门禁组合成功');
      favoriteModalVisible.value = false;
    } catch (error) {
      // 表单验证未通过
      return;
    }
  };
  //获取收藏门禁组合列表
  const getFavorites = async () => {
    if (!userInfo?.userName) return;
    const res = await getDoorAccessGroup(userInfo.userName);
    console.log('收藏门禁组合列表', res);
    favoriteGroups.value = res;
  };
  // 从已选列表中移除人员
  const removeFromSelectedPersons = (item: any) => {
    selectedPersons.value = selectedPersons.value.filter((p) => p.key !== item.key);
    checkedKeys.value = checkedKeys.value.filter((key) => key !== item.key);
  };

  // 合并门禁并去重
  const mergeDoorsWithSelected = (doors: any[]) => {
    const newDoors = doors.filter((door) => !selectedPersons.value.some((selected) => selected.key === door.key));
    // 更新已选门禁
    selectedPersons.value = [...selectedPersons.value, ...newDoors];
    // 更新选中状态
    const newKeys = newDoors.map((door) => door.key);
    checkedKeys.value = [...checkedKeys.value, ...newKeys];
  };

  // 加载收藏的门禁组合
  const loadFavoriteGroup = async (group: Group) => {
    const { createConfirm } = useMessage();
    createConfirm({
      iconType: 'warning',
      title: '应用门禁组合',
      content: '确定要使用这个门禁组合吗?',
      onOk: async () => {
        try {
          const doors = await getDoorAccessByGroupId(group.id);
          console.log('加载收藏的门禁组合', doors);
          // 合并新的门禁，同时去重
          mergeDoorsWithSelected(doors);
        } catch (error) {
          console.log('加载收藏的门禁组合失败', error);
          message.error('加载门禁组合失败');
        }
      },
    });
  };

  // 删除收藏的门禁组合
  const removeFavoriteGroup = (group: Group) => {
    const { createConfirm } = useMessage();
    createConfirm({
      iconType: 'warning',
      title: '删除确认',
      content: '确定要删除这个门禁组合吗?',
      onOk: async () => {
        await deleteBuildCollect(group.id);
        await getFavorites();
        message.success('删除门禁组合成功');
      },
    });
  };

  // 处理节点加载
  const onLoadData = (treeNode: any) => {
    console.log('加载数据', treeNode);
    return new Promise<void>(async (resolve) => {
      const nodeKey = treeNode.dataRef.key;
      const isLoading = loadingKeys.value.includes(nodeKey);

      if (treeNode.dataRef.children || isLoading) {
        resolve();
        return;
      }

      try {
        loadingKeys.value.push(nodeKey);

        const nodeChildren = await getChildrenNodes(nodeKey);

        if (Array.isArray(nodeChildren) && nodeChildren.length > 0) {
          // 将获取到的子节点添加到当前节点
          treeNode.dataRef.children = nodeChildren.map((item) => ({ ...item, isLeaf: item.nodeType == 2 }));
          // 更新树数据以触发重新渲染
          treeData.value = [...treeData.value];
          // 将当前节点标记为已加载
          loadedKeys.value.push(nodeKey);
        } else {
          treeNode.dataRef.isLeaf = true;
          // 更新树数据以触发重新渲染
          treeData.value = [...treeData.value];
        }
      } catch (err) {
        console.error('加载子节点失败:', err);
      } finally {
        loadingKeys.value = loadingKeys.value.filter((key) => key !== nodeKey);
        resolve();
      }
    });
  };

  // 确认选择
  const handleOk = () => {
    emit('ok', selectedPersons.value);
    visible.value = false;
    emit('update:value', false);
    emit('update:selected', selectedPersons.value);
    clearData();
  };

  // 取消选择
  const handleCancel = () => {
    emit('cancel');
    visible.value = false;
    emit('update:value', false);
    clearData();
  };

  // 搜索方法
  const onSearch = async (value: string) => {
    if (!value.trim()) {
      clearSearch();
      return;
    }
    searchMode.value = true;
    try {
      const results = await searchPersons(value);
      searchResults.value = results.map((item) => ({ title: item.deviceName, key: item.deviceCode }));
    } catch (err) {
      console.error('搜索失败:', err);
      searchResults.value = [];
    }
  };

  // 清除搜索
  const clearSearch = () => {
    searchText.value = '';
    searchMode.value = false;
    searchResults.value = [];
  };

  // 检查人员是否被选中
  const isPersonSelected = (key: string) => {
    return selectedPersons.value.some((person) => person.key === key);
  };

  // 切换选择人员状态
  const toggleSelectPerson = (person: any, checked: boolean) => {
    if (checked) {
      if (props.multipleNum > 0 && selectedPersons.value.length >= props.multipleNum) {
        emit('exceed', true);
        return;
      }
      selectedPersons.value.push(person);
      checkedKeys.value.push(person.key);
    } else {
      selectedPersons.value = selectedPersons.value.filter((p) => p.key !== person.key);
      checkedKeys.value = checkedKeys.value.filter((key) => key !== person.key);
    }
  };

  // 处理复选框选中事件
  const handleCheck = async (keys: string[], { checked, node }) => {
    if (props.multipleNum > 0 && selectedPersons.value.length >= props.multipleNum) {
      emit('exceed', true);
      return;
    }
    console.log('点击复选', keys, checked, node);
    handleCheckAll(node, checked);
    checkedKeys.value = keys;
  };
  //处理是选中
  const handleCheckAll = async (node: any, checked: boolean) => {
    if (node.nodeType == 2) {
      if (checked) {
        addToSelectedPersons(node.dataRef);
      } else {
        removeFromSelectedPersons(node.dataRef);
      }
    }
    const allItems = await getAllDoorAccess(node.dataRef.key);
    console.log('allItems', allItems);
    if (checked) {
      for (const item of allItems) {
        if (!checkedKeys.value.includes(item.key)) {
          checkedKeys.value.push(item.key); // 将所有子节点的key添加到checkedKeys中
        }
        addToSelectedPersons(item);
      }
    } else {
      selectedPersons.value = selectedPersons.value.filter((person) => !allItems.some((item) => item.key === person.key));
      checkedKeys.value = checkedKeys.value.filter((key) => !allItems.some((item) => item.key === key));
    }
  };

  // 添加人员到已选列表，避免重复添加
  const addToSelectedPersons = (item: any) => {
    console.log('组织树限制==', props.multipleNum);
    if (props.multipleNum > 0 && selectedPersons.value.length >= props.multipleNum) {
      emit('exceed', true);
      return;
    }
    // 检查是否已经存在相同ID的人员
    if (!isPersonSelected(item.key)) {
      selectedPersons.value.push(item);
      // 同时更新选中的key
      if (!checkedKeys.value.includes(item.key)) {
        checkedKeys.value.push(item.key);
      }
    }
  };
</script>

<style lang="less" scoped>
  .person-select-container {
    display: flex;
    height: 500px;
    gap: 16px;

    .org-tree {
      flex: 1;
      border: 1px solid #d9d9d9;
      display: flex;
      flex-direction: column;

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
      }
    }

    .selected-persons {
      flex: 1;
      border: 1px solid #d9d9d9;
      padding: 0;
      display: flex;
      flex-direction: column;

      .selected-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        min-height: 0;
        overflow: hidden;

        .selected-title {
          font-weight: bold;
          margin: 8px;
          padding-bottom: 8px;
          border-bottom: 1px solid #f0f0f0;
        }

        .selected-list-container {
          flex: 1;
          overflow: hidden;
          position: relative;

          .no-selected {
            color: #999;
            font-style: italic;
            text-align: center;
            margin-top: 20px;
          }

          .selected-list {
            height: 100%;
            overflow-y: auto;
            padding: 0 8px;
          }
        }
      }

      .fixed-bottom {
        background-color: #f9f9f9;
        border-top: 1px solid #eee;
        padding: 8px;

        .favorite-button-container {
          margin: 8px 0;
          display: flex;
          justify-content: center;

          .favorite-button {
            width: 80%;
          }
        }
      }
    }
  }

  .search-title {
    font-weight: bold;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid #f0f0f0;
  }

  .no-results {
    color: #999;
    font-style: italic;
    text-align: center;
    margin-top: 20px;
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

  .favorite-group {
    padding: 8px;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .group-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .group-actions {
        display: flex;
        gap: 4px;

        .remove-button {
          color: #ff4d4f;

          &:hover {
            color: #ff7875;
          }
        }
      }
    }
  }

  .person-tree {
    :deep(.ant-tree-checkbox-checked .ant-tree-checkbox-inner) {
      background-color: #1890ff;
      border-color: #1890ff;
    }

    :deep(.ant-tree-switcher .ant-tree-switcher-icon) {
      color: #333;
      font-size: 12px;
    }
  }

  // 新增顶部收藏标签样式
  .favorite-groups-top {
    display: flex;
    align-items: center;
    // margin-bottom: 16px;
    background-color: #fff;
    padding: 10px 15px;
    border-radius: 4px;
    // border-bottom: 1px solid #e8e8e8;

    .favorite-tags {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      // gap: 8px;

      .favorite-tag {
        cursor: pointer;
        margin-right: 8px;
        padding: 6px 10px;
        font-size: 13px;
        background-color: #f5f5f5;
        border: 1px solid #d9d9d9;
        border-radius: 4px;

        &:hover {
          color: #1890ff;
          border-color: #1890ff;
          background-color: #e6f7ff;
        }
      }
    }
  }
</style>
