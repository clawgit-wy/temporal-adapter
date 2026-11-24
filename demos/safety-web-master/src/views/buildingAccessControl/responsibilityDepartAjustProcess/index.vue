<template>
  <div class="systemd-noticemessage">
    <BasicTable title="提示信息" @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="createFlow">新建</a-button>
        <!-- <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls" v-if="hasAuth('facilities:facilitiesquery:log:export')"
          >导出</a-button
        > -->
      </template>
      <template #form-dutyDeptName="{ model, field }">
        <a-input readOnly v-model:value="model[field]" @click="departmentChose" placeholder="请选择" />
      </template>
      <template #form-newDeviceName="{ model, field }">
        <a-input readOnly v-model:value="model[field]" @click="buildChose" placeholder="请选择" />
      </template>

      <template #flowId="{ text, record }">
        <span style="color: blue; cursor: pointer" @click="goInfo(record)">{{ text }}</span>
      </template>
    </BasicTable>

    <DepartmentSelect v-model:visible="departVisible" @change="depChange"></DepartmentSelect>

    <door-access-select v-model:value="showPersonSelect" v-model:selected="selectedDoors" :multipleNum="1" placeholder="请选择门禁" @ok="buidlSure" />
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
  import { useRouter, useRoute } from 'vue-router';
  import { useUserStore } from '/@/store/modules/user';
  import dayjs from 'dayjs';
  import { useGlobSetting } from '/@/hooks/setting';
  import DepartmentSelect from '/@/components/DepartmentSelect.vue';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  import { useModal } from '/@/components/Modal';
  import { addFullParam } from '/@/utils/router/fullParam';
  const globSetting = useGlobSetting();
  const { getUserInfo }: { getUserInfo: any } = useUserStore();
  const route = useRoute();
  const router = useRouter();
  let autoStatus = true;

  interface DepartAjustRecord {
    buildList: Array<{
      deviceName: string;
      anFactoryName: string;
      anBuilding: string;
      [key: string]: any;
    }>;

    [key: string]: any;
  }

  // 列表页面公共参数、方法
  const { tableContext, onExportXls } = useListPage({
    designScope: 'checkCar-list',
    tableProps: {
      title: '责任部门调整流程',
      isTreeTable: true,
      api: async (params) => {
        // 调用原始API获取数据
        const result = await list(params);

        let res = result.dataList || [];
        res = res.map((item) => {
          if (item.buildList && item.buildList.length > 0) {
            const obj = {
              ...item,
            };
            delete obj.buildList;
            let children = null;
            obj.factory = '';
            obj.buildingName = '';
            obj.deviceName = '';
            if (item.buildList != null && item.buildList.length > 1) {
              children = item.buildList.map((duty) => {
                return {
                  // ...obj,
                  combFactoryName: duty.anFactoryName,
                  combBuild: duty.anBuilding,
                  newDeviceName: duty.deviceName,
                };
              });
            } else if (item.buildList != null && item.buildList.length == 1) {
              children = null;
              obj.combFactoryName = item.buildList[0].anFactoryName;
              obj.combBuild = item.buildList[0].anBuilding;
              obj.newDeviceName = item.buildList[0].deviceName;
            }

            return {
              ...obj,
              children,
            };
          }
          return {
            ...item,
          };
        });
        console.log(res, '=======');
        return { ...result, dataList: res };
      },
      columns: columns,
      showSizeChanger: true,
      pagination: {
        pageSize: 15,
        showSizeChanger: true,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
        fieldMapToTime: [['submitDate', ['createStartTime', 'createEndTime'], 'YYYY-MM-DD']],
      },
      showIndexColumn: true,
      indexColumnProps: {
        customCell: (record: any) => {
          return {
            rowSpan: record.rowSpan,
          };
        },
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
      showActionColumn: false,
      // beforeFetch: (params) => {
      //   if (route.query.id) {
      //     params['taskId'] = route.query.id;
      //     if (autoStatus) {
      //       delete params.status;
      //       getForm().setFieldsValue({ status: null });
      //       autoStatus = false;
      //     }
      //   }
      //   return params;
      // },
      // actionColumn: {
      //   width: 140,
      //   title: '操作',
      //   //是否锁定操作列取值 right ,left,false
      //   fixed: 'right',
      //   dataIndex: 'action',
      //   slots: { customRender: 'action' },
      // },
    },
  });
  // 注册 ListTable
  const [registerTable, { getForm }, { rowSelection, selectedRowKeys }] = tableContext;
  const { hasPermission } = usePermission();
  function hasAuth(code) {
    return hasPermission(code);
  }

  const createFlow = () => {
    router.push(addFullParam({
      path: '/responsibilityDepartAjustProcess/add',
    }));
  };
  const goInfo = (record) => {
    console.log('789456');
    
    router.push(addFullParam({
      path: '/responsibilityDepartAjustProcess/add',
      query: {
        formInstanceId: record.id,
        processId: record.bpmFlowId,
      },
    }));
  };

  //筛选项 组织
  const departVisible = ref(false);
  function departmentChose() {
    departVisible.value = true;
  }
  function depChange(depart) {
    console.log('组织选择==', depart);
    getForm().setFieldsValue({
      dutyDeptCode: depart.key,
      dutyDeptName: depart.title,
    });
    departVisible.value = false;
  }
  //筛选项 门禁
  const showPersonSelect = ref(false);
  const selectedDoors = ref([]);
  function buildChose() {
    console.log('增加', selectedDoors.value);
    showPersonSelect.value = true;
  }

  function buidlSure(buildList) {
    console.log('门禁选择==', buildList);
    getForm().setFieldsValue({
      newDeviceCode: buildList[0].key,
      newDeviceName: buildList[0].title,
    });
    showPersonSelect.value = false;
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
        width: 100px;
        margin: 0;
      }
    }
  }
</style>
