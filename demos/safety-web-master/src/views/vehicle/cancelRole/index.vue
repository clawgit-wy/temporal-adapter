<template>
  <div class="container">
    <a-button type="primary" :loading="loading" @click="apply" v-if="hasAuth('vehicle:cancelRole:apply')">应用</a-button>
    <div class="ipt">
      <span>连续</span>&nbsp;&nbsp;&nbsp;&nbsp;

      <a-input-number v-model:value="date" :min="1"></a-input-number>&nbsp;&nbsp;&nbsp;&nbsp;
      <span>天车辆未入厂取消车辆和人员的门禁权限</span>
    </div>
    <div class="tableBox">
      <a-tabs>
        <a-tab-pane tab="临时运输车辆管理"></a-tab-pane>
      </a-tabs>
      <a-button
        type="primary"
        preIcon="ant-design:plus-outlined"
        v-if="hasAuth('systemd:field:add')"
        @click="onAdd({ fieldType: 'temporaryTransMana', title: '临时运输车辆管理', fieldLevel: 3 })"
        >添加</a-button
      >
      <BasicTable
        style="margin-top: 10px"
        v-for="item in dataSource"
        :key="item.id"
        :title="item.fieldName"
        childrenColumnName="regionList"
        :columns="item.fieldCode == 'temporaryTransMana_cllx_0' ? columns : moveColumns"
        :dataSource="item.regionList"
        :minHeight="300"
        :showIndexColumn="false"
        :pagination="false"
        :loading="loading"
        :actionColumn="{
          width: 100,
          title: '操作',
          dataIndex: 'action',
          slots: { customRender: 'action' },
        }"
      >
        <!--插槽:table标题-->
        <template #tableTitle>
          <div class="systemd-field-title">
            <p>{{ item.fieldName }}</p>
            <div>
              <a-button class="delete_btn" preIcon="ant-design:delete-outlined" v-if="hasAuth('systemd:field:delete')" @click="onDelete(item)"
                >删除</a-button
              >
              <a-button
                type="primary"
                preIcon="ant-design:plus-outlined"
                v-if="hasAuth('systemd:field:add')"
                @click="onAdd({ fieldType: item.fieldCode, title: item.fieldName, parentId: item.id, fieldLevel: 4 })"
                >添加</a-button
              >
            </div>
          </div>
        </template>
        <template #fieldSort="{record}">
          <div>
            <a-button type="text" :disabled="record.fieldSort === 0" @click="move({ record, type: 0 })"> <arrow-up-outlined />上移 </a-button>
            <a-button type="text" :disabled="record.fieldSort === record.maxSort" @click="move({ record, type: 1 })">
              <arrow-down-outlined />下移
            </a-button>
          </div>
        </template>
        <!--操作栏-->
        <template #action="{ record, index}">
          <TableAction :actions="getTableAction(record, index, item)" />
        </template>
      </BasicTable>

      <FieldModal @register="registerModal" @success="reload" />
  </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import {getDate, setDate} from './api'
import { message } from 'ant-design-vue'
import { usePermission } from '/@/hooks/web/usePermission';
  import { deleteSingle, getTabs, moveList } from '/@/views/systemd/field/field.api';
  import FieldModal from '/@/views/systemd/field/FieldModal.vue';
  import { BasicTable, TableAction, BasicColumn } from '/@/components/Table';
  import { moveColumns } from '/@/views/systemd/field/field.data';
  import { useModal } from '/@/components/Modal';
const { hasPermission } = usePermission();
const [registerModal, { openModal }] = useModal();
function hasAuth(code) {
  return hasPermission(code);
}

const date = ref('')
const id = ref()
const dataSource = ref<any[]>([]);
const columns: BasicColumn[] = [
    {
      title: '名称',
      dataIndex: 'fieldName',
      width: 80,
      align: 'left',
    },
    {
      title: '是否临时运输车辆',
      dataIndex: 'fieldValue',
      width: 80,
      align: 'left',
    },
    {
      title: '是否填写环保清单',
      dataIndex: 'envirInfo',
      width: 80,
      align: 'left',
    },
    {
      title: '移动',
      dataIndex: 'fieldSort',
      width: 100,
      slots: { customRender: 'fieldSort' },
    },
];
const getDateApi = async() => {
  const res = await getDate({})
  date.value = res.limitNumber
  id.value = res.id
}
  const getList = () => {
    getTabs({ fieldType: 'temporaryTransMana' }).then((res) => {
      setChildren(res);
      dataSource.value = res;
    });
  };
  function getTableAction(record, index: any = undefined, item: any) {
    let actionList = [
      { label: '添加', onClick: add.bind(null, { ...record, fieldLevel: 5 }, item), auth: 'systemd:field:add' },
      { label: '编辑', onClick: onEdit.bind(null, record, item), auth: 'systemd:field:edit' },
      { label: '删除', onClick: onDelete.bind(null, record, item), auth: 'systemd:field:delete' },
    ];
    if (record.fieldLevel && record.fieldLevel > 4) {
      actionList.shift();
    }
    if (item.fieldType == 'buildingAccessFactoryManage' || item.fieldType == 'secretLevel') {
      actionList.shift();
    }
    return actionList;
  }
  const reload = () => {
    getList();
  };
 function onAdd(record) {
    console.log('添加======11==', record);
    let str = '添加';
    // if (record.title) {
    //     str = `添加${record.title}`
    // }
    openModal(true, {
      title: str,
      isUpdate: false,
      showFooter: true,
      record,
    });
  }
  function add(record, item) {
    console.log('添加======22==', record, item);
    const str = '添加';
    openModal(true, {
      title: str,
      isUpdate: false,
      showFooter: true,
      record: {
        fieldLevel: record.fieldLevel,
        parentId: record.id,
        fieldType: item.fieldCode,
      },
    });
  }
    // 删除操作
  async function onDelete(record, item?: any) {
    if (record) {
      await deleteSingle({ id: String(record.id) }, true);
      await getList();
      reload();
    }
  }
  const setChildren = (list) => {
    list.forEach((item) => {
      if (!item.regionList || item.regionList.length == 0) {
        item.regionList = undefined;
      } else {
        setChildren(item.regionList);
      }
    });
  };
  // 编辑
  function onEdit(record) {
    console.log(record);
    
    let str = '编辑';

    openModal(true, {
      title: str,
      isUpdate: true,
      record: record,
      showFooter: true,
    });
  }
  const move = ({ record, type }) => {
    moveList({ id: record.id, move: type }).then(() => reload());
  };
getDateApi()
getList()

const apply = async() => {
  loading.value = true
   await setDate({id: id.value, limitNumber: date.value})
  loading.value = false
  message.success('操作成功')
}

const loading = ref(false)

</script>
<style scoped>
.container{
  margin: 10px;
  background-color: #fff;
  width: 100%;
  max-width: 100%;
  padding: 20px;
}
.ipt{
  margin-top: 20px;

}
.tableBox{
  margin-top: 20px;
  padding: 10px;
}
.systemd-field-title {
    width: 100%;
    display: flex;
    justify-content: space-between;
  }
  .delete_btn {
    margin-right: 10px;
  }
</style>