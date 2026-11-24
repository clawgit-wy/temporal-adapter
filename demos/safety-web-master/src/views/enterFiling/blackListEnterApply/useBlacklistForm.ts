import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useMessage } from '/@/hooks/web/useMessage';
import { useUserStore } from '/@/store/modules/user';
import { getEmployeeByWorkNo, getProcessDetailById } from './blacklist.api';

interface FormMethods {
  validateFields: (nameList?: any[]) => Promise<any>;
  getFieldsValue: (type?: boolean) => any;
  setFieldsValue: (values: any) => Promise<void>;
}

/**
 * 黑名单表单逻辑封装
 */
export function useBlacklistForm(formMethods?: FormMethods) {
  const route = useRoute();
  const { createMessage, createWarningModal } = useMessage();
  const userInfo = useUserStore().getUserInfo;

  const detailInfo = ref(null);

  // 表单操作方法
  const { validateFields, getFieldsValue, setFieldsValue } = formMethods || {
    validateFields: () => Promise.resolve(),
    getFieldsValue: () => ({}),
    setFieldsValue: () => Promise.resolve(),
  };

  const detailId = computed(() => {
    return route.query?.formInstanceId || '';
  });

  /**
   * 初始化表单数据
   */
  async function initFormData() {
    try {
      const userData = await getEmployeeByWorkNo({ workNo: userInfo.workNo });

      return {
        createEmpName: userData.empName,
        createEmpNo: userData.empNo,
        department: userData.orgNamePath,
        createEmpLogin: userData.empLogin,
      };
    } catch (error) {
      console.error('获取用户信息失败:', error);
      createMessage.error('获取用户信息失败');
      return null;
    }
  }

  /**
   * 获取流程详情
   */
  async function getProcessDetail() {
    try {
      const res = await getProcessDetailById({ processId: detailId.value as string });

      // 设置黑名单数据，并初始化审核状态
      const blacklistData = (res.detailList || []).map((item) => ({
        ...item,
        // applyStatus: '',
      }));
      detailInfo.value = {
        ...res,
        detailList: blacklistData,
        departmentHead: [
          {
            id: res.applyEmpNo,
            name: res.applyEmpName,
            email: `${res.applyEmpLogin}@yutong.com`,
          },
        ],
      };

      return {
        ...detailInfo.value,
      };
    } catch (error) {
      console.error('获取流程详情失败:', error);
      return null;
    }
  }

  /**
   * 导入成功回调
   * @param fileInfo 后端返回的文件信息
   */
  function handleImportSuccess(fileInfo) {
    console.log('导入成功', fileInfo);
    const { data: dataObj } = fileInfo;
    if (dataObj.message) {
      createWarningModal({ content: dataObj.message, title: '提示信息' });
      return;
    }
    const data = dataObj?.data;

    if (data && Array.isArray(data)) {
      const importedData = data.map((item, index) => ({
        ...item,
        key: `${Date.now()}_${index}`,
        approvalStatus: 1, // 默认允许入厂
      }));
      // 获取当前表单中的黑名单数据，追加新导入的数据
      const formValues = getFieldsValue();
      const currentData = formValues.detailList || [];
      setFieldsValue({ detailList: [...currentData, ...importedData] });
      // validateFields(['blacklistData']).catch(() => {});
    }
  }

  function handleImportError(fileInfo) {
    console.log('导入失败', fileInfo);
    if (fileInfo.code != '0') {
      createMessage.error(fileInfo.msg);
    }
  }
  /**
   * 删除单条数据
   */
  function handleDelete(index) {
    const formValues = getFieldsValue();
    const currentData = formValues.detailList || [];
    currentData.splice(index, 1);
    setFieldsValue({ detailList: [...currentData] });
    createMessage.success('删除成功');
  }

  /**
   * 验证黑名单数据
   */
  function validateBlacklistData() {
    const formValues = getFieldsValue();
    const detailList = formValues.detailList || [];
    if (detailList.length === 0) {
      createMessage.warning('请至少添加一条黑名单人员信息');
      return false;
    }
    return true;
  }
  /**
   * 提取部门负责人信息
   * @param departmentHead 部门负责人对象
   * @returns 申请人信息对象
   */
  function extractApplyEmployeeInfo(departmentHead: any) {
    if (!departmentHead) {
      return null;
    }
    return {
      applyEmpLogin: departmentHead.email?.split('@')[0] || '',
      applyEmpName: departmentHead.name || '',
      applyEmpNo: departmentHead.id || '',
    };
  }

  const currentState = computed({
    get() {
      return detailInfo.value?.status;
    },
    set(value) {
      detailInfo.value.status = value;
    },
  });
  const versionNo = computed(() => {
    console.log('传递的值==', route.query.versionNo);
    return route.query.versionNo || '';
  });
  return {
    detailId,
    detailInfo,
    versionNo,
    initFormData,
    getProcessDetail,
    handleImportSuccess,
    handleDelete,
    validateBlacklistData,
    extractApplyEmployeeInfo,
    handleImportError,
  };
}
