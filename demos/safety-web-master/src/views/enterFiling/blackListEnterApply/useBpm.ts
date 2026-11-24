import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '/@/store/modules/user';
import { useGlobSetting } from '/@/hooks/setting';
import { getProcessCurDealHandler } from './blacklist.api';
import '/@/assets/bpm/domain';
// 00 废弃
// 10 驳回
// 20 待审核
// 30 通过
const globSetting = useGlobSetting();

const { bpmUrl } = globSetting;
export function useBpm(detailInfo, detailId) {
  const isApproveStatus = ref(false); //是否是审核人审核状态
  const isEdit = ref(false); //是否是编辑状态
  const isRejectStatus = ref(false); //提交人提交被驳回状态
  const currentNode = ref(''); //当前节点
  /**
   * 启用编辑模式
   */
  function enableEdit() {
    console.log('enableEdit 被调用, isEdit 修改前:', isEdit.value);
    isEdit.value = true;
    console.log('enableEdit 被调用, isEdit 修改后:', isEdit.value);
  }

  const route = useRoute();
  const userInfo = useUserStore().getUserInfo;
  // const iframeUrl = ref(
  //   `${bpmUrl}?method=showProcess&sysId=SAFETY&modelId=monitor_post&templateFormId=safety_monitor_post_process&formInstanceId=${route.query.id}&processId=${route.query.bpmFlowId}`
  // );
  /**
   * 是否为查看模式
   */
  const isView = computed(() => {
    if (route.query?.type === 'view') return true;
    return false;
  });
  /**
   * 是否为新增模式
   */
  const isAdd = computed(() => {
    return !detailId.value;
  });
  //是否显示编辑按钮
  const isShowEdit = computed(() => {
    if (isView.value) {
      return false;
    }
    return (detailInfo.value?.status == '10' || currentNode.value == 'N2') && detailInfo.value?.createEmpNo == userInfo.workNo && !isEdit.value;
  });

  //是否显示bpm流程
  const isShowBpm = computed(() => {
    return Boolean(route.query.bpmFlowId || detailInfo.value?.bpmFlowId);
  });
  const bpmId = computed(() => {
    return route.query.bpmFlowId;
  });
  const iframeUrl = computed(() => {
    let bpmFlowId = detailInfo.value?.bpmFlowId;

    if (!bpmFlowId) {
      bpmFlowId = detailInfo.value?.bpmFlowId;
    }
    if (detailId.value && bpmFlowId) {
      const url = `${bpmUrl}?method=showProcess&formInstanceId=${detailId.value}&processId=${bpmFlowId}`;
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
  async function checkCanApprove(bpmFlowId) {
    if (detailInfo.value?.status == '00' || detailInfo.value?.status == '30') {
      //废弃 或者 结束都不显示审批
      return false;
    }
    const res = await getProcessCurDealHandler(bpmFlowId);
    // 使用some方法简化遍历判断逻辑
    if (Array.isArray(res) && userInfo.userName) {
      const match = res.find((item) => item?.fdCurrLoginNames?.includes(userInfo.userName) && item?.nodeIds?.includes('N2'));
      if (match) {
        currentNode.value = match.nodeIds;
        debugger;
      }
      if (
        res.some((item) => item?.fdCurrLoginNames?.includes(userInfo.userName) && !item?.nodeIds?.includes('N2') && detailInfo.value?.status == '20')
      ) {
        isApproveStatus.value = true;
        return true;
      }
      if (
        res.some((item) => item?.fdCurrLoginNames?.includes(userInfo.userName) && item?.nodeIds?.includes('N2') && detailInfo.value?.status == '10')
      ) {
        isRejectStatus.value = true;
        return true;
      }
    }

    return false;
  }

  const isShowSubmit = computed(() => {
    if (isView.value) {
      return false;
    }
    return isAdd.value || isEdit.value || isApproveStatus.value;
  });
  const isCanEditInfo = computed(() => {
    if (isView.value) {
      return false;
    }
    return isAdd.value || isEdit.value;
  });

  //是否是审核人审核状态
  const isApprovalStatus = computed(() => {
    //待审核
    return detailInfo.value?.status == '20' && detailInfo.value?.applyEmpLogin == userInfo.userName;
  });
  //是否是9开头的员工
  const isStartsWith9 = computed(() => {
    if (!userInfo.workNo) {
      return false;
    }
    return userInfo.workNo?.startsWith("9");
  });
  return {
    isEdit,
    isView,
    isAdd,
    isShowEdit,
    isShowSubmit,
    isShowBpm,
    iframeUrl,
    bpmId,
    isApproveStatus,
    checkCanApprove,
    getBpmData,
    isCanEditInfo,
    enableEdit,
    isRejectStatus,
    isStartsWith9,
  };
}
