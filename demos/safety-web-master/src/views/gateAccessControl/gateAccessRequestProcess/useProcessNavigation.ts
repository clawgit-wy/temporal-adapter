import { useRouter } from 'vue-router';
import { useMessage } from '/@/hooks/web/useMessage';
import { addFullParam } from '/@/utils/router/fullParam';

/**
 * 流程导航钩子
 * 提供通过流程ID跳转到相应详情页面的功能
 */
export function useProcessNavigation() {
  const router = useRouter();
  const { createMessage } = useMessage();
  /**
   *
   * @param processId
   * @param bpmFlowId
   * @param processType
   * @param extraParams
   * @returns
   */
  const navigateToProcessDetail = (
    processId: string,
    bpmFlowId: string,
    processType: 'gateAccessRequestProcess' ,
    extraParams: Record<string, any> = {}
  ) => {
    if (!processId) {
      createMessage.warning('流程ID不能为空');
      return;
    }
    // 打印传入的参数
    console.log('流程导航参数:', {
      processId: processId,
      bpmFlowId: bpmFlowId,
      processType: processType,
      extraParams: extraParams,
    });
    router.push(addFullParam({
      path: '/gateAccessRequestProcess/form',
      query: {
        formInstanceId: processId,
        bpmFlowId: bpmFlowId,
        type: 'view',
      },
    }));
  };

  return {
    navigateToProcessDetail,
  };
}
