<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <BasicForm @register="registerForm">
      <template #serviceEndDate="{ model, field, schema }">
        <a-date-picker
          placeholder="请选择服务截止日期"
          v-model:value="model[field]"
          valueFormat="YYYY-MM-DD"
          :allowClear="false"
          :disabled-date="disabledDate"
        ></a-date-picker>
      </template>
    </BasicForm>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicForm, FormSchema, useForm } from '/@/components/Form/index';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { saveOrUpdate, getEmployeeByWorkNo } from './list.api';
  import { getNewDictItems } from '/@/api/common/api';
  import { useMessage } from '/@/hooks/web/useMessage';
  import dayjs from 'dayjs';
  const { createMessage } = useMessage();

  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);

  let editDetail = {};
  const getPersonInfo = async (workNo) => {
    const res = await getEmployeeByWorkNo({ workNo });
    setFieldsValue({
      creatorName: res.empName,
    });
  };
  const formSchemas: FormSchema[] = [
    {
      label: 'ID',
      field: 'id',
      component: 'Input',
      show: false,
    },
    {
      label: '发起人员工号',
      field: 'creator',
      component: 'Input',
      required: true,
      componentProps: {
        onblur(e) {
          getPersonInfo(e.target.value);
        },
      },
    },
    {
      label: '发起人姓名',
      field: 'creatorName',
      component: 'Input',
      required: true,
      componentProps: {
        disabled: true,
      },
    },
    {
      label: '授权厂区',
      field: 'factory',
      component: 'ApiSelect',
      componentProps: () => {
        return {
          api: async () => {
            const res = await getNewDictItems('factory');
            return res;
          },
          mode: 'multiple',
          placeholder: '请选择厂区',
          valueField: 'id',
          labelField: 'fieldName',
        };
      },
    },
    {
      label: '服务截止日期',
      field: 'serviceEndDate',
      slot: 'serviceEndDate',
      component: 'InputTextArea',
      ifShow: true,
    },
    {
      label: '备注',
      field: 'remark',
      component: 'InputTextArea',
    },
  ];
  // 注册 form
  const [registerForm, { resetFields, setFieldsValue, validate, setProps }] = useForm({
    schemas: formSchemas,
    showActionButtonGroup: false,
  });
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    await resetFields();
    const obj = {
      ...data.record,
      creator: data.record.creatorNo,
      factory: data.record.factory ? data.record.factory.split(',').map((item) => Number(item)) : [],
      serviceEndDate: data.record.serviceEndDate ? data.record.serviceEndDate : dayjs(),
    };
    editDetail = { ...data.record };
    await setFieldsValue(obj);
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter });
  });

  const disabledDate = (current) => {
    return current < dayjs().subtract(1, 'day') || current > dayjs().add(365, 'day');
  };

  // 比较两个数据
  function arraysEqual(arr1, arr2) {
    // 先比较长度
    if (arr1.length !== arr2.length) return false;

    // 逐个比较元素
    for (let i = 0; i < arr1.length; i++) {
      if (arr2.indexOf(arr1[i]) === -1) {
        return false;
      }
    }

    return true;
  }

  // 对比表单有没有更改
  function compareForm(values) {
    const data = {
      ...values,
    };
    const editFactory = editDetail.factory ? editDetail.factory.split(',') : [];
    const valueFactory = values.factory ? values.factory.split(',') : [];
    let tag = true;
    if (!arraysEqual(editFactory, valueFactory)) {
      tag = false;
    }
    if (data.creator != editDetail.creatorNo) {
      tag = false;
    }
    if (data.remark != editDetail.remark) {
      tag = false;
    }
    if (data.serviceEndDate != editDetail.serviceEndDate) {
      tag = false;
    }
    return tag;
  }
  //表单提交事件
  async function onSubmit() {
    try {
      const values = await validate();
      const tag = compareForm(values);
      if (tag) {
        createMessage.warning('您没有修改任何内容');
        return;
      }
      setModalProps({ confirmLoading: true });
      // 提交表单
      await saveOrUpdate(values);
      //关闭弹窗
      closeModal();
      //刷新列表
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
