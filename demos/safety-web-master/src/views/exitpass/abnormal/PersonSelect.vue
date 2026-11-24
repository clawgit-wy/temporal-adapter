<template>
  <div class="box">
    <div class="left">
      <a-select
        size="small"
        v-model:value="searchPositionValue"
        show-search
        placeholder="input search text"
        :default-active-first-option="false"
        :show-arrow="false"
        :filter-option="false"
        :not-found-content="null"
        :options="orgSearchList"
        @search="orgSearch"
        @change="orgChange"
      ></a-select>
      <div class="position-box">
        <a-tree
          v-model:expandedKeys="expandedKeys"
          v-model:selectedKeys="selectedKeys"
          :tree-data="treeData"
          @select="select"
        >
        </a-tree>
      </div>
    </div>
    <div class="right">
      <a-input-search size="small" v-model:value="searchPerValue" @search="searchPerName" style="margin-bottom: 8px" placeholder="Search" />
      <ul id="person-box">
        <li v-for="item in perseonList" @click="perChange(item)">
          <p class="name">{{ item.empName }}</p>
          <p>{{ item.orgNamePath }}</p>
        </li>
        <div style="text-align: center">
          <span v-if="loadStatus == 2">已全部加载</span>
          <a-spin v-if="loadStatus == 1" :indicator="indicator" />
        </div>
      </ul>
    </div>
  </div>
</template>
<script setup lang="ts">
import {ref, onMounted, h} from 'vue'
import { defHttp } from '/@/utils/http/axios';
import { LoadingOutlined } from '@ant-design/icons-vue';

const expandedKeys = ref<string[]>([]);
const selectedKeys = ref<string[]>([]);
const treeData = ref([])
const indicator = h(LoadingOutlined, {
  style: {
    fontSize: '24px',
  },
  spin: true,
});
const loadStatus = ref(0)
const getPositons = async(key='') => {
  const {dataList} = await defHttp.post({url: '/safety/group/selectGroupTreeNonAsyn', params: {key}})
  treeData.value = dataList
  console.log(treeData.value);
  
}
// const onLoadData = async(treeNode) => {
//   if (treeNode.dataRef.children) return
//   const res = await getPositons(treeNode.key)
//   treeNode.dataRef.children = res
//   treeData.value = [...treeData.value];
//   return
// }
getPositons()
const onPersonScroll = () => {
  const container = document.getElementById('person-box')
  container?.addEventListener('scroll', () => {
    const bottom = (container.scrollHeight-(container.scrollTop + container.clientHeight))
    console.log(container.scrollTop + container.clientHeight, container.scrollHeight, bottom);
    
    if (bottom <= 50 && loadStatus.value == 0) {
      pageNum.value+=1
      perSearch()
    }
  })
}
const select = () => {
  loadStatus.value = 0
  pageNum.value = 1
  perSearch()
}

const orgSearchList = ref([])
const orgSearch = async(keyWords) => {
  const res = await defHttp.post({url: '/safety/group/getDepartName', params: {keyWords}})
  orgSearchList.value = res.length > 0 ? res.map(item => ({...item, label: item.name, value: item.orgCodePath})) : []
}
const orgChange = (key) => {
  expandedKeys.value = key.split(',')
  selectedKeys.value = [expandedKeys.value[expandedKeys.value.length-1]]
  loadStatus.value = 0
  pageNum.value = 1
  perSearch()
}
const perseonList = ref([])
const searchPositionValue = ref('')
const searchPerValue = ref('')
const pageNum = ref(1)
const perSearch = async(empName = '') => {
  if (loadStatus.value === 0) {
    loadStatus.value = 1
    const res = await defHttp.post({url: '/safety/employee/getUserListByCondition', params: {pageNum: pageNum.value, empName, pageSize: 20, orgCode: selectedKeys.value[0]}})
    if (pageNum.value == 1) {
      document.querySelector('#person-box').scrollTop = 0
      perseonList.value = res.dataList
    }else {
      perseonList.value = [...perseonList.value, ...res.dataList]
    }
    if(res.dataList.length < 20) {
      loadStatus.value = 2
    }else {
      loadStatus.value = 0
    }
  }
}
const searchPerName = (empName) => {
  loadStatus.value = 0
  pageNum.value = 1
  perSearch(empName)
}
const perChange = (item) => {
  expandedKeys.value = item.orgCodePath.split(',')
}
onMounted(() => {
  onPersonScroll()
})
</script>
<style lang="less" scoped>
.box{
  display: flex;
}
.left, .right {
  border: 1px solid #eee;
  width: 50%;
  height: 400px;
  display: flex;
  flex-direction: column;
  padding: 5px;
  margin: 5px;
}
.position-box {
  flex: 1;
  overflow-x: auto;
}
.right{
  display: flex;
  flex-direction: column;
  ul{
    flex: 1;
    height: 100%;
    overflow-x: auto;
    margin: 0;
    font-size: 14px;
    color: #818181;
    p{
      margin-bottom: 0;
      width: 100%;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
    li{
      cursor: pointer;
      padding: 5px;
    }
    .name{
      color: #333;
      font-size: 16px;
    }
  }
}
</style>