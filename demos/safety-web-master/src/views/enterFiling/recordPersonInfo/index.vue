<template>
  <div class="p-4 import-loading-wrapper">
    <!--定义表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection" @selectionChange="selectionChange">
      <template #toolbar>
        <a-button type="primary" class="custom-button" @click="muchDone('edit')">批量编辑</a-button>
        <a-button type="primary" class="custom-button" @click="muchDone('review')">批量审核</a-button>
        <a-button type="primary" class="custom-button" @click="muchDone('renewal')">批量续期</a-button>
        <a-button type="primary" class="custom-button" @click="onAdd">添加人员</a-button>
        <a-button type="primary" class="custom-button" @click="onExportXls()">导出名单</a-button>
        <BatchImport
          button-text="导入名单"
          :import-url="Api.importDataRecord"
          :download-url="Api.downloadTemplate"
          :loading="importLoading"
          @start="importLoading = true"
          @success="handleImportSuccess"
          @error="handleImportError"
        />
        <!-- <a-button type="primary" class="custom-button" @click="enterTypeConfig()">入厂方式配置</a-button> -->
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <Modal @register="registerModal" @success="reload" :selectKeys="selectedRowKeys" />
    <ApplicationModal @register="registerAddModal" @success="reload" />
    <ImportFailModal @register="registerFailModal" />
    <ConfigModal @register="registerConfigModal" @success="getEntryConfig" />
    <!-- 导入全局loading遮罩 -->
    <div v-if="importLoading" class="import-loading-mask">
      <Spin size="large" tip="导入中..." />
    </div>
  </div>
