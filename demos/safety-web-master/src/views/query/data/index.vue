<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" v-if="hasAuth('query:data:export')" @click="onExportXls"> 导出</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
      <template #form-factoryName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择厂区" allowClear>
          <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
      <template #form-sendState="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择下发结果" allowClear>
          <a-select-option v-for="item in sendStateList" :key="item.value" :value="item.value">{{ item.label }}</a-select-option>
        </a-select>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" setup name="query-data">
  import { Image as AImage } from 'ant-design-vue';
  import { ref } from 'vue';
  // import { Modal, Button } from 'ant-design-vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { usePermission } from '/@/hooks/web/usePermission';
  // import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Api, list, factoryList, send } from './data.api';
  import { columns, searchFormSchema, sendStateLists } from './data.data';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();

  const factoryLists = ref<Array>([]);
  const { hasPermission } = usePermission();
  const sendStateList = ref(sendStateLists);
  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'query-data',
    tableProps: {
      title: '数据下发日志列表数据',
      api: list,
      columns: columns,
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['date', ['startTime', 'endTime'], 'YYYY-MM-DD']],
      },
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
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
    },
    exportConfig: {
      url: Api.exportXls,
      name: '数据下发日志列表',
    },
  });
  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;
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
  /**
   * 操作栏
   */
  function getTableAction(record) {
    if (record.sendState === 2 || record.sendState === 4) {
      return [{ label: '重新下发', onClick: handleSend.bind(null, record) }];
    } else {
      return [];
    }
  }

  async function handleSend(record) {
    try {
      await send({ id: record.id });
      createMessage.success('下发成功');
      reload();
    } catch (err) {
      console.log(err, 'handleSend');
    }
  }
</script>

<style lang="less">
  @import 'index';
</style>
