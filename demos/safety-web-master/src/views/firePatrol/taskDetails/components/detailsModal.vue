<template>
  <div ref="refModal">
    <BasicModal @register="registerModal" :title="title" :width="1080" v-bind="$attrs"
                :getContainer="() => $refs.refModal">
      <!--定义表格-->
      <BasicTable @register="registerTable">
        <template #processId="{ text }">
          <a style="color: #1b90ff; cursor: pointer" :href="`${eipUrl}${text}`"
             v-if="text && text.length > 0"
             target="_blank" rel="noopener noreferrer">异常流程</a>
          <span v-else>无</span>
        </template>
      </BasicTable>
    </BasicModal>
  </div>
</template>

<script lang="ts" setup>
import {ref, unref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import BasicTable from "/@/components/Table/src/BasicTable.vue";
import {useListPage} from "/@/hooks/system/useListPage";
import {taskDetailsPageList} from "/@/views/firePatrol/taskDetails/taskDetailsApi";
import {detailsModalColumns} from "/@/views/firePatrol/taskDetails/components/detailsModal.data";
import {useGlobSetting} from "/@/hooks/setting";

// 声明 emits
const emit = defineEmits(['success', 'register']);
const title = ref<string>('');
const inspectionTaskId = ref('');

const eipUrl = useGlobSetting().eipUrl

// 列表页面公共参数、方法
const {tableContext} = useListPage({
    designScope: 'basic-table-check-standard',
    tableProps: {
      title: '',
      tableSetting: {
        redo: true, // 是否显示刷新按钮
        size: true, // 是否显示尺寸调整按钮
        setting: true, // 是否显示字段调整按钮
        fullScreen: true // 是否显示全屏按钮
      },
      useSearchForm: false,
      api: taskDetailsPageList,
      columns: detailsModalColumns,
      rowKey: "id",
      showActionColumn: false,//隐藏操作列
      beforeFetch: (params) => {
        params.inspectionTaskId = inspectionTaskId.value;
        return params;
      },
      fetchSetting: {
        // 传给后台的当前页字段
        pageField: 'pageNum',
        // 传给后台的每页显示多少条的字段
        sizeField:
          'pageSize',
        // 接口返回表格数据的字段
        listField:
          'dataList',
        // 接口返回表格总数的字段
        totalField:
          'total',
      }
      ,
    },
  })
;

const [registerTable, {reload}] = tableContext;


// 注册 modal
const [registerModal, {setModalProps, closeModal}] = useModalInner(async (data) => {
  setModalProps({
    confirmLoading: false,
    showCancelBtn: !!data?.showFooter,
    showOkBtn: !!data?.showFooter,
    loading: true
  });
  title.value = unref(data.title);
  inspectionTaskId.value = data.record.id
  setModalProps({loading: false});
  reload({page: 1})
});


</script>
<style lang="less" scoped>

</style>
