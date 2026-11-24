<template>
  <div>
    <a-table bordered :pagination="false" :dataSource="props.dataSource" :columns="columns">
      <template #title> <span style="font-size: 16px; color: rgba(0,0,0,0.85);">点检标准</span></template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'imgUrl'">
          <TableImg v-if="record.imgUrl" :size="60" :simpleShow="true" :imgList="record.imgUrl.split(',')" />
        </template>
        <template v-if="column.key === 'finishUrl'">
          <TableImg v-if="record.finishUrl" :size="60" :simpleShow="true" :imgList="record.finishUrl.split(',')" />
        </template>
      </template>
    </a-table>
  </div>
</template>
<script setup lang="ts">
import { TableImg } from '/@/components/Table';
  const props = defineProps({
    dataSource: []
  })
  const columns = [
    {title: '点检标准', dataIndex: 'ruleName', key: 'ruleName'},
    {title: '是否异常', dataIndex: 'isException', key: 'isException', customRender: ({text}) => text == 0 ? '正常' : text == 1 ? '异常' : ''},
    {title: '问题描述', dataIndex: 'excepDesc', key: 'excepDesc'},
    {title: '图片上传', dataIndex: 'imgUrl', key: 'imgUrl'},
    {title: '是否当场整改', dataIndex: 'isLocal', key: 'isLocal', customRender: ({text}) => text == 0 ? '否' : text == 1 ? '是' : ''},
    {title: '当场整改完成照片', dataIndex: 'finishUrl', key: 'finishUrl'},
  ]
</script>
<style>
</style>