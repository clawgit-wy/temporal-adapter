<template>
  <div class="content">
    <h1 class="title">外协外包入厂权限调整申请</h1>
    <div style="padding: 0 50px">
      <p class="no" v-if="flowNo">{{ flowNo }}</p>
      <p class="code">流程说明：本流程用于修改外协外包人员入厂权限，流程通过后权限立即生效。仅能由外协外包的内部对接人申请。</p>
    </div>
    <div style="padding: 0 50px">
      <BasicForm class="top-form" @register="registerForm" />
      <a-table bordered size="small" :pagination="false" :dataSource="currentRoleSource" :columns="columns">
        <template #title> 当前权限 </template>
      </a-table>
      <br />
      <a-table bordered size="small" :pagination="false" :dataSource="changeRoleSource" :columns="changeColumns">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'org' && !route.query.processId">
            <!-- <a-popover placement="topLeft">
              <template #content>
                <div style="width: 400px; height: 500px; overflow-y: auto;">
                  <a-tree
                    @select="(selects, e) => orgSelect(e, record)"
                    :load-data="onLoadData"
                    :tree-data="groupList"
                  />
                </div>

              </template>
              <p style="color: blue; cursor: pointer;" type="link">{{ record.org }}</p>
            </a-popover> -->
            <p>{{ record.org }}</p>
          </template>
        </template>

        <template #title> 变更后权限 </template>
        <template #footer v-if="!route.query.processId">
          <div>
            <a-upload v-model:file-list="fileList" name="file" :action="uploadUrl + Api.import" :headers="headers" @change="handleChange">
              <a-button> 选择文件 </a-button>
              <a-button style="margin-left: 20px" @click.stop="ondownLoadXls">下载模板</a-button>
            </a-upload>
          </div>
        </template>
      </a-table>
      <BasicForm v-if="!route.query.processId" @register="regForm" style="margin-top: 50px; margin-left: 50px">
        <template #sectionChiefNo="{ model, field }">
          <div style="display: flex">
            <a-input disabled v-model:value="model[field]" placeholder="请选择人员" />
            <a-button @click="selectChiefNo('sectionChiefNo')">选择人员</a-button>
          </div>
        </template>
        <template #deptHeadNo="{ model, field }">
          <div style="display: flex">
            <a-input disabled v-model:value="model[field]" placeholder="请选择人员" />
            <a-button @click="selectChiefNo('deptHeadNo')">选择人员</a-button>
          </div>
        </template>
      </BasicForm>
      <div style="padding: 20px 0">
        <iframe style="width: 100%; height: 3600px" v-if="iframe && route.query.processId" :src="iframe" frameborder="0" id="bpmIframe"></iframe>
      </div>
      <div class="change-box">
        <div v-if="!route.query.processId && hasPremission" style="text-align: center"
          ><a-button type="primary" :loading="loading" @click="submit">提交</a-button></div
        >
        <div v-if="route.query.processId && showChange && curStatus != '30'" style="text-align: center" :loading="loading"
          ><a-button type="primary" @click="auditSubmit">提交</a-button></div
        >
      </div>
    </div>
    <a-modal :width="1000" size="small" v-model:visible="choosePersonVisible" title="选择人员" :footer="null" @ok="choosePersonOk">
      <ChoosePersonTable v-if="choosePersonVisible" :dept="deptType" @add="onAdd" :checkRole="checkRole" />
    </a-modal>
  </div>
