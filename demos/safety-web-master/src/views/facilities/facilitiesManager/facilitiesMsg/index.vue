<template>
  <div class="systemd-noticemessage">
    <BasicTable
      title="提示信息"
      @register="registerTable"
      :rowSelection="rowSelection"
    >
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="showTemporar(0)" v-if="hasAuth('facilities:facilitiesmanager:facilitiermsg:temporarily')">一键应用临时点检人</a-button>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="showTemporar(1)" v-if="hasAuth('facilities:facilitiesmanager:facilitiermsg:temporarily')">一键应用点检人</a-button>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="showTemporar(1)" v-if="hasAuth('facilities:facilitiesmanager:facilitiermsg:apply')">一键应用点检人</a-button>
        <a-button type="primary" preIcon="ant-design:minus-outlined" @click="showCancleTemporar()" v-if="hasAuth('facilities:facilitiesmanager:facilitiermsg:canceltemporarily')">一键取消临时点检人</a-button>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="onAdd" v-if="hasAuth('facilities:facilitiesmanager:facilitiermsg:add')">添加</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onDelete(selectedRowKeys)" v-if="hasAuth('facilities:facilitiesmanager:facilitiermsg:deletes')">批量删除</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="downQrcode()" v-if="hasAuth('facilities:facilitiesmanager:facilitiermsg:downQrcodes')">批量下载二维码</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="openUpload = true" v-if="hasAuth('facilities:facilitiesmanager:facilitiermsg:impord')">批量导入</a-button>
        <a-button type="primary" preIcon="ant-design:import-outlined" @click="onExportXls()" v-if="hasAuth('facilities:facilitiesmanager:facilitiermsg:expord')">导出</a-button>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
    </BasicTable>
    <listModal @register="registerModal" @success="reload" />
    <a-modal v-model:visible="temporarilyVisible" :title="!temporarilyType ? '设置临时点检人':'设置点检人'" @ok="submitSetEmp" width="800px">
      <ul class="temp-box">
        <li>
          <p>{{!temporarilyType ? '设置临时点检人':'设置点检人'}}：</p>
          <SearchPerson mode="multiple" style="width: 500px" @change="personChange"></SearchPerson>
        </li>
        <li v-if="!temporarilyType">
          <p>{{!temporarilyType ? '临时' : ''}}点检人应用范围：</p>
          <a-range-picker v-model:value="applyTime" value-format="YYYY-MM-DD"></a-range-picker>
        </li>
      </ul>
    </a-modal>
    <a-modal v-model:visible="openUpload" title="提示" @ok="handleOk">
      <div class="blacklistUpload">
        <p>请按照模板规范填写，否则无法正确导入<a-button @click="ondownLoadXls">下载模板</a-button></p>
        <p>
          <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls">上传</j-upload-button>
        </p>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" name="facilities-msg" setup>
import { ActionItem, BasicTable, TableAction } from '/@/components/Table';
import { useListPage } from '/@/hooks/system/useListPage';
import { usePermission } from '/@/hooks/web/usePermission';
import listModal from './listModal.vue';
import { list, remove, download, setEmp, bathSetEmp, Api, cancelEmp } from './list.api';
import { columns,searchFormSchema, } from './list.data';
import { useModal } from '/@/components/Modal';
import { ref } from 'vue';
import { downloadByData } from '/@/utils/file/download';
import SearchPerson from '/@/components/Form/src/components/SearchPerson.vue'
import { useMessage } from '/@/hooks/web/useMessage';
import { toDataURL } from 'qrcode'
import { useGlobSetting } from '/@/hooks/setting';
import jszip from 'jszip'
import FileSaver from 'file-saver'

const globSetting = useGlobSetting();
const { createMessage } = useMessage()
const [registerModal, { openModal }] = useModal(); // 厂区弹出框
const { identifier } = globSetting

// 列表页面公共参数、方法
const { tableContext, onImportXls, onExportXls } = useListPage({
  designScope: 'checkCar-list',
  tableProps: {
    title: '滞留提醒通知列表',
    api: list,
    columns: columns,
    pagination: {
      pageSize: 15,
      showSizeChanger: true,
      showQuickJumper: false,
    },
    formConfig: {
      schemas: searchFormSchema,
      fieldMapToTime: [['date', ['proTemBeginDate', 'proTemEndDate'], 'YYYY-MM-DD']],
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
      width: 180,
      title: '操作',
      //是否锁定操作列取值 right ,left,false
      fixed: 'right',
      dataIndex: 'action',
      slots: { customRender: 'action' },
    },
  },
  exportConfig: {
    url: Api.exportXls,
    name: '设备信息管理列表',
  },
  importConfig: {
    url: Api.importXls,
    success: () => reload(),
  },
});
  // 注册 ListTable
const [registerTable, { reload }, {rowSelection, selectedRows, selectedRowKeys}] = tableContext;
const { hasPermission } = usePermission();
 function hasAuth(code) {
    return hasPermission(code);
  }


