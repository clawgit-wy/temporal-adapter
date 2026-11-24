<template>
  <div>
    <div v-if="!disabled && dataList.length < lastNum" style="display: flex; justify-content: flex-end; margin-bottom: 8px">
      <a-button type="primary" preIcon="ant-design:plus-outlined" @click="openAddModal">添加</a-button>
    </div>
    <a-table :dataSource="dataList" :pagination="false" bordered size="small" rowKey="id">
      <a-table-column title="序号" :width="60" :customRender="({ index }) => index + 1" />
      <a-table-column title="员工号" dataIndex="empNo" :width="120" />
      <a-table-column title="姓名" dataIndex="empName" :width="100" />
      <a-table-column title="组织架构" dataIndex="orgName" :minWidth="160" />
      <a-table-column title="岗位" dataIndex="position" :width="100" />
      <a-table-column v-if="disabled" title="职种" dataIndex="jobName" :width="100" />
      <a-table-column v-if="!disabled" title="操作" :width="70">
        <template #default="{ index }">
          <a-button type="link" danger @click="removeRow(index)">删除</a-button>
        </template>
      </a-table-column>
    </a-table>

    <!-- 添加被授权人弹窗 -->
    <a-modal
      v-model:visible="addVisible"
      title="添加"
      width="600px"
      @ok="handleSubmit"
      @cancel="handleCancel"
      destroyOnClose
    >
      <div class="add-form">
        <div class="form-row">
          <label class="form-label">被授权人员工号：</label>
          <div class="form-content">
            <a-input
              v-model:value="form.empNo"
              placeholder="请输入员工号"
              @blur="onEmpNoBlur"
            />
            <span class="form-tip">输入员工号自动带出人员姓名、组织、岗位</span>
          </div>
        </div>
        <div class="form-row">
          <label class="form-label">被授权人姓名：</label>
          <div class="form-content">
            <a-input v-model:value="form.empName" disabled placeholder="自动带出" />
          </div>
        </div>
        <div class="form-row">
          <label class="form-label">被授权人组织：</label>
          <div class="form-content">
            <a-input v-model:value="form.orgName" disabled placeholder="自动带出" />
          </div>
        </div>
        <div class="form-row">
          <label class="form-label">被授权人岗位：</label>
          <div class="form-content">
            <a-input v-model:value="form.position" disabled placeholder="自动带出" />
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref, unwatch, UnwrapRef, watchEffect, watch } from 'vue';
import { getEmployeeByWorkNo } from '../field.api';
import { message } from 'ant-design-vue';

interface AuthorizeeItem {
  id: number;
  empNo: string;
  empName: string;
  orgName: string;
  position: string;
  jobName: string;
}

interface FormData {
  empNo: string;
  empName: string;
  orgName: string;
  position: string;
  jobName: string;
}

export default defineComponent({
  name: 'AuthorizeeTable',
  props: {
    value: { type: Array, default: () => [] },
    disabled: { type: Boolean, default: false },
    lastNum: { type: Number, default: 0 },
  },
  emits: ['change', 'update:value'],
  setup(props, { emit }) {
    const dataList: UnwrapRef<AuthorizeeItem[]> = reactive([]);
    const nextId = ref(1);
    const addVisible = ref(false);

    const form: UnwrapRef<FormData> = reactive({
      empNo: '',
      empName: '',
      orgName: '',
      position: '',
      jobName: '',
    });

    const resetForm = () => {
      form.empNo = '';
      form.empName = '';
      form.orgName = '';
      form.position = '';
      form.jobName = '';
    };

    const openAddModal = () => {
      resetForm();
      addVisible.value = true;
    };

    const handleCancel = () => {
      addVisible.value = false;
      resetForm();
    };

    const onEmpNoBlur = async () => {
      const workNo = form.empNo?.trim();
      if (!workNo) return;

      try {
        const userInfo = await getEmployeeByWorkNo({ workNo, phone: '' });
        if (userInfo) {
          form.empName = userInfo.empName || '';
          form.orgName = userInfo.deptName || userInfo.orgName || '';
          form.position = userInfo.positionName || '';
          form.jobName = userInfo.jobTypeName || '';
        } else {
          message.warning('未查询到该员工信息');
          form.empName = '';
          form.orgName = '';
          form.position = '';
          form.jobName = '';
        }
      } catch {
        message.error('查询员工信息失败');
        form.empName = '';
        form.orgName = '';
        form.position = '';
        form.jobName = '';
      }
    };

    const handleSubmit = () => {
      const workNo = form.empNo?.trim();
      if (!workNo) {
        message.warning('请输入被授权人员工号');
        return;
      }
      if (!form.empName) {
        message.warning('请填写完整的被授权人信息');
        return;
      }

      // 检查是否已存在
      const exists = dataList.some((item) => item.empNo === workNo);
      if (exists) {
        message.warning('该员工已添加');
        return;
      }

      dataList.push({
        id: nextId.value++,
        empNo: workNo,
        empName: form.empName,
        orgName: form.orgName,
        position: form.position,
        jobName: form.jobName,
      });

      emitChange();
      addVisible.value = false;
      resetForm();
    };

    const removeRow = (index: number) => {
      dataList.splice(index, 1);
      emitChange();
    };

    const emitChange = () => {
      const list = dataList.map(({ id, ...rest }) => ({ ...rest }));
      emit('update:value', list);
      emit('change', list);
    };

    const syncData = (val: any[]) => {
      dataList.length = 0;
      if (val && Array.isArray(val)) {
        val.forEach((item: any) => {
          dataList.push({
            id: nextId.value++,
            empNo: item.empNo || '',
            empName: item.empName || '',
            orgName: item.orgName || '',
            position: item.position || '',
            jobName: item.jobName || '',
          });
        });
      }
    };

    watch(() => props.value, syncData, { immediate: true });

    return {
      dataList,
      addVisible,
      form,
      openAddModal,
      handleCancel,
      onEmpNoBlur,
      handleSubmit,
      removeRow,
    };
  },
});
</script>

<style lang="less" scoped>
.add-form {
  padding: 20px 20px 0;
}

.form-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;

  .form-label {
    width: 120px;
    text-align: right;
    margin-right: 12px;
    flex-shrink: 0;
  }

  .form-content {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;

    .ant-input {
      flex: 1;
    }
  }

  .form-tip {
    color: #999;
    font-size: 12px;
    white-space: nowrap;
  }
}

:deep(.ant-modal-footer) {
  text-align: center;
  border-top: none;
  padding-bottom: 24px;

  .ant-btn {
    margin: 0 8px;
  }
}
</style>
