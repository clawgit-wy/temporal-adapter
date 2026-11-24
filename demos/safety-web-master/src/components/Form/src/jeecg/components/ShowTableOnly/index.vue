<template>
  <div>
    <a-table 
      size="small" 
      :dataSource="value" 
      :columns="mergedColumns" 
      :pagination="pagination" 
      v-if="value && value.length > 0" 
      bordered
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.slots && column.slots.customRender">
          <slot :name="column.slots.customRender" :record="record" :column="column"></slot>
        </template>
      </template>
    </a-table>
    <div style="line-height: 32px;" v-else>无</div>
  </div>
</template>
<script lang="ts" setup>
  import { defineProps, computed } from 'vue';
  
  const props = defineProps({
    value: {
      type: Array,
      default: () => [],
    },
    columns: {
      type: Array,
      default: () => {
        return [
          {
            title: '厂区名称',
            dataIndex: 'factoryName',
            key: 'factoryName',
          },
          {
            title: '授权期限',
            dataIndex: 'powerBeginTime',
            key: 'powerTime',
            slots: { customRender: 'powerTime' },
          },
        ];
      },
    },
    pagination: {
      type: Object,
      default: () => {
        return {
          pageSize: 15,
          showTotal: (total) => `共 ${total} 条`,
        }
      }
    }
  });

  // 合并默认列配置和自定义列配置
  const mergedColumns = computed(() => {
    // 如果传入了自定义列，则使用自定义列
    return props.columns;
  });
</script>
