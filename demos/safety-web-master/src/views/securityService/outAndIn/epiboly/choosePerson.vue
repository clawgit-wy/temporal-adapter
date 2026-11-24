<template>
  <div class="systemd-noticemessage">
    <BasicTable title="双击选择" @row-dbClick="dbClick" @register="registerTable"> </BasicTable>
  </div>
</template>
<script setup lang="ts">
  import { BasicTable } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { findPerson, validProcess } from './list.api';
  import { onMounted } from 'vue';
  import { useUserStore } from '/@/store/modules/user';
  import { message } from 'ant-design-vue';
  const { getUserInfo } = useUserStore();
  const props = defineProps({
    dept: {
      type: Boolean,
      default: false,
    },
    checkRole: {
      type: Boolean,
      default: false,
    },
  });
  onMounted(() => {
    console.log(props.dept);
  });
  // 列表页面公共参数、方法
  const { tableContext } = useListPage({
    designScope: 'checkCar-list',
    tableProps: {
      immediate: false,
      title: '双击选择',
      api: findPerson,
      columns: [
        { title: '姓名', dataIndex: 'empName', key: 'empName' },
        { title: '工号', dataIndex: 'empNo', key: 'empNo' },
        { title: '部门', dataIndex: 'empOrg', key: 'empOrg' },
      ],
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: [{ label: '姓名/工号', field: 'empStr', component: 'Input' }],
      },
      showIndexColumn: true,
      ellipsis: true,
      async beforeFetch(params) {
        delete params.column;
        delete params.order;
        delete params.pageNo;
        delete params.pageSize;
        if (props.dept) {
          params['type'] = 'dept';
        }
        return params;
      },
      showActionColumn: false,
    },
  });
  const emit = defineEmits<{
    (e: 'add', data: Object): void;
  }>();
  const dbClick = async (record) => {
    // if (props.checkRole) {
    //   const res = await validProcess({
    //     curEmpNo: getUserInfo.workNo,
    //     chooseEmpNo: record.empNo
    //   })
    //   if (res === false) {
    //     message.warning(`您不是${record.empName}的对接人，无权限申请！`)
    //     return
    //   }
    // }

    emit('add', {
      empNo: record.empNo,
      empName: record.empName,
      org: record.empOrg,
      email: record.email ? record.email : '',
      factory: record.factory ? record.factory.split(',') : [],
      factoryId: record.factory ? record.factory.split(',').map((item) => Number(item)) : [],
      serviceEndDate: record.serviceEndDate ? record.serviceEndDate : '',
    });
  };
  // 注册 ListTable
  const [registerTable, { reload }] = tableContext;
</script>
<style lang="less" scoped>
  :deep(.jeecg-basic-title) {
    color: red !important;
  }
</style>
