<template>
  <div class="p-4">
    <!--定义表格-->
    <BasicTable @register="registerTable">
      <template #toolbar>
        <a-button
          type="primary"
          preIcon="ant-design:export-outlined"
          class="custom-button"
          @click="onExportXls"
          v-if="hasAuth('VehicleManagement:carFil:export')"
        >
          导出</a-button
        >
      </template>
      <template #tableTitle>
        <div class="title-left-box">
          <a-radio-group v-model:value="inStatus" @change="inStatusChange" style="margin-top: 5px">
            <a-radio-button value="未入厂">未入厂</a-radio-button>
            <a-radio-button value="在厂">在厂</a-radio-button>
            <a-radio-button value="已出厂">已离厂</a-radio-button>
          </a-radio-group>
          <div class="tb-til">
            <div :span="10"
              >&nbsp;&nbsp;当前备案流程数量：<span>{{ total.processNum }}</span>
            </div>
            <div :span="10"
              >&nbsp;&nbsp;已入厂车辆数量：<span>{{ total.enterCarNum }}</span></div
            >
            <div :span="10"
              >&nbsp;&nbsp;车辆进厂次数： <span>{{ total.enterTotalNum }}</span></div
            >
          </div>
        </div>
      </template>
      <!--操作栏-->
      <!-- <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template> -->
    </BasicTable>
  </div>
</template>

<script lang="ts" name="basic-table-demo" setup>
  import { searchFormSchema, columns } from './car.data';
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { Api, list, enterStatistic } from './api';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { reactive, ref } from 'vue';
  const { hasPermission } = usePermission();
  function hasAuth(code) {
    return hasPermission(code);
  }
  const resetColumns = (type) => {
    console.log(type, 'type');

    if (type == '在厂') {
      // 隐藏出厂大门、出厂时间、出厂识别照片、停车时长(小时)；
      const hideList = ['outOperatorName', 'outTime', 'outPicture', 'stayLasts'];
      const columnList = columns.filter((item) => !hideList.includes(item.dataIndex));
      getForm().updateSchema({
        field: 'fdFilingFactoryCode',
        show: true,
      });
      getForm().updateSchema({
        field: 'inTime',
        show: true,
      });
      setColumns(columnList);
    }
    if (type == '未入厂') {
      // 隐藏厂区、入厂大门、入场时间、入厂识别照片、出厂大门、出厂时间、出厂识别照片、停车时长
      const hideList = ['inFactory', 'inOperatorName', 'inTime', 'inPicture', 'outOperatorName', 'outTime', 'outPicture', 'stayLasts'];
      const columnList = columns.filter((item) => !hideList.includes(item.dataIndex));
      getForm().updateSchema({
        field: 'fdFilingFactoryCode',
        show: false,
      });
      getForm().updateSchema({
        field: 'inTime',
        show: false,
      });
      setColumns(columnList);
    }
    if (type == '已出厂') {
      getForm().updateSchema({
        field: 'fdFilingFactoryCode',
        show: true,
      });
      getForm().updateSchema({
        field: 'inTime',
        show: true,
      });
      setColumns(columns);
    }
  };
  const inStatus = ref('未入厂');
  const inStatusChange = () => {
    reload();
  };
  // 列表页面公共参数、方法
  const { tableContext, onExportXls } = useListPage({
    designScope: 'basic-table-demo-filter',
    tableProps: {
      title: '',
      api: list,
      columns: columns,
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [
          ['filingDate', ['fdFilingBegin', 'fdFilingEnd'], 'YYYY-MM-DD'],
          ['inTime', ['inTimeBegin', 'inTimeEnd'], 'YYYY-MM-DD'],
        ],
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
        console.log(23333);

        resetColumns(inStatus.value);
        // if (params.date) {
        //   params.startTime = dayjs(data.date).format("YYYY-MM-DD");
        //   params.endTime = dayjs
        // }
        params.inStatus = inStatus.value;
        if (params.fdFilingBegin && params.fdFilingEnd) {
          getEnterStatistic({ fdFilingBegin: params.fdFilingBegin, fdFilingEnd: params.fdFilingEnd });
        } else {
          total.enterCarNum = 0;
          total.enterTotalNum = 0;
          total.processNum = 0;
        }

        return params;
      },
    },
    exportConfig: {
      url: Api.exportXls,
      params: { inStatus: inStatus.value },
      name: '备案物流车进厂分析',
    },
  });
  const total = reactive({
    enterCarNum: 0,
    enterTotalNum: 0,
    processNum: 0,
  });
  const getEnterStatistic = async (params) => {
    const res = await enterStatistic(params);
    total.enterCarNum = res.enterCarNum;
    total.enterTotalNum = res.enterTotalNum;
    total.processNum = res.processNum;
  };

  //BasicTable绑定注册
  const [registerTable, { reload, setColumns, getForm }] = tableContext;
</script>
<style lang="less" scoped>
  //   .custom-button {
  //     border-radius: 4px !important;
  //   }

  ::v-deep(.ant-picker-range) {
    width: 100%;
  }
  .title-left-box {
    display: flex;
    .tb-til {
      display: flex;
      align-items: center;
      .div {
        border: 1px solid #797979;
        padding-top: 10px;
        padding-bottom: 10px;
      }
      .ant-col:first-of-type {
        border-right: none;
      }
      .ant-col:last-of-type {
        border-left: none;
      }
    }
  }
</style>
