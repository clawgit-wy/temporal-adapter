<template>
  <div>
    <a-table bordered :pagination="false" :dataSource="props.dataSource" :columns="columns">
      <template #title> <span style="font-size: 16px; color: rgba(0, 0, 0, 0.85)">整改历史记录</span></template>
      <template #headerCell="{ column }">
        <template v-if="column.key === 'reformImg'">
          <span>
            <span style="color: red">*</span>
            整改完成图片
          </span>
        </template>
      </template>
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'reformImg'">
          <TableImg v-if="record.reformImg" :size="60" :simpleShow="true" :imgList="record.reformImg.split(',')" />
        </template>
      </template>
    </a-table>
  </div>
</template>
<script setup lang="ts">
  import { TableImg } from '/@/components/Table';
  import dayjs from 'dayjs';
  const props = defineProps({
    dataSource: [],
  });
  const columns = [
    { title: '序号', dataIndex: 'index', key: 'index', customRender: ({ text, _, index }) => index + 1 },
    { title: '是否整改', dataIndex: 'reformFinish', key: 'reformFinish', customRender: ({ text }) => (text == 1 ? '是' : '否') },
    { title: '整改完成日期', dataIndex: 'finishTime', key: 'finishTime', customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD') : '') },
    {
      title: '计划整改完成日期',
      dataIndex: 'planTime',
      key: 'planTime',
      customRender: ({ text }) => (text ? `${dayjs(text).format('YYYY-MM-DD')}` : ''),
    },
    { title: '整改完成图片', dataIndex: 'reformImg', key: 'reformImg' },
    { title: '整改人', dataIndex: 'reformName', key: 'reformName' },
    {
      title: '验证结果',
      dataIndex: 'validResult',
      key: 'validResult',
      customRender: ({ text }) => (text == '1' ? '已验证' : text == '0' ? '未验证' : text == '4' ? '整改不合格' : ''),
    },
    { title: '验证详情描述', dataIndex: 'validRemark', key: 'validRemark' },
  ];
</script>
