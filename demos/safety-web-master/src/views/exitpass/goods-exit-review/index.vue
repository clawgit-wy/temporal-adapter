<template>
  <div class="container" v-if="!loading">
    <h1 style="text-align: center">出门证业务流程审批</h1>
    <div style="text-align: right">
      <a-space>
        <a-button v-if="isShowReSubmit" type="primary" @click="reSubmit">重新提交</a-button>
        <a-button type="primary" @click="circularizeVisible = true">传阅</a-button>
      </a-space>
    </div>
    <br />
    <commonApprove :info="info"></commonApprove>
    <iframe v-if="getbpmUrl()" :src="getbpmUrl()" id="bpmIframe" frameborder="0" style="height: 1350px"></iframe>
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
          <!-- <SearchPerson mode="multiple" style="width: 500px" @change="personChange"></SearchPerson> -->
          <person-select-input v-model:personList="toUserList" placeholder="请选择" :multiple="true" @confirm="personChange" />
        </li>
      </ul>
    </a-modal>
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
  import SearchPerson from '/@/components/Form/src/components/SearchPerson.vue';
  import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';

  import { view, fieldTree, getApproverList, modifyCurDealHandler, circulate, getProcessId, getProcessCurDealHandler } from './field.api';
  import commonApprove from './childForm/commonApprove.vue';
  const globSetting = useGlobSetting();
  const userStore = useUserStore().getUserInfo;

  const { bpmUrl } = globSetting;

  const route = useRoute();
  const router = useRouter();
  const { formInstanceId: formId } = route.query;
  const processId = route.query.processId as string;
  const info = ref<any>({});
  const outTypeList = ref([]);

  // 审批记录

  // 审批记录

  const getFormInfo = async () => {
    if (processId) {
      const data = await getProcessCurDealHandler({ id: formId, flowId: processId });
      info.value = data;
    }
    // 获取详情回显表单
    const detailInfo = await view({ processId: formId });
    info.value = { ...detailInfo, ...info.value, createTime: dayjs(detailInfo.createTime).format('YYYY-MM-DD HH:mm:ss') };
  };
  if (formId) {
    getFormInfo();
  }

  const getbpmUrl = () => {
    const query = location.href.split('?')[1];
    console.log('a87777777777777==', query, bpmUrl);
    if (!query || !query.includes('processId')) {
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
      toUserList.value = [];
      message.success('操作成功');
    } catch (error) {
      circularizeLoading.value = false;
    }
  };
  const toUser = ref();
  const toUserList = ref<any[]>([]);
  const personChange = () => {
    console.log(toUserList.value);
    const list = toUserList?.value.map((item) => (item.email ? item.email.split('@')[0] : '')) || [];
    toUser.value = list?.join(',') || '';
  };
  if (!processId && formId) {
    getProcessId({ formInstanceId: formId, type: 'safety_goods_out_process' }).then((res) => {
      console.log(res, 'proces');
      if (res) {
        router.replace({
          name: 'goodsexitreview',
          query: {
            ...route.query,
            processId: res,
          },
        });
      }
    });
  }
  const isShowReSubmit = computed(() => {
    return info.value.submitNo == userStore.workNo;
  });
  // 重新提交
  const reSubmit = async () => {
    router.push({
      name: 'goodsexitpermit',
      query: {
        formInstanceId: formId,
        type: 'reSubmit',
      },
    });
  };
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
