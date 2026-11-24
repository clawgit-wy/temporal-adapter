<template>
  <div>
    <a-card title="审批信息">
      <BasicForm class="top-form" @register="registerApplicantForm">
        <template #examineEmpName="{ model, field, schema }">
          <a-select
            v-model:value="model[field]"
            show-search
            :disabled="schema.componentProps.disabled"
            style="width: 100%"
            placeholder="请输入员工姓名查询选择"
            :default-active-first-option="false"
            :show-arrow="false"
            :filter-option="false"
            :not-found-content="null"
            :options="examineEmpList"
            @change="examineEmpChange"
            @search="examineEmpSearch"
          >
            <template #option="{ label, empNo }"> &nbsp;&nbsp;{{ label }} -工号:({{ empNo }}) </template>
          </a-select>
        </template>
        <template #gooutEmpNo="{ model, field, schema }">
          <a-input v-model:value="model[field]" placeholder="请输入员工号" required @blur="onBlur" :disabled="schema.componentProps.disabled" />
        </template>
        <template #superiorEmpNo="{ model, field }">
          <div>
            <SearchPerson :disabled="props.info.curNodeId != 'N26'" v-model:value="model[field]" @change="microChange" />
            <!-- <span style="color: red;">申请人未上传出场原因证明材料，流程需升级至审核人上级!</span> -->
          </div>
        </template>
        <template #goodsList="{ model, field, schema }">
          <FormTable v-model:value="model[field]" :columns="goodsListColumns" :disabled="schema.componentProps.disabled"></FormTable>
        </template>
        <template #vehicleList="{ model, field, schema }">
          <FormTable v-model:value="model[field]" :columns="outerColumns" :disabled="schema.componentProps.disabled"></FormTable>
        </template>
        <template #receiveList="{ model, field, schema }">
          <FormArray
            v-model:value="model[field]"
            :disabled="schema.componentProps.disabled"
            :flowId="$props.info.flowId"
            :processId="$props.info.id"
            :subject="$props.info.subject"
            :curNodeId="$props.info.curNodeId"
          ></FormArray>
        </template>
        <template #qaExamineEmp="{ model, field }">
          <a-select
            v-model:value="model[field]"
            show-search
            style="width: 100%"
            placeholder="请输入员工姓名查询选择"
            :default-active-first-option="false"
            :show-arrow="false"
            :filter-option="false"
            :not-found-content="null"
            :options="personQaList"
            @change="personQaChange"
            @search="(val) => personQaSearch({ empName: val })"
          >
            <template #option="{ label, empNo }"> &nbsp;&nbsp;{{ label }} -工号:({{ empNo }}) </template>
          </a-select>
        </template>
        <template #superiorEmpName="{ model, field }">
          <a-select
            v-model:value="model[field]"
            show-search
            style="width: 100%"
            placeholder="请输入员工姓名查询选择"
            :default-active-first-option="false"
            :show-arrow="false"
            :filter-option="false"
            :not-found-content="null"
            :options="personList"
            @change="personChange"
            @search="(val) => personSearch({ empName: val })"
          >
            <template #option="{ label, empNo }"> &nbsp;&nbsp;{{ label }} -工号:({{ empNo }}) </template>
          </a-select>
        </template>
      </BasicForm>
    </a-card>
    <br />
    <a-card title="放行信息" v-if="showReleaseForm">
      <BasicForm class="top-form" @register="registerReleaseForm">
        <template #releaseStatus="{ model, field }">
          <a-select
            v-model:value="model[field]"
            :disabled="props.info?.curNodeId != 'N7'"
            :options="releaseStatusOptions"
            @change="selaseStatusChange"
          ></a-select>
        </template>
      </BasicForm>
    </a-card>
    <br />
    <a-card
      title="抽查信息"
      v-if="
        (props.info?.curNodeId === 'N7' ||
          props.info?.curNodeId === '' ||
          props.info?.curNodeId === 'N66' ||
          props.info?.curNodeId === 'N75' ||
          props.info?.curNodeId === 'N74' ||
          props.info?.curNodeId === 'N3') &&
        (selaseStatus == '4' || selaseStatus == '2' || props.info?.releaseStatus == '4' || props.info?.releaseStatus == '2')
      "
    >
      <BasicForm class="top-form" @register="registerCheckForm"></BasicForm>
    </a-card>
    <a-card title="接收信息" v-if="props.info?.curNodeId === 'N74' || props.info?.curNodeId === 'N3' || props.info?.status === '30'">
      <BasicTable @register="registerTable" :columns="receiveResultColumn" :dataSource="props.info.receiveList || []">
        <template #sceneImg="{ text }">
          <TableImg v-if="text" :size="60" :simpleShow="true" :imgList="text.split(/[,;|]+/g)" />
        </template>
      </BasicTable>
    </a-card>
    <br />
    <a-card title="审核人确认物资接收情况" v-if="props.info?.curNodeId === 'N74' || props.info?.curNodeId == ''">
      <BasicForm class="top-form" @register="registerValidateForm"></BasicForm>
    </a-card>
    <div class="change-box">
      <a-button class="btn" type="primary" @click="circularizeVisible = true" v-if="isShowCircularize">转办</a-button>
      <a-button class="btn" type="primary" @click="submitApprove" v-if="showN75Change && props.info.curNodeId === 'N75'">提交</a-button>
      <div v-else>
        <div v-if="showChange && props.info.status != '30'" style="text-align: center" :loading="loading">
          <a-button class="btn" type="primary" @click="submitApprove" v-if="props.info.curNodeId !== 'N2' && props.info.curNodeId !== 'N3'"
            >提交</a-button
          >
        </div>
      </div>
    </div>
    <a-modal v-model:visible="openVehicleUpload" title="提示" @ok="handleVehicleOk">
      <div class="blacklistUpload">
        <p>请按照模板规范填写，否则无法正确导入<a-button @click="onVehicledownLoadXls">下载模板</a-button></p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onVehicleImportXls">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
    <a-modal
      destroyOnClose
      v-model:visible="circularizeVisible"
      title="转办"
      :confirmLoading="circularizeLoading"
      @ok="submitCircularize"
      width="600px"
    >
      <ul class="temp-box">
        <li>
          <p>转办给：</p>
          <SearchPerson style="width: 500px" @change="receivePersonChange"></SearchPerson>
        </li>
      </ul>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
  import { useForm, BasicForm, FormSchema, ApiSelect } from '/@/components/Form';
  import { computed, onMounted, createVNode, ref, watchEffect } from 'vue';
  import { cloneDeep, omit } from 'lodash-es';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useGlobSetting } from '/@/hooks/setting';
  import { BasicTable, useTable, TableImg } from '/@/components/Table';
  import { Modal, message } from 'ant-design-vue';
  import { ExclamationCircleOutlined } from '@ant-design/icons-vue';
  import { useUserStore } from '/@/store/modules/user';
  import { useTabs } from '/@/hooks/web/useTabs';
  import { useRouter, useRoute } from 'vue-router';
  import dayjs from 'dayjs';
  import { ApplicantSchemas, ReleaseSchemas, goodsListColumns, outerColumns, receiveResultColumn, CheckSchemas, validateSchemas } from '../config';
  import FormTable from './formTable.vue';
  import FormArray from './formArray.vue';
  import {
    SystemFlagEnum,
    productionMaterialReasonTypeFlag,
    GoodsTypeFlag,
    leaveDataFlagOptions,
    leaveDataFlagEnum,
    releaseStatusOptions,
  } from '../const';
  import {
    getEmployeeByWorkNo,
    getPersonByCondition,
    fieldTree,
    employeeList,
    updateAndAudit,
    updateTransfer,
    modifyCurDealHandler,
    Api,
    saveAndCreateProcess,
    auditForProcess,
    saveReceiveDetail,
    changeEIPTodoWithList,
  } from '../field.api';
  import { usePermission } from '/@/hooks/web/usePermission';
  import SearchPerson from '/@/components/Form/src/components/SearchPerson.vue';
  import { debounce } from 'lodash-es';

  const { hasPermission } = usePermission();

  const router = useRouter();
  const route = useRoute();
  const globSetting = useGlobSetting();
  const { close } = useTabs();

  const userStore: any = useUserStore().getUserInfo;

  console.log(userStore, 'userstore');
  const props = defineProps({
    info: {
      type: Object,
      default: () => {},
    },
    outTypeList: {
      type: Array,
      default: () => [],
    },
  });
  const discretionFlag = ref(false);
  const showReleaseFormNodes = ['N7', 'N66', 'N74', 'N75', 'N3'];
  const showReleaseForm = computed(() => {
    if (!props.info.curNodeId || showReleaseFormNodes.includes(props.info.curNodeId)) {
      return true;
    }
    return false;
  });
  const showN75Change = computed(() => {
    if (userStore && userStore.workNo && props.info.receiveList && props.info.receiveList.length > 0) {
      return props.info.receiveList.some((item) => item.receiveEmpNo == userStore.workNo);
    }
  });
  const receiveNotDone = computed(() => {
    const selectPerson = props.info.receiveList.find(
      (item) => item.receiveEmpNo == userStore.workNo && (route.query.receiveId ? item.id == route.query.receiveId : true)
    );
    if (selectPerson) {
      return !selectPerson.receiveStatus;
    }
    return false;
  });
  // 判断是否是当前节点处理人
  const showChange = computed(() => {
    console.log('xinxi===', props.info, userStore);
    if (userStore && userStore.email && props.info.curNodeHandler) {
      const eip = userStore.email.split('@')[0];
      console.log(eip, props.info.curNodeHandler.includes(eip), '[[[[[[[[[[[]]]]]]]]]]]');
      return props.info.curNodeHandler.includes(eip);
    } else {
      return false;
    }
  });
  //是否显示转办
  const isShowCircularize = computed(() => {
    if (showN75Change.value && props.info.curNodeId === 'N75' && receiveNotDone.value) {
      return true;
    }
    if (showChange.value && props.info.value?.status != '00' && props.info.value?.status != '30') {
      return true;
    }
    return false;
  });
  //n75转办
  const circularizeVisible = ref(false);
  const circularizeLoading = ref(false);
  const changeReceiveSelectPerson = ref({});
  const receivePersonChange = (val, option) => {
    if (props.info.curNodeId == 'N74') {
      const { empNo = '', empName = '', email = '', orgName = '', label = '', phoneNo = '' } = option;
      let selectPerson = {} as any;
      selectPerson.receiveEmpNo = empNo;
      selectPerson.receiveEmpEip = email.split('@')[0];
      selectPerson.receiveEmpDept = orgName;
      selectPerson.receiveEmp = label;
      selectPerson.receivePhone = phoneNo;
      changeReceiveSelectPerson.value = selectPerson;
      return;
    }
    console.log(val, option);
    console.log('转变前的数据==', props.info.receiveList);
    const selectPerson = props.info.receiveList.find((item) => item.receiveEmpNo == userStore.workNo && item.id == route.query.receiveId);
    if (selectPerson) {
      const { empNo = '', empName = '', email = '', orgName = '', label = '', phoneNo = '' } = option;
      selectPerson.receiveEmpNo = empNo;
      selectPerson.receiveEmpEip = email.split('@')[0];
      selectPerson.receiveEmpDept = orgName;
      selectPerson.receiveEmp = label;
      selectPerson.receivePhone = phoneNo;
    }
    changeReceiveSelectPerson.value = selectPerson;
    console.log('转变后的数据==', props.info.receiveList, changeReceiveSelectPerson.value);
  };
  const submitCircularize = async () => {
    const { curNodeId, subject, id, flowId, curNodeName, curNodeHandler, curHandlerName } = props.info || {};
    circularizeLoading.value = true;
    if (curNodeId == 'N74') {
      const eip = userStore.email?.split('@')[0] || '';
      try {
        await updateTransfer({
          id: id,
          transferTo: changeReceiveSelectPerson.value.receiveEmpEip,
          curPersonLogin: eip,
          flowId,
        });
        circularizeVisible.value = false;
        circularizeLoading.value = false;
        message.success('操作成功');
        location.reload();
      } catch (error) {
        circularizeLoading.value = false;
      }
      return;
    }
    try {
      let params = {
        processId: id,
        toEmpNo: changeReceiveSelectPerson.value.receiveEmpNo,
        toEmpEip: changeReceiveSelectPerson.value.receiveEmpEip,
        toEmpName: changeReceiveSelectPerson.value.receiveEmp,
        toEmpDept: changeReceiveSelectPerson.value.receiveEmpDept,
        toEmpPhone: changeReceiveSelectPerson.value.receivePhone,
        submitNo: userStore.workNo,
        subject: subject,
        receiveId: changeReceiveSelectPerson.value.id,
        flowId: flowId,
        fromEIPEmpNo: userStore.workNo,
      };
      await changeEIPTodoWithList({ enList: [params] });
      circularizeVisible.value = false;
      circularizeLoading.value = false;
      message.success('操作成功');
      location.reload();
    } catch (error) {
      console.log('错误', error);
      circularizeLoading.value = false;
    }
  };

  const colProps = { span: 12 };
  //点检人表单字段
  const disabled = ref(true);
  console.log(userStore, 'userStore');
  // 申请人信息
  const [
    registerApplicantForm,
    { setFieldsValue, validate: validateApplicant, setProps: setEventProps, updateSchema, getFieldsValue: getEventFieldValue },
  ] = useForm({
    layout: 'vertical',
    //注册表单列
    schemas: ApplicantSchemas,
    model: {
      systemFlag: SystemFlagEnum.EIP,
      createTime: dayjs(new Date()).format('YYYY-MM-DD HH:mm:ss'),
      submitName: userStore.realName,
      submitNo: userStore.workNo,
      creator: userStore.userName,
      subject: `应用系统开发模块-${userStore.realName}-出门申请`,
    },
    disabled: false,
    //回车提交
    autoSubmitOnEnter: true,
    //不显示重置按钮
    showResetButton: false,
    // 不显示提交按钮
    showSubmitButton: false,
    //查询列占比 24代表一行 取值范围 0-24
    actionColOptions: { span: 24 },
  });
  const microChange = (val, e) => {
    setFieldsValue({
      superiorEmpNo: val,
      superiorEmpEip: e.email ? e.email.split('@')[0] : '',
      superiorEmpName: e.empName,
      superiorDept: e.orgName,
    });
  };
  // 放行信息
  const [
    registerReleaseForm,
    { setFieldsValue: setReleaseField, validate: validateRelease, updateSchema: updateReleaseSchema, setProps: setReleaseProps },
  ] = useForm({
    layout: 'vertical',

    //注册表单列
    schemas: ReleaseSchemas,
    //回车提交
    autoSubmitOnEnter: true,
    //不显示重置按钮
    showResetButton: false,
    // 不显示提交按钮
    showSubmitButton: false,
    //查询列占比 24代表一行 取值范围 0-24
    actionColOptions: { span: 17 },
    fieldMapToTime: [['date', ['checkTimeStart', 'checkTimeEnd'], 'YYYY-MM-DD']],
  });
  //  { setFieldsValue: setCheckField, validate: validateCheck, updateSchema: updateCheckSchema }
  // 抽查信息
  const [registerCheckForm, { validate: checkValiDate, setFieldsValue: setCheckField, setProps: setCheckProps }] = useForm({
    layout: 'vertical',

    //注册表单列
    schemas: CheckSchemas,
    //回车提交
    autoSubmitOnEnter: true,
    //不显示重置按钮
    showResetButton: false,
    // 不显示提交按钮
    showSubmitButton: false,
    //查询列占比 24代表一行 取值范围 0-24
    actionColOptions: { span: 17 },
    fieldMapToTime: [['date', ['checkTimeStart', 'checkTimeEnd'], 'YYYY-MM-DD']],
  });
  const [registerValidateForm, { setFieldsValue: setValidateField, validate: validateValidate, setProps, updateSchema: updateValidateSchema }] =
    useForm({
      layout: 'vertical',

      //注册表单列
      schemas: validateSchemas,
      //回车提交
      autoSubmitOnEnter: true,
      //不显示重置按钮
      showResetButton: false,
      // 不显示提交按钮
      showSubmitButton: false,
      //查询列占比 24代表一行 取值范围 0-24
      actionColOptions: { span: 17 },
    });
  const getTeamOptions = (parentId) => {
    fieldTree({ parentId, topFlag: false }).then((res) => {
      updateSchema([
        {
          field: 'teamId',
          componentProps: {
            options: res.map((item) => ({ label: item.fieldName, value: item.id })),
          },
        },
      ]);
    });
  };
  const personQaChange = (data) => {
    console.log(data);
    const selectPerson = personQaList.value.find((item) => item.value == data);
    if (selectPerson) {
      const { empNo = '', email = '' } = selectPerson;
      setFieldsValue({
        qaExamineEmpNo: empNo,
        qaExamineEmpEip: email.split('@')[0],
      });
    }
  };
  const personQaList = ref<any[]>([]);
  let timerQa: any = null;
  const personQaSearch = (params) => {
    if (timerQa) clearTimeout(timerQa);
    timerQa = setTimeout(() => {
      getPersonQaList(params);
    }, 500);
  };
  const getPersonQaList = async (params) => {
    const { dataList } = await employeeList(params);
    personQaList.value = dataList.map((item) => {
      let value = null;
      if (item.email) {
        value = item.email.split('@')[0];
      }
      return { ...item, label: item.empName, value };
    });
  };

  // 获取流程
  const getBpmData = () => {
    return new Promise((resolve, reject) => {
      const flowIFrame = document.getElementById('bpmIframe');
      const domain = window?.domain || '';
      if (domain) {
        domain.call(flowIFrame.contentWindow, 'lbpm_getApprovalData', [], function (data) {
          const res = JSON.parse(data);
          const result = res['sysWfBusinessForm.fdParameterJson'];
          if (result) {
            resolve(JSON.parse(result));
          }
          reject('获取iframe数据失败');
        });
      } else {
        reject();
      }
    });
  };

  //n75节点转办
  const changeReceiver = async () => {};
  // 审核流程表单填写
  const submitApprove = debounce(submitApproveSin, 300);
  async function submitApproveSin() {
    // N2是起草，N3是驳回
    const { curNodeId, subject, id, flowId, curNodeName, curNodeHandler, curHandlerName } = props.info || {};
    const formDataApplicant = await validateApplicant();
    if (curNodeId === 'N75') {
      console.log(userStore.workNo);
      const { receiveStatus, receiveConfirmImg, unReceiveReason } = formDataApplicant;
      await saveReceiveDetail({
        id: route.query.receiveId,
        processId: id,
        receiveEmpNo: userStore.workNo,
        receiveStatus,
        nodeFlag: 7,
        receiveImg: receiveConfirmImg,
        remark: unReceiveReason,
      });
      message.success('操作成功');
      close();
      return;
    }
    const bpmData = await getBpmData();
    const auditNote = bpmData.param.auditNote || '';
    const processParam = {
      'processParam.subject': subject,
      'processParam.flowId': route.query.processId,
      'processParam.modelId': 'goods_outsider',
      'processParam.formId': 'safety_goods_out_process',
      'processParam.type': 'audit',
      'processParam.fdParameterJson': JSON.stringify({ 'sysWfBusinessForm.fdParameterJson': bpmData }),
    };

    const eip = userStore.email.split('@')[0];
    if (curNodeId === 'N26') {
      const { superiorEmpName, superiorEmpNo, superiorEmpEip, superiorDept } = formDataApplicant;
      const params = {
        // ...others,
        ...processParam,
        superiorEmpName,
        superiorEmpNo,
        superiorEmpEip,
        superiorDept,
        nodeFlag: 4,
        curPersonLogin: eip,
        auditNote,
        isUpdateDetail: false,
        id,
        flowId,
        curNodeId,
        curNodeName,
        curNodeHandler,
        curHandlerName,
      };
      await auditForProcess(params);
    } else if (curNodeId === 'N7') {
      // 门卫人员仅可编辑放行节点
      const formDataRelease = await validateRelease();
      let checkData = {};
      if (formDataRelease.releaseStatus == '4') {
        checkData = await checkValiDate();
      }
      const params = {
        // ...others,
        ...formDataRelease,
        ...processParam,
        ...checkData,
        id,
        nodeFlag: 6,
        curPersonLogin: eip,
        auditNote,
        isUpdateDetail: false,
        flowId,
        curNodeId,
        curNodeName,
        curNodeHandler,
        curHandlerName,
        releaseEmp: userStore.realName, // 放行人姓名
        releaseEmpNo: userStore.workNo, // 放行人员工号
        releaseEmpEip: eip, // 放行人EIP登录名
      };
      await auditForProcess(params);
    } else if (curNodeId === 'N74') {
      const validateData = await validateValidate();
      const params = {
        ...processParam,
        ...validateData,
        isUpdateDetail: false,
        auditNote,
        id,
        flowId,
        curNodeId,
        curNodeName,
        curNodeHandler,
        curHandlerName,
        curPersonLogin: eip,
        nodeFlag: -1,
      };
      await auditForProcess(params);
    } else if (curNodeId === 'N75') {
      // 接收人仅可编辑接收人信息
      const { receiveStatus, receiveConfirmImg, unReceiveReason } = formDataApplicant;
      const params = {
        // ...others,
        // ...formDataApplicant,
        ...processParam,
        receiveStatus,
        receiveConfirmImg,
        // checkReceiveStatus,
        unReceiveReason,
        nodeFlag: 7,
        curPersonLogin: eip,
        auditNote,
        isUpdateDetail: false,
        id,
        flowId,
        curNodeId,
        curNodeName,
        curNodeHandler,
        curHandlerName,
      };
      // await auditForProcess(params);
      console.log(userStore);
      return;
    } else {
      // nodeFlag 取值：0-起草， 1-废料销售价格确认 2-原因类别 3- 质保部审批 4- 审核人审核 5-审核人上级 6-门卫人员 7-接收人接收确认 8-审核人确认物资接收信息 结束 -1

      const nodeFlagMap = {
        N71: 1,
        N60: 3,
        N26: 4,
        N64: 5,
        N7: 6,
        N75: 7,
        N82: 0,
      };
      const { superiorEmpName, superiorEmpNo, superiorEmpEip, superiorDept } = formDataApplicant;
      // 其他节点信息均不可编辑
      const params = {
        // ...others,
        ...processParam,
        superiorEmpName,
        superiorEmpNo,
        superiorEmpEip,
        superiorDept,
        nodeFlag: nodeFlagMap[curNodeId],
        curPersonLogin: eip,
        auditNote,
        isUpdateDetail: false,
        id,
        flowId,
        curNodeId,
        curNodeName,
        curNodeHandler,
        curHandlerName,
      };
      await auditForProcess(params);
    }
    message.success('操作成功');
    // 创建流程之后关闭当前页面。
    close();
    // router.push({
    //   name: 'processdetail',
    // });
  }
  const selaseStatus = ref('');
  const selaseStatusChange = (e) => {
    console.log(e, 'eeeeee');
    selaseStatus.value = e;
  };
  const approveBaseParams = () => {
    const { id, flowId, eventSource } = props.info;
    const { email } = userStore;
    const curPersonNo = email ? email.split('@')[0] : '';
    return {
      id,
      flowId,
      eventSource,
      curPersonNo,
    };
  };

  const transFerVisible = ref(false);
  const transferData = ref({
    dutyPerson: '',
    dutyPersonNo: '',
    nodeFlag: 1,
    isScene: 0,
    isTransfer: 1,
    // 保卫联系人
    guardPhone: userStore.phone,
    guardPerson: userStore.realName,
    guardPersonNo: userStore.email.split('@')[0],
  });
  // 转派相关
  const personChange = (data) => {
    const selectPerson = personList.value.find((item) => item.value == data);
    if (selectPerson) {
      const { empNo = '', orgName = '', email = '' } = selectPerson;
      setFieldsValue({
        superiorEmpNo: empNo,
        superiorEmpEip: email.split('@')[0],
        superiorDept: orgName,
      });
    }
  };
  const personList = ref<any[]>([]);
  let timer: any = null;
  const personSearch = (params) => {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      getPersonList(params);
    }, 500);
  };
  const getPersonList = async (params) => {
    const { dataList } = await employeeList(params);
    personList.value = dataList.map((item) => {
      return { ...item, label: item.empName, value: item.empName };
    });
  };
  const transfer = () => {
    transFerVisible.value = true;
  };
  const transferOk = () => {
    Modal.confirm({
      title: '确定转派?',
      icon: createVNode(ExclamationCircleOutlined),
      content: createVNode('div', { style: 'color:red;' }, `转派至：${transferData.value.dutyPerson}`),
      onOk() {
        console.log(23333);

        updateTransfer({
          ...approveBaseParams(),
          dutyPerson: transferData.value.dutyPerson,
          dutyPersonNo: transferData.value.dutyPersonNo,
          isDealTransfer: props.info.nodeFlag == 2 ? 1 : 2,
        }).then((res) => {
          console.log(res);
          if (res == 'T') {
            transFerVisible.value = false;
            router.push({
              name: 'serviceData',
            });
          } else {
            message.error('服务端错误');
          }
        });
      },
    });
    // transFerVisible.value = false
  };
  // 监听数据进行数据回填
  // nodeFlag 取值：0-起草， 1-废料销售价格确认 2-原因类别 3- 质保部审批 4- 审核人审核 5-审核人上级 6-门卫人员 7-接收人接收确认 8-审核人确认物资接收信息 结束 -1
  watchEffect(() => {
    const { formInstanceId: formId } = route.query;

    if (Object.keys(props.info).length > 0 && formId) {
      const {
        createTime,
        releaseStatus,
        exceptionDesc,
        curNodeId,
        leaveData,
        leaveDataFlag,
        goodsType,
        reasonType,
        outTypeName,
        releaseEmp,
        releaseTime,
        exceptionRemark,
        isReceiveShr,
        reasonShr,
        photoShr,
        businessType,
        exceptionType,
      } = props.info;
      console.log(curNodeId, 'nodeFlag');
      // N7节点为门卫放行
      if (curNodeId == 'N7') {
        updateSchema([
          { field: 'creator', componentProps: { disabled: true } },
          { field: 'subject', componentProps: { disabled: true } },
          { field: 'systemFlag', componentProps: { disabled: true } },
          { field: 'createTime', componentProps: { disabled: true } },
          { field: 'submitDept', componentProps: { disabled: true } },
          { field: 'submitName', componentProps: { disabled: true } },
          { field: 'submitNo', componentProps: { disabled: true } },
          { field: 'outFactoryId', componentProps: { disabled: true } },
          { field: 'outTypeId', componentProps: { disabled: true } },
          { field: 'outPersonType', componentProps: { disabled: true } },
          { field: 'examineEmpName', componentProps: { disabled: true } },
          { field: 'examineDept', componentProps: { disabled: true } },
          { field: 'examinePhone', componentProps: { disabled: true } },
          { field: 'outPowerDesc', componentProps: { disabled: true } },
          { field: 'gooutEmpNo', componentProps: { disabled: true } },
          { field: 'gooutEmpName', componentProps: { disabled: true } },
          { field: 'gooutPhone', componentProps: { disabled: true } },
          { field: 'gooutEmpDept', componentProps: { disabled: true } },
          { field: 'outsideName', componentProps: { disabled: true } },
          { field: 'outsidePhone', componentProps: { disabled: true } },
          { field: 'deliveryUnitName', componentProps: { disabled: true } },
          { field: 'isVehicle', componentProps: { disabled: true } },
          { field: 'vehicleNo', componentProps: { disabled: true } },
          { field: 'goodsType', componentProps: { disabled: true } },
          { field: 'reasonType', componentProps: { disabled: true } },
          { field: 'isSetAccept', componentProps: { disabled: true } },
          { field: 'qaExamineEmp', componentProps: { disabled: true } },
          { field: 'qaExamineEmpNo', componentProps: { disabled: true } },
          { field: 'qaExamineEmpEip', componentProps: { disabled: true } },
          { field: 'reasonDesc', componentProps: { disabled: true } },
          { field: 'goodsList', componentProps: { disabled: true } },
          { field: 'exceptionDesc', componentProps: { disabled: true } },
          { field: 'exceptionRemark', componentProps: { disabled: true } },
          { field: 'isGoods', componentProps: { disabled: true } },
          { field: 'isSpareTire', componentProps: { disabled: true } },
          { field: 'spareTireNum', componentProps: { disabled: true } },
          { field: 'vehicleList', componentProps: { disabled: true } },
          { field: 'receiveList', componentProps: { disabled: true } },
          { field: 'receiveStatus', ifShow: false, componentProps: { disabled: true } },
          { field: 'receiveConfirmImg', ifShow: false, componentProps: { disabled: true } },
          // { field: 'checkReceiveStatus', componentProps: { disabled: true } },
          { field: 'checkReceiveReson', ifShow: false, componentProps: { disabled: true } },
          { field: 'sceneImg', componentProps: { disabled: true } },
          { field: 'leaveData', componentProps: { disabled: true } },
          { field: 'leaveDataFlag', componentProps: { disabled: true } },
          { field: 'superiorEmpNo', componentProps: { disabled: false } },
          { field: 'superiorEmpEip', componentProps: { disabled: false } },
          { field: 'superiorEmpName', componentProps: { disabled: false } },
          { field: 'superiorDept', componentProps: { disabled: false } },
        ]);

        // 只能编辑放行信息，
        updateReleaseSchema([
          { field: 'releaseStatus', componentProps: { disabled: false } },
          { field: 'exceptionDesc', componentProps: { disabled: false } },
        ]);

        setFieldsValue({
          ...props.info,
          createTime: dayjs(createTime),
          outTypeId: outTypeName,
        });
        setReleaseField({
          exceptionDesc,
          releaseEmp: userStore.realName, // 放行人姓名
          releaseTime: dayjs().format('YYYY-MM-DD HH:mm'),
        });
      } else if (curNodeId == 'N75') {
        // 只能编辑接收信息
        updateSchema([
          { field: 'creator', componentProps: { disabled: true } },
          { field: 'subject', componentProps: { disabled: true } },
          { field: 'systemFlag', componentProps: { disabled: true } },
          { field: 'createTime', componentProps: { disabled: true } },
          { field: 'submitDept', componentProps: { disabled: true } },
          { field: 'submitName', componentProps: { disabled: true } },
          { field: 'submitNo', componentProps: { disabled: true } },
          { field: 'outFactoryId', componentProps: { disabled: true } },
          { field: 'outTypeId', componentProps: { disabled: true } },
          { field: 'outPersonType', componentProps: { disabled: true } },
          { field: 'examineEmpName', componentProps: { disabled: true } },
          { field: 'examineDept', componentProps: { disabled: true } },
          { field: 'examinePhone', componentProps: { disabled: true } },
          { field: 'outPowerDesc', componentProps: { disabled: true } },
          { field: 'gooutEmpNo', componentProps: { disabled: true } },
          { field: 'gooutEmpName', componentProps: { disabled: true } },
          { field: 'gooutPhone', componentProps: { disabled: true } },
          { field: 'gooutEmpDept', componentProps: { disabled: true } },
          { field: 'outsideName', componentProps: { disabled: true } },
          { field: 'outsidePhone', componentProps: { disabled: true } },
          { field: 'deliveryUnitName', componentProps: { disabled: true } },
          { field: 'isVehicle', componentProps: { disabled: true } },
          { field: 'vehicleNo', componentProps: { disabled: true } },
          { field: 'goodsType', componentProps: { disabled: true } },
          { field: 'reasonType', componentProps: { disabled: true } },
          { field: 'isSetAccept', componentProps: { disabled: true } },
          { field: 'qaExamineEmp', componentProps: { disabled: true } },
          { field: 'qaExamineEmpNo', componentProps: { disabled: true } },
          { field: 'qaExamineEmpEip', componentProps: { disabled: true } },
          { field: 'reasonDesc', componentProps: { disabled: true } },
          { field: 'goodsList', componentProps: { disabled: true } },
          { field: 'exceptionDesc', componentProps: { disabled: true } },
          { field: 'exceptionRemark', componentProps: { disabled: true } },
          { field: 'isGoods', componentProps: { disabled: true } },
          { field: 'isSpareTire', componentProps: { disabled: true } },
          { field: 'spareTireNum', componentProps: { disabled: true } },
          { field: 'vehicleList', componentProps: { disabled: true } },
          { field: 'receiveList', componentProps: { disabled: true } },
          { field: 'sceneImg', componentProps: { disabled: true } },
          { field: 'leaveData', componentProps: { disabled: true } },
          { field: 'leaveDataFlag', componentProps: { disabled: true } },
          { field: 'superiorEmpNo', componentProps: { disabled: false } },
          { field: 'superiorEmpEip', componentProps: { disabled: false } },
          { field: 'superiorEmpName', componentProps: { disabled: false } },
          { field: 'superiorDept', componentProps: { disabled: false } },
          { field: 'receiveStatus', ifShow: true, componentProps: { disabled: false } },
          {
            field: 'receiveConfirmImg',
            ifShow: ({ values }) => {
              return values.receiveStatus != '0';
            },
            componentProps: { disabled: false },
          },
          // { field: 'checkReceiveStatus', componentProps: { disabled: false } },
          { field: 'unReceiveReason', ifShow: true, componentProps: { disabled: false } },
        ]);
        // 只能编辑放行信息，
        updateReleaseSchema([
          { field: 'releaseStatus', componentProps: { disabled: true } },
          // { field: 'exceptionDesc', componentProps: { disabled: true } },
        ]);
        const params = omit({ ...props.info }, 'receiveStatus', 'receiveConfirmImg', 'unReceiveReason');
        setFieldsValue({
          ...params,
          outTypeId: outTypeName,
          createTime: dayjs(createTime),
        });
      } else {
        updateSchema([
          { field: 'creator', componentProps: { disabled: true } },
          { field: 'subject', componentProps: { disabled: true } },
          { field: 'systemFlag', componentProps: { disabled: true } },
          { field: 'createTime', componentProps: { disabled: true } },
          { field: 'submitDept', componentProps: { disabled: true } },
          { field: 'submitName', componentProps: { disabled: true } },
          { field: 'submitNo', componentProps: { disabled: true } },
          { field: 'outFactoryId', componentProps: { disabled: true } },
          { field: 'outTypeId', componentProps: { disabled: true } },
          { field: 'outPersonType', componentProps: { disabled: true } },
          { field: 'examineEmpName', componentProps: { disabled: true } },
          { field: 'examineDept', componentProps: { disabled: true } },
          { field: 'examinePhone', componentProps: { disabled: true } },
          { field: 'outPowerDesc', componentProps: { disabled: true } },
          { field: 'gooutEmpNo', componentProps: { disabled: true } },
          { field: 'gooutEmpName', componentProps: { disabled: true } },
          { field: 'gooutPhone', componentProps: { disabled: true } },
          { field: 'gooutEmpDept', componentProps: { disabled: true } },
          { field: 'outsideName', componentProps: { disabled: true } },
          { field: 'outsidePhone', componentProps: { disabled: true } },
          { field: 'deliveryUnitName', componentProps: { disabled: true } },
          { field: 'isVehicle', componentProps: { disabled: true } },
          { field: 'vehicleNo', componentProps: { disabled: true } },
          { field: 'goodsType', componentProps: { disabled: true } },
          { field: 'reasonType', componentProps: { disabled: true } },
          { field: 'isSetAccept', componentProps: { disabled: true } },
          { field: 'qaExamineEmp', componentProps: { disabled: true } },
          { field: 'qaExamineEmpNo', componentProps: { disabled: true } },
          { field: 'qaExamineEmpEip', componentProps: { disabled: true } },
          { field: 'reasonDesc', componentProps: { disabled: true } },
          { field: 'goodsList', componentProps: { disabled: true } },
          { field: 'exceptionDesc', componentProps: { disabled: true } },
          { field: 'exceptionRemark', componentProps: { disabled: true } },
          { field: 'isGoods', componentProps: { disabled: true } },
          { field: 'isSpareTire', componentProps: { disabled: true } },
          { field: 'spareTireNum', componentProps: { disabled: true } },
          { field: 'vehicleList', componentProps: { disabled: true } },
          { field: 'receiveList', componentProps: { disabled: true } },
          { field: 'receiveStatus', componentProps: { disabled: true } },
          { field: 'receiveConfirmImg', componentProps: { disabled: true } },
          // { field: 'checkReceiveStatus', componentProps: { disabled: true } },
          { field: 'checkReceiveReson', componentProps: { disabled: true } },
          { field: 'sceneImg', componentProps: { disabled: true } },
          { field: 'leaveData', componentProps: { disabled: true } },
          { field: 'leaveDataFlag', componentProps: { disabled: true } },
        ]);
        updateReleaseSchema([
          { field: 'releaseStatus', componentProps: { disabled: true } },
          { field: 'exceptionDesc', componentProps: { disabled: true } },
          { field: 'exceptionRemark', componentProps: { disabled: true } },
          { field: 'releaseExceptionReason', componentProps: { disabled: true } },
          { field: 'uploadSitePhoto', componentProps: { disabled: true } },
          { field: 'exitReasonProof', componentProps: { disabled: true } },
          { field: 'referenceAttachment', componentProps: { disabled: true } },
        ]);
        if (curNodeId == 'N74') {
          updateValidateSchema([{ field: 'photoShr', ifShow: false }]);
        }
        setFieldsValue({
          ...props.info,
          outTypeId: outTypeName,
          createTime: dayjs(createTime),
        });
        // const isShow =
        //   goodsType === GoodsTypeFlag.productionMaterial &&
        //   (reasonType === productionMaterialReasonTypeFlag.supplierMaterialExit ||
        //     reasonType === productionMaterialReasonTypeFlag.outsideProcessing ||
        //     reasonType === productionMaterialReasonTypeFlag.outsideMaintenance ||
        //     reasonType === productionMaterialReasonTypeFlag.other);
        // if (curNodeId == 'N26' && !leaveData && leaveDataFlag === leaveDataFlagEnum.notReceived && isShow) {
        //   updateSchema([
        //     { field: 'superiorEmpNo', ifShow: true },
        //     { field: 'superiorEmpEip', ifShow: true },
        //     { field: 'superiorEmpName', ifShow: true },
        //     { field: 'superiorDept', ifShow: true },
        //   ]);
        // }
      }

      if (curNodeId == '') {
        setValidateField({
          isReceiveShr,
          reasonShr,
          photoShr,
        });
        setProps({
          disabled: true,
        });
      }
      if (curNodeId != 'N7') {
        setReleaseField({
          releaseEmp,
          releaseTime,
          releaseStatus,
          exceptionRemark,
          exceptionDesc,
        });
        setCheckField({
          businessType,
          exceptionType,
          exceptionDesc,
        });
        setCheckProps({
          disabled: true,
        });
      }
      updateReleaseSchema([{ field: 'releaseStatus', componentProps: { disabled: true } }]);
    }
  });

  // 审核人相关
  const examineEmpList = ref<any[]>([]);
  let examineEmptimer: any = null;
  const examineEmpChange = (data) => {
    console.log(data, 'data---');
    const selectExamineEmp = examineEmpList.value.find((item) => item.value == data);
    if (selectExamineEmp) {
      const { empDept, powerDesc, empNo, empEip, phoneNum } = selectExamineEmp;
      setFieldsValue({
        examineDept: empDept,
        outPowerDesc: powerDesc,
        examineEmpEip: empEip,
        examineEmpNo: empNo,
        examinePhone: phoneNum,
      });
    }
  };
  const examineEmpSearch = (val) => {
    if (!val) return;
    if (examineEmptimer) clearTimeout(examineEmptimer);
    examineEmptimer = setTimeout(() => {
      getExamineEmpList(val);
    }, 500);
  };
  const getExamineEmpList = async (val) => {
    const fields = getEventFieldValue();

    const fieldList = await fieldTree({
      fieldType: 'goodsOut_dict_0_type',
      topFlag: false,
    });
    const index = fieldList.findIndex((item) => item.id === fields.outTypeId);
    const outTypeItem = fieldList[index];
    const params = {
      searchContent: val,
      outType: outTypeItem.fieldName,
      outFactory: fields.outFactoryId,
      ownerEmpNo: userStore.workNo,
    };
    const dataList = await getPersonByCondition(params);
    examineEmpList.value = dataList.map((item) => {
      return { ...item, label: item.empName, value: item.empName };
    });
  };
  // 出门人员工号失焦查询员工信息
  async function onBlur(e) {
    getEmployeeByWorkNo({ workNo: e.target.value, phone: '' }).then((res) => {
      const { orgName, phoneNo, empName } = res || {};
      setFieldsValue({
        gooutEmpName: orgName,
        gooutPhone: phoneNo,
        gooutEmpDept: empName,
      });
    });
  }

  // 自动请求并暴露内部方法
  const initEmployee = () => {
    const { workNo, phone } = userStore;
    getEmployeeByWorkNo({ workNo, phone }).then((res) => {
      const { orgName } = res;
      setFieldsValue({
        submitDept: orgName,
        workNo,
        phone,
      });
    });
  };
  initEmployee();

  const [registerTable] = useTable({
    rowKey: 'id',
    pagination: false,
    showSizeChanger: false,
    showTableSetting: false,
    showIndexColumn: true,
    useSearchForm: false,
  });
</script>
<style scoped lang="less">
  .temp-box {
    padding: 20px;
    li {
      display: flex;
      align-items: center;
      margin-top: 20px;
      p {
        width: 80px;
        margin: 0;
      }
    }
  }
  .change-box {
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
  :deep(.ant-col-sm-4) {
    flex: 0 0 20% !important;
    max-width: 70% !important;
  }
  :deep(.text-secondary) {
    color: #508cee !important;
  }
</style>
