<template>
  <div class="systemd-noticemessage">
    <BasicTable title="提示信息" @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="hasAuth('facilities:facilitiesquery:log:export')"
          >导出</a-button
        >
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="triggerApproves" v-if="hasAuth('facilities:facilitiesquery:log:export')"
          >触发需求评审流程</a-button
        >
      </template>
      <template #checkImg="{ text }">
        <TableImg v-if="text" :size="60" :simpleShow="true" :imgList="text.split(',')" />
      </template>
      <template #reformImg="{ text }">
        <TableImg v-if="text" :size="60" :simpleShow="true" :imgList="text.split(',')" />
      </template>
      <template #flowId="{ text, record }">
        <span style="color: blue; cursor: pointer" @click="goInfo(record)">{{ text }}</span>
      </template>

      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <a-modal width="1400px" v-model:visible="infoVisible">
      <a-table :loading="infoLoading" style="margin-top: 40px" :columns="infoColumns" :pagination="false" :data-source="infoData">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'reformImg'">
            <TableImg v-if="record.reformImg" :size="60" :simpleShow="true" :imgList="record.reformImg.split(',')" />
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
  import { ActionItem, BasicTable, TableAction, TableImg } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { Api, list, infoList, createEIPFlow } from './list.api';
  import { columns, searchFormSchema } from './list.data';
  import { Modal, message } from 'ant-design-vue';
  import { ref } from 'vue';
  import day from 'dayjs';
  import { useRoute } from 'vue-router';
  import { useUserStore } from '/@/store/modules/user';
  import dayjs from 'dayjs';
  const { getUserInfo }: { getUserInfo: any } = useUserStore();
  const route = useRoute();
  let autoStatus = true;
  // 列表页面公共参数、方法
  const { tableContext, onExportXls } = useListPage({
    designScope: 'checkCar-list',
    tableProps: {
      title: '巡查日志',
      api: list,
      columns: columns,
      showSizeChanger: true,
      pagination: {
        pageSize: 15,
        showSizeChanger: true,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [
          ['spot', ['spotBeginDate', 'spotEndDate'], 'YYYY-MM-DD'],
          ['planReform', ['planReformBegin', 'planReformEnd'], 'YYYY-MM-DD'],
          ['validate', ['validateBegin', 'validateEnd'], 'YYYY-MM-DD'],
        ],
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
        if (route.query.id) {
          params['taskId'] = route.query.id;
          if (autoStatus) {
            delete params.status;
            getForm().setFieldsValue({ status: null });
            autoStatus = false;
          }
        }
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
      name: '设备巡检日志',
    },
  });
  // 注册 ListTable
  const [registerTable, { getForm }, { rowSelection, selectedRowKeys }] = tableContext;
  const { hasPermission } = usePermission();
  function hasAuth(code) {
    return hasPermission(code);
  }
  const infoLoading = ref(false);
  const infoVisible = ref(false);
  const showInfo = async (record) => {
    infoLoading.value = true;
    infoVisible.value = true;
    try {
      const { dataList } = await infoList({ recordId: record.id });
      infoLoading.value = false;
      infoData.value = dataList.map((item) => {
        const planDate = day(item.planTime).format('YYYY-MM-DD');
        const finishDate = day(item.finishTime).format('YYYY-MM-DD');
        return {
          ...item,
          validateEmp: record.validateEmp,
          validateStatus: record.validateStatus,
          reformDelay: day(finishDate).isSame(day(planDate)) || day(finishDate).isBefore(day(planDate)) ? 0 : 1,
        };
      });
      console.log(infoData.value);
    } catch (error) {
      infoLoading.value = false;
      infoData.value = [];
    }
  };

  const goInfo = (record) => {
    const hasFullParam = window.location.hash.includes('__full__=true');
    if (hasFullParam) {
      window.open(record.flowUrl+'&__full__=true', '_blank')
    } else {
      window.open(record.flowUrl, '_blank')
    }
  };

  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    return [{ label: '详情', onClick: showInfo.bind(null, record), auth: 'facilities:facilitiesquery:log:info' }];
  }

  const infoColumns = [
    {
      title: '计划整改完成日期',
      dataIndex: 'planTime',
      key: 'planTime',
      customRender: ({ text }) => (text ? day(text).format('YYYY-MM-DD') : ''),
      width: 150,
    },
    {
      title: '实际整改完成日期',
      dataIndex: 'finishTime',
      key: 'finishTime',
      customRender: ({ text }) => (text ? day(text).format('YYYY-MM-DD') : ''),
      width: 150,
    },
    { title: '整改后图片', dataIndex: 'reformImg', key: 'reformImg', resizable: true, width: 110 },
    { title: '整改人', dataIndex: 'reformName', key: 'reformName', resizable: true, width: 120 },
    {
      title: '整改人提交时间',
      dataIndex: 'reformTime',
      key: 'reformTime',
      customRender: ({ text }) => (text ? day(text).format('YYYY-MM-DD') : ''),
      width: 140,
    },
    {
      title: '是否延期整改',
      dataIndex: 'reformDelay',
      key: 'reformDelay',
      resizable: true,
      width: 130,
      customRender: ({ text }) => (text == '1' ? '是' : '否'),
    },
    {
      title: '延期计划整改完成日期',
      dataIndex: 'delayTime',
      key: 'delayTime',
      customRender: ({ text }) => (text ? day(text).format('YYYY-MM-DD') : ''),
      width: 180,
    },
    {
      title: '验证结果',
      dataIndex: 'validResult',
      key: 'validResult',
      resizable: true,
      width: 140,
      customRender: ({ text }) => (text == '1' ? '已验证' : text == '0' ? '未验证' : text == '4' ? '整改不合格' : ''),
    },
    { title: '验证详请描述', dataIndex: 'validRemark', key: 'validRemark', resizable: true, width: 140 },
    { title: '验证人', dataIndex: 'validName', key: 'validName', resizable: true, width: 140 },
  ];
  const infoData = ref([]);
  const triggerApproves = () => {
    if (selectedRowKeys.value.length == 0) {
      return message.warning('请选择数据');
    }

    Modal.confirm({
      title: '提示',
      content: '是否确认批量触发所选中需求评审流程？',
      onOk: () => {
        createFlow();
      },
    });
  };
  const createFlow = async () => {
    const eip = getUserInfo.email ? getUserInfo.email.split('@')[0] : '';
    createEIPFlow({ idList: selectedRowKeys.value, eip }).then(() => {
      message.success('操作成功');
    });
  };
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
        width: 100px;
        margin: 0;
      }
    }
  }
</style>
