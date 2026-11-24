<template>
  <div class="systemd-noticemessage">
    <BasicTable title="提示信息" @register="registerTable"> </BasicTable>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { useRouter } from 'vue-router';
  import { columns } from './data';

  // 列表页面公共参数、方法
  const { tableContext, onExportXls } = useListPage({
    designScope: 'checkCar-list',
    tableProps: {
      title: '授权区域设备管理',
      api: undefined,
      columns: columns,
      useSearchForm: false,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      showIndexColumn: true,
      ellipsis: true,
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
    },
  });
  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;
  const { hasPermission } = usePermission();
  function hasAuth(code) {
    return hasPermission(code);
  }

  const router = useRouter();
  function onAdd() {
    router.push({
      name: 'initiate',
    });
  }
</script>

<style lang="less" scoped></style>
