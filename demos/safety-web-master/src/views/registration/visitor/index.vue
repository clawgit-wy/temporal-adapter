<template>
  <div :class="prefixCls">
    <BasicTable @register="registerTable" :rowSelection="rowSelection" @selectionChange="selectionChange">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" @click="quitCard" v-if="hasAuth('registration:visitor:card')">退卡</a-button>
        <a-button type="primary" @click="oneSendPremiss" v-if="hasAuth('registration:visitor:onePermission')">一键权限下发</a-button>
        <!-- <a-button type="primary" @click="readCard" v-if="hasAuth('registration:visitor:card')">读卡</a-button> -->
        <a-button type="primary" preIcon="ant-design:plus-outlined" v-if="hasAuth('registration:visitor:add')" @click="onAdd">添加</a-button>
        <a-switch
          v-model:checked="status"
          checkedChildren="开"
          unCheckedChildren="关"
          style="margin-top: 5px"
          :checkedValue="0"
          :unCheckedValue="1"
          @change="open"
          v-if="hasAuth('registration:visitor:openOrClose')"
        />
        <!-- <a-button type="primary" @click="open(0)">开</a-button>
        <a-button type="primary" v-if="hasAuth('registration:visitor:openOrClose')" @click="open(1)">关</a-button> -->
      </template>
      <template #form-factoryName="{ model, field }">
        <!-- <a-input v-model:value="model[field]" /> -->
        <a-select v-model:value="model[field]" placeholder="请选择厂区" mode="multiple" allowClear>
          <a-select-option v-for="item in factoryLists" :key="item.id" :value="item.id">{{ item.fieldName }}</a-select-option>
        </a-select>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <EmpowerModal
      :sheetList="sheetList"
      :filedList="filedList"
      :readIDObj="readIDObj"
      @register="registerModal"
      @success="reload"
      @visible-change="visibleChange"
      :readIdorIC="readIdorIC"
    />
    <QuitModal @register="registerQuizModal" @success="reload" />
    <a-modal :footer="null" v-model:visible="viewIdCarOpen" title="确认">
      <p style="padding: 20px">该访客已发卡{{ viewIdCarRes.hairpinNo }}、{{ viewIdCarRes.authFactoryNames }}厂区，请确认访客是否已归还访客卡?</p>
      <p style="padding-bottom: 20px; text-align: center">
        <a-button @click="viewIdCarCancel(viewIdCarRes)" :loading="viewIdCarloading" style="margin-right: 20px">未知</a-button>
        <a-button type="primary" :loading="viewIdCarloading" @click="viewIdCarOk(viewIdCarRes)">已回收</a-button>
      </p>
    </a-modal>
  </div>
</template>

