<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <div class="scm-modal">
      <BasicForm @register="registerForm">
        <template #depart="{ model, field }">
          <a-input v-model:value="model[field]" placeholder="请选择" readOnly @click="deptChose" />
        </template>

        <template #factoryName="{ model, field }">
          <a-select v-model:value="model[field]" placeholder="请选择厂区" mode="multiple" allowClear>
            <a-select-option v-for="item in props.factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
          </a-select>
        </template>
      </BasicForm>
      <DepartmentSelect v-model:visible="searchDepartVisible" @change="searchDepartChange"></DepartmentSelect>
    </div>
  </BasicModal>
</template>
<script lang="ts" setup name="ScmModal">
  import { ref, reactive, computed, unref } from 'vue';
  import { debounce } from 'lodash-es';
  import { AutoComplete } from 'ant-design-vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStore } from '/@/store/modules/user';
  import { scmSchema } from './config';
  import { update, fieldChildTree } from './field.api';
  import DepartmentSelect from '/@/components/DepartmentSelect.vue';
  // 声明Emits
  const emit = defineEmits(['success', 'register']);
  const props = defineProps(['factoryLists']);
  const isUpdate = ref(true); // 新增/编辑
  const isUpdated = ref(0); // 操作表示：0 无操作 1 新增/新增加编辑 2 编辑
  // 页面消息
  const { createMessage } = useMessage();
  // 用户信息
  const username = ref('');
  const userStore = useUserStore();
  username.value = userStore.getUserInfo.username;

  // 表单配置
  const rowId = ref('');
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, updateSchema, getFieldsValue, clearValidate }] = useForm({
    schemas: scmSchema,
    showActionButtonGroup: false,
  });
  // 表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    console.log('厂区规则=======', data);

    //重置表单
    await resetFields();
    await setProps({
      labelCol: {
        sm: { span: 5 },
      },
      wrapperCol: {
        sm: { span: 17 },
      },
      baseRowStyle: { width: 50 },
    });
    await setModalProps({ confirmLoading: false, canFullscreen: false });
    isUpdate.value = !!data?.isUpdate;
    if (unref(isUpdate)) {
      rowId.value = data.record.id;
    }
    //表单赋值
    if (typeof data.record === 'object') {
      setFieldsValue({
        ...data.record,
      });
    }
  });

  //组织选择
  const searchDepartVisible = ref(false);
  function deptChose() {
    searchDepartVisible.value = true;
  }
  const searchDepartChange = (depart) => {
    console.log('组织选择==', depart);
    setFieldsValue({ orgName: depart.title, orgCode: depart.key });
    searchDepartVisible.value = false;
  };

  //获取标题
  const getTitle = computed(() => (!unref(isUpdate) ? '新增' : '编辑'));

  //表单提交事件
  async function handleSubmit() {
    try {
      let values = await validate();
      console.log('验证表单==', values, unref(isUpdate));
      setModalProps({ confirmLoading: true });
      if (unref(isUpdate)) {
        values.id = unref(rowId);
      }
      await update(values, unref(isUpdate));
      isUpdated.value = 1;
      console.log('编辑应用成功');
      createMessage.success({ content: '操作成功' });
      closeModal();
      emit('success', { isUpdate: unref(isUpdated), values });
    } catch (err) {
      console.log('subsystem modal err', typeof err, JSON.stringify(err), err);
      if (err && err instanceof Error && err.message) {
        createMessage.error({ content: err.message });
      }
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
<style lang="less">
  .scm-modal {
    .ant-row {
      .ant-col {
        // width: 100%;
        .ant-calendar-picker {
          display: block;
        }
      }
    }
  }
</style>
