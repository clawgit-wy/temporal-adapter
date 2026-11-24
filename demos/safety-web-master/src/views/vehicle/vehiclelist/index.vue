<template>
  <div class="p-4">
    <!--定义表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection" bordered>
      <template #toolbar>
        <!--   -->
        <a-button v-if="authList.includes('11')" type="primary" preIcon="ant-design:plus-outlined" class="custom-button" @click="onAdd">
          添加备案</a-button
        >
        <a-button v-if="authList.includes('13')" type="primary" preIcon="ant-design:plus-outlined" class="custom-button" @click="openUpload = true">
          导入备案</a-button
        >
        <a-button v-if="authList.includes('12')" type="primary" preIcon="ant-design:export-outlined" class="custom-button" @click="onExportXlsx">
          导出</a-button
        >
      </template>
      <template #tableTitle>
        <!-- v-if="authList.includes('10')" -->
        <a-button
          type="primary"
          preIcon="ant-design:safety-certificate-outlined"
          class="custom-button"
          @click="onVerify"
          :disabled="disabled"
          v-if="authList.includes('10')"
        >
          批量审核</a-button
        >
        <!-- <a-button type="primary" preIcon="ant-design:check-outlined" class="custom-button" @click="selectAll"> 全选</a-button> -->
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
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
    <VeAddModal @register="registerModal" @success="reload" />
    <VerifyModal @register="registerVerifyModal" @success="reload" />
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

<script lang="ts" name="basic-table-demo" setup>
  import { searchFormSchema, columns, chColumns } from './vehicle.data';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import VeAddModal from './VeAddModal.vue';
  import VerifyModal from './VerifyModal.vue';
  import { list, exportXls, importXls, download, auths,deleteSingle } from './api';
  import { onMounted, ref, watch } from 'vue';
  import { useRouter } from 'vue-router';
  import { message } from 'ant-design-vue';
  import { downloadByData } from '/@/utils/file/download';

  const authList = ref<string[]>([]);
  const router = useRouter();
  const disabled = ref(true); //批量审核按钮状态
  const ids = ref([]);
  async function getAuths() {
    const res = await auths({});
    if (res) {
      res.forEach((item) => {
        authList.value.push(item.authCode);
      });
    }
  }
  onMounted(() => {
    getAuths();
  });

  // 列表页面公共参数、方法
  const { tableContext, onExportXls, onImportXls, onExportXlsx } = useListPage({
    designScope: 'basic-table-demo-filter',
    tableProps: {
      title: '表单搜索',
      api: list,
      columns: columns,
      rowKey: 'id',
      rowSelection: {
        type: 'checkbox',
      },
      formConfig: {
        schemas: searchFormSchema,
        // submitFunc: customSubmitFunc,
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
      name: '物流车辆管理备案',
    },
    importConfig: {
      url: importXls,
      success: () => reload(),
    },
  });
  //BasicTable绑定注册
  const [registerTable, { reload }, { rowSelection, selectedRows, selectedRowKeys,  }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  const [registerVerifyModal, verifyModalFun] = useModal();
  const onDelete = async(id) => {
    await deleteSingle({id}, true)
    reload()
  }
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    let items: ActionItem[] = new Array<ActionItem>();
    if (record.fdState == '审批中') {
      items = [
        { label: '审核', onClick: onApply.bind(null, record) },
      ];
      
    } else if (record.fdState == '已审批') {
      items = [
        { label: '编辑', onClick: onEdit.bind(null, record.id) },
        { label: '删除', onClick: onDelete.bind(null, record.id), ifShow: authList.value.includes('14') },
        // { label: '废弃', onClick: onAbanded.bind(null, record), auth: 'members:blacklist:delete' },
      ];
    } else if (record.fdState == '已过期') {
      items = [
      ];
    } else if (record.fdState == '手动添加') {
      items = [
        { label: '编辑', onClick: onEdit.bind(null, record.id) },
        { label: '删除', onClick: onDelete.bind(null, record.id), ifShow: authList.value.includes('14') },
      ];
    }
    return items;
  }
  // 添加
  function onAdd() {
    openModal(true, {
      title: '添加备案',
      isUpdate: false,
      record: {},
      showFooter: true,
    });
  }
  watch(
    () => selectedRows,
    (n) => {
      if (selectedRows.value.length > 0) {
        disabled.value = false;
      }
    },
    {
      deep: true,
    }
  );
  //   批量审核
  const onVerify = () => {
    ids.value = []
    selectedRows.value.filter((item) => {
      if (item.fdState == '审批中' && item.fdFilingType == '物流车辆') {
        ids.value.push(item.id);
      }
    });
    if(ids.value.length == 0) {
      return message.warning('请选择审批中的有效数据')
    }
    verifyModalFun.openModal(true, {
      title: '批量审核',
      ids: ids.value,
    });
  };
  // 审核
  function onApply(record) {
    if (record) {
      router.push({
        path: '/VehicleManagement/filing/approve',
        query: {
          formInstanceId: record.id,
        },
      });
    }
  }
  function onEdit(id) {
    openModal(true, {
      title: '修改备案',
      isUpdate: true,
      record: {id},
      showFooter: true,
    });
  }
  // const { refreshPage, closeCurrent } = useTabs();
  // 废弃
  // async function onAbanded(record) {
  //   let params = {
  //     approveType: 3,
  //     fdFilingId: record.id,
  //   };
  //   await approve(params, 3, reload);
  // }

  const openUpload = ref(false)
  async function ondownLoadXls() {
    const res = await download({});
    downloadByData(res, '导入模板.xlsx');
  }
  function handleOk() {
    openUpload.value = false;
    // reload();
  }
</script>
<style lang="less" scoped>
 .blacklistUpload {
    padding: 10px;
  }
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
  // ::v-deep(.ant-table-tbody > tr:nth-child(2n + 1)) {
  //   background-color: #d9d9d999;
  // }

  // ::v-deep(.ant-table-tbody > tr.ant-table-row:hover > td, .ant-table-body > tr > td.ant-table-cell-row-hover) {
  //   background-color: none !important;
  // }
</style>
