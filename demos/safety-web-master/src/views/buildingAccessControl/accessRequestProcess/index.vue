<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable">
      <template #processCode="{ record, text }">
        <span
          style="color: blue; cursor: pointer"
          @click="navigateToProcessDetail(record.id, record.bpmFlowId, 'accessRequestProcess', record.roleType)"
          >{{ text }}</span
        >
      </template>
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" @click="onAutoAuthOrg" v-if="hasAuth('buildingAccess:accessRequestProcess:autoAuthOrg:button')"
          >自动授权组织</a-button
        >
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd">新建申请</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts" name="buildingAccessControl-accessRequestProcess" setup>
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { Api, list, cancelRequest, checkBuildRoleType } from './accessRequest.api';
  import { columns, searchFormSchema } from './accessRequest.data';
  import { debounce } from 'lodash-es';
  import { useRouter } from 'vue-router';
  import { addFullParam } from '/@/utils/router/fullParam';
  import { ref, onMounted } from 'vue';
  import { useUserStore } from '/@/store/modules/user';
  const userInfo = useUserStore().getUserInfo;
  import { useProcessNavigation } from '/@/views/buildingAccessControl/hooks/useProcessNavigation';
  const { navigateToProcessDetail } = useProcessNavigation();
  import { h } from 'vue';
  import { Button } from 'ant-design-vue';
  // 列表页面公共参数、方法
  const { prefixCls, onExportXls, tableContext } = useListPage({
    designScope: 'buildingAccessControl-accessRequestProcess',
    tableProps: {
      title: '门禁申请流程列表',
      api: list,
      columns: columns,
      size: 'small',

      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['createTimeRange', ['crateTimeBegin', 'createTimeEnd'], 'YYYY-MM-DD']],
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
        width: 150,
        title: '操作',
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
        customHeaderCell: () => {
          return {
            style: { padding: '8px' },
            // 使用render函数自定义表头
            slots: {
              title: (column) => {
                return h(
                  Button,
                  {
                    type: 'primary',
                    size: 'small',
                    onClick: (e) => {
                      e.stopPropagation();
                      // 这里可以添加点击按钮时的操作
                      console.log('操作按钮点击');
                    },
                  },
                  () => '操作'
                );
              },
            },
          };
        },
      },
    },
    exportConfig: {
      url: Api.exportXls,
      name: '门禁申请记录',
    },
  });

  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;
  const { hasPermission } = usePermission();
  const router = useRouter();

  const currentUserRoleType = ref('');
  onMounted(async () => {
    currentUserRoleType.value = await checkBuildRoleType();
  });

  // 创建新申请
  function onAdd() {
    if (currentUserRoleType.value == 1) {
      router.push(addFullParam('/accessRequestProcess/form'));
    } else {
      router.push(addFullParam('/accessRequestProcess/chargeAndManagerProcess'));
    }
  }
  //自动授权组织
  function onAutoAuthOrg() {
    router.push(addFullParam('/accessRequestProcess/autoAuthOrg'));
  }
  // 编辑申请
  function editRecord(record) {
    router.push({
      path: 'form',
      query: {
        id: record.id,
        type: 'edit',
      },
    });
  }
  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    //只有自己提交的才能看到提交按钮
    if (record.createEmployeeLogin != userInfo.userName) {
      return [];
    }
    console.log('record', record);
    const actions: ActionItem[] = [{ label: '重新提交', onClick: resubmitAction.bind(null, record) }];
    return actions;
  }
  function resubmitAction(record) {
    console.log('重新提交2', record);
    if (record.roleType == '1') {
      router.push(addFullParam({
        path: record.roleType == 1 ? '/accessRequestProcess/form' : '/accessRequestProcess/chargeAndManagerProcess',
        query: {
          formInstanceId: record.id,
          type: 'edit',
        },
      }));
    } else {
      router.push(addFullParam({
        path: '/accessRequestProcess/chargeAndManagerProcess',
        query: {
          formInstanceId: record.id,
          type: 'edit',
        },
      }));
    }
  }
  function goInfo(record) {
    router.push({
      path: '/accessRequestProcess/form',
      query: {
        formInstanceId: record.id,
        bpmFlowId: record.bpmFlowId,
        type: 'view',
      },
    });
  }
  function hasAuth(code) {
    return hasPermission(code);
  }
</script>

<style lang="less" scoped>
  .access-request-process {
    padding: 20px;
  }
</style>
