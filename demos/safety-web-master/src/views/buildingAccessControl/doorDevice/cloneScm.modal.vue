<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <div class="scm-modal">
      <BasicForm @register="registerForm">
        <!-- <template #cloneDeviceName="{ model, field }">
          <a-select v-model:value="model[field]" placeholder="请选择厂区" showSearch allowClear @change="factoryChange(model, field)">
            <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
          </a-select>
        </template> -->
        <template #cloneDeviceName="{ model, field }">
          <a-select
            v-model:value="model[field]"
            label-in-value
            showSearch
            allowClear
            placeholder="请选择要克隆的设备"
            style="width: 100%"
            :filter-option="false"
            :options="deviceList"
            @search="handleSearch"
          >
          </a-select>
        </template>
      </BasicForm>
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
  import { cloneScmSchema } from './config';
  import { submitCopyData, getCopyDeviceInfo } from './field.api';
  // 声明Emits
  const emit = defineEmits(['success', 'register']);
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
    schemas: cloneScmSchema,
    showActionButtonGroup: false,
  });
  // 表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    console.log('门禁设备编辑数据=======', data);

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
      await setFieldsValue({
        ...data.record,
      });
      clearValidate();
    }
  });

  //获取标题
  const getTitle = '克隆权限';

  const deviceList = ref([]);
  const handleSearch = debounce(async (val) => {
    console.log('搜索==', val);
    // fetch(val, d => (data.value = d));
    if (!val) {
      deviceList.value = [];
      return;
    }
    let res = await getCopyDeviceInfo({ deviceNameOrCode: val });
    console.log('eip角色获取信息==', res);
    deviceList.value =
      res != null && res.length > 0
        ? res.map((item) => {
            return {
              label: item.anDeviceName ? `${item.deviceCode}(${item.anDeviceName})` : item.deviceCode,
              value: item.deviceCode,
            };
          })
        : [];
    console.log('帅选项==', deviceList.value);
  }, 300);

  //表单提交事件
  async function handleSubmit() {
    try {
      let values = await validate();
      console.log('验证表单==', values, unref(isUpdate));
      setModalProps({ confirmLoading: true });
      values.id = unref(rowId);
      let params = { deviceCode: values.cloneDeviceName.value, id: values.id };
      const res = await submitCopyData(params);
      isUpdated.value = 1;
      console.log('克隆结果', res);
      createMessage.success({ content: '操作成功' });
      closeModal();
      // emit('success', { isUpdate: unref(isUpdated), values });
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
