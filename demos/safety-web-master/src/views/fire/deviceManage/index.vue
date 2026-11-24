<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <template #toolbar>

        <a-button type="primary" preIcon="ant-design:import-outlined" v-if="hasPermission('buildingAccess:whiteManage:export')" @click="onExportXls()"
          >导出</a-button
        >
      </template>
      <!-- <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template> -->
    </BasicTable>
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  import { ref, unref } from 'vue';
  import { cloneDeep } from 'lodash-es';
  import { BasicTable, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns } from './config';
  import { getListPage, Api } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { Loading, useLoading } from '/@/components/Loading';
  import { useRouter, useRoute } from 'vue-router';
  const route = useRoute();
  const router = useRouter();
  const wrapEl = ref<ElRef>(null);
  const [openWrapLoading, closeWrapLoading] = useLoading({
    target: wrapEl,
    props: {
      tip: '加载中...',
      absolute: true,
    },
  });
  // 消息
  const { createMessage } = useMessage();
  const userStore: any = useUserStore().getUserInfo;
  const openUpload = ref<boolean>(false);
  const { hasPermission } = usePermission();
  const [registerModal, { openModal }] = useModal(); // 厂区弹出框

  // 列表页面公共参数、方法
  const { onExportXls, onImportXls, tableContext } = useListPage({
    designScope: 'goodsOut-list',
    tableProps: {
      title: '',
      api: getListPage,
      columns: columns,
      size: 'small',
      useSearchForm: false,
      showTableSetting: false,
      showActionColumn: false,
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
      showIndexColumn: true,
    },
    exportConfig: {
      url: Api.exportData,
      name: '监控设备管理',
    },
    importConfig: {
      url: Api.exportData,
      success: () => {
        console.log('上传成功回调');
        openUpload.value = false;
        closeWrapLoading();
        reload();
      },
    },
  });

  //注册table数据
  const [registerTable, { getForm, reload, updateTableDataRecord }] = tableContext;
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
