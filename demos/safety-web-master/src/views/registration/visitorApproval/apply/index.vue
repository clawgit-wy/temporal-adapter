<template>
  <div class="apply-container">
    <h1 style="text-align: center">
      {{ isView ? (detailData?.title || '访客审批授权流程详情') : pageTitle }}
    </h1>
    <br />

    <a-card title="提交人信息">
      <BasicForm @register="registerSubmitterForm" />
    </a-card>
    <br />

    <a-card title="授权人信息">
      <BasicForm @register="registerAuthorizerForm">
        <template #grantorNoSlot="{ model, field }">
          <PersonSelectInput v-if="!isView" v-model:personList="grantorPersonList" :multiple="false" placeholder="点击选择授权人" />
          <a-input v-else v-model:value="model[field]" disabled />
        </template>
      </BasicForm>
    </a-card>
    <br />

    <a-card title="被授权人信息">
      <AuthorizeeTable v-model:value="authorizeeList" :disabled="isView" :lastNum="authorizeeLastNum" />
    </a-card>
    <br />

    <a-card title="其他信息">
      <BasicForm @register="registerOtherForm">
        <template #sectionChiefSlot="{ model, field }">
          <PersonSelectInput v-if="!isView" v-model:personList="sectionChiefPersonList" :multiple="false" placeholder="点击选择科长" />
          <a-input v-else v-model:value="model[field]" disabled />
        </template>
        <template #authorizationReasonSlot="{ model, field }">
          <a-textarea
            v-model:value="model[field]"
            :disabled="isView"
            placeholder="请输入"
            :maxLength="500"
            :showCount="true"
            :rows="4"
          />
          <div class="reason-desc">
            <strong>流程说明：</strong><br />
            提交本流程，可将“授权人”的访客审批权限授予“被授权人”，流程通过后，被授权人可代为审批“被访人上级”节点。若“被授权人”为被访人，则无需被访人上级审批。
          </div>
        </template>
      </BasicForm>
    </a-card>
    <br />

    <div v-if="!isView" style="text-align: center">
      <a-button @click="goBack" style="margin-right: 8px">取消</a-button>
      <a-button type="primary" :loading="loading" @click="handleSubmit">提交</a-button>
    </div>
    <div v-if="showApproveBtn" class="approve-box">
      <a-button class="btn" type="primary" :loading="loading" @click="handleApprove">提交</a-button>
    </div>

    <iframe v-if="isView && bpmSrc" :src="bpmSrc" class="bpm-iframe" />
  </div>
</template>

<script lang="ts" setup name="visitorApprovalApply">
  import { ref, computed, watch, nextTick } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { useGlobSetting } from '/@/hooks/setting';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStore } from '/@/store/modules/user';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';
  import { defHttp } from '/@/utils/http/axios';
  import { submitterFormSchema, authorizerFormSchema, otherFormSchema } from './config';
  import { getEmployeeByWorkNo, saveAndUpdateProcess, checkEmpNo, checkSectionChief, getProcessCurDealHandler } from './field.api';
  import AuthorizeeTable from './childForm/authorizeeTable.vue';
  import '/@/assets/bpm/domain';
  import dayjs from 'dayjs';

  const router = useRouter();
  const route = useRoute();
  const globSetting = useGlobSetting();
  const { bpmUrl } = globSetting;
  const { createMessage } = useMessage();
  const userStore = useUserStore().getUserInfo;

  /** 是否查看模式 */
  const isView = computed(() => !!route.query.formInstanceId);
const historyId = computed(() => route.query.historyId as string);

  const loading = ref(false);
  const authorizeeList = ref<any[]>([]);

  /** 授权人选人组件绑定 */
  const grantorPersonList = ref<any[]>([]);
  const sectionChiefPersonList = ref<any[]>([]);
  const sectionChiefType = ref(0);
  const sectionChiefEip = ref('');
  const detailData = ref<any>(null);
  const submitterOrgName = ref('');
const authorizeeLastNum = ref(0);

  /** 页面标题（新建模式） */
