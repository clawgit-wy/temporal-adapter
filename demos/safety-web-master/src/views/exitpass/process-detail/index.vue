<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onExportXls" v-if="hasPermission('processdetail:export')"
>导出</a-button>
      </template>
      <template #flowNo="{ text, record }">
        <span style="color: blue; cursor: pointer" @click="goInfo(record)">{{ text }}</span>
      </template>
      <template #sceneImg="{ text }">
        <TableImg v-if="text" :size="60" :simpleShow="true" :imgList="text.split(/[,;|]+/g)" />
      </template>
       <template #leaveData="{ text }">
        <span v-if="text" width="120px">
            <a-button  type="link" v-for="url in text.split(/[,;|]+/g)" :href='url' :key="url" target="_blank">
              <a-tooltip :title="url.split('/').pop()">
                <span class="ellipsis">{{url.split('/').pop()}}</span>
              </a-tooltip>
          </a-button>
        </span>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <ScmModal @register="registerScmModal" @success="handleSuccess" />
    <a-modal v-model:visible="abnormalVisible" title="原因填写" @ok="handleOk">
      <a-row style="padding: 10px">
        <a-col :span="4" style="text-align: right;"><span style="color: red;">*</span>原因：</a-col>
        <a-col :span="20">
          <a-textarea
            v-model:value="abnormalReason"
            placeholder="请输入原因"
            :auto-size="{ minRows: 2, maxRows: 5 }"
          />
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>

<script lang="ts" name="processdetail" setup>
import { cloneDeep } from 'lodash-es';
import { useRoute, useRouter } from 'vue-router';
//ts语法
import { BasicTable, TableImg, TableAction, ActionItem, EditRecordRow } from '/@/components/Table';
import ScmModal from './scm.modal.vue';
import { useListPage } from '/@/hooks/system/useListPage';
import { useMessage } from '/@/hooks/web/useMessage';
import { usePermission } from '/@/hooks/web/usePermission';
import { useUserStore } from '/@/store/modules/user';
import { addFullParam } from '/@/utils/router/fullParam';
import { useModal } from '/@/components/Modal';
import { columns, searchFormSchema } from './user.data';
import { Api, getPageList, auditProcess, rejectProcess, createAbnormal } from './user.api';
import { ref } from 'vue';
import {StatusEnum} from './const'
const route = useRoute();
const router = useRouter();
const { hasPermission } = usePermission();
const userStore: any = useUserStore().getUserInfo;

// 消息
const { createMessage } = useMessage();
// 列表页面公共参数、方法
const { tableContext, onExportXls } = useListPage({
  designScope: 'page-list',
  exportConfig: {
    url: Api.exportXls,
    name: '出门证流程详情列表',
  },
  tableProps: {
    title: '出门证流程详情',
    api: getPageList,
    columns: columns,
    size: 'small',
    showTableSetting: false,
    formConfig: {
      schemas: searchFormSchema,
    },
    actionColumn: {
      width: 180,
      title: '操作',
      //是否锁定操作列取值 right ,left,false
      fixed: 'right',
      dataIndex: 'action',
      slots: { customRender: 'action' },
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
    pagination: {
      pageSize: 15,
      showSizeChanger: true,
      showQuickJumper: true,
    },
    showIndexColumn: true,
    beforeFetch: (params) => {
      if (params.submissionTime) {
        const [createBegin, createEnd] = params.submissionTime.split(',');
        params.createBegin = createBegin;
        params.createEnd = createEnd;
        delete params.submissionTime;
      }
      return params;
    },
  },
});

//注册table数据
const [registerTable, { reload }] = tableContext;
const [registerScmModal, { openModal }] = useModal();

/**
 * 列表操作栏
 */
function getTableAction(record: EditRecordRow): ActionItem[] {
  return [
    {
      label: '审核',
      onClick: handleAudit.bind(null, record),
      ifShow: () => record.status == StatusEnum.Approving && hasPermission('processdetail:audit'),
    },
    {
      label: '驳回',
      ifShow: () =>  record.status == StatusEnum.Approving && hasPermission('processdetail:rejection'),

      onClick: handleRejection.bind(null, record),
      color: 'error',
    },
    {
      label: '流程异常',
      onClick: abnormal.bind(null, record),
      ifShow: () =>  (record.status != StatusEnum.Discard) && hasPermission('processdetail:audit'),
    },
    // {
    //   label: '重新提交',
    //   onClick: reSubmit.bind(null, record),
    //   ifShow: () =>  (record.submitNo == userStore.workNo),

    // },
  ];
}
const abnormalVisible = ref(false)
const abnormalReason = ref('')
let abnormalId = ''
const abnormal = (record) => {
  abnormalId = record.id
  abnormalVisible.value = true

}
const handleOk = () => {
  if (!abnormalReason.value) return createMessage.error('请输入原因')
  createAbnormal({
    exitPassId: abnormalId,
    exceptionType: '流程异常',
    detailedDescription: abnormalReason.value
  }).then(res => {
    createMessage.success('操作成功')
    abnormalVisible.value = false
    reload()
  })
}
// 流程审核,点击跳转审核页面
const handleAudit = async (record: EditRecordRow) => {
  // 创建流程之后关闭当前页面。
  goInfo(record);
};
// 驳回弹框填写驳回理由
const handleRejection = async (record: EditRecordRow) => {
  const { flowId, id, curNodeId } = record;
  //  1-废料销售价格确认 2-原因类别 3- 质保部审批 4- 审核人审核 5-审核人上级
  console.log(curNodeId, 'curNodeId');
  if (curNodeId && curNodeId !== 'N2') {
    const params = {
      curPersonLogin: userStore.realName,
      flowId,
      id,
      isUpdateDetail: false,
      nodeFlag: -1,
      status: '11',
      toNodeId: 'N2',
    };
    openModal(true, {
      record: cloneDeep(params),
      isUpdate: true,
      showFooter: true,
    });
  }
};
// 重新提交
const reSubmit = async (record: EditRecordRow) => {
  console.log(record);
  router.push(addFullParam({
    name: 'goodsexitpermit',
    query: {
      formInstanceId: record.id,
      type: 'reSubmit',
    },
  }));
}
/**
 * 成功回调
 * @param {number} isUpdate 操作类型：0 无操作 1 新增/新增加编辑 2 编辑
 * @param {object} values 编辑的数据
 */
function handleSuccess() {
  reload();
}
// 跳转流程详情页面
const goInfo = (record) => {
  console.log(record);
  if (record.flowUrl) {
    const hasFullParam = window.location.hash.includes('__full__=true');
    if (hasFullParam) {
      window.open(record.flowUrl+'&__full__=true', '_blank')
    } else {
      window.open(record.flowUrl, '_blank')
    }
  } else {
    router.push(addFullParam({
      name: 'goodsexitreview',
      query: {
        formInstanceId: record.id,
      },
    }));
  }
};
</script>

<style scoped>
.ellipsis{
  display: inline-block;
    width: 120px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}</style>
