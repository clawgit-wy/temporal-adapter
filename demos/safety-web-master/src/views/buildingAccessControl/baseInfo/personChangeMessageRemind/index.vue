<template>
  <div>
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-radio-group :value="currentCategory" @change="categoryChange">
          <a-radio-button value="1">全部</a-radio-button>
          <a-radio-button value="2">待确认</a-radio-button>
        </a-radio-group>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <TableAction
            v-if="record.infoConfirm === '1'"
            :actions="[
              {
                label: '确认',
                onClick: handleConfirm.bind(null, record),
              },
            ]"
          />
          <span v-else>已确认</span>
        </template>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" setup>
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { columns, searchFormSchema } from './personChangeMessageRemind.data';
  import { getPersonChangeMessageRemindList, updatePersonChangeMessageRemind } from './personChangeMessageRemind.api';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { ref } from 'vue';
  const { createMessage, createConfirm } = useMessage();
  const currentCategory = ref('1');
  const [registerTable, { reload }] = useTable({
    title: '人员变动消息提醒',
    api: getPersonChangeMessageRemindList,
    columns,
    formConfig: {
      labelWidth: 80,
      schemas: searchFormSchema,
      actionColOptions: {
        span: 6,
        // style: { textAlign: 'left' },
      },
      fieldMapToTime: [['createTimeRange', ['beginDate', 'endDate'], 'YYYY-MM-DD']],
    },
    useSearchForm: true,
    showTableSetting: true,
    bordered: true,
    showIndexColumn: false,
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
      params.infoConfirm = currentCategory.value === '1' ? '' : '1';
      return params;
    },
    actionColumn: {
      width: 150,
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
    },
  });

  // 确认
  function handleConfirm(record: Recordable) {
    createConfirm({
      iconType: 'warning',
      title: '提示',
      content: '是否已确认?',
      onOk: async () => {
        await updatePersonChangeMessageRemind({
          id: record.id,
          infoConfirm: '0',
        });
        createMessage.success('确认成功');
        reload();
      },
    });
  }

  // 切换分类
  function categoryChange(target: any) {
    currentCategory.value = target.target.value;
    reload();
  }
</script>
