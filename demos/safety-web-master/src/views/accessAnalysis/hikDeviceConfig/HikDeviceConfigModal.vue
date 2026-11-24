<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getAreaCodes, getAreaNames, getAreaValue } from '../common/area';
  import { deviceFormSchemas, thresholdFormSchemas } from './data';
  import { saveOrUpdate } from './api';

  type ConfigType = 'device' | 'threshold';

  const emit = defineEmits(['success', 'register']);
  const { createMessage } = useMessage();
  const title = ref('');
  const configType = ref<ConfigType>('device');
  const isUpdate = ref(false);

  const [registerForm, { resetFields, resetSchema, setFieldsValue, validate, setProps }] = useForm({
    schemas: deviceFormSchemas,
    showActionButtonGroup: false,
  });
  /*修改分支名称*/
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    title.value = unref(data.title);
    isUpdate.value = unref(data.isUpdate);
    configType.value = data.configType || 'device';
    await resetSchema(configType.value === 'device' ? deviceFormSchemas : thresholdFormSchemas);
    await resetFields();
    await setFieldsValue({
      ...getDefaultValues(),
      ...data.record,
      areaCode: getAreaValue(data.record),
      entryMode: data.record?.accessMethod,
      threshold: getThresholdValue(data.record?.sceneValue || data.record?.threshold),
    });
    setProps({ disabled: !data?.showFooter });
  });

  async function onSubmit() {
    try {
      const values = await validate();
      const { areaCode, ...restValues } = values || {};
      setModalProps({ confirmLoading: true });
      const areaParams = configType.value === 'device' ? getAreaParams(areaCode) : {};
      await saveOrUpdate({ ...restValues, ...areaParams, configType: configType.value, isUpdate: isUpdate.value });
      createMessage.success('提交成功');
      closeModal();
      emit('success', configType.value);
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }

  function getDefaultValues() {
    if (configType.value === 'device') {
      return {
        id: undefined,
        indexCode: undefined,
        areaCode: [],
        entryMode: undefined,
      };
    }
    return {
      id: undefined,
      sceneCode: undefined,
      sceneName: '',
      threshold: 90,
    };
  }

  function getThresholdValue(value) {
    if (value === 0 || value) {
      return Number(String(value).replace('%', ''));
    }
    return getDefaultValues().threshold;
  }

  function getAreaParams(value) {
    const { factoryCode, buildCode } = getAreaCodes(value);
    const { factoryName, buildName } = getAreaNames(value);
    return {
      factoryCode,
      buildCode,
      factoryName,
      buildName,
    };
  }
</script>
