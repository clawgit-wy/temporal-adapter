<template>
  <PageWrapper title="受限人员入厂申请" dense :contentStyle="{ background: '#fff' }" contentFullHeight>
    <template #headerContent> </template>
    <div class="blacklist-enter-apply">
      <a-card title="受限人员入厂申请">
        <BasicForm @register="registerForm" :showActionButtonGroup="false">
          <template #tipsSlot>
            <div class="process-tips">
              <div class="tips-title">流程说明</div>
              <div class="tips-content">
                <p>1.业务部门对受限人员进出权限及厂内活动负责，请依据业务需求谨慎提交申请流程。</p>
                <p
                  >2.外来人员入厂备案：依据各负责人自身权限选择备案类型，在本流程直接为受限人员申请进出厂权限，流程通过并发布后自动授权进出权限，无需另行备案。（注意：备案类型不要选择“/”，否则无法授权）。</p
                >
                <p
                  >3.访客及物流车入厂：备案类型选择“/”，受限人员可在本流程通过并发布后，自行提交访客/物流车申请进厂权限，仅限本流程的提交人作为访客被访人/物流车对接人为受限人员授权流程范围内的进出权限，超出备案时间、备案厂区范围无法授权，受限人员选择其余员工作为对接人仍为禁止入厂状态。</p
                >
              </div>
            </div>
          </template>
          <template #blacklistDataSlot="{ model, field }">
            <div class="blacklist-table-wrapper">
              <div class="table-header" v-if="isAdd || isEdit">
                <a-button type="primary" preIcon="ant-design:plus-outlined"  @click="onAdd" class="mr-10px">单个添加</a-button>
                <BatchImport
                  button-text="批量导入"
                  :import-url="Api.importBlacklist"
                  :download-url="Api.downloadBlacklistTemplate"
                  @success="handleImportSuccess"
                  @error="handleImportError"
                  :disabled="!isStartsWith9"
                />
              </div>
              <BasicTable
                :columns="columns"
                :dataSource="model[field]"
                :pagination="false"
                :scroll="{ x: 1800 }"
                :showIndexColumn="false"
                :bordered="true"
                :useSearchForm="false"
                :showTableSetting="false"
                :showActionColumn="isAdd || isEdit || isApproveStatus"
                :actionColumn="{
                  width: 200,
                  title: '操作',
                  dataIndex: 'action',
                  fixed: 'right',
                  slots: { customRender: 'action' },
                }"
              >
                <template #action="{ record, index }">
                  <!-- 审核模式：显示单选框 -->
                  <div v-if="isApproveStatus" class="approval-radio">
                    <!-- 直接绑定 model[field][index].applyStatus，这样就是直接操作表单数据 -->
                    <a-radio-group v-model:value="model[field][index].applyStatus">
                      <a-radio :value="1">允许</a-radio>
                      <a-radio :value="2">不允许</a-radio>
                    </a-radio-group>
                  </div>
                  <!-- 编辑模式：使用 TableAction 组件 -->
                  <TableAction v-if="isEdit || isAdd" :actions="getTableAction(record, index)" />
                </template>
              </BasicTable>
            </div>
          </template>
        </BasicForm>
        <iframe v-if="isShowBpm" :src="iframeUrl" style="height: 1350px; width: 100%" id="bpmIframe" class="iframeClass"></iframe>
      </a-card>
    </div>
    <div class="bottom-view">
      <div class="action-buttons">
        <!-- <div class="action-buttons-sub" v-if="isCanApprove">
          <a-button @click="auditProcessAction(false)">不通过</a-button>
          <a-button type="primary" @click="auditProcessAction(true)">通过</a-button>
        </div> -->
        <div class="action-buttons-pass" v-if="isShowSubmit">
          <a-button @click="goBack">取消</a-button>
          <a-button type="primary" @click="submitForm" :disabled="!isStartsWith9">提交</a-button>
        </div>
        <div class="action-buttons-edit" v-if="isShowEdit">
          <a-button type="primary" @click="editAction">编辑</a-button>
        </div>
      </div>
    </div>
  </PageWrapper>
    <ApplicationModal @register="registerAddModal" @success="reload" />

</template>

