<template>
  <div>
    <a-modal v-model:visible="props.visible" title="组织选择" width="800px" @ok="change" @cancel="cancel">
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
              ref="departTreeref"
              :height="465"
            >
            </a-tree>
          </div>
        </div>
        <div class="right">
          <!-- <a-input-search size="small" v-model:value="searchPerValue" @search="searchPerName" style="margin-bottom: 8px" placeholder="请输入姓名搜索" /> -->
          <ul id="person-box">
            <li v-for="item in perseonList" :class="selectPerson.key == item.key ? 'active' : ''" @click="perChange(item)">
              <p class="name">{{ item.title }}</p>
              <p>{{ item.namePath }}</p>
            </li>
            <div style="text-align: center">
              <span v-if="loadStatus == 2">已全部加载</span>
              <a-spin v-if="loadStatus == 1" :indicator="indicator" />
            </div>
          </ul>
        </div>
      </div>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
  import { ref, onMounted, h } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { LoadingOutlined } from '@ant-design/icons-vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();
  const emits = defineEmits(['change', 'update:visible']);
  const props = defineProps({
    visible: {
      type: Boolean,
      defalut: false,
    },
    isSameDept: {
      //是否只显示本部门及本部门一下
      type: Boolean,
      defalut: false,
    },
  });
  const departTreeref = ref();
  const expandedKeys = ref<string[]>([]);
  const selectedKeys = ref<string[]>([]);
  const treeData = ref([]);
  const selectedItem = ref({});
  const indicator = h(LoadingOutlined, {
    style: {
      fontSize: '24px',
    },
    spin: true,
  });
  const loadStatus = ref(0);
  const getPositons = async (key = '') => {
    const { dataList } = await defHttp.post({
      url: '/safety/group/selectGroupTreeNonAsyn',
      params: { key },
    });
    treeData.value = dataList;
    console.log(treeData.value);
  };
  // const onLoadData = async(treeNode) => {
  //   if (treeNode.dataRef.children) return
  //   const res = await getPositons(treeNode.key)
  //   treeNode.dataRef.children = res
  //   treeData.value = [...treeData.value];
  //   return
  // }
  getPositons();
  const onPersonScroll = () => {
    const container = document.getElementById('person-box');
    // container?.addEventListener('scroll', () => {
    //   const bottom = container.scrollHeight - (container.scrollTop + container.clientHeight);
    //   console.log(container.scrollTop + container.clientHeight, container.scrollHeight, bottom);

    //   if (bottom <= 50 && loadStatus.value == 0) {
    //     pageNum.value += 1;
    //     // perSearch();
    //   }
    // });
  };
  const select = (value, node, extra) => {
    console.log('左边选择==', value, node, extra);
    loadStatus.value = 0;
    pageNum.value = 1;
    perSearch(node);
  };

  const orgSearchList = ref([]);
  let timer: any = null;
  const orgSearch = async (keyWords) => {
    console.log(2333);

    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      defHttp.post({ url: '/safety/group/getDepartName', params: { keyWords } }).then((res) => {
        orgSearchList.value =
          res.length > 0
            ? res.filter((item) => item.name && item.orgCodePath).map((item) => ({ ...item, label: item.name, value: item.orgCodePath }))
            : [];
      });
    }, 800);
  };
  const orgChange = (key) => {
    if (key == null) return;
    console.log('搜索选中=', key, departTreeref.value);
    expandedKeys.value = key.split(',');
    selectedKeys.value = [expandedKeys.value[expandedKeys.value.length - 1]];
    loadStatus.value = 0;
    pageNum.value = 1;
    const selectNode = orgSearchList.value.find((item) => item.value == key);
    if (selectNode) {
      perSearch({
        node: { ...selectNode, codePath: selectNode.orgCodePath, title: selectNode.label, namePath: selectNode.orgNamePath, key: selectNode.code },
      });
    }
    setTimeout(() => {
      console.log('搜索选中key=', selectedKeys.value[0]);
      departTreeref.value.scrollTo({ key: selectedKeys.value[0] });
    }, 200);
    // perSearch();
  };
  const perseonList = ref([]);
  const searchPositionValue = ref('');
  const searchPerValue = ref('');
  const pageNum = ref(1);
  const perSearch = (node) => {
    console.log('选中的组织==', expandedKeys, selectedKeys.value[0], selectedKeys.rawValue, node.node);
    // if (loadStatus.value === 0) {
    //   loadStatus.value = 1
    //   const res = await defHttp.post({url: '/safety/employee/getUserListByCondition', params: {pageNum: pageNum.value, empName, pageSize: 20, orgCode: selectedKeys.value[0]}})
    //   if (pageNum.value == 1) {
    //     document.querySelector('#person-box').scrollTop = 0
    //     perseonList.value = res.dataList
    //   }else {
    //     perseonList.value = [...perseonList.value, ...res.dataList]
    //   }
    //   if(res.dataList.length < 20) {
    //     loadStatus.value = 2
    //   }else {
    //     loadStatus.value = 0
    //   }
    // }
    perseonList.value = [node.node];
    console.log('选中的组织=33=', perseonList.value);
    selectPerson.value = perseonList.value[0];
    expandedKeys.value = perseonList.value[0].codePath.split(',');
    selectedKeys.value = [expandedKeys.value[expandedKeys.value.length - 1]];
  };
  const searchPerName = (empName) => {
    loadStatus.value = 0;
    pageNum.value = 1;
    perSearch(empName);
  };
  let selectPerson = ref({});
  const perChange = (item) => {
    selectPerson.value = item;
    expandedKeys.value = item.codePath.split(',');
    selectedKeys.value = [expandedKeys.value[expandedKeys.value.length - 1]];
  };
  const change = () => {
    console.log('选中的结果==', selectPerson);
    if (props.isSameDept) {
      defHttp
        .post({
          url: '/safety/group/validDept',
          params: { orgPath: expandedKeys.value.join(',') },
        })
        .then((res) => {
          console.log('选组织校验==', res);
          if (res.accessEight) {
            emits('change', selectPerson.value);
          } else {
            createMessage.error('不能选择该组织');
          }
        })
        .catch((err) => {
          createMessage.error('选择失败');
        });
    } else {
      emits('change', selectPerson.value);
    }
  };
  const cancel = () => {
    emits('update:visible', false);
  };
  onMounted(() => {
    onPersonScroll();
  });
</script>
<style lang="less" scoped>
  .box {
    display: flex;
  }
  .left,
  .right {
    border: 1px solid #eee;
    width: 50%;
    height: 500px;
    display: flex;
    flex-direction: column;
    padding: 5px;
    margin: 5px;
  }
  .position-box {
    flex: 1;
    overflow-x: auto;
  }
  .right {
    display: flex;
    flex-direction: column;
    ul {
      flex: 1;
      height: 100%;
      overflow-x: auto;
      margin: 0;
      font-size: 14px;
      color: #818181;
      p {
        margin-bottom: 0;
        width: 100%;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
      li {
        cursor: pointer;
        padding: 5px;
      }
      .name {
        color: #333;
        font-size: 16px;
      }
    }
  }
  .active {
    background-color: #eee;
  }
</style>
