import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from '/@/store/modules/user';
import dayjs from 'dayjs';
import { getBuildByEmployee, getEmployeeByWorkNo,getBuildByEmployeeForDiy } from './accessRequestForm.api';

// 定义类型
interface EmployeeInfo {
  empName?: string;
  empNo?: string;
  orgNamePath?: string;
  orgCodePath?: string;
  [key: string]: any;
}

interface DoorItem {
  deviceCode?: string;
  deviceName?: string;
  deviceDOList?: Array<{
    deviceCode: string;
    deviceName: string;
  }>;
  [key: string]: any;
}

interface ChiefEmployee {
  id: string;
  name: string;
  email?: string;
  [key: string]: any;
}

export function useAccessRequestForm(props) {
  const route = useRoute();
  const userInfo = useUserStore().getUserInfo;

  // 计算属性 - 是否编辑模式
  const isEdit = computed(() => {
    return route.query.type === 'edit';
  });
  // 计算属性 - 是否查看模式
  const isView = computed(() => {
    return route.query.type === 'view';
  });
  // 计算属性 - 是否编辑/查看模式
  const isViewOrEdit = computed(() => {
    return route.query.type === 'view' || route.query.type === 'edit';
  });
  const isManage = computed(() => {
    return route.query?.isManage === '1';
  });
  
  const isDetail = computed(() => {
    return isViewOrEdit.value ;
  });
  //获取id
  const processId = computed(() => {
    return route.query.formInstanceId;
  })
  //是否显示bpm流程
  const isShowBpm = computed(() => {
    return route.query.bpmFlowId;
  })
  // 当前用户信息
  const currentUserInfo = ref<EmployeeInfo>({});

  // 部门科长
  const chiefEmployee = ref<ChiefEmployee[]>([]);

  // 门禁列表
  const doorList = ref<DoorItem[]>([]);

  // 日期选择器控制
  const datePickerOpen = ref(false);

  // 初始化用户数据
  const initUserData = async (workNo?:string) => {
    try {
      workNo = workNo || userInfo.workNo;
      // 使用具体工号获取员工信息，后续可改为动态获取
      currentUserInfo.value = (await getBuildByEmployeeForDiy(workNo)) as EmployeeInfo;

      return {
        createEmployeeName: currentUserInfo.value.empName,
        createEmployeeNo: currentUserInfo.value.empNo,
        createDeptName: currentUserInfo.value.orgNamePath,
        authorizedDoors: currentUserInfo.value.buildPowerList.map((item) => item.deviceName).join(',') || '/',
      };
    } catch (error) {
      console.error('获取用户数据失败', error);
      return {};
    }
  };

  // 处理点击长期按钮
  const handleLongTermClick = (model: any, field: string) => {
    // 设置长期日期（今天到2099-12-31）
    model[field] = [dayjs(), dayjs('2099-12-31')];

    // 直接关闭日期选择器面板
    datePickerOpen.value = false;
  };

  // 移除门禁项
  const removeDoorItem = (_: any, index: number) => {
    doorList.value.splice(index, 1);
  };

  // 更新门禁项详情
  const updateItemDetail = (detail: DoorItem, index: number) => {
    if (index >= 0 && index < doorList.value.length) {
      // 使用splice更新数组中的项，保持响应性
      doorList.value.splice(index, 1, detail);
    }
  };

  // 科长选择确认
  const onChiefEmployeeOk = () => {
    if (chiefEmployee.value.length > 0) {
      return {
        chiefEmployeeName: chiefEmployee.value[0]?.name,
        chiefEmployeeLogin: chiefEmployee.value[0]?.email?.split('@')[0],
      };
    }
    return {};
  };

  return {
    isDetail,
    processId,
    isViewOrEdit,
    isView,
    isEdit,
    isManage,
    currentUserInfo,
    chiefEmployee,
    doorList,
    datePickerOpen,
    initUserData,
    handleLongTermClick,
    removeDoorItem,
    updateItemDetail,
    onChiefEmployeeOk,
  };
}