<script lang="ts" name="device-binding" setup>
  import { ref, onMounted, onBeforeUnmount } from 'vue';
  import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { useListPage } from '/@/hooks/system/useListPage';
  import EmpowerModal from './VisitorModal.vue';
  import QuitModal from './QuitModal.vue';
  import {
    list,
    detail,
    viewIdCar,
    viewHairpinNo,
    getfiledList,
    getSheetList,
    factoryList,
    saveOrUpdate,
    roleSend,
    roleCancel,
    openOrClose,
    getOenOrCloseStaus,
    sendPermissionByPersonList,
  } from './visitor.api';
  import { Modal, message } from 'ant-design-vue';
  import { save as cardrefundSave } from '/@/views/query/cardrefund/cardrefund.api';
  import { columns, searchFormSchema } from './visitor.data';
  import dayjs from 'dayjs';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import { ViewIdCarResponse } from './type.ts';
  import { watchEffect } from 'vue';
  const { createMessage } = useMessage();
  const { hasPermission } = usePermission();
  const readIDObj = ref({});
  const filedList = ref([]);
  const sheetList = ref({});
  const factoryLists = ref([]); // 厂区列表

  const viewIdCarRes = ref<ViewIdCarResponse>({});
  const viewIdCarOpen = ref<boolean>(false);
  const viewIdCarloading = ref<boolean>(false);

  let modalState = false;
  // 列表页面公共参数、方法
  const { prefixCls, tableContext } = useListPage({
    designScope: 'registration-visitor',
    tableProps: {
      title: '访客登记',
      api: list,
      columns: columns,
      rowKey: 'id',
      rowSelection: {
        type: 'checkbox',
      },
      pagination: {
        pageSize: 15,
        showSizeChanger: false,
        showQuickJumper: false,
      },
      formConfig: {
        schemas: searchFormSchema,
        showAdvancedButton: false,
      },
      showIndexColumn: true,
      ellipsis: true,
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
      beforeFetch: (params) => {
        // if (params.date) {
        //   params.startTime = dayjs(data.date).format("YYYY-MM-DD");
        //   params.endTime = dayjs
        // }
        return params;
      },
      actionColumn: {
        width: 170,
        title: '操作',
        //是否锁定操作列取值 right ,left,false
        fixed: 'right',
        dataIndex: 'action',
        slots: { customRender: 'action' },
      },
    },
  });

  // 注册 ListTable
  const [registerTable, { getForm, reload, setColumns }, { rowSelection, selectedRowKeys }] = tableContext;
  const [registerModal, { openModal }] = useModal();
  const [registerQuizModal, quitModalFun] = useModal();

  function visibleChange(params) {
    modalState = params;
  }
  const status = ref(1);
  const getOpenStatus = () => {
    getOenOrCloseStaus({ openName: '访客发卡' }).then((res) => {
      status.value = res.openOrClose;
      // if (status.value == 0) {
      //   setColumns(columns.filter(item => (item.dataIndex != 'hairpinNo' && item.dataIndex != 'recoveryNo')))
      // }else{
      //   setColumns(columns)
      // }
    });
  };
  getOpenStatus();
  const open = async () => {
    // console.log(setProps);
    try {
      await openOrClose({ openName: '访客发卡', openOrClose: status.value });
      // if (status.value == 0) {
      //   setColumns(columns.filter(item => (item.dataIndex != 'hairpinNo' && item.dataIndex != 'recoveryNo')))
      // }else{
      //   setColumns(columns)
      // }
      message.success('操作成功');
    } catch (error) {
      getOpenStatus();
    }
  };
  // 退卡流程
  async function quitCard() {
    const res = await readIdorIC();
    if (res.resultFlag === 0) {
      const data = await viewHairpinNo({ hairpinNo: res.ICCardSerial });
      if (data && (data.vendorState === 1 || data.vendorState === 8 || data.vendorState === 9)) {
        quitModalFun.openModal(true, {
          title: '访客卡回收',
          showFooter: true,
          record: { ...data, recoveryNo: data.hairpinNo },
        });
      } else {
        createMessage.warning('没有查询到数据');
      }
    }
  }
  const selectionChange = ({ keys, rows }) => console.log('列表选择==', keys, rows, selectedRowKeys.value);
  // 一键权限下发
  const oneSendPremiss = async () => {
    if (selectedRowKeys.value.length == 0) {
      return createMessage.warn('请选择数据');
    }
    try {
      // const params;
      await sendPermissionByPersonList({ ids: selectedRowKeys.value.join(',') });
      message.success('操作成功');
      selectedRowKeys.value = [];
      reload();
    } catch (error) {
      reload();
    }
  };

  function onAdd() {
    openModal(true, {
      title: '访客登记',
      isUpdate: false,
      showFooter: true,
      record: {},
    });
  }

  function timeFormat(data) {
    return {
      ...data,
      startTime: data.startTime ? dayjs(data.startTime) : '',
      endTime: data.endTime ? dayjs(data.endTime) : '',
    };
  }

  async function handleAdd(record) {
    const res = await detail({ id: record.id });
    console.log(res, '{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}');
    idCardProcessDialog(res, {});
  }

  /**
   * 操作栏
   */
  function getTableAction(record): ActionItem[] {
    let actions: any[] = [{ label: '发卡', onClick: handleAdd.bind(null, record), auth: 'registration:visitor:edit' }];
    if (status.value == 0 && record.vendorState != 3) {
      actions.unshift({ label: '注销', onClick: cancelRoles.bind(null, record), auth: 'registration:visitor:cancel' });
    }
    if ([0, 1, 2, 8, 9].indexOf(record.vendorState) > -1) {
      if (status.value == 0) {
        actions.unshift({ label: '权限下发', onClick: sendRoles.bind(null, record), auth: 'registration:visitor:send' });
      }
      // else{
      //   actions.unshift(
      //     { label: '发卡', onClick: handleAdd.bind(null, record), auth: 'registration:visitor:edit' }
      //   )
      // }
      // return [{ label: '发卡', onClick: handleAdd.bind(null, record), auth: 'registration:visitor:edit' }];
    }
    return actions;
  }
  const sendRoles = async (record) => {
    const res = await roleSend({ id: record.id });
    console.log(res, 'rreesss');

    message.success('操作成功');
    reload();
  };
  const cancelRoles = async (record) => {
    const res = await roleCancel({ id: record.id });
    message.success('操作成功');
    reload();
  };
  // 处理身份证数据
  function idCardFormat(data) {
    return {
      visitorName: data.partyName,
      visitorIdCard: data.certNumber,
    };
  }
  function readCard() {
    idCardProcess({
      visitorName: '杜燕云',
      visitorIdCard: '410526199210093785',
    });
  }
  // 读取身份证流程
  async function idCardProcess(data) {
    if (modalState) {
      readIDObj.value = { ...data };
    } else {
      // 请求接口获取身份证状态
      const res = await viewIdCar({ idCard: data.visitorIdCard });
      if (!res) {
        // 没有流程走发卡逻辑
        openModal(true, {
          title: '访客登记',
          isUpdate: true,
          showFooter: true,
          record: { ...data },
        });
      } else {
        if (res.rowCount > 1) {
          // 多个流程走选择流程逻辑 将身份证号输入搜索框并显示搜索结果
          handleSearchByIdCard(data.visitorIdCard);
          createMessage.info('该人员有多个流程，请在下方列表自己选择操作');
        } else {
          idCardProcessDialog(res, data);
        }
      }
    }
  }

  // 身份证号搜索
  function handleSearchByIdCard(value) {
    getForm().resetFields();
    getForm().setFieldsValue({ visitorIdCard: value });
    reload();
  }

  // 身份证刷卡流程
  function idCardProcessDialog(res, data) {
    if (dayjs().isAfter(res.endTime)) {
      createMessage.info('没有该访客信息');
      // 超过访问时间，相当于没有流程走发卡逻辑
      openModal(true, {
        title: '访客登记',
        isUpdate: true,
        showFooter: true,
        record: { ...data },
      });
    } else {
      if (res.vendorState === 0) {
        const tipTime = dayjs(res.startTime).subtract(30, 'm').format('YYYY-MM-DD HH:mm:ss');
        if (dayjs().isBefore(tipTime)) {
          Modal.confirm({
            width: 500,
            title: '发卡提醒',
            content: `该访客提前超过30分钟到访，请确认被访人（${res.visitedName}-${res.visitedPhone}）放行意见后再行发卡。`,
            cancelText: '暂不放行',
            okText: '被访人确认放行',
            onOk: () => {
              openModalHairpin(res);
            },
          });
        } else {
          openModalHairpin(res);
        }
        // if (res.vendorState === 0) {
        //   console.log(0);

        //   // 未发卡 是否到入厂开始时间
        //   if (dayjs().isBefore(res.startTime)) {
        //     console.log(1);

        //     const startTime = dayjs(res.startTime).format('HH:mm');
        //     handleSearchByIdCard(data.visitorIdCard)
        //     createMessage.info(`未到今日访问时间${startTime}，请耐心等待`);
        //   } else {
        //     console.log(2);

        //     openModalHairpin(res);
        //   }
      } else if (res.vendorState === 3) {
        handleSearchByIdCard(data.visitorIdCard);
        createMessage.info('流程正在审批中');
      } else if (res.vendorState === 1 || res.vendorState === 8 || res.vendorState === 9) {
        viewIdCarRes.value = res;
        viewIdCarOpen.value = true;
      } else if (res.vendorState === 2) {
        if (dayjs().isBefore(res.endTime)) {
          //已退卡（发卡有效）
          openModalHairpin(res);
        }
      }
    }
  }

  // 发卡确认弹出框--确定按钮
  async function viewIdCarOk(res) {
    // 执行退卡逻辑弹出发卡页面
    viewIdCarloading.value = true;
    await saveOrUpdate(
      {
        id: res.id,
        visitorName: res.visitorName,
        recoveryNo: res.hairpinNo,
        vendorState: 2,
      },
      true
    );
    reload();
    viewIdCarOpen.value = false;
    viewIdCarloading.value = false;
    openModalHairpin(res);
  }

  // 发卡确认弹出框--取消按钮
  async function viewIdCarCancel(res) {
    // 旧卡及访客信息录入“访客未退卡查询明细”页面，关闭旧卡及人员、二维码进出权限，弹出发卡页面，发卡后新卡ID替换旧卡ID，不显示回收卡号
    viewIdCarloading.value = true;
    await cardrefundSave({ visitorId: res.id });
    viewIdCarOpen.value = false;
    viewIdCarloading.value = false;
    openModalHairpin(res);
  }

  // 弹出发卡页面
  function openModalHairpin(data) {
    openModal(true, {
      title: '访客登记',
      isUpdate: true,
      showFooter: true,
      record: { ...timeFormat(data), hairpinNo: '' },
    });
  }

  let webSocketObject;
  const WebSocketFun = {}; // 存放websocket回调
  // 连接服务端
  function openWebSocket() {
    const url = 'ws://127.0.0.1:18890';
    if ('WebSocket' in window) {
      webSocketObject = new WebSocket(url);
    } else if ('MozWebSocket' in window) {
      console.log('MozWebSocket');
      webSocketObject = new MozWebSocket(url);
    } else {
      console.log('SockJS');
    }
    if (webSocketObject == null) {
      console.log('创建WebSocket对象失败');
    }

    //连接发生错误的回调方法
    webSocketObject.onerror = function () {
      console.log('WebSocket连接发生错误');
    };

    //连接成功建立的回调方法
    webSocketObject.onopen = function () {
      console.log('WebSocket连接成功');
      startPushInfosWithOutImg();
      //禁止重复读卡
      const jsonObj = {
        name: 'repeatRead',
        isRepeatRead: false,
      };
      const jStr = JSON.stringify(jsonObj);
      webSocketObject.send(jStr);
    };

    //接收到消息的回调方法
    webSocketObject.onmessage = function (event) {
      const data = eval('(' + event.data + ')');
      if (['PushWithOutImg'].indexOf(data.name) > -1) {
        if (data.resultFlag === 0) {
          // console.log(data, '----');
          // 处理数据
          const res = idCardFormat(data.resultContent);
          // 请求接口获取状态
          idCardProcess(res);
        }
      } else {
        const name = data.name + 'onResove';
        if (WebSocketFun[name]) {
          WebSocketFun[name](data);
          WebSocketFun[name] = null;
        }
      }
    };

    //连接关闭的回调方法
    webSocketObject.onclose = function () {
      console.log('WebSocket连接关闭');
    };
  }
  //开启推送（不带图片)
  function startPushInfosWithOutImg() {
    const jsonObj = {
      name: 'openPush',
      withImg: false,
      interval: '100',
      params: {
        enableTypeA: false,
      },
    };
    const jStr = JSON.stringify(jsonObj);
    webSocketObject.send(jStr);
  }
  // 关闭websocket
  function closeWebSocket() {
    webSocketObject.close();
  }

  // 读IC卡
  function readIdorIC() {
    const jsonObj = {
      name: 'readICCardSN',
    };
    const jStr = JSON.stringify(jsonObj);
    webSocketObject.send(jStr);
    return new Promise((resolve) => {
      WebSocketFun.readICCardSNonResove = resolve;
    });
  }
  function hasAuth(code) {
    return hasPermission(code);
  }

  async function getfiledListfun() {
    const res = await getfiledList();
    if (res) {
      filedList.value = res.visitList
        ? res.visitList.map((item) => {
            return item.fieldName;
          })
        : [];
    }
  }
  getfiledListfun();

  async function getSheetListFun() {
    const res = await getSheetList();
    if (res) {
      const list = res.filter((item) => {
        return item.takeEffect;
      });
      const obj = {};
      list.forEach((item) => {
        obj[item.code] = item;
      });
      sheetList.value = obj;
      console.log(obj, '======');
    }
  }
  getSheetListFun();

  // 访客websocket
  onMounted(() => {
    openWebSocket();
  });
  // 获取厂区列表
  async function getFactoryList() {
    const res = await factoryList({});
    factoryLists.value = res.factoryList;
  }
  getFactoryList();
  onBeforeUnmount(() => {
    webSocketObject && closeWebSocket();
    console.log('onBeforeUnmount');
  });
</script>

<style lang="less">
  @import 'index';
  .hairpinNo {
    position: relative;
    .hairpinNo1 {
      position: absolute;
      right: -5px;
      transform: translate(100%, -50%);
      top: 50%;
      cursor: pointer;
      color: #1664ff;
    }
  }
</style>
