<template>
  <div class="p-4">
    <!--定义表格-->
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" preIcon="ant-design:export-outlined" class="custom-button" v-if="hasAuth('facilities:facilitiesquery:log:export')" @click="onExportXlsx"> 导出</a-button>
      </template>
      <!--操作栏-->
      <!-- <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template> -->
      <template #fdPer="{ record }">
        <div class="item_table">
          <tr class="item_tr" v-for="item in record.perList">
            <td class="item_td">
              <Tooltip placement="top" :title="item.fdPerType">
                <span>{{ item.fdPerType }}</span>
              </Tooltip>
            </td>
          </tr>
        </div>
      </template>
      <template #fdName="{ record }">
        <div class="item_table">
          <tr class="item_tr" v-for="item in record.perList">
            <td class="item_td">
              <Tooltip placement="top" :title="item.fdPerName">
                <span>{{ item.fdPerName }}</span>
              </Tooltip>
            </td>
          </tr>
        </div>
      </template>
      <template #fdCard="{ record }">
        <div class="item_table">
          <tr class="item_tr" v-for="item in record.perList">
            <td class="item_td">
              <Tooltip placement="top" :title="item.fdPerCard">
                <span>{{ item.fdPerCard }}</span>
              </Tooltip>
            </td>
          </tr>
        </div>
      </template>
      <template #fdphone="{ record }">
        <div class="item_table">
          <tr class="item_tr" v-for="item in record.perList">
            <td class="item_td">
              <Tooltip placement="top" :title="item.fdPerPhone">
                <span>{{ item.fdPerPhone }}</span>
              </Tooltip>
            </td>
          </tr>
        </div>
      </template>
      <!-- <template #fdImg="{ record }">
        <div class="item_table">
          <tr class="item_tr" v-for="item in record.perList">
            <td class="item_td">
              <a :href="item.fdPerImg" target="_blank" v-if="item.fdPerImg">点击查看</a>
            </td>
          </tr>
        </div>
      </template> -->
    </BasicTable>
  </div>
</template>

<script lang="ts" name="basic-table-demo" setup>
  import { searchFormSchema, columns } from './histor.data';
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { list, exportXls } from './api';
  import { usePermission } from '/@/hooks/web/usePermission';
  const { hasPermission } = usePermission();
  function hasAuth(code) {
    return hasPermission(code);
  }
  // 列表页面公共参数、方法
  const { tableContext, onExportXlsx } = useListPage({
    designScope: 'basic-table-demo-filter',
    tableProps: {
      title: '',
      api: list,
      columns: columns,
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['createTime', ['createTimeBegin', 'createTimeEnd'], 'YYYY-MM-DD']],
      },
      showIndexColumn: true,
      showActionColumn: false,
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
      beforeFetch: (params) => {
        // if (params.date) {
        //   params.startTime = dayjs(data.date).format("YYYY-MM-DD");
        //   params.endTime = dayjs
        // }
        return params;
      },
    },
    exportConfig: {
      url: exportXls,
      name: '历史备案信息表',
    },
  });
  //BasicTable绑定注册
  const [registerTable] = tableContext;
</script>
<style lang="less" scoped>
  ::v-deep(.ant-picker-range) {
    width: 100%;
  }
  .ant-table-cell {
    padding: 0px !important;
  }
  ::v-deep(.ant-table-row > td:nth-child(n + 12):nth-child(-n + 16)) {
    padding: 0px !important;
  }
  // .cs {
  //   border-bottom: 1px solid red;
  //   width: 100%;
  // }
  .item_tr {
    height: 30px;
    background: transparent;
    display: flex;
    justify-content: center;
    align-items: center;
    border-bottom: 1px solid #e5e7eb;
    .item_td {
      padding: 5px;
      height: 100%;
      width: 100%;
      justify-content: center;
      align-items: center;
      display: inline;
      white-space: nowrap;
      overflow: hidden; /* 隐藏超出部分的文本 */
      text-overflow: ellipsis;
      a {
        text-decoration: underline;
        // border-bottom: 1px solid #1664ff;
      }
    }
  }
  .item_tr:last-child {
    border-bottom: none;
  }

  ::v-deep(.jeecg-basic-table-action) {
    justify-content: center !important;
  }
  // ::v-deep(.ant-btn-sm) {
  //   padding: 0 !important;
  // }

  ::v-deep(.jeecg-basic-table-action .ant-divider, .jeecg-basic-table-action .ant-divider-vertical) {
    margin: 0px -5px !important;
  }
</style>
