<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
    >
      <template #tableTitle>
      </template>
      <template #subject="{ text, record }">
        <span style="color: blue; cursor: pointer;" @click="goInfo(record)">{{text}}</span>
      </template>
      <template #goodsDetail="{ text, record }">
        <span style="color: blue; cursor: pointer;" @click="getGoodsInfo(record.goodsOutId)">{{text}}</span>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <EditModal @register="registerModal" @success="reload" />
    <a-modal v-model:visible="visible" title="详情" width="1000px" :footer="null">
      <div style="padding: 10px 20px">
        <a-table :dataSource="dataSource" :columns="historycolumns" size="small" :pagination="false">
        </a-table>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { BasicTable, TableImg, TableAction, ActionItem } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import {Api, list, getGoodsDetail} from './list.api';
import { columns,searchFormSchema } from './list.data';
import { useRoute, useRouter } from 'vue-router';
import EditModal from './editModal.vue'
import {Image} from 'ant-design-vue'
import { ref } from 'vue';
import { useModal } from '/@/components/Modal';
const route = useRoute()
const router = useRouter()

// 列表页面公共参数、方法
const { tableContext, onExportXls } = useListPage({
  designScope: 'checkCar-list',
  tableProps: {
    title: '业务验证台账',
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
        ['RangeDate', ['checkTimeBegin', 'checkTimeEnd'], 'YYYY-MM-DD'],
      ],
    },
    showIndexColumn: true,
    // showActionColumn: false,
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
      if (route.query.id) {
        params['taskId'] = route.query.id
      }
      return params;
    },
  },
  exportConfig: {
    url: Api.exportXls,
    name: '设备巡检日志',
  },
});
  // 注册 ListTable
const [registerTable, {reload}] = tableContext;
const { hasPermission } = usePermission();
function hasAuth(code) {
  return hasPermission(code);
}
const goInfo = (record) => {
  if (record.goodsOutFlowUrl) {
    const hasFullParam = window.location.hash.includes('__full__=true');
    if (hasFullParam) {
      window.open(record.goodsOutFlowUrl+'&__full__=true', '_blank')
    } else {
      window.open(record.goodsOutFlowUrl, '_blank')
    }
  }
}
const [registerModal, { openModal }] = useModal();

const edit = (record) => {
  openModal(true, {
    title: '编辑',
    isUpdate: true,
    showFooter: true,
    record,
  });
}
function getTableAction(record): ActionItem[] {
  return [
    { label: '编辑', onClick: edit.bind(null, record), auth: 'exitpass:businessValidation:edit' },
  ];
}
const historycolumns = [
  { title: '编号', dataIndex: 'goodsCode', key: 'goodsCode', },
  { title: '名称', dataIndex: 'goodsName', key: 'goodsName', },
  { title: '单位', dataIndex: 'goodsUnit', key: 'goodsUnit', },
  { title: '数量', dataIndex: 'goodsNum', key: 'goodsNum', },
  { title: '车工号', dataIndex: 'vehicleNo', key: 'vehicleNo', },
  { title: 'VIN号', dataIndex: 'vinNo', key: 'vinNo'},
  { title: '备注', dataIndex: 'goodsRemark', key: 'goodsRemark', customRender: ({text, record}) => record.vehicleId ? record.vehicleRemark : text },
  { title: '是否验证通过', dataIndex: 'validAccess', key: 'validAccess', },
]
const visible = ref(false)
const dataSource = ref([])
const getGoodsInfo = async (id) => {
  visible.value = true
  const res = await getGoodsDetail({processId: id})
  dataSource.value = res
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
