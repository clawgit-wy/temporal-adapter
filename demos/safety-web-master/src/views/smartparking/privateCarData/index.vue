<template>
  <div :class="prefixCls">
    <BasicTable title="提示信息"  @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="openUpload = true" v-if="hasAuth('smartparking:privateCarData:import')">导入</a-button>
      </template>
    </BasicTable>
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <div class="blacklistUpload">
        <p>请按照模板规范填写，否则无法正确导入<a-button @click="ondownLoadXls">下载模板</a-button></p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" name="systemd-noticemessage" setup>
import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import { list, Api, download } from './list.api';
import { columns,searchFormSchema } from './list.data';
import { useModal } from '/@/components/Modal';
import { ref } from 'vue';
import { downloadByData } from '/@/utils/file/download';
const [registerModal, { openModal }] = useModal(); // 厂区弹出框

// 列表页面公共参数、方法
const { prefixCls, tableContext, onImportXls } = useListPage({
  designScope: 'retention-list',
  tableProps: {
    title: '滞留提醒通知列表',
    api: list,
    columns: columns,
    pagination: {
      pageSize: 15,
      showSizeChanger: false,
      showQuickJumper: false,
    },
    formConfig: {
      schemas: searchFormSchema,
      fieldMapToTime: [['date', ['beginDate', 'endDate'], 'YYYY-MM-DD']],
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
    actionColumn: {
      width: 140,
      title: '操作',
      //是否锁定操作列取值 right ,left,false
      fixed: 'right',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  },
  importConfig: {
    url: Api.import,
    success: () => reload(),
  },
});
  // 注册 ListTable
const [registerTable, { reload }] = tableContext;
const { hasPermission } = usePermission();
 function hasAuth(code) {
    return hasPermission(code);
  }

const openUpload = ref(false)

const handleOk = () => {
  openUpload.value = false
}
async function ondownLoadXls() {
  const res = await download({});
  downloadByData(res, '员工入厂私家车信息表导入模板.xlsx');
}

</script>

<style lang="less" scoped>
@import 'index';

.blacklistUpload {
  padding: 10px;
}
</style>
