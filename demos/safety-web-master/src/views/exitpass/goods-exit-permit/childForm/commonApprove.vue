<template>
  <div>
    <a-card title="审批信息">
      <BasicForm class="top-form" @register="registerApplicantForm">
        <template #examineEmpName="{ model, field, schema }">
          <div style="display: flex">
            <a-input disabled v-model:value="model[field]" placeholder="请选择人员" />
            <a-button @click="selectChiefNo(model)">选择人员</a-button>
          </div>
          <!-- <a-select
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
          </a-select> -->
        </template>
        <template #microManagerNo="{ model, field }">
          <SearchPerson v-model:value="model[field]" @change="microChange" />
        </template>
        <template #gooutEmpNo="{ model, field, schema }">
          <a-input v-model:value="model[field]" placeholder="请输入员工号" required @blur="onBlur" :disabled="schema.componentProps.disabled" />
        </template>
        <template #goodsList="{ model, field, schema }">
          <!-- <FormTable v-model:value="model[field]" :columns="goodsListColumns" :disabled="schema.componentProps.disabled"></FormTable> -->
          <FormArrayGoogsList v-model:value="model[field]" :disabled="schema.componentProps.disabled"></FormArrayGoogsList>
          <br />
        </template>
        <template #shortBargeCar="{ model, field }">
          <a-radio-group v-model:value="model[field]" @change="shortBargeCarChange" name="radioGroup" :options="shortBargeCarOpt"></a-radio-group>
        </template>
        <template #vehicleList="{ model, field, schema }">
          <FormTable v-model:value="model[field]" :columns="outerColumns" :disabled="schema.componentProps.disabled"></FormTable>
        </template>
        <template #receiveList="{ model, field, schema }">
          <FormArray v-model:value="model[field]" :disabled="schema.componentProps.disabled"></FormArray>
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
        <template #referAttachment="{ model, field }">
          <!-- 通过链接直接打开 -->
          <div class="attachen">
            <a :href="item.url" @click.stop="downloadFile(item)" v-for="item in fileList">
              {{ item.fileName }}
            </a>
          </div>
        </template>
      </BasicForm>
    </a-card>
    <br />
    <!-- <a-card title="放行信息">
      <BasicForm class="top-form" @register="registerReleaseForm"></BasicForm>
    </a-card>
    <br />
    <a-card title="抽查信息">
      <BasicForm class="top-form" @register="registerCheckForm"></BasicForm>
    </a-card>
    <a-card title="接收信息">
      <BasicTable @register="registerTable" :columns="receiveResultColumn" :dataSource="props.info.receiveList || []">
        <template #sceneImg="{ text }">
          <TableImg v-if="text" :size="60" :simpleShow="true" :imgList="text.split(',')" />
        </template>
      </BasicTable>
    </a-card> -->
    <div class="change-box">
      <div v-if="!route.query.processId || !route.query.formInstanceId" style="text-align: center"
        ><a-button type="primary" :loading="loading" @click="submit">提交</a-button></div
      >
    </div>
    <a-modal v-model:visible="openVehicleUpload" title="提示" @ok="handleVehicleOk">
      <div class="blacklistUpload">
        <p>请按照模板规范填写，否则无法正确导入<a-button @click="onVehicledownLoadXls">下载模板</a-button></p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onVehicleImportXls">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
    <a-modal :width="1000" size="small" v-model:visible="choosePersonVisible" title="选择人员" :footer="null" @ok="choosePersonOk">
      <ChoosePersonTable v-if="choosePersonVisible" :params="searchPersonParams" @add="examineEmpChange" />
    </a-modal>
  </div>
