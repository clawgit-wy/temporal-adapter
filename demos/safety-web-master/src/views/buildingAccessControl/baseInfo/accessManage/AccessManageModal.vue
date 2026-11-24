<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm">
      <template #secretAreaManagerSlot>
        <a-form-item-rest>
          <person-select-input
            v-model:personList="secretAreaManagerPersons"
            :placeholder="'请选择绝密区负责人'"
            :multiple="false"
            :disabled="formDisabled"
          />
        </a-form-item-rest>
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref, watch } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { formSchemas } from './accessManage.data';
  import { saveOrUpdate } from './accessManage.api';
  import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';

  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  const formDisabled = ref(false);

  // 人员选择相关状态 - 现在是数组
  const secretAreaManagerPersons = ref<any[]>([]);

  // 监听人员列表变化
  watch(
    () => secretAreaManagerPersons.value,
    (persons) => {
      // 更新表单中的人员 topSecretEmpCode  topSecretEmpLogin topSecretEmpName
      updateSecretManagerIds(persons);
    },
    { deep: true }
  );

  // 更新表单中的人员 topSecretEmpCode  topSecretEmpLogin topSecretEmpName
  const updateSecretManagerIds = (persons: any[]) => {
    // 将所有ID以逗号分隔存入表单
    const topSecretEmpCode = persons.map((p) => p.id).join(',');
    const topSecretEmpLogin =
      persons.length > 0
        ? persons
            .map((p) => {
              return p.email?.split('@')[0] || '';
            })
            .join(',')
        : '';
    const topSecretEmpName = persons.map((p) => p.name).join(',');
    setFieldsValue({
      topSecretEmpCode,
      topSecretEmpLogin,
      topSecretEmpName,
    });
  };

  // 注册 form
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, clearValidate, updateSchema }] = useForm({
    schemas: formSchemas,
    showActionButtonGroup: false,
  });

  // 加载保密等级数据
  const setupForm = async () => {
    try {
      // 更新表单中保密等级字段的选项
      updateSchema({
        field: 'securityLevel',
        componentProps: {
          onChange: (value: string) => {
            // 当保密等级不是绝密时，清空人员选择
            console.log('门禁管理员************', value);
            if (value !== '937_gsjm_2' && !Array.isArray(value)) {
              secretAreaManagerPersons.value = [];
              // 清空相关隐藏字段
              setFieldsValue({
                secretAreaManager: '',
                topSecretEmpCode: '',
                topSecretEmpLogin: '',
                topSecretEmpName: '',
              });
            }
          },
        },
      });
    } catch (error) {
      console.error('获取保密等级数据失败:', error);
    }
  };

  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    console.log('模态框打开，数据:', data);
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    formDisabled.value = !data?.showFooter;

    // 初始化表单
    await resetFields();

    // 设置表单值
    await setFieldsValue({ ...data.record });
    await setupForm();
    // 清除验证状态
    clearValidate();
    // 隐藏底部时禁用整个表单
    setProps({ disabled: formDisabled.value });
    updateSecretManagerPersons(data.record);
  });
  //将 topSecretEmpCode,topSecretEmpLogin,topSecretEmpName 转换为数组并赋值给secretAreaManagerPersons
  const updateSecretManagerPersons = (record) => {
    console.log('secretAreaManagerPersons++++++', record);
    secretAreaManagerPersons.value = [{ id: record.topSecretEmpCode, name: record.topSecretEmpName, email: record.topSecretEmpLogin }];
  };
  // 使用防抖包装提交函数，防止多次点击
  const onSubmit = async () => {
    try {
      const values = await validate();
      delete values.anFactoryName;
      delete values.anBuilding;
      delete values.deviceName;
      setModalProps({ confirmLoading: true });
      console.log('values************', values);
      await saveOrUpdate(values, isUpdate);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  };
</script>
