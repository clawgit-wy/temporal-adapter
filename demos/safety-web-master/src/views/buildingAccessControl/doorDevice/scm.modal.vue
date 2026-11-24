<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <div class="scm-modal">
      <BasicForm @register="registerForm">
        <template #anFactoryCode="{ model, field }">
          <a-select v-model:value="model[field]" placeholder="请选择厂区" allowClear @change="factoryChange(model, field)">
            <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
          </a-select>
        </template>

        <template #anBuildingCode="{ model, field }">
          <a-select v-model:value="model[field]" placeholder="请选择楼栋" allowClear @change="buildIngChange(model, field)">
            <a-select-option v-for="item in buildingLists" :key="item.fieldCode" :value="item.fieldCode">{{ item.fieldName }}</a-select-option>
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
  import { scmSchema } from './config';
  import { update, fieldChildTree } from './field.api';
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

  const factoryLists = ref<Array<Object>>([]);
  const buildingLists = ref<Array<Object>>([]);

  // 表单配置
  const rowId = ref('');
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, updateSchema, getFieldsValue, clearValidate }] = useForm({
    schemas: scmSchema,
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
    factoryLists.value = unref(data.factoryLists);
    buildingLists.value = unref(data.buildingLists);
    //表单赋值
    if (typeof data.record === 'object') {
      await setFieldsValue({
        ...data.record,
        anFactoryCode: data.record.anFactoryCode ? data.record.anFactoryCode * 1 : undefined,
      });
      clearValidate();
    }
  });

  // // 员工号输入框 鼠标失去焦点触发事件
  // async function onBlur(e) {
  //   let userInfo = await getEmployeeByWorkNo({ workNo: e.target.value, phone: '' });
  //   if (userInfo) {
  //     const { empName, email = '' } = userInfo;
  //     setFieldsValue({ empName });
  //     setFieldsValue({ empEip: email.split('@')[0] });
  //   }
  // }
  // 模糊筛选审核部门
  async function factoryChange(model, field) {
    const factory = factoryLists.value.find((item) => item.id === model['anFactoryCode']);
    if (factory) {
      model['anFactoryName'] = factory.fieldName;
    }
    model['anBuilding'] = '';
    model['anBuildingCode'] = '';
    console.log('厂区修改==', model, field);
    buildingLists.value = await fieldChildTree({ fieldType: model.anFactoryCode });
  }

  function buildIngChange(model, field) {
    const building = buildingLists.value.find((item) => item.fieldCode === model['anBuildingCode']);
    if (building) {
      model['anBuilding'] = building.fieldName;
    }
    console.log('楼栋修改==', model, field);
  }

  //获取标题
  const getTitle = computed(() => (!unref(isUpdate) ? '新增' : '编辑'));

  //表单提交事件
  async function handleSubmit() {
    try {
      let values = await validate();
      console.log('验证表单==', values, unref(isUpdate));
      setModalProps({ confirmLoading: true });
      values.id = unref(rowId);
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