function onEdit(record) {
  const ruleIds = record.ruleIds ? record.ruleIds.split(',').map(item => Number(item)) : []
  const deviceFieldCode = Number(record.deviceFieldCode)
  const checkEmpNos = record.checkEmpNo ? record.checkEmpNo.split(',') : []
  // changeFieldParams({fieldType: record.deviceFieldCode, topFlag: false})
  openModal(true, {
    title: '修改',
    isUpdate: true,
    record: {...record, ruleIds, deviceFieldCode, checkEmpNo: checkEmpNos},
    showFooter: true,
  });
}

function onAdd() {
  openModal(true, {
    title: '添加',
    isUpdate: false,
    showFooter: true,
    record: {},
  });
}

async function onDelete(ids) {
  if (ids.length>0) {
    await remove({ ids: ids.join() }, true);
    await reload();
  }else{
    createMessage.warning('请选择')
  }
}

/**
 * 操作栏
 */
function getTableAction(record): ActionItem[] {
  return [
    { label: '下载二维码', onClick: downCode.bind(null, record), auth: 'facilities:facilitiesmanager:facilitiermsg:downQrcode' },
    { label: '编辑', onClick: onEdit.bind(null, record), auth: 'facilities:facilitiesmanager:facilitiermsg:edit' },
    { label: '删除', onClick: onDelete.bind(null, [record.id]), auth: 'facilities:facilitiesmanager:facilitiermsg:delete'},
  ];
}
const openUpload = ref(false)
const handleOk = () => {
  openUpload.value = false
}
async function ondownLoadXls() {
  const res = await download({});
  downloadByData(res, '设备信息管理导入模板.xlsx');
}

const temporarilyVisible = ref(false)
const temporarilyType = ref(0) // 0临时点检人 1点检人
// 临时点检人弹窗
const showTemporar = (type) => {
  temporarilyType.value = type
  if (selectedRowKeys.value.length == 0) {
    return createMessage.warning('请选择数据')
  }
  temporarilyVisible.value = true
}
// 取消临时点检人
const showCancleTemporar = async() => {
  if (selectedRowKeys.value.length == 0) {
    return createMessage.warning('请选择数据')
  }
  await cancelEmp({ids: selectedRowKeys.value.join()})
  selectedRowKeys.value = []
  reload()
}
// 临时点检人表单数据
const personParams = ref({
  proTemCheckEmpNo: '',
  proTemCheckEmp: ''
})
const applyTime = ref([])
const personChange = (val, option) => {
  personParams.value.proTemCheckEmpNo = val.join(',')
  personParams.value.proTemCheckEmp = option.map(item => item.empName).join(',')

}
// 设置临时点检人
const submitSetEmp = async() => {
  if (!personParams.value.proTemCheckEmpNo) {
    return createMessage.warning('请选择员工')
  }
  if (applyTime.value.length == 0 && !temporarilyType.value) {
    return createMessage.warning('请选择日期')
  }
  if(temporarilyType.value == 0) {
    await setEmp({...personParams.value, ids: selectedRowKeys.value.join(), proTemBeginDate: applyTime.value[0], proTemEndDate: applyTime.value[1]})
  }else {
    await bathSetEmp({checkEmp: personParams.value.proTemCheckEmp, checkEmpNo: personParams.value.proTemCheckEmpNo, ids: selectedRowKeys.value.join()})
  }
  temporarilyVisible.value = false
  reload()
}
// 生成二维码
const downQrcode = () => {
  if (selectedRows.value.length == 0) {
    return createMessage.warning('请选择数据')
  }
  const zip = new jszip()
  
  selectedRows.value.map(item => {
    toDataURL(`${identifier}?${encodeURIComponent(`path=equipment_create&id=${item.id}&code=${item.deviceCode}`)}`, {width: 200}, function (_, url) {
      zip.file(`${item.deviceName}-${item.location}-${item.deviceCode}.png`, url.replace('data:image/png;base64,', ''), {base64: true})
    })
  })
  downZip(zip)
}
const downZip = (zip) => {
  zip
    .generateAsync({
      type: "blob"
    })
    .then(content => {
      let fileName = "批量下载.zip";
      FileSaver.saveAs(content, fileName);
    });
}
const downCode = (item) => {
  toDataURL(`${identifier}?${encodeURIComponent(`path=equipment_create&id=${item.id}&code=${item.deviceCode}`)}`, {width: 200}, function (_, url) {
    downloadQrcode(url, `${item.deviceName}-${item.location}-${item.deviceCode}`)
  })
}
// 下载二维码
const downloadQrcode = (url: string, name: string) => {
  const linkNode = document.createElement('a')
  linkNode.download = name
  linkNode.href = url
  console.log(linkNode);
  
  linkNode.click()
}
</script>

<style lang="less" scoped>
@import 'index';

.blacklistUpload {
  padding: 10px;
}
.temp-box{
  padding: 20px;
  li {
    display: flex;
    align-items: center;
    margin-top: 20px;
    p{
      width: 160px;
      margin: 0;
    }
  }
}
</style>
