<template>
  <!-- <a-form-item> -->
  <BasicTable @register="registerTable" :columns="props.columns" :dataSource="dataSource">
    <!-- <template #tableTitle>
      <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd" v-if="!props.disabled">添加</a-button>
      <a-button type="primary" preIcon="ant-design:import-outlined" @click="showModal" v-if="!props.disabled"> 导入</a-button>
    </template> -->
    <!--操作栏-->
    <!-- <template #action="{ index, record }">
      <TableAction :actions="getTableAction(record, index)" v-if="!props.disabled"/>
    </template> -->
    <template #deviceName="{ index, record }">
      <div v-if="deviceNameList != null && deviceNameList.length > 0">
        <div v-for="item in deviceNameList" :key="item" style="text-align: left; display: flex; justify-content: space-between; align-items: center"
          >{{ item }}
          <a-button style="margin-left: 8px; margin-top: 5px; visibility: collapse" type="primary" size="small" v-if="!disabled"> 删除</a-button>
        </div>
        <a-button type="primary" style="margin-top: 8px; visibility: collapse" size="small" v-if="!disabled">选择门禁</a-button>
      </div>

      <div v-else>无</div>
    </template>

    <template #newDeviceName="{ index, record }">
      <div style="display: flex; flex-direction: column">
        <div v-for="(item, index) in newDeviceNameList" :key="index" style="display: flex; justify-content: space-between; align-items: center">
          <span style="text-align: left">{{ item }}</span>
          <a-button style="margin-left: 8px; margin-top: 5px" type="primary" size="small" @click="deleteBuilding(index)" v-if="!disabled">
            删除</a-button
          >
        </div>
        <a-button type="primary" @click="choseBuilding" style="margin-top: 8px" size="small" v-if="!disabled">选择门禁</a-button>
      </div>
    </template>

    <template #oldDepartEmployeeName="{ index, record }">
      <div v-if="oldDepartEmployeeName">{{ oldDepartEmployeeName }}</div>
      <div v-else>无</div>
    </template>

    <template #newEmployeeName="{ index, record }">
      <a-form-item-rest>
        <person-select-input
          v-model:personList="secretAreaManagerPersons"
          :placeholder="'请选择'"
          :multiple="true"
          :multipleNum="2"
          :needEip="true"
          :disabled="disabled"
          @change="peopleDelete"
          @confirm="peopleConfirm"
        />
      </a-form-item-rest>
    </template>
  </BasicTable>
  <!-- </a-form-item> -->
  <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
    <div>
      <p class="pl-12">请按照模板规范填写，否则无法正确导入:<a-button :href="props.link" type="link">下载模板</a-button></p>
      <p class="p-12">
        <a-upload v-model:file-list="fileList" accept=".xls,.xlsx" :max-count="1" :customRequest="handleImport">
          <a-button>
            <upload-outlined></upload-outlined>
            上传
          </a-button>
        </a-upload>
      </p>
    </div>
  </a-modal>

  <door-access-select
    v-model:value="showPersonSelect"
    v-model:selected="selectedDoors"
    :multipleNum="buildMultipleNum"
    placeholder="请选择门禁"
    @ok="buidlSure"
    @exceed="exceed"
  />
</template>

