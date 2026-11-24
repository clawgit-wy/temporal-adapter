<template>
  <div class="list-container">
    <BasicTable @register="registerTable" @edit-change="onEditChange">
      <!-- <template #tableTitle>
        <a-button type="primary" @click="addAccess" v-if="!isView">添加门禁</a-button>
      </template> -->
      <!-- <template #headerCell="{ column }">
        <template v-if="column.dataIndex === 'action' && !isView">
          <a-button type="primary" @click="addAccess">添加门禁</a-button>
        </template>
        <template v-else>
          {{ column.customTitle }}
        </template>
      </template> -->

      <template #action="{ record, column, index }" v-if="!isView">
        <TableAction :actions="createActions(record, index)" />
      </template>

      <!-- 类型 -->
      <template #empowerType="{ record, text }">
        <Select
          v-if="record.editable"
          v-model:value="record.empowerType"
          :options="empowerTypeOptions"
          @change="(value) => handleEmpowerTypeRecordChange(record, value)"
          placeholder="请选择"
        />
        <span v-else>{{ record.empowerType == '1' ? '按人员' : record.empowerType == '2' ? '按组织' : '' }}</span>
      </template>

      <!-- 授权组织或则人员 -->
      <template #empowerAreaNameView="{ text, record }">
        <div style="display: flex; flex-direction: column; align-items: center">
          <!-- <a-tooltip v-if="record.empowerType == '1' && ellipsis10(text).includes('...')">
            <template #title>{{ tipStr(text) }}</template>
            <span @click="choseDeptOrPer(record)" :style="isView ? 'cursor:auto;' : 'cursor: pointer'">{{
              text ? (record.empowerType == '1' ? ellipsis10(text) : text) : '请选择'
            }}</span>
          </a-tooltip> -->
          <div style="display: flex; flex-direction: row; align-items: center">
            <a-button type="primary" @click="choseDeptOrPer(record)" v-if="!isView" size="small" style="margin-top: 8px; margin-right: 15px"
              >{{ record.empowerType == '1' ? '单人选择' : '组织选择' }}
            </a-button>
            <a-button type="primary" @click="showModal(record)" v-if="record.empowerType == '1' && !isView" size="small" style="margin-top: 8px"
              >人员导入
            </a-button>
          </div>

          <span @click="choseDeptOrPer(record)" :style="isView ? 'cursor:auto;' : 'cursor: pointer'">{{
            text ? (record.empowerType == '1' ? ellipsis10(text) : text) : ''
          }}</span>
        </div>
      </template>
      <!-- 日期 -->
      <template #powerTime="{ text, record }">
        <a-range-picker
          v-model:value="record.powerTime"
          v-model:open="record.rangePickerOpen"
          :showToday="false"
          @change="handleDateRangeChange"
          :disabled="[true, isView]"
          :disabledDate="disabledDate"
          valueFormat="YYYY-MM-DD"
          ref="powerTimeRefs"
        >
          <template #renderExtraFooter>
            <div style="text-align: center">
              <a-button type="link" @click="handleLongTermClick(record)">长期</a-button>
            </div>
          </template>
        </a-range-picker>
      </template>
      <template #headerCell="{ column }">
        <template v-if="column.dataIndex === 'deviceName'">
          <span v-if="isView">申请门禁</span>
          <a-button type="primary" @click="addAccess" v-else>添加门禁</a-button>
        </template>
        <template v-else>
          {{ column.customTitle }}
        </template>
      </template>
      <!-- 对接部门 -->
      <template #dockingDeptCode="{ record, text }">
        <Select
          v-if="record.editable"
          v-model:value="record.dockingDeptCode"
          :fieldNames="{ label: 'dutyDeptName', value: 'dutyDeptCode' }"
          :options="record.powerList"
          @change="(value) => handleRecordChange(record, value)"
          labelInValue
          placeholder="请选择"
          dropdownClassName="my-select-dropdown"
        />
        <span v-else>{{ record.dockingDeptCode?.label }}</span>
      </template>
      <!-- 公共门禁 -->
      <template #publicDeviceCode="{ record, text }">
        <div class="tags">
          <!-- 查看按钮 -->
          <template v-if="record?.deviceDOList && record.deviceDOList.length > 0">
            <a-button type="link" @click="handleViewPublicDevice(record)">查看</a-button>
          </template>
          <template v-else>
            <span>无</span>
          </template>
        </div>
      </template>
    </BasicTable>
  </div>

  <DoorAccessSelect v-model:value="showDoorSelect" placeholder="请选择门禁" @ok="selectedDoorAction" />
  <DepartmentSelect v-model:visible="departVisible" @change="departChange" :isSameDept="false"></DepartmentSelect>
  <PublicDeviceModal @register="registerPublicDeviceModal" @success="handlePublicDeviceSuccess" />
  <person-select-tree
    v-model:value="personSelectVisible"
    :selected="secretAreaManagerPersons"
    :multiple="true"
    :showRemoveButton="true"
    @ok="handlePersonSelected"
    @cancel="handleCancel"
  />
  <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
    <div>
      <p class="pl-12"
        >请按照模板规范填写，否则无法正确导入:
        <a-button @click="ondownLoadXls">下载模板</a-button>
      </p>
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
</template>
<script lang="ts" setup name="ScmModal">
  import { ref, reactive, computed, unref, watch, onMounted } from 'vue';
  import { ActionItem, BasicTable, TableAction, TableImg } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { debounce } from 'lodash-es';
  import { AutoComplete, Select } from 'ant-design-vue';
  import { BasicModal, useModalInner, useModal } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStore } from '/@/store/modules/user';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  import DepartmentSelect from '/@/components/DepartmentSelect.vue';
  import PersonSelectTree from '/@/components/PersonSelectTree/index.vue';
  import PublicDeviceModal from '/@/views/buildingAccessControl/accessRequestProcess/form/components/PublicDeviceModal.vue';
  import * as XLSX from 'xlsx/xlsx.mjs';
  import { downloadByData } from '/@/utils/file/download';
  import { downloadEmpTemplate, listWithSecret, listWithSecretBatch, validPeople, classPeople, buildConfigQuery } from './api';
  import { tableOpenColumns, tableCloseColumns, empowerTypeOptions } from './data';
  import dayjs from 'dayjs';
  import { buildUUID } from '/@/utils/uuid';
  import { useAccessRequestForm } from '../accessRequestProcess/form/useAccessRequest';

  const { isDetail, processId, isView, isEdit } = useAccessRequestForm();
  // 声明Emits
  const emit = defineEmits(['success']);
  // 属性定义
  const props = defineProps({
    list: {
      type: Array,
      default: () => [],
    },
    open: {
      type: Number,
      default: 0,
    },
  });
  // 页面消息
  const { createMessage, createErrorModal, createConfirm } = useMessage();
  // 用户信息
  const username = ref('');
  const userStore = useUserStore();
  username.value = userStore.getUserInfo.username;

  //公共方法
  //超过10个省略号
  function ellipsis10(str) {
    if (str.indexOf(',') == -1) return str;
    const strArr = str.split(',');
    if (strArr.length > 10) {
      return `${strArr.slice(0, 10).join(',')}...`;
    }
    return str;
  }

  function tipStr(str) {
    if (str.indexOf(',') == -1) return str;
    const strArr = str.split(',');

    return strArr.join(' , ');
  }

  const buildGrantNum = ref(0); //门禁一次性授权人数
  async function buildConfigQuerys() {
    let res = await buildConfigQuery({});
    console.log('授权配置信息==', res);
    buildGrantNum.value = res.buildGrantNum;
  }

  buildConfigQuerys();

  //表格
  const { tableContext } = useListPage({
    designScope: 'basic-table-demo',
    tableProps: {
      title: '',
      showIndexColumn: true,
      dataSource: [],
      columns: props.open == 1 ? tableOpenColumns : tableCloseColumns,
      bordered: true,
      showActionColumn: !isView.value,
      pagination: false,
      useSearchForm: false,
      showTableSetting: false,
      ellipsis: false,
      actionColumn: {
        width: 150,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' },
        fixed: 'right',
      },
      tableSetting: { fullScreen: true },
      rowKey: 'id',
    },
  });
  //注册table数据
  const [registerTable, { updateTableDataRecord, setTableData, getDataSource, deleteTableDataRecord, insertTableDataRecord }] = tableContext;

  onMounted(async () => {
    if (isDetail.value) {
      if (props.open == 1) {
        // const tempDoorList = (await listWithSecretBatch({ deviceCodes: deviceCodes })).map((item, index) => {
        //   const doorIndex = props.list.findIndex((ele) =>!ele.tag &&  ele.deviceCode == item.deviceCode  );
        //   props.list[doorIndex].tag = "asd"
        //   const powerItem = item.powerList?.find((item) => item.dutyDeptCode === props.list[doorIndex].dockingDeptCode);
        //   item.acrossEmployeeName = powerItem?.enList.map((person) => `${person.employeeName}${person.employeeCode}`).join('/');
        //   item.acrossEmployee = powerItem?.enList;
        //   const openItem = props.list[doorIndex];
        //   console.log('openItem******************', openItem);
        //   const publicDeviceNameList = (openItem.publicDeviceName || '').split(',').filter(Boolean);
        //   const publicDeviceCodeList = (openItem.publicDeviceCode || '').split(',').filter(Boolean);
        //   item.deviceDOList = publicDeviceNameList.map((name, index) => {
        //     return {
        //       deviceCode: publicDeviceCodeList[index],
        //       deviceName: name,
        //     };
        //   });
        //   if (props.list[doorIndex].empowerType == 1) {
        //     const nameArr = props.list[doorIndex].empowerAreaName.split(';');
        //     const codeArr = props.list[doorIndex].empowerAreaCode.split(';');
        //     item.empowerAreaNameView = nameArr.map((item, index) => `${item}${codeArr[index]}`).join(',');
        //   } else {
        //     item.empowerAreaNameView = props.list[doorIndex].empowerAreaName;
        //   }
        //   console.log('详情回显的item===', item);
        //   return {
        //     ...openItem,
        //     ...item,
        //     empowerType: `${openItem.empowerType}`,
        //     powerTime: [openItem.powerBeginTime, openItem.powerEndTime],
        //     editable: isView.value ? false : true,
        //     dockingDeptCode: {
        //       value: props.list[doorIndex].dockingDeptCode,
        //       label: props.list[doorIndex].dockingDeptName || powerItem.dutyDeptName,
        //     },
        //   };
        // });
        const deviceCodes = props.list.map((item) => item.deviceCode).join(',');
        const doorListInfo = await listWithSecretBatch({ deviceCodes: deviceCodes });
        const tempDoorList = props.list.reduce((acc, item, index) => {
          const tempDoorItem = doorListInfo.find((ele) => ele.deviceCode == item.deviceCode);
          if (!tempDoorItem) {
            if (isEdit.value) {
              console.warn(`设备 ${item.deviceCode} 未在门禁信息中找到，已自动过滤`);
              return acc;
            }
            if (item.empowerType == 1) {
              const nameArr = item.empowerAreaName.split(';');
              const codeArr = item.empowerAreaCode.split(';');
              item.empowerAreaNameView = nameArr.map((item, index) => `${item}${codeArr[index]}`).join(',');
            } else {
              item.empowerAreaNameView = item.empowerAreaName;
            }
            item.dockingDeptCode = {
              value: item.dockingDeptCode,
              label: item.dockingDeptName || powerItem.dutyDeptName,
            };
            acc.push({
              ...item,
              powerTime: [item.powerBeginTime, item.powerEndTime],
            });
            return acc;
          }
          const powerItem = tempDoorItem.powerList?.find((ele) => ele.dutyDeptCode === item.dockingDeptCode);
          if (powerItem) {
            item.acrossEmployeeName = powerItem?.enList.map((person) => `${person.employeeName}${person.employeeCode}`).join('/');
            item.acrossEmployee = powerItem?.enList;
            item.dockingDeptCode = {
              value: item.dockingDeptCode,
              label: item.dockingDeptName || powerItem.dutyDeptName,
            };
          } else {
            if (tempDoorItem.powerList?.length === 1) {
              item.dockingDeptCode = tempDoorItem.powerList.map((dutyDeptItem) => {
                return {
                  value: dutyDeptItem.dutyDeptCode,
                  label: dutyDeptItem.dutyDeptName,
                };
              })[0];
              item.acrossEmployeeName = tempDoorItem.powerList[0].enList.map((item) => `${item.employeeName}${item.employeeCode}`).join('/');
              item.acrossEmployee = tempDoorItem.powerList[0].enList;
              item.buildSysManagerRole = tempDoorItem.powerList[0].buildSysManagerRole;
            }
          }

          item.powerList = tempDoorItem.powerList;
          const publicDeviceNameList = (item.publicDeviceName || '').split(',').filter(Boolean);
          const publicDeviceCodeList = (item.publicDeviceCode || '').split(',').filter(Boolean);
          item.deviceDOList = publicDeviceNameList.map((name, index) => {
            return {
              deviceCode: publicDeviceCodeList[index],
              deviceName: name,
            };
          });
          if (item.empowerType == 1) {
            const nameArr = item.empowerAreaName.split(';');
            const codeArr = item.empowerAreaCode.split(';');
            item.empowerAreaNameView = nameArr.map((item, index) => `${item}${codeArr[index]}`).join(',');
          } else {
            item.empowerAreaNameView = item.empowerAreaName;
          }
          item.topSecretEmpName = tempDoorItem.topSecretEmpName;
          item.topSecretEmpLogin = tempDoorItem.topSecretEmpLogin;
          item.topSecretEmpCode = tempDoorItem.topSecretEmpCode;
          acc.push({
            ...item,
            empowerType: `${item.empowerType}`,
            refuseReason: !isView.value ? '' : item.refuseReason,
            powerTime: [item.powerBeginTime, item.powerEndTime],
            editable: !isView.value,
          });

          return acc;
          // return {
          //   ...item,
          //   empowerType: `${item.empowerType}`,
          //   refuseReason: !isView.value ? '' : item.refuseReason,
          //   powerTime: [item.powerBeginTime, item.powerEndTime],
          //   editable: isView.value ? false : true,
          // };
        }, []);
        console.log('门禁信息---------', tempDoorList);
        setTableData(tempDoorList);
      }
      if (props.open == 2) {
        const tempCloseList = props.list.map((item) => {
          if (item.empowerType == 1) {
            const nameArr = item.empowerAreaName.split(';');
            const codeArr = item.empowerAreaCode.split(';');
            item.empowerAreaNameView = nameArr.map((ele, index) => `${ele}${codeArr[index]}`).join(',');
          } else {
            item.empowerAreaNameView = item.empowerAreaName;
          }
          return {
            ...item,
            editable: isView.value ? false : true,
          };
        });
        setTableData(tempCloseList);
      }
    } else {
      setTableData([
        {
          empowerType: '2',
          editable: isView.value ? false : true,
          powerTime: [dayjs().format('YYYY-MM-DD'), dayjs('2099-12-31').format('YYYY-MM-DD')],
        },
      ]);
    }
  });

  //编辑
  function onEditChange({ column, value, record }) {
    console.log('编辑数据====', column, value, record, column.dataIndex);
    if (column.dataIndex === 'empowerType') {
      record.empowerAreaNameView = '';
      record.empowerAreaName = '';
      record.empowerAreaCode = '';
    }
    record[`${column.dataIndex}`] = `${value}`;
    console.log('编辑数据==赋值后==', column, value, record, currentRecord.value);
  }

  function createActions(record, index): ActionItem[] {
    return [
      {
        label: '删除',
        color: 'error',
        onClick: handleDelete.bind(null, record),
        ifShow: () => record.deviceCode,
      },
      {
        label: '复制',
        onClick: handleCopy.bind(null, record, index),
        ifShow: () => record.deviceCode,
      },
    ];
  }

  function handleDelete(record) {
    console.log('删除记录', record);
    deleteTableDataRecord(record.id);
    return;
    createConfirm({
      iconType: 'warning',
      title: '确定删除',
      content: '',
      onOk: async () => {
        deleteTableDataRecord(record.id);
      },
    });
    // 从列表数据中移除此条记录
    // removeDoorItem(record);
  }

  function handleCopy(record, index) {
    console.log('复制', record, index);
    insertTableDataRecord({ ...record, id: buildUUID() }, index);
  }

  //类型
  function handleEmpowerTypeRecordChange(record, value) {
    console.log('类型选择==', record, value);
    record.empowerAreaNameView = '';
    record.empowerAreaName = '';
    record.empowerAreaCode = '';
  }

  //组织、人员选择
  const currentRecord = ref({});

  function choseDeptOrPer(record) {
    console.log('点击组织人员选择==', record);
    if (isView.value) return;
    currentRecord.value = record;
    if (currentRecord.value.empowerType == '2') {
      choseDept();
    }
    if (currentRecord.value.empowerType == '1') {
      if (!record.empowerAreaCode) {
        secretAreaManagerPersons.value = [];
      } else {
        const nameArr = record.empowerAreaName.split(';');
        const codeArr = record.empowerAreaCode.split(';');
        secretAreaManagerPersons.value = nameArr.map((item, index) => {
          return {
            name: item,
            id: codeArr[index],
          };
        });
      }
      chosePeople();
    }
  }

  //部门选择
  const departVisible = ref(false);

  function choseDept() {
    departVisible.value = true;
  }

  const departChange = (depart) => {
    console.log('组织选择==', depart);
    currentRecord.value.empowerAreaNameView = depart.namePath;
    currentRecord.value.empowerAreaName = depart.namePath;
    currentRecord.value.empowerAreaCode = depart.codePath;
    departVisible.value = false;
  };

  // 人员
  const secretAreaManagerPersons = ref<any[]>([]);
  const personSelectVisible = ref(false);

  function chosePeople() {
    personSelectVisible.value = true;
  }

  // 处理取消选择
  const handleCancel = () => {
    personSelectVisible.value = false;
  };
  const handlePersonSelected = async (persons: TreePersonData[]) => {
    // const perList = persons.map((item) => {
    //   return {
    //     ...item,
    //     empNo: item.id,
    //     empName: item.name,
    //   };
    // });
    // const res = await validPeople({ userList: JSON.stringify(perList) });
    // if (res.notBelongList == null && res.belongList == null && !res.accessEight) {
    //   createMessage.error('无法选择该员工');
    // }
    // if (res.notBelongList != null && res.notBelongList.length > 0) {
    //   const notBelongStr = res.notBelongList.map((item) => `${item.empName}${item.empNo}`).join(',');
    //   createMessage.info(`${notBelongStr}不属于该部门员工`);
    // }
    // if (res.belongList != null && res.belongList.length > 0) {
    //   // newList = res.belongList;
    //   const belongList = res.belongList.map((item) => {
    //     return {
    //       ...item,
    //       name: item.empName,
    //       id: item.empNo,
    //     };
    //   });
    //   // secretAreaManagerPersons.value = belongList;
    //   secretAreaManagerPersons.value = belongList;

    //   // 关闭选择对话框
    //   personSelectVisible.value = false;
    // }

    secretAreaManagerPersons.value = persons;
    // 关闭选择对话框
    personSelectVisible.value = false;
  };

  // 监听人员列表变化
  watch(
    () => secretAreaManagerPersons.value,
    (persons) => {
      // 更新表单中的人员 topSecretEmpCode  topSecretEmpLogin topSecretEmpName
      updateSecretManagerIds(persons);
      if (persons != null && persons.length > buildGrantNum.value) {
        createMessage.info(`一次性授权人数不能超过${buildGrantNum.value}人`);
      }
    },
    { deep: true }
  );
  // 更新表单中的人员 topSecretEmpCode  topSecretEmpLogin topSecretEmpName
  const updateSecretManagerIds = (persons: any[]) => {
    // 将所有ID以逗号分隔存入表单
    //去重
    const seen = {}; // 用来记录已存在的 id 和 name 组合
    const tempPersons = persons.filter((item) => {
      const key = `${item.id}-${item.name}`;
      if (seen[key]) {
        return false; // 已经存在，过滤掉
      } else {
        seen[key] = true; // 记录下来
        return true; // 保留这个元素
      }
    });
    console.log('去重后的人员==', tempPersons);

    const leaderEmployeeName = tempPersons?.map((p) => p.name).join(';');
    const leaderEmployeeCode = tempPersons?.map((p) => p.id).join(';');

    currentRecord.value.empowerAreaNameView = tempPersons?.map((item) => `${item.name}${item.id}`).join(',');
    currentRecord.value.empowerAreaName = leaderEmployeeName;
    currentRecord.value.empowerAreaCode = leaderEmployeeCode;
  };

  //人员  导入操作
  const openUpload = ref<boolean>(false);
  const fileList = ref([]);
  const exportData = ref<Array<Record<string, any>>>([]);

  /**
   * 上传弹出框
   */
  function showModal(record) {
    currentRecord.value = record;
    openUpload.value = true;
    if (!record.empowerAreaCode) {
      secretAreaManagerPersons.value = [];
    } else {
      const nameArr = record.empowerAreaName.split(';');
      const codeArr = record.empowerAreaCode.split(';');
      secretAreaManagerPersons.value = nameArr.map((item, index) => {
        return {
          name: item,
          id: codeArr[index],
        };
      });
    }
  }

  function handleOk() {
    openUpload.value = false;
    secretAreaManagerPersons.value = currentRecord.value.empowerAreaCode
      ? [...secretAreaManagerPersons.value, ...exportData.value]
      : [...exportData.value];
    exportData.value = [];
    fileList.value = [];
  }

  const handleImport = async (file) => {
    const fileReader = new FileReader();
    fileReader.readAsBinaryString(file.file);
    fileReader.onload = async (event) => {
      try {
        const data = event.target?.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const worksheet = workbook.Sheets[workbook.SheetNames[0]];
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        jsonData.shift();
        // exportData.value = jsonData.map((item) => {
        //   return {
        //     name: item[1],
        //     id: item[0],
        //   };
        // });
        const idArrStr = jsonData.map((item) => item[0]).join(',');
        const res = await classPeople({ userCodeList: idArrStr });
        exportData.value =
          res.belongList != null && res.belongList.length > 0
            ? res.belongList.map((item) => {
                return {
                  ...item,
                  name: item.empName,
                  id: item.empNo,
                };
              })
            : [];
        // if (res.notBelongList != null && res.notBelongList.length > 0) {
        //   const notBelongStr = res.notBelongList.map((item) => `${item.empName}${item.empNo}`).join(',');
        //   createMessage.info(`${notBelongStr}不属于该部门员工`);
        // }
      } catch {
        createMessage.error('文件上传失败');
      }
    };
    fileList.value.map((i) => {
      i.status = 'done';
      return i;
    });

    return;
  };

  async function ondownLoadXls() {
    const res = await downloadEmpTemplate({});
    downloadByData(res, '人员导入模板.xlsx');
  }

  //门禁选择
  const showDoorSelect = ref(false);

  function addAccess() {
    showDoorSelect.value = true;
  }

  // 选中门禁
  async function selectedDoorAction(doors) {
    console.log('选中门禁', doors);
    if (props.open == 1) {
      const doorDeviceCodes = doors.map((door) => door.key).join(',');
      const res = await listWithSecretBatch({ deviceCodes: doorDeviceCodes });
      console.log('选中门禁接口结果===', res);
      const tableLastItem = getDataSource()[getDataSource().length - 1];
      console.log('表格最后一条数据是==', tableLastItem);
      let empowerType = '2';
      let empowerAreaNameView = '';
      let empowerAreaName = '';
      let empowerAreaCode = '';
      let powerTime = [dayjs().format('YYYY-MM-DD'), dayjs('2099-12-31').format('YYYY-MM-DD')];
      let powerBeginTime = dayjs().format('YYYY-MM-DD');
      let powerEndTime = dayjs('2099-12-31').format('YYYY-MM-DD');
      let rangePickerOpen = false;
      if (tableLastItem) {
        console.log('有最后一条==', tableLastItem);
        empowerType = tableLastItem.empowerType;
        empowerAreaNameView = tableLastItem?.empowerAreaNameView;
        empowerAreaName = tableLastItem?.empowerAreaName;
        empowerAreaCode = tableLastItem?.empowerAreaCode;
        powerTime =
          tableLastItem.powerTime?.length > 0 ? tableLastItem.powerTime : [dayjs().format('YYYY-MM-DD'), dayjs('2099-12-31').format('YYYY-MM-DD')];
        powerBeginTime = tableLastItem.powerBeginTime ? tableLastItem.powerBeginTime : dayjs().format('YYYY-MM-DD');
        powerEndTime = tableLastItem.powerEndTime;
      }
      console.log('转换后的数据==', empowerAreaNameView, empowerAreaName);
      const transRes = res?.map((item) => {
        if (item.powerList?.length === 1) {
          item.dockingDeptCode = item.powerList.map((dutyDeptItem) => {
            return {
              value: dutyDeptItem.dutyDeptCode,
              label: dutyDeptItem.dutyDeptName,
            };
          })[0];
          item.acrossEmployeeName = item.powerList[0].enList.map((item) => `${item.employeeName}${item.employeeCode}`).join('/');
          item.acrossEmployee = item.powerList[0].enList;
          item.buildSysManagerRole = item.powerList[0].buildSysManagerRole;
        }
        return {
          ...item,
          editable: isView.value ? false : true,
          empowerType,
          empowerAreaNameView,
          empowerAreaName,
          empowerAreaCode,
          powerTime,
          powerBeginTime,
          powerEndTime,
          rangePickerOpen,
        };
      });
      const filterDataSource = getDataSource().filter((item) => item.id);
      // console.log('过滤后的表格数据==', filterDataSource);
      setTableData([...filterDataSource, ...transRes]);
    }

    if (props.open == 2) {
      const deviceCode = doors.map((door) => door.key).join(',');
      const deviceName = doors.map((door) => door.title).join(',');
      const tableLastItem = getDataSource()[getDataSource().length - 1];
      console.log('表格最后一条数据是==', tableLastItem);
      let empowerType = '2';
      let empowerAreaNameView = '';
      let empowerAreaName = '';
      let empowerAreaCode = '';

      if (tableLastItem) {
        console.log('有最后一条==', tableLastItem);
        empowerType = tableLastItem.empowerType;
        empowerAreaNameView = tableLastItem?.empowerAreaNameView;
        empowerAreaName = tableLastItem?.empowerAreaName;
        empowerAreaCode = tableLastItem?.empowerAreaCode;
      }
      const transTableData = [
        {
          editable: isView.value ? false : true,
          empowerType,
          empowerAreaNameView,
          empowerAreaName,
          empowerAreaCode,
          deviceName,
          deviceCode,
        },
      ];
      const filterDataSource = getDataSource().filter((item) => item.deviceCode);
      setTableData([...filterDataSource, ...transTableData]);
    }
  }

  //开通期限
  // 禁用日期
  const disabledDate = (current) => {
    return (current && current < dayjs().startOf('day')) || current > dayjs('2099-12-31');
  };

  // 处理日期范围变化
  function handleDateRangeChange(dates) {
    console.log('日期变化==', dates, currentRecord.value);
  }

  // 处理点击长期按钮
  function handleLongTermClick(record) {
    // 设置长期日期（今天到2099-12-31）
    record['powerTime'] = [`${dayjs().format('YYYY-MM-DD')}`, `${dayjs('2099-12-31').format('YYYY-MM-DD')}`];
    record.rangePickerOpen = false;
    console.log('日期长期时改变的值-===', record);
  }

  //对接部门选择
  function handleRecordChange(record, value) {
    console.log('获取的值是', value);
    record.acrossEmployeeName = value.option.enList.map((item) => `${item.employeeName}${item.employeeCode}`).join('/');
    record.acrossEmployee = value.option.enList;
    record.buildSysManagerRole = value.option.buildSysManagerRole;
  }

  //公共区域
  function removePublicDevice(record, index) {
    createConfirm({
      iconType: 'warning',
      title: '提示',
      content: '确定删除公共区域门禁?',
      okText: '确定',
      onOk: async () => {
        console.log('删除公共区域门禁', record, index);
        record.deviceDOList.splice(index, 1);
      },
    });
  }

  // 验证所有表单
  const validateAll = () => {
    try {
      const dataSource = getDataSource();
      if (dataSource != null && dataSource.length > 0) {
        for (const door of dataSource) {
          console.log('单个门禁数据==', door);
          if (!door.deviceCode) {
            createMessage.error('请选择门禁');
            return false;
          }
          if (!door.empowerAreaCode) {
            createMessage.error('请选择授权范围');
            return false;
          }
          if (props.open == 1) {
            if (!door.powerTime || door.powerTime == null || door.powerTime.length < 2 || !door.powerTime[1]) {
              createMessage.error('请选择开通期限');
              return false;
            }

            // if (!door.dockingDeptCode) {
            //   createMessage.error('请选择对接部门');
            //   return fasle;
            // }
          }
        }
        let needArr = [];
        dataSource.forEach((door, index) => {
          if (!door.dockingDeptCode) {
            needArr.push(index + 1);
          }
        });
        if (needArr.length > 0 && props.open === 1) {
          createMessage.error(`第${needArr.join(',')}行申请信息未选择对接部门，请选择`);
          return false;
        }
      } else {
        createMessage.error('门禁数据不能为空');
        return false;
      }

      console.log('历史结果-----', dataSource);
      return dataSource;
    } catch (error) {
      console.log('验证失败88888888', error);
      return false;
    }
  };
  // 暴露验证方法
  defineExpose({
    validateAll,
  });

  // 注册公共设备弹框
  const [registerPublicDeviceModal, { openModal: openPublicDeviceModal }] = useModal();

  // 处理查看公共设备
  function handleViewPublicDevice(record) {
    openPublicDeviceModal(true, {
      deviceDOList: record.deviceDOList || [],
      isView: isView,
      record: record,
    });
  }

  // 处理公共设备弹框成功回调
  function handlePublicDeviceSuccess({ record, deviceList }) {
    // 更新表格中的数据
    record.deviceDOList = deviceList;
    // 强制表格刷新
    setTableData([...getDataSource()]);
  }
</script>
<style lang="less" scoped>
  ::v-deep .ant-table-title {
    min-height: 0px !important;
  }

  .scm-modal {
    .ant-row {
      .ant-col {
        // width: 100%;
        .ant-calendar-picker {
          display: block;
        }
      }
    }

    .door-access-selection {
      border: 1px solid #d9d9d9;
      border-radius: 2px;
      background-color: #fff;
      padding: 5px 11px;
      min-height: 34px;

      &.disabled {
        background-color: #f5f5f5;
        cursor: not-allowed;
        border-color: #d9d9d9;
      }
    }
  }

  .list-container {
    .add-button {
      display: flex;

      .ant-btn {
        flex: 1;
        color: rgba(0, 0, 0, 0.85);
        border-color: #d9d9d9;
        background: #fff;
        border-style: dashed;
      }
    }
  }
</style>
<style lang="less">
  .my-select-dropdown {
    .ant-select-item-option-content {
      white-space: normal !important;
    }
  }
</style>
