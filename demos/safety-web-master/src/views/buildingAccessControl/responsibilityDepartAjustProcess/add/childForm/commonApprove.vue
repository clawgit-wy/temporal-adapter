<template>
  <div>
    <a-card title="审批信息">
      <BasicForm class="top-form" @register="registerApplicantForm">
        <template #department="{ model, field, schema }">
          <a-input-group compact>
            <a-input disabled v-model:value="model[field]" style="width: calc(100% - 100px)"/>
            <a-button type="primary" @click="searchPerson" v-if="!isApprove">选择</a-button>
          </a-input-group>
        </template>
        <template #buildingList="{ model, field, schema }">
          <FormTable
            v-model:value="model[field]"
            :columns="buildingColumns"
            :disabled="schema.componentProps.disabled"
            :buildMultipleNum="buildMultipleNum"
            @exceed="buildeChoseExceed"
          ></FormTable>
        </template>
        <template #departmentManager>
          <a-form-item-rest>
            <person-select-input
              v-model:personList="secretAreaManagerPersons"
              :placeholder="'请选择本部门部长'"
              :multiple="false"
              :disabled="isApprove"
            />
          </a-form-item-rest>
        </template>
        <template #buildSysManagerRole="{ model, field, schema }">
          <a-select
            v-model:value="model[field]"
            show-search
            placeholder="请输入角色名称或者编码后选择"
            style="width: 350px"
            :default-active-first-option="false"
            :show-arrow="false"
            :filter-option="false"
            :not-found-content="null"
            :disabled="(isApprove && !showChange) || isOver"
            :options="data"
            @search="handleSearch"
            @select="handleSelect"
          ></a-select>
        </template>
        <template #tips>
          <div class="tips-content">
            <p>注意:
              门禁管理员调整后请确认部门内人员门禁授权情况和白名单，对本部门人员门禁授权范围负责！</p>
            <p>
              办公楼的公共区域（如：1楼、-1楼）如果存在门禁管制，请同步开通管辖区域所在办公楼公共区域门禁管理权限</p>
          </div>
        </template>
      </BasicForm>
    </a-card>
    <br/>
    <div class="change-box">
      <div v-if="!route.query.id && !isApprove" style="text-align: center">
        <a-button type="primary" :loading="loading" @click="submit">提交</a-button>
      </div>
      <div v-if="isApprove && showChange && !isOver" style="text-align: center">
        <a-button type="primary" :loading="loading" @click="approve(0)">不通过</a-button>
        <a-button type="primary" :loading="loading" @click="approve(1)" style="margin-left: 10px">
          通过
        </a-button>
      </div>
    </div>
    <DepartmentSelect v-model:visible="visible" @change="perChange"></DepartmentSelect>
  </div>
</template>
<script setup lang="ts">
import {useForm, BasicForm, FormSchema, ApiSelect} from '/@/components/Form';
import {computed, onMounted, createVNode, ref, watchEffect, watch} from 'vue';
import {cloneDeep, has} from 'lodash-es';
import {useMessage} from '/@/hooks/web/useMessage';
import {useListPage} from '/@/hooks/system/useListPage';
import {useGlobSetting} from '/@/hooks/setting';
import {BasicTable, useTable, TableImg} from '/@/components/Table';
import {usePermission} from '/@/hooks/web/usePermission';
import {useTabs} from '/@/hooks/web/useTabs';
import {Modal, message} from 'ant-design-vue';
import {ExclamationCircleOutlined} from '@ant-design/icons-vue';
import {useUserStore} from '/@/store/modules/user';
import {useRouter, useRoute} from 'vue-router';
import dayjs from 'dayjs';
import {ApplicantSchemas, buildingColumns, GetApplicantSchemas} from '../config';
import {SystemFlagEnum} from '../const';
import FormTable from './formTable.vue';
import {
  getEmployeeByWorkNo,
  getGuardInfoListByDept,
  saveAndCreateProcess,
  approvalProcess,
  checkBuildRoleType,
  getRoleList,
  managerList,
  getRoles,
} from '../field.api';
import DepartmentSelect from '/@/components/DepartmentSelect.vue';
import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';
import {debounce} from 'lodash-es';

const router = useRouter();
const route = useRoute();
const globSetting = useGlobSetting();
const {close} = useTabs();
import '/@/assets/bpm/domain'

const userStore: any = useUserStore().getUserInfo;
console.log('登录人信息==', userStore);
const {hasPermission} = usePermission();

