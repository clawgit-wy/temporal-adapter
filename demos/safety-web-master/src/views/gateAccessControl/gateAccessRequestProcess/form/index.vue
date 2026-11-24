<template>
  <PageWrapper title="大门权限申请" dense :contentStyle="{ background: '#fff' }" contentFullHeight>
    <template #headerContent>
      <div class="header-content" v-if="isShowReSubmit">
        <a-button type="primary" @click="reSubmit">重新提交</a-button>
      </div>
    </template>
    <div class="access-request-form">
      <a-card title="大门权限申请">
        <BasicForm @register="registerForm" :showActionButtonGroup="false">
          <template #newFactoryTips>
            <div class="new-factory-tips"> 流程审批通过后将同步变更员工车辆厂区权限 </div>
          </template>
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
          <template #authorizeDateRangeSlot="{ model, field }">
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
        </BasicForm>
        <iframe v-if="isShowBpm" :src="iframeUrl" style="height: 1350px; width: 100%" id="bpmIframe" class="iframeClass"></iframe>
      </a-card>
    </div>
    <div class="bottom-view">
      <div class="action-buttons">
        <div class="action-buttons-sub" v-if="isCanApprove">
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
  import dayjs from 'dayjs';

  import { ref, onMounted, computed, watchEffect } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form';
  import { PageWrapper } from '/@/components/Page';
  import { useRouter, useRoute } from 'vue-router';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { manageFormSchemas, empFormSchemas } from './form.data';
  import { useUserStore } from '/@/store/modules/user';
  import {
    getEmployeeByWorkNo,
    checkBuildRoleType,
    getDetailById,
    saveAndCreateProcess,
    getProcessDetailById,
    approvalProcess,
    getFactoryById,
  } from './form.api';
  import { useTabs } from '/@/hooks/web/useTabs';
  import { useBpm } from './useBpm';
  import { uniqBy } from 'lodash-es';

  const { close } = useTabs();
  const userInfo = useUserStore().getUserInfo;
  const router = useRouter();
  const route = useRoute();
  const { createMessage } = useMessage();
  const currentUserRoleType = ref('');
  const detailInfo = ref<any>(null);
  const datePickerOpen = ref(false); // 控制日期选择器面板的显示状态
  const isEdit = ref(false);
  const isView = computed(() => {
    if (isEdit.value) {
      return false;
    }
    if (route.query.type && route.query.type === 'view') {
      return true;
    }
    if (route.query.formInstanceId) {
      return true;
    }
    return false;
  });

  const { isCanApprove, isShowBpm, iframeUrl, checkCanApprove, getBpmData } = useBpm(detailInfo, isEdit);

  const isUser = computed(() => {
    return currentUserRoleType.value === '1';
  });
  const isShowReSubmit = computed(() => {
    return isView.value && detailInfo.value?.createEmployeeNo === userInfo.workNo;
  });
  const [registerForm, { validate, getFieldsValue, setFieldsValue, resetSchema, clearValidate, setProps }] = useForm({
    labelWidth: 130,
    schemas: [...manageFormSchemas, ...empFormSchemas],
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    fieldMapToTime: [
      // 正确格式：[单个源字段, [目标开始字段, 目标结束字段], 日期格式]
      ['useDateRange', ['applyStartTime', 'applyEndTime'], 'YYYY-MM-DD'],
    ],
    disabled: isView.value,
  });

  onMounted(() => {
    init();
  });
  // 页面init
  async function init() {
    try {
      await clearValidate();
      currentUserRoleType.value = await checkBuildRoleType({});
      // 获取角色豫详情

      if (isView.value) {
        await getProcessDetail();
        await checkCanApprove(detailInfo.value.bpmFlowId);
      } else {
        if (!isUser.value) {
          await resetSchema(manageFormSchemas);
        } else {
          await resetSchema(empFormSchemas);
          // setFieldsValue(mockEmpData);
        }
        const userData = await getEmployeeByWorkNo({ workNo: userInfo.workNo });
        const formValues = {
          createEmployeeName: userData.empName,
          createEmployeeNo: userData.empNo,
          createDeptName: userData.orgNamePath,
        };
        // setFieldsValue()
        // if (isUser.value) {
        const currentFactorys = await getDetailById({ userCode: userInfo.workNo });
        formValues.curFactory = currentFactorys;
        // } else {
        //   await useFromManager({});
        // }
        setFieldsValue(formValues);
      }

      // console.log(userData, 'userData');
    } catch (error) {
      console.log(error, 'init');
    }
  }
  //数组根据元素去重
  function arrDedu(arr) {
    return uniqBy(arr, 'employeeCode');
  }
  // 重新提交
  async function reSubmit() {
    console.log('重新提交1');
    isEdit.value = true;
    setProps({ disabled: false });
  }
  // 提交表单
  async function submitForm() {
    try {
      console.log('*****************', [{ name: '123' }, { name: '123' }].join(','));
      const res111 = await validate();
      const res = getFieldsValue(true);
      console.log(res, '[[[[[[[[]]]]]]]]');
      const params = {} as any;
      params.chiefEmployeeLogin = res.chiefEmployee.map((item) => item.email?.split('@')[0]).join(',');
      params.chiefEmployeeName = res.chiefEmployee.map((item) => item.name).join(',');
      params.applyReason = res.applyReason;
      params.applyStartTime = res.applyStartTime;
      params.applyEndTime = res.applyEndTime;
      params.montageFactoryCode = res.montageFactoryCode.map((item) => item.value).join(',');
      params.montageFactoryName = res.montageFactoryCode.map((item) => item.label).join(',');
      if (isUser.value) {
        params.roleType = '1';
        params.powerType = '1';
      } else {
        params.roleType = currentUserRoleType.value;
        params.powerType = res.powerType;
        params.userList = res.powerType == '1' ? arrDedu(res.addUserList) : arrDedu(res.closeUserList);
        const hasOut = params.userList.some((ele) => {
          return !ele.employeeCode.startsWith('9');
        });
        if (hasOut) {
          createMessage.error('请删除外协外包人员，调整外协外包人员厂区大门权限请联系内部对接人提交外协外包进出权限调整流程');
          return;
        }
      }
      console.log('提交的参数', params);
      const result = await saveAndCreateProcess(params);
      createMessage.success('提交成功');
      goBack();
      console.log(result, 'result');
    } catch (error) {
      console.log(error, 'validate');
    }
  }
  //审批
  async function auditProcessAction(isPass) {
    let fdParameterJson = '';

    try {
      // 审批操作
      if (iframeUrl.value) {
        fdParameterJson = await getBpmData();
        console.log(fdParameterJson, 'fdParameterJson');
      }
    } catch (error) {
    } finally {
      // 可选的finally逻辑
      try {
        const res = await approvalProcess({
          id: (detailInfo.value as any)?.id as string,
          bpmFlowId: (detailInfo.value as any)?.bpmFlowId as string,
          auditNote: isPass ? '通过' : '不通过',
          fdParameterJson: JSON.stringify({ 'sysWfBusinessForm.fdParameterJson': fdParameterJson }) || '',
        });
        createMessage.success('操作成功');
        close();
      } catch (error) {
        // debugger;
        console.log(error, 'approvalProcess');
      }
    }
  }
  // 返回
  function goBack() {
    router.back();
  }

  async function getProcessDetail() {
    const res = await getProcessDetailById({ id: route.query.formInstanceId });
    await resetSchema(res.roleType == '1' ? empFormSchemas : manageFormSchemas);

    detailInfo.value = res;
    console.log('流程详情', res);
    const filedsValue = {
      createEmployeeName: res.createEmployeeName,
      createEmployeeNo: res.createEmployeeNo,
      createDeptName: res.createEmployeeDept,
      powerType: res.powerType,
      applyReason: res.applyReason,
      useDateRange: [res.applyStartTime, res.applyEndTime],
      montageFactoryCode: getMontageFactoryCode(res),
      chiefEmployee: [{ id: res.chiefEmployeeLogin, name: res.chiefEmployeeName, email: `${res.chiefEmployeeLogin}@yutong.com` }],
    } as any;
    if (res.roleType == '1') {
      const factorys = await getFactoryById({ userCode: res.createEmployeeNo });
      console.log('当前授权厂区', factorys);
      filedsValue.curFactory = factorys;
    }
    if (res.powerType == '1') {
      filedsValue.addUserList = res.userList;
    } else {
      filedsValue.closeUserList = res.userList;
    }

    setFieldsValue(filedsValue);
  }
  function getMontageFactoryCode(detail) {
    // montageFactoryCode: "147,148,149"
    // montageFactoryName: "一工厂（十八里河）,二工厂（新能源）,研发中心"
    const montageFactoryCode = detail.montageFactoryCode.split(',');
    const montageFactoryName = detail.montageFactoryName.split(',');
    return montageFactoryCode.map((item, index) => ({
      value: item,
      label: montageFactoryName[index],
    }));
  }
  // 处理点击长期按钮
  function handleLongTermClick(model, field) {
    model[field] = [dayjs(), dayjs('2099-12-31')];

    // 直接关闭日期选择器面板
    datePickerOpen.value = false;
  }
  // 禁用日期
  const disabledDate = (current) => {
    return (current && current < dayjs().startOf('day')) || current > dayjs('2099-12-31');
  };
</script>

<style lang="less" scoped>
  .header-content {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
  .access-request-form {
    padding: 16px 16px 64px 16px;

    background-color: #fff;
    ::v-deep .ant-form-item-control {
      max-width: 100%;
    }
    .form-content {
      padding: 16px;
    }
    .action-buttons {
      display: flex;
      width: 100%;
      justify-content: center;
      gap: 16px;
      margin-top: 32px;
    }

    .new-factory-tips {
      font-size: 14px;
      line-height: 1.6;
      border: 1px solid #ff4d4f;
      border-radius: 4px;
      padding: 10px;
      background-color: #fff1f0;
      color: #1890ff;

      p {
        margin-bottom: 4px;
      }
    }

    .date-range-wrapper {
      display: flex;
      align-items: center;

      .long-term-btn {
        margin-left: 8px;
        white-space: nowrap;
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
