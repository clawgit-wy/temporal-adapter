<template>
  <div class="container" v-if="!loading">
    <h1 style="text-align: center">治安巡查业务流程审批</h1>
    <div style="display: flex; justify-content: space-between">
      <span>事件编号：{{ info.flowNo }}</span>
      <a-button type="primary" @click="circularizeVisible = true">传阅</a-button>
    </div>
    <br />
    <eventReportingApprove
      v-if="info.eventSource == '2' && (info.curNodeId == 'N5' || info.nodeFlag == null)"
      :info="info"
      :eventList="eventList"
    ></eventReportingApprove>
    <commonApprove v-else :info="info" :eventList="eventList"></commonApprove>
    <iframe v-if="getbpmUrl()" :src="getbpmUrl()" style="height: 1350px"></iframe>
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
  import '/@/assets/bpm/domain';
  import { view, fieldTree, getApproverList, modifyCurDealHandler } from './field.api';
  import eventReportingApprove from './childForm/eventReportingApprove.vue';
  import commonApprove from './childForm/commonApprove.vue';
  import { useGlobSetting } from '/@/hooks/setting';
  import { useUserStore } from '/@/store/modules/user';
  import dayjs from 'dayjs';
  import { circulate, getProcessId } from '../../facilities/approval/field.api';
  import SearchPerson from '/@/components/Form/src/components/SearchPerson.vue';
  import { message } from 'ant-design-vue';
  const globSetting = useGlobSetting();
  const userStore = useUserStore().getUserInfo;
  const { bpmUrl } = globSetting;
  const route = useRoute();
  const router = useRouter();
  const { formInstanceId: formId, processId } = route.query;
  const info = ref<any>({});
  const eventList = ref([]);

  // 审批记录

  const getFormInfo = async () => {
    if (processId) {
      await modifyCurDealHandler({ id: formId, flowId: processId });
    }
    // 获取详情回显表单
    const detailInfo = await view({ id: formId });
    if (detailInfo.nodeFlag == 2) {
      detailInfo.dealTime = null;
      detailInfo.dealRecord = null;
      detailInfo.dealImage = '';
    }
    if (detailInfo.nodeFlag == 3) {
      detailInfo.isFinish = null;
      detailInfo.resultRemark = null;
    }
    info.value = { ...detailInfo, createTime: dayjs(detailInfo.createTime).format('YYYY-MM-DD HH:mm:ss') };

    console.log(detailInfo, 'detailInfo');

    const events = await fieldTree({ fieldType: 'inspection001_zaxclx_0', topFlag: false });
    eventList.value = events.map((item) => ({ label: item.fieldName, value: item.id }));
  };
  getFormInfo();
  const getbpmUrl = () => {
    const query = location.href.split('?')[1];
    console.log(query, bpmUrl);
    if (!query.includes('processId')) {
      return false;
    }
    if (query) {
      const url = `${bpmUrl}?method=showProcess&${query}`;
      console.log(url);
      return url;
    }
  };
  getbpmUrl();
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
        fromUser: userStore.email ? userStore.email.split('@')[0] : '',
        toUser: toUser.value,
        flowId: processId,
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
  if (!processId && formId) {
    getProcessId({ formInstanceId: formId, type: route.query.templateFormId }).then((res) => {
      console.log(res, 'proces');
      if (res) {
        router.replace({
          name: 'patrolApproval',
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
  p {
    margin: 0;
  }
  .container {
    background-color: #fff;
    width: 100%;
    max-width: 100%;
    padding: 0 50px;
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
    height: 3600px;
  }
  :deep(.top-form > .ant-row > .ant-col:last-child) {
    display: none !important;
  }
  .btn-box {
    position: fixed;
    width: 100%;
    background-color: #fff;
    bottom: 0;
    left: 0;
    padding: 5px 0;
  }
</style>
