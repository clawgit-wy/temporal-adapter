<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="modalTitle"
    @ok="handleSubmit"
    width="800px"
    :canFullscreen="true"
  >
    <BasicForm @register="registerForm">
      <template #doorAccessList>
        <div class="door-access-list">
          <a-table
            :columns="doorAccessColumns"
            :dataSource="doorAccessList"
            :pagination="false"
            size="small"
            bordered
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'status'">
                <span :class="record.status === '1' ? 'text-success' : 'text-warning'">
                  {{ record.status === '1' ? '有效' : '已过期' }}
                </span>
              </template>
            </template>
          </a-table>
        </div>
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { detailFormSchema } from './personChangeAuthConfirm.data';
  import {  } from './personChangeAuthConfirm.api';
  import { useMessage } from '/@/hooks/web/useMessage';

  const emit = defineEmits(['success', 'register']);
  const { createMessage } = useMessage();

  const isView = ref(false);
  const recordId = ref('');
  const doorAccessList = ref([]);

  const doorAccessColumns = [
    {
      title: '门禁名称',
      dataIndex: 'deviceName',
      width: 150,
    },
    {
      title: '门禁编码',
      dataIndex: 'deviceCode',
      width: 120,
    },
    {
      title: '所属楼宇',
      dataIndex: 'buildingName',
      width: 150,
    },
    {
      title: '所属厂区',
      dataIndex: 'factoryName',
      width: 150,
    },
    {
      title: '授权开始时间',
      dataIndex: 'powerBeginTime',
      width: 150,
    },
    {
      title: '授权结束时间',
      dataIndex: 'powerEndTime',
      width: 150,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
    },
  ];

  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    resetForm();
    if (data && data.record) {
      recordId.value = data.record.id;
      isView.value = !!data.isView;
      await loadPersonChangeDetail();
    }
  });

  // 表单
  const [registerForm, { validate, resetFields: resetForm, setFieldsValue }] = useForm({
    labelWidth: 100,
    schemas: detailFormSchema,
    showActionButtonGroup: false,
  });

  // 模态框标题
  const modalTitle = computed(() => isView.value ? '查看人员变更详情' : '确认人员变更');

  // 加载详情
  async function loadPersonChangeDetail() {
    if (!recordId.value) return;
    
    try {
      setModalProps({ loading: true });
      // const data = await getPersonChangeDetail(recordId.value);
      
      // 设置表单数据
      setFieldsValue({
        ...data,
        changeType: data.changeType?.toString(),
      });
      
      // 设置门禁列表
      doorAccessList.value = data.doorAccessList || [];
    } catch (error) {
      console.error('获取人员变更详情失败:', error);
    } finally {
      setModalProps({ loading: false });
    }
  }

  // 提交表单
  async function handleSubmit() {
    try {
      if (isView.value) {
        closeModal();
        return;
      }
      
      setModalProps({ confirmLoading: true });
      await validate();
      
      // await confirmPersonChange(recordId.value);
      createMessage.success('确认成功');
      emit('success');
      closeModal();
    } catch (error) {
      console.error('表单验证或提交失败:', error);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>

<style lang="less" scoped>
.door-access-list {
  margin-top: 8px;
  
  .text-success {
    color: #52c41a;
  }
  
  .text-warning {
    color: #faad14;
  }
}
</style> 