<script lang="ts" setup>
  import {keyBy,merge, values} from "lodash-es";
  import { onMounted, computed, watch, watchEffect } from 'vue';
  import { useRouter } from 'vue-router';
  import { Image } from 'ant-design-vue';
  import { BasicForm, useForm } from '/@/components/Form';
  import { BasicTable, TableAction, ActionItem } from '/@/components/Table';
  import { PageWrapper } from '/@/components/Page';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useTabs } from '/@/hooks/web/useTabs';
  import { useModal } from '/@/components/Modal';
  import { formSchemas, columns as baseColumns } from './form.data';
  import { saveAndCreateProcess, approvalProcess, Api, getBlacklistByBatchNo } from './blacklist.api';
  import { useBpm } from './useBpm';
  import { useBlacklistForm } from './useBlacklistForm';
  import BatchImport from '/@/components/BatchImport/index.vue';
  import { is } from '/@/utils/is';
  import ApplicationModal from './ApplicationModal.vue';
  
  // 处理单个添加成功后的回调
  function reload(data) {
    console.log('单个添加成功:', data);
    // 批量表单去重，并且更新表单
    const formValues = getFieldsValue()
    const {detailList} = formValues;
    // 这里需要将data处理成以身份证号为键的对象
     // 1. 将原数组转为以身份证号为键的对象
  const arrayObj = keyBy(detailList, 'cardNo');
  
  // 2. 创建包含新数据的对象
  const newDataObj = { [data['cardNo']]: data };
  
  // 3. 合并两个对象（新数据覆盖旧数据）
  const mergedObj = merge({}, arrayObj, newDataObj);
  
  // 4. 转换回数组
  const newDetailList = values(mergedObj);
  // 更新表单数据
  setFieldsValue({...formValues, detailList:newDetailList})
}

  defineOptions({ name: 'BlackListEnterApply' });

  const router = useRouter();
  const { closeCurrent } = useTabs();
  const { createMessage } = useMessage();
  const [registerAddModal, { openModal: openAddModal }] = useModal();

  // 表单注册（先注册表单以获取表单方法）
  const [registerForm, { validate, getFieldsValue, setFieldsValue, clearValidate, setProps, validateFields, updateSchema }] = useForm({
    labelWidth: 130,
    schemas: formSchemas,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    disabled: false, // 初始化时设置为false，后续根据isView动态设置
  });

  // 表单逻辑（传入表单方法）
  const {
    detailId,
    detailInfo,
    versionNo,
    initFormData,
    getProcessDetail: loadProcessDetail,
    handleImportSuccess,
    handleDelete,
    validateBlacklistData,
    extractApplyEmployeeInfo,
    handleImportError,
  } = useBlacklistForm({
    validateFields,
    getFieldsValue,
    setFieldsValue,
  });

  // BPM流程逻辑
  const {
    isShowBpm,
    iframeUrl,
    isRejectStatus,
    checkCanApprove,
    getBpmData,
    isView,
    isAdd,
    isShowEdit,
    isShowSubmit,
    isEdit,
    isCanEditInfo,
    enableEdit,
    isApproveStatus,
    isStartsWith9,
  } = useBpm(detailInfo, detailId);
  // 单个人员添加
   const onAdd = (): void => {
    openAddModal(true, {
      isUpdate: false,
      showFooter: true,
      title: '添加人员',
    });
  };
  // 根据 isView 动态计算表格列配置
  const columns = computed(() => {
    return baseColumns(detailInfo.value);
  });

  // 监听 isCanEditInfo 变化，动态更新表单的 disabled 状态
  watch(isCanEditInfo, (canEdit, oldValue) => {
    console.log('watch触发了！监听的值是', canEdit, '旧值是', oldValue);
    setProps({ disabled: !canEdit });
  });
  watchEffect(() => {
    console.log('watch触发了！列表变化', getFieldsValue());
    // const { detailList } = getFieldsValue();
    const fieldsValue = getFieldsValue();
    if (fieldsValue) {
      const { detailList } = fieldsValue;
      console.log('watch触发了！列表变化2222', detailList);
      if (detailList && detailList.length > 0) {
        const hasLeaveType = detailList.some((item) => item.blankType == 0);
        if (hasLeaveType) {
          updateSchema([{ field: 'departmentHead', ifShow: true }]);
        }
      }
    }
  });
  // 页面初始化
  onMounted(async () => {
    try {
      if (detailId.value) {
        // 查看模式：加载流程详情
        const formValues = await loadProcessDetail();
        if (formValues) {
          await setFieldsValue(formValues);
          if (detailInfo.value?.bpmFlowId) {
            await checkCanApprove(detailInfo.value.bpmFlowId);
          }
        }
        setProps({ disabled: !isCanEditInfo.value });
      } else {
        if (!isStartsWith9.value) {
          createMessage.info('暂无权限申请，请联系对接人申请');
        }
        // 新建模式：初始化表单
        const formValues = await initFormData();
        if (versionNo.value) {
          const blacklistBatch = await getBlacklistByBatchNo({ versionNo: versionNo.value });
          if (blacklistBatch && blacklistBatch.length > 0) {
            formValues.detailList = blacklistBatch;
          }
        }

        console.log(formValues, 'formValues');
        if (formValues) {
          await setFieldsValue(formValues);
        }
      }
      // 初始化完成后清除所有校验状态
      await clearValidate();
    } catch (error) {
      console.error('页面初始化失败:', error);
      // createMessage.error('页面初始化失败');
    }
  });

  function editAction() {
    console.log('editAction 调用前:', {
      isEdit: isEdit.value,
      isCanEditInfo: isCanEditInfo.value,
    });
    enableEdit();
    console.log('editAction 调用后:', {
      isEdit: isEdit.value,
      isCanEditInfo: isCanEditInfo.value,
    });
    // watch 会自动更新表单的 disabled 状态，无需手动调用 setProps
  }
  /**
   * 提交表单
   */
  async function submitForm() {
    try {
      // 验证表单
      await validate();

      // 验证黑名单数据
      if (!validateBlacklistData()) {
        return;
      }

      const formData = getFieldsValue(true);
      console.log('提交表单数据:', formData);

      // 获取申请人信息（部门负责人）
      const departmentHead = formData.departmentHead?.[0];
      const applyEmployeeInfo = extractApplyEmployeeInfo(departmentHead);
      if (iframeUrl.value) {
        const fdParameterJson = await getBpmData();
        formData.fdParameterJson = JSON.stringify(fdParameterJson) || '';
      }
      // 构建提交参数
      const params = {
        ...formData,
        ...applyEmployeeInfo,
      };
      if (detailInfo.value?.bpmFlowId) {
        params.bpmFlowId = detailInfo.value?.bpmFlowId;
      }
      if (detailId.value) {
        params.id = detailId.value;
      }
      // 审批模式：检查是否所有记录都已选择审批状态
      if (
        isApproveStatus.value &&
        formData.detailList.some((item) => item.applyStatus == '0' || item.applyStatus === '' || item.applyStatus === null)
      ) {
        createMessage.error('请为所有受限人员选择是否允许入场');
        return;
      }
      delete params.departmentHead;
      console.log('审核提交的参数', params);
      await saveAndCreateProcess(params);
      createMessage.success('提交成功');
      closeCurrent();
    } catch (error) {
      console.error('提交失败:', error);
    }
  }
  /**
   * 获取表格操作列按钮
   */
  function getTableAction(record, index): ActionItem[] {
    const actions: ActionItem[] = [];
    actions.push({
      label: '删除',
      color: 'error',
      onClick: handleDelete.bind(null, index),
    });
    return actions;
  }

  /**
   * 返回上一页
   */
  function goBack() {
    router.back();
  }