</template>
<script lang="ts" setup>
  import { ref, unref, onMounted } from 'vue';
  import ApplicationModal from './ApplicationModal.vue';
  import ImportFailModal from './ImportFailModal.vue';
  import ConfigModal from './ConfigModal.vue';
  import { columns, formSchemas } from './data';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import Modal from './Modal.vue';
  import dayjs from 'dayjs';
  import { list, deleteDict, Api, getEntryMethod } from './api';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();
  import { usePermission } from '/@/hooks/web/usePermission';
  const { hasPermission } = usePermission();
  import BatchImport from '/@/components/BatchImport/index.vue';
  import { useRouter } from 'vue-router';
  const router = useRouter();
  import { getToken } from '/@/utils/auth';
  import { Spin } from 'ant-design-vue';
  console.log(getToken());

  // 导入全局loading
  const importLoading = ref(false);

  function hasAuth(code) {
    return hasPermission(code);
  }
  // 列表页面公共参数、方法
  const { onExportXls, onImportXls, tableContext } = useListPage({
    tableProps: {
      title: '备案人员信息表',
      api: list,
      columns: columns,
      useSearchForm: true,
      formConfig: {
        schemas: formSchemas,
        fieldMapToTime: [['enterTime', ['enterStartTime', 'enterEndTime'], 'YYYY-MM-DD']],
      },
      rowKey: 'id',
      rowSelection: {
        type: 'checkbox',
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
        params['supperCode'] = 'enter_per_type';
        params['supperId'] = 2;
        return params;
      },
    },
    exportConfig: {
      url: Api.exportInfoUrl,
      name: '备案人员信息表',
      params: { token: getToken() },
    },
  });
  //BasicTable绑定注册
  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  const [registerAddModal, { openModal: openAddModal }] = useModal();
  const [registerFailModal, { openModal: openImportFailModal }] = useModal();
  const [registerConfigModal, { openModal: openConfigModal }] = useModal();
  //是否过期
  const isExpired = (dataTime) => {
    const t = dayjs(dataTime, 'YYYY-MM-DD HH:mm'); // 指定格式
    console.log('验证是否过期==', t.isValid(), t.isBefore(dayjs()));
    return t.isValid() && t.isBefore(dayjs()); // 早于现在就算过期
  };
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [
      {
        label: '审核',
        onClick: onReview.bind(null, record),
        // auth: 'EnterFiling:recordPersonInfo:review',
        ifShow: () => record.status == '20' && !isExpired(record.enterEndTime),
      },
      {
        label: '编辑',
        onClick: onEdit.bind(null, record),
        // auth: 'EnterFiling:recordPersonInfo:edit',
        ifShow: () => record.status != '20' && !isExpired(record.enterEndTime),
      },
      {
        label: '续期',
        onClick: onRenewal.bind(null, record),
        // auth: 'EnterFiling:recordPersonInfo:renewal',
        ifShow: () => isExpired(record.enterEndTime),
      },
      {
        label: '注销',
        onClick: onDelete.bind(null, record),
        // auth: 'EnterFiling:recordPersonInfo:delete',
        ifShow: () => record.status != '20' && !isExpired(record.enterEndTime),
      },
    ];
  }
  // 审核
  function onReview(record) {
    openModal(true, {
      type: 'singleReview',
      title: '审核',
      isUpdate: true,
      record,
      showFooter: true,
    });
  }

  // 续期
  function onRenewal(record) {
    openModal(true, {
      type: 'singleRenewal',
      title: '续期',
      isUpdate: true,
      record,
      showFooter: true,
    });
  }
  //导入成功
  function handleImportSuccess(fileInfo) {
    importLoading.value = false;
    console.log('上传结果==', fileInfo, fileInfo.data, fileInfo.data.data.length);
    // 检查是否有失败的数据
    if (fileInfo && fileInfo.data && fileInfo.data.data.length > 0) {
      console.log('打开失败弹窗===');
      // 有失败数据，打开失败弹窗
      openImportFailModal(true, {
        failList: fileInfo.data.data,
        msg: fileInfo.data.message,
      });
    }
    createMessage.success(fileInfo.data.message);
    reload();
  }
  function handleImportError(fileInfo) {
    importLoading.value = false;
    console.log('上传失败==', fileInfo);
    createMessage.error(fileInfo.msg);
  }

  //黑名单入厂申请
  // const onApply = () => {
  //   router.push({
  //     path: '/blackListEnterApply',
  //   });
  // };
  //入厂方式配置
  const enterTypeConfig = () => {
    openConfigModal(true, {
      title: '入厂方式配置',
      isUpdate: false,
      record: { entryMethod: enterTypeConfigValue.value },
      showFooter: true,
    });
  };
  const enterTypeConfigValue = ref(0);
  const getEntryConfig = async () => {
    let res = await getEntryMethod();
    console.log('获取入厂方式配置结果==', res);
    if (String(res) != 'null') {
      enterTypeConfigValue.value = res;
    }
  };
  onMounted(() => {
    getEntryConfig();
  });

  async function onDelete(record) {
    if (record) {
      openModal(true, {
        type: 'singleDelete',
        title: '注销',
        isUpdate: false,
        record,
        showFooter: true,
      });
    }
  }

  // 添加
  const onAdd = (): void => {
    openAddModal(true, {
      isUpdate: false,
      showFooter: true,
      title: '添加人员',
    });
  };
  // 编辑
  function onEdit(record) {
    // openAddModal(true, {
    //   title: '编辑',
    //   isUpdate: true,
    //   record,
    //   showFooter: true,
    // });
    openModal(true, {
      type: 'singleEdit',
      title: '编辑',
      isUpdate: true,
      record,
      showFooter: true,
    });
  }

  const muchDone = (type) => {
    console.log('选中的==', rowSelection.selectedRows);
    if (selectedRowKeys.value.length == 0) {
      return createMessage.warn('请选择人员');
    }
    const mapList = rowSelection.selectedRows.map((item) => {
      return {
        enterFactory: item.enterFactory,
        enterFactoryCode: item.enterFactoryCode,
        enterStartTime: item.enterStartTime,
        enterEndTime: item.enterEndTime,
        id: item.id,
      };
    });
    if (type == 'edit') {
      if (!hasDuplicates(mapList)) {
        return createMessage.warn('请选择备案权限一致人员');
      }
      if (hasReview(rowSelection.selectedRows)) {
        return createMessage.warn('请选择不在审批中状态的数据');
      }
      if (hasExpired(mapList)) {
        return createMessage.warn('只能选择备案有效期内人员');
      }
    }
    if (type == 'review') {
      if (!hasDuplicates(mapList)) {
        return createMessage.warn('请选择备案权限一致人员');
      }
      if (hasNotReview(rowSelection.selectedRows)) {
        return createMessage.warn('请选择审批中的数据');
      }
      if (hasExpired(mapList)) {
        return createMessage.warn('只能选择备案有效期内人员');
      }
    }
    if (type == 'renewal') {
      if (hasReview(rowSelection.selectedRows)) {
        return createMessage.warn('请选择不在审批中状态的数据');
      }
    }
    openModal(true, {
      type: type,
      title: 'title',
      isUpdate: false,
      record: rowSelection.selectedRows[0],
      showFooter: true,
    });
  };

  const hasDuplicates = (mapList) => {
    if (mapList.length == 1) return true;
    return mapList.every(
      (item) =>
        item.enterFactoryCode === mapList[0].enterFactoryCode &&
        item.enterStartTime === mapList[0].enterStartTime &&
        item.enterEndTime === mapList[0].enterEndTime
    );
  };
  const hasNotReview = (mapList) => {
    return mapList.some((item) => item.status != '20');
  };
  const hasReview = (mapList) => {
    return mapList.some((item) => item.status == '20');
  };
  const hasExpired = (mapList) => {
    return mapList.some((item) => isExpired(item.enterEndTime));
  };

  const selectionChange = ({ keys, rows }) => console.log(keys, rows);
</script>
<style lang="less" scoped>
  .import-loading-wrapper {
    position: relative;
  }

  .import-loading-mask {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 9999;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background-color: rgba(240, 242, 245, 0.4);
  }

  ::v-deep(.ant-picker-range) {
    width: 100%;
  }

  ::v-deep(.ant-input-affix-wrapper) {
    // border-color: #797979;
    border-radius: 4px;
  }
</style>