</template>
<script setup lang="ts">
  import { useForm, BasicForm, FormSchema, ApiSelect } from '/@/components/Form';
  import { computed, onMounted, createVNode, ref, watchEffect } from 'vue';
  import { cloneDeep, has } from 'lodash-es';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useGlobSetting } from '/@/hooks/setting';
  import { BasicTable, useTable, TableImg } from '/@/components/Table';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { useTabs } from '/@/hooks/web/useTabs';
  import ChoosePersonTable from './choosePerson.vue';
  import { Modal, message } from 'ant-design-vue';
  import { ExclamationCircleOutlined } from '@ant-design/icons-vue';
  import { useUserStore } from '/@/store/modules/user';
  import { useRouter, useRoute } from 'vue-router';
  import dayjs from 'dayjs';
  import { ApplicantSchemas, ReleaseSchemas, goodsListColumns, outerColumns, receiveResultColumn, CheckSchemas } from '../config';
  import FormTable from './formTable.vue';
  import FormArray from './formArray.vue';
  import FormArrayGoogsList from './formArrayGoodsList.vue';
  import { SystemFlagEnum, leaveDataFlagOptions, outTypeFlag } from '../const';
  import SearchPerson from '/@/components/Form/src/components/SearchPerson.vue';
  import { downloadByOnlineUrl } from '/@/utils/file/download';

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
    getGoodsTemplateFile,
    downGoodsTemplateFile,
  } from '../field.api';
  const shortBargeCarOpt = [
    { label: '是', value: '是' },
    { label: '否', value: '否' },
  ];
  const router = useRouter();
  const route = useRoute();
  const globSetting = useGlobSetting();
  const { close } = useTabs();

  const userStore: any = useUserStore().getUserInfo;
  const { hasPermission } = usePermission();

  const props = defineProps({
    info: {
      type: Object,
      default: () => {},
    },
    outTypeList: {
      type: Array,
      default: () => [],
    },
    outFactoryIdMap: {
      type: Object,
      default: () => {},
    },
    outTypeFlagMap: {
      type: Object,
      default: () => {},
    },
  });
  const discretionFlag = ref(false);
  // 是否重新提交
  const isReSubmit = computed(() => {
    return route.query.type === 'reSubmit';
  });
  // 判断是否是当前节点处理人
  const showChange = computed(() => {
    if (userStore && userStore.email && props.info.curDealHandler) {
      const eip = userStore.email.split('@')[0];
      console.log(eip, props.info.curDealHandler.includes(eip));
      return props.info.curDealHandler.includes(eip);
    } else {
      return false;
    }
  });
  const colProps = { span: 12 };
  //点检人表单字段
  const disabled = ref(true);
  const choosePersonVisible = ref(false);
  const searchPersonParams = ref({});
  const selectChiefNo = (model) => {
    console.log(model);
    searchPersonParams.value = {
      outType: model.outTypeId,
      outFactory: model.outFactoryId,
      ownerEmpNo: userStore.workNo,
    };
    choosePersonVisible.value = true;
  };
  const fileList = ref<any[]>([]);
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
      subject: `${userStore.realName}${userStore.phone}`,
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
      microManagerNo: val,
      microManagerEip: e.email ? e.email.split('@')[0] : '',
      microManagerName: e.empName,
      microManagerPhone: e.phoneNo,
    });
  };
  // 放行信息
  const [registerReleaseForm, { setFieldsValue: setReleaseField, validate: validateRelease, updateSchema: updateReleaseSchema }] = useForm({
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
  const [registerCheckForm] = useForm({
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
  const getTeamOptions = (parentId) => {
    fieldTree({ parentId, topFlag: false }).then((res) => {
      updateSchema([
        {
          field: 'teamId',
          componentProps: {
            options: res.map((item) => ({ label: item.fieldName, value: item.fieldName })),
          },
        },
      ]);
    });
  };
  const shortBargeCarChange = (e) => {
    if (e.target.value == '是') {
      setFieldsValue({
        isVehicle: '是',
      });
    }
  };
  const checkArrayObj = (array) => {
    if (!array || array.length == 0) {
      return false;
    }
    const list = JSON.parse(array);
    const checkArray = list.map((item) => {
      delete item['remark'];
      const keys = Object.keys(item);
      console.log('key====', keys);
      return keys.every((key) => !!item[key]);
    });
    return checkArray.every((item) => item);
  };
  // 创建审批流程
  const submit = async () => {
    const formDataApplicant = await validateApplicant();
    delete formDataApplicant.referAttachment;
    debugger;
    const { goodsList = '', receiveList = '', vehicleList = '', outFactoryId, outTypeId, reasonType } = formDataApplicant;
    // 起草人不能是审核人
    if (formDataApplicant.examineEmpNo === userStore.workNo) {
      message.warning('起草人不能是审核人');
      return;
    }
    if (reasonType == '外部电脑退网' || reasonType == '公司以外区域办公') {
      Modal.confirm({
        title: '提示',
        content: '请在eip提交流程，路径：流程中心-新建电子流程-IT管理(J)>>信息安全管理(B) ',
        onOk() {
          return;
        },
        onCancel() {
          return;
        },
      });
      return;
    }
    // 解析接收人列表
    let parsedReceiveList = [];
    try {
      parsedReceiveList = typeof receiveList === 'string' ? JSON.parse(receiveList) : (receiveList || []);
    } catch (e) {
      parsedReceiveList = [];
    }
    const hasReceiveList = Array.isArray(parsedReceiveList) && parsedReceiveList.length > 0;
    // 如果填了接收人，强制 isSetAccept = '是'
    if (hasReceiveList) {
      formDataApplicant.isSetAccept = '是';
    }
    // 需要设置接收人时，校验接收人列表完整性
    if (formDataApplicant.isSetAccept == '是') {
      const flag = checkArrayObj(receiveList);
      if (!flag || !hasReceiveList) {
        message.error('请完善接收人列表！');
        return;
      }
    }
    if (outTypeId == '物资出厂') {
      if (!goodsList || goodsList == '[]') {
        message.error('请填写 物资清单列表！');
        return;
      } else {
        const flag = checkArrayObj(goodsList);
        if (!flag) {
          message.error('请完善物资清单列表！');
          return;
        }
      }
    }
    if (goodsList && goodsList.length > 0 && goodsList != '[]') {
      // message.error('物资清单列表不能为空！');
      const flag = checkArrayObj(goodsList);
      if (!flag) {
        message.error('请完善物资清单列表！');
        return;
      }
    }
    debugger;
    if (has(formDataApplicant, 'vehicleList') && !vehicleList) {
      message.error('车辆清单列表不能为空！');
      return;
    }
    let formParams = { ...formDataApplicant };
    if (has(formDataApplicant, 'weightDataUrl')) {
      formParams.weightDataFlag = '是';
    }
    //如果formParams 只有时分没有秒,则加上秒
    formParams.createTime = dayjs(formParams.createTime).format('YYYY-MM-DD HH:mm:ss');
    // isSetAccept 兜底：字段隐藏时根据是否有接收人补充默认值
    formParams.isSetAccept = formParams.isSetAccept ?? (hasReceiveList ? '是' : '否');
    await saveAndCreateProcess({
      ...formParams,
      outFactoryName: props.outFactoryIdMap[outFactoryId],
      outTypeName: outTypeId,
      outTypeId: props.outTypeFlagMap[outTypeId],
      goodsList: goodsList ? JSON.parse(goodsList) : [],
      receiveList: receiveList ? JSON.parse(receiveList) : [],
      vehicleList: vehicleList ? JSON.parse(vehicleList) : [],
      authId: authId.value,
      nodeFlag: formParams.outTypeId === outTypeFlag.ITAsset ? 9 : 0,
    });
    message.success('操作成功');

    // 创建流程之后关闭当前页面。
    close();
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
  let timer: any = null;
  const personQaSearch = (params) => {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
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
  const authId = ref('');
  watchEffect(() => {
    const { formInstanceId: formId } = route.query;

    if (Object.keys(props.info).length > 0 && formId) {
      const { createTime, nodeFlag, releaseStatus, exceptionDesc, curNodeId } = props.info;
      console.log(nodeFlag, 'nodeFlag');
      if (curNodeId == 'N2') {
        // 除了放行和接收，其他都能编辑，默认状态
        setFieldsValue({
          ...props.info,
          createTime: dayjs(createTime),
        });
      } else if (curNodeId == 'N7') {
        // 只能编辑放行信息，
        updateReleaseSchema([
          { field: 'releaseStatus', componentProps: { disabled: false } },
          { field: 'exceptionDesc', componentProps: { disabled: false } },
        ]);
        setFieldsValue({
          ...props.info,
          createTime: dayjs(createTime),
        });
        setReleaseField({
          releaseStatus,
          exceptionDesc,
        });
      } else if (curNodeId == 'N75') {
        // 只能编辑接收信息
        updateSchema([
          { field: 'receiveStatus', componentProps: { disabled: false } },
          { field: 'receiveConfirmImg', componentProps: { disabled: false } },
          // { field: 'checkReceiveStatus', componentProps: { disabled: false } },
          { field: 'checkReceiveReson', componentProps: { disabled: false } },
        ]);
        setFieldsValue({
          ...props.info,
          createTime: dayjs(createTime),
        });
      } else {
        if (!isReSubmit.value) {
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
            { field: 'vehicleType', componentProps: { disabled: true } },
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
          ]);
          updateReleaseSchema([
            { field: 'releaseStatus', componentProps: { disabled: true } },
            { field: 'releaseExceptionReason', componentProps: { disabled: true } },
            { field: 'uploadSitePhoto', componentProps: { disabled: true } },
            { field: 'exitReasonProof', componentProps: { disabled: true } },
            { field: 'referenceAttachment', componentProps: { disabled: true } },
          ]);
        }
        console.log(props.info.outTypeName, '******************');
        authId.value = props.info.authId;
        setFieldsValue({
          ...props.info,
          createTime: dayjs(createTime).format('YYYY-MM-DD HH:mm:ss'),
          outTypeId: props.info.outTypeName,
        });
      }
      setFieldsValue({
        outTypeId: props.info.outTypeName,
      });
    }
  });

  // 审核人相关
  const examineEmpList = ref<any[]>([]);
  let examineEmptimer: any = null;
  const examineEmpChange = (data) => {
    const { createTime, outFactoryId, outTypeId } = getEventFieldValue();
    if (data) {
      const { empDept, powerDesc, empNo, empEip, phoneNum, empName, authId: id } = data;
      // 起草人不能是审核人
      if (empNo === userStore.workNo) {
        message.warning('起草人不能是审核人');
        return;
      }
      authId.value = id;
      setFieldsValue({
        examineDept: empDept,
        outPowerDesc: powerDesc,
        examineEmpEip: empEip,
        examineEmpNo: empNo,
        examinePhone: phoneNum,
        examineEmpName: empName,
        subject: `${dayjs(createTime).format('YYYY年MM月DD日')}-${props.outFactoryIdMap[outFactoryId]}-${userStore.realName}${
          userStore.phone
        }-${outTypeId}`,
      });
      choosePersonVisible.value = false;
    }
  };
  const examineEmpSearch = (val) => {
    console.log(val, 'val------');
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

    const index = fieldList.findIndex((item) => item.fieldName === fields.outTypeId);
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
        gooutEmpName: empName,
        gooutPhone: phoneNo,
        gooutEmpDept: orgName,
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
  // function downloadFile() {
  //   // downloadByUrl({
  //   //   url: 'https://img.yutong.com/winered/app/oss/pull/mc-im/19af46d29c2f513ecee0c225cf5f45e2.xls',
  //   //   fileName: '物资出厂信息导入模板.xls',
  //   // });
  // }
  onMounted(async () => {
    let list = await getGoodsTemplateFile();
    fileList.value = list;
  });
  function downloadFile(item) {
    if (item.fileName === '出门证审核人清单.xlsx') {
      router.push('/exitpass/permissionconfig/people');
      return;
    }
    downGoodsTemplateFile({ fileId: item.fileId }).then((res) => {
      const url = window.URL.createObjectURL(new Blob([res]));
      const a = document.createElement('a');
      a.href = url;
      a.download = item.fileName;
      a.click();
    });
  }
</script>
<style scoped>
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
    max-width: 20% !important;
  }
  .attachen {
    display: flex;
    flex-direction: column;
  }
</style>
