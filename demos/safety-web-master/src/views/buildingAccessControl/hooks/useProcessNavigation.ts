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
    processType: 'personAccessDetail' | 'accessLog' | 'autoAuthOrg' | 'accessRequestProcess',
    roleType: string,
    extraParams: Record<string, any> = {}
  ) => {
    if (!processId) {
      createMessage.warning('流程ID不能为空');
      return;
    }
    switch (processType) {
      case 'accessLog':
        {
        }
        break;
      default:
        break;
    }

    // 打印传入的参数
    console.log('流程导航参数:', {
      processId: processId,
      bpmFlowId: bpmFlowId,
      processType: processType,
      extraParams: extraParams,
    });
    const targetRoute = {
      path: '/accessRequest',
      query: {
        formInstanceId: processId,
        bpmFlowId: bpmFlowId,
        type: 'view',
        roleType: roleType,
        isManage: '1',
      },
    };
    console.log('addFullParam 输入:', targetRoute);
    const resultRoute = addFullParam(targetRoute);
    console.log('addFullParam 输出:', resultRoute);
    router.push(resultRoute);
  };

  return {
    navigateToProcessDetail,
  };
}
