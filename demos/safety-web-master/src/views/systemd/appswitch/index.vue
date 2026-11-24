<template>
  <div class="systemd-appswitch">
    <BasicTable title="应用入口" bordered :columns="columnsField" :dataSource="dataVisit" :showIndexColumn="false"
      :pagination="false" :loading="loading" :actionColumn="{
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
    <AppswitchModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="systemd-appswitch" setup>
import { ref } from 'vue';
import { appFileds } from './type';
import { ActionItem, BasicTable, TableAction, BasicColumn } from '/@/components/Table';
import AppswitchModal from './AppswitchModal.vue';
import { list } from './appswitch.api';
import { columns } from './appswitch.data';
import { useModal } from '/@/components/Modal';
const [registerModal, { openModal }] = useModal(); // 厂区弹出框


const columnsField = ref<BasicColumn[]>(columns);
const loading = ref(false);

const dataVisit = ref<appFileds[]>([]);

function onEdit(record) {
  openModal(true, {
    title: '修改信息',
    isUpdate: true,
    record: record,
    showFooter: true,
  });
}

// 获取列表
function getList() {
  loading.value = true;
  list({ generalType: 'switch' }).then((res) => {
    const data = res;
    dataVisit.value = [{
      id: data.id,
      generalType: data.generalType,
      generalShow: data.generalShow,
      content: data.content,
    }];
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


async function reload() {
  getList();
}

</script>

<style lang="less" scoped>
@import 'index';

.blacklistUpload {
  padding: 10px;
}
</style>
