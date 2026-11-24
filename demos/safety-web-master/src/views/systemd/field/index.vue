<template>
  <div class="systemd-field">
    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="1" tab="厂区名称">
        <BasicTable
          title="厂区名称"
          childrenColumnName="regionList"
          :columns="factoryColumnsField"
          :dataSource="dataFactoryList"
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
              <!-- <h3>厂区名称</h3> -->
              <div>
                <a-button
                  type="primary"
                  preIcon="ant-design:plus-outlined"
                  v-if="hasAuth('systemd:field:add')"
                  @click="onAdd({ fieldType: 'factory' })"
                  >添加</a-button
                >
              </div>
            </div>
          </template>
          <!--操作栏-->
          <template #action="{ record }">
            <TableAction :actions="getTableAction(record)" />
          </template>
          <!-- <template #fieldSort="{ record }"> -->
          <!-- <a-tag color="green">
          {{ record.id }}
        </a-tag> -->
          <!-- <div>
          <a-button type="text" :disabled="record.fieldSort === 0" @click="move({ record, fieldType: 'factory', type: 0 })"
            ><arrow-up-outlined />上移</a-button
          >
          <a-button type="text" :disabled="dataFactoryList.length === record.fieldSort + 1" @click="move({ record, fieldType: 'factory', type: 1 })"
            ><arrow-down-outlined />下移</a-button
          >
        </div>
      </template> -->
        </BasicTable>
      </a-tab-pane>
      <a-tab-pane key="2" tab="来访事由" force-render>
        <BasicTable
          title="来访事由"
          :columns="columnsField"
          :dataSource="dataVisit"
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
              <!-- <h3>来访事由</h3> -->
              <div>
                <a-button type="primary" preIcon="ant-design:plus-outlined" v-if="hasAuth('systemd:field:add')" @click="onAdd({ fieldType: 'visit' })"
                  >添加</a-button
                >
              </div>
            </div>
          </template>
          <!--操作栏-->
          <template #action="{ record }">
            <TableAction :actions="getTableAction(record)" />
          </template>
          <!-- <template #fieldSort="{ record }"> -->
          <!-- <a-tag color="green">
          {{ record.id }}
        </a-tag> -->
          <!-- <div>
          <a-button type="text" :disabled="record.fieldSort === 0" @click="move({ record, fieldType: 'visit', type: 0 })"
            ><arrow-up-outlined />上移</a-button
          >
          <a-button type="text" :disabled="dataVisit.length === record.fieldSort + 1" @click="move({ record, fieldType: 'visit', type: 1 })"
            ><arrow-down-outlined />下移</a-button
          >
        </div>
      </template> -->
        </BasicTable></a-tab-pane
      >
      <a-tab-pane v-for="item in moreTabs" :key="item.id" :tab="item.fieldName" force-render>
        <SecurityPatrol :fieldCode="item.fieldCode"></SecurityPatrol>
      </a-tab-pane>
    </a-tabs>

    <!-- <div class="systemd-field-box1"> </div>
    <div class="systemd-field-box2"> -->
    <!-- 厂区添加弹出框 -->
    <FieldModal @register="registerModal" :subType="subType" @success="reload" />
    <!-- </div> -->
  </div>
</template>

