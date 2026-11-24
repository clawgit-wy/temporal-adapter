<template>
  <div>
    <a-card title="基础信息">
      <BasicForm class="top-form" @register="registerForm" />
    </a-card>
    <br />
    <a-card title="事件信息">
      <template #extra><span @click="showHistory = true" style="color: blue; cursor: pointer">历史记录</span></template>
      <BasicForm class="top-form" @register="registerEventForm">
        <template #problemImage="{ model, field }">
          <Image v-for="item in model[field]" :width="100" :src="item"></Image>
        </template>
      </BasicForm>
    </a-card>
    <br />
    <a-card title="处理结果" v-if="discretionFlag">
      <BasicForm class="top-form" @register="registersubForm">
        <template #problemImage="{ model, field }">
          <Image v-for="item in model[field]" :width="100" :src="item"></Image>
        </template>
      </BasicForm>
    </a-card>
    <div class="change-box" v-if="showChange">
      <template v-if="!discretionFlag">
        <!-- <a-button class="btn" type="primary" @click="edit()">{{disabled ? '编辑' : '保存编辑'}}</a-button> -->
        <!-- <a-button class="btn" type="info" v-show="(!disabled)" @click="cancelEdit()">取消编辑</a-button> -->
        <a-button class="btn" type="primary" v-if="info.dutyDept == '司法保卫室'" v-show="disabled" @click="transfer">转派至相关责任人</a-button>
        <a-button class="btn" type="primary" v-else v-show="disabled" @click="transfer">发送相关部门处理</a-button>
        <a-button class="btn" type="primary" v-show="disabled" @click="discretionChange">已自行处理</a-button>
      </template>
      <a-button v-else type="primary" @click="discretionSubmit()">已处理</a-button>
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
  import { fieldTree, employeeList, updateAndAudit, modifyCurDealHandler } from '../field.api';
  import { Image, Modal, message } from 'ant-design-vue';
  import { ExclamationCircleOutlined } from '@ant-design/icons-vue';
  import { useUserStore } from '/@/store/modules/user';
  import { useRouter } from 'vue-router';
  import dayjs from 'dayjs';
  import HistoryModal from './historyModal.vue';

  const router = useRouter();
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
  const formSchemas: FormSchema[] = [
    { label: '姓名', field: 'submitName', component: 'Input', colProps, componentProps: { disabled: true } },
    { label: '用户名', field: 'submitNo', component: 'Input', colProps, componentProps: { disabled: true } },
    { label: '部门', field: 'submitDept', component: 'Input', colProps, componentProps: { disabled: true } },
    { label: '上报时间', field: 'createTime', component: 'Input', colProps, componentProps: { disabled: true, valueType: 'date' } },
  ];
  const [registerForm, { setFieldsValue }] = useForm({
    //注册表单列
    schemas: formSchemas,
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
  const eventLevelDic = {
    EVENT_LEVEL_01: '一般隐患/违规',
    EVENT_LEVEL_02: '较大隐患/违规',
    EVENT_LEVEL_03: '重大隐患/违规',
  };
  const disabled = ref(true);

  const [registerEventForm, { setFieldsValue: setEventField, setProps: setEventProps, updateSchema, getFieldsValue: getEventFieldValue }] = useForm({
    //注册表单列
    schemas: [
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
      { label: '责任部门', field: 'dutyDept', component: 'Input', colProps, componentProps: {} },
      { label: '地理位置', field: 'eventAddress', component: 'Input', colProps, componentProps: {} },
      {
        label: '是否重点部位',
        field: 'isKeynote',
        component: 'Select',
        colProps,
        componentProps: {
          options: [
            { label: '是', value: 1 },
            { label: '否', value: 0 },
          ],
        },
      },
      { label: '问题描述', field: 'problemDesc', component: 'InputTextArea', colProps, componentProps: { maxlength: 260 } },
      {
        label: '是否当场处理',
        field: 'isScene',
        component: 'Select',
        colProps,
        componentProps: {
          options: [
            { label: '是', value: 1 },
            { label: '否', value: 0 },
          ],
        },
      },
      { label: '图片上传', field: 'problemImage', component: 'JImageUpload', colProps, componentProps: { fileMax: 9 } },
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
  const [registersubForm, { setFieldsValue: setSubField, getFieldsValue, validate }] = useForm({
    //注册表单列
    schemas: [
      {
        label: '处理完成日期',
        labelWidth: 10,
        field: 'dealTime',
        required: true,
        component: 'DatePicker',
        colProps,
        componentProps: {
          format: 'YYYY-MM-DD HH:mm:ss',
          valueFormat: 'YYYY-MM-DD HH:mm:ss',
        },
      },
      { label: '处理人姓名', field: 'dealPerson', component: 'Input', colProps, show: false },
      { label: '处理人部门', field: 'dealPersonGroup', component: 'Input', colProps, show: false },
      {
        label: '处理人',
        field: 'dealPersonNo',
        required: true,
        labelField: 'dealPerson',
        component: 'SearchPerson',
        colProps,
        componentProps: {
          onChange(e, a) {
            setSubField({ dealPersonGroup: a.orgName });
          },
        },
      },
      { label: '事件处理过程及结果', field: 'dealRecord', required: true, component: 'InputTextArea', colProps },
      { label: '处理完成照片', field: 'dealImage', component: 'JImageUpload', colProps, componentProps: { fileMax: 9 } },
    ],
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
  watchEffect(() => {
    if (Object.keys(props.info).length > 0) {
      const {
        submitName,
        submitNo,
        submitDept,
        createTime,
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
      } = props.info;
      setTimeout(() => {
        setFieldsValue({
          submitName,
          submitNo,
          submitDept,
          createTime,
        });
        setEventField({
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
        });
        if (eventId) {
          getTeamOptions(eventId);
        }
      }, 0);
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
  const discretionChange = () => {
    discretionFlag.value = true;
    setTimeout(() => {
      const { realName, workNo } = userStore;
      setSubField({
        dealTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
        dealPerson: realName,
        dealPersonNo: workNo,
      });
    });
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
  const discretionSubmit = async () => {
    await validate();
    const { dealImage, dealTime, dealPerson, dealPersonNo, dealRecord } = getFieldsValue();
    const { realName, email, phone } = userStore;
    const dutyPersonNo = email ? email.split('@')[0] : '';
    const dutyPerson = realName;
    const formData = {
      ...approveBaseParams(),
      guardPerson: realName,
      guardPhone: phone,
      guardPersonNo: dutyPersonNo,
      dealImage,
      dealTime,
      dealPerson,
      dealPersonNo,
      dealRecord,
      dutyPersonNo,
      dutyPerson,
      nodeFlag: -1,
      isTransfer: 0,
    };
    fetchSubmit(formData);
  };
  // 更新审批人
  const updateApprovePerson = () => {
    modifyCurDealHandler({ id: props.info.id, flowId: props.info.flowId }).then(() => {
      transFerVisible.value = false;
      message.success('操作成功');
      router.push({
        name: 'serviceData',
      });
    });
  };
  const fetchSubmit = async (formData) => {
    try {
      const res = await updateAndAudit({ ...formData, ...approveBaseParams() });
      if (res == 'T') {
        updateApprovePerson();
      }
    } catch (error) {
      updateApprovePerson();
    }
  };
  const edit = () => {
    if (disabled.value == true) {
      disabled.value = false;
      setEventProps({
        disabled: false,
      });
    } else {
      disabled.value = true;
      setEventProps({
        disabled: true,
      });
      console.log(getEventFieldValue(), 'getEventFieldValue()');
    }
  };
  const cancelEdit = () => {};

  const transFerVisible = ref(false);
  const transferData = ref({
    dutyPerson: '',
    dutyPersonNo: '',
    transferDept: '',
    nodeFlag: 1,
    isTransfer: 1,
    // 保卫联系人
    guardPhone: userStore.phone,
    guardPerson: userStore.realName,
    guardPersonNo: userStore.email.split('@')[0],
  });
  // 转派相关
  const personChange = (data) => {
    console.log(data);
    const selectPerson = personList.value.find((item) => item.value == data);
    console.log('选中责任人====', selectPerson);
    if (selectPerson) {
      transferData.value.dutyPerson = selectPerson.empName;
      transferData.value.transferDept = selectPerson.orgNamePath;
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
        console.log('转派确认==', transferData.value);
        fetchSubmit({
          ...transferData.value,
        });
      },
    });
    // transFerVisible.value = false
  };
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
</style>
