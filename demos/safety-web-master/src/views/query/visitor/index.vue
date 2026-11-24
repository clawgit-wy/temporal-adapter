<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="hasAuth('query:visitor:export')"> 导出</a-button>
      </template>
       <template #form-factoryName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择厂区" allowClear>
          <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
      <!--操作栏-->
      <!-- <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template> -->
    </BasicTable>
  </div>
</template>

<script lang="ts" name="device-binding" setup>
  import { ref } from 'vue';
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Api, list, factoryList } from './visitor.api';
  import { columns, searchFormSchema } from './visitor.data';
  import dayjs from 'dayjs';
  // import { useMessage } from '/@/hooks/web/useMessage';
  // const { createMessage } = useMessage();
  import { usePermission } from '/@/hooks/web/usePermission';
  const factoryLists = ref([]);
  const { hasPermission } = usePermission();
  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'query-visitor',
    tableProps: {
      title: '访客登记明细查询',
      api: list,
      columns: columns,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      ellipsis: true,
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['date', ['startTime', 'endTime'], 'YYYY-MM-DD']],
      },
      showIndexColumn: true,
      showTableSetting: false,
      showActionColumn: false,
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
    exportConfig: {
      url: Api.exportXls,
      name: '访客登记明细查询',
    },
  });

  // 注册 ListTable
  const [registerTable] = tableContext;

  // 获取厂区列表
  async function getFactoryList() {
    const res = await factoryList({});
    factoryLists.value = res.factoryList;
  }
  getFactoryList();
  function hasAuth(code) {
    return hasPermission(code);
  }
</script>

<style lang="less">
  @import 'index';
</style>
