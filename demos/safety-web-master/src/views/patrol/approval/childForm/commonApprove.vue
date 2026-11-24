<template>
  <div>
    <a-card title="事件信息">
      <template #extra><span @click="showHistory = true" style="color: blue; cursor: pointer">历史记录</span></template>
      <BasicForm class="top-form" @register="registerEventForm"></BasicForm>
    </a-card>
    <br />
    <a-card title="处理记录" v-if="dataSource.length > 0">
      <a-table :dataSource="dataSource" :columns="columns" size="small" :pagination="false">
        <template #bodyCell="{ column, text }">
          <template v-if="column.dataIndex === 'dealImage'">
            <Image v-for="item in text.split(',')" :key="item" :width="40" :src="item" />
          </template>
        </template>
      </a-table>
    </a-card>
    <br v-if="dataSource.length > 0" />
    <a-card title="处理结果">
      <BasicForm class="top-form" @register="registersubForm" v-if="props.info.isException != 0">
        <template #resultDesc="{ model, field }">
          <a-input :disabled="info.curNodeId == 'N3' || info.status == 30" v-model:value="model[field]" placeholder="请输入得分原因" />
          <span style="color: #bbb">提示：请综合评估该事件处理方式的合理性及应用范围、事件处理的及时性，进行评分。</span>
        </template>
      </BasicForm>
    </a-card>
    <div class="change-box" v-if="props.info.status != 30 && showChange">
      <a-button class="btn" type="primary" @click="submitApprove" v-if="props.info.curNodeId == 'N7' || props.info.curNodeId == 'N9'">提交</a-button>
      <a-button class="btn" type="primary" v-if="props.info.curNodeId != 'N9' && props.info.isDealTransfer != 1" @click="transfer">转派</a-button>
      <a-button class="btn" type="primary" v-if="props.info.curNodeId == 'N14'" @click="submitApprove">已处理</a-button>
    </div>
    <a-modal v-model:visible="transFerVisible" title="转派人员选择" @ok="transferOk">
      <div style="padding: 20px 10px">
        <a-row>
          <a-col :span="3">
            <span style="line-height: 32px">转派至：</span>
          </a-col>
          <a-col :span="18">
            <a-select
              v-model:value="transferData.dutyPersonNo"
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
              <template #option="{ value: val, label }"> &nbsp;&nbsp;{{ label }} （{{ val }}） </template>
            </a-select>
          </a-col>
        </a-row>
      </div>
    </a-modal>

    <HistoryModal :id="props.info.id" v-if="showHistory" @hide="showHistory = false" />
  </div>
