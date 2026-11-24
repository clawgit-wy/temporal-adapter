<template>
  <PageWrapper title="楼宇门禁申请" dense>
    <div class="access-request-form">
      <a-card title="楼宇门禁申请流程">
        <BasicForm @register="registerForm" :showActionButtonGroup="false">
          <!-- 门禁新增提醒 - 放在部门和已授权门禁字段后面 -->
          <template #doorAccessTips>
            <div class="door-access-info">
              <div class="door-access-info-title"><span class="required-mark">*</span>门禁新增： </div>
              <div class="door-access-info-content">
                <p class="blue-text">1.流程审批通过后自动开通楼宇门禁所在厂区大门权限</p>
                <p class="blue-text"
                  >2.长期未进出{{ cancelAuthDays }}天的门禁，系统将自动推送您确认权限是否保留，门禁管理员有权直接管理您的长期未进出门禁权限</p
                >
                <p class="blue-text">
                  3.如您需要开通的办公楼公共区域（如：1楼、-1楼）存在门禁管制，系统将自动为您开通对应公共区域门禁，无需手动选择公共区域门禁。</p
                >
              </div>
            </div>
          </template>
          <template #createDeptName="{ model, field }">
            <div class="createDeptName">
              <a-input v-model:value="model[field]" placeholder="请选择" :disabled="true" />
              <div class="required-mark-container" v-if="isShowDutyPerson">
                <span class="required-mark-name">本部门门禁管理员: </span>
                <div class="required-mark-value" v-for="(item, index) in currentUserInfo?.dutyPersonList" :key="index">
                  <span>{{ item.empName }}{{ item.empNo }}</span>
                  <span>{{ item.phoneNo }}</span>
                </div>
                <!-- <span class="required-mark-value">{{ currentUserInfo?.dutyPersonStr }}</span> -->
              </div>
              <div class="required-mark-container-tips" v-if="isShowDoorManagerTips">
                {{ doorManagerTipsStr }}
                <!-- <span class="required-mark-value">{{ currentUserInfo?.dutyPersonStr }}</span> -->
              </div>
            </div>
          </template>
          <!-- 日期选择器和长期按钮 -->
          <template #applyDateRangeSlot="{ model, field }">
            <a-range-picker
              v-model:value="model[field]"
              v-model:open="datePickerOpen"
              :showToday="false"
              :disabled="[true, isView]"
              :disabledDate="disabledDate"
            >
              <template #renderExtraFooter>
                <div style="text-align: center">
                  <a-button type="link" @click="handleLongTermClick(model, field)">长期</a-button>
                </div>
              </template>
            </a-range-picker>
          </template>

          <!-- 门禁选择组件 -->
          <template #doorAccessSelection>
            <div class="door-items-container">
              <!-- <SelectAccessFormItemList ref="doorListRef" @remove="removeDoorItem" v-model:list="doorList" @update="updateItemDetail" /> -->
              <!-- <div style="margin-bottom: 10px; text-align: right" v-if="!isView">
                <a-button type="primary" @click="handleAddDoor"> <plus-outlined />添加门禁 </a-button>
              </div> -->
              <BasicTable @register="registerTable">
                <template #action="{ record, column }">
                  <TableAction :actions="createActions(record)" />
                </template>
                <template #dockingDeptCode="{ record, text }">
                  <!-- <div v-if="record.editable">{{ record.powerList.map((item) => ({ label: item.dutyDeptName, value: item.dutyDeptCode })) }}</div> -->
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
                <template #publicDeviceCode="{ record, text }">
                  <div class="tags">
                    <!-- 查看按钮 -->
                    <template v-if="record?.deviceDOList && record.deviceDOList.length > 0">
                      <a-button type="link" @click="handleViewPublicDevice(record)">查看</a-button>
                    </template>
                    <template v-else>
                      <span>无</span>
                    </template>
                    <!-- <a-tag
                      :closable="!isView"
                      v-for="(item, index) in record?.deviceDOList"
                      :key="item.deviceCode"
                      @close.prevent="removePublicDevice(record, index)"
                    >
                      {{ item.deviceName }}
                    </a-tag> -->
                  </div>
                </template>
                <template #headerCell="{ column }">
                  <template v-if="column.dataIndex === 'deviceName'">
                    <span v-if="isCanApprove || isView">申请门禁</span>
                    <a-button type="primary" @click="handleAddDoor" v-else>添加门禁</a-button>
                  </template>
                  <template v-else>
                    {{ column.customTitle }}
                  </template>
                </template>
              </BasicTable>
            </div>
          </template>
          <template #secretAreaManagerSlot="{ model, field }">
            <a-form-item-rest>
              <person-select-input
                v-model:personList="chiefEmployee"
                :placeholder="'请选择本部门科长'"
                :multiple="false"
                :disabled="isView"
                @confirm="handleChiefEmployeeOk"
              />
            </a-form-item-rest>
          </template>
          <!-- 表单底部操作按钮 -->
          <!-- <template #formFooter>
            <div class="action-buttons">
              <a-button @click="goBack">返回</a-button>
              <a-button type="primary" @click="submitForm">提交</a-button>
            </div>
          </template> -->
        </BasicForm>
        <iframe v-if="isShowBpm" :src="iframeUrl" style="height: 1350px; width: 100%" id="bpmIframe" class="iframeClass"></iframe>
      </a-card>
    </div>
    <div class="bottom-view">
      <div class="action-buttons">
        <div class="action-buttons-sub" v-if="isCanApprove && isView">
          <a-button @click="auditProcessAction(false)">不通过</a-button>
          <a-button type="primary" @click="auditProcessAction(true)">通过</a-button>
          <a-button type="primary" ghost @click="handPartPass" v-if="doorList && doorList.length > 1">部分通过 </a-button>
        </div>

        <div class="action-buttons-pass" v-if="!isView">
          <a-button @click="goBack">返回</a-button>
          <a-button type="primary" @click="submitForm">提交</a-button>
        </div>
      </div>
    </div>
    <DoorAccessSelect @ok="onSelectedChange" v-model:value="doorSelectVisible" placeholder="请选择门禁" />
    <PublicDeviceModal @register="registerPublicDeviceModal" @success="handlePublicDeviceSuccess" />
    <PartPassModal @register="partPassModal" @success="partPassRequestSuccess"></PartPassModal>
  </PageWrapper>
