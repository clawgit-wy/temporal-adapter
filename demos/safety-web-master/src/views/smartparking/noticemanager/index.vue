<template>
  <div class="systemd-noticemessage">
    <BasicTable title="提示信息" :columns="columnsField" :dataSource="dataVisit" :showIndexColumn="false" :pagination="false"
      :loading="loading" bordered :actionColumn="{
        width: 100,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      }">
      <template #generalShow="{ record }">
        <a-switch :checked="record.generalShow" :checkedValue="0" :unCheckedValue="1" disabled />
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <NoticemessageModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="systemd-noticemessage" setup>
import { ref } from 'vue';
import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
import NoticemessageModal from './NoticemessageModal.vue';
import { list } from './noticemessage.api';
import { columns } from './noticemessage.data';
import { useModal } from '/@/components/Modal';
const [registerModal, { openModal }] = useModal(); // 厂区弹出框

const columnsField = ref<any[]>(columns);
const loading = ref(false);
const dataVisit = ref<any[]>([]);

function onEdit(record) {
  openModal(true, {
    title: '修改信息',
    isUpdate: true,
    record: record,
    showFooter: true,
  });
}

async function reload() {
  getList();
}

// 获取列表
function getList() {
  loading.value = true;
  list({}).then((res) => {
    dataVisit.value = res;
    loading.value = false;
  });
}

getList();

/**
 * 操作栏
 */
function getTableAction(record): ActionItem[] {
  return [
    { label: '编辑', onClick: onEdit.bind(null, record) },
  ];
}


</script>

<style lang="less" scoped>
@import 'index';

.blacklistUpload {
  padding: 10px;
}
</style>