const pageTitle = computed(() => {
  const date = dayjs().format('YYYY年MM月DD日');
  const dept = submitterOrgName.value || '';
  const workNo = userStore?.workNo || '';
  const name = userStore?.realName || '';
  return `${date}-${dept}-${workNo}-${name}的访客审批授权流程`;
});

  /** 提交人信息表单 */
  const [registerSubmitterForm, { setFieldsValue: setSubmitterFields }] = useForm({
    layout: 'vertical',
    schemas: submitterFormSchema,
    showActionButtonGroup: false,
    disabled: isView.value,
    actionColOptions: { span: 24 },
  });

  /** 授权人信息表单 */
  const [registerAuthorizerForm, { setFieldsValue: setAuthorizerFields, validate: validateAuthorizer, getFieldsValue: getAuthorizerValues }] =
    useForm({
      layout: 'vertical',
      schemas: authorizerFormSchema,
      showActionButtonGroup: false,
      disabled: isView.value,
      actionColOptions: { span: 24 },
    });

  /** 其他信息表单 */
  const [
    registerOtherForm,
    { validate: validateOther, getFieldsValue: getOtherValues, updateSchema: updateOtherSchema, setFieldsValue: setOtherFields },
  ] = useForm({
    layout: 'vertical',
    schemas: otherFormSchema,
    showActionButtonGroup: false,
    disabled: isView.value,
    actionColOptions: { span: 24 },
  });

  /** 初始化提交人信息（新建模式） */
  if (!isView.value) {
    const workNo = userStore?.workNo;
    if (workNo) {
      getEmployeeByWorkNo({ workNo, phone: '' }).then((userInfo: any) => {
        const dept = userInfo?.deptName || userInfo?.orgName || '';
        submitterOrgName.value = dept;
        setSubmitterFields({
          submitterName: userStore?.realName || '',
          submitterNo: workNo,
          submitterOrgName: dept,
        });
      });
    }
  }

  /** 加载详情（查看模式） */
  async function loadDetail(authorizationApprovalId: string) {
    try {
      const pid = route.query.processId as string;
      const detail = await defHttp.get({
        url: '/safety/visitor/authorization/getDetail',
        params: { processId: authorizationApprovalId },
      });
      // 合并审批节点信息
      let processInfo: any = {};
      if (pid) {
        try {
          processInfo = await getProcessCurDealHandler({ id: authorizationApprovalId, flowId: pid });
        } catch {
          // ignore
        }
      }
      const merged = { ...detail, ...processInfo };
      if (merged) {
        detailData.value = merged;
        setSubmitterFields({
          submitterName: merged.submitName || '',
          submitterNo: merged.submitNo || '',
          submitterOrgName: merged.submitOrgName || '',
        });
        setAuthorizerFields({
          grantorNo: merged.grantorNo || '',
          grantorName: merged.grantorName || '',
          grantorPosition: merged.grantorPosition || '',
          grantorJobName: merged.grantorJobName || '',
          grantorOrgName: merged.grantorOrgName || '',
        });
        setOtherFields({
          authorizationTime:
            merged.authorizationBeginTime && merged.authorizationEndTime ? [merged.authorizationBeginTime, merged.authorizationEndTime] : [],
          authorizationReason: merged.authorizationReason || '',
          sectionChiefName: merged.applyEmpName || '',
          sectionChiefNo: merged.applyEmpNo || '',
        });
        sectionChiefType.value = merged.sectionChiefType || 0;
        sectionChiefEip.value = merged.applyEmpLogin || '';
        updateOtherSchema([{ field: 'sectionChiefName', ifShow: sectionChiefType.value !== 1 }]);

        if (merged.detailList && Array.isArray(merged.detailList)) {
          authorizeeList.value = merged.detailList.map((item: any) => ({
            empNo: item.granteeNo || '',
            empName: item.granteeName || '',
            orgName: item.granteeOrgName || '',
            position: item.granteePosition || '',
            jobName: item.granteeJobName || '',
          }));
        }
      }
    } catch {
      // ignore
    }
  }

  if (isView.value) {
    loadDetail(route.query.formInstanceId as string);
  }

  // 快速回填模式：加载详情并添加当前登录人为被授权人
  if (historyId.value) {
    (async () => {
      await loadDetail(historyId.value);
      // 提交人改为当前登录人
      setSubmitterFields({
        submitterName: userStore?.realName || '',
        submitterNo: userStore?.workNo || '',
        submitterOrgName: submitterOrgName.value || '',
      });
      // 授权人工号回填到 PersonSelectInput
      const gNo = detailData.value?.grantorNo;
      const gName = detailData.value?.grantorName;
      if (gNo && gName) {
        grantorPersonList.value = [{ id: gNo, name: gName }];
      }
      // 被授权人改为当前登录人
      if (userStore?.workNo && userStore?.realName) {
        const userInfo = await getEmployeeByWorkNo({ workNo: userStore.workNo, phone: '' });
        const org = userInfo?.deptName || userInfo?.orgName || '';
        authorizeeList.value = [{
          empNo: userStore.workNo,
          empName: userStore.realName,
          orgName: org,
          position: userInfo?.positionName || '',
          jobName: userInfo?.jobTypeName || '',
        }];
      }
      // 科长信息回填
      const d = detailData.value;
      sectionChiefType.value = d?.sectionChiefType || 0;
      if (d?.applyEmpNo && d?.applyEmpName) {
        sectionChiefPersonList.value = [{ id: d.applyEmpNo, name: d.applyEmpName, email: d.applyEmpLogin ? `${d.applyEmpLogin}@yutong.com` : '' }];
        sectionChiefEip.value = d.applyEmpLogin || '';
      }
      updateOtherSchema([{ field: 'sectionChiefName', ifShow: sectionChiefType.value !== 1 }]);
    })();
  }

  // ============ 授权人选择 ============

  watch(grantorPersonList, async (val) => {
    if (isView.value) return;
    if (!val || val.length === 0) {
      setAuthorizerFields({
        grantorNo: '',
        grantorName: '',
        grantorPosition: '',
        grantorJobName: '',
        grantorOrgName: '',
        sectionChiefName: '',
        sectionChiefNo: '',
      });
      return;
    }

    const person = val[0];
    const workNo = person.id;
    if (!workNo) return;

    try {
      try {
        const checkRes = await checkEmpNo(workNo);
        if (!checkRes) {
          createMessage.warning('请选择主管级以上人员');
          grantorPersonList.value.splice(0);
          await nextTick();
          grantorPersonList.value = [];
          return;
        }
        const isChief = checkRes.sectionChief === true;
        sectionChiefType.value = isChief ? 1 : 0;
        authorizeeLastNum.value = checkRes.lastNum || 0;
        updateOtherSchema([{ field: 'sectionChiefName', ifShow: !isChief }]);
      } catch {
        createMessage.warning('请选择主管级以上人员');
        grantorPersonList.value.splice(0);
        await nextTick();
        grantorPersonList.value = [];
        return;
      }

      const userInfo = await getEmployeeByWorkNo({ workNo, phone: '' });
      if (!userInfo) {
        createMessage.warning('未查询到该员工信息');
        return;
      }

      setAuthorizerFields({
        grantorNo: workNo,
        grantorName: userInfo.empName || person.name || '',
        grantorPosition: userInfo.positionName || '',
        grantorJobName: userInfo.jobTypeName || '',
        grantorOrgName: userInfo.deptName || userInfo.orgName || person.namePath || '',
      });
    } catch {
      createMessage.warning('获取人员信息失败');
    }
  });

  // ============ 科长选择 ============

  watch(sectionChiefPersonList, async (val) => {
    if (isView.value) return;
    if (!val || val.length === 0) {
      setOtherFields({ sectionChiefName: '', sectionChiefNo: '' });
      return;
    }

    const person = val[0];
    const workNo = person.id;
    if (!workNo) return;

    try {
      try {
        await checkSectionChief(workNo);
      } catch {
        sectionChiefPersonList.value.splice(0);
        await nextTick();
        sectionChiefPersonList.value = [];
        return;
      }

      setOtherFields({
        sectionChiefName: person.name || '',
        sectionChiefNo: workNo,
      });
      sectionChiefEip.value = person.email ? person.email.split('@')[0] : '';
    } catch {
      createMessage.warning('获取人员信息失败');
    }
  });

  // ============ 提交 ============

  function goBack() {
    router.back();
  }

  // ============ 审批 ============

  /** 是否显示审批提交按钮：审批中 && 当前节点处理人是当前登录人 */
  const showApproveBtn = computed(() => {
    if (!isView.value || !detailData.value) return false;
    const d = detailData.value;
    if (Number(d.status) !== 0) return false;
    if (!d.curNodeHandler) return false;
    const email = (userStore as any)?.email;
    if (!email) return false;
    const eip = email.split('@')[0];
    return d.curNodeHandler.includes(eip);
  });

  /** 从 iframe 获取 BPM 审批数据 */
  const getBpmData = (): Promise<any> => {
    return new Promise((resolve, reject) => {
      const flowIFrame = document.querySelector('.bpm-iframe') as HTMLIFrameElement;
      const domain = (window as any)?.domain;
      if (domain && flowIFrame) {
        domain.call(flowIFrame.contentWindow, 'lbpm_getApprovalData', [], (data: string) => {
          const res = JSON.parse(data);
          const result = res['sysWfBusinessForm.fdParameterJson'];
          if (result) {
            resolve(JSON.parse(result));
          } else {
            reject('获取iframe数据失败');
          }
        });
      } else {
        reject();
      }
    });
  };

  /** 审批提交 */
  async function handleApprove() {
    loading.value = true;
    try {
      const d = detailData.value;
      console.log('getBpmData start...');
      const bpmData = await getBpmData();
      console.log('getBpmData result:', bpmData);
      const params: Record<string, any> = {
        id: d.id,
        submitEmpLogin: (userStore as any)?.email ? (userStore as any).email.split('@')[0] : '',
        grantorNo: d.grantorNo || '',
        authorizationBeginTime: d.authorizationBeginTime || '',
        authorizationEndTime: d.authorizationEndTime || '',
        authorizationReason: d.authorizationReason || '',
        detailList: (d.detailList || []).map((item: any) => item.granteeNo),
        sectionChiefType: d.sectionChiefType || 0,
        applyEmpNo: d.applyEmpNo || '',
        applyEmpLogin: d.applyEmpLogin || '',
        applyEmpName: d.applyEmpName || '',
        bpmFlowId: d.bpmFlowId || '',
        fdParameterJson: JSON.stringify(bpmData),
      };

      await saveAndUpdateProcess(params);
      createMessage.success('审批通过');
      router.replace('/vehicleManagement/filing/approveResult');
    } catch {
      // 错误已在请求工具中统一提示
    } finally {
      loading.value = false;
    }
  }

  async function handleSubmit() {
    loading.value = true;
    try {
      await validateAuthorizer();
      await validateOther();

      if (!authorizeeList.value || authorizeeList.value.length === 0) {
        createMessage.warning('请添加至少一条被授权人信息');
        loading.value = false;
        return;
      }

      if (sectionChiefType.value === 0 && !sectionChiefPersonList.value.length) {
        createMessage.warning('请选择授权人科长');
        loading.value = false;
        return;
      }

      const authorizerData = getAuthorizerValues();
      const otherData = getOtherValues();

      let authorizationBeginTime = '';
      let authorizationEndTime = '';
      const timeVal = otherData.authorizationTime;
      if (Array.isArray(timeVal)) {
        [authorizationBeginTime, authorizationEndTime] = timeVal;
      } else if (typeof timeVal === 'string' && timeVal) {
        [authorizationBeginTime, authorizationEndTime] = timeVal.split(',');
      }

      const submitData: Record<string, any> = {
        submitEmpLogin: (userStore as any)?.email ? (userStore as any).email.split('@')[0] : '',
        grantorNo: authorizerData.grantorNo,
        authorizationBeginTime,
        authorizationEndTime,
        authorizationReason: otherData.authorizationReason,
        detailList: authorizeeList.value.map((item: any) => item.empNo),
        sectionChiefType: sectionChiefType.value,
        applyEmpNo: otherData.sectionChiefNo || '',
        applyEmpLogin: sectionChiefEip.value || '',
        applyEmpName: otherData.sectionChiefName || '',
      };
      if (historyId.value && detailData.value?.id) {
        submitData.id = detailData.value.id;
      }

      const res = await saveAndUpdateProcess(submitData);
      createMessage.success('提交成功');
      // 接口返回新记录 ID，刷新为查看模式
      const newId = (res && res.data) || res;
      if (newId) {
        router.replace({
          name: 'VisitorApprovalApply',
          query: { formInstanceId: String(newId) },
        });
      } else {
        router.back();
      }
    } catch {
      // 错误已在请求工具中统一提示
    } finally {
      loading.value = false;
    }
  }

  /** iframe BPM URL（查看模式） */
  const bpmSrc = computed(() => {
    const fid = route.query.formInstanceId as string;
    const pid = route.query.processId as string;
    if (!fid || !pid) return '';
    return `${bpmUrl}?method=showProcess&formInstanceId=${fid}&processId=${pid}`;
  });
</script>

<style scoped lang="less">
  .apply-container {
    background-color: #fff;
    width: 100%;
    max-width: 100%;
    padding: 30px 50px;
  }
  h1 {
    font-size: 18px;
    font-weight: 600;
    padding-top: 10px;
  }
  .bpm-iframe {
    width: 100%;
    height: 800px;
  }
  .approve-box {
    position: fixed;
    left: 0;
    bottom: 0;
    width: 100%;
    display: flex;
    justify-content: center;
    padding: 10px;
    background-color: #fff;
    box-shadow: 0 -1px 1px rgba(0, 0, 0, 0.1);
  }
  .btn {
    margin: 0 10px;
  }
  .reason-desc {
    color: #1890ff;
    margin-top: 8px;
    line-height: 1.6;
  }
</style>