const props = defineProps({
  info: {
    type: Object,
    default: () => {
    },
  },
  outTypeList: {
    type: Array,
    default: () => [],
  },
  outFactoryIdMap: {
    type: Object,
    default: () => {
    },
  },
  outTypeFlagMap: {
    type: Object,
    default: () => {
    },
  },
});
const isApprove = computed(() => props.info.nodeFlag != null || props.info.nodeFlag != undefined);

// 申请人信息
const [
  registerApplicantForm,
  {
    setFieldsValue,
    validate: validateApplicant,
    setProps: setEventProps,
    updateSchema,
    getFieldsValue: getEventFieldValue
  },
] = useForm({
  layout: 'vertical',
  //注册表单列
  schemas: GetApplicantSchemas(isApprove),
  model: {
    createEmployeeName: userStore.realName,
    createEmployeeNo: userStore.workNo,
    createEmployeeLogin: userStore.email.split('@')[0],
    processSubject: `${dayjs(new Date()).format('YYYY-MM-DD')}-${userStore.realName}-申请楼宇门禁责任部门调整流程`,
  },
  disabled: false,
  //回车提交
  autoSubmitOnEnter: true,
  //不显示重置按钮
  showResetButton: false,
  // 不显示提交按钮
  showSubmitButton: false,
  //查询列占比 24代表一行 取值范围 0-24
  actionColOptions: {span: 24},
});

const isOver = computed(() => (props.info.curNodeId == 'N3' && props.info.processStatus == '30') || props.info.processStatus == '00');
const isShowEipRole = ref(false);
console.log('是否是审批', isApprove.value);

function splitArr(str) {
  if (str) {
    if (str.includes(',')) {
      return str.split(',');
    }
    return [str];
  }
  return [];
}

// 个人信息
const currentUserRoleType = ref('');
const initEmployee = async () => {
  const {workNo, phone} = userStore;
  getEmployeeByWorkNo({workNo}).then((res) => {
    const {empName, empNo, email, orgCode, orgName} = res;
    setFieldsValue({
      createEmployeeName: empName,
      createEmployeeLogin: email ? email.split('@')[0] : '',
      createEmployeeNo: empNo,
      createDeptCode: orgCode,
      createDeptName: orgName,
    });
  });
  currentUserRoleType.value = await checkBuildRoleType({});
  setFieldsValue({
    roleType: currentUserRoleType.value,
  });
};
const getManagerList = async () => {
  const roles = await getRoles({});
  const manageItem = roles.find((item) => item.name == '门禁系统管理员');
  if (manageItem) {
    managerList({roleId: manageItem.id}).then((res) => {
      const nameArr = splitArr(res.dataList.map((item) => item.empName).join(','));
      const codeArr = splitArr(res.dataList.map((item) => item.empNo).join(','));
      buildPeole.value = nameArr != null && nameArr.length > 0 ? nameArr.map((item, index) => `${item}:${codeArr[index]}`).join(',') : '';
    });
  }
};
if (!isApprove.value) {
  initEmployee();
  getManagerList();
}

//组织选择
const visible = ref(false);
const buildMultipleNum = ref(0);
const buildPeole = ref('');
const searchPerson = () => {
  visible.value = true;
};
const perChange = (depart) => {
  console.log('组织选择==', depart);
  setFieldsValue({
    dutyDeptName: depart.title,
    dutyDeptCode: depart.key,
    dutyDeptCodePath: depart.codePath,
  });
  visible.value = false;
  getGuardInfoListByDeptForBuilding(depart.key);
};

const getGuardInfoListByDeptForBuilding = async (key) => {
  let res = await getGuardInfoListByDept({dutyDeptCode: key});
  console.log('根据部门获取对应的门禁数据==', res);
  const {
    oldDeviceName,
    oldDeviceCode,
    newDeviceName,
    newDeviceCode,
    oldDepartEmployeeName,
    oldDepartEmployeeLogin,
    oldDepartEmployeeCode,
    newDepartEmployeeName,
    neweDepartEmployeeLogin,
    newDepartEmployeeCode,
    buildNum,
  } = res;
  buildMultipleNum.value = buildNum;

  setFieldsValue({
    buildingList: JSON.stringify([
      {
        oldDeviceName,
        oldDeviceCode,
        newDeviceName,
        newDeviceCode,
        oldDepartEmployeeName,
        oldDepartEmployeeLogin,
        oldDepartEmployeeCode,
        newDepartEmployeeName,
        neweDepartEmployeeLogin,
        newDepartEmployeeCode,
      },
    ]),
  });
};

