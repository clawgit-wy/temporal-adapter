<template>
  <PageWrapper title="楼宇门禁申请" dense :contentStyle="{ background: '#fff' }" contentFullHeight>
    <div class="access-request-form">
      <a-card title="楼宇门禁申请流程">
        <BasicForm @register="registerForm" :showActionButtonGroup="false">
          <!-- 门禁新增提醒 - 放在部门和已授权门禁字段后面 开通-->
          <template #doorAccessTips>
            <div class="door-access-info">
              <div class="door-access-info-title"><span class="required-mark">*</span>开通权限：</div>
              <div class="door-access-info-content">
                <p class="blue-text">1.流程审批通过后默认开通楼宇门禁所在厂区大门权限</p>
                <p class="blue-text">2.按组织授权默认为此组织架构下所有成员授权，后续该部门新增成员将自动下发门禁权限</p>
                <p class="blue-text"
                  >3.如您管辖区域所在办公楼公共区域（如：1楼、-1楼）存在门禁管制，请同步申请开通管辖区域所在办公楼公共区域门禁权限，否则将影响员工进出。</p
                >
              </div>
            </div>
          </template>

          <!-- 日期选择器和长期按钮 -->
          <!-- <template #applyDateRangeSlot="{ model, field }">
            <a-range-picker v-model:value="model[field]" v-model:open="datePickerOpen" :showToday="false">
              <template #renderExtraFooter>
                <div style="text-align: center">
                  <a-button type="link" @click="handleLongTermClick(model, field)">长期</a-button>
                </div>
              </template>
            </a-range-picker>
          </template> -->

          <!-- 门禁选择组件 -->
          <template #openDOList>
            <div class="door-items-container">
              <SelectAccessFormItemList
                ref="openDOListRef"
                :open="1"
                :isDetail="isDetail"
                @remove="removeDoorItem"
                @copy="copyDoorItem"
                v-model:list="openDOList"
                @update="updateItemDetail"
                @selectedType="updateItemType"
                @selectedDept="updateItemDept"
                @selectedPeople="updateItemPeople"
                @selectedDate="updateItemDate"
                @selectedDockingDept="updateItemDockDept"
              />
            </div>
          </template>

          <!-- 关闭 -->
          <template #doorAccessCloseTips>
            <div class="door-access-info">
              <div class="door-access-info-title"><span class="required-mark">*</span>关闭权限：</div>
              <div class="door-access-info-content">
                <p class="blue-text">当员工关闭门禁为本楼栋内最后一个非公共区域门禁，系统将关闭楼栋内所有门禁权限。</p>
              </div>
            </div>
          </template>
          <!-- 门禁选择组件 -->
          <template #closeList>
            <div class="door-items-container">
              <SelectAccessFormItemList
                ref="closeListRef"
                :open="2"
                :isDetail="isDetail"
                @remove="removeDoorItem"
                @copy="copyDoorItem"
                v-model:list="closeList"
                @update="updateItemDetail"
                @selectedType="updateItemType"
                @selectedDept="updateItemDept"
                @selectedPeople="updateItemPeople"
              />
            </div>
          </template>

          <template #secretAreaManagerSlot>
            <a-form-item-rest>
              <person-select-input
                v-model:personList="chiefEmployee"
                :placeholder="'请选择绝密区负责人'"
                :multiple="false"
                :disabled="isView"
                @confirm="onChiefEmployeeOk"
              />
            </a-form-item-rest>
          </template>
        </BasicForm>
        <iframe v-if="isShowBpm" :src="iframeUrl" style="height: 1350px; width: 100%" id="bpmIframe" class="iframeClass"></iframe>
      </a-card>
    </div>
    <div class="bottom-view">
      <div class="action-buttons">
        <div class="action-buttons-sub" v-if="isCanApprove && isView">
          <a-button @click="auditProcessAction(false)">不通过</a-button>
          <a-button type="primary" @click="auditProcessAction(true)">通过</a-button>
        </div>
        <div class="action-buttons-pass" v-if="!isView">
          <a-button @click="goBack">返回</a-button>
          <a-button type="primary" @click="submitForm">提交</a-button>
        </div>
      </div>
    </div>
  </PageWrapper>
</template>

