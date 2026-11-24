<template>
  <div class="p-4">
    <!--定义表格-->
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button type="primary" preIcon="ant-design:plus-outlined" class="custom-button" @click="onAdd">新增</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
      <template #form-souQuanType="{ model, field }">
        <a-select v-model:value="model[field]" placeholder="请选择授权类型" allowClear>
          <a-select-option v-for="item in sqTypeList" :key="item.id" :value="item.id">{{ item.lable }}</a-select-option>
        </a-select>
      </template>
      <template #form-banType="{ model, field }">
        <a-select v-model:value="model[field]" placeholder="请备案类型" allowClear>
          <a-select-option v-for="item in sqTypeList" :key="item.id" :value="item.id">{{ item.lable }}</a-select-option>
        </a-select>
      </template>
    </BasicTable>
    <Modal @register="registerModal" @success="reload" />
  </div>
</template>
<script lang="ts" setup>
  import { columns, searchFormSchema } from './data';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import Modal from './Modal.vue';
  import { ref } from 'vue';
  interface sqType {
    id: number;
    lable: string;
    value: string;
  }
  const sqTypeList = ref<sqType[]>();
  // 列表页面公共参数、方法
  const { tableContext } = useListPage({
    tableProps: {
      title: '门禁通道维护',
      api: undefined,
      columns: columns,
      // useSearchForm: false,
      showIndexColumn: true,
      showActionColumn: false,
      formConfig: {
        schemas: searchFormSchema,
        // submitFunc: customSubmitFunc,
      },
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
  });
  //BasicTable绑定注册
  const [registerTable, { reload }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  // 导出
  function onExportXls() {}
  // 编辑
  function onEdit(record) {}
  //搜索
  const search = (): void => {};
  // 重置
  const reset = (): void => {};
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [
      { label: '编辑', onClick: onEdit.bind(null, record), auth: 'members:blacklist:edit' },
      { label: '删除', onClick: onDelete.bind(null, record), auth: 'members:blacklist:delete' },
    ];
  }

  // 新增
  const onAdd = (): void => {
    openModal(true, {
      title: '门禁通道维护',
      isUpdate: true,
      record: {},
      showFooter: true,
    });
  };

  async function onDelete(record) {
    // if (record) {
    //   await deleteSingle({ id: String(record.id) }, true);
    //   await reload();
    // }
  }
</script>
<style lang="less" scoped></style>
