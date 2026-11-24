<template>
  <div class="container" v-if="!loading">
    <h1 style="text-align: center">楼宇门禁责任部门调整流程</h1>

    <br />
    <commonApprove :info="info" :outFactoryIdMap="outFactoryIdMap" :outTypeFlagMap="outTypeFlagMap"></commonApprove>
    <iframe v-if="showIframe" :src="iframeUrl" style="height: 1350px" id="bpmIframe"></iframe>
  </div>
</template>
<script setup lang="ts">
  import dayjs from 'dayjs';
  import { ref, computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { message } from 'ant-design-vue';
  import '/@/assets/bpm/domain';
  import { useGlobSetting } from '/@/hooks/setting';
  import { useUserStore } from '/@/store/modules/user';
  import { getDetailById,getApproverList } from './field.api';
  import commonApprove from './childForm/commonApprove.vue';
  const globSetting = useGlobSetting();
  const userStore = useUserStore().getUserInfo;

  const { bpmUrl } = globSetting;

  const route = useRoute();
  const router = useRouter();
  const { formInstanceId, processId } = route.query;
  const info = ref<any>({});
  const outTypeList = ref([]);

  const getFormInfo = async () => {
    if (processId) {
      // await modifyCurDealHandler({ id: formId, flowId: processId });
    }
    const approverList = await getApproverList({id: formInstanceId, flowId: processId, formId: 'safety_build_manage_process'})
    const approverListData = JSON.parse(approverList) || []

    // 获取详情回显表单
    const detailInfo = await getDetailById({ id: formInstanceId });
    detailInfo.curNodeHandler = approverListData.map(item => item.LoginName).join(',') || ''
    info.value = { ...detailInfo, approverList: approverListData };

    console.log(detailInfo, 'detailInfo');
  };

  if (formInstanceId) {
    getFormInfo();
  }

  const queryFormInstanceId = ref(formInstanceId);
  const queryProcessId = ref(processId);
  const iframeUrl = ref(
    `${bpmUrl}?method=showProcess&sysId=SAFETY&modelId=monitor_post&templateFormId=safety_monitor_post_process&formInstanceId=${queryFormInstanceId.value}&processId=${queryProcessId.value}`
  );
  const showIframe = computed(() => queryFormInstanceId.value && queryProcessId.value);
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
