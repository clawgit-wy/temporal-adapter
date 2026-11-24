<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="hasAuth('query:cardrefund:export')"> 导出</a-button>
      </template>
       <template #form-factoryName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择厂区" allowClear>
          <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.fieldName">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
      <template #form-day="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <yinput v-model="model[field]"></yinput>
      </template>
      <!--操作栏-->
      <!-- <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template> -->
    </BasicTable>
    
  </div>
</template>

<script lang="ts" name="device-binding" setup>
  import { ref, reactive } from 'vue';
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Api, list, factoryList } from './cardrefund.api';
  import { columns, searchFormSchema } from './cardrefund.data';
  import { usePermission } from '/@/hooks/web/usePermission';
  import yinput from './yinput.vue';
  import dayjs from 'dayjs';
  const factoryLists = ref([]);
  const { hasPermission } = usePermission();
  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'query-visitor',
    tableProps: {
      title: '访客未退卡明细查询',
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
        fieldMapToTime: [['date', ['hairpinStartTime', 'hairpinEndTime'], 'YYYY-MM-DD']],
        fieldMapToNumber: [['day', ['loseStartDay', 'loseEndDay']]],
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
      // beforeFetch: (params) => {
      //   if (params.day) {
      //     if(Array.isArray(params.day)){
      //       params.loseStartDay = params.day[0];
      //       params.loseEndDay = params.day[1];
      //       delete params.day;
      //     } else {
      //       params.loseStartDay = params.day.split(",")[0];
      //       params.loseEndDay = params.day.split(",")[1];
      //       delete params.day;
      //     }
      //   }
      //   if (params.date) {
      //     params.hairpinStartTime = dayjs(params.date[0]).format("YYYY-MM-DD");
      //     params.hairpinEndTime = dayjs(params.date[1]).format("YYYY-MM-DD")
      //   }
      //   delete params.date;
      //   console.log(params);
      //   return params;
      // },
    },
    exportConfig: {
      url: Api.exportXls,
      name: '访客未退卡明细查询',
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
