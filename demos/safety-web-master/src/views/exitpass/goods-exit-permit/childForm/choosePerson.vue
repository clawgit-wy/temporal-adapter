<template>
  <div class="systemd-noticemessage">
    <div style="display:flex;">
      <a-input v-model:value="searchContent" placeholder="请输入姓名" /> <a-button @click="search" type="primary">查询</a-button>
    </div>
    <p class="tips" >请双击选择</p>
    <div>
      <div>最近选择</div>
      <p class="recent-box"><a-tag v-for="item in recentList" @dblclick="dbClick(item)">{{ item.empName }}-{{ item.empNo }}-{{ item.empDept }}</a-tag></p>
      <br>
    </div>
    <a-table
      :dataSource="dataSource"
      :columns="columns"
      :customRow="(record) => {
        return {
          onDblclick: () => dbClick(record)
        }
      }"
      size="small">
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'collectType'">
          <star-filled @click="collect(record)" v-if="text == 1" style="color: blue; cursor: pointer"></star-filled>
          <star-outlined @click="collect(record)" style="color: blue; cursor: pointer" v-else />
        </template>
      </template>
    </a-table>
    <!-- <BasicTable
      title="双击选择"
      @row-dbClick="dbClick"
      @register="registerTable"
    >
    </BasicTable> -->
  </div>
</template>
<script setup lang="ts">
import {findPerson,getRecentSelect, collectPerson} from '../field.api'
import { onMounted, ref } from 'vue';
import { StarOutlined, StarFilled } from '@ant-design/icons-vue'
import { useUserStore } from '/@/store/modules/user';
const userStore: any = useUserStore().getUserInfo;
const props = defineProps({
  params: {
    type: Object,
    default: () => {}
  },
})
const recentList = ref([])
onMounted(() => {
  console.log(props.params);
  getRecentSelect(props.params).then(res => {
    recentList.value = res.splice(0, 5)
  })
  search()
  
})
const searchContent = ref('')
const dataSource = ref([])
const columns = ref([
    {title: '姓名', dataIndex: 'empName', key: 'empName'},
    {title: '工号', dataIndex: 'empNo', key: 'empNo'},
    {title: '部门', dataIndex: 'empDept', key: 'empDept'},
    {title: '收藏', dataIndex: 'collectType', key: 'collectType',}
])
const search = async() => {
  const res = await findPerson({searchContent: searchContent.value, ...props.params})
  dataSource.value = res
}
const emit = defineEmits<{
  (e: 'add', data:Object):void
}>()
const dbClick = async (record) => {
  emit('add', record)
}
const collect = async(item) => {
  const type = item.collectType == '1' ? '2' : '1'
  const res = await collectPerson({
    ownerEmpNo: userStore.workNo,
    authId: item.authId,
    collectType: type
  })
  item.collectType = type
  
}
</script>
<style lang="less" scoped>
.systemd-noticemessage{
  padding: 20px;
}
.tips{
  color:red;
  padding: 10px 0px 10px 0;
  font-size: 18px;
  margin-bottom: 0;
}
.recent-box{
  margin-bottom: 0;
}
.recent-box span{
  cursor: pointer;
  margin-right: 10px;
}
.recent-box span:hover{
  color: blue;
}
</style>