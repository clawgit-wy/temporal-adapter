<template>
  <div class="person-select-tree">
    <a-modal
      v-model:visible="visible"
      title="选择人员"
      @ok="handleOk"
      @cancel="handleCancel"
      width="800px"
      :destroyOnClose="true"
      :keyboard="true"
      :getContainer="false"
      :maskClosable="false"
    >
      <div class="person-select-container">
        <!-- 左侧树形组织结构 -->
        <div class="org-tree" v-if="!props.readonly">
          <div class="search-bar">
            <a-input-search v-model:value="searchText" placeholder="姓名/员工号" style="width: 100%" @search="onSearch" autocomplete="off" />
            <a-button v-if="searchMode" type="link" @click="clearSearch" class="clear-search"> 清除搜索 </a-button>
          </div>

          <!-- 树内容区域，可滚动 -->
          <div class="tree-content">
            <!-- 显示搜索结果 -->
            <div v-if="searchMode" class="search-results">
              <div class="search-title">搜索结果 ({{ searchResults.length }})</div>
              <div v-if="searchResults.length === 0" class="no-results"> 未找到匹配人员 </div>
              <div v-else>
                <div v-for="person in searchResults" :key="person.id" class="person-item" :class="{ 'selected-person': isPersonSelected(person.id) }">
                  <div class="person-info">
                    <span>{{ person.name }} ({{ person.id }})</span>
                    <a-checkbox :checked="isPersonSelected(person.id)" @change="(e) => toggleSelectPerson(person, e.target.checked)" />
                  </div>
                  <span class="person-dept">{{ person.namePath || '' }}</span>
                </div>
              </div>
            </div>

            <!-- 树组件，搜索时隐藏 -->
            <a-tree
              v-if="!searchMode"
              v-model:expandedKeys="expandedKeys"
              :loadedKeys="loadedKeys"
              :selectedKeys="selectedKeys"
              :tree-data="treeData"
              :load-data="onLoadData"
              class="person-tree"
              @select="handleSelect"
            >
              <template #title="{ title, key, nodeType }">
                <div
                  class="node-title"
                  :class="{
                    'selected-node': isPersonSelected(key),
                    'person-node': nodeType === 2,
                    'org-node': nodeType !== 2,
                  }"
                >
                  <div class="node-content">
                    <a-checkbox v-if="nodeType === 2" :checked="isPersonSelected(key)" class="node-checkbox" />
                    <span>{{ title }}</span>
                    <span v-if="nodeType === 2">({{ key }})</span>
                  </div>
                </div>
              </template>
              <template #switcherIcon="{ data, expanded }">
                <span class="switcher-wrapper">
                  <span v-if="loadingKeys.includes(data.key)" class="loading-icon">
                    <a-spin size="small" />
                  </span>
                  <span v-else class="default-arrow">
                    <CaretDownOutlined v-if="expanded" />
                    <CaretRightOutlined v-else />
                  </span>
                </span>
              </template>
            </a-tree>
          </div>
        </div>

        <!-- 右侧已选人员列表 -->
        <div class="selected-persons">
          <div class="selected-title">已选人员 ({{ selectedPersons.length }})</div>
          <div v-if="selectedPersons.length === 0" class="no-selected"> 未选择人员 </div>
          <div v-else>
            <div v-for="person in selectedPersons" :key="person.id" class="person-item">
              <div class="person-info">
                <span>{{ person.name }} ({{ person.id }})</span>
                <a-button v-if="props.showRemoveButton" type="link" size="small" class="remove-button" @click="toggleSelectPerson(person, false)">
                  移除
                </a-button>
              </div>
              <span class="person-dept">{{ person.namePath }}</span>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, watch, computed } from 'vue';
  import type { DataNode } from 'ant-design-vue/es/tree';
  import { getChildrenNodes, searchPersons } from './personSelect.api';
  import { CaretDownOutlined, CaretRightOutlined } from '@ant-design/icons-vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  const $message = useMessage();

  // 定义组件属性
  const props = defineProps({
    value: {
      type: Boolean,
      default: false,
    },
    needEip: {
      type: Boolean,
      default: false,
    },
    multiple: {
      type: Boolean,
      default: true,
    },
    // 是否可选择多个人员
    multipleNum: {
      type: Number,
      default: 0,
    },
    selected: {
      type: Array as () => {
        id: string;
        name: string;
        email?: string;
        namePath: string;
      }[],
      default: () => [],
    },
    showRemoveButton: {
      type: Boolean,
      default: false,
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    isSameDept: {
      //是否只显示本部门及本部门一下
      type: Boolean,
      defalut: false,
    },
  });

  // 定义事件
  const emit = defineEmits(['update:value', 'update:selected', 'ok', 'cancel']);

  // 组件内部状态
  const visible = ref(false);
  const searchText = ref('');
  const expandedKeys = ref<string[]>([]);
  const selectedPersons = ref<any[]>([]);
  const searchMode = ref(false);
  const searchResults = ref<any[]>([]);
  const loadedKeys = ref<string[]>([]); // 已加载的节点
  const loadingKeys = ref<string[]>([]); // 正在加载中的节点

  // 树形数据
  const treeData = ref<DataNode[]>([]);

  // 检查人员是否已被选中
  const isPersonSelected = (personId: string): boolean => {
    return selectedPersons.value.some((person) => person.id === personId);
  };

  // 切换选择/取消选择人员
  const toggleSelectPerson = (person: any, checked: boolean) => {
    if (checked) {
      // 如果是单选模式，先清空之前的选择
      if (!props.multiple) {
        selectedPersons.value = [];
        // selectedKeys.value = [];
      }
      // 如果已经选中了，就不要重复添加
      if (!isPersonSelected(person.id)) {
        if (props.multiple && props.multipleNum > 0 && selectedPersons.value.length >= props.multipleNum) {
          $message.createMessage.warn(`最多只能选择${props.multipleNum}人`);
          return;
        }
        if (props.needEip && !person.email) {
          $message.createMessage.warn(`该人员没有eip账户，不可选！`);
          return;
        }
        // 添加到已选列表
        selectedPersons.value.push({
          id: person.id,
          name: person.name,
          namePath: person.namePath || '',
          email: person.email || '',
          orgCodePath: person.orgCodePath || '',
        });

        // 更新选中状态
        // selectedKeys.value = [...selectedKeys.value, person.id];
      }
    } else {
      // 从已选列表中移除
      selectedPersons.value = selectedPersons.value.filter((p) => p.id !== person.id);
    }
  };

  // 初始化数据
  const initData = async () => {
    try {
      // 彻底清空前一次的数据
      clearData();

      // 设置默认选中的人员
      selectedPersons.value = [...props.selected];

      // 获取组织架构数据
      const orgData = await getChildrenNodes('');

      // 处理返回的数据，确保格式正确
      if (Array.isArray(orgData) && orgData.length > 0) {
        // 递归处理节点数据
        const processedData = processTreeNodes(orgData);
        treeData.value = processedData;
      } else {
        console.warn('API返回的组织数据为空或格式不正确');
      }
    } catch (err) {
      console.error('加载组织架构数据失败:', err);
    }
  };

  // 清除数据
  const clearData = () => {
    treeData.value = [];
    expandedKeys.value = [];
    loadedKeys.value = [];
    loadingKeys.value = [];
    selectedPersons.value = [];
    searchText.value = '';
    searchMode.value = false;
    searchResults.value = [];
  };
  const selectedKeys = computed(() => {
    return selectedPersons.value.map((person) => person.id);
  });

  // 监听visible属性变化
  watch(
    () => props.value,
    (val) => {
      visible.value = val;
      if (val) {
        // 打开弹框时初始化数据
        initData();
      }
    },
    { immediate: false }
  );

  // 递归处理树节点数据
  const processTreeNodes = (nodes: any[]): any[] => {
    return nodes.map((node) => {
      // 处理子节点(如果有)
      const hasChildren = node.children && Array.isArray(node.children) && node.children.length > 0;
      const processedChildren = hasChildren ? processTreeNodes(node.children) : [];

      // 判断是否为人员节点(nodeType为2表示人员)
      const isPerson = node.nodeType === 2;

      // 创建处理后的节点
      return {
        ...node, // 保留原有属性
        isLeaf: isPerson, // 人员节点设为叶子节点，不显示箭头
        children: processedChildren.length > 0 ? processedChildren : null, // 有子节点时设置，否则设为null但不是undefined
        // 确保parent属性是对象类型，如果存在的话
        parent: typeof node.parent === 'string' ? { key: node.parent } : node.parent,
      };
    });
  };

  // 处理选择
  const handleSelect = (_: any[], info: any) => {
    const { node } = info;

    // 如果点击的是组织节点，展开/折叠该节点
    if (node.dataRef && node.dataRef.nodeType !== 2) {
      // 获取当前展开状态
      const isExpanded = expandedKeys.value.includes(node.key);

      if (isExpanded) {
        // 如果已展开，则折叠
        expandedKeys.value = expandedKeys.value.filter((key) => key !== node.key);
      } else {
        // 如果未展开，则展开
        expandedKeys.value = [...expandedKeys.value, node.key];
      }
      return;
    }

    // 处理人员节点的选择
    if (node.dataRef && node.dataRef.nodeType === 2) {
      // 获取当前节点是否已选中
      const isSelected = isPersonSelected(node.key);

      // 使用toggleSelectPerson处理选择
      toggleSelectPerson(
        {
          id: node.key,
          name: node.title,
          namePath: node.dataRef.namePath,
          email: node.dataRef.dataId,
          orgCodePath: node.dataRef.codePath,
        },
        !isSelected
      );
    }
  };

  // 处理搜索
  const onSearch = async (value: string) => {
    if (!value.trim()) {
      searchMode.value = false;
      return;
    }

    try {
      // 调用搜索API
      const response = await searchPersons(value);

      console.log('搜索返回数据:', response);

      // 检查返回数据格式
      if (!response || !Array.isArray(response)) {
        console.warn('搜索返回数据格式不正确:', response);
        searchResults.value = [];
        searchMode.value = true;
        return;
      }

      // 处理搜索结果
      searchResults.value = response.map((person: any) => ({
        id: person.empNo?.toString() || '',
        name: person.empName || '',
        namePath: person.orgNamePath || '',
        orgCodePath: person.orgCodePath || '',
        email: person.email || '',
        nodeType: 2,
        isLeaf: true,
      }));

      console.log('处理后的搜索结果:', searchResults.value);
      searchMode.value = true;
    } catch (err) {
      console.error('搜索失败:', err);
      searchResults.value = [];
      searchMode.value = true;
    }
  };

  // 清除搜索并恢复树
  const clearSearch = async () => {
    searchText.value = '';
    searchMode.value = false;
    searchResults.value = [];

    // 如果树节点数据丢失，重新加载树数据
    if (treeData.value.length === 0) {
      try {
        const orgData = await getChildrenNodes('');

        if (Array.isArray(orgData) && orgData.length > 0) {
          treeData.value = processTreeNodes(orgData);
          expandedKeys.value = [];
        }
      } catch (err) {
        console.error('重新加载组织架构数据失败:', err);
      }
    }
  };

  // 确认选择
  const handleOk = () => {
    if(selectedPersons.value.length === 0){
      $message.createMessage.warn('请先选择人员');
      return;
    }
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

  // 添加 onLoadData 函数
  const onLoadData = (treeNode: any) => {
    return new Promise<void>(async (resolve) => {
      // 检查是否正在加载中，如果正在加载则不再发起请求
      const nodeKey = treeNode.dataRef.key;
      const isLoading = loadingKeys.value.includes(nodeKey);

      // 如果已经有子节点数据或正在加载，则直接返回
      if (treeNode.dataRef.children || isLoading) {
        resolve();
        return;
      }

      try {
        // 标记为正在加载
        loadingKeys.value.push(nodeKey);

        // 加载子节点数据
        const nodeChildren = await getChildrenNodes(nodeKey);

        // 处理返回的数据
        if (Array.isArray(nodeChildren) && nodeChildren.length > 0) {
          // 为子节点添加正确的parent引用
          const childrenWithParent = nodeChildren.map((child) => ({
            ...child,
            parent: { key: nodeKey },
          }));

          // 有子节点，递归处理并设置
          const processedChildren = processTreeNodes(childrenWithParent);
          treeNode.dataRef.children = processedChildren;
        } else {
          // 如果没有子节点，设置为叶子节点
          treeNode.dataRef.isLeaf = true;
        }

        // 强制更新树视图
        treeData.value = [...treeData.value];
      } catch (err) {
        console.error('加载节点数据失败:', err);
      } finally {
        // 移除加载状态
        loadingKeys.value = loadingKeys.value.filter((key) => key !== nodeKey);
        resolve();
      }
    });
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
      padding: 8px;
      overflow: auto;
    }
  }

  .search-title,
  .selected-title {
    font-weight: bold;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid #f0f0f0;
  }

  .no-results,
  .no-selected {
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

  .person-tree {
    :deep(.ant-tree-checkbox-checked .ant-tree-checkbox-inner) {
      background-color: #1890ff;
      border-color: #1890ff;
    }

    :deep(.ant-tree-node-selected) {
      background-color: transparent !important;
    }

    :deep(.ant-tree-node-content-wrapper.ant-tree-node-selected) {
      background-color: transparent;
    }
  }

  .node-title {
    display: flex;
    align-items: center;
    width: 100%;

    .node-content {
      display: flex;
      align-items: center;
      flex: 1;
    }

    .node-checkbox {
      margin-right: 8px;
    }

    &.selected-node {
      color: #1890ff;
      font-weight: 500;
    }

    &.person-node {
      color: #333;
      cursor: pointer;

      &:hover {
        color: #1890ff;
      }
    }

    &.org-node {
      color: #666;
      font-weight: 500;
    }
  }

  .switcher-wrapper {
    position: relative;
    display: inline-flex;
    align-items: center;

    .loading-icon {
      position: absolute;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .default-arrow {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 14px;
      height: 14px;
      font-size: 12px;
      color: rgba(0, 0, 0, 0.65);
    }
  }
</style>