</template>
<script setup lang="ts">
  import { useForm, BasicForm, FormSchema } from '/@/components/Form';
  import { computed, createVNode, ref, watchEffect } from 'vue';
  import { fieldTree, employeeList, updateAndAudit, updateTransfer, modifyCurDealHandler, getDealHistoryList } from '../field.api';
  import { Modal, message, Image } from 'ant-design-vue';
  import { ExclamationCircleOutlined } from '@ant-design/icons-vue';
  import { useUserStore } from '/@/store/modules/user';
  import { useRouter, useRoute } from 'vue-router';
  import HistoryModal from './historyModal.vue';
  import dayjs from 'dayjs';
  import { useTabs } from '/@/hooks/web/useTabs';
  const { closeCurrent } = useTabs();
  const router = useRouter();
  const route = useRoute();
  const userStore: any = useUserStore().getUserInfo;
  const props = defineProps({
    info: {
      type: Object,
      default: () => {},
    },
    eventList: {
      type: Array,
      default: () => [],
    },
  });
  const showHistory = ref(false);
  const discretionFlag = ref(false);
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
  const nodeStep = ref(-1);

  const [registerEventForm, { setFieldsValue: setEventField, setProps: setEventProps, updateSchema, getFieldsValue: getEventFieldValue }] = useForm({
    //注册表单列
    schemas: [
      { label: '事件类型', field: 'eventSource', component: 'Input', ifShow: false },
      {
        label: '任务名称',
        field: 'planName',
        component: 'Input',
        colProps,
        ifShow: ({ values }) => {
          console.log(values);

          return values.eventSource == 1;
        },
      },
      {
        label: '事件发生时间',
        field: 'occurTime',
        component: 'DatePicker',
        colProps,
        componentProps: {
          format: 'YYYY-MM-DD HH:mm:ss',
          valueFormat: 'YYYY-MM-DD HH:mm:ss',
        },
      },
      { label: '厂区', field: 'factory', component: 'Input', show: false },
      { label: '厂区', field: 'factoryId', component: 'FactoryId', labelField: 'factory', colProps },
      { label: '事件类型', field: 'eventId', labelField: 'eventType', component: 'Select', colProps, componentProps: {} },
      { label: '事项', field: 'teamId', labelField: 'teamName', component: 'Select', colProps, componentProps: {} },
      {
        label: '事件等级',
        field: 'eventLevel',
        component: 'Select',
        colProps,
        componentProps: {
          options: [
            { label: '一般隐患/违规', value: 'EVENT_LEVEL_01' },
            { label: '较大隐患/违规', value: 'EVENT_LEVEL_02' },
            { label: '重大隐患/违规', value: 'EVENT_LEVEL_03' },
          ],
        },
      },
      { label: '责任部门', field: 'dutyDept', component: 'Input', colProps, ifShow: ({ values }) => values.isException != 0 },
      { label: '地理位置', field: 'eventAddress', component: 'Input', colProps, ifShow: ({ values }) => values.isException != 0, componentProps: {} },
      {
        label: '是否重点部位',
        field: 'isKeynote',
        component: 'Select',
        colProps,
        ifShow: ({ values }) => values.isException != 0,
        componentProps: {
          options: [
            { label: '是', value: 1 },
            { label: '否', value: 0 },
          ],
        },
      },
      {
        label: '问题描述',
        field: 'problemDesc',
        component: 'InputTextArea',
        componentProps: { maxlength: 260 },
        ifShow: ({ values }) => values.isException != 0,
        colProps,
      },
      {
        label: '是否当场处理',
        field: 'isScene',
        component: 'Select',
        ifShow: ({ values }) => values.isException != 0,
        colProps,
        componentProps: {
          options: [
            { label: '是', value: 1 },
            { label: '否', value: 0 },
          ],
        },
      },
      {
        label: '是否存在异常',
        field: 'isException',
        component: 'Select',
        colProps,
        componentProps: {
          options: [
            { label: '是', value: 1 },
            { label: '否', value: 0 },
          ],
        },
      },
      {
        label: '图片上传',
        field: 'problemImage',
        component: 'JImageUpload',
        ifShow: ({ values }) => values.isException != 0,
        colProps,
        componentProps: { fileMax: 9 },
      },
    ],
    disabled: true,
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
  const approveChildrenOne: FormSchema[] = [
    {
      label: '保卫联系人',
      field: 'guardPerson',
      component: 'Input',
      colProps,
      componentProps: { disabled: true },
      ifShow: ({ values }) => {
        return !(props.info.curNodeId == 'N7' && props.info.eventSource == 1);
      },
    },
    {
      label: '预计处理完成日期',
      labelWidth: 200,
      field: 'planDealTime',
      required: true,
      component: 'DatePicker',
      colProps,
      componentProps: {
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      label: '计划处理措施',
      field: 'planMeasure',
      required: true,
      component: 'InputTextArea',
      colProps,
      componentProps: {
        maxlength: 200,
        autoSize: {
          //最小显示行数
          minRows: 5,
          //最大显示行数
          maxRows: 5,
        },
      },
    },
  ];
  const approveChildrenTwo: FormSchema[] = [
    { label: '处理人', field: 'dealPerson', component: 'Input', colProps, componentProps: { disabled: true } },
    { label: '处理人员工号', field: 'dealPersonNo', component: 'Input', colProps, componentProps: { disabled: true }, show: false },
    {
      label: '处理完成日期',
      field: 'dealTime',
      required: true,
      component: 'DatePicker',
      colProps,
      componentProps: {
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    { label: '事件处理过程及结果', field: 'dealRecord', required: true, colProps, component: 'InputTextArea' },
    { label: '处理完成照片', field: 'dealImage', component: 'JImageUpload', colProps, componentProps: { fileMax: 9 } },
  ];
  const approveChildrenThree: FormSchema[] = [
    {
      label: '是否妥善处理完成',
      field: 'isFinish',
      required: true,
      colProps,
      component: 'RadioGroup',
      componentProps: {
        options: [
          { label: '是', value: 1 },
          { label: '否', value: 0 },
        ],
      },
    },
    {
      label: '事件处理结果及过程评分',
      required: true,
      labelWidth: '200px',
      field: 'resultScore',
      colProps,
      component: 'InputNumber',
      ifShow: ({ values }) => values.isFinish == 1,
      componentProps: { min: 1, max: 5, placeholder: '请填写1-5分之间的整数' },
    },
    {
      label: '得分原因',
      field: 'resultDesc',
      colProps,
      required: true,
      component: 'Input',
      ifShow: ({ values }) => values.isFinish == 1,
      slot: 'resultDesc',
    },
    { label: '描述情况', field: 'resultRemark', colProps, component: 'Input' },
  ];

  const [registersubForm, { setFieldsValue: setSubField, validate, updateSchema: updateSubSchema, setProps }] = useForm({
    //注册表单列
    schemas: [...approveChildrenOne, ...approveChildrenTwo, ...approveChildrenThree],
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
            options: res.map((item) => ({ label: item.fieldName, value: item.id })),
          },
        },
      ]);
    });
  };
  const submitApprove = async () => {
    const { nodeFlag, eventSource, curNodeId } = props.info;
    const formData = await validate();
    const { email, realName, workNo } = userStore;
    const dutyPerson = realName;
    const dutyPersonNo = email ? email.split('@')[0] : '';
    if (curNodeId == 'N7') {
      const { planDealTime, planMeasure } = formData;

      if (eventSource == 1) {
        fetchSubmit({
          planDealTime,
          planMeasure,
          dutyPerson,
          dutyPersonNo,
          nodeFlag: 2,
          guardPhone: userStore.phone,
          guardPerson: userStore.realName,
          guardPersonNo: userStore.email.split('@')[0],
        });
      } else {
        fetchSubmit({
          planDealTime,
          planMeasure,
          dutyPerson,
          dutyPersonNo,
          nodeFlag: 2,
        });
      }

      // this.submitOne()
    }
    if (curNodeId == 'N14') {
      const { dealTime, dealRecord, dealImage } = formData;

      fetchSubmit({
        dealTime,
        dealPerson: realName,
        dealPersonNo: workNo,
        dealRecord,
        dealImage,
        nodeFlag: 3,
      });
    }
    if (curNodeId == 'N9') {
      const { isFinish, resultScore, resultDesc, resultRemark } = formData;
      if (isFinish == 1) {
        if (!resultScore) return message.error('请填写处理结果及评分');
        if (!resultDesc) return message.error('请填写得分原因');
        if (resultScore < 1 || resultScore > 5) return message.error('请填写1-5之间评分');
      } else {
        if (!resultRemark) return message.error('请填写描述情况');
      }
      fetchSubmit({
        resultPerson: realName,
        resultPersonNo: workNo,
        isFinish,
        resultScore,
        resultRemark,
        resultDesc,
        nodeFlag: isFinish == 1 ? -1 : 1,
      });
    }
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
  // 更新审批人
  const updateApprovePerson = () => {
    modifyCurDealHandler({ id: props.info.id, flowId: props.info.flowId }).then(() => {
      transFerVisible.value = false;
      message.success('操作成功');
      // router.push({
      //   name: 'serviceData',
      // });
      goList();
    });
  };
  //进入列表页面
  const goList = () => {
    // location.reload();
    closeCurrent();
    router.push({
      name: 'Welcome',
    });
  };
  // 审批接口
  const fetchSubmit = async (formData) => {
    const res = await updateAndAudit({
      ...formData,
      ...approveBaseParams(),
    });
    console.log('提交信息===', res);
    if (res == 'T') {
      updateApprovePerson();
    } else {
      message.error('服务端错误');
    }
  };

  const transFerVisible = ref(false);
  const transferData = ref({
    dutyPerson: '',
    dutyPersonNo: '',
    nodeFlag: 1,
    // 保卫联系人
    guardPhone: userStore.phone,
    guardPerson: userStore.realName,
    guardPersonNo: userStore.email.split('@')[0],
  });
  // 转派相关
  const personChange = (data) => {
    console.log(data);
    const selectPerson = personList.value.find((item) => item.value == data);
    if (selectPerson) {
      transferData.value.dutyPerson = selectPerson.empName;
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
        let params: any = {
          ...approveBaseParams(),
          dutyPerson: transferData.value.dutyPerson,
          dutyPersonNo: transferData.value.dutyPersonNo,
          isDealTransfer: props.info.nodeFlag == 2 ? 1 : 2,
        };
        if (props.info.nodeFlag == 1 && props.info.eventSource == 1) {
          params = {
            ...params,
            guardPhone: userStore.phone,
            guardPerson: userStore.realName,
            guardPersonNo: userStore.email.split('@')[0],
          };
        }
        updateTransfer(params).then((res) => {
          console.log(res);
          if (res == 'T') {
            transFerVisible.value = false;
            // router.push({
            //   name: 'serviceData',
            // });
            goList();
          } else {
            message.error('服务端错误');
          }
        });
      },
    });
    // transFerVisible.value = false
  };
  watchEffect(() => {
    if (Object.keys(props.info).length > 0) {
      const {
        dutyPerson,
        planDealTime,
        planMeasure,
        dealTime,
        dealRecord,
        dealImage,
        isFinish,
        resultScore,
        resultDesc,
        resultRemark,
        eventType,
        eventLevel,
        dutyDept,
        eventAddress,
        isKeynote,
        problemDesc,
        problemImage,
        isScene,
        occurTime,
        factory,
        teamName,
        factoryId,
        eventId,
        teamId,
        eventSource,
        planName,
        nodeFlag,
        isDealTransfer,
        isTransfer,
        isException,
        guardPerson,
        dealPerson,
        dealPersonNo,
        curNodeId,
      } = props.info;
      if (curNodeId == 'N5' && isScene == 1) {
        updateSubSchema([
          { field: 'planDealTime', ifShow: false },
          { field: 'planMeasure', ifShow: false },
        ]);
      }
      if (curNodeId == 'N14') {
        updateSubSchema([
          { field: 'planDealTime', componentProps: { disabled: true, format: 'YYYY-MM-DD', valueFormat: 'YYYY-MM-DD' } },
          { field: 'planMeasure', componentProps: { disabled: true } },
          { field: 'isFinish', ifShow: false },
          { field: 'resultScore', ifShow: false },
          { field: 'resultRemark', ifShow: false },
          { field: 'resultDesc', ifShow: false },
        ]);
      } else if (curNodeId == 'N9' && isTransfer != 1 && isDealTransfer != 1 && isDealTransfer != 2) {
        updateSubSchema([
          { field: 'planDealTime', componentProps: { disabled: true } },
          { field: 'planMeasure', componentProps: { disabled: true } },
          { field: 'dealTime', componentProps: { disabled: true } },
          { field: 'dealRecord', componentProps: { disabled: true } },
          { field: 'dealImage', componentProps: { disabled: true } },
          { field: 'isFinish', ifShow: false },
          { field: 'resultScore', ifShow: false },
          { field: 'resultRemark', ifShow: false },
          { field: 'resultDesc', ifShow: false },
        ]);
        if (isScene == 1) {
          updateSubSchema([
            { field: 'planDealTime', ifShow: false },
            { field: 'planMeasure', ifShow: false },
          ]);
        }
      } else if (curNodeId == 'N9' && (isTransfer == 1 || isDealTransfer == 1 || isDealTransfer == 2)) {
        console.log(33333333333333);

        updateSubSchema([
          { field: 'planDealTime', componentProps: { disabled: true } },
          { field: 'planMeasure', componentProps: { disabled: true } },
          { field: 'dealTime', componentProps: { disabled: true } },
          { field: 'dealRecord', componentProps: { disabled: true } },
          { field: 'dealImage', componentProps: { disabled: true } },
        ]);
      } else if (curNodeId == 'N3') {
        if (isException != 0 && (isScene == 1 || !planDealTime)) {
          updateSubSchema([
            // {field: 'dutyPerson', ifShow: false,},
            { field: 'planDealTime', ifShow: false },
            { field: 'planMeasure', ifShow: false },

            { field: 'isFinish', ifShow: false },
            { field: 'resultScore', ifShow: false },
            { field: 'resultRemark', ifShow: false },
            { field: 'resultDesc', ifShow: false },
          ]);
        }

        setProps({
          disabled: true,
        });
      } else {
        updateSubSchema([
          { field: 'dealTime', ifShow: false },
          { field: 'dealRecord', ifShow: false },
          { field: 'dealImage', ifShow: false },
          { field: 'dealPerson', ifShow: false },
          { field: 'isFinish', ifShow: false },
          { field: 'resultScore', ifShow: false },
          { field: 'resultRemark', ifShow: false },
          { field: 'resultDesc', ifShow: false },
        ]);
      }
      setTimeout(() => {
        setEventField({
          planName,
          eventSource,
          eventType,
          eventLevel,
          dutyDept,
          eventAddress,
          isKeynote,
          problemDesc,
          problemImage: problemImage ? problemImage.split(',') : '',
          isScene,
          occurTime,
          factory,
          factoryId,
          eventId,
          teamId,
          teamName,
          isException,
        });
        if (eventId) {
          getTeamOptions(eventId);
        }
        setSubField({
          guardPerson,
          dutyPerson,
          planDealTime: planDealTime ? dayjs(planDealTime, 'YYYY-MM-DD') : '',
          planMeasure,
          dealTime: dealTime ? dayjs(dealTime, 'YYYY-MM-DD') : '',
          dealRecord,
          dealImage,
          isFinish,
          resultScore,
          resultDesc,
          resultRemark,
          dealPerson,
          dealPersonNo,
        });
        console.log(nodeFlag, 'nodeFlag');
      });
    }
  });
  watchEffect(() => {
    if (props.eventList.length > 0) {
      updateSchema([
        {
          field: 'eventId',
          componentProps: {
            options: props.eventList,
          },
        },
      ]);
    }
  });
  const columns = [
    { title: '处理人', dataIndex: 'dealPerson', key: 'dealPerson' },
    { title: '处理人员工号', dataIndex: 'dealPersonNo', key: 'dealPersonNo' },
    { title: '问题描述', dataIndex: 'dealRecord', key: 'dealRecord' },
    { title: '处理日期', dataIndex: 'dealTime', key: 'dealTime' },
    { title: '处理图片', dataIndex: 'dealImage', key: 'dealImage' },
    { title: '处理结果', dataIndex: 'isFinish', key: 'isFinish', customRender: ({ text }) => (!text ? '未通过' : '通过') },
    { title: '情况描述', dataIndex: 'resultRemark', key: 'resultRemark' },
    { title: '评价人', dataIndex: 'resultPerson', key: 'resultPerson' },
  ];
  const dataSource = ref([]);
  const getHistory = async () => {
    const res = await getDealHistoryList({ processId: route.query.formInstanceId });
    console.log(res, 'history');
    dataSource.value = res;
  };
  if (route.query.formInstanceId) {
    getHistory();
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
  :deep(.ant-input-number) {
    width: 100%;
  }
</style>