<script setup lang="ts">
  import { PropType, watch, watchEffect, nextTick } from 'vue';
  import { ref, computed } from 'vue';
  import { cloneDeep } from 'lodash-es';
  import * as XLSX from 'xlsx/xlsx.mjs';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { BasicTable, TableAction, useTable, BasicColumn, ActionItem, EditRecordRow, TableRowSelection } from '/@/components/Table';
  import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  const emit = defineEmits(['update:value', 'exceed']);
  const $message = useMessage();

  const props = defineProps({
    // value: {
    //   type: Array<Record<string, any>>,
    //   default: () => [],
    // },
    value: {
      type: String,
      default: '',
    },
    columns: {
      type: [Array] as PropType<BasicColumn[]>,
      default: () => [],
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    buildMultipleNum: {
      type: Number,
      default: 0,
    },
  });
  const currentEditKeyRef = ref('');
  const dataSource = computed({
    // getter
    get: () => (props.value ? JSON.parse(props.value) : []),
    // setter
    set: (newValue) => {
      console.log(newValue, '[[[[[]]]]]');
      emit('update:value', newValue);
    },
  });

  function splitArr(str) {
    if (str) {
      if (str.includes(',')) {
        return str.split(',');
      }
      return [str];
    }
    return [];
  }

  const deviceNameList = computed(() => {
    console.log('管理门禁信息==', dataSource, props.value);
    return splitArr(dataSource.value[0].oldDeviceName);
  });
  const newDeviceNameList = computed(() => {
    return splitArr(dataSource.value[0].newDeviceName);
  });
  const newDeviceCodeList = computed(() => {
    return splitArr(dataSource.value[0].newDeviceCode);
  });
  const oldDepartEmployeeName = computed(() => {
    return dataSource.value[0].oldDepartEmployeeName;
  });

  const [registerTable, { reload, getDataSource, setTableData, updateTableDataRecord, deleteSelectRowByKey, deleteTableDataRecord }] = useTable({
    rowKey: 'id',
    bordered: true,
    pagination: false,
    showSizeChanger: false,
    showTableSetting: false,
    showIndexColumn: false,
    useSearchForm: false,
  });

  //门禁选择
  const showPersonSelect = ref(false);
  const selectedDoors = ref([]);

  function choseBuilding() {
    selectedDoors.value = newDeviceNameList.value.map((item, index) => {
      return {
        key: newDeviceCodeList.value[index],
        title: item,
      };
    });
    console.log('增加', selectedDoors.value);
    showPersonSelect.value = true;
    return;
    openModal(true, {
      title: '添加门禁',
      isUpdate: false,
      showFooter: true,
      record: {},
    });
  }

  function deleteBuilding(index) {
    const tableData = getDataSource();
    console.log('门禁删除==', newDeviceNameList, newDeviceNameList.value);
    newDeviceNameList.value.splice(index, 1);
    newDeviceCodeList.value.splice(index, 1);
    tableData[0].newDeviceName = newDeviceNameList.value.join(',');
    tableData[0].newDeviceCode = newDeviceCodeList.value.join(',');

    console.log('修改人员后的table数据=', tableData);
    emit('update:value', JSON.stringify(tableData));
  }

  function buidlSure(buildList) {
    console.log('门禁选择==', buildList);
    const tableData = getDataSource();
    tableData[0].newDeviceName = buildList.map((item) => item.title).join(',');
    tableData[0].newDeviceCode = buildList.map((item) => item.key).join(',');
    debugger;
    emit('update:value', JSON.stringify(tableData));
  }

  function exceed() {
    // $message.createMessage.warn(`您部门可管理门禁数量超过限制，请联系门禁管理员姓名XX+员工号`);
    emit('exceed', true);
  }

  //门禁管理员
  // const secretAreaManagerPersons = ref<any[]>([]);

  const secretAreaManagerPersons = computed(() => {
    if (dataSource.value.length <= 0 || !dataSource.value[0].newDepartEmployeeName) return [];
    let nameArr = splitArr(dataSource.value[0].newDepartEmployeeName);
    let idArr = splitArr(dataSource.value[0].newDepartEmployeeCode);
    let emailArr = splitArr(dataSource.value[0].neweDepartEmployeeLogin);
    let people = nameArr.map((item, index) => {
      return {
        id: idArr[index],
        name: item,
        email: `${emailArr[index]}@yutong.com`,
      };
    });
    return people;
  });
  function peopleDelete(newPeopleList) {
    console.log('新选中的人==', newPeopleList);
    const tableData = getDataSource();
    tableData[0].newDepartEmployeeName = newPeopleList.length > 0 ? newPeopleList.map((item) => item.name).join(',') : '';
    tableData[0].newDepartEmployeeCode = newPeopleList.length > 0 ? newPeopleList.map((item) => item.id).join(',') : '';
    tableData[0].neweDepartEmployeeLogin = newPeopleList.length > 0 ? newPeopleList.map((item) => item.email.split('@')[0]).join(',') : '';
    console.log('修改人员后的table数据=', tableData, newPeopleList);
    emit('update:value', JSON.stringify(tableData));
  }
  function peopleConfirm(newList) {
    console.log();
    const tableData = getDataSource();
    tableData[0].newDepartEmployeeName = newList.length > 0 ? newList.map((item) => item.name).join(',') : '';
    tableData[0].newDepartEmployeeCode = newList.length > 0 ? newList.map((item) => item.id).join(',') : '';
    tableData[0].neweDepartEmployeeLogin = newList.length > 0 ? newList.map((item) => item.email.split('@')[0]).join(',') : '';
    console.log('新增后的table数据=', tableData);
    emit('update:value', JSON.stringify(tableData));
  }
</script>

<style></style>