</template>
<script setup lang="tsx">
  import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
  import { message } from 'ant-design-vue';
  import { useForm, BasicForm } from '/@/components/Form';
  import { formSchemas } from './list.data';
  import {
    Api,
    infoForNo,
    factoryList,
    createFlow,
    view,
    audit,
    selectGroupTree,
    getEipNo,
    download,
    getApproverList,
    getCurrentNodesInfo,
    getEmployeeInfo,
  } from './list.api';
  import { onMounted, reactive, ref } from 'vue';
  import '/@/assets/bpm/domain';
  import { useUserStore } from '/@/store/modules/user';
  import { useRouter, useRoute } from 'vue-router';
  import { useGlobSetting } from '/@/hooks/setting';
  import { getToken } from '/@/utils/auth';
  import { downloadByData } from '/@/utils/file/download';
  import ChoosePersonTable from './choosePerson.vue';
  import dayjs from 'dayjs';
  import { BasicColumn } from '/@/components/Table';
  const globSetting = useGlobSetting();
  const deptType = ref(false);
  const { bpmUrl, uploadUrl } = globSetting;
  const router = useRouter();
  const route = useRoute();
  const { getUserInfo } = useUserStore();
  // 注册 form
  const [registerForm, { setFieldsValue, validate, setProps }] = useForm({
    labelCol: { style: { width: '140px' } },
    schemas: formSchemas,
    fieldMapToTime: [['date', ['beginDate', 'endDate'], 'YYYY-MM-DD']],
    showActionButtonGroup: false,
  });
  const headers = reactive({
    'X-Access-Token': getToken(),
  });
  // 选择人员相关
  const choosePersonVisible = ref(false);
  const choosePersonOk = () => {
    choosePersonVisible.value = false;
  };
  const checkRole = ref(false);
  // 选择变更人员
  const addPersonClick = (index, type) => {
    console.log(1);

    deptType.value = false;
    choosePresonType.value = type;
    addIndex.value = index;
    checkRole.value = true;
    console.log(checkRole.value);

    choosePersonVisible.value = true;
  };
  const addIndex = ref(null);
  // 添加
  const onAdd = (person) => {
    const selectPerson = JSON.parse(JSON.stringify(person));
    console.log(selectPerson, 'selectPerson');

    // 如果是变更权限人员
    if (choosePresonType.value == 'record') {
      const repeatFlag = currentRoleSource.value.some((item) => item.empNo == selectPerson.empNo);
      if (repeatFlag) {
        message.warn('员工号重复');
        return;
      }
      selectPerson.factory = selectPerson.factory.map((item) => factoryDic.value[item]).join();
      if (addIndex.value !== null) {
        changeRoleSource.value[addIndex.value] = JSON.parse(JSON.stringify(selectPerson));
        currentRoleSource.value[addIndex.value] = JSON.parse(JSON.stringify(selectPerson));
      }
      filterSecretFactory();
    }
    // 任职部门科长
    if (choosePresonType.value == 'sectionChiefNo') {
      regSetField({
        sectionChiefName: selectPerson.empName,
        sectionChiefUsername: selectPerson.email ? selectPerson.email.split('@')[0] : '',
        sectionChiefNo: selectPerson.empNo,
      });
    }
    // 任职部门负责人
    if (choosePresonType.value == 'deptHeadNo') {
      regSetField({
        deptHeadName: selectPerson.empName,
        deptHeadUsername: selectPerson.email ? selectPerson.email.split('@')[0] : '',
        deptHeadNo: selectPerson.empNo,
      });
    }
    choosePersonVisible.value = false;
  };
  const choosePresonType = ref('');
  // 选择任职部门科长
  const selectChiefNo = (type) => {
    console.log(2);

    deptType.value = true;
    choosePresonType.value = type;
    checkRole.value = false;
    console.log(checkRole.value);

    choosePersonVisible.value = true;
  };
  const columns = ref([
    { title: '序号', dataIndex: 'index', key: 'index', width: 80, align: 'center', customRender: ({ index }) => index + 1 },
    {
      title: '员工号',
      dataIndex: 'empNo',
      width: 150,
      key: 'empNo',
      customRender: ({ text, record, index }) => (text ? text : <a-button onClick={() => addPersonClick(index, 'record')}>选择人员</a-button>),
    },
    { title: '姓名', dataIndex: 'empName', width: 130, key: 'empName' },
    { title: '外协组织', dataIndex: 'org', width: 400, key: 'org' },
    { title: '已授权厂区', dataIndex: 'factory', key: 'factory' },
    { title: '服务截止日期', dataIndex: 'serviceEndDate', key: 'serviceEndDate' },
    {
      title: '',
      width: 100,
      dataIndex: 'action',
      key: 'action',
      customRender: ({ index }) => (
        <>
          <PlusOutlined onClick={() => add(index)} style="color: #1890ff; margin-right: 20px;" class="change-icon" />
          {index ? <DeleteOutlined onClick={() => sub(index)} style="color: red;" class="change-icon" /> : ''}
        </>
      ),
    },
  ]);

  const changeColumns = ref([
    { title: '序号', dataIndex: 'index', key: 'index', width: 80, align: 'center', customRender: ({ index }) => index + 1 },
    { title: '员工号', dataIndex: 'empNo', width: 150, key: 'empNo' },
    { title: '姓名', dataIndex: 'empName', width: 130, key: 'empName' },
    { title: '外协组织', dataIndex: 'org', width: 400, key: 'org' },
    {
      title: '授权厂区',
      dataIndex: 'factoryId',
      key: 'factoryId',
      customRender: ({ text, record }) => (
        <a-select options={factorys.value} style="width: 100%" onChange={(val) => factoryChange(val, record)} mode="multiple" value={text}></a-select>
      ),
    },
    {
      title: '服务截止日期',
      dataIndex: 'serviceEndDate',
      key: 'serviceEndDate',
      customRender: ({ text, record }) => (
        <a-date-picker
          placeholder="请选择服务截止日期"
          value={text}
          onChange={(val) => dateChange(val, record)}
          valueFormat="YYYY-MM-DD"
          style="width: 100%"
          allowClear={false}
          disabled-date={disabledDate}
        ></a-date-picker>
      ),
    },
  ]);

  const disabledDate = (current) => {
    return current < dayjs().subtract(1, 'day') || current > dayjs().add(365, 'day');
  };

  const secret = ref('0');
  // 检测授权厂区是否涉密
  const filterSecretFactory = () => {
    let cache = '0';
    changeRoleSource.value.forEach((item) => {
      const sercrets = ['研发中心', '工程试验中心'];
      const f = item.factoryId.some((child) => {
        return sercrets.includes(factoryDic.value[child]);
      });
      if (f) {
        cache = '1';
      }
    });
    secret.value = cache;
  };
  // 改变授权厂区
  const factoryChange = (val, record) => {
    record.factoryId = val;
    filterSecretFactory();
  };
  // 改变截止日期
  const dateChange = (val, record) => {
    console.log('日期改变=', val, record);
    record.serviceEndDate = val;
  };
  // 添加
  const add = (index) => {
    currentRoleSource.value.splice(
      index + 1,
      0,
      JSON.parse(
        JSON.stringify({
          empNo: '',
          empName: '',
          empzz: '',
          factory: '',
        })
      )
    );
    changeRoleSource.value.splice(
      index + 1,
      0,
      JSON.parse(
        JSON.stringify({
          empNo: '',
          empName: '',
          empzz: '',
          factory: '',
        })
      )
    );
  };
  // 删除
  const sub = (index) => {
    currentRoleSource.value.splice(index, 1);
    changeRoleSource.value.splice(index, 1);
  };
  // 当前权限table
  const currentRoleSource = ref([{ empNo: '', empName: '', empzz: '', factory: '' }]);
  // 修改后的权限
  const changeRoleSource = ref([{ empNo: '', empName: '', empzz: '', factory: '' }]);
  // 审批人信息
  const [regForm, { setFieldsValue: regSetField, validate: orgValidate, getFieldsValue: orgGetFieldsValue }] = useForm({
    labelCol: { style: { width: '140px' } },
    schemas: [
      {
        label: '任职部门科长',
        field: 'sectionChiefNo',
        required: true,
        colProps: { span: 12 },
        component: 'Input',
        slot: 'sectionChiefNo',
      },
      {
        label: '任职部门科长姓名',
        field: 'sectionChiefName',
        required: true,
        colProps: { span: 12 },
        component: 'Input',
        componentProps: {
          disabled: true,
        },
      },
      {
        label: '任职部门科长EIP',
        field: 'sectionChiefUsername',
        required: true,
        colProps: { span: 12 },
        component: 'Input',
        show: false,
        componentProps: {
          disabled: true,
        },
      },
      {
        label: '任职部门负责人',
        field: 'deptHeadNo',
        colProps: { span: 12 },
        required: true,
        component: 'Input',
        ifShow: () => secret.value == '1',
        slot: 'deptHeadNo',
      },
      {
        label: '任职部门负责人姓名',
        field: 'deptHeadName',
        required: true,
        component: 'Input',
        colProps: { span: 12 },
        ifShow: () => secret.value == '1',
        componentProps: {
          disabled: true,
        },
      },
      {
        label: '任职部门负责人EIP',
        field: 'deptHeadUsername',
        ifShow: () => secret.value == '1',
        colProps: { span: 12 },
        required: true,
        show: false,
        component: 'Input',
      },
    ],
    showActionButtonGroup: false,
  });
  const iframe = ref();
  const processParam = ref({});
  const hasPremission = ref(false);
  const initCreateForm = async () => {
    await initFactoryData();
    const { workNo, phone, realName } = getUserInfo;
    let no = workNo;
    if (workNo.startsWith('01')) {
      const resNo = await getEipNo({ workNo: workNo });
      no = resNo;
    }
    // 获取员工部门
    const { orgPath } = await infoForNo({ empNos: no, type: 'dept' });
    console.log(orgPath, 'orgpath');

    // 获取员工部门岗位类型
    const { positionType } = await getEmployeeInfo({ empNo: no });
    hasPremission.value = positionType == '间接岗位';
    console.log('是否有权限=', positionType, hasPremission.value);
    if (!hasPremission.value) {
      message.error('非外协外包对接人无法提交此流程。如有疑问请联系司法保卫室司志豪（0371 8533 7339）');
    }

    const title = `${realName}提交的外协外包入厂权限申请流程`;
    setFieldsValue({
      flowId: '', // 流程id
      title: title, // 流程主题
      creatorName: realName,
      creator: no,
      creatorDept: orgPath ? orgPath : '',
      creatorPhone: phone,
    });
    processParam.value = {
      'processParam.subject': title,
      'processParam.flowId': '',
      'processParam.modelId': 'outsider_promise',
      'processParam.formId': 'safety_outsider_promise_change',
      'processParam.type': 'add',
    };
  };
  const disabledForm = () => {
    setProps({ disabled: true });
    columns.value.splice(1, 1, { title: '员工号', dataIndex: 'empNo', width: 150, key: 'empNo' });
    columns.value.pop();
    changeColumns.value.splice(4, 1, { title: '已授权厂区', dataIndex: 'factory', key: 'factory' });
    changeColumns.value.splice(5, 1, { title: '服务截止日期', dataIndex: 'serviceEndDate', key: 'serviceEndDate' });
  };
  const flowNo = ref();
  const showChange = ref(false);
  const curNode = ref('');
  const curStatus = ref('');
  const initViewForm = async () => {
    disabledForm();
    iframe.value = `${bpmUrl}?method=showProcess&sysId=SAFETY&modelId=outsider_promise&templateFormId=safety_outsider_promise_change&formInstanceId=${route.query.formInstanceId}&processId=${route.query.processId}`;
    await initFactoryData();
    const res = await view({ flowId: route.query.processId });
    curStatus.value = res.status;

    // 获取当前节点 如果是n2 代表驳回 可编辑
    const node = await getCurrentNodesInfo({ id: res.id, flowId: route.query.processId, formId: route.query.formInstanceId });
    const currentNode = JSON.parse(node);
    if (currentNode && currentNode.length > 0) {
      curNode.value = currentNode[0].nodeId;
      if (currentNode[0].nodeId != 'N2') {
      }
    }
    // 获取当前审批人
    const getCurPerson = await getApproverList({ id: res.id, flowId: route.query.processId, formId: route.query.formInstanceId });
    const persons = JSON.parse(getCurPerson);

    showChange.value = persons.some((item) => item.LoginName == getUserInfo.userName);
    const { flowNo: fNo, title, id, flowId, secret: s } = JSON.parse(JSON.stringify(res));
    flowNo.value = fNo;
    const detailDTOs = JSON.parse(res.detailDTOs);
    const oldDTOs = JSON.parse(res.oldDetailDTOs);

    delete res.detailDTOs;
    setFieldsValue({
      ...res,
    });
    secret.value = s;
    processParam.value = {
      id: id,
      'processParam.subject': title,
      'processParam.flowId': flowId,
      'processParam.modelId': 'outsider_promise',
      'processParam.formId': 'safety_outsider_promise_change',
      'processParam.type': 'audit',
    };
    currentRoleSource.value = oldDTOs.map((item) => ({
      ...item,
      org: item.org,
      factory: item.factory
        .split(',')
        .map((child) => factoryDic.value[child])
        .join(),
    }));
    changeRoleSource.value = detailDTOs.map((item) => ({
      ...item,
      factoryId: item.factory.split(',').map((item) => Number(item)),
      factory: item.factory
        .split(',')
        .map((child) => factoryDic.value[child])
        .join(),
    }));
  };
  const factorys = ref([]);
  const factoryDic = ref({});

  const initFactoryData = async () => {
    const res = await factoryList();

    const factoryObj = {};
    factorys.value = res.map((item) => {
      factoryObj[item.id] = item.fieldName;
      return {
        label: item.fieldName,
        value: item.id,
      };
    });
    factoryDic.value = factoryObj;
  };
  // 获取流程
  const getBpmData = () => {
    return new Promise((resolve, reject) => {
      const flowIFrame = document.getElementById('bpmIframe');
      const domain = window?.domain || '';
      if (domain) {
        domain.call(flowIFrame.contentWindow, 'lbpm_getApprovalData', [], function (data) {
          const res = JSON.parse(data);
          resolve(JSON.parse(res['sysWfBusinessForm.fdParameterJson']));
        });
      } else {
        reject();
      }
    });
  };
  const loading = ref(false);
  const submit = async () => {
    loading.value = true;
    try {
      const baseForm = await validate();
      const orgForm = await orgValidate();
      // if (secret.value ==)
      const detailDTOs = changeRoleSource.value.map((item) => {
        const el = JSON.parse(JSON.stringify(item));
        const factoryId = item.factoryId.join();
        delete el.factoryId;
        return {
          ...el,
          factory: factoryId,
        };
      });

      const oldDetailDTOs = currentRoleSource.value.map((item) => ({
        empNo: item.empNo,
        empName: item.empName,
        org: item.org,
        factory: item.factoryId.join(),
        serviceEndDate: item.serviceEndDate,
      }));

      const formData = {
        ...baseForm,
        ...orgForm,
        ...processParam.value,
        status: 1,
        oldDetailDTOs: JSON.stringify(oldDetailDTOs),
        detailDTOs: JSON.stringify(detailDTOs),
      };
      await createFlow(formData);
      message.success('操作成功');
      loading.value = false;
      router.replace('/securityService/outAndIn/epiboly');
    } catch (error) {
      console.log(error);

      loading.value = false;
      message.error('请检查输入项');
    }
  };
  const auditSubmit = async () => {
    loading.value = true;
    const bpmData = await getBpmData();
    let formData = {
      flowId: route.query.processId,
      ...processParam.value,
      'processParam.fdParameterJson': JSON.stringify({ 'sysWfBusinessForm.fdParameterJson': bpmData }),
    };
    // if (curNode.value == 'N2') {
    //   const detailDTOs = changeRoleSource.value.map(item => {
    //     const el = JSON.parse(JSON.stringify(item))
    //     const factoryId = item.factoryId.join()
    //     delete el.factoryId
    //     return {
    //       ...el,
    //       factory: factoryId
    //     }
    //   })
    //   formData['detailDTOs'] = JSON.stringify(detailDTOs)
    // }
    const res = await audit(formData);
    if (res == 'T') {
      iframe.value = '';
      message.success('操作成功');
      setTimeout(() => location.reload(), 100);
    }
    loading.value = false;
    console.log(res);
  };
  // 加载组织架构
  const onLoadData = async (treeNode) => {
    if (treeNode.dataRef.children) {
      return;
    }
    const res = await selectGroupTree({ key: treeNode.key });
    treeNode.dataRef.children = res.dataList;
    groupList.value = [...groupList.value];

    return true;
  };
  // 组织架构选择
  const orgSelect = (e, record) => {
    console.log(e);

    const parents = e.node.parent.nodes.map((item) => item.title);
    const deps = [...parents, e.node.title];
    record.org = deps.join('>>');
    console.log(deps);
  };
  // 导入
  const fileList = ref([]);
  const handleChange = ({ file }) => {
    console.log(file.status, file);

    if (file.status === 'done') {
      if (file.response.code != '0') {
        return message.warn(file.response.msg);
      }
      if (file.response.data) {
        const datas = JSON.parse(file.response.data);
        console.log(datas, 'import');
        if (datas.msg) {
          message.warn(datas.msg);
        }
        currentRoleSource.value = datas.oldPromise.map((item) => {
          return {
            ...item,
            org: item.empOrg,
            factory: item.factory
              ? item.factory
                  .split(',')
                  .map((item) => factoryDic.value[item])
                  .join()
              : '',
            factoryId: item.factory ? item.factory.split(',').map((item) => Number(item)) : '',
          };
        });
        changeRoleSource.value = datas.empDatas.map((item) => {
          return {
            ...item,
            empName: item.name,
            org: item.orgPath ? item.orgPath : '',
            factory: item.factoryStr
              .split(',')
              .map((item) => factoryDic.value[item])
              .join(),
            factoryId: item.factoryStr.split(',').map((item) => Number(item)),
          };
        });
      }
    }
  };
  // 下载模板
  async function ondownLoadXls(e) {
    const res = await download({});
    downloadByData(res, '导入模板.xlsx');
  }
  const groupList = ref([]);
  selectGroupTree({}).then((res) => {
    groupList.value = res.dataList;
  });

  // const getGroupTree = (params = {}) => {

  // }
  // getGroupTree()
  onMounted(() => {
    if (route.query.processId) {
      initViewForm();
    } else {
      initCreateForm();
    }
  });
</script>
<style scoped lang="less">
  .top-form {
    border: 1px solid #e5e7eb;
    padding: 20px 0;
    margin-bottom: 20px;
  }

  .content {
    background-color: #fff;
    padding: 20px;
  }
  .title {
    text-align: center;
    font-size: 24px;
  }
  .no {
    text-align: right;
  }
  :deep(.ant-col-12 .ant-form-item-control-input) {
    width: 390px !important;
  }
  :deep(.ant-col-12 .ant-input-number) {
    width: 390px !important;
  }
  .change-icon {
    color: #1890ff;
    margin: 0 10px;
    cursor: pointer;
  }
  .change-box {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    padding: 5px 0;
  }
</style>
