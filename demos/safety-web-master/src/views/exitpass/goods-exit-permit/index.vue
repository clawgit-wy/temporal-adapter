<template>
  <div class="container" v-if="!loading">
    <h1 style="text-align: center">出门证业务流程审批</h1>
    <div style="text-align: right">
      <a-button type="primary" @click="circularizeVisible = true">传阅</a-button>
    </div>
    <br />
    <commonApprove :info="info" :outFactoryIdMap="outFactoryIdMap" :outTypeFlagMap="outTypeFlagMap"></commonApprove>
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
  import dayjs from 'dayjs';
  import { ref,computed } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { message } from 'ant-design-vue';
  import '/@/assets/bpm/domain';
  import { useGlobSetting } from '/@/hooks/setting';
  import { useUserStore } from '/@/store/modules/user';
  import SearchPerson from '/@/components/Form/src/components/SearchPerson.vue';

  import { view, fieldTree, getApproverList, modifyCurDealHandler, circulate, getProcessId } from './field.api';
  import commonApprove from './childForm/commonApprove.vue';
  const globSetting = useGlobSetting();
  const userStore = useUserStore().getUserInfo;

  const { bpmUrl } = globSetting;

  const route = useRoute();
  const router = useRouter();
  const { formInstanceId: formId, processId } = route.query;
  const info = ref<any>({});
  const outTypeList = ref([]);

  // 审批记录

  // 审批记录
  const outFactoryIdMap = ref({});
  const outTypeFlagMap = ref({});
  // 是否重新提交
  const isReSubmit = computed(() => {
    return route.query.type === 'reSubmit';
  });
  const getFormInfo = async () => {
    if (processId) {
      await modifyCurDealHandler({ id: formId, flowId: processId });
    }
    // 获取详情回显表单
    const detailInfo = await view({ processId: formId });
    if(isReSubmit.value)
    {

      detailInfo.subject = dayjs().format('YYYY年MM月DD日') + detailInfo.subject.substring(detailInfo.subject.indexOf('-'));
    }
    detailInfo.receiveList = detailInfo.receiveList.map(item => ({targetFactory: item.targetFactory,receiveEmpNo: item.receiveEmpNo,receiveEmp: item.receiveEmp,receiveEmpDept: item.receiveEmpDept,receiveEmpEip: item.receiveEmpEip}));
    info.value = { ...detailInfo, createTime: dayjs().format('YYYY-MM-DD HH:mm:ss') ,goodsList: JSON.stringify(detailInfo.goodsList),receiveList: JSON.stringify(detailInfo.receiveList),vehicleList: JSON.stringify(detailInfo.vehicleList)};

    console.log(detailInfo, 'detailInfo');
  };
  if (formId) {
    getFormInfo();
  }

  const initData = async () => {
    const outFactoryList = await fieldTree({ fieldType: 'factory', topFlag: false });
    console.log(outFactoryList, 'outFactoryList');
    if (outFactoryList && Array.isArray(outFactoryList)) {
      outFactoryIdMap.value = outFactoryList.reduce((pre, cur) => {
        pre[cur.id] = cur.fieldName;
        return pre;
      }, {});
    }
    const outTypeList = await fieldTree({ fieldType: 'goodsOut_dict_0_type', topFlag: false });
    console.log(outTypeList, 'outFactoryList');
    if (outTypeList && Array.isArray(outTypeList)) {
      outTypeFlagMap.value = outTypeList.reduce((pre, cur) => {
        pre[cur.fieldName] = cur.id;
        return pre;
      }, {});
    }
  };
  initData();
  const getbpmUrl = () => {
    const query = location.href.split('?')[1];
    console.log(query, bpmUrl);
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
  if (!processId && formId && !isReSubmit.value) {
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
