<script setup lang="ts">
import workPhase from "./components/workPhase.vue";
import {ref} from "vue";
import CheckStandard from "/@/views/firePatrol/riskLedger/components/checkStandard.vue";

const inStatus = ref(0)

</script>

<template>
  <div class="p-4 view-riskLedger">
    <div class="riskLedger-type">
      <a-radio-group v-model:value="inStatus" style="margin-top: 5px">
        <a-radio-button :value="0">作业环节</a-radio-button>
        <a-radio-button :value="1">检查标准</a-radio-button>
      </a-radio-group>
    </div>
    <div v-if="inStatus === 0">
      <!--作业环节-->
      <work-phase></work-phase>
    </div>
    <div v-else>
      <!--检查标准-->
      <check-standard></check-standard>
    </div>
  </div>
</template>

<style scoped lang="less">
.view-riskLedger {
  display: flex;
  flex-direction: column;
}

.riskLedger-type {
  display: flex;
  background-color: white;
  border-radius: 10px;
  padding: 15px;
  box-sizing: border-box;
  margin-left: 10px;
  margin-right: 10px;
}

</style>
