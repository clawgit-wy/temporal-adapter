<template>
  <div class="container">
        <h1 style="text-align: center">设备设施点检流程审批</h1>
        <div style="text-align: right;">
          <a-button type="primary" @click="circularizeVisible = true">传阅</a-button>
        </div>
        <p style="color: #faad14;" v-if="showChange">
          节点职责提醒：
          <span v-if="baseInfo.nodeFlag == 1 && baseInfo.commissionFlag == '1'">{{ tips["4"] }}</span>
          <span v-else>{{ tips[baseInfo.nodeFlag] }}</span>
        </p>
        <br>
        <CheckPersonInfo :info="baseInfo" />
        <br>
        <CheckObjInfo :info="baseInfo" ref="objInfo"/>
        <br>
        <standardTable :dataSource="recordRuleDOS" />
        <br>
        <a-card title="整改结果" v-if="baseInfo.nodeFlag == 2">
          <ChangeLog ref="changeLog"></ChangeLog>
        </a-card>
        
        <br v-if="baseInfo.nodeFlag == 2">
        <changeLogResultTable v-if="changeResultList.length > 0" :dataSource="changeResultList"></changeLogResultTable>
        <br>
        <a-card title="验证详请描述" v-if="baseInfo.nodeFlag == 3 && baseInfo.status!=30">
          <ChengDescInfo ref="changeDdescInfo"></ChengDescInfo>
        </a-card>
        <br>
        <div>
          <iframe style="width: 100%;height: 3600px;" v-if="iframe" :src="iframe" frameborder="0" id="bpmIframe"></iframe>
        </div>
        <div class="btn-box" style="display:flex; justify-content: center;" v-if="showChange">
          <div v-show="baseInfo.nodeFlag == 1">
            <a-button style="margin-right: 40px;" v-if="baseInfo.commissionFlag != '1'" type="primary"  @click="choosePerson">转派至整改责任人</a-button>
            <a-button type="primary"  @click="submit">提交</a-button>
          </div>
          <div v-show="baseInfo.nodeFlag == 2">
            <a-button style="margin-right: 40px;" type="primary"  @click="abarbeitung">已完成整改</a-button>
            <a-button type="primary"  @click="dateVisible = true">延期整改</a-button>
          </div>
          <div v-show="baseInfo.nodeFlag == 3">
            <a-button style="margin-right: 40px;" type="primary"  @click="checkPass">已验证</a-button>
            <a-button type="primary"  @click="checkFail">整改不合格</a-button>
          </div>
        </div>
        <a-modal v-model:visible="personVisible" title="转派责任人" @ok="changePerson">
          <p style="display: flex; align-items: center; padding: 10px 20px">
            <p style="width: 150px;">整改责任人：</p>
            <a-select
              v-model:value="reformUser"
              show-search
              style="width: 200px"
              placeholder="请输入员工姓名查询选择"
              :default-active-first-option="false"
              :show-arrow="false"
              :filter-option="false"
              :not-found-content="null"
              :options="employeeOption"
              @search="(val) => handleSearch({empName: val})"
            >
              <template #option="{ value: val, label }">
                  &nbsp;&nbsp;{{ label }} （{{ val }}）
              </template>
            </a-select>
          </p>
          <p style="display: flex; align-items: center; padding: 10px 20px">
            <p style="width: 150px;">整改责任人直接上级：</p>
            <a-select
              v-model:value="reformUserLeader"
              show-search
              style="width: 200px"
              placeholder="请输入员工姓名查询选择"
              :default-active-first-option="false"
              :show-arrow="false"
              :filter-option="false"
              :not-found-content="null"
              :options="employeeLeaderOpt"
              @search="(val) => handleLeaderSearch({empName: val})"
            >
              <template #option="{ value: val, label }">
                  &nbsp;&nbsp;{{ label }} （{{ val }}）
              </template>
            </a-select>
          </p>
        </a-modal>
        <a-modal destroyOnClose v-model:visible="dateVisible" title="延期整改" @ok="postponeSubmit">
          <p style="display: flex; padding: 20px"><span>延期时间：</span><a-date-picker v-model:value="delayTime" /></p>
        </a-modal>
        <a-modal destroyOnClose v-model:visible="circularizeVisible" title="传阅" :confirmLoading="circularizeLoading" @ok="submitCircularize" width="600px">
          <ul class="temp-box">
            <li>
              <p>传阅给：</p>
              <SearchPerson mode="multiple" style="width: 500px" @change="personChange"></SearchPerson>
            </li>
          </ul>
        </a-modal>
    </div>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import {useRoute, useRouter} from 'vue-router'
