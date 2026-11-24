<template>
  <div class="department-select-wrapper">
    <div class="input-like-box" @click="handleOpen">
      <div v-if="getValues.length > 0" class="selected-value">
        {{ getValues[0].title || getValues[0].name }}
      </div>
      <div v-else class="placeholder">请选择</div>
    </div>
    <DepartmentSelect 
      v-model:visible="visible" 
      :isSameDept="isSameDept" 
      @change="handleChange"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch } from 'vue';
import DepartmentSelect from '/@/components/DepartmentSelect.vue';
import AIcon from '/@/components/jeecg/AIcon.vue';

export default defineComponent({
  name: 'DepartmentSelectWrapper',
  components: {
    DepartmentSelect,
    AIcon,
  },
  props: {
    value: {
      type: [String, Object, Array],
      default: () => null,
    },
    showValue: {
      type: Boolean,
      default: true,
    },
    isSameDept: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['change', 'update:value'],
  setup(props, { emit }) {
    const visible = ref(false);
    const selectedValues = ref<any[]>([]);

    // 计算显示的值
    const getValues = computed(() => {
      if (!props.value) return [];
      
      if (Array.isArray(props.value)) {
        return props.value;
      } else if (typeof props.value === 'object') {
        return [props.value];
      } else {
        return [];
      }
    });

    // 监听外部值变化
    watch(
      () => props.value,
      (val) => {
        if (val) {
          selectedValues.value = Array.isArray(val) ? val : [val];
        } else {
          selectedValues.value = [];
        }
      },
      { immediate: true }
    );

    // 打开选择器对话框
    const handleOpen = () => {
      visible.value = true;
    };

    // 处理选择结果
    const handleChange = (item) => {
      const result = item;
      selectedValues.value = [result];
      emit('update:value', result);
      emit('change', result);
      visible.value = false;
    };

    // 清空选择
    const handleClear = () => {
      selectedValues.value = [];
      emit('update:value', null);
      emit('change', null);
    };

    return {
      visible,
      selectedValues,
      getValues,
      handleOpen,
      handleChange,
      handleClear,
    };
  },
});
</script>

<style lang="less" scoped>
.department-select-wrapper {
  position: relative;
  width: 100%;
}

.input-like-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid #d9d9d9;
  border-radius: 2px;
  padding: 4px 11px;
  min-height: 32px;
  width: 100%;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #40a9ff;
  }
}

.selected-value {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.placeholder {
  color: #bfbfbf;
  flex: 1;
}

.actions {
  display: flex;
  align-items: center;
}

.clear-icon {
  font-size: 12px;
  color: #bfbfbf;
  margin-right: 8px;
  
  &:hover {
    color: #595959;
  }
}

.arrow-icon {
  font-size: 12px;
  color: #bfbfbf;
}
</style>
