<template>
  <div class="container">
    <h1 style="text-align: center">监控岗位上报</h1>
    <div style="text-align: right; padding-right: 200px">
      <a-button type="primary" @click="circularizeVisible = true">传阅</a-button>
    </div>
    <BasicForm class="top-form" @register="registerForm" />
    <a-divider orientation="left">处理结果</a-divider>
    <BasicForm @register="registerApprove" @submit="handleSubmit" />
    <div style="padding: 0 20px">
      <iframe v-if="iframe" style="height: 1350px" :src="iframe" frameborder="0" id="bpmIframe"></iframe>
    </div>
    <div class="change-box" v-if="!processEnd && !$route.query.look && approveUserCheck">
      <a-button type="primary" @click="handleSubmit">提交</a-button>
    </div>
    <a-modal
      destroyOnClose
      v-model:visible="circularizeVisible"
      title="传阅"
      :confirmLoading="circularizeLoading"
      @ok="submitCircularize"
      width="600px"
    >
      <ul class="temp-box">
        <li>
          <p>传阅给：</p>
          <SearchPerson mode="multiple" style="width: 500px" @change="personChange"></SearchPerson>
        </li>
      </ul>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
  import { ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { useForm, BasicForm, FormSchema } from '/@/components/Form';
  import { underLine2CamelCase } from '/@/utils/common/compUtils';
  import '/@/assets/bpm/domain';
  import { updateProcess, getInfo, fieldTree, getNode, getFieldRule } from './field.api';
  import { useGlobSetting } from '/@/hooks/setting';
  import { useUserStore } from '/@/store/modules/user';
  import SearchPerson from '/@/components/Form/src/components/SearchPerson.vue';
  import { circulate, getProcessId } from '/@/views/facilities/approval/field.api';
  import { message } from 'ant-design-vue';
  const userStore = useUserStore();
  const globSetting = useGlobSetting();
  const { bpmUrl } = globSetting;
  const route = useRoute();
  const router = useRouter();
  const { formInstanceId: formId, processId: proceId, look } = route.query;

  //自定义表单字段
  const formSchemas: FormSchema[] = [
    {
      label: '事件信息',
      field: 'divisionLine',
      component: 'Divider',
      componentProps: {
        //是否虚线
        dashed: false,
        //分割线标题的位置（left | right | center）
        orientation: 'left',
        //文字是否显示为普通正文样式
        plain: true,
        //水平还是垂直类型（horizontal | vertical）
        type: 'horizontal',
      },
    },
    {
      label: '厂区',
      field: 'factory',
      component: 'FactoryId',
      colProps: { span: 12 },
      componentProps: {
        disabled: true,
      },
    },
    {
      label: '位置/岗位',
      field: 'positionPost',
      component: 'Input',
      colProps: { span: 12 },
      componentProps: {
        disabled: true,
      },
    },
    {
      label: '镜头名称',
      field: 'lensName',
      component: 'Input',
      colProps: { span: 12 },
      componentProps: {
        disabled: true,
      },
    },
    {
      label: '方式途径',
      field: 'waysMeans',
      component: 'Input',
      colProps: { span: 12 },
      componentProps: {
        disabled: true,
      },
    },
    {
      label: '验证时间',
      field: 'date',
      component: 'RangePicker',
      componentProps: {
        showTime: true,
        //日期格式化
        format: 'YYYY/MM/DD HH:mm',
        valueType: 'Date',
        disabled: true,
      },
      colProps: { span: 12 },
    },
    {
      label: '异常事件类型',
      field: 'abnormalType',
      component: 'ApiSelect',
      colProps: { span: 12 },
      componentProps: {
        api: (params) => fieldTree(params),
        labelField: 'fieldName',
        valueField: 'fieldName',
        params: { fieldType: 'inspect_006_ycsjlx_0', topFlag: false },
        disabled: true,
      },
    },
    {
      label: '异常事件描述',
      field: 'abnormalDesc',
      component: 'InputTextArea',
      colProps: { span: 12 },
      componentProps: {
        disabled: true,
      },
    },

    {
      label: '异常事件接收人',
      field: 'abnormalCreator',
      component: 'SearchPerson',
      colProps: { span: 12 },
      componentProps: {
        disabled: true,
      },
    },
  ];
  const [registerForm, { setFieldsValue: setBaseFormValue }] = useForm({
    //注册表单列
    schemas: formSchemas,
    //回车提交
    autoSubmitOnEnter: true,
    //不显示重置按钮
    showResetButton: false,
    // 不显示提交按钮
    showSubmitButton: false,
    //自定义提交按钮文本和图标
    submitButtonOptions: { text: '提交', preIcon: '' },
    //查询列占比 24代表一行 取值范围 0-24
    actionColOptions: { span: 17 },
    fieldMapToTime: [['date', ['checkTimeStart', 'checkTimeEnd'], 'YYYY-MM-DD']],
  });
  const cacheCheckUsers = ref<any[]>([]);
  const approveForm: FormSchema[] = [
    {
      label: '验证对象',
      field: 'checkName',
      colProps: { span: 12 },
      component: 'Input',
      ifShow: () => {
        return look == '1' || disableDic.value['checkUserno'] || processEnd.value;
      },
      dynamicDisabled: () => look == '1' || disableDic.value['checkUserno'] || processEnd.value,
    },
    {
      label: '验证对象',
      field: 'checkUserno',
      component: 'SearchPerson',
      colProps: { span: 12 },
      componentProps: {
        onSelect(val, option) {
          const isExit = cacheCheckUsers.value.find((item) => item.value == val);
          if (!isExit) {
            cacheCheckUsers.value.push(option);
          }
          setFieldsValue({ checkName: option.orgName });
        },
        mode: 'multiple',
      },
      dynamicDisabled: () => look == '1' || disableDic.value['checkUserno'] || processEnd.value,
      ifShow: () => {
        return !(look == '1' || disableDic.value['checkUserno'] || processEnd.value);
      },
    },
    {
      label: '是否异常',
      field: 'checkException',
      component: 'RadioGroup',
      colProps: { span: 12 },
      componentProps: {
        options: [
          { label: '是', value: '是' },
          { label: '否', value: '否' },
        ],
      },
      dynamicDisabled: () => look == '1' || disableDic.value['checkException'] || processEnd.value,
    },
    {
      label: '是否考核兑现',
      field: 'examFulfill',
      component: 'RadioGroup',
      colProps: { span: 12 },
      componentProps: {
        options: [
          { label: '是', value: '是' },
          { label: '否', value: '否' },
        ],
      },
      dynamicDisabled: () => look == '1' || disableDic.value['examFulfill'] || processEnd.value,
    },
    {
      label: '整改处理结果',
      field: 'improveResult',
      component: 'InputTextArea',
      colProps: { span: 12 },
      componentProps: {
        row: 2,
      },
      dynamicDisabled: () => look == '1' || disableDic.value['improveResult'] || processEnd.value,
    },
    {
      label: '备注',
      field: 'remark',
      component: 'InputTextArea',
      colProps: { span: 12 },
      componentProps: {
        row: 2,
      },
      dynamicDisabled: () => look == '1' || disableDic.value['remark'] || processEnd.value,
    },
  ];
  // 流程是否结束
  const processEnd = ref(false);
  const [registerApprove, { setFieldsValue, setProps, validate }] = useForm({
    schemas: approveForm,
    //回车提交
    autoSubmitOnEnter: true,
    //不显示重置按钮
    showResetButton: false,
    // 不显示提交按钮
    showSubmitButton: false,
    //自定义提交按钮文本和图标
    submitButtonOptions: { text: '提交', preIcon: '' },
    //查询列占比 24代表一行 取值范围 0-24
    actionColOptions: { span: 17 },
  });
  const formInstanceId = ref(formId);
  const processId = ref(proceId);
  const iframe = ref(
    `${bpmUrl}?method=showProcess&sysId=SAFETY&modelId=monitor_post&templateFormId=safety_monitor_post_process&formInstanceId=${formInstanceId.value}&processId=${processId.value}`
  );
  const disableDic = ref({});
  // 获取字段权限
  const getFieldRules = (nodeId) => {
    getFieldRule({ formId: 'safety_monitor_post_process', flowId: processId.value, nodeId, id: formInstanceId.value }).then((res) => {
      const results = JSON.parse(res);
      const objRules = results.reduce((pre, cur) => {
        pre[underLine2CamelCase(cur['fieldCode'])] = cur.authType == '0' ? false : true;
        return pre;
      }, {});
      disableDic.value = objRules;
    });
  };
  const getBpmData = () => {
    return new Promise((resolve, reject) => {
      const flowIFrame = document.getElementById('bpmIframe')!;
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
  const reloadIframe = () => {
    const url = iframe.value;
    iframe.value = '';
    setTimeout(() => {
      iframe.value = url;
    }, 100);
  };
  async function handleSubmit() {
    const values = await validate();
    const checkUsernos = values.checkUserno.split(',');
    const filterUser = cacheCheckUsers.value.filter((item) => checkUsernos.includes(item.value));
    const checkDept = filterUser.map((item) => item.orgName).join();
    const checkName = filterUser.map((item) => item.empName).join();
    let fdParameterJson: any = '';
    if (iframe.value) {
      fdParameterJson = await getBpmData();
    }
    updateProcess({
      ...values,
      checkDept,
      checkName,
      title: `${values.submitName}发起的监控岗位上报流程`,
      creator: values.submitNo,
      flowId: processId.value,
      id: formInstanceId.value,
      processParam: {
        type: 'audit',
        formId: 'safety_monitor_post_process',
        modelId: 'monitor_post',
        subject: `${values.submitName}发起的监控岗位上报流程`,
        fdParameterJson: JSON.stringify({ 'sysWfBusinessForm.fdParameterJson': fdParameterJson }),
        flowId: processId.value,
      },
    }).then((res) => {
      location.reload();
      reloadIframe();
    });
  }
  const approveUserCheck = ref(false);
  const getFormInfo = async () => {
    // 获取详情回显表单
    const detailInfo = await getInfo(formId);
    const factory = Number(detailInfo.factory);
    const date = [detailInfo.checkTimeStart, detailInfo.checkTimeEnd];
    setBaseFormValue({ ...detailInfo, factory, date });
    let curNode = [];
    if (processId.value) {
      // 获取当前节点
      const currentNode = await getNode({ formId: 'safety_monitor_post_process', flowId: processId.value, id: formInstanceId.value });
      curNode = JSON.parse(currentNode);
    }
    console.log(curNode[0], 'curNode');
    console.log(userStore.getUserInfo, 'userStroe');

    // 如果数据为[] 当前审批已完成
    if (curNode.length == 0) {
      processEnd.value = true;
      setProps({ showActionButtonGroup: false });
      // 如果审批已完成则回填审批表单
      setFieldsValue(detailInfo);
    } else {
      approveUserCheck.value = curNode[0].workitems.some((item) => item.expecter == userStore.getUserInfo.userName);
      console.log(approveUserCheck.value, 'approveUserCheck.value');

      const [{ nodeId }] = curNode;
      // 获取字段编辑权限
      getFieldRules(nodeId);
    }
  };
  if (formId) {
    getFormInfo();
  }
  const circularizeVisible = ref(false);
  const circularizeLoading = ref(false);

  const submitCircularize = async () => {
    if (!toUser.value) {
      message.info('请选择传阅人');
      return;
    }
    circularizeLoading.value = true;
    try {
      await circulate({
        fromUser: userStore.getUserInfo.email ? userStore.getUserInfo.email.split('@')[0] : '',
        toUser: toUser.value,
        flowId: proceId,
      });
      circularizeVisible.value = false;
      circularizeLoading.value = false;
      message.success('操作成功');
    } catch (error) {
      circularizeLoading.value = false;
    }
  };
  const toUser = ref();
  const personChange = (val, option) => {
    console.log(val, option);
    const toUserList = option.map((item) => (item.email ? item.email.split('@')[0] : ''));
    toUser.value = toUserList.join(',');
    console.log(toUser.value);
  };
  if (!route.query.processId && route.query.formInstanceId) {
    getProcessId({ formInstanceId: route.query.formInstanceId, type: route.query.templateFormId }).then((res) => {
      console.log(res, 'proces');
      if (res) {
        router.replace({
          name: 'monitorPostApproval',
          query: {
            ...route.query,
            processId: res,
          },
        });
      }
    });
  }
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
    bottom: 0;
    left: 0;
    width: 100%;
    padding: 5px 0;
    background-color: #fff;
    display: flex;
    justify-content: center;
  }
  .container {
    background-color: #fff;
    width: 100%;
    max-width: 100%;
    position: relative;
  }
  h1 {
    font-size: 18px;
    padding-left: 50px;
    font-weight: 600;
    padding-top: 30px;
  }
  :deep(.ant-divider-plain.ant-divider-with-text) {
    font-size: 16px;
    font-weight: 400;
  }
  iframe {
    width: 100%;
    height: 600px;
  }
  :deep(.top-form > .ant-row > .ant-col:last-child) {
    display: none !important;
  }
</style>
