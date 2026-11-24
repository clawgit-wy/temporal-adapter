<template>
    <a-tree
      block-node
      checkable
      checkStrictly
      v-model:checked-keys="targetKeys"
      @check="handleCheck"
      :tree-data="treeData"
      :height="600"
    />
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { selectGroupTree } from './api';
import { onMounted } from 'vue';

const props = defineProps(['defaultList'])
const emits = defineEmits(['change'])


const handleCheck = () => {
  emits('change', targetKeys.value.checked.join(','))
}

const targetKeys = ref({
  checked: [],
  halfChecked: []
});

const treeData = ref([])
const formatList = (list) => {
  list.forEach(item => {
    delete item.parent
    if (item.children) {
      formatList(item.children)
    }
  })
}
const getDeptTree = async() => {
  const {dataList} = await selectGroupTree()
  formatList(dataList)
  // const list = dataList.map(item => {
  //   delete item.parent
  //   return {...item, isLeaf: false}
  // })

  treeData.value = dataList
  console.log(treeData.value, 'treeData');
}

getDeptTree()
onMounted(() => {
  console.log('mounted', props.defaultList);
  
  if(props.defaultList){
    targetKeys.value.checked = props.defaultList.split(',')
  }

})

</script>
<style scoped>

</style>