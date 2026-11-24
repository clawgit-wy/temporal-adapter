<template>
  <div :class="prefixCls">
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <div style="display: flex; flex-direction: column">
          <div style="display: flex">
            <a-button type="primary" @click="allReservedAuth()">一键保留授权</a-button>
            <a-button type="primary" danger style="margin-left: 15px" @click="allCancleAuth()">一键取消授权</a-button>
          </div>
          <span style="color: #508cee">注意：员工部门变动，请在7天内确认授权门禁是否保留，超时未处理自动取消授权</span>
        </div>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  //ts语法
  import { ref, unref } from 'vue';
  import { cloneDeep } from 'lodash-es';
  import { BasicTable, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns } from './config';
  import { getListPage, Api, batchAcrossConfirm } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { useRouter, useRoute } from 'vue-router';
  import { Modal } from 'ant-design-vue';
  const route = useRoute();
  const router = useRouter();
  // 消息
  const { createMessage } = useMessage();
  const userStore: any = useUserStore().getUserInfo;
  const openUpload = ref<boolean>(false);
  const { hasPermission } = usePermission();
  const [registerModal, { openModal }] = useModal(); // 厂区弹出框

  // 列表页面公共参数、方法
  const { prefixCls, tableContext } = useListPage({
    designScope: 'goodsOut-list',
    tableProps: {
      title: '调岗原对接门禁管理员权限确认',
      api: getListPage,
      columns: columns,
      rowKey: 'id',
      rowSelection: {
        type: 'checkbox',
      },
      size: 'small',
      useSearchForm: false,
      showTableSetting: false,
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
      },
      showIndexColumn: false,
    },
  });

  //注册table数据
  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  /**
   * 列表操作栏
   */
  function getTableAction(record: EditRecordRow): ActionItem[] {
    return [
      {
        label: '保留授权',
        onClick: handleSingleGoingAuth.bind(null, record),
      },
      {
        label: '取消授权',
        onClick: handlesingleCancleAuth.bind(null, record),
      },
    ];
  }
  /**
   * 编辑事件
   */
  function handleSingleGoingAuth(record: Recordable) {
    Modal.confirm({
      title: '提示',
      content: '是否继续保留授权？',
      async onOk() {
        await batchAcrossConfirm({ dealType: 1, idStr: record.id });
        createMessage.success('操作成功');
        reload();
      },
      onCancel() {},
    });
  }
  function handlesingleCancleAuth(record: Recordable) {
    Modal.confirm({
      title: '提示',
      content: '是否取消授权？',
      async onOk() {
        await batchAcrossConfirm({ dealType: 2, idStr: record.id });
        createMessage.success('操作成功');
        reload();
      },
      onCancel() {},
    });
  }

  function hasAuth(code) {
    return hasPermission(code);
  }

  function allReservedAuth() {
    console.log('一键保留授权==', selectedRowKeys.value);
    if (selectedRowKeys.value == null || selectedRowKeys.value.length <= 0) {
      createMessage.info('请选择操作数据');
      return;
    }
    Modal.confirm({
      title: '提示',
      content: '是否一键保留授权？',
      async onOk() {
        await batchAcrossConfirm({ dealType: 1, idStr: selectedRowKeys.value.join(',') });
        createMessage.success('操作成功');
        reload();
      },
      onCancel() {},
    });
  }
  function allCancleAuth() {
    console.log('一键取消授权==', selectedRowKeys.value);
    if (selectedRowKeys.value == null || selectedRowKeys.value.length <= 0) {
      createMessage.info('请选择操作数据');
      return;
    }
    Modal.confirm({
      title: '提示',
      content: '是否一键取消授权？',
      async onOk() {
        await batchAcrossConfirm({ dealType: 2, idStr: selectedRowKeys.value.join(',') });
        createMessage.success('操作成功');
        reload();
      },
      onCancel() {},
    });
  }
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
