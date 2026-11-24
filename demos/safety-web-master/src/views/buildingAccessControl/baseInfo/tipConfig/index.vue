<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <template #generalShow="{ record }">
        <a-switch :checked="record.showFlag" :checkedValue="'1'" :unCheckedValue="'0'" disabled />
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <TipModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  //ts语法
  import { ref, unref } from 'vue';
  import { cloneDeep } from 'lodash-es';
  import { BasicTable, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
  import TipModal from './TipModal.vue';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns } from './config';
  import { getListPage, Api, download } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { useRouter, useRoute } from 'vue-router';
  const route = useRoute();
  const router = useRouter();
  // 消息
  const { createMessage } = useMessage();
  const userStore: any = useUserStore().getUserInfo;
  const openUpload = ref<boolean>(false);
  const { hasPermission } = usePermission();
  const [registerModal, { openModal }] = useModal(); // 厂区弹出框

  // 列表页面公共参数、方法
  const { tableContext } = useListPage({
    designScope: 'goodsOut-list',
    tableProps: {
      title: '小助手提醒内容配置',
      api: getListPage,
      columns: columns,
      size: 'small',
      useSearchForm: false,
      showTableSetting: false,
      ellipsis: false,
      pagination: {
        pageSize: 15,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      fetchSetting: {
        // 传给后台的当前页字段
        pageField: 'pageNum',
        // 传给后台的每页显示多少条的字段
        sizeField: 'pageSize',
        // 接口返回表格数据的字段
        listField: 'dataList',
        // 接口返回表格总数的字段
        totalField: 'total',
      },
      actionColumn: {
        width: 180,
        title: '操作',
        //是否锁定操作列取值 right ,left,false
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
      showIndexColumn: true,
    },
  });

  //注册table数据
  const [registerTable, { getForm, reload, updateTableDataRecord }] = tableContext;

  /**
   * 列表操作栏
   */
  function getTableAction(record: EditRecordRow): ActionItem[] {
    return [
      {
        label: '编辑',
        ifShow: () => hasPermission('buildingAccess:baseInfo:tipConfig:edit'),
        onClick: handleEdit.bind(null, record),
      },
    ];
  }
  /**
   * 编辑事件
   */
  async function handleEdit(record: Recordable) {
    openModal(true, {
      title: '修改信息',
      isUpdate: true,
      record: record,
      showFooter: true,
    });
  }

  function hasAuth(code) {
    return hasPermission(code);
  }
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
