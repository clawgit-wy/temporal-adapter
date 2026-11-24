<template>
  <div class="systemd-noticemessage">
    <BasicTable @register="registerTable">
      <template #distributionList="{ text, record }">
        <span style="color: blue; cursor: pointer" @click="download(record)" v-if="hasPermission('securityService:issueExceptionRecords:download')"
          >下载</span
        >
      </template>
      <template #exceptionLog="{ text, record }">
        <span style="color: blue; cursor: pointer" @click="look(record)" v-if="hasPermission('securityService:issueExceptionRecords:view')"
          >查看</span
        >
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { list, downloadLog, employeeIssuedView, reissue } from './list.api';
  import { columns, searchFormSchema } from './list.data';
  import { useRouter, useRoute } from 'vue-router';
  import { Modal, message } from 'ant-design-vue';
  import ListModal from './ListModal.vue';
  import { useModal } from '/@/components/Modal';
  import { ref } from 'vue';
  import { downloadByData } from '/@/utils/file/download';
  const route = useRoute();
  const router = useRouter();
  // 列表页面公共参数、方法
  const { tableContext } = useListPage({
    designScope: 'issue-record',
    tableProps: {
      title: '员工权限下发异常记录',
      api: list,
      columns: columns,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['updateTime', ['operateBeginTime', 'operateEndTime'], 'YYYY-MM-DD']],
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
      actionColumn: {
        width: 140,
        title: '操作',
        //是否锁定操作列取值 right ,left,false
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
    },
  });
  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;
  const { hasPermission } = usePermission();
  function hasAuth(code) {
    return hasPermission(code);
  }

  function getTableAction(record): ActionItem[] {
    return [
      { label: '重新下发', onClick: onReIssue.bind(null, record), ifShow: () => hasPermission('securityService:issueExceptionRecords:reIssue') },
    ];
  }
  function onReIssue(record) {
    Modal.confirm({
      title: '提示',
      content: '是否重新下发',
      async onOk() {
        await reissue({ id: record.id });
        message.success('操作成功');
        reload();
      },
      onCancel() {},
    });
  }

  const download = async (recorder) => {
    const res = await downloadLog({ id: recorder.id });
    downloadByData(res, '下发异常日志.xlsx');
  };

  async function look(record) {
    const res = await employeeIssuedView({ id: record.id });
    Modal.confirm({
      title: '异常日志',
      content: res.exceptionDesc,
      onOk() {},
      onCancel() {},
    });
  }
</script>

<style lang="less" scoped>
  @import 'index';

  .blacklistUpload {
    padding: 10px;
  }
  .temp-box {
    padding: 20px;
    li {
      display: flex;
      align-items: center;
      margin-top: 20px;
      p {
        width: 160px;
        margin: 0;
      }
    }
  }
  :deep(.jeecg-basic-table) {
    & > .ant-form-horizontal {
      & > .ant-row {
        & > .ant-col:first-child {
          .ant-form-item-label {
            flex: auto;
            max-width: 30%;
          }
        }
      }
    }
  }
</style>
