<template>
  <div class="door-access-card">
    <div class="door-card-header">
      <span class="header-title"></span>
      <div class="header-actions">
        <a-button type="primary" size="small" @click="addAction" class="add-btn"></a-button>
        <a-button v-if="showRemove" type="primary" danger ghost size="small" @click="removeAction" class="remove-btn"> 删除 </a-button>
        <a-button v-if="!isView" type="primary" ghost size="small" @click="copyAction"> 复制 </a-button>
      </div>
    </div>
    <div class="door-card-content">
      <BasicForm @register="registerForm">
        <!-- 组织、人员 -->
        <template #empowerAreaName="{ model, field, schema }">
          <a-input readOnly v-model:value="model[field]" @click="choseDept" v-if="model['empowerType'] == 2" :disabled="isView" />
          <div style="display: flex; align-items: center" v-else>
            <a-form-item-rest>
              <person-select-input
                v-model:personList="secretAreaManagerPersons"
                :placeholder="'请选择人员'"
                :multiple="true"
                :disabled="isView"
                :showNameNo="true"
                :maxItem="5"
              />
            </a-form-item-rest>
            <a-button type="primary" ghost size="small" @click="showModal" style="margin-left: 8px" v-if="!isView"> 批量导入 </a-button>
          </div>
        </template>
        <!-- 日期 -->
        <template #powerTime="{ model, field }">
          <a-range-picker
            v-model:value="model[field]"
            v-model:open="datePickerOpen"
            :showToday="false"
            @change="handleDateRangeChange"
            :disabled="[true, isView]"
            :disabledDate="disabledDate"
          >
            <template #renderExtraFooter>
              <div style="text-align: center">
                <a-button type="link" @click="handleLongTermClick(model, field)">长期</a-button>
              </div>
            </template>
          </a-range-picker>
        </template>
        <!-- 门禁 -->
        <template #doorAccess>
          <DoorAccessInput :item="item" :multiple="true" placeholder="请选择门禁" @confirm="confirmAction" :disabled="true" />
        </template>
        <!-- 公共区域 -->
        <template #doorAccessSelection>
          <div class="door-access-selection" :class="{ disabled: isView }">
            <a-tag v-for="tag in item.deviceDOList" :key="tag.key" :closable="!isView" @close.prevent="removeDevice(tag)">{{ tag.deviceName }}</a-tag>
          </div>
        </template>
      </BasicForm>

      <DepartmentSelect v-model:visible="deptVisible" @change="deptChange"></DepartmentSelect>

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
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted, watch } from 'vue';
  import { BasicForm, FormSchema, useForm } from '/@/components/Form';
  import DoorAccessInput from '/@/components/DoorAccessSelect/DoorAccessInput.vue';
  import { listWithSecret } from './accessRequestForm.api';
  import DepartmentSelect from '/@/components/DepartmentSelect.vue';
  import PersonSelectInput from '/@/components/PersonSelectTree/PersonSelectInput.vue';
  import dayjs from 'dayjs';
  import type { UploadChangeParam } from 'ant-design-vue';
  import * as XLSX from 'xlsx/xlsx.mjs';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { downloadByData } from '/@/utils/file/download';
  import { downloadEmpTemplate } from './accessRequestForm.api';
  import { useAccessRequestForm } from '../accessRequestProcess/form/useAccessRequest';

  // 禁用日期
  const disabledDate = (current) => {
    return (current && current < dayjs().startOf('day')) || current > dayjs('2099-12-31');
  };
  const { isView } = useAccessRequestForm();

  function buildingFun(labelName): FormSchema {
    return {
      field: 'empowerAreaName',
      label: labelName,
      component: 'Select',
      required: true,
      slot: 'empowerAreaName',
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请选择',
      },
    };
  }

  // 定义表单schema
  const schemas: FormSchema[] = [
    {
      field: 'empowerType',
      label: '类型',
      component: 'Select',
      required: true,
      colProps: { span: 24 },
      defaultValue: '2',
      componentProps: ({ formActionType, formModel }) => {
        return {
          placeholder: '请选择',
          options: [
            { label: '按人员', value: '1' },
            { label: '按组织', value: '2' },
          ],
          onChange: (val) => {
            const empowerAreaName = buildingFun(val == 1 ? '人员名单' : '授权范围');
            formActionType.updateSchema([empowerAreaName]);
            formModel['empowerAreaName'] = undefined;
            formModel['empowerAreaCode'] = undefined;
            emit('selectedType', val);
          },
        };
      },
    },

    {
      field: 'empowerAreaName',
      label: '授权范围',
      component: 'Select',
      required: true,
      slot: 'empowerAreaName',
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请选择',
      },
    },

    {
      field: 'empowerAreaCode',
      label: '授权范围',
      component: 'Select',
      required: true,
      show: false,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请选择对接部门',
      },
    },
    {
      field: 'powerTime',
      label: '开通期限',
      component: 'RangePicker',
      slot: 'powerTime',
      required: true,
      colProps: { span: 24 },
      componentProps: {
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
        valueType: 'array',
      },
    },
    {
      field: 'deviceCode',
      label: '开通门禁',
      component: 'Input',
      slot: 'doorAccess',
      required: true,
      colProps: { span: 24 },
    },
    {
      field: 'deviceName',
      label: '开通门禁',
      component: 'Input',
      required: true,
      show: false,
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
      field: 'acrossEmployeeCode',
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
      field: 'topSecretEmployeeCode',
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
    },
  ];

  // 关闭 定义表单schema
  const closeSchemas: FormSchema[] = [
    {
      field: 'empowerType',
      label: '类型',
      component: 'Select',
      required: true,
      colProps: { span: 24 },
      defaultValue: '2',
      componentProps: ({ formActionType, formModel }) => {
        return {
          placeholder: '请选择',
          options: [
            { label: '按人员', value: '1' },
            { label: '按组织', value: '2' },
          ],
          onChange: (val) => {
            const empowerAreaName = buildingFun(val == 1 ? '人员名单' : '授权范围');
            formActionType.updateSchema([empowerAreaName]);
            formModel['empowerAreaName'] = undefined;
            formModel['empowerAreaCode'] = undefined;
            emit('selectedType', val);
          },
        };
      },
    },

    {
      field: 'empowerAreaName',
      label: '授权范围',
      component: 'Select',
      required: true,
      slot: 'empowerAreaName',
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请选择',
      },
    },

    {
      field: 'empowerAreaCode',
      label: '授权范围',
      component: 'Select',
      required: true,
      show: false,
      colProps: { span: 24 },
      componentProps: {
        placeholder: '请选择对接部门',
      },
    },

    {
      field: 'deviceCode',
      label: '开通门禁',
      component: 'Input',
      slot: 'doorAccess',
      required: true,
      colProps: { span: 24 },
    },
    {
      field: 'deviceName',
      label: '开通门禁',
      component: 'Input',
      required: true,
      show: false,
      colProps: { span: 24 },
    },
  ];

  // 属性定义
  const props = defineProps({
    showRemove: {
      type: Boolean,
      default: true,
    },
    showCopy: {
      type: Boolean,
      default: true,
    },
    item: {
      type: Object,
      default: () => {},
    },
    open: {
      type: Number,
      default: 0,
    },
    isDetail: {
      type: Boolean,
      default: false,
    },
  });

  // 定义事件
  const emit = defineEmits([
    'add',
    'copy',
    'remove',
    'select',
    'update:modelValue',
    'selectedAccess',
    'detailLoaded',
    'selectedType',
    'selectedDept',
    'selectedDockingDept',
    'selectedPeople',
    'selectedDate',
  ]);

  // 内部状态
  const detailLoaded = ref(false);

  // 注册表单
  const [registerForm, { validate, setFieldsValue, updateSchema }] = useForm({
    labelWidth: 150,
    schemas: props.open == 2 ? closeSchemas : schemas,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    disabled: isView.value,
    // fieldMapToTime: [
    //   // 正确格式：[单个源字段, [目标开始字段, 目标结束字段], 日期格式]
    //   ['powerTime', ['powerBeginTime', 'powerEndTime'], 'YYYY-MM-DD'],
    // ],
  });

  // 暴露验证方法给父组件
  defineExpose({
    validate,
  });
  onMounted(() => {
    console.log('当前item====', props.item);
    if (props.open == 1) {
      listWithSecret(props.item.deviceCode).then((res) => {
        console.log('门禁详情===', res);
        if (props.item.isNewAdd) {
          emit('detailLoaded', {
            ...res,
            empowerAreaCode: props.item.empowerAreaCode,
            empowerAreaName: props.item.empowerAreaName,
            empowerType: props.item.empowerType,
          });
        }
        const powerList = res.powerList;
        // dockingDeptCode
        if (powerList && powerList.length > 0) {
          // 更新Select组件的选项
          const dockingDeptCodeField = schemas.find((schema) => schema.field === 'dockingDeptCode');
          if (dockingDeptCodeField) {
            if (powerList.length == 1) {
              setSecretDept(powerList[0]);
              emit('selectedDockingDept', powerList[0]);
            }
            updateSchema([
              {
                field: 'dockingDeptCode',
                componentProps: {
                  options: powerList,
                  fieldNames: {
                    label: 'dutyDeptName',
                    value: 'dutyDeptCode',
                  },
                  onChange: (value) => {
                    const powerItem = powerList.find((item) => item.dutyDeptCode === value);
                    console.log('powerItem77777777777777', powerItem.enList);
                    if (powerItem) {
                      setSecretDept(powerItem);
                      emit('selectedDockingDept', powerItem);
                    }
                    console.log('选择部门', value);
                    setFieldsValue({});
                  },
                },
              },
            ]);
          }
          // 标记详情已加载
          detailLoaded.value = true;
        }
        //复制复显
        if (!props.item.isNewAdd) {
          if (powerList && powerList.length > 0) {
            const powerItem = powerList.find((item) => item.dutyDeptCode === props.item.dockingDeptCode);
            if (powerItem) {
              setSecretDept(powerItem);
              setFieldsValue({
                dockingDeptCode: props.item.dockingDeptCode,
              });
            }
          }
          if (props.item.powerBeginTime && props.item.powerEndTime) {
            setFieldsValue({
              powerTime: [dayjs(props.item.powerBeginTime).format('YYYY-MM-DD'), dayjs(props.item.powerEndTime).format('YYYY-MM-DD')],
            });
          }
        }
        if (props.item.empowerAreaCode) {
          if (props.item.empowerType == 1) {
            secretAreaManagerPersons.value = props.item.empowerAreaName.split(';').map((name, index) => {
              return {
                id: props.item.empowerAreaCode.split(';')[index],
                name: name,
              };
            });
          }
          setFieldsValue({
            empowerAreaCode: props.item.empowerAreaCode,
            empowerAreaName: props.item.empowerAreaName,
          });
        }
        if (props.item.empowerType != null || props.item.empowerType != undefined) {
          setFieldsValue({
            empowerType: props.item.empowerType,
          });
        }

        setFieldsValue({
          deviceCode: res.deviceCode,
          deviceName: res.deviceName,
          targetDept: powerList?.length == 1 ? powerList[0].dutyDeptCode : undefined,
          dockingDeptName: powerList?.length == 1 ? powerList[0].dutyDeptName : undefined,
          topSecretEmployeeLoginView: `${res.topSecretEmpName || ''}${res.topSecretEmpCode || ''}`,
          topSecretEmployeeLogin: res.topSecretEmpLogin || '',
          topSecretEmployeeName: res.topSecretEmpName || '',
          topSecretEmployeeCode: res.topSecretEmpCode || '',
        });

        //复显详情加载
        if (props.isDetail) {
          const {
            powerBeginTime = '',
            powerEndTime = '',
            acrossEmployeeLogin = '',
            acrossEmployeeName = '',
            acrossEmployeeCode = '',
            topSecretEmployeeLogin = '',
            topSecretEmployeeName = '',
            topSecretEmployeeCode = '',
            publicDeviceCode = '',
            publicDeviceName = '',
            empowerAreaName = '',
            empowerAreaCode = '',
            empowerType = '',
            dockingDeptCode = '',
            dockingDeptName = '',
          } = props.item;
          secretAreaManagerPersons.value = empowerAreaName.split(';').map((name, index) => {
            return {
              id: empowerAreaCode.split(';')[index],
              name: name,
            };
          });
          emit('selectedPeople', secretAreaManagerPersons.value);

          const tempAcrossEmployeeCode = ref('');
          if (powerList && powerList.length > 0) {
            const powerItem = powerList.find((item) => item.dutyDeptCode === props.item.dockingDeptCode);
            if (powerItem) {
              tempAcrossEmployeeCode.value = handelEnListItem(powerItem.enList)
                .map((item) => item.acrossEmployeeCode)
                .join(',');
            }
          }
          props.item.deviceDOList = publicDeviceName
            ? publicDeviceName.split(',').map((name, index) => {
                return {
                  deviceCode: publicDeviceCode.split(',')[index],
                  deviceName: name,
                };
              })
            : [];
          setFieldsValue({
            ...props.item,
            powerTime: [powerBeginTime, powerEndTime],
            acrossEmployeeNameView: `${acrossEmployeeName}${tempAcrossEmployeeCode.value}`,
            topSecretEmployeeLoginView: `${topSecretEmployeeName}${topSecretEmployeeCode}`,
          });
        }
      });
    }
    // const door = res.powerList.map((item) => item.deviceName).join(',');
    if (props.open == 2) {
      // if (props.item.isNewAdd) {
      //   emit('detailLoaded', props.item);
      // }

      if (props.item.empowerAreaCode) {
        if (props.item.empowerType == 1) {
          secretAreaManagerPersons.value = props.item.empowerAreaName.split(';').map((name, index) => {
            return {
              id: props.item.empowerAreaCode.split(';')[index],
              name: name,
            };
          });
        }
        setFieldsValue({
          empowerAreaCode: props.item.empowerAreaCode,
          empowerAreaName: props.item.empowerAreaName,
        });
      }
      if (props.item.empowerType != null || props.item.empowerType != undefined) {
        setFieldsValue({
          empowerType: props.item.empowerType,
        });
      }
      setFieldsValue({
        deviceCode: props.item.deviceCode,
        deviceName: props.item.deviceName,
      });

      //复显详情加载
      if (props.isDetail) {
        const {
          powerBeginTime = '',
          powerEndTime = '',
          acrossEmployeeLogin = '',
          acrossEmployeeName = '',
          acrossEmployeeCode = '',
          topSecretEmployeeLogin = '',
          topSecretEmployeeName = '',
          topSecretEmployeeCode = '',
          publicDeviceCode = '',
          publicDeviceName = '',
          empowerAreaName = '',
          empowerAreaCode = '',
          empowerType = '',
          dockingDeptCode = '',
          dockingDeptName = '',
        } = props.item;

        emit('selectedDept', { namePath: empowerAreaName, codePath: empowerAreaCode });
        emit('selectedDockingDept', { dutyDeptName: dockingDeptName, dutyDeptCode: dockingDeptCode });
        setFieldsValue({
          ...props.item,
        });
      }
    }
  });

  function setSecretDept(powerItem) {
    setFieldsValue({
      acrossEmployeeLogin: handelEnListItem(powerItem.enList)
        .map((item) => item.acrossEmployeeLogin)
        .join(','),
      acrossEmployeeName: handelEnListItem(powerItem.enList)
        .map((item) => item.acrossEmployeeName)
        .join(','),
      acrossEmployeeCode: handelEnListItem(powerItem.enList)
        .map((item) => item.acrossEmployeeCode)
        .join(','),
      acrossEmployeeNameView: handelEnListItem(powerItem.enList)
        .map((item) => `${item.acrossEmployeeNameView}`)
        .join('/'),
      dockingDeptName: powerItem.dutyDeptName,
    });
  }

  //组织选择
  const deptVisible = ref(false);
  const choseDept = () => {
    deptVisible.value = true;
  };
  const deptChange = (depart) => {
    console.log('组织选择==', depart);
    emit('selectedDept', depart);
    setFieldsValue({
      empowerAreaName: depart.namePath,
      empowerAreaCode: depart.codePath,
    });
    deptVisible.value = false;
  };

  // 人员
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

    const leaderEmployeeName = persons.map((p) => p.name).join(';');
    const leaderEmployeeCode = persons.map((p) => p.id).join(';');

    setFieldsValue({
      empowerAreaName: leaderEmployeeName,
      empowerAreaCode: leaderEmployeeCode,
    });
    emit('selectedPeople', persons);
  };

  //人员  导入操作
  const $message = useMessage();
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
    emit('selectedPeople', exportData.value);
    exportData.value = [];
    fileList.value = [];
  }

  const handleImport = async (file) => {
    const fileReader = new FileReader();
    fileReader.readAsBinaryString(file.file);
    fileReader.onload = (event) => {
      try {
        const data = event.target?.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const worksheet = workbook.Sheets[workbook.SheetNames[0]];
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
        jsonData.shift();
        const dataArr = jsonData.map((item) => {
          return {
            name: item[1],
            id: item[0],
          };
        });
        exportData.value = dataArr;
      } catch {
        $message.createMessage.error('文件上传失败');
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

  //日期
  const dateRange = ref([]); // 日期范围
  const datePickerOpen = ref(false); // 控制日期选择器面板的显示状态

  // 处理日期范围变化
  function handleDateRangeChange(dates) {
    console.log('日期变化==', dates);
    if (dates && dates.length === 2) {
      emit('selectedDate', dates);
      // setFieldsValue({
      //   powerTime: [dates[0], dates[1]],
      //   // powerBeginTime: dates[0],
      //   // powerEndTime: dates[1],
      // });
    } else {
      emit('selectedDate', undefined);
      // setFieldsValue({
      //   powerBeginTime: undefined,
      //   powerEndTime: undefined,
      // });
    }
  }
  // 处理点击长期按钮
  function handleLongTermClick(model, field) {
    // 设置长期日期（今天到2099-12-31）
    // model[field] = [dayjs(), dayjs('2099-12-31')];
    setFieldsValue({
      powerTime: [dayjs(), dayjs('2099-12-31')],
    });
    emit('selectedDate', [dayjs(), dayjs('2099-12-31')]);
    // 直接关闭日期选择器面板
    datePickerOpen.value = false;
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
  // 复制
  const copyAction = () => {
    console.log('复制');
    emit('copy');
  };
  function removeDevice(tag) {
    // 使用findIndex方法根据tag.key查找匹配的元素
    const index = props.item.deviceDOList.findIndex((item) => item.key === tag.key);
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
        acrossEmployeeCode: item.employeeCode,
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
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 16px;
      background-color: #f5f7fa;
      border-bottom: 1px solid #e8e8e8;
      border-radius: 8px 8px 0 0;

      .header-title {
        font-weight: 500;
        color: #000000d9;
      }

      .header-actions {
        display: flex;
        gap: 8px;
        .add-btn {
        }
        .remove-btn {
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