function buildeChoseExceed() {
  message.warn(`您部门可管理门禁数量超过限制，请联系门禁管理员${buildPeole.value}`);
}

// 部门长
const secretAreaManagerPersons = ref<any[]>([]);
// 监听人员列表变化
watch(
  () => secretAreaManagerPersons.value,
  (persons) => {
    // 更新表单中的人员 topSecretEmpCode  topSecretEmpLogin topSecretEmpName
    updateSecretManagerIds(persons);
  },
  {deep: true}
);
//将 topSecretEmpCode,topSecretEmpLogin,topSecretEmpName 转换为数组并赋值给secretAreaManagerPersons
const updateSecretManagerPersons = (record) => {
  console.log('secretAreaManagerPersons++++++', record);
  secretAreaManagerPersons.value = [{
    id: record.topSecretEmpCode,
    name: record.topSecretEmpName,
    email: record.topSecretEmpLogin
  }];
};
// 更新表单中的人员 topSecretEmpCode  topSecretEmpLogin topSecretEmpName
const updateSecretManagerIds = (persons: any[]) => {
  // 将所有ID以逗号分隔存入表单

  const leaderEmployeeLogin =
    persons.length > 0
      ? persons
        .map((p) => {
          return p.email?.split('@')[0] || '';
        })
        .join(',')
      : '';
  const leaderEmployeeName = persons.map((p) => p.name).join(',');
  setFieldsValue({
    leaderEmployeeName,
    leaderEmployeeLogin,
  });
};

//eip角色
const data = ref([]);
const handleSearch = debounce(async (val) => {
  console.log('搜索==', val);
  // fetch(val, d => (data.value = d));
  if (!val) return;
  let res = await getRoleList({roleName: val});
  console.log('eip角色获取信息==', res);
  data.value =
    res != null && res.length > 0
      ? res
        .filter((item) => item.roleName && item.roleCode && item.roleName != 'null' && item.roleCode != 'null')
        .map((item) => {
          return {
            label: item.roleName,
            value: item.roleCode,
          };
        })
      : [];
  console.log('帅选项==', data.value);
}, 300);

const handleSelect = (value) => {
  console.log('选中==', value);
  setFieldsValue({
    buildSysManagerRoleCode: value,
  });
};

const discretionFlag = ref(false);

// 判断是否是当前节点处理人
const showChange = computed(() => {
  if (userStore && userStore.email && props.info.curNodeHandler) {
    const eip = userStore.email.split('@')[0];
    console.log(eip, props.info.curNodeHandler.includes(eip));
    return props.info.curNodeHandler.includes(eip);
  } else {
    return false;
  }
});

console.log(userStore, 'userStore');

// 创建审批流程
const submit = async () => {
  const formDataApplicant = await validateApplicant();
  console.log('提交流程验证信息==', formDataApplicant);
  const {buildingList = ''} = formDataApplicant;
  const buildingListArr = JSON.parse(buildingList);
  if (!buildingListArr || buildingListArr.length == 0 || !buildingListArr[0].newDepartEmployeeName) {
    message.error('请完善门禁信息！');
    return;
  }
  if (buildingListArr[0]?.newDeviceName?.split(',').length > buildMultipleNum.value) {
    buildeChoseExceed();
    return;
  }
  delete formDataApplicant.tips;
  let formParams = {
    ...formDataApplicant,
    oldDeviceName: buildingListArr[0].oldDeviceName,
    oldDeviceCode: buildingListArr[0].oldDeviceCode,
    newDeviceName: buildingListArr[0].newDeviceName,
    newDeviceCode: buildingListArr[0].newDeviceCode,
    oldDepartEmployeeName: buildingListArr[0].oldDepartEmployeeName,
    oldDepartEmployeeCode: buildingListArr[0].oldDepartEmployeeCode,
    oldDepartEmployeeLogin: buildingListArr[0].oldDepartEmployeeLogin,
    newDepartEmployeeName: buildingListArr[0].newDepartEmployeeName,
    newDepartEmployeeCode: buildingListArr[0].newDepartEmployeeCode,
    neweDepartEmployeeLogin: buildingListArr[0].neweDepartEmployeeLogin,
  };
  console.log('提交流程信息==', formParams);
  await saveAndCreateProcess({
    ...formParams,
    nodeFlag: 0, //0是起草节点；1是绝密区负责人审核；2是部门长审核 3门禁系统管理员
  });
  message.success('操作成功');
  // 创建流程之后关闭当前页面。
  router.back();
};

