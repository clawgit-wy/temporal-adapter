import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '/@/store/modules/user';
import { useGlobSetting } from '/@/hooks/setting';
import { getProcessCurDealHandler } from './form.api';
import '/@/assets/bpm/domain';

const globSetting = useGlobSetting();

const { bpmUrl } = globSetting;
export function useBpm(detailInfo, isEdit) {
  const isCanApprove = ref(false);

  const route = useRoute();
  const userInfo = useUserStore().getUserInfo;
  // const iframeUrl = ref(
  //   `${bpmUrl}?method=showProcess&sysId=SAFETY&modelId=monitor_post&templateFormId=safety_monitor_post_process&formInstanceId=${route.query.id}&processId=${route.query.bpmFlowId}`
  // );

  //是否显示bpm流程
  const isShowBpm = computed(() => {
    if (isEdit.value) {
      return false;
    }
    return Boolean(route.query.bpmFlowId || detailInfo.value?.bpmFlowId);
  });
  const bpmId = computed(() => {
    return route.query.bpmFlowId;
  });
  const iframeUrl = computed(() => {
    let bpmFlowId = route.query?.bpmFlowId ? route.query?.bpmFlowId : route.query?.processId;

    if (!bpmFlowId) {
      bpmFlowId = detailInfo.value?.bpmFlowId;
    }
    if (route.query && route.query.formInstanceId && bpmFlowId) {
      const url = `${bpmUrl}?method=showProcess&formInstanceId=${route.query.formInstanceId}&processId=${bpmFlowId}`;
      return url;
    }
    return '';
  });
  //获取bpm数据
  const getBpmData = () => {
    return new Promise((resolve, reject) => {
      const flowIFrame = document.getElementById('bpmIframe');
      const domain = window?.domain || '';
      if (domain) {
        domain.call(flowIFrame.contentWindow, 'lbpm_getApprovalData', [], function (data) {
          const res = JSON.parse(data);
          const result = res['sysWfBusinessForm.fdParameterJson'];
          if (result) {
            resolve(JSON.parse(result));
          }
          reject('获取iframe数据失败');
        });
      } else {
        reject();
      }
    });
  };
  // const isCanApprove = computed(() => {
  //   if (isEdit.value) {
  //     return false;
  //   }
  //   if (detailInfo.value?.status == '00' || detailInfo.value?.status == '30') {
  //     //废弃 或者 结束都不显示审批
  //     return false;
  //   }
  //   if (userInfo?.email && detailInfo.value?.curNodeHandler) {
  //     const eip = userInfo?.email.split('@')[0];
  //     console.log(eip, detailInfo.value.curNodeHandler.includes(eip), '[[[[[[[[[[[]]]]]]]]]]]');
  //     return detailInfo.value.curNodeHandler.includes(eip);
  //   } else {
  //     return false;
  //   }
  // });
  async function checkCanApprove(bpmFlowId) {
    if (isEdit.value) {
      return false;
    }
    if (detailInfo.value?.status == '00' || detailInfo.value?.status == '30') {
      //废弃 或者 结束都不显示审批
      return false;
    }
    const res = await getProcessCurDealHandler(bpmFlowId);
    // 使用some方法简化遍历判断逻辑
    if (Array.isArray(res) && userInfo.userName) {
      if (res.some((item) => item?.fdCurrLoginNames?.includes(userInfo.userName))) {
        debugger;
        isCanApprove.value = true;
        return true;
      }
    }

    return false;
  }

  return {
    isShowBpm,
    iframeUrl,
    bpmId,
    isCanApprove,
    checkCanApprove,
    getBpmData,
  };
}
