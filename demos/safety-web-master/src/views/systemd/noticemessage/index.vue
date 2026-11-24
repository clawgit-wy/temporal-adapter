<template>
  <div class="systemd-noticemessage">
    <div class="notice-tips">
      <p>①授权流程生效提醒：访客审批权限授权流程审批通过后向授权人、被授权人进行消息提醒。</p>
      <p>②授权到期前提醒：授权到期前7天、到期当天向授权人、被授权人进行提醒。</p>
      <p>③岗位调整提醒：被授权人或授权人岗位调整时，向被授权人进行提醒。</p>
      <p>④高频业务被访人提醒：当被访人最近7天审批访客超过10条、且不在访客审批授权表中，向被访人进行消息提醒<span style="color: red;">（每人每季度只提醒一次）</span>。</p>
    </div>
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

.notice-tips {
  background: #fff;
  padding: 16px;
  margin-bottom: 16px;
  p {
    margin: 4px 0;
  }
}
.blacklistUpload {
  padding: 10px;
}
</style>
