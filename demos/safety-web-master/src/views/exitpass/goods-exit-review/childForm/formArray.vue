<template>
  <div style="display: flex">
    <span style="width: 15%; margin-right: 3%; margin-bottom: 5px">目的厂区</span>
    <span style="width: 15%; margin-right: 3%; margin-bottom: 5px">接收人员工号</span>
    <span style="width: 15%; margin-right: 3%; margin-bottom: 5px">接收人姓名</span>
    <span style="width: 15%; margin-right: 3%; margin-bottom: 5px">接收人部门</span>
    <span style="width: 15%; margin-right: 3%; margin-bottom: 5px">接收人EIP</span>
    <span style="width: 50px" v-if="!$props.disabled">操作</span>
  </div>
  <div v-for="(param, index) in dynamicInput.params" :key="index" style="display: flex">
    <a-form-item style="width: 100%">
      <ApiSelect
        :disabled="canEdit(param)"
        label="目的厂区"
        placeholder="请选择目的厂区"
        required
        v-model:value="param.targetFactory"
        style="width: 15%; margin-right: 3%; margin-bottom: 5px"
        :api="getFactoryList"
        @change="emitChange"
      />
      <a-input
        :disabled="canEdit(param)"
        label="接收人员工号"
        placeholder="请输入接收人员工号"
        required
        v-model:value="param.receiveEmpNo"
        style="width: 15%; margin-right: 3%; margin-bottom: 5px"
        @blur="onBlur($event, index)"
      />
      <a-input
        label="接收人姓名"
        placeholder="请输入接收人姓名"
        required
        disabled
        v-model:value="param.receiveEmp"
        style="width: 15%; margin-right: 3%; margin-bottom: 5px"
      />
      <a-input
        label="接收人部门"
        placeholder="请输入接收人部门"
        required
        disabled
        v-model:value="param.receiveEmpDept"
        style="width: 15%; margin-right: 3%; margin-bottom: 5px"
      />
      <a-input
        label="接收人EIP"
        placeholder="请输入接收人EIP"
        required
        disabled
        v-model:value="param.receiveEmpEip"
        style="width: 15%; margin-right: 3%; margin-bottom: 5px"
      />
    </a-form-item>
    <a-button type="primary" style="margin-left: 10px" v-if="!canEdit(param) && $props.curNodeId === 'N75'" @click="sureChangeReceive(index)">
      确认
    </a-button>

    <MinusCircleOutlined class="dynamic-delete-button" v-if="!$props.disabled" @click="remove(param)" style="width: 50px"></MinusCircleOutlined>
  </div>
  <div>
    <a-button type="dashed" style="width: 100%" @click="add" :disabled="$props.disabled">
      <PlusOutlined />
      新增
    </a-button>
  </div>
</template>
<script lang="ts">
  import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons-vue';
  import { defineComponent, reactive, UnwrapRef, watchEffect } from 'vue';
  import { ApiSelect } from '/@/components/Form';
  import { useUserStore } from '/@/store/modules/user';
  import { getEmployeeByWorkNo, getFactoryList, changeEIPTodoWithList } from '../field.api';
  import { message } from 'ant-design-vue';

  interface Params {
    targetFactory: {};
    receiveEmpNo: string;
    receiveEmp: string;
    receiveEmpDept: string;
    receiveEmpEip: string;
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
      processId: {
        type: String,
        default: '',
      },
      flowId: {
        type: String,
        default: '',
      },
      subject: {
        type: String,
        default: '',
      },
      curNodeId: {
        type: String,
        default: '',
      },
      //update-begin---author:wangshuai ---date:20220516  for：[VUEN-1043]系统编码规则，最后一个输入框不能删除------------
      //自定义删除按钮多少才会显示
      // min: propTypes.integer.def(1),
      //update-end---author:wangshuai ---date:20220516  for：[VUEN-1043]系统编码规则，最后一个输入框不能删除--------------
    },
    emits: ['change', 'update:value'],
    setup(props, { emit }) {
      const userStore: any = useUserStore().getUserInfo;
      console.log('用户信息===', userStore);
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
          targetFactory: '',
          receiveEmpNo: '',
          receiveEmp: '',
          receiveEmpDept: '',
          receiveEmpEip: '',
        });
        emitChange();
      };

      function isSpuer() {
        return userStore.roles.map((item) => item.id).includes(2);
      }

      function receiveNotDone(value) {
        return value == null || value == undefined;
      }

      function canEdit(item) {
        if (isSpuer() && receiveNotDone(item.receiveStatus)) {
          return false;
        }
        return props.disabled;
      }

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
          dynamicInput.params = props.value;
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
      function onBlur(e, index) {
        console.log(e, index, 'index,e');
        getEmployeeByWorkNo({ workNo: e.target.value, phone: '' }).then((res) => {
          console.log(dynamicInput.params, 'dynamicInput.params');
          const obj = dynamicInput.params[index];
          const { orgName, empName, email = '' } = res || {};
          obj.receiveEmp = empName;
          obj.receiveEmpDept = orgName;
          obj.receiveEmpEip = email.split('@')[0];

          dynamicInput.params[index] = obj;
          emitChange();
        });
      }

      async function sureChangeReceive(index) {
        console.log('改变的值==', index, props.value[index]);
        let tempArr = [];
        if (typeof props.value === 'string') {
          try {
            tempArr = JSON.parse(props.value);
          } catch (e) {
            tempArr = [];
          }
        }
        if (Array.isArray(props.value)) {
          tempArr = props.value;
        }
        try {
          let params = {
            processId: props.processId,
            toEmpNo: tempArr[index].receiveEmpNo,
            toEmpEip: tempArr[index].receiveEmpEip,
            toEmpName: tempArr[index].receiveEmp,
            toEmpDept: tempArr[index].receiveEmpDept,
            toEmpPhone: tempArr[index].receivePhone,
            submitNo: userStore.workNo,
            subject: props.subject,
            receiveId: tempArr[index].id,
            flowId: props.flowId,
            fromEIPEmpNo: tempArr[index].receiveEmpNo,
          };
          await changeEIPTodoWithList({ enList: [params] });
          message.success('操作成功');
          location.reload();
        } catch (error) {
          message.error(error || '操作失败');
        }
      }

      return {
        dynamicInput,
        emitChange,
        remove,
        add,
        onBlur,
        getFactoryList,
        isSpuer,
        receiveNotDone,
        canEdit,
        sureChangeReceive,
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
