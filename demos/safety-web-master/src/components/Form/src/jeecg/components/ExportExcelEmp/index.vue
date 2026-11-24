<template>
  <div>
    <div v-if="tips" class="export-excel-emp-tips">{{ tips }}</div>
    <div style="line-height: 32px" v-if="!disabled">
      <a-space>
        <a-button type="primary" @click="showPersonSelect" style="margin-bottom: 20px"> 单人选择 </a-button>
        <a-button type="primary" @click="showModal" style="margin-bottom: 20px"> 批量导入 </a-button>
      </a-space>
    </div>
    <a-table size="small" :dataSource="tempList" :columns="tempColumns" :pagination="pagination" bordered />
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <div style="width: 80%; margin: 0 auto; padding-top: 30px">
        <p class="" style="text-align: left">请按照模板规范填写，否则无法正确导入: <a-button @click="ondownLoadXls">下载模板</a-button></p>
        <p class="" style="text-align: left">
          <a-upload v-model:file-list="fileList" accept=".xls,.xlsx" :max-count="1" :customRequest="handleImport">
            <a-button> 上传 </a-button>
          </a-upload>
        </p>
      </div>
    </a-modal>
    <person-select-tree
      v-model:value="personSelectVisible"
      :selected="secretAreaManagerPersons"
      :multiple="true"
      :showRemoveButton="true"
      @ok="handlePersonSelected"
      @cancel="handleCancel"
    />
  </div>
</template>
<script lang="ts" setup>
  import { defineProps, computed, ref, defineEmits, watch } from 'vue';
  import { downloadEmpTemplate } from './excel.api';
  import { downloadByData } from '/@/utils/file/download';
  import * as XLSX from 'xlsx/xlsx.mjs';
  import { useMessage } from '/@/hooks/web/useMessage';
  import PersonSelectTree from '/@/components/PersonSelectTree/index.vue';
  const $message = useMessage();
  const fileList = ref<any[]>([]); // 上传的文件列表
  const openUpload = ref<boolean>(false);
  const personSelectVisible = ref<boolean>(false);
  const secretAreaManagerPersons = ref<any[]>([]);
  const props = defineProps({
    disabled: {
      type: Boolean,
      default: false,
    },
    value: {
      type: Array,
      default: () => [],
    },
    tips: {
      type: String,
      default: '',
    },
    columns: {
      type: Array,
      default: () => [],
      required: true,
    },
    mapDataFn: {
      type: Function,
      default: null,
    },
    showIndex: {
      type: Boolean,
      default: true,
    },
    downloadUrl: {
      type: String,
      default: '',
    },
    maxCount: {
      type: Number,
      default: 200,
    },
  });
  const tempColumns = computed(() => {
    if (props.showIndex) {
      return [{ title: '序号', dataIndex: 'index', key: 'index' }, ...props.columns];
    }
    return props.columns;
  });
  const tempList = computed(() => {
    if (props.showIndex) {
      return props.value.map((item: any, index) => {
        return { ...item, index: index + 1 };
      });
    }
    return props.value;
  });
  const pagination = ref({
    pageSize: 15,
    showTotal: (total) => `共 ${total} 条`,
  });
  // 定义事件
  const emit = defineEmits(['update:value', 'change']);
  // 显示弹出框
  function showModal() {
    openUpload.value = true;
  }
  //table数据
  const list = computed({
    // getter
    get: () => props.value,
    // setter
    set: (newValue) => {
      console.log('newValue改变', newValue);
      emit('update:value', newValue);
      emit('change', newValue);
    },
  });

  // 下载模板
  async function ondownLoadXls() {
    const res = await downloadEmpTemplate(props.downloadUrl, {});
    downloadByData(res, '人员导入模板.xlsx');
  }

  let excelDataAll: any[] = [];

  // 读取excel
  const handleImport = async (file: any) => {
    const fileReader = new FileReader();
    fileReader.readAsBinaryString(file.file);
    fileReader.onload = async (event) => {
      try {
        const data = event.target?.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const worksheet = workbook.Sheets[workbook.SheetNames[0]];
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

        // 去掉表头行
        jsonData.shift();
        console.log('***************dataArr的值是', tempColumns.value);

        // 根据columns的dataIndex动态处理数据
        let dataArr = jsonData.map((item: any[], index: number) => {
          const rowData: Record<string, any> = { index: index + 1 };
          // 遍历所有列定义
          tempColumns.value.forEach((column: any, colIndex: number) => {
            if (props.showIndex) {
              if (column.dataIndex && column.dataIndex !== 'index') {
                // Excel中的数据索引可能与列定义顺序不同，这里假设Excel表格列顺序与columns定义顺序一致
                rowData[column.dataIndex] = item[colIndex - 1];
              }
            } else {
              rowData[column.dataIndex] = item[colIndex];
            }
          });

          return rowData;
        });
        console.log('dataArr的值是', dataArr);
        if (dataArr.length > props.maxCount) {
          $message.createMessage.error(`导入数量超过最大限制${props.maxCount}条`);
          return;
        }
        if (props.mapDataFn) {
          dataArr = await Promise.resolve(props.mapDataFn(dataArr));
        }
        console.log(dataArr, 'dataArrdataArr');
        excelDataAll = removeDuplicateEmployees([...dataArr, ...list.value]);
      } catch (error) {
        console.error('文件处理错误:', error);
        $message.createMessage.error('文件上传失败');
      }
    };
    //判断是否要自己处理数据

    fileList.value = fileList.value.map((i: any) => {
      i.status = 'done';
      return i;
    });
    return;
  };

  // 弹出框点击确定
  function handleOk() {
    openUpload.value = false;
    list.value = excelDataAll;
    fileList.value = [];
  }
  //根据员工号去重
  function removeDuplicateEmployees(employees) {
    const seenEmployeeCodes = new Set();

    return employees.filter(function (employee) {
      if (!seenEmployeeCodes.has(employee.employeeCode)) {
        seenEmployeeCodes.add(employee.employeeCode);
        return true;
      }
      return false;
    });
  }
  function showPersonSelect() {
    personSelectVisible.value = true;
    secretAreaManagerPersons.value = removeDuplicateEmployees(tempList.value).map((item) => {
      return {
        name: item.employeeName,
        id: item.employeeCode,
      };
    });
  }
  async function handlePersonSelected(persons: TreePersonData[]) {
    console.log('persons', persons);
    const dataArr = persons.map((item) => {
      return {
        employeeCode: item.id,
      };
    });
    if (props.mapDataFn) {
      list.value = removeDuplicateEmployees([...(await Promise.resolve(props.mapDataFn(dataArr)))]);
    }
  }
  function handleCancel() {
    personSelectVisible.value = false;
  }
</script>
<style lang="less" scoped>
  .export-excel-emp-tips {
    font-size: 14px;
    line-height: 1.6;
    border: 1px solid #ff4d4f;
    border-radius: 4px;
    padding: 10px;
    background-color: #fff1f0;
    color: #1890ff;
    margin-bottom: 10px;
  }
</style>
