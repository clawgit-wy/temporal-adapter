<template>
    <div>
        <a-select v-bind="$attrs" :options="options" placeholder="请选择厂区" ></a-select>
    </div>
</template>
<script setup lang="ts">
import { ref} from 'vue';

import { defHttp } from '../../../../utils/http/axios';

const options = ref([])

defHttp.post({url: '/safety/retention/laneFactory/getFactoryList'}).then(res => {
  options.value = res.map(key => ({label: key, value: key}))
})

</script>