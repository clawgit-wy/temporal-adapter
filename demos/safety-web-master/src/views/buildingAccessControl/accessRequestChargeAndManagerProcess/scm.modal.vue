<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="新增" @ok="handleSubmit">
    <div class="scm-modal">
      <BasicForm @register="registerForm">
        <!-- 门禁 -->
        <template #doorAccessSelection="{ model, field, schema }">
          <div class="door-access-selection" @click="chooseDoor">
            <a-tag v-for="tag in deviceDOList" :key="tag.deviceCode" closable @close.prevent="removeDevice(tag.deviceCode)">{{
              tag.deviceName
            }}</a-tag>
          </div>
        </template>

        <template #empowerAreaName="{ model, field, schema }">
          <a-input readOnly v-model:value="model[field]" @click="choseDept" v-if="model['empowerType'] == 2" />
          <div style="display: flex; align-items: center" v-else>
            <a-form-item-rest>
              <person-select-input
                v-model:personList="secretAreaManagerPersons"
                :placeholder="'请选择人员'"
                :multiple="true"
                :showNameNo="true"
                :maxItem="5"
                :isSameDept="true"
              />
            </a-form-item-rest>
            <a-button type="primary" ghost size="small" @click="showModal" style="margin-left: 8px"> 批量导入 </a-button>
          </div>
        </template>
      </BasicForm>
    </div>
  </BasicModal>

  <DoorAccessSelect v-model:value="showDoorSelect" placeholder="请选择门禁" @ok="selectedDoorAction" />

  <DepartmentSelect v-model:visible="departVisible" @change="departChange" :isSameDept="true"></DepartmentSelect>

  <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
    <div>
      <p class="pl-12">请按照模板规范填写，否则无法正确导入:<a-button @click="ondownLoadXls">下载模板</a-button></p>
      <p class="p-12">
        <a-upload v-model:file-list="fileList" accept=".xls,.xlsx" :max-count="1" :customRequest="handleImport">
          <a-button>
            <upload-outlined></upload-outlined>
            上传
          </a-button>
        </a-upload>
      </p>
    </div>
  </a-modal>
