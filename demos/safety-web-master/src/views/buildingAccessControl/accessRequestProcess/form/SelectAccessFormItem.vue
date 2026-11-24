<template>
  <div class="door-access-card">
    <div class="door-card-header">
      <span class="header-title"></span>
      <div class="header-actions">
        <span class="refuse-reason" :class="{ 'refuse-reason-pass': item.applyStatus == '1' }">{{ item.refuseReason }}</span>
        <!-- <a-button type="primary" size="small" @click="addAction" class="add-btn"></a-button>    -->
        <a-button v-if="showRemove" type="primary" danger ghost size="small" @click="removeAction" class="remove-btn"> 删除 </a-button>
      </div>
    </div>
    <div class="door-card-content">
      <BasicForm @register="registerForm">
        <template #doorAccess>
          <DoorAccessInput :item="item" :multiple="true" disabled placeholder="请选择门禁" @confirm="confirmAction" />
        </template>
        <template #doorAccessSelection>
          <div class="door-access-selection" :class="{ disabled: isView }">
            <a-tag v-for="tag in item.deviceDOList" :key="tag.deviceCode" :closable="!isView" @close.prevent="removeDevice(tag)">{{
              tag.deviceName
            }}</a-tag>
          </div>
        </template>
      </BasicForm>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted, computed } from 'vue';
  import { BasicForm, FormSchema, useForm } from '/@/components/Form';
  import DoorAccessInput from '/@/components/DoorAccessSelect/DoorAccessInput.vue';
  import { listWithSecret } from './accessRequestForm.api';
  import { useAccessRequestForm } from './useAccessRequest';
  // 定义表单schema
  const schemas: FormSchema[] = [
    {
      field: 'deviceCode',
      label: '开通门禁',
      component: 'Input',
      slot: 'doorAccess',
      required: true,
      colProps: { span: 24 },
    },
    {
      field: 'dockingDeptCode',
      label: '对接部门',
      component: 'Select',
      required: true,
      componentProps: {
        placeholder: '请选择对接部门',
      },
      colProps: { span: 24 },
    },
    {
      field: 'dockingDeptName',
      label: '对接部门',
      component: 'Select',
      show: false,
    },
    {
      field: 'acrossEmployeeNameView',
      label: '对接部门门禁管理员',
      component: 'Input',
      componentProps: {
        placeholder: '对接部门门禁管理员',
        disabled: true,
      },
      colProps: { span: 24 },
    },
    {
      field: 'acrossEmployeeName',
      label: '对接部门门禁管理员姓名',
      component: 'Input',
      show: false,
    },
    {
      field: 'acrossEmployeeLogin',
      label: '对接部门门禁管理员姓名',
      component: 'Input',
      show: false,
    },
    {
      field: 'topSecretEmployeeLoginView',
      label: '绝密区负责人',
      component: 'Input',
      componentProps: {
        placeholder: '绝密区负责人',
        disabled: true,
      },
      colProps: { span: 24 },
    },
    {
      field: 'topSecretEmployeeName',
      label: '绝密区负责人',
      component: 'Input',
      show: false,
    },
    {
      field: 'topSecretEmployeeLogin',
      label: '绝密区负责人',
      component: 'Input',
      show: false,
    },
    {
      field: 'doorAccessSelection',
      label: '对应开通公共区域门禁',
      component: 'Input',
      slot: 'doorAccessSelection',
      colProps: { span: 24 },
      show: true,
    },
  ];

  // 属性定义
  const props = defineProps({
    showRemove: {
      type: Boolean,
      default: true,
    },
    item: {
      type: Object,
      default: () => {},
    },
  });
  const { isView } = useAccessRequestForm(props);
  const isDetail = computed(() => {
    console.log('item的值是', props?.item);
    return props?.item?.isDetail || false;
  });
  // 定义事件
  const emit = defineEmits(['add', 'remove', 'select', 'update:modelValue', 'selectedAccess', 'detailLoaded']);

  // 内部状态
  const detailLoaded = ref(false);

  // 注册表单
  const [registerForm, { validate, setFieldsValue, updateSchema }] = useForm({
    labelWidth: 150,
    schemas,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    compact: true,
    disabled: isView.value,
  });

  // 暴露验证方法给父组件
  defineExpose({
    validate,
  });
  onMounted(async () => {
    const res = await listWithSecret(props.item.deviceCode);
    if (isDetail.value) {
      console.log('是否是详情', props.item.deviceDOList);
      // res.deviceDOList = props.item.deviceDOList;
      props.item.deviceDOList = res.deviceDOList;
    }
    const powerList = res.powerList;
    if (powerList && powerList.length > 0) {
      // 更新Select组件的选项
      const dockingDeptCodeField = schemas.find((schema) => schema.field === 'dockingDeptCode');
      if (dockingDeptCodeField) {
        await updateSchema([
          {
            field: 'dockingDeptCode',
            componentProps: {
              options: powerList,
              fieldNames: {
                label: 'dutyDeptName',
                value: 'dutyDeptCode',
              },
              onChange: (value) => {
                console.log('改变了************************', value);
                const powerItem = powerList.find((item) => item.dutyDeptCode === value);
                console.log('powerItem77777777777777', powerItem.enList);
                if (powerItem) {
                  setSecretDept(powerItem);
                }
                console.log('选择部门', value);
              },
            },
          },
        ]);
        if (isDetail.value) {
          const powerItem = powerList.find((item) => item.dutyDeptCode === props.item.dockingDeptCode);
          await setSecretDept(powerItem);
        } else {
          if (powerList?.length == 1) {
            await setSecretDept(powerList[0]);
          }
        }
      }
      // 标记详情已加载
      detailLoaded.value = true;
    }
    console.log('获取的值是', powerList[0].dutyDeptName);
    const formData = {
      deviceCode: res.deviceCode,
      topSecretEmployeeLoginView: `${res.topSecretEmpName || ''}${res.topSecretEmpCode || ''}`,
      topSecretEmployeeLogin: res.topSecretEmpLogin || '',
      topSecretEmployeeName: res.topSecretEmpName || '',
    };
    console.log('是否是详情', props.item);
    if (isDetail.value) {
      formData.dockingDeptCode = props.item.dockingDeptCode;
    } else {
      formData.dockingDeptName = powerList?.length == 1 ? powerList[0].dutyDeptName : undefined;
      formData.dockingDeptCode = powerList?.length == 1 ? powerList[0].dutyDeptCode : undefined;
    }
    await setFieldsValue(formData);
    // setFieldsValue({
    //   deviceCode: res.deviceCode,
    //   dockingDeptName: powerList?.length == 1 ? powerList[0].dutyDeptName : undefined,
    //   dockingDeptCode: powerList?.length == 1 ? powerList[0].dutyDeptCode : undefined,
    //   topSecretEmployeeLoginView: `${res.topSecretEmpName || ''}${res.topSecretEmpCode || ''}`,
    //   topSecretEmployeeLogin: res.topSecretEmpLogin || '',
    //   topSecretEmployeeName: res.topSecretEmpName || '',
    // });
    emit('detailLoaded', {
      applyStatus: props.item.applyStatus,
      refuseReason: props.item.refuseReason,
      ...res,
    });
  });
  //给部门责任人对接部门门禁管理员赋值
  async function setSecretDept(powerItem) {
    console.log('h', powerItem);
    let tempForm = {
      acrossEmployeeLogin: handelEnListItem(powerItem.enList)
        .map((item) => item.acrossEmployeeLogin)
        .join(','),
      acrossEmployeeName: handelEnListItem(powerItem.enList)
        .map((item) => item.acrossEmployeeName)
        .join(','),
      acrossEmployeeNameView: handelEnListItem(powerItem.enList)
        .map((item) => `${item.acrossEmployeeNameView}`)
        .join('/'),
      dockingDeptName: powerItem.dutyDeptName,
    } as any;
    if (isDetail.value) {
      tempForm.dockingDeptCode = props.item.dockingDeptCode;
    }
    await setFieldsValue(tempForm);
  }
  // 更新门禁列表
  const confirmAction = (doors) => {
    console.log('doors########################', doors);
    emit('selectedAccess', doors);
  };

  // 增加
  const addAction = () => {
    console.log('增加');
    emit('add');
  };

  // 删除
  const removeAction = () => {
    console.log('删除');
    emit('remove');
  };
  function removeDevice(tag) {
    // 使用findIndex方法根据tag.deviceCode查找对应的门禁项
    const index = props.item.deviceDOList.findIndex((item) => item.deviceCode === tag.deviceCode);
    if (index !== -1) {
      props.item.deviceDOList.splice(index, 1);
    }
  }
  function handelEnListItem(list) {
    console.log('list', list);

    list = list.map((item) => {
      return {
        acrossEmployeeLogin: item.employeeLogin,
        acrossEmployeeName: item.employeeName,
        acrossEmployeeNameView: `${item.employeeName}${item.employeeCode}`,
      };
    });
    console.log('后list', list);

    return list;
  }
</script>

<style lang="less" scoped>
  .door-access-card {
    border: 1px solid #e8e8e8;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

    .door-card-header {
      box-sizing: border-box;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 16px;
      background-color: #f5f7fa;
      border-bottom: 1px solid #e8e8e8;
      border-radius: 8px 8px 0 0;
      min-height: 45px;

      .header-title {
        font-weight: 500;
        color: #000000d9;
      }

      .header-actions {
        box-sizing: border-box;
        display: flex;
        gap: 8px;
        .add-btn {
        }
        .remove-btn {
        }
        .refuse-reason {
          color: red;
          &.refuse-reason-pass {
            color: #1890ff;
          }
        }
        .add-btn,
        .remove-btn {
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 4px;
        }
      }
    }

    .door-card-content {
      padding: 20px;
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

      .select-btn-row {
        margin-bottom: 16px;
      }

      .contact-info-row {
        display: flex;
        margin-bottom: 16px;
        border-radius: 4px;
        padding: 8px 12px;

        .contact-item {
          flex: 1;
          display: flex;
          align-items: center;

          .contact-label {
            color: #606266;
            padding-right: 8px;
            white-space: nowrap;
          }

          .contact-value {
            flex: 1;
          }
        }
      }
    }
  }
</style>