import CheckPersonInfo from './childForm/checkPersonInfo.vue'
import CheckObjInfo from './childForm/checkObjInfo.vue';
import changeLogResultTable from './childForm/changeLogResultTable.vue'
import ChangeLog from './childForm/changeLog.vue'
import ChengDescInfo from './childForm/checkDescInfo.vue'
import standardTable from './childForm/standardTable.vue'
import '/@/assets/bpm/domain'
import { view, delay, audit, employeeList, getApproverList, circulate, getProcessId} from './field.api'
import { useGlobSetting } from '/@/hooks/setting';
import { useUserStore } from '/@/store/modules/user';
import { message,Modal } from 'ant-design-vue';
import SearchPerson from '/@/components/Form/src/components/SearchPerson.vue'
import dayjs from 'dayjs'
const tips = {
  "1": '查看故障设备详请，将故障设备转派至相关责任人整改/跟进（此时不需要填写计划整改完成日期）。如因业务职责自行跟进整改，请于24小时内填写计划整改完成日期并提交，在下一个节点整改完成后上传整改完成照片。',
  "2": '对故障设备进行整改，并在计划整改完成日期前上传整改完成照片。如需延期整改，请重新确定计划整改完成日期。',
  "3": '验证故障设备整改情况，查看设备是否恢复正常。',
  "4": '根据故障设备详请于24小时内填写计划整改完成日期并提交。',
}
const userStore = useUserStore().getUserInfo;

const globSetting = useGlobSetting();
const {bpmUrl} = globSetting;
const route = useRoute()
const router = useRouter()
const {formInstanceId: formId, processId} = route.query
const iframe = ref(`${bpmUrl}?method=showProcess&sysId=SAFETY&modelId=monitor_post&templateFormId=safety_monitor_post_process&formInstanceId=${formId}&processId=${processId}`)

// 获取流程
const getBpmData = () =>{
  return new Promise((resolve, reject) => {
    const flowIFrame = document.getElementById('bpmIframe')
    const domain = window?.domain || ''
    if (domain) {
      domain.call(flowIFrame.contentWindow, 'lbpm_getApprovalData', [], function(data){
        const res = JSON.parse(data)
        resolve(JSON.parse(res['sysWfBusinessForm.fdParameterJson']))
      })
    }else{
      reject()
    }
  })
}

const getProcessParam = async () => {
  const bpmData:any = await getBpmData()
  return {
    flowId:processId,
    loginName: userStore.userName,
    'processParam.subject':`${baseInfo.value.teamName}-${baseInfo.value.deviceName}`,
    'processParam.flowId': processId,
    'processParam.modelId':"spot_check",
    'processParam.formId':"safety_spot_check_process",
    'processParam.type':"audit",
    'processParam.fdParameterJson': JSON.stringify(bpmData),
    auditNoteMsg: bpmData.param.auditNote
  }
}
const objInfo = ref()
const submit = async() => {
  const reformTime = await objInfo.value.getTime()
  if (!reformTime) {
    return message.error('请选择计划完成日期')
  }
  const {dataList} = await employeeList({email: userStore.email})
  const [curPerson] = dataList
  let dept = ''
  if (curPerson && curPerson.orgName) {
    dept = curPerson.orgName
  }
  const processParam = await getProcessParam()
  fetchAudit({reformTime, ...processParam, reformUserInfo: `${userStore.realName}-${userStore.workNo}-${dept}`, reformUser: userStore.userName, nodeFlag: 2, id: baseInfo.value.id})
}
const fetchAudit = (params) => {
  audit(params).then(res => {
    if (res == 'T') {
      message.success('操作成功')
      location.reload()
    }else{
      message.error(res)
    }
  })
}
const personVisible = ref(false)
// 展示选择人员
const choosePerson = () => {
  personVisible.value = true
}


// 转派人
const reformUser = ref()
// 转派人上级
const reformUserLeader = ref()
// 选择人员提交
const changePerson = async() => {
  if (!reformUser.value || !reformUserLeader.value) {
    return message.error('请选择人员')
  }
  const name = employeeOption.value.find(item => item.email == reformUser.value)
  const user = reformUser.value.split('@')[0]
  const leaderUser = reformUserLeader.value.split('@')[0]
  Modal.confirm({
    title: '转派至整改责任人',
    content: `整改责任人: ${name.empName}`,
    async onOk() {
      const processParam = await getProcessParam()
      await fetchAudit({...processParam, commissionFlag: 1, reformUserLeader: leaderUser, reformUser: user, flowId: processId, id:baseInfo.value.id})
      personVisible.value = false
      reformUser.value = null
    },
    onCancel() {},
  });
  
}
const changeLog = ref()
// 完成整改
const abarbeitung = async() => {
  const result = await changeLog.value.getLog()
  if (!result.reformFinish) {
    return message.error('请选择整改结果')
  }
  if (!result.reformImg) {
    return message.error('请上传设备整改后照片')
  }
  const param = {
    reformFinish: result?.reformFinish || '',
    reformImg: result.reformImg ? result.reformImg : ''
  }
  const resultDTOS = [{
    ...param,
    planTime: baseInfo.value.reformTime,
  }]
  const processParam = await getProcessParam()
  fetchAudit({...processParam, resultDTOS: JSON.stringify(resultDTOS), nodeFlag: 3, id:baseInfo.value.id, deviceId: baseInfo.value.id})
}
const delayTime = ref()
const dateVisible = ref(false)
// 延期整改
const postponeSubmit = async() =>{
  if (!delayTime.value) {
    return message.error('请选择日期')
  }
  const time = dayjs(delayTime.value).format('YYYY-MM-DD')
  Modal.confirm({
    title: '延期整改',
    content: `延期至: ${time}`,
    onOk() {
      delay({flowId: processId, delayTime: time, leaderUsername: baseInfo.value.reformUserLeader}).then(res => {
        dateVisible.value = false
        delayTime.value = null
        message.success('操作成功')
        location.reload()
      })
    },
    onCancel() {},
  });
}
const changeDdescInfo = ref()
// 检查通过
const checkPass = async() => {
  const result = await changeDdescInfo.value.getRemark()
  if (!result.validRemark) {
    return message.error('请输入验证详情描述')
  }
  console.log("正常执行===========")
  const processParam = await getProcessParam()
  fetchAudit({...processParam, id: baseInfo.value.id, deviceId: baseInfo.value.id, checkResult: 1,validRemark:result.validRemark})
}
// 不通过
const checkFail = async() => {
  const result = await changeDdescInfo.value.getRemark()
   if (!result.validRemark) {
    return message.error('请输入验证详情描述')
  }
   console.log("正常执行===========")
  const processParam = await getProcessParam()
  fetchAudit({...processParam, nodeFlag: 1, id: baseInfo.value.id, reformUser: baseInfo.value.reformUser, deviceId: baseInfo.value.id, checkResult: 4,validRemark:result.validRemark})
}
// 展示操作按钮
const showChange = ref(false)
// 回显信息
const baseInfo = ref({})
const recordRuleDOS = ref([])
const changeResultList = ref([])
// 审批记录

