<template>
  <div>
    <a-tabs v-model:activeKey="activeKey" @change="tabChange">
      <a-tab-pane v-for="item in tabs" :key="item.fieldCode" :tab="item.fieldName"></a-tab-pane>
    </a-tabs>
    <a-button
      type="primary"
      preIcon="ant-design:plus-outlined"
      v-if="hasAuth('systemd:field:add')"
      @click="onAdd({ fieldType: activeKey, title: currentTabName, fieldLevel: 3 })"
      >添加</a-button
    >
    <BasicTable
      style="margin-top: 10px"
      v-for="item in dataSource"
      :key="item.id"
      :title="item.fieldName"
      childrenColumnName="regionList"
      :columns="item.fieldCode == 'temporaryTransMana_cllx_0' ? columns : moveColumns"
      :dataSource="item.regionList"
      :minHeight="300"
      :showIndexColumn="false"
      :pagination="false"
      :loading="loading"
      :actionColumn="{
        width: 100,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      }"
    >
      <!--插槽:table标题-->
      <template #tableTitle>
        <div class="systemd-field-title">
          <p>{{ item.fieldName }}</p>
          <div>
            <a-button class="delete_btn" preIcon="ant-design:delete-outlined" v-if="hasAuth('systemd:field:delete')" @click="onDelete(item)"
              >删除</a-button
            >
            <a-button
              type="primary"
              preIcon="ant-design:plus-outlined"
              v-if="hasAuth('systemd:field:add')"
              @click="onAdd({ fieldType: item.fieldCode, title: item.fieldName, parentId: item.id, fieldLevel: 4 })"
              >添加</a-button
            >
          </div>
        </div>
      </template>
      <template #fieldSort="{ text, record, index, column }">
        <div>
          <a-button type="text" :disabled="record.fieldSort === 0" @click="move({ record, type: 0 })"> <arrow-up-outlined />上移 </a-button>
          <a-button type="text" :disabled="record.fieldSort === record.maxSort" @click="move({ record, type: 1 })">
            <arrow-down-outlined />下移
          </a-button>
        </div>
      </template>
      <!--操作栏-->
      <template #action="{ record, index }">
        <TableAction :actions="getTableAction(record, index, item)" />
      </template>
    </BasicTable>

    <FieldModal @register="registerModal" @success="reload" />
  </div>
</template>
<script setup>
  import { ref, watchEffect } from 'vue';
  import FieldModal from '../FieldModal.vue';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { moveColumns } from '../field.data';
  import { list, deleteSingle, getTabs, moveList } from '../field.api';
  import { useModal } from '/@/components/Modal';
  import { computed } from 'vue';
  import { ArrowDownOutlined, ArrowUpOutlined } from '@ant-design/icons-vue';
  const columnsField = ref(moveColumns);
  const { hasPermission } = usePermission();
  const [registerModal, { openModal }] = useModal();
  const activeKey = ref('validation');
  const props = defineProps({
    fieldCode: '',
  });
  function getTableAction(record, index = undefined, item) {
    let actionList = [
      { label: '添加', onClick: add.bind(null, { ...record, fieldLevel: 5 }, item), auth: 'systemd:field:add' },
      { label: '编辑', onClick: onEdit.bind(null, record, item), auth: 'systemd:field:edit' },
      { label: '删除', onClick: onDelete.bind(null, record, item), auth: 'systemd:field:delete' },
    ];
    if (record.fieldLevel && record.fieldLevel > 4) {
      actionList.shift();
    }
    if (item.fieldType == 'buildingAccessFactoryManage' || item.fieldType == 'secretLevel') {
      actionList.shift();
    }
    return actionList;
  }
  function recordFileType(item) {
    if (item.fieldType == 'buildingAccessFactoryManage' || item.fieldType == 'secretLevel') {
      return 'buildingAccessFactoryManage_';
    }
    return item.fieldCode;
  }
  function onAdd(record) {
    console.log('添加======11==', record);
    let str = '添加';
    // if (record.title) {
    //     str = `添加${record.title}`
    // }
    openModal(true, {
      title: str,
      isUpdate: false,
      showFooter: true,
      record,
    });
  }
  function hasAuth(code) {
    return hasPermission(code);
  }
  const reload = () => {
    getList();
  };
  const loading = ref(false);
  const dataFactoryList = ref([]);

  const currentTabName = computed(() => {
    return tabs.value.find((item) => item.fieldCode == activeKey.value).fieldName;
  });

  const dataSource = ref([]);
  function add(record, item) {
    console.log('添加======22==', record, item);
    const str = '添加';
    openModal(true, {
      title: str,
      isUpdate: false,
      showFooter: true,
      record: {
        fieldLevel: record.fieldLevel,
        parentId: record.id,
        fieldType: item.fieldCode,
      },
    });
  }
  // 删除操作
  async function onDelete(record) {
    if (record) {
      await deleteSingle({ id: String(record.id) }, true);
      await getList();
      reload();
    }
  }
  // 编辑
  function onEdit(record) {
    let str = '编辑';

    openModal(true, {
      title: str,
      isUpdate: true,
      record: record,
      showFooter: true,
    });
  }
  const tabChange = () => {
    getList();
  };
  const tabs = ref([]);
  watchEffect(() => {
    getTabs({ fieldType: props.fieldCode }).then((res) => {
      activeKey.value = res[0].fieldCode;
      tabs.value = res;
      getList();
    });
  });
  const columns = [
    {
      title: '名称',
      dataIndex: 'fieldName',
      width: 80,
      align: 'left',
    },
    {
      title: '是否临时运输车辆',
      dataIndex: 'fieldValue',
      width: 80,
      align: 'left',
    },
    {
      title: '是否填写环保清单',
      dataIndex: 'envirInfo',
      width: 80,
      align: 'left',
    },
    {
      title: '移动',
      dataIndex: 'fieldSort',
      width: 100,
      slots: { customRender: 'fieldSort' },
    },
  ];
  const setChildren = (list) => {
    list.forEach((item) => {
      if (!item.regionList || item.regionList.length == 0) {
        item.regionList = undefined;
      } else {
        setChildren(item.regionList);
      }
    });
  };
  const getList = () => {
    getTabs({ fieldType: activeKey.value }).then((res) => {
      setChildren(res);
      dataSource.value = res;
    });
  };
  const move = ({ record, type }) => {
    moveList({ id: record.id, move: type }).then(() => reload());
  };
</script>
<style lang="less">
  .systemd-field-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .delete_btn {
    margin-right: 10px;
  }
</style>