</script>

<style lang="less" scoped>
  .header-content {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }

  .blacklist-enter-apply {
    padding: 16px 16px 64px 16px;
    background-color: #fff;

    ::v-deep .ant-form-item-control {
      max-width: 100%;
    }

    .process-tips {
      .tips-title {
        margin-bottom: 10px;
        color: #1890ff;
        font-weight: 600;
      }

      .tips-content {
        p {
          margin: 5px 0;
          color: #1890ff;
          font-size: 14px;
          line-height: 1.6;
        }
      }
    }

    .blacklist-table-wrapper {
      .table-header {
        display: flex;
        justify-content: flex-end;
        margin-bottom: 16px;
      }

      .approval-radio {
        display: flex;
        justify-content: center;
        align-items: center;
        ::v-deep .ant-radio-group {
          display: flex;
          flex-direction: row;
          gap: 8px;
        }
      }
    }
  }

  .bottom-view {
    box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.1);
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #fff;

    .action-buttons {
      display: flex;
      width: 100%;
      justify-content: center;
      gap: 16px;

      .action-buttons-sub,
      .action-buttons-pass,
      .action-buttons-edit {
        margin: 16px 0;
        display: flex;
        gap: 16px;

        .ant-btn {
          width: 100px;
        }
      }
    }
  }
</style>
