<template>
  <div :class="prefixCls">
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <div style="display: flex; flex-direction: column">
          <div style="display: flex">
            <a-button type="primary" @click="allReservedAuth()">全部保留授权</a-button>
            <a-button type="primary" style="margin-left: 15px" @click="allCancleAuth()">全部取消授权</a-button>
          </div>
        </div>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>

    <div style="padding-bottom: 20px; display: flex; padding-top: 50px; width: 100%; justify-content: center">
      <a-button type="primary" @click="cancle()">取消</a-button>
      <a-button type="primary" style="margin-left: 15px" @click="sure()">确认</a-button>
    </div>
  </div>
</template>

<script lang="ts" name="system-subsystem" setup>
  //ts语法
  import { ref, unref } from 'vue';
  import { cloneDeep } from 'lodash-es';
  import { BasicTable, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { columns } from './config';
  import { getListPage, Api, batchCloseBuild, batchSaveWhite, batchDealBuild } from './field.api';
  import { useUserStore } from '/@/store/modules/user';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { useRouter, useRoute } from 'vue-router';
  import { Modal } from 'ant-design-vue';
  import { useTabs } from '/@/hooks/web/useTabs';
  const { close } = useTabs();
  const route = useRoute();
  const router = useRouter();
  // 消息
  const { createMessage } = useMessage();
  const userStore: any = useUserStore().getUserInfo;
  const openUpload = ref<boolean>(false);
  const { hasPermission } = usePermission();
  const [registerModal, { openModal }] = useModal(); // 厂区弹出框

  // 列表页面公共参数、方法
  const { prefixCls, tableContext } = useListPage({
    designScope: 'goodsOut-list',
    tableProps: {
      title: '调岗原对接门禁管理员权限确认',
      api: async (params) => {
        console.log('用户信息====', userStore);
        const res = await getListPage(Object.assign(params, { employeeCode: userStore.workNo }));
        return res;
      },
      columns: columns,
      size: 'small',
      useSearchForm: false,
      showTableSetting: false,
      pagination: {
        pageSize: 15,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      fetchSetting: {
        // 传给后台的当前页字段
        pageField: 'pageNum',
        // 传给后台的每页显示多少条的字段
        sizeField: 'pageSize',
        // 接口返回表格数据的字段
        listField: 'dataList',
        // 接口返回表格总数的字段
        totalField: 'total',
      },
      actionColumn: {
        width: 180,
        title: '操作',
        //是否锁定操作列取值 right ,left,false
        fixed: 'right',
      },
      showIndexColumn: false,
    },
  });

  //注册table数据
  const [registerTable, { reload, getDataSource }] = tableContext;

  /**
   * 列表操作栏
   */
  function getTableAction(record: EditRecordRow): ActionItem[] {
    return [
      {
        label: '保留',
        onClick: handleSingleGoingAuth.bind(null, record),
        color: record.flag ? (record.flag === '1' ? 'warning' : undefined) : undefined,
      },
      {
        label: '取消',
        onClick: handlesingleCancleAuth.bind(null, record),
        color: record.flag ? (record.flag === '0' ? 'warning' : undefined) : undefined,
      },
    ];
  }
  const doneArr = ref<Recordable>([]);
  /**
   * 编辑事件
   */
  function handleSingleGoingAuth(record: Recordable) {
    record.flag = '1';
    const findIndex = doneArr.value.findIndex((item) => item.id == record.id);
    if (findIndex != -1) {
      doneArr.value[findIndex] = record;
    } else {
      doneArr.value.push(record);
    }
  }
  function handlesingleCancleAuth(record: Recordable) {
    record.flag = '0';
    const findIndex = doneArr.value.findIndex((item) => item.id == record.id);
    if (findIndex != -1) {
      doneArr.value[findIndex] = record;
    } else {
      doneArr.value.push(record);
    }
  }

  function hasAuth(code) {
    return hasPermission(code);
  }

  function allReservedAuth() {
    if (getDataSource() != null && getDataSource().length > 0) {
      Modal.confirm({
        title: '提示',
        content: '是否全部保留授权？',
        async onOk() {
          const keepIdStr = getDataSource()
            .map((item) => item.id)
            .join(',');
          // await batchSaveWhite({ idStr });
          await batchDealBuild({ keepIdStr });
          createMessage.success('操作成功');
          reload();
        },
        onCancel() {},
      });
    } else {
      createMessage.info('暂无可操作数据');
    }
  }
  function allCancleAuth() {
    if (getDataSource() != null && getDataSource().length > 0) {
      Modal.confirm({
        title: '提示',
        content: '是否全部取消授权？',
        async onOk() {
          const closeIdStr = getDataSource()
            .map((item) => item.id)
            .join(',');
          await batchDealBuild({ closeIdStr });
          createMessage.success('操作成功');
          reload();
        },
        onCancel() {},
      });
    } else {
      createMessage.info('暂无可操作数据');
    }
  }

  function cancle() {
    doneArr.value = [];
    reload();
  }
  async function sure() {
    if (getDataSource() == null || getDataSource().length < 0) {
      createMessage.info('暂无可操作数据');
      return;
    }
    console.log('一键取消授权==', doneArr.value);
    if (doneArr.value != null && doneArr.value.length > 0) {
      const keepIdStr = doneArr.value
        .filter((item) => item.flag == 1)
        .map((item) => item.id)
        .join(',');
      const closeIdStr = doneArr.value
        .filter((item) => item.flag == 0)
        .map((item) => item.id)
        .join(',');
      await batchDealBuild({ keepIdStr, closeIdStr });
      createMessage.success('操作成功');
      cancle();
    } else {
      createMessage.info('请点击保留或取消后确认');
    }
  }
</script>

<style lang="less">
  .blacklistUpload {
    padding: 10px;
  }
</style>