const getFormInfo = async() => {
  console.log(route.query);
  if (!route.query.processId && route.query.formInstanceId) {
    const res = await getProcessId({formInstanceId: route.query.formInstanceId})
    router.replace({
      name: 'facilitiesApprovals',
      query: {
        ...route.query,
        processId: res
      }
    })
    return
  }
  // 获取详情回显表单
  const detailInfo = await view({flowId: processId})
  // 获取当前审批人
  const getCurPerson = await getApproverList({id: detailInfo.id, flowId: route.query.processId, formId: route.query.formInstanceId})
  const persons = JSON.parse(getCurPerson)
  showChange.value = persons.some(item => item.LoginName == userStore.userName)
  baseInfo.value = {...detailInfo, checkTime: detailInfo.checkTime ? detailInfo.checkTime.split('.')[0] : ''}
  recordRuleDOS.value = JSON.parse(detailInfo.recordRuleDOS)
  changeResultList.value = detailInfo.resultDTOS ? JSON.parse(detailInfo.resultDTOS) : []
  if (detailInfo.commissionFlag == 1) {
  }
}
getFormInfo()
let timer:any = null
// 人员列表
const employeeOption = ref([])
const employeeLeaderOpt = ref([])
// 搜索人员
const handleSearch = (params) => {
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => {
    employeeList(params).then(res => {
      const list = res.dataList.map(item => {
          return ({ ...item, label: item.empName, value: item.email})
      })
      employeeOption.value = JSON.parse(JSON.stringify(list))
    })
  }, 500)
}
const handleLeaderSearch = (params) => {
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => {
    employeeList(params).then(res => {
      const list = res.dataList.map(item => {
          return ({ ...item, label: item.empName, value: item.email})
      })
      employeeLeaderOpt.value = JSON.parse(JSON.stringify(list))
    })
  }, 500)
}
const circularizeVisible = ref(false)
const circularizeLoading = ref(false)

const submitCircularize = async() => {
  if (!toUser.value) {
      message.info('请选择传阅人');
      return;
    }
  circularizeLoading.value = true
  try {
    await circulate({
      fromUser: userStore.email ? userStore.email.split('@')[0] : '',
      toUser: toUser.value,
      flowId: processId
    })
    circularizeVisible.value = false
    circularizeLoading.value = false
    message.success('操作成功')
  } catch (error) {
    circularizeLoading.value = false
  }
  
}
const toUser = ref()
const personChange = (val, option) => {
  console.log(val, option);
  const toUserList = option.map(item  => item.email ? item.email.split('@')[0] : '')
  toUser.value = toUserList.join(',')
  console.log(toUser.value);
  

}
</script>
<style scoped lang="less">
.temp-box{
  padding: 20px;
  li {
    display: flex;
    align-items: center;
    margin-top: 20px;
    p{
      width: 80px;
      margin: 0;
    }
  }
}
p{
  margin: 0;
}
.container{
    background-color: #fff;
    width: 100%;
    max-width: 100%;
    padding: 0 50px;
}
h1{
    font-size: 18px;
    padding-left: 50px;
    font-weight: 600;
    padding-top: 30px;
}
:deep(.ant-divider-plain.ant-divider-with-text) {
    font-size: 16px;
    font-weight: 400;
}
iframe{
    width: 100%;
    height: 3600px;
}
:deep(.top-form>.ant-row>.ant-col:last-child) {
  display: none !important;
}
.btn-box{
  position: fixed;
  width: 100%;
  background-color: #fff;
  bottom: 0;
  left: 0;
  padding: 5px 0;
}
</style>
