<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd" v-if="hasAuth('members:organizational:add')">添加</a-button>
        <!-- <a-button type="primary" preIcon="ant-design:import-outlined" @click="openUpload = true" v-if="hasAuth('smartparking:checkcar:checkcarrole:add')">批量导入</a-button> -->
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="hasAuth('members:organizational:export')"> 导出</a-button>
      </template>

      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <OrganizationalModal @register="registerModal" @success="reload" />
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

<script lang="ts" name="members-organizational" setup>
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import OrganizationalModal from './OrganizationalModal.vue';
  import { Api, list, deleteSingle, download} from './organizational.api';
  import { columns, searchFormSchema } from './organizational.data';
import { ref } from 'vue';
import { downloadByData } from '/@/utils/file/download';

  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'members-organizational',
    tableProps: {
      title: '外协对接人组织架构配置',
      api: list,
      columns: columns,
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
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
    exportConfig: {
      url: Api.exportXls,
      name: '外协免检台账列表',
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
      title: '修改信息',
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
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'members:organizational:edit' },
      { label: '删除', onClick: onDelete.bind(null, record), auth: 'members:organizational:delete' },
    ];
  }


  function hasAuth(code) {
    return hasPermission(code);
  }
const openUpload = ref(false)
const handleOk = () => {
  openUpload.value = false
}
async function ondownLoadXls() {
  const res = await download({});
  downloadByData(res, '公出人员列表.xlsx');
}
</script>

<style lang="less" scoped>
  @import 'index';
  .blacklistUpload {
    padding: 10px;
  }
</style>
