import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '/@/store/modules/user';
import { useGlobSetting } from '/@/hooks/setting';
import { getProcessCurDealHandler } from './accessRequestForm.api';

const globSetting = useGlobSetting();

const { bpmUrl } = globSetting;
export function useBpm(props) {
  const route = useRoute();
  const userInfo = useUserStore().getUserInfo;
  const isCanApprove = ref(false)
  // const iframeUrl = ref(
  //   `${bpmUrl}?method=showProcess&sysId=SAFETY&modelId=monitor_post&templateFormId=safety_monitor_post_process&formInstanceId=${route.query.id}&processId=${route.query.bpmFlowId}`
  // );

  //是否显示bpm流程
  const isShowBpm = computed(() => {
    return route.query.bpmFlowId;
  })
  const bpmId = computed(() => {
    return route.query.bpmFlowId;
  })
  const iframeUrl = computed(() => {
    if (route.query && route.query.formInstanceId && route.query.bpmFlowId) {
      const url = `${bpmUrl}?method=showProcess&formInstanceId=${route.query.formInstanceId}&processId=${route.query.bpmFlowId}`
      return url
    }
    return ''
  })
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
  async function checkCanApprove(processId,status) {
    const res = await getProcessCurDealHandler(processId);
    console.log('当前的节点', res);
    if(status === '30' || res.curNodeId == 'N3' || status === '00'){
      isCanApprove.value = false;
      return false
    }
    // 使用some方法简化遍历判断逻辑
    if (Array.isArray(res) && userInfo.userName) {
      if (res.some(item => item?.fdCurrLoginNames?.includes(userInfo.userName))) {
        isCanApprove.value = true;
        return true;
      }
    }
  
    return false
  }
  return {
    isShowBpm,
    iframeUrl,
    bpmId,
    isCanApprove,
    checkCanApprove
  };
}