//审批相关
//审批
async function approve(type) {
  //0不通过 1通过

  // 如果是不通过，设置为非必填
  if (type == 0 && props.info.curNodeId == 'N10') {
    await updateSchema([
      {field: 'buildSysManagerRole', required: false},
      {field: 'buildSysManagerRoleCode', required: false},
    ]);
  }

  const {processSubject, id, bpmFlowId} = props.info || {};
  const formDataApplicant = await validateApplicant();
  const {buildSysManagerRole, buildSysManagerRoleCode} = formDataApplicant;

  const params = {
    // nodeFlag: 1,//审批不传了 后台自己判断
    auditNote: type == 0 ? '不通过' : '通过',
    bpmFlowId,
    id,
    buildSysManagerRole,
    buildSysManagerRoleCode,
  };
  if (!isShowEipRole.value) {
    delete params.buildSysManagerRole;
    delete params.buildSysManagerRoleCode;
  }
  const bpmData = await getBpmData();
  params.fdParameterJson = JSON.stringify(bpmData);
  console.log('pppppppppp',params)
  await approvalProcess(params);
  message.success('操作成功');
  close();
  router.back();
}

// 获取流程
const getBpmData = () => {
  return new Promise((resolve, reject) => {
    const flowIFrame = document.getElementById('bpmIframe');

    const domain = window?.domain || '';
    console.log('111a', window)
    if (domain) {
      console.log('222')
      domain.call(flowIFrame.contentWindow, 'lbpm_getApprovalData', [], function (data) {
        console.log('444')
        const res = JSON.parse(data);
        const result = res['sysWfBusinessForm.fdParameterJson'];
        if (result) {
          resolve(JSON.parse(result));
        }
        reject('获取iframe数据失败');
      });
    } else {
      console.log('333')
      reject('获取iframe数据失败');
    }
  });
};

// 监听数据进行数据回填
// nodeFlag //0是起草节点；1是绝密区负责人审核；2是部门长审核
watchEffect(() => {
  const {formInstanceId} = route.query;
  if (Object.keys(props.info).length > 0 && formInstanceId) {
    const {nodeFlag, curNodeId, leaderEmployeeName} = props.info;
    console.log(nodeFlag, 'nodeFlag');
    secretAreaManagerPersons.value = [{name: leaderEmployeeName}];
    isShowEipRole.value = props.info.nodeFlag == 3 && showChange;
    if (isShowEipRole.value) {
      //nodeflag:3 且当前登录人是处理人需要选择eip角色
      updateSchema([
        {field: 'buildSysManagerRole', required: true, show: true},
        {field: 'buildSysManagerRoleCode', required: true, show: false},
      ]);
    }
    updateSchema([
      {field: 'dutyDeptName', componentProps: {disabled: true}},
      {field: 'buildingList', componentProps: {disabled: true}},
      {field: 'leaderEmployeeName', componentProps: {disabled: true}},
    ]);
    setFieldsValue({
      ...props.info,
      buildingList: JSON.stringify([
        {
          oldDeviceName: props.info.oldDeviceName,
          oldDeviceCode: props.info.oldDeviceCode,
          newDeviceName: props.info.newDeviceName,
          newDeviceCode: props.info.newDeviceCode,
          oldDepartEmployeeName: props.info.oldDepartEmployeeName,
          oldDepartEmployeeLogin: props.info.oldDepartEmployeeLogin,
          oldDepartEmployeeCode: props.info.oldDepartEmployeeCode,
          newDepartEmployeeName: props.info.newDepartEmployeeName,
          neweDepartEmployeeLogin: props.info.neweDepartEmployeeLogin,
          newDepartEmployeeCode: props.info.newDepartEmployeeCode,
        },
      ]),
    });
  } else {
    updateSchema([
      {field: 'buildSysManagerRole', required: false, show: false},
      {field: 'buildSysManagerRoleCode', required: false, show: false},
      {field: 'dutyDeptName', componentProps: {disabled: false}},
      {field: 'buildingList', componentProps: {disabled: false}},
      {field: 'leaderEmployeeName', componentProps: {disabled: false}},
    ]);
  }
});

const [registerTable] = useTable({
  rowKey: 'id',
  pagination: false,
  showSizeChanger: false,
  showTableSetting: false,
  showIndexColumn: true,
  useSearchForm: false,
});
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

.tips-content {
  font-size: 14px;
  line-height: 1.6;
  border: 1px solid #ff4d4f;
  border-radius: 4px;
  padding: 10px;
  background-color: #fff1f0;
  color: #1890ff;

  p {
    margin-bottom: 0px;
  }
}
</style>