<script lang="ts" setup>
  import { ref, onMounted, computed } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form';
  import { PageWrapper } from '/@/components/Page';
  import { useRouter, useRoute } from 'vue-router';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { createFormData, formSchema } from './accessRequestForm.data';
  import { createDoorItem } from './accessRequestForm.data';
  import SelectAccessFormItemList from './SelectAccessFormItemList.vue';
  import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';
  import dayjs from 'dayjs';
  import { getEmployeeByWorkNo, saveAndCreateProcess, checkBuildRoleType, showProcessDetail, auditProcess } from './accessRequestForm.api';
  import { useUserStore } from '/@/store/modules/user';
  import { useGlobSetting } from '/@/hooks/setting';
  import { useAccessRequestForm } from '../accessRequestProcess/form/useAccessRequest';
  import { useBpm } from '../accessRequestProcess/form/useBpm';
  import { Modal, message } from 'ant-design-vue';
  import { useTabs } from '/@/hooks/web/useTabs';

  const { close } = useTabs();
  const userInfo = useUserStore().getUserInfo;

  const router = useRouter();
  const route = useRoute();
  const { createMessage } = useMessage();
  const currentUserInfo = ref({});
  const currentUserRoleType = ref('');
  const chiefEmployee = ref([]); // 本部门科长
  const openDOList = ref([]); // 门禁列表
  const openDOIdList = ref([]); // 门禁列表
  const closeList = ref([]); // 门禁列表
  const closeIdList = ref([]); // 门禁列表

  const { isDetail, processId, isView } = useAccessRequestForm();

  const { formInstanceId } = route.query;
  const { isShowBpm, iframeUrl, bpmId, isCanApprove, checkCanApprove } = useBpm();
  const queryFormInstanceId = ref(formInstanceId);

  // 使用useForm替代原来的ref方式
  const [registerForm, { validate, getFieldsValue, setFieldsValue, updateSchema }] = useForm({
    labelWidth: 100,
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    disabled: isView.value, // 如果是查看模式，则禁用表单
  });

  onMounted(async () => {
    // 获取员工所属楼栋信息
    currentUserInfo.value = await getEmployeeByWorkNo({ workNo: userInfo.workNo });
    setFieldsValue({
      createEmployeeName: currentUserInfo.value.empName,
      createEmployeeNo: currentUserInfo.value.empNo,
      createEmployeeLogin: currentUserInfo.value.empLogin,
      createDeptName: currentUserInfo.value.orgNamePath,
      createDeptCode: currentUserInfo.value.orgCodePath,
      processSubject: `${dayjs(new Date()).format('YYYY-MM-DD')}-${currentUserInfo.value.empName}-楼宇门禁申请流程`,
    });
    currentUserRoleType.value = await checkBuildRoleType();
    if (currentUserRoleType.value == 3) {
      updateSchema([
        {
          field: 'chiefEmployeeName',
          required: true,
        },
        {
          field: 'createDeptName',
          label: '部门',
          component: 'Input',
          required: false,
          show: true,
          componentProps: {
            placeholder: '默认带出',
            disabled: true,
          },
          colProps: { span: 12 },
        },
      ]);
    }

    if (isDetail.value) {
      const res = await showProcessDetail({ processId: queryFormInstanceId.value });
      await checkCanApprove(queryFormInstanceId.value, res.status);
      handelDetail(res);
    }
  });

  function handelDetail(res) {
    const { openList, closeList: tempCloseList, chiefEmployeeName, chiefEmployeeLogin } = res;
    openDOIdList.value = openList != null && openList.length > 0 ? openList.map((item) => item.id) : [];
    closeIdList.value = tempCloseList != null && tempCloseList.length > 0 ? tempCloseList.map((item) => item.id) : [];
    console.log('详情==', res);
    openDOList.value = openList;
    closeList.value = tempCloseList;
    chiefEmployee.value =
      chiefEmployeeName && chiefEmployeeLogin ? [{ name: chiefEmployeeName, email: `${chiefEmployeeLogin}@yutong.com`, id: chiefEmployeeLogin }] : '';
    setFieldsValue({
      ...res,
    });
  }

  // 门禁选择弹窗
  const doorSelectModalVisible = ref(false);

  // 门禁列表组件引用
  const openDOListRef = ref();
  const closeListRef = ref();

  // 移除门禁项
  function removeDoorItem(item, index, open) {
    if (open == 1) {
      openDOList.value.splice(index, 1);
    }
    if (open == 2) {
      closeList.value.splice(index, 1);
    }
  }
  // 复制门禁项
  function copyDoorItem(item, index, open) {
    console.log('复制后的数据=开始=', item);

    if (open == 1) {
      openDOList.value.splice(index + 1, 0, item);
    }
    if (open == 2) {
      closeList.value.splice(index + 1, 0, item);
    }
    console.log('复制后的数据==', openDOList.value, closeList.value);
  }
  // 更新门禁项详情
  function updateItemDetail(detail: any, index: number, open) {
    console.log('更新信息item==', detail);
    if (open == 1 && index >= 0 && index < openDOList.value.length) {
      openDOList.value.splice(index, 1, detail);
    }
    if (open == 2 && index >= 0 && index < closeList.value.length) {
      closeList.value.splice(index, 1, detail);
    }
  }
  // 更新类型
  function updateItemType(type: any, index: number, open) {
    console.log('更新类型item==', type, index);
    if (open == 1 && index >= 0 && index < openDOList.value.length) {
      openDOList.value[index]['empowerType'] = type;
      openDOList.value[index]['empowerAreaName'] = undefined;
      openDOList.value[index]['empowerAreaCode'] = undefined;
    }
    if (open == 2 && index >= 0 && index < closeList.value.length) {
      closeList.value[index]['empowerType'] = type;
      closeList.value[index]['empowerAreaName'] = undefined;
      closeList.value[index]['empowerAreaCode'] = undefined;
    }
  }
  // 更新部门
  function updateItemDept(dept: any, index: number, open) {
    console.log('更新部门item==', dept, index);
    if (open == 1 && index >= 0 && index < openDOList.value.length) {
      openDOList.value[index]['empowerAreaName'] = dept.namePath;
      openDOList.value[index]['empowerAreaCode'] = dept.codePath;
    }
    if (open == 2 && index >= 0 && index < closeList.value.length) {
      closeList.value[index]['empowerAreaName'] = dept.namePath;
      closeList.value[index]['empowerAreaCode'] = dept.codePath;
    }
  }
  // 更新人员
  function updateItemPeople(persons: any, index: number, open) {
    console.log('更新人员item==', persons, index, openDOList.value, closeList.value);
    const leaderEmployeeName = persons.map((p) => p.name).join(';');
    const leaderEmployeeCode = persons.map((p) => p.id).join(';');
    if (open == 1 && index >= 0 && index < openDOList.value.length) {
      openDOList.value[index]['empowerAreaName'] = leaderEmployeeName;
      openDOList.value[index]['empowerAreaCode'] = leaderEmployeeCode;
    }
    if (open == 2 && index >= 0 && index < closeList.value.length) {
      closeList.value[index]['empowerAreaName'] = leaderEmployeeName;
      closeList.value[index]['empowerAreaCode'] = leaderEmployeeCode;
    }
    console.log('更新人员item=后==', openDOList.value, closeList.value);
  }
  // 更新日期
  function updateItemDate(date: any, index: number, open) {
    console.log('更新日期item==', date, date[0]);
    if (open == 1 && index >= 0 && index < openDOList.value.length) {
      openDOList.value[index]['powerBeginTime'] = date[0];
      openDOList.value[index]['powerEndTime'] = date[1];
    }
    if (open == 2 && index >= 0 && index < closeList.value.length) {
      closeList.value[index]['powerBeginTime'] = date[0];
      closeList.value[index]['powerEndTime'] = date[1];
    }
  }

  // 更新对接部门
  function updateItemDockDept(dockDept: any, index: number) {
    console.log('更新对接部门item==', dockDept);
    if (index >= 0 && index < openDOList.value.length) {
      openDOList.value[index]['dockingDeptName'] = dockDept.dutyDeptName;
      openDOList.value[index]['dockingDeptCode'] = dockDept.dutyDeptCode;
    }
  }

  // 提交表单
  async function submitForm() {
    try {
      if (openDOList.value.length === 0 && closeList.value.length === 0) {
        createMessage.error('请至少添加一个门禁');
        return;
      }
      const validateData = await validate();
      const { chiefEmployeeLogin, chiefEmployeeName, applyReason, powerType } = validateData;

      console.log('门禁表单验证结果11==', validateData);

      // const values = getFieldsValue();
      // console.log('获取的所有的value的值是', values);
      // 移除非表单字段，不提交给后端
      // const { chiefEmployeeLogin, chiefEmployeeName, applyReason, powerType } = values;

      const { orgCodePath, orgNamePath } = currentUserInfo.value;
      let parmas = {
        chiefEmployeeLogin, //本部门科长登录名
        chiefEmployeeName, //本部门科长姓名
        createDeptCode: orgCodePath, //申请人编码全路径
        createDeptName: orgNamePath, //申请人部门全路径
        createEmployeeLogin: userInfo.userName, //申请人EIP登录名
        createEmployeeName: userInfo.realName, //申请人姓名
        createEmployeeNo: userInfo.workNo, //申请人员工号
        powerType, //1-开通 2-关闭  1,2 开通+关闭
        roleType: currentUserRoleType.value, // 1-员工 2-管理员 3-门禁管理员
        applyReason, //申请事由
      };
      if (powerType.includes('1')) {
        const openDOFormsValid = await openDOListRef.value.validateAll();
        const openDOListCopy = [] as any[];
        let count = 0;
        for (const door of openDOList.value) {
          let doorFormItem = openDOFormsValid.find(
            (item) =>
              item.deviceCode === door.deviceCode && item.empowerAreaCode == door.empowerAreaCode && item.dockingDeptCode == door.dockingDeptCode
          );
          if (doorFormItem) {
            delete doorFormItem.acrossEmployeeNameView;
            delete doorFormItem.topSecretEmployeeLoginView;
            delete doorFormItem.doorAccessSelection;
            console.log('doorFormItem***********', doorFormItem);
            const beginDate = dayjs(doorFormItem.powerTime[0]).format('YYYY-MM-DD');
            const endDate = dayjs(doorFormItem.powerTime[1]).format('YYYY-MM-DD');
            openDOListCopy.push({
              ...doorFormItem,
              powerBeginTime: beginDate,
              powerEndTime: endDate,
              publicDeviceCode: door.deviceDOList?.map((item) => item.deviceCode).join(','),
              publicDeviceName: door.deviceDOList?.map((item) => item.deviceName).join(','),
            });
          }

          count++;
        }
        parmas.openDOList = openDOListCopy;
        console.log('门禁表单验证结果=开启==', openDOFormsValid);
      }
      if (powerType.includes('2')) {
        const closeFormsValid = await closeListRef.value.validateAll();
        const closeListCopy = [] as any[];
        let closeCount = 0;
        for (const door of closeList.value) {
          let doorFormItem = closeFormsValid.find((item) => item.deviceCode === door.deviceCode && item.empowerAreaCode == door.empowerAreaCode);
          if (doorFormItem) {
            delete doorFormItem.acrossEmployeeNameView;
            delete doorFormItem.topSecretEmployeeLoginView;
            delete doorFormItem.doorAccessSelection;
            console.log('doorFormItem***********', doorFormItem);
            closeListCopy.push({
              ...doorFormItem,
            });
          }

          closeCount++;
        }
        parmas.closeList = closeListCopy;
        console.log('门禁表单验证结果=关闭==', closeFormsValid);
      }

      // 提交formValues而不是values
      console.log('提交到接口的参数', parmas);
      if (parmas.openDOList != null && parmas.openDOList.length > 0) {
        Modal.confirm({
          title: '提示',
          content: '开通权限请确认已与对接部门门禁管理员和绝密区负责人沟通！',
          async onOk() {
            let res = await saveAndCreateProcess(parmas);
            console.log('门禁提交结果==', res);
            if (res && res.success) {
              createMessage.success('提交成功');
              router.back();
            } else {
              createMessage.error(res.mes || '操作失败');
            }
          },
          onCancel() {},
        });
      } else {
        let res = await saveAndCreateProcess(parmas);
        console.log('门禁提交结果==', res);
        if (res && res.success) {
          createMessage.success('提交成功');
          router.back();
        } else {
          createMessage.error(res.mes || '操作失败');
        }
      }
    } catch (error) {
      console.error('验证失败', error);
    }
  }

  async function auditProcessAction(isPass: boolean) {
    const parmas = {
      applyStatus: isPass ? '1' : '2',
      curPersonLogin: userInfo.userName,
      curPersonName: userInfo.realName,
      flowId: bpmId.value,
      id: processId.value,
      openIdStr: openDOIdList.value != null && openDOIdList.value.length > 0 ? openDOIdList.value.join(',') : '',
      closeIdStr: closeIdList.value != null && closeIdList.value.length > 0 ? closeIdList.value.join(',') : '',
    };
    console.log('审核参数', parmas);
    await auditProcess(parmas);
    createMessage.success('操作成功');
    close();
  }

  function onChiefEmployeeOk() {
    console.log('onChiefEmployeeOk');
    setFieldsValue({
      chiefEmployeeName: chiefEmployee.value[0].name,
      chiefEmployeeLogin: chiefEmployee.value[0].email?.split('@')[0],
    });
  }
  // 返回
  function goBack() {
    router.back();
  }
</script>

<style lang="less" scoped>
  .access-request-form {
    padding: 16px 16px 64px 16px;
    background-color: #fff;

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

    .action-buttons {
      display: flex;
      width: 100%;
      justify-content: center;
      gap: 16px;
      margin-top: 32px;
      .action-buttons-sub,
      .action-buttons-pass {
        display: flex;
        gap: 16px;
        .ant-btn {
          width: 100px;
        }
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
