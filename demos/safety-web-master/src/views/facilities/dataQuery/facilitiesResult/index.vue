<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
    >
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="hasAuth('facilities:facilitiesquery:facilitiesQueryResult:export')">导出</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import {Api, list, download, downloadInfo} from './list.api';
import { addFullParam } from '/@/utils/router/fullParam';
import { columns,searchFormSchema } from './list.data';
import { useRouter } from 'vue-router';
import { downloadByData } from '/@/utils/file/download';
import { usePermission } from '/@/hooks/web/usePermission';
const router = useRouter()

const { hasPermission } = usePermission();
function hasAuth(code) {
  return hasPermission(code);
}
// 列表页面公共参数、方法
const { tableContext, onExportXls } = useListPage({
  designScope: 'checkCar-list',
  tableProps: {
    title: '设备巡查结果',
    api: list,
    columns: columns,
    pagination: {
      pageSize: 15,
      showSizeChanger: false,
      showQuickJumper: false,
    },
    formConfig: {
      schemas: searchFormSchema,
      fieldMapToTime: [
        ['taskDate', ['taskBeginDate', 'taskEndDate'], 'YYYY-MM-DD'],
      ],
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
    beforeFetch: (params) => {
      // if (params.date) {
      //   params.startTime = dayjs(data.date).format("YYYY-MM-DD");
      //   params.endTime = dayjs
      // }
      return params;
    },
    actionColumn: {
      width: 140,
      title: '项目设备点检清单',
      //是否锁定操作列取值 right ,left,false
      fixed: 'right',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  },
  exportConfig: {
    url: Api.exportXls,
    name: '设备巡查结果',
  },
});
  // 注册 ListTable
const [registerTable] = tableContext;

const showInfo = async(record) => {
  router.push(addFullParam({
    name: 'facilitiesQueryLog',
    query: {
      id: record.id
    }
  }))
}



/**
 * 操作栏
 */
function getTableAction(record): ActionItem[] {
  return [
    { label: '详情', onClick: showInfo.bind(null, record), auth: 'facilities:facilitiesquery:facilitiesQueryResult:info' },
    { label: '下载', onClick: downInfoExcel.bind(null, record.id), auth: 'facilities:facilitiesquery:facilitiesQueryResult:downinfo' },
  ];
}

const downExcel = async(id) => {
  const res = await download({taskId: id})
  downloadByData(res, '设备巡查结果.xls')
}
const downInfoExcel = async(id) => {
  const res = await downloadInfo({taskId: id})
  downloadByData(res, '设备巡查结果.xls')
}


</script>

<style lang="less" scoped>
@import 'index';

.blacklistUpload {
  padding: 10px;
}
.temp-box{
  padding: 20px;
  li {
    display: flex;
    align-items: center;
    margin-top: 20px;
    p{
      width: 100px;
      margin: 0;
    }
  }
}
</style>
