<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit">
    <div class="scm-modal">
      <BasicForm @register="registerForm">
        <template #empNo="{ model, field }">
          <!-- 如果是组件需要进行双向绑定，model当前表单对象，field当前字段名称  -->
          <a-input v-model:value="model[field]" placeholder="请输入员工号" required @blur="onBlur" />
        </template>
        <template #empDept="{ model, field }">
          <!-- 如果是组件需要进行双向绑定，model当前表单对象，field当前字段名称  -->
          <AutoComplete
            v-model:value="model[field]"
            :options="deptOptions"
            style="width: 100%"
            placeholder="请输入审核部门"
            @search="handleDeptChange"
          />
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
import { save, update, getEmployeeByWorkNo, getGroupTree } from './field.api';
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
const [registerForm, { resetFields, setFieldsValue, validate, setProps, updateSchema, getFieldsValue }] = useForm({
  schemas: scmSchema,
  showActionButtonGroup: false,
});
// 表单赋值
const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
  console.log('this modal data', data);
  // 分别设置编辑状态和新增表单状态的字段内容
  if (data.isUpdate) {
    updateSchema([
      {
        field: 'empNo',
        componentProps: {
          disabled: true,
        },
      },
    ]);
  }
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

// 员工号输入框 鼠标失去焦点触发事件
async function onBlur(e) {
  let userInfo = await getEmployeeByWorkNo({ workNo: e.target.value, phone: '' });
  if (userInfo) {
    const { empName, email = '' } = userInfo;
    setFieldsValue({ empName });
    setFieldsValue({ empEip: email.split('@')[0] });
  }
}
// 模糊筛选审核部门
const deptOptions = ref([]);
let timeoutId = null as any;
const handleDeptChange = (value) => {
  if (!value) return;
  if (timeoutId) clearTimeout(timeoutId);
  timeoutId = setTimeout(() => {
    // 使用防抖函数
    deptSearch(value);
  }, 500);
};
const deptSearch = async (value = '') => {
  const list = await getGroupTree(value);
  console.log(list, 'list=======');
  const optionList = (list || []).slice(0, 50);
  deptOptions.value = optionList.map((item) => {
    return {
      value: item.name,
      label: item.name,
    };
  });
};

//获取标题
const getTitle = computed(() => (!unref(isUpdate) ? '新增' : '编辑'));

//表单提交事件
async function handleSubmit() {
  try {
    let values = await validate();
    console.log('验证表单', values);
    setModalProps({ confirmLoading: true });
    //提交表单
    if (unref(isUpdate)) {
      values.id = unref(rowId);
      await save(values, unref(isUpdate));
      // 2 由于表单获取的是ids, 无法回显到列表，统一刷新列表，不再进行行更新
      isUpdated.value = 1;
      console.log('编辑应用成功');
    } else {
      delete values.id;
      await update(values, unref(isUpdate));
      isUpdated.value = 1;
      console.log('新增应用成功');
    }
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