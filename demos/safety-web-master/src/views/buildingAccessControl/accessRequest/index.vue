<template>
  <div class="access-request"></div>
</template>

<script lang="ts">
  import { defineComponent } from 'vue';
  import { useUserStore } from '/@/store/modules/user';
  import { Spin } from 'ant-design-vue';
  import { viewProcessMain, checkBuildRoleType } from '/@/views/buildingAccessControl/accessRequestProcess/accessRequest.api.ts';

  export default defineComponent({
    name: 'BuildingAccessControlAccessRequest',
    components: {
      Spin,
    },
    beforeRouteEnter(to, _from, next) {
      next(async (vm) => {
        console.log('*****************', to.query);
        let query = {};
        let { roleType, formInstanceId, bpmFlowId, processId } = to.query;
        if (!formInstanceId && !roleType) {
          const res = await checkBuildRoleType({});
          if (res == '2' || res == '3') {
            //跳转到非员工申请页面
            vm.$router.replace({
              path: 'accessRequestProcess/chargeAndManagerProcess',
              query: to.query.__full__ === 'true' ? { __full__: 'true' } : null, // 保留原始query参数
            });
          } else {
            //跳转到员工提交页面
            vm.$router.replace({
              path: '/accessRequestProcess/form',
            });
          }
          return;
        }

        if (!roleType) {
          const res = await viewProcessMain(to.query.formInstanceId);
          console.log('*****************', res);
          roleType = res.roleType;
          query = { formInstanceId: formInstanceId, bpmFlowId: processId, roleType, type: 'view' };
          to.query.__full__ === 'true' ? query['__full__']= true  : null
        } else {
          query = { ...to.query };
          to.query.__full__ === 'true' ? query['__full__']= true  : null
        }
        // 根据工号判断是否为员工
        if (roleType != '1') {
          // 非员工，跳转到非员工申请页面
          vm.$router.replace({
            path: 'accessRequestProcess/chargeAndManagerProcess',
            query: query, // 保留原始query参数
          });
        } else {
          // 是员工，跳转到员工申请页面
          vm.$router.replace({
            path: '/accessRequestProcess/form',
            query: query, // 保留原始query参数
          });
        }
      });
    },
  });
</script>

<style lang="less" scoped>
  .access-request {
  }
</style>
