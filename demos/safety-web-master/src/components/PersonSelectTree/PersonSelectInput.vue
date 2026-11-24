<template>
  <div class="person-select-input">
    <!-- 统一使用标签输入框 -->
    <div class="tag-input-wrapper" :class="{ disabled: disabled }" @click="openPersonSelect">
      <div class="tag-input-content">
        <a-tag v-for="person in personList" :key="person.id" class="person-tag" :closable="!disabled" @close.stop="removePerson(person)">
          {{ $props.showNameNo ? `${person.name}${person.id}` : person.name }}
        </a-tag>
        <span v-if="maxItem > 0 && personList.length > maxItem">...</span>
        <span v-if="!personList || personList.length === 0" class="placeholder">{{ placeholder }}</span>
      </div>
      <div class="tag-input-icon">
        <UserOutlined />
      </div>
    </div>

    <!-- 人员选择树组件 -->
    <person-select-tree
      v-model:value="personSelectVisible"
      :selected="personList"
      :multiple="multiple"
      :multipleNum="multipleNum"
      :needEip="needEip"
      :isSameDept="isSameDept"
      @ok="handlePersonSelected"
      @cancel="handleCancel"
      @update:selected="handleUpdateSelected"
    />
  </div>
</template>

<script lang="ts" setup>
  import { ref, watch } from 'vue';
  import PersonSelectTree from './index.vue';
  import { UserOutlined } from '@ant-design/icons-vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { defHttp } from '/@/utils/http/axios';
  const { createMessage } = useMessage();

  // 定义人员对象类型
  interface PersonData {
    id: string;
    name: string;
    code?: string;
    namePath?: string;
    email?: string;
    [key: string]: any;
  }

  // 为PersonSelectTree定义的格式化后的人员类型
  interface TreePersonData {
    id: string;
    name: string;
    email?: string;
    code?: string;
    namePath?: string;
  }

  // 定义组件属性
  const props = defineProps({
    // 完整的人员对象列表
    personList: {
      type: Array as () => PersonData[],
      default: () => [],
    },
    // 是否需要eip账号
    needEip: {
      type: Boolean,
      default: false,
    },
    // 是否可选择多个人员
    multiple: {
      type: Boolean,
      default: false,
    },
    // 是否可选择多个人员
    multipleNum: {
      type: Number,
      default: 0,
    },
    // 输入框提示文字
    placeholder: {
      type: String,
      default: '请选择人员',
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false,
    },
    // 展示名字+员工号
    showNameNo: {
      type: Boolean,
      default: false,
    },
    // 最多展示多少个  之后的用...
    maxItem: {
      type: Number,
      default: 0,
    },
    isSameDept: {
      //是否只显示本部门及本部门一下
      type: Boolean,
      defalut: false,
    },
  });

  // 定义事件
  const emit = defineEmits(['update:personList', 'confirm', 'change']);

  // 组件内部状态
  const personSelectVisible = ref(false);

  // 处理打开对话框
  watch(
    () => personSelectVisible.value,
    (val) => {
      if (val) {
        console.log('打开人员选择器');
      } else {
        console.log('关闭人员选择器');
      }
    }
  );

  // 处理selected更新
  const handleUpdateSelected = (selected: TreePersonData[]) => {
    console.log('选中人员更新:', selected);
  };

  // 打开人员选择对话框
  const openPersonSelect = () => {
    if (props.disabled) return;
    if (personSelectVisible.value) return; // 如果已经打开则不再处理
    personSelectVisible.value = true;
  };

  // 移除单个人员
  const removePerson = (person: PersonData) => {
    if (props.disabled) return;
    // 从列表中过滤掉该人员，创建新数组
    const newList = props.personList.filter((p) => p.id !== person.id);
    // 更新父组件的值
    updateParentValue(newList);
    // 触发change事件，让父组件知道人员被移除
    emit('change', newList);
  };

  // 处理人员选择结果
  const handlePersonSelected = (persons: TreePersonData[]) => {
    // 单选模式下只保留第一项
    const newList = props.multiple ? persons : persons.slice(0, 1);
    if (props.isSameDept) {
      const perList = newList.map((item) => {
        return {
          ...item,
          empNo: item.id,
          empName: item.name,
        };
      });
      defHttp
        .post({
          url: '/safety/group/validPeople',
          params: { userList: JSON.stringify(perList) },
          headers: {
            'Content-Type': 'application/json',
          },
        })
        .then((res) => {
          console.log('选组织校验==', res);
          if (res.notBelongList == null && res.belongList == null && !res.accessEight) {
            createMessage.error('无法选择该员工');
          }
          if (res.notBelongList != null && res.notBelongList.length > 0) {
            const notBelongStr = res.notBelongList.map((item) => `${item.empName}${item.empNo}`).join(',');
            createMessage.info(`${notBelongStr}不属于该部门员工`);
          }
          if (res.belongList != null && res.belongList.length > 0) {
            // newList = res.belongList;
            const belongList = res.belongList.map((item) => {
              return {
                ...item,
                name: item.empName,
                id: item.empNo,
              };
            });
            // 更新父组件的值
            updateParentValue(belongList);

            // 触发确认选择事件
            emit('confirm', belongList);

            // 关闭选择对话框
            personSelectVisible.value = false;
          }
        })
        .catch((err) => {
          createMessage.error('选择失败');
        });
    } else {
      // 更新父组件的值
      updateParentValue(newList);

      // 触发确认选择事件
      emit('confirm', newList);

      // 关闭选择对话框
      personSelectVisible.value = false;
    }
  };

  // 更新父组件的值
  const updateParentValue = (persons: PersonData[]) => {
    // 更新人员列表
    emit('update:personList', persons);
  };

  // 处理取消选择
  const handleCancel = () => {
    personSelectVisible.value = false;
  };
</script>

<style lang="less" scoped>
  .person-select-input {
    width: 100%;
  }

  .tag-input-wrapper {
    display: flex;
    align-items: center;
    min-height: 32px;
    padding: 0 11px;
    border: 1px solid #d9d9d9;
    border-radius: 2px;
    background-color: #fff;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      border-color: #40a9ff;
    }

    &.disabled {
      background-color: #f5f5f5;
      cursor: not-allowed;

      &:hover {
        border-color: #d9d9d9;
      }
    }
  }

  .tag-input-content {
    flex: 1;
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    min-height: 30px;
    padding: 4px 0;
  }

  .tag-input-icon {
    margin-left: 8px;
    color: rgba(0, 0, 0, 0.45);
  }

  .person-tag {
    margin-right: 4px;
    // margin-bottom: 4px;
  }

  .placeholder {
    color: #bfbfbf;
    user-select: none;
  }
</style>
