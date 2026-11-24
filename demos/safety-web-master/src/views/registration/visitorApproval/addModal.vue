<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" width="750px">
    <div class="add-modal">
      <BasicForm @register="registerForm">
        <template #granteeNoSlot="{ model, field }">
          <a-input v-model:value="model[field]" placeholder="请输入被授权人员工号" @blur="onGranteeBlur" />
        </template>
        <template #grantorNoSlot="{ model, field }">
          <a-input v-model:value="model[field]" placeholder="请输入授权人员工号" @blur="onGrantorBlur" />
        </template>
      </BasicForm>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup name="VisitorApprovalAddModal">
import { ref, computed, unref } from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import { BasicForm, useForm } from '/@/components/Form/index';
import { useMessage } from '/@/hooks/web/useMessage';
import { modalFormSchema } from './config';
import { getEmployeeByWorkNo, saveOrUpdate } from './field.api';
import dayjs from 'dayjs';

const emit = defineEmits(['success', 'register']);
const isUpdate = ref(false);
const rowId = ref('');
const { createMessage } = useMessage();

/** 校验日期格式是否为 YYYY-MM-DD，无效则返回当天 */
function formatDate(dateStr: string | undefined | null): string {
  if (dateStr && dayjs(dateStr, 'YYYY-MM-DD', true).isValid()) {
    return dayjs(dateStr).format('YYYY-MM-DD');
  }
  return dayjs().format('YYYY-MM-DD');
}

const [registerForm, { resetFields, setFieldsValue, validate, setProps }] = useForm({
  schemas: modalFormSchema,
  showActionButtonGroup: false,
});

const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
  await resetFields();
  await setProps({
    labelCol: { sm: { span: 6 } },
    wrapperCol: { sm: { span: 16 } },
  });
  await setModalProps({ confirmLoading: false });
  isUpdate.value = !!data?.isUpdate;
  if (unref(isUpdate)) {
    rowId.value = data.record?.id;
  }
  if (typeof data.record === 'object') {
    const record = data.record;
    // 授权时间：校验格式，无效则用当前日期
    // valueFormat: 'YYYY-MM-DD' 时 RangePicker 接受字符串格式
    const beginStr = formatDate(record.authorizationBeginTime);
    const endStr = formatDate(record.authorizationEndTime);
    const authorizationTime = [beginStr, endStr];
    setFieldsValue({
      ...record,
      authorizationTime,
    });
  }
});

/** 被授权人工号失焦 - 自动带出姓名和组织 */
async function onGranteeBlur(e) {
  const workNo = e.target.value?.trim();
  if (!workNo) return;
  try {
    const userInfo = await getEmployeeByWorkNo({ workNo, phone: '' });
    if (userInfo) {
      setFieldsValue({
        granteeName: userInfo.empName,
        granteeOrgName: userInfo.deptName || userInfo.orgName || '',
      });
    }
  } catch {
    // 忽略错误
  }
}

/** 授权人工号失焦 - 自动带出姓名和组织 */
async function onGrantorBlur(e) {
  const workNo = e.target.value?.trim();
  if (!workNo) return;
  try {
    const userInfo = await getEmployeeByWorkNo({ workNo, phone: '' });
    if (userInfo) {
      setFieldsValue({
        grantorName: userInfo.empName,
        grantorOrgName: userInfo.deptName || userInfo.orgName || '',
      });
    }
  } catch {
    // 忽略错误
  }
}

const getTitle = computed(() => (!unref(isUpdate) ? '添加' : '编辑'));

/** 提交 */
async function handleSubmit() {
  try {
    const values = await validate();
    setModalProps({ confirmLoading: true });

    // 拆分日期范围为开始/结束时间
    // valueFormat: 'YYYY-MM-DD' 使 RangePicker 返回格式化字符串（可能是数组或逗号分隔字符串）
    let authorizationBeginTime = '';
    let authorizationEndTime = '';
    const timeVal = values.authorizationTime;
    if (Array.isArray(timeVal)) {
      [authorizationBeginTime, authorizationEndTime] = timeVal;
    } else if (typeof timeVal === 'string' && timeVal) {
      [authorizationBeginTime, authorizationEndTime] = timeVal.split(',');
    }

    // 仅组装接口需要的字段
    const submitData: Record<string, any> = {
      granteeNo: values.granteeNo,
      grantorNo: values.grantorNo,
      authorizationBeginTime,
      authorizationEndTime,
      authorizationReason: values.authorizationReason,
    };
    if (unref(rowId)) {
      submitData.id = unref(rowId);
    }

    await saveOrUpdate(submitData);
    createMessage.success(unref(isUpdate) ? '编辑成功' : '新增成功');
    closeModal();
    emit('success');
  } catch {
    // 错误提示已在请求工具中统一处理
  } finally {
    setModalProps({ confirmLoading: false });
  }
}
</script>

<style lang="less" scoped>
.add-modal {
  .ant-row {
    margin-bottom: 8px;
  }
}
</style>