<script lang="ts" setup name="systemd-field">
  import { onMounted, ref } from 'vue';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  // import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { list, deleteSingle, getTabs } from './field.api';
  import { columns, factoryColumns } from './field.data';
  import FieldModal from './FieldModal.vue';
  import SecurityPatrol from './securityPatrol/index.vue';

  const [registerModal, { openModal }] = useModal(); // 厂区弹出框
  const columnsField = ref<any[]>(columns);
  const factoryColumnsField = ref<any[]>(factoryColumns);
  const dataVisit = ref<any[]>([]); // 来访事由数据
  const dataFactoryList = ref<any[]>([]); // 厂区数据
  const loading = ref(true);
  const { hasPermission } = usePermission();
  // const loadingVisit = ref<Boolean>(true);
  const activeKey = ref('1');

  // const { prefixCls } = useListPage({
  //   designScope: 'systemd-field',
  // });
  const subType = ref('');
  // 添加厂区
  function onAdd(record) {
    let str = '添加厂区';
    if (record.fieldType === 'visit') {
      str = '添加来访事由';
    } else {
      if (!record.parentId) {
        str = '添加厂区';
      } else {
        str = '添加区域';
      }
    }
    subType.value = str;
    openModal(true, {
      title: str,
      isUpdate: false,
      showFooter: true,
      record: {
        fieldType: record.fieldType,
      },
    });
  }

  // 添加区域
  function addArea(record) {
    const str = '添加区域';
    subType.value = str;
    openModal(true, {
      title: str,
      isUpdate: false,
      showFooter: true,
      record: {
        parentId: record.id,
        fieldType: record.level == 1 ? 'authType' : 'region',
      },
    });
  }

  // 添加来访事由
  // function onAddVisit() {
  //   visitModal.openModal(true, {
  //     title: '添加来访事由',
  //     isUpdate: false,
  //     showFooter: true,
  //     record: {},
  //   });
  // }

  // 编辑
  function onEdit(record) {
    let str = '修改厂区';
    if (record.fieldType === 'visit') {
      str = '修改来访事由';
    } else {
      if (!record.parentId) {
        str = '修改厂区';
      } else {
        str = '修改区域';
      }
    }
    openModal(true, {
      title: str,
      isUpdate: true,
      record: record,
      showFooter: true,
    });
  }

  function getTableAction(record): ActionItem[] {
    if ((!record.parentId || record.fieldType === 'factory' || record.fieldType === 'region') && record.level < 2) {
      return [
        { label: '添加', onClick: addArea.bind(null, record), auth: 'systemd:field:add' },
        { label: '编辑', onClick: onEdit.bind(null, record), auth: 'systemd:field:edit' },
        { label: '删除', onClick: onDelete.bind(null, record), auth: 'systemd:field:delete' },
      ];
    } else {
      return [
        { label: '编辑', onClick: onEdit.bind(null, record), auth: 'systemd:field:edit' },
        { label: '删除', onClick: onDelete.bind(null, record), auth: 'systemd:field:delete' },
      ];
    }
  }

  // 删除操作
  async function onDelete(record) {
    if (record) {
      await deleteSingle({ id: String(record.id) }, true);
      await getList();
    }
  }

  async function reload() {
    await getList();
  }
  const datas = ref();
  const formatList = (list, level = 0) => {
    list.forEach((item) => {
      item['level'] = level;
      if (!item.regionList || item.regionList.length == 0) {
        item.regionList = undefined;
      } else {
        formatList(item.regionList, level + 1);
      }
    });
  };
  async function getList() {
    setLoading({ value: true });
    const res = await list({});
    datas.value = res;
    dataVisit.value = res ? res.visitList : [];
    if (res && Array.isArray(res.factoryList)) {
      formatList(res.factoryList);
    }
    dataFactoryList.value = res ? res.factoryList : [];
    console.log(dataFactoryList.value, 'dataFactoryList.value');

    setLoading({ value: false });
  }

  function setLoading({ value }) {
    // if (fieldType === 'factory') {
    //   loadingFactory.value = value;
    // } else if (fieldType === 'visit') {
    //   loadingVisit.value = value;
    // } else {
    // loadingFactory.value = value;
    loading.value = value;
    // }
  }

  // 移动
  // async function move({ record, fieldType, type }) {
  //   loading({ fieldType, value: true });
  //   await moveList({ id: record.id, move: type });
  //   await getList();
  //   loading({ fieldType, value: false });
  // }

  onMounted(async () => {
    await getList();
    // loadingFactory.value = false;
    loading.value = false;
  });

  function hasAuth(code) {
    return hasPermission(code);
  }
  const moreTabs = ref([]);
  getTabs('').then((res) => {
    console.log('一级他不是=====', res);
    moreTabs.value = res;
  });
</script>

<style lang="less">
  @import 'index';
  .systemd-field {
    padding-left: 10px;
    padding-left: 20px;
    .ant-table-container {
      width: 80%;
    }
    .jeecg-basic-table-header__table-title-box {
      width: 88%;
    }
    .systemd-field-box2 {
      min-height: 500px;
    }
    .ant-tabs-tab-btn {
      font-size: 18px;
    }
  }
  .systemd-field-title {
    // display: flex;
    width: 100%;
    // justify-content: space-between;
    text-align: left;
    h3 {
      font-size: 18px;
    }
  }
</style>
