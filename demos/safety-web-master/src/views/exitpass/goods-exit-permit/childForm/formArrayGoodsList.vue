<template>
  <div style="display: flex">
    <span style="width: 5%; margin-right: 3%; margin-bottom: 5px">序号</span>
    <span style="width: 17%; margin-right: 2%; margin-bottom: 5px">物资编号(若无，请填写无)</span>
    <span style="width: 12%; margin-right: 2%; margin-bottom: 5px">物资名称</span>
    <span style="width: 12%; margin-right: 3%; margin-bottom: 5px">单位(计量单位)</span>
    <span style="width: 12%; margin-right: 2%; margin-bottom: 5px">数量</span>
    <span style="width: 12%; margin-right: 5%; margin-bottom: 5px">备注</span>
    <span style="width: 125px; text-align: right" v-if="!$props.disabled">操作</span>
  </div>
  <div v-for="(param, index) in dynamicInput.params" :key="index" style="display: flex">
    <a-form-item style="width: 100%">
      <div style="width: 5%; margin-right: 3%; margin-bottom: 5px; display: inline-block">{{ index + 1 }}</div>
      <a-input
        :disabled="$props.disabled"
        placeholder="请输入物资编号"
        required
        v-model:value="param.goodsCode"
        style="width: 17%; margin-right: 3%; margin-bottom: 5px"
        @blur="onBlur()"
      />
      <a-input
        :disabled="$props.disabled"
        placeholder="请输入物资名称"
        required
        v-model:value="param.goodsName"
        style="width: 12%; margin-right: 3%; margin-bottom: 5px"
        @blur="onBlur()"
      />
      <a-input
        label="接收人姓名"
        placeholder="请输入单位(计量单位)"
        required
        v-model:value="param.goodsUnit"
        style="width: 12%; margin-right: 3%; margin-bottom: 5px"
        @blur="onBlur()"
      />
      <a-input-number
        placeholder="请输入数量"
        required
        v-model:value="param.goodsNum"
        style="width: 12%; margin-right: 3%; margin-bottom: 5px"
        @blur="onBlur()"
      />
      <a-input
        placeholder="请输入备注"
        required
        v-model:value="param.remark"
        style="width: 12%; margin-right: 3%; margin-bottom: 5px"
        @blur="onBlur()"
      />
    </a-form-item>
    <div style="width: 50px">
      <MinusCircleOutlined class="dynamic-delete-button" v-if="!$props.disabled && index != 0" @click="remove(param)"></MinusCircleOutlined>
    </div>
  </div>
  <div>
    <a-button type="dashed" style="width: 100%" @click="add" :disabled="$props.disabled">
      <PlusOutlined />
      新增
    </a-button>
    <a-button type="dashed" style="width: 100%" @click="showModal" :disabled="$props.disabled">
      <PlusOutlined />
      导入
    </a-button>
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <div>
        <p class="pl-12">请按照模板规范填写，否则无法正确导入:<a-button type="link" @click="ondownLoadXls">下载模板</a-button></p>
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
</template>
<script lang="ts">
  import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons-vue';
  import { defineComponent, reactive, ref, UnwrapRef, watchEffect } from 'vue';
  import { ApiSelect } from '/@/components/Form';
  import * as XLSX from 'xlsx/xlsx.mjs';
  import { getEmployeeByWorkNo, getFactoryList, download } from '../field.api';
  import { message } from 'ant-design-vue';
  import { downloadByData } from '/@/utils/file/download';

  interface Params {
    goodsCode: string;
    goodsName: string;
    goodsUnit: string;
    goodsNum: string;
    remark: string;
  }

  export default defineComponent({
    name: 'FormArray',
    props: {
      value: {
        type: Array<Params>,
        default: () => [],
      },
      disabled: {
        type: Boolean,
        default: false,
      },
      link: {
        type: String,
        default: 'http://ekp.yutong.com/ekp/sys/attachment/sys_att_main/sysAttMain.do?method=download&fdId=17c1123c6b19933e770d62d4878865c0',
      },
      //update-begin---author:wangshuai ---date:20220516  for：[VUEN-1043]系统编码规则，最后一个输入框不能删除------------
      //自定义删除按钮多少才会显示
      // min: propTypes.integer.def(1),
      //update-end---author:wangshuai ---date:20220516  for：[VUEN-1043]系统编码规则，最后一个输入框不能删除--------------
    },
    emits: ['change', 'update:value'],
    setup(props, { emit }) {
      async function ondownLoadXls() {
        const res = await download({});
        downloadByData(res, '物资出厂信息导入模板.xlsx');
      }
      //input动态数据
      const dynamicInput: UnwrapRef<{ params: Params[] }> = reactive({ params: [] });
      //删除Input
      const remove = (item: Params) => {
        let index = dynamicInput.params.indexOf(item);
        if (index !== -1) {
          dynamicInput.params.splice(index, 1);
        }
        emitChange();
      };
      //新增Input
      const add = () => {
        dynamicInput.params.push({
          goodsCode: '',
          goodsName: '',
          goodsUnit: '',
          goodsNum: '',
          remark: '',
        });
        emitChange();
      };

      //监听传入数据value
      watchEffect(() => {
        initVal();
      });

      /**
       * 初始化数值
       */
      function initVal() {
        if (!props.value) {
          dynamicInput.params = [];
          add();
          return;
        }
        if (typeof props.value === 'string') {
          try {
            dynamicInput.params = JSON.parse(props.value);
          } catch (e) {
            dynamicInput.params = [];
          }
        }
        if (Array.isArray(props.value)) {
          if (props.value.length == 0) {
            add();
          } else {
            dynamicInput.params = props.value;
          }
        }
      }
      /**
       * 数值改变
       */
      function emitChange() {
        emit('change', JSON.stringify(dynamicInput.params));
        emit('update:value', JSON.stringify(dynamicInput.params));
      }

      // 出门人员工号失焦查询员工信息
      function onBlur() {
        emitChange();
      }
      const openUpload = ref(false);
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
        dynamicInput.params = [...exportData.value];
        exportData.value = [];
        fileList.value = [];
        emitChange();
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
                goodsCode: item[0],
                goodsName: item[1],
                goodsUnit: item[2],
                goodsNum: item[3],
                remark: item[4],
              };
            });
            exportData.value = dataArr;
          } catch {
            message.error('文件上传失败');
          }
        };
        fileList.value.map((i) => {
          i.status = 'done';
          return i;
        });

        return;
      };
      return {
        showModal,
        openUpload,
        dynamicInput,
        emitChange,
        remove,
        add,
        onBlur,
        getFactoryList,
        handleOk,
        fileList,
        handleImport,
        ondownLoadXls,
      };
    },
    components: {
      MinusCircleOutlined,
      PlusOutlined,
      ApiSelect,
    },
  });
</script>
<style scoped>
  .dynamic-delete-button {
    cursor: pointer;
    position: relative;
    top: 4px;
    font-size: 24px;
    color: #999;
    transition: all 0.3s;
  }

  .dynamic-delete-button:hover {
    color: #777;
  }

  .dynamic-delete-button[disabled] {
    cursor: not-allowed;
    opacity: 0.5;
  }
</style>
