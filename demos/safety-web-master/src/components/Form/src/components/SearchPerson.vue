<template>
  <div>
    <!-- <a-select v-bind="$attrs" :options="options" placeholder="请选择厂区" ></a-select> -->
    <a-select
      v-bind="$attrs"
      show-search
      :mode="props.mode"
      style="width: 100%"
      placeholder="请输入员工号/姓名查询选择"
      :default-active-first-option="false"
      :show-arrow="false"
      :filter-option="false"
      :not-found-content="null"
      :options="options"
      @search="(val) => handleSearch(isNaN(val) ? { empName: val } : { empNo: val })"
    >
      <template #option="{ value: val, label }"> &nbsp;&nbsp;{{ label }} （{{ val }}） </template>
    </a-select>
  </div>
</template>
<script setup lang="ts">
  import { ref, useAttrs, watchEffect } from 'vue';
  import { defHttp } from '../../../../utils/http/axios';
  const attrs = useAttrs();
  const initFlag = ref(true);
  let timer: any = null;
  const options = ref([]);

  const getList = (params) => {
    console.log(params, 'mutgetlistaetnar');
    initFlag.value = false;
    if (Array.isArray(params.empNo)) {
      params.empNo.map((item) =>
        defHttp.post({ url: '/safety/employee/list', params: { empNo: item } }).then((res) => {
          const items = res.dataList.map((item) => {
            return { ...item, label: item.empName, value: item.empNo };
          });
          options.value = [...options.value, ...items];
        })
      );
      return;
    }
    defHttp.post({ url: '/safety/employee/list', params }).then((res) => {
      options.value = res.dataList.map((item) => {
        return { ...item, label: item.empName, value: item.empNo };
      });
    });
  };
  watchEffect(() => {
    if (initFlag.value && attrs.value) {
      getList({ empNo: attrs.value });
    }
  });

  const props = defineProps({
    mode: {
      type: String,
      default: 'combobox',
    },
    valueField: {
      type: String,
      default: '',
    },
  });

  const handleSearch = (params) => {
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      getList(params);
    }, 500);
  };
</script>
