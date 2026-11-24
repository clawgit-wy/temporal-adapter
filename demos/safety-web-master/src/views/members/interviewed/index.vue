<template>
  <div :class="prefixCls">
    <BasicTable
      @register="registerTable"
    >
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onAdd" v-if="hasAuth('members:interviewed:add')"> 添加</a-button>
      </template>
    <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <BlacklistModal @register="registerModal" @success="reload" />
  </div>
</template>

<script lang="ts" name="members-interviewed" setup>
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import BlacklistModal from './Modal.vue';
  import { list, deleteSingle} from './api';
  import { columns, searchFormSchema } from './data';
  // import dayjs from 'dayjs';

  // 列表页面公共参数、方法
  const { prefixCls, tableContext,  } = useListPage({
    designScope: 'members-interviewed',
    tableProps: {
      title: '员工信息台账数据',
      api: list,
      columns: columns,
      rowKey: "id",
      rowSelection: {
        type: 'checkbox'
      },
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['date', ['startTime', 'endTime'], 'YYYY-MM-DD']],
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
  const [registerModal, { openModal }] = useModal();
  const { hasPermission } = usePermission();
  function onAdd() {
    openModal(true, {
      title: '添加人员',
      isUpdate: false,
      showFooter: true,
      record: {},
    });
  }

  function onEdit(record) {
    openModal(true, {
      title: '修改',
      isUpdate: true,
      record: record,
      showFooter: true,
    });
  }

  async function onDelete(record) {
    if (record) {
      await deleteSingle({ id: String(record.id) }, true);
      await reload();
    }
  }
  
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'members:interviewed:edit' },
      { label: '删除', onClick: onDelete.bind(null, record), auth: 'members:interviewed:delete' },
    ];
  }

  function hasAuth(code) {
    return hasPermission(code);
  }

</script>

<style lang="less" scoped>
  @import 'index';
  .blacklistUpload {
    padding: 10px;
  }
</style>
