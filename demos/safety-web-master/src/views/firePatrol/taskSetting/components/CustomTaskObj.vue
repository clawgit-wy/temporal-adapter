<script setup>

import {onMounted, ref, watch} from "vue";
import {
  taskSetGetCustomTaskObject,
  taskSetGetDefaultTaskObject
} from "/@/views/firePatrol/taskSetting/taskSettingApi";

const objType = ref('0')

// 定义 props
const props = defineProps({
  /*workDeptCode*/
  workDeptCode: {
    type: String,
    require: true
  },
  riskLevel: {
    type: String,
    require: true
  }
});

const isLoading = ref(false);

// 定义 emits
const emit = defineEmits(['change']);

const customItem = ref([])

const defaultItem = ref([])

const customDataArr = ref([])

const defaultDataArr = ref([])

watch(
  () => [props.workDeptCode, props.riskLevel],
  () => {
    resetRequest(true)
  },
  {immediate: true} // 立即执行一次
);


onMounted(() => {

})

function handleOpenAdd() {
  resetRequest(true)
}

async function resetRequest(isReload) {
  isLoading.value = true
  if (props.riskLevel && props.workDeptCode && props.riskLevel.length > 0 && props.workDeptCode.length > 0) {
    if (objType.value && objType.value === '0') {
      await requestGetDefaultData('');
    } else {
      await requestGetCustomData('');
    }
  }
  if (isReload) {
    customItem.value = [];
    emit('change', {empNo: '', empName: '', taskObjectType: objType.value});
  }
  isLoading.value = false

}

async function resetTaskObj(type, empNo, empName) {
  if (isLoading.value) {
    setTimeout(() => {
      resetTaskObj(type, empNo, empName)
    }, 50)
    return
  }
  objType.value = type.toString()
  if (Number(objType.value) === 0) {
    // defaultItem.value = empNo
    emit('change', {empNo: '', empName: '', taskObjectType: objType.value});
  } else {
    let empNoArr = empNo.split(',')
    let empNameArr = empName.split(',')
    let emitArr = []
    if (empNoArr.length === empNameArr.length) {
      customDataArr.value = []
      for (let i = 0; i < empNameArr.length; i++) {
        let item = {
          value: empNoArr[i],
          label: `${empNameArr[i]}（${empNoArr[i]}）`,
          realName: empNameArr[i]
        }
        customDataArr.value.push(item)
        emitArr.push(item)
      }
    }
    emit('change', {data: emitArr, taskObjectType: objType.value})
    customItem.value = empNo.split(',')
  }
}

async function requestGetDefaultData(keyWord) {
  let res = await taskSetGetDefaultTaskObject({
    workDeptCode: props.workDeptCode,
    riskLevel: props.riskLevel,
    empNoAndName: keyWord
  })
  defaultItem.value = res.map(item => {
    return item.empNo
  })

  defaultDataArr.value = res.map(item => {
    return {
      value: item.empNo,
      label: `${item.empName}（${item.empNo}）`,
      realName: item.empName
    }
  })
}

async function requestGetCustomData(keyWord) {
  let res = await taskSetGetCustomTaskObject({
    workDeptCode: props.workDeptCode,
    riskLevel: props.riskLevel,
    empNoAndName: keyWord
  })
  customDataArr.value = res.map(item => {
    return {
      value: item.empNo,
      label: `${item.empName}（${item.empNo}）`,
      realName: item.empName
    }
  })
}

function handleDefaultChange(value, node) {
  emit('change', {data: node, taskObjectType: objType.value})
}

function handleCustomChange(value, node) {
  emit('change', {data: node, taskObjectType: objType.value})
}

function handleDefaultSearch(value) {
  if (value && value.length > 0) {
    requestGetDefaultData(value)
  }
}

function handleCustomSearch(value) {
  if (value && value.length > 0) {
    requestGetCustomData(value)
  }
}

defineExpose({resetTaskObj, handleOpenAdd})

</script>

<template>
  <div class="custom-task-obj">
    <a-radio-group v-model:value="objType" button-style="solid" @change="resetRequest">
      <a-radio-button value="0">默认</a-radio-button>
      <a-radio-button value="1">自定义</a-radio-button>
    </a-radio-group>
    <a-select
      v-if="objType === '0'"
      v-model:value="defaultItem"
      show-search
      disabled
      mode="multiple"
      style="width: 100%"
      :default-active-first-option="false"
      :show-arrow="false"
      :filter-option="false"
      :not-found-content="null"
      :options="defaultDataArr"
      @search="handleDefaultSearch"
      @change="handleDefaultChange"
    ></a-select>
    <a-select
      v-if="objType === '1'"
      v-model:value="customItem"
      show-search
      mode="multiple"
      placeholder="请选择任务对象"
      style="width: 100%"
      :default-active-first-option="false"
      :show-arrow="false"
      :filter-option="false"
      :not-found-content="null"
      :options="customDataArr"
      @search="handleCustomSearch"
      @change="handleCustomChange"
    ></a-select>
  </div>
</template>
<style scoped lang="less">
.ant-form-item-has-error .ant-radio-button-wrapper:not(:first-child)::before {
  background-color: #d9d9d9 !important;
}

.ant-form-item-has-error .ant-radio-button-wrapper {
  border-color: #d9d9d9 !important;
}

.custom-task-obj {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
