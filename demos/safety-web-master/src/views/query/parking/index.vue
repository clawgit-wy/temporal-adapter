<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="hasAuth('query:parking:export')"> 导出</a-button>
      </template>

      <template #form-factoryName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择厂区" allowClear>
          <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.fieldName">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="query-parking" setup>
  import { ref } from 'vue';
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { Api, list, factoryList } from './parking.api';
  import { columns, searchFormSchema } from './parking.data';
  // import dayjs from 'dayjs';
  const factoryLists = ref<Array>([]);
  const { hasPermission } = usePermission();

  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'query-parking',
    tableProps: {
      title: '停车场授权记录',
      api: list,
      columns: columns,
      ellipsis: false,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
      },
      showIndexColumn: true,
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
      showTableSetting: false,
      showActionColumn: false,
    },
    exportConfig: {
      url: Api.exportXls,
      name: '停车场授权记录',
    },
  });

  // 注册 ListTable
  const [registerTable] = tableContext;

  // 获取厂区列表
  async function getFactoryList() {
    const res = await factoryList({});
    factoryLists.value = res.factoryList;
    console.log(factoryLists, 'factoryListsfactoryLists');
  }
  getFactoryList();
  function hasAuth(code) {
    return hasPermission(code);
  }
</script>

<style lang="less">
  @import 'index';
</style>