</template>

<script lang="ts" setup>
  import { ref, onMounted, computed } from 'vue';
  import { BasicTable, useTable, TableAction, ActionItem } from '/@/components/Table';
  import '/@/assets/bpm/domain';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  import { Select } from 'ant-design-vue';
  import PublicDeviceModal from './components/PublicDeviceModal.vue';

  import { BasicForm, useForm } from '/@/components/Form';
  import { PageWrapper } from '/@/components/Page';
  import { useRouter } from 'vue-router';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { formSchema, tabelColumns } from './accessRequestForm.data';
  import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';
  import { saveAndCreateProcess, viewProcessDetail, auditProcess, listWithSecretBatch } from './accessRequestForm.api';
  import { useAccessRequestForm } from './useAccessRequest';
  import { useBpm } from './useBpm';
  import { useUserStore } from '/@/store/modules/user';

  const userInfo = useUserStore().getUserInfo;
  import { useTabs } from '/@/hooks/web/useTabs';
  import dayjs from 'dayjs';

  const router = useRouter();
  const { createMessage, createErrorModal, createConfirm } = useMessage();
  const { isShowBpm, iframeUrl, bpmId, isCanApprove, checkCanApprove } = useBpm({});
  import { getAuthConfig } from '/@/views/systemd/authConfig/authConfig.api';
  import { useModal } from '/@/components/Modal';
  import { getEipRoleByProcessId } from '/@/views/buildingAccessControl/accessRequestManageProcess/api';
  import PartPassModal from '/@/views/buildingAccessControl/accessRequestProcess/form/components/PartPassModal.vue';

  const doorId = ref([]);
  const doorSelectVisible = ref(false);
  let cancelAuthDays = ref(0);

  // 注册公共设备弹框
  const [registerPublicDeviceModal, { openModal: openPublicDeviceModal }] = useModal();

  const [partPassModal, { openModal: openPartPassModal }] = useModal();

  // 处理查看公共设备
  function handleViewPublicDevice(record) {
    openPublicDeviceModal(true, {
      deviceDOList: record.deviceDOList || [],
      isView: isView.value,
      record: record,
    });
  }

  // 处理公共设备弹框成功回调
  function handlePublicDeviceSuccess({ record, deviceList }) {
    // 更新表格中的数据
    record.deviceDOList = deviceList;
    doorList.value = [...getDataSource()];
    doorList.value = isCanApprove.value && isView.value ? doorList.value.filter((item) => Number(item.applyStatus) !== 2) : doorList.value;
    console.log('doorList.value111', doorList.value);
    // 强制表格刷新
    setTableData(doorList.value);
  }

  // 使用抽取出来的hooks
  const {
    isDetail,
    isEdit,
    processId,
    isView,
    currentUserInfo,
    chiefEmployee,
    doorList,
    datePickerOpen,
    initUserData,
    handleLongTermClick,
    removeDoorItem,
    updateItemDetail,
    onChiefEmployeeOk,
    isManage,
  } = useAccessRequestForm({});
  const { close } = useTabs();
  const editDataArr = ref([]); //编辑时需要替换的数据

  //是否显示部门负责人
  const isShowDutyPerson = computed(() => {
    return currentUserInfo.value?.dutyPersonList?.length > 0;
  });
  //是否显示维护门禁管理员 提醒
  const isShowDoorManagerTips = computed(() => {
    return !isShowDutyPerson.value && currentUserInfo.value?.buildAdminList?.length == 0;
  });
  const doorManagerTipsStr = computed(() => {
    const str = currentUserInfo.value?.buildAdminList?.map((item) => `${item.empName}${item.empNo}`).join(' ');
    return str ? `您所在组织没有维护门禁管理员，请联系系统管理员 ${str}` : '您所在组织没有维护门禁管理员，请联系系统管理员';
  });
  // 使用useForm替代原来的ref方式
  const [registerForm, { validate, getFieldsValue, setFieldsValue, clearValidate }] = useForm({
    labelWidth: 100,
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    // 添加字段映射，自动将日期字段映射为后端需要的格式
    fieldMapToTime: [
      // 正确格式：[单个源字段, [目标开始字段, 目标结束字段], 日期格式]
      ['applyDateRange', ['powerBeginTime', 'powerEndTime'], 'YYYY-MM-DD'],
    ],
    disabled: isView.value, // 如果是查看模式，则禁用表单
  });

  function handleAddDoor() {
    console.log('添加门禁');
    doorSelectVisible.value = true;
  }

  function createActions(record): ActionItem[] {
    return [
      {
        label: '删除',
        color: 'error',
        onClick: handleDelete.bind(null, record),
      },
    ];
  }

  function handleRecordChange(record, value) {
    console.log('获取的值是', value);
    record.acrossEmployeeName = value.option.enList.map((item) => `${item.employeeName}${item.employeeCode}`).join('/');
    record.acrossEmployee = value.option.enList;
    record.buildSysManagerRole = value.option.buildSysManagerRole;
  }

  //批量获取门禁信息
  async function getListWithSecretBatch(deviceCodes) {
    const res = await listWithSecretBatch(deviceCodes);
    const newDoorList =
      res?.map((item) => {
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
          editable: true,
        };
      }) || [];
    //过滤下如果原来有就使用原来的
    const oldDoorList = getDataSource();
    const resultList = oldDoorList.concat(
      newDoorList.filter((item) => {
        return !oldDoorList.some((oldItem) => oldItem.deviceCode === item.deviceCode);
      })
    );
    doorList.value = isCanApprove.value && isView.value ? resultList.filter((item) => Number(item.applyStatus) !== 2) : resultList;
    console.log('doorList.value222', doorList.value);
    setTableData(resultList);
  }

  const [registerTable, { deleteTableDataRecord, getDataSource, setTableData }] = useTable({
    dataSource: doorList.value,
    columns: tabelColumns,
    showIndexColumn: true,
    showTableSetting: false,
    showActionColumn: isView.value ? false : true,
    bordered: true,
    // ellipsis: true,
    actionColumn: {
      width: 150,
      title: '操作',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
    pagination: false,
    ellipsis: false,
    canResize: true,
    rowKey: 'deviceCode',
  });

  function onSelectedChange(selected) {
    console.log('门禁选择', selected);
    getListWithSecretBatch(selected.map((item) => item.key).join(','));
  }

  function handleDelete(record) {
    console.log('删除记录', record);
    deleteTableDataRecord(record.deviceCode);
    return;
    createConfirm({
      iconType: 'warning',
      title: '确定删除',
      content: '',
      onOk: async () => {
        deleteTableDataRecord(record.deviceCode);
      },
    });
    // 从列表数据中移除此条记录
    // removeDoorItem(record);
  }

  //获取授权配置
  async function getAuthConfigResult() {
    const res = await getAuthConfig();
    console.log('res的值是', res);
    cancelAuthDays.value = res.cancelAccessNum && res.cancelAccessNum - 7 > 0 ? res.cancelAccessNum - 7 : 0;
  }

  onMounted(async () => {
    getAuthConfigResult();
    // 获取员工所属楼栋信息
    // 如果是编辑模式，可以在这里加载已有数据
    if (isDetail.value) {
      // 这里可以添加编辑模式下的特殊处理逻辑
      const res = await viewProcessDetail(processId.value, isManage.value);
      const { openList } = res;
      doorId.value = openList != null && openList.length > 0 ? openList.filter((item) => item.applyStatus != '2').map((item) => item.id) : [];

      await checkCanApprove(processId.value, res.status);
      handelDetail(res);
      const userData = await initUserData(res.createEmployeeNo);
      setFieldsValue(userData);
    } else {
      const userData = await initUserData();
      setFieldsValue(userData);
    }
    clearValidate();

    if (isEdit.value) {
      getEipRoleByProcessIdRequest();
    }
  });

  function getEipRoleByProcessIdRequest() {
    getEipRoleByProcessId({ processId: processId.value }).then((res) => {
      editDataArr.value = res;
    });
  }

  async function handelDetail(res) {
    // const deviceCodes = res.openList.map((item) => item.deviceCode).join(',');

    const deviceCodes = res.openList.map((item) => item.deviceCode).join(',');
    const deviceItemList = await listWithSecretBatch(deviceCodes);
    let tempDoorList = [];
    console.log('doorList.value:444', res.openList);
    console.log('doorList.value:555', deviceItemList);
    res.openList.forEach((openItem) => {
      const doorItem = deviceItemList.find((door) => door.deviceCode === openItem.deviceCode);
      if (doorItem) {
        const powerItem = doorItem.powerList.find((item) => item.dutyDeptCode === openItem.dockingDeptCode);
        if (powerItem) {
          doorItem.acrossEmployeeName = powerItem.enList.map((person) => `${person.employeeName}${person.employeeCode}`).join('/');
          doorItem.acrossEmployee = powerItem.enList;
          doorItem.buildSysManagerRole = powerItem.buildSysManagerRole;
          doorItem.dockingDeptCode = {
            value: openItem.dockingDeptCode,
            label: openItem.dockingDeptName || powerItem?.dutyDeptName,
          };
        } else {
          if (doorItem.powerList?.length === 1) {
            doorItem.dockingDeptCode = doorItem.powerList.map((dutyDeptItem) => {
              return {
                value: dutyDeptItem.dutyDeptCode,
                label: dutyDeptItem.dutyDeptName,
              };
            })[0];
            doorItem.acrossEmployeeName = doorItem.powerList[0].enList.map((item) => `${item.employeeName}${item.employeeCode}`).join('/');
            doorItem.acrossEmployee = doorItem.powerList[0].enList;
            doorItem.buildSysManagerRole = doorItem.powerList[0].buildSysManagerRole;
          }
        }
        console.log('openItem******************', openItem);
        const publicDeviceNameList = (openItem.publicDeviceName || '').split(',').filter(Boolean);
        const publicDeviceCodeList = (openItem.publicDeviceCode || '').split(',').filter(Boolean);
        doorItem.deviceDOList = publicDeviceNameList.map((name, index) => {
          return {
            deviceCode: publicDeviceCodeList[index],
            deviceName: name,
          };
        });
        tempDoorList.push({
          ...doorItem,
          id: openItem.id,
          applyStatus: openItem.applyStatus,
          empowerType: openItem.empowerType,
          empowerAreaName: openItem.empowerAreaName,
          refuseReason: !isView.value ? '' : openItem.refuseReason,
          editable: isView.value ? false : true,

          topSecretEmpCode: doorItem.topSecretEmpCode,
          topSecretEmpLogin: doorItem.topSecretEmpLogin,
          topSecretEmpName: doorItem.topSecretEmpName,
        });
      } else {
        /*todo 门禁已删除*/
        // tempDoorList.push(openItem);
      }
    });

    // const tempDoorList = (await listWithSecretBatch(deviceCodes)).map((item, index) => {
    //   const powerItem = item.powerList.find((item) => item.dutyDeptCode === res.openList[index].dockingDeptCode);
    //   item.acrossEmployeeName = powerItem.enList.map((person) => `${person.employeeName}${person.employeeCode}`).join('/');
    //   item.acrossEmployee = powerItem.enList;
    //   item.buildSysManagerRole = powerItem.buildSysManagerRole;

    //   const openItem = res.openList[index];
    //   console.log('openItem******************', openItem);
    //   const publicDeviceNameList = (openItem.publicDeviceName || '').split(',').filter(Boolean);
    //   const publicDeviceCodeList = (openItem.publicDeviceCode || '').split(',').filter(Boolean);
    //   item.deviceDOList = publicDeviceNameList.map((name, index) => {
    //     return {
    //       deviceCode: publicDeviceCodeList[index],
    //       deviceName: name,
    //     };
    //   });
    //   return {
    //     ...item,
    //     editable: isView.value ? false : true,
    //     dockingDeptCode: {
    //       value: res.openList[index].dockingDeptCode,
    //       label: res.openList[index].dockingDeptName || powerItem.dutyDeptName,
    //     },
    //   };
    // });
    console.log('门禁信息---------', tempDoorList);

    // const newDoorList = res.openList.map((item) => {
    //   const tempDoor = tempDoorList.find((tempItem) => tempItem.deviceCode === item.deviceCode);
    //   return {
    //     deviceCode: item.deviceCode,
    //     deviceName: item.deviceName, // 门禁名称
    //     deviceDOList: item.publicDeviceName.split(',').map((name, index) => {
    //       return {
    //         deviceCode: item.publicDeviceCode.split(',')[index],
    //         deviceName: name,
    //       };
    //     }),
    //     dockingDeptCode: item.dockingDeptCode,
    //     acrossEmployeeLogin: item.acrossEmployeeLogin,
    //     acrossEmployeeName: item.acrossEmployeeName,
    //     isDetail: true, // 是否是详情模式
    //     applyStatus: isEdit.value ? '' : item.applyStatus,
    //     refuseReason: isEdit.value ? '' : item.refuseReason,
    //   };
    // });
    doorList.value = isCanApprove.value && isView.value ? tempDoorList.filter((item) => Number(item.applyStatus) !== 2) : tempDoorList;
    console.log('doorList.value666', doorList.value);
    setTableData(doorList.value);
    chiefEmployee.value = [{ name: res.chiefEmployeeName, id: res.chiefEmployeeLogin }];
    const fromData = {
      applyDateRange: res.openList?.length > 0 ? [res.openList[0].powerBeginTime, res.openList[0].powerEndTime] : [],
      applyReason: res.applyReason,
      chiefEmployeeName: res.chiefEmployeeName,
      chiefEmployeeLogin: res.chiefEmployeeLogin,
    };
    if (isEdit.value) {
      fromData.applyDateRange = [dayjs().format('YYYY-MM-DD'), res.openList[0].powerEndTime];
    }
    setFieldsValue(fromData);
  }

  // 门禁选择弹窗
  const doorSelectModalVisible = ref(false);

  // 门禁列表组件引用
  const doorListRef = ref();

  // 确认门禁选择
  function confirmDoorSelect() {
    doorSelectModalVisible.value = false;
    createMessage.success('已选择门禁');
  }

  // 提交表单
  async function submitForm() {
    try {
      const dataSource = getDataSource();
      console.log('门禁信息', dataSource);
      if (dataSource.length === 0) {
        createMessage.error('请至少添加一个门禁');
        return;
      }
      let needArr = [];
      dataSource.forEach((door, index) => {
        if (!door.dockingDeptCode) {
          needArr.push(index + 1);
        }
      });
      if (needArr.length > 0) {
        createMessage.error(`第${needArr.join(',')}行申请信息未选择对接部门，请选择`);
        return false;
      }
      let openDOList = [] as any[];
      for (const door of dataSource) {
        // console.log('门禁的值是**************8', door);
        // if (!door.dockingDeptCode) {
        //   createMessage.warn('请完善门禁信息');
        //   return;
        // }
        let changeItem = editDataArr.value.find((item) => item.dutyDeptCode === door.dockingDeptCode.value);

        openDOList.push({
          // powerBeginTime: values.powerBeginTime,
          // powerEndTime: values.powerEndTime,
          acrossEmployeeLogin: door.acrossEmployee.map((item: any) => item.employeeLogin).join(',') || '',
          acrossEmployeeName: door.acrossEmployee.map((item: any) => item.employeeName).join(',') || '',
          deviceCode: door.deviceCode || '',
          dockingDeptCode: door.dockingDeptCode.value || '',
          dockingDeptName: door.dockingDeptCode.label || '',
          buildSysManagerRole: isEdit.value && changeItem ? changeItem.buildSysManagerRole : door.buildSysManagerRole || '',
          topSecretEmployeeLogin: door.topSecretEmpLogin || '',
          topSecretEmployeeName: door.topSecretEmpName || '',
          publicDeviceCode: door.deviceDOList?.map((item: any) => item.deviceCode).join(',') || '',
          publicDeviceName: door.deviceDOList?.map((item: any) => item.deviceName).join(',') || '',
        });
      }
      console.log('获取的所有的数据是', dataSource);
      // 验证所有门禁表单
      // const doorFormsValid = await doorListRef.value.validateAll();
      // console.log('门禁表单验证结果', doorFormsValid);
      await validate();

      const values = getFieldsValue();
      const { chiefEmployeeLogin, chiefEmployeeName, applyReason } = values;
      if (isShowDoorManagerTips.value) {
        createErrorModal({ title: '错误提示', content: doorManagerTipsStr.value });
        return;
      }
      openDOList = openDOList.map((item) => {
        return {
          ...item,
          powerBeginTime: values.powerBeginTime,
          powerEndTime: values.powerEndTime,
        };
      });
      const { orgCodePath, orgNamePath } = currentUserInfo.value;
      const params = {
        chiefEmployeeLogin, //本部门科长登录名
        chiefEmployeeName, //本部门科长姓名
        createDeptCode: orgCodePath, //申请人编码全路径
        createDeptName: orgNamePath, //申请人部门全路径
        createEmployeeLogin: userInfo.userName, //申请人EIP登录名
        createEmployeeName: userInfo.realName, //申请人姓名
        createEmployeeNo: userInfo.workNo, //申请人员工号
        powerType: '1', //1-开通 2-关闭  1,2 开通+关闭
        roleType: '1', //1-员工 2-管理员 3-门禁管理员
        applyReason, //申请事由
        openDOList: openDOList,
      };
      createConfirm({
        iconType: 'warning',
        title: '提示',
        content: '请确认已与本部门门禁管理员和对接部门门禁管理员沟通！',
        okText: '已沟通，提交',
        onOk: async () => {
          // 提交表单数据
          console.log('提交到接口的参数', params);
          // return;
          await saveAndCreateProcess(params);
          createMessage.success('提交成功');
          router.back();
        },
      });
    } catch (error) {
      console.error('验证失败', error);
    }
  }

  async function handPartPass() {
    console.log('doorList.value>>>>>>>>>>', doorList.value);
    const bpmData = await getBpmData();
    const param = {
      curPersonLogin: userInfo.userName,
      curPersonName: userInfo.realName,
      flowId: bpmId.value,
      fdParameterJson: JSON.stringify(bpmData),
      id: processId.value,
    };

    openPartPassModal(true, {
      doorList: doorList.value,
      param: param,
    });
  }

  function partPassRequestSuccess() {
    createMessage.success('操作成功');
    close();
  }

  async function auditProcessAction(isPass: boolean) {
    const bpmData = await getBpmData();
    const parmas = {
      applyStatus: isPass ? '1' : '2',
      curPersonLogin: userInfo.userName,
      curPersonName: userInfo.realName,
      flowId: bpmId.value,
      openIdStr: doorList.value != null && doorList.value.length > 0 ? doorList.value.map((item) => item.id).join(',') : '',
      fdParameterJson: JSON.stringify(bpmData),
      id: processId.value,
    };
    console.log('审核参数', parmas);
    await auditProcess(parmas);
    createMessage.success('操作成功');
    close();
  }

  // 获取流程
  const getBpmData = () => {
    return new Promise((resolve, reject) => {
      const flowIFrame = document.getElementById('bpmIframe');

      const domain = window?.domain || '';
      console.log('111a', window);
      if (domain) {
        console.log('222');
        domain.call(flowIFrame.contentWindow, 'lbpm_getApprovalData', [], function (data) {
          console.log('444');
          const res = JSON.parse(data);
          const result = res['sysWfBusinessForm.fdParameterJson'];
          if (result) {
            resolve(JSON.parse(result));
          }
          reject('获取iframe数据失败');
        });
      } else {
        console.log('333');
        reject('获取iframe数据失败');
      }
    });
  };

  // 处理科长选择确认
  function handleChiefEmployeeOk() {
    const chiefData = onChiefEmployeeOk();
    setFieldsValue(chiefData);
  }

  // 返回
  function goBack() {
    router.back();
  }

  // 禁用日期
  const disabledDate = (current) => {
    return (current && current < dayjs().startOf('day')) || current > dayjs('2099-12-31');
  };

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
</script>

<style lang="less" scoped>
  .access-request-form {
    padding: 16px 16px 64px 16px;
    background-color: #fff;

    ::v-deep .ant-form-item-control {
      max-width: 100%;
    }

    .door-items-container {
      ::v-deep .ant-table-body {
        max-height: 500px !important;
      }
    }

    .tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      justify-content: center;

      .ant-tag {
        cursor: pointer;
      }
    }

    .iframeClass {
      width: 100%;
    }

    .form-content {
      padding: 16px;
    }

    .door-access-info {
      margin: 0 0 16px 0;

      .door-access-info-title {
        margin-bottom: 8px;
        font-weight: 500;
      }

      .door-access-info-content {
        font-size: 14px;
        line-height: 1.6;
        border: 1px solid #ff4d4f;
        border-radius: 4px;
        padding: 10px;
        background-color: #fff1f0;

        p {
          margin-bottom: 4px;
        }
      }
    }

    .createDeptName {
      .required-mark-container {
        display: flex;
        color: #0079fe;
        font-size: 13px;

        .required-mark-name {
        }

        .required-mark-value {
          display: flex;

          span {
            margin-right: 4px;
          }
        }
      }

      .required-mark-container-tips {
        display: flex;
        color: #0079fe;
        font-size: 12px;
      }
    }

    .date-separator {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 32px;
      font-weight: bold;
      color: #999;
    }

    .door-access-card {
      border: 1px solid #e8e8e8;
      border-radius: 4px;
      margin-bottom: 16px;

      .door-card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 16px;
        background-color: #f5f5f5;
        border-bottom: 1px solid #e8e8e8;

        .header-title {
          font-weight: 500;
        }

        .header-actions {
          display: flex;
          gap: 8px;
        }
      }

      .door-card-content {
        padding: 16px;

        .door-row {
          .select-door-btn-wrapper {
            margin-bottom: 16px;
          }

          .target-dept-form-item {
            margin-top: 0;
          }
        }
      }
    }

    .dept-info {
      margin-bottom: 16px;

      .dept-info-text {
        color: #1890ff;
      }
    }

    .date-range-section {
      display: flex;
      align-items: center;
      margin-bottom: 16px;

      .date-range-wrapper {
        display: flex;
        align-items: center;
        margin-left: 8px;

        .date-picker {
          width: 180px;
        }

        .date-separator {
          margin: 0 8px;
        }
      }
    }

    .reason-section {
      margin-bottom: 16px;

      .reason-label {
        margin-bottom: 8px;
      }
    }

    .door-manager-section {
      margin-bottom: 16px;

      .door-manager-label {
        margin-bottom: 8px;
      }
    }

    .required-mark {
      color: #ff4d4f;
      margin-right: 4px;
      font-weight: bold;
    }

    .blue-text {
      color: #1890ff;
    }

    .date-range-wrapper {
      display: flex;
      align-items: center;

      .long-term-btn {
        margin-left: 8px;
        white-space: nowrap;
      }
    }

    .table-top-buttons {
      display: flex;
      justify-content: flex-end;
      margin-bottom: 16px;
    }
  }

  .bottom-view {
    box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.1);
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #fff;

    .action-buttons {
      display: flex;
      width: 100%;
      justify-content: center;
      gap: 16px;

      .action-buttons-sub,
      .action-buttons-pass {
        margin: 16px 0;

        display: flex;
        gap: 16px;

        .ant-btn {
          width: 100px;
        }
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
