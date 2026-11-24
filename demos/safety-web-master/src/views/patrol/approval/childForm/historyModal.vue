<template>
  <a-modal v-model:visible="visible" title="修改记录" width="1400px" :footer="null" @cancel="hide">
    <a-table :dataSource="dataSource" :columns="columns" @cancel="visible = false">
      <template #bodyCell="{ column, text, record }">
        <template v-if="column.dataIndex === 'problemImage'">
          <div :class="record.changeKeys.includes('problemImage') ? 'linehight' : ''">
            <Badge :count="text.length">
              <ImagePreviewGroup>
                <Image :width="80" v-for="(item, index) in text" :style="{display: index > 0 ? 'none' : 'block'}" :key="item" :src="item" />
              </ImagePreviewGroup>
            </Badge>
          </div>
        </template>
      </template>
    </a-table>
  </a-modal>
</template>
<script setup lang="ts">
import {ImagePreviewGroup, Image, Badge} from 'ant-design-vue'
import { h, ref, watchEffect } from 'vue';
import {getHistoryChange} from '../field.api'

const props = defineProps({
  id: {
    type: Number,
    default: null
  }
})
const emits = defineEmits(['hide'])
const visible = ref(true)
const columns = [
  {title: '事件发生时间', dataIndex: 'occurTime', customRender: ({text, record}) => record.changeKeys.includes('occurTime') ? h('div', {class: 'linehight'}, text) : text },
  {title: '厂区', dataIndex: 'factory', customRender: ({text, record}) => record.changeKeys.includes('factory') ? h('div', {class: 'linehight'}, text) : text },
  {title: '事件类型', dataIndex: 'eventType', customRender: ({text, record}) => record.changeKeys.includes('eventType') ? h('div', {class: 'linehight'}, text) : text },
  {title: '事项', dataIndex: 'teamName', customRender: ({text, record}) => record.changeKeys.includes('teamName') ? h('div', {class: 'linehight'}, text) : text },
  {title: '事件等级', dataIndex: 'eventLevel', customRender: ({text, record}) => record.changeKeys.includes('eventLevel') ? h('div', {class: 'linehight'}, text) : text },
  {title: '责任部门', dataIndex: 'dutyDept', customRender: ({text, record}) => record.changeKeys.includes('dutyDept') ? h('div', {class: 'linehight'}, text) : text },
  {title: '地理位置', dataIndex: 'eventAddress', customRender: ({text, record}) => record.changeKeys.includes('eventAddress') ? h('div', {class: 'linehight'}, text) : text },
  {title: '是否重点部位', dataIndex: 'isKeynote', customRender: ({text, record}) => record.changeKeys.includes('isKeynote') ? h('div', {class: 'linehight'}, text) : text },
  {title: '问题描述', dataIndex: 'problemDesc', customRender: ({text, record}) => record.changeKeys.includes('problemDesc') ? h('div', {class: 'linehight'}, text) : text },
  {title: '图片上传', dataIndex: 'problemImage'},
  {title: '是否当场处理', dataIndex: 'isScene', customRender: ({text, record}) => record.changeKeys.includes('isScene') ? h('div', {class: 'linehight'}, text) : text },
  {title: '修改人', dataIndex: 'updateUser'},
]
const dataSource = ref([])
if (props.id) {

  getHistoryChange({patrolId: props.id, pageSize: 99}).then(res => {
    if (res.dataList) {
      dataSource.value = res.dataList.map(item => {
        return {
          ...item,
          eventLevel: {EVENT_LEVEL_01: '较大隐患/违规', EVENT_LEVEL_02: '一般隐患/违规', EVENT_LEVEL_03: '重大隐患/违规'}[item.eventLevel],
          isKeynote: {1: '是', 0: '否'}[item.isKeynote],
          isScene: {1: '是', 0: '否'}[item.isScene],
          problemImage: item.problemImage ? item.problemImage.split(',') : [],
          changeKeys: item.modifyColumn ? item.modifyColumn.split(',') : []
        }
      })
      console.log(dataSource.value);
      
    }
  })
}
const hide = () => {
  emits('hide')
}
</script>
<style>
.linehight::before{
  content: '';
  position: absolute;
  width: 0;
  height: 0;
  border: 10px solid #fa5151;
  border-bottom-color: transparent;
  border-right-color: transparent;
  left: -8px;
  top: 0px;

}
</style>