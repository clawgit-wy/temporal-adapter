<template>
  <div class="list-container">
    <SelectAccessFormItem
      v-for="(item, index) in list"
      :key="item.uuid"
      :item="item"
      :open="open"
      :isDetail="isDetail"
      :showRemove="!isView"
      @remove="removeAction(item, index)"
      @copy="copyAction(item, index)"
      @selectedAccess="(doors) => selectedAccess(doors, item, index)"
      @detailLoaded="(detail) => updateItemDetail(detail, index)"
      @selectedType="(type) => updateItemType(type, index)"
      @selectedDept="(dept) => updateItemDept(dept, index)"
      @selectedPeople="(people) => updateItemPeople(people, index)"
      @selectedDate="(date) => updateItemDate(date, index)"
      @selectedDockingDept="(dockDept) => updateItemDockDept(dockDept, index)"
      ref="formRefs"
    />
    <div class="add-button" v-if="!isView">
      <a-button type="primary" @click="addAccess">增加</a-button>
    </div>
    <!-- <DoorAccessSelect v-model:value="showDoorSelect" placeholder="请选择门禁" @ok="selectedDoorAction" /> -->

    <ScmModal @register="registerScmModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import SelectAccessFormItem from './SelectAccessFormItem.vue';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  import { useAccessRequestForm } from '../accessRequestProcess/form/useAccessRequest';
  import { buildUUID } from '/@/utils/uuid';
  import ScmModal from './scm.modal.vue';
  import { useModal } from '/@/components/Modal';

  const { isView } = useAccessRequestForm();
  interface Item {
    key: string;
    title: string;
    [key: string]: any;
  }

  // const showDoorSelect = ref(false);
  const formRefs = ref([]);

  // 验证所有表单
  const validateAll = async () => {
    try {
      const res = await Promise.all(formRefs.value.map((formRef: any) => formRef.validate()));

      console.log('历史结果-----', res);
      return res;
    } catch (error) {
      console.log('验证失败88888888', error);
      return false;
    }
  };

  // 定义事件
  const emit = defineEmits([
    'add',
    'copy',
    'remove',
    'selectedAccess',
    'update:list',
    'update',
    'selectedType',
    'selectedDept',
    'selectedPeople',
    'selectedDate',
    'selectedDockingDept',
  ]);
  // 属性定义
  const props = defineProps({
    list: {
      type: Array,
      default: () => [],
    },
    open: {
      type: Number,
      default: 0,
    },
    isDetail: {
      type: Boolean,
      default: false,
    },
  });
  // 增加
  const addAction = (item: any, index: number) => {
    console.log('增加', item, index);
    emit('add', item, index, props.open);
  };

  // 复制
  const copyAction = (item: any, index: number) => {
    console.log('复制', item, index);
    emit('copy', JSON.parse(JSON.stringify({ ...item, uuid: buildUUID(), isNewAdd: false })), index, props.open);
  };

  // 删除
  const removeAction = (item: any, index: number) => {
    console.log('删除', item, index);
    emit('remove', item, index, props.open);
  };

  // 选择门禁
  const selectedAccess = (doors: any, item: any, index: number) => {
    console.log('选择门禁', item, index);
    emit('selectedAccess', doors, item, index, props.open);
  };

  // 新增门禁
  //新增/编辑弹窗
  const [registerScmModal, { openModal: openModal }] = useModal();
  function addAccess() {
    // showDoorSelect.value = true;
    openModal(true, {
      record: {},
      isUpdate: false,
      showFooter: true,
    });
  }

  // 更新门禁项详情
  function updateItemDetail(detail: any, index: number) {
    console.log('更新门禁项详情', detail, index);
    emit('update', { ...detail, uuid: buildUUID() }, index, props.open);
  }
  // 更新类型
  function updateItemType(type: any, index: number) {
    console.log('更新类型', type, index);
    emit('selectedType', type, index, props.open);
  }
  // 更新部门
  function updateItemDept(dept: any, index: number) {
    console.log('更新部门', dept, index);
    emit('selectedDept', dept, index, props.open);
  }
  // 更新人员
  function updateItemPeople(people: any, index: number) {
    console.log('更新人员', people, index);
    emit('selectedPeople', people, index, props.open);
  }
  // 更新日期
  function updateItemDate(date: any, index: number) {
    console.log('更新日期', date, index);
    emit('selectedDate', date, index, props.open);
  }
  // 更新对接部门
  function updateItemDockDept(dockDept: any, index: number) {
    console.log('更新对接部门', dockDept, index);
    emit('selectedDockingDept', dockDept, index);
  }

  // 选中门禁
  function selectedDoorAction(doors) {
    // doors = doors.map((door) => {
    //   return {
    //     ...door,
    //     deviceCode: door.key,
    //     deviceName: door.title,
    //   };
    // });
    console.log('选中门禁', doors);
    // if (isView.value) return; // 查看模式不允许操作
    let updatedListTemp = null;
    if (props.open == 1) {
      updatedListTemp = doors.filter(
        (door) => props.list == null || props.list.length == 0 || !props.list.some((item) => item.deviceCode === door.deviceCode)
      );
    } else {
      const tempName = doors.map((door) => door.deviceName).join(',');
      const tempCode = doors.map((door) => door.deviceCode).join(',');
      const empowerAreaCode = doors[0].empowerAreaCode;
      const empowerAreaName = doors[0].empowerAreaName;
      const empowerType = doors[0].empowerType;
      console.log('转换后的数据==', tempName, tempCode);
      updatedListTemp = [{ deviceName: tempName, deviceCode: tempCode, empowerAreaCode, empowerAreaName, empowerType }];
    }
    console.log('选中门禁--------------', updatedListTemp);
    const updatedList = (props.list == null || props.list.length == 0 ? [...updatedListTemp] : [...props.list, ...updatedListTemp]).map((item) => {
      return {
        ...item,
        isNewAdd: true,
      };
    });
    console.log('最终选中门禁--------------', updatedList);
    emit('update:list', updatedList);
  }

  /**
   * 成功回调
   * @param {number} isUpdate 操作类型：0 无操作 1 新增/新增加编辑 2 编辑
   * @param {object} values 编辑的数据
   */
  function handleSuccess({ values }) {
    console.log('弹窗回调', values);
    if (values.doorAccessSelection != null && values.doorAccessSelection.length > 0) {
      const tempDoorData = values.doorAccessSelection.map((item) => {
        return {
          ...item,
          empowerAreaCode: values.empowerAreaCode,
          empowerAreaName: values.empowerAreaName,
          empowerType: values.empowerType,
        };
      });
      selectedDoorAction(tempDoorData);
    }
  }
  // 暴露验证方法
  defineExpose({
    validateAll,
  });
</script>

<style lang="less" scoped>
  .list-container {
    .add-button {
      display: flex;
      .ant-btn {
        flex: 1;
        color: rgba(0, 0, 0, 0.85);
        border-color: #d9d9d9;
        background: #fff;
        border-style: dashed;
      }
    }
  }
</style>