</template>
<script lang="ts" setup name="ScmModal">
  import { ref, reactive, computed, unref, watch } from 'vue';
  import { debounce } from 'lodash-es';
  import { AutoComplete } from 'ant-design-vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form/index';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useUserStore } from '/@/store/modules/user';
  import DoorAccessSelect from '/@/components/DoorAccessSelect/index.vue';
  import DepartmentSelect from '/@/components/DepartmentSelect.vue';
  import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';
  import { scmSchema } from './accessRequestForm.data';
  import * as XLSX from 'xlsx/xlsx.mjs';
  import { downloadByData } from '/@/utils/file/download';
  import { downloadEmpTemplate, classPeople, buildConfigQuery } from './accessRequestForm.api';
  // 声明Emits
  const emit = defineEmits(['success']);
  // const isUpdate = ref(true); // 新增/编辑
  // const isUpdated = ref(0); // 操作表示：0 无操作 1 新增/新增加编辑 2 编辑
  // 页面消息
  const { createMessage } = useMessage();
  // 用户信息
  const username = ref('');
  const userStore = useUserStore();
  username.value = userStore.getUserInfo.username;

  const buildGrantNum = ref(0); //门禁一次性授权人数
  async function buildConfigQuerys() {
    let res = await buildConfigQuery({});
    console.log('授权配置信息==', res);
    buildGrantNum.value = res.buildGrantNum;
  }
  buildConfigQuerys();

  // 表单配置
  // const rowId = ref('');
  const [registerForm, { resetFields, setFieldsValue, clearValidate, validate, setProps, updateSchema, getFieldsValue }] = useForm({
    schemas: scmSchema,
    showActionButtonGroup: false,
  });
  // 表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    console.log('this modal data', data);
    // 分别设置编辑状态和新增表单状态的字段内容

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
    await setFieldsValue({
      doorAccessSelection: undefined,
      empowerAreaName: undefined,
      empowerAreaCode: undefined,
    });
    // 清除验证状态
    clearValidate();
    deviceDOList.value = [];
    secretAreaManagerPersons.value = [];
    // isUpdate.value = !!data?.isUpdate;
    // if (unref(isUpdate)) {
    //   rowId.value = data.record.id;
    // }
    // //表单赋值
    // if (typeof data.record === 'object') {
    // await setFieldsValue({
    //   ...data.record,
    // });
    // // 清除验证状态
    // clearValidate();
    // }
  });
  //门禁选择
  const showDoorSelect = ref(false);
  const deviceDOList = ref([]);
  function chooseDoor() {
    showDoorSelect.value = true;
  }

  // 选中门禁
  function selectedDoorAction(doors) {
    doors = doors.map((door) => {
      return {
        deviceCode: door.key,
        deviceName: door.title,
      };
    });
    deviceDOList.value = doors;
    setFieldsValue({
      doorAccessSelection: doors,
    });
  }

  function removeDevice(deviceCode) {
    // 使用findIndex方法根据tag.key查找匹配的元素
    const index = deviceDOList.findIndex((item) => item.deviceCode === deviceCode);
    if (index !== -1) {
      deviceDOList.splice(index, 1);
    }
  }

  //部门选择
  const departVisible = ref(false);
  function choseDept() {
    departVisible.value = true;
  }
  const departChange = (depart) => {
    console.log('组织选择==', depart);
    setFieldsValue({
      empowerAreaName: depart.namePath,
      empowerAreaCode: depart.codePath,
    });
    departVisible.value = false;
  };

  // 人员
  const secretAreaManagerPersons = ref<any[]>([]);
  // 监听人员列表变化
  watch(
    () => secretAreaManagerPersons.value,
    (persons) => {
      // 更新表单中的人员 topSecretEmpCode  topSecretEmpLogin topSecretEmpName
      updateSecretManagerIds(persons);
      if (persons != null && persons.length > buildGrantNum.value) {
        createMessage.info(`一次性授权人数不能超过${buildGrantNum.value}人`);
      }
    },
    { deep: true }
  );
  // 更新表单中的人员 topSecretEmpCode  topSecretEmpLogin topSecretEmpName
  const updateSecretManagerIds = (persons: any[]) => {
    // 将所有ID以逗号分隔存入表单

    const leaderEmployeeName = persons.map((p) => p.name).join(';');
    const leaderEmployeeCode = persons.map((p) => p.id).join(';');

    setFieldsValue({
      empowerAreaName: leaderEmployeeName,
      empowerAreaCode: leaderEmployeeCode,
    });
  };

  //人员  导入操作
  const openUpload = ref<boolean>(false);
  const fileList = ref([]);
  const exportData = ref<Array<Record<string, any>>>([]);
  /**
   * 上传弹出框
   */
  function showModal() {
    openUpload.value = true;
  }

  function handleOk() {
    openUpload.value = false;
    const leaderEmployeeName = exportData.value.map((p) => p.name).join(';');
    const leaderEmployeeCode = exportData.value.map((p) => p.id).join(';');
    secretAreaManagerPersons.value = exportData.value;
    setFieldsValue({
      empowerAreaName: leaderEmployeeName,
      empowerAreaCode: leaderEmployeeCode,
    });
    exportData.value = [];
    fileList.value = [];
  }

  const handleImport = async (file) => {
    const fileReader = new FileReader();
    fileReader.readAsBinaryString(file.file);
    fileReader.onload = async (event) => {
      try {
        const data = event.target?.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const worksheet = workbook.Sheets[workbook.SheetNames[0]];
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        jsonData.shift();
        // const dataArr = jsonData.map((item) => {
        //   return {
        //     name: item[1],
        //     id: item[0],
        //   };
        // });
        const idArrStr = jsonData.map((item) => item[0]).join(',');
        const res = await classPeople({ userCodeList: idArrStr });
        exportData.value =
          res.belongList != null && res.belongList.length > 0
            ? res.belongList.map((item) => {
                return {
                  ...item,
                  name: item.empName,
                  id: item.empNo,
                };
              })
            : [];
        if (res.notBelongList != null && res.notBelongList.length > 0) {
          const notBelongStr = res.notBelongList.map((item) => `${item.empName}${item.empNo}`).join(',');
          createMessage.info(`${notBelongStr}不属于该部门员工`);
        }
      } catch {
        createMessage.error('文件上传失败');
      }
    };
    fileList.value.map((i) => {
      i.status = 'done';
      return i;
    });

    return;
  };

  async function ondownLoadXls() {
    const res = await downloadEmpTemplate({});
    downloadByData(res, '人员导入模板.xlsx');
  }

  // //表单提交事件
  async function handleSubmit() {
    try {
      let values = await validate();
      console.log('验证表单', values);
      setModalProps({ confirmLoading: true });
      console.log('编辑应用成功');
      closeModal();
      emit('success', { values });
      await resetFields();
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
    .door-access-selection {
      border: 1px solid #d9d9d9;
      border-radius: 2px;
      background-color: #fff;
      padding: 5px 11px;
      min-height: 34px;
      &.disabled {
        background-color: #f5f5f5;
        cursor: not-allowed;
        border-color: #d9d9d9;
      }
    }
  }
</style>
