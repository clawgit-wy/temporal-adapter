AA<template>
  <div ref="refModal">
    <BasicModal 
      :closeFunc="beforeClose"
      @register="registerModal" :title="title" :width="900" okText="提交" @ok="submit" :getContainer="() => $refs.refModal">
      <div class="visitor-box">
        <a-form ref="formRef" :model="formState" class="myForm" :rules="rules">
          <div>
            <div class="visitor-title">
              <span>接口人信息</span>
            </div>
            <a-row class="row-zl2" :loading="true" v-for="(item, index) in dockLists" :key="index">
              <a-col :span="6">
                <a-form-item label="姓名">
                  <Tooltip placement="top" :title="item.fdDockName">
                    <span>{{ item.fdDockName }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item label="员工号" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                  <Tooltip placement="top" :title="item.fdDockNo">
                    <span>{{ item.fdDockNo }} </span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item label="手机号" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                  <Tooltip placement="top" :title="item.fdDockPhone">
                    <span>{{ item.fdDockPhone }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item label="部门" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                  <Tooltip placement="top" :title="item.fdDockDepartment">
                    <span>{{ item.fdDockDepartment }} </span>
                  </Tooltip>
                </a-form-item>
              </a-col>
            </a-row>
          </div>

          <div>
            <div class="visitor-title">
              <span> 物流车辆信息 </span>
            </div>
            <a-row class="row-zl">
              <a-col :span="8">
                <a-form-item label="车牌号" name="visitedName">
                  <a-input :disabled="isEdit" @blur="getCarInfo" v-model:value="formState.fdCarNo"></a-input>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="车辆归属" name="visitedName">
                  <Tooltip placement="top" :title="formState.fdCarBelong">
                    <span>{{ formState.fdCarBelong }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="车主姓名">
                  <Tooltip placement="top" :title="formState.fdDriverName">
                    <span>{{ formState.fdDriverName }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="环保等级">
                  <span>{{ formState.fdProtectGrade }}</span>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="随车清单">
                  <a :href="formState.fdDetailedImg" target="_blank" v-if="formState.fdDetailedImg">点击查看</a>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="入厂类别">
                  <a-select v-model:value="formState.fdEnterTypeCode" style="width: 120px" :options="enterTypeOptions"></a-select>
                </a-form-item>
              </a-col>
            </a-row>
            
          </div>
          <div>
            <div class="visitor-title">
              <span> 人员信息 </span>
            </div>
            <a-row class="row-zl2" v-for="(item, index) in perList" :key="index">
              <a-col :span="2">
                <a-form-item  :label="item.fdPerTypeCode == '10' ? '物流司机' : '随行人员'" :colon="false" style="font-weight: bold; color: black"> </a-form-item>
              </a-col>
              <a-col :span="22">
                <a-row>
                  <a-col :span="8">
                    <a-form-item label="手机号" name="visitedName" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                      <a-input :disabled="isEdit" @blur="getPersonInfo(item, index)" v-model:value="item.fdPerPhone"></a-input>
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item label="姓名" name="visitedName" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                        <a-input :disabled="isEdit || index == 0" v-model:value="item.fdPerName"></a-input>
                    </a-form-item>
                  </a-col>
                  <a-col :span="10">
                    <a-form-item label="身份证号" name="visitedName" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                        <a-input :disabled="isEdit || index == 0" v-model:value="item.fdPerCard"></a-input>
                    </a-form-item>
                  </a-col>
                </a-row>
              </a-col>
            </a-row>
          </div>
          
          <div>
            <div class="visitor-title">
              <span> {{isEdit ? '备案信息：（只能减少备案厂区和备案时间，如需增加请重新提交流程）' : '备案信息'}} </span>
            </div>
            <a-row class="row-ft">
              <a-col>
                <a-form-item label="备案厂区" label-align="left" :wrapper-col="{ span: '16' }" name="fdFilingFactory">
                  <a-select mode="multiple"
                    v-model:value="formState.fdFilingFactory"
                    :options="showFactorys"
                    >
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row class="row-ft">
              <a-col>
                <a-form-item label="备案时间" :wrapper-col="{ span: '16' }" name="rangePicker">
                  <a-range-picker
                    @calendarChange="onCalendarChange"
                    :disabled-date="disabledDate"
                    v-model:value="rangePicker"
                    value-format="YYYY-MM-DD"
                    @openChange="onOpenChange"
                    :placeholder="['开始日期', '结束日期']"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </div>
        </a-form>
      </div>
    </BasicModal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, unref, computed } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { useUserStore } from '/@/store/modules/user';
  import { message } from 'ant-design-vue'
  import {getEnterProtectByCarNo, selectEnterRegisterByPhone, factoryList, checkAuthEmpInfo, insertEnterFiling, detail, updateEnterFiling} from './api.ts'
import dayjs, { Dayjs } from 'dayjs';
  const {getUserInfo}: {getUserInfo: any} = useUserStore();
  const emit = defineEmits(['success'])
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  const rangePicker = ref<string[]>([])
  const disabledRangePicker = ref<string[]>([])
  const isEdit = ref(false)
  const enterTypeOptions = [
    {label: "送货", value: '10'},
    {label: "取货", value: '20'},
  ]
  const formState = ref({
    id: '',
    fdEnterTypeCode: '10',
    fdCarNo: '',
    fdCarBelong: '',
    fdDriverName: '',
    fdProtectGrade: '',
    fdDetailedImg: '',
    fdFilingFactory: []
  })
  const dockLists = ref([
    {fdDockName: '', fdDockNo: '', fdDockPhone: '',fdDockDepartment: ''},
  ])
  const perList = ref([
    {fdPerTypeCode: '10', fdPerPhone: '', fdPerName: '', fdPerCard: '',},
    {fdPerTypeCode: '20', fdPerPhone: '', fdPerName: '', fdPerCard: '',},
    {fdPerTypeCode: '20', fdPerPhone: '', fdPerName: '', fdPerCard: '',}
  ])
  const refModal = ref<any>();
  // 注册 modal
  const [registerModal, { setModalProps, closeModal, changeOkLoading }] = useModalInner(async (data) => {
    setModalProps({ loading: false, confirmLoading: false });
    if (data.record.id) {
      isEdit.value = true
      getInfo(data.record.id)
    }else{
      getCurDept()
      isEdit.value = false
    }
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
  });
  const getInfo = async(id) => {
    const res = await detail({id})
    const info = JSON.parse(res)
    console.log(info, 'info');
    const { 
      fdCarNo, fdCarBelong, fdDriverName, fdProtectGrade,
      fdDetailedImg, approveFilingFactoryCode, dockList, perList: per,
      approveFilingBegin, approveFilingEnd, fdEnterTypeCode
     } = info
    formState.value = {
      id,
      fdEnterTypeCode,
      fdCarNo,
      fdCarBelong,
      fdDriverName,
      fdProtectGrade,
      fdDetailedImg,
      fdFilingFactory: approveFilingFactoryCode.split(',')
    }
    rangePicker.value = [approveFilingBegin, approveFilingEnd]
    disabledRangePicker.value = [approveFilingBegin, approveFilingEnd]
    dockLists.value = dockList
    perList.value = per
    // const perItem = {fdPerTypeCode: '20', fdPerPhone: '', fdPerName: '', fdPerCard: '',}
    // if (per.length == 1) {
    //   perList.value = [...per, perItem, perItem]
    // }else if (per.length == 2){
    //   perList.value = [...per, perItem]
    // }else{
    //   perList.value = per
    // }
    
  }
  const getCarInfo = async () => {
    if (!formState.value.fdCarNo) return
    const res:any = await getEnterProtectByCarNo({carNo: formState.value.fdCarNo})
    if (res) {
      const {fdCarBelong, fdDriverName, fdProtectGrade, fdDetailedImg} = res
      formState.value = {
        ...formState.value,
        fdCarBelong,
        fdDriverName,
        fdProtectGrade,
        fdDetailedImg
      }
    }else{
      message.warning(`${formState.value.fdCarNo}无备案记录，首次备案需由物流司机提交备案流程。`)
      formState.value.fdCarNo = ''
      formState.value.fdCarBelong = ''
      formState.value.fdDriverName = ''
      formState.value.fdProtectGrade = ''
      formState.value.fdDetailedImg = ''
    }
  }
  const getPersonInfo = async(item, index) => {
    if(!item.fdPerPhone) return
    try {
      const res = await selectEnterRegisterByPhone({phone: item.fdPerPhone})
      const {fdName, fdCardNo} = res
      item.fdPerName = fdName
      item.fdPerCard = fdCardNo
    } catch (error) {
      if (index == 0){
        item.fdPerPhone = ''
        item.fdPerName = ''
        item.fdPerCard = ''
      }
    }
  }
  
  const userFactoryCodes = ref<string[]>([])
  const fdLimitDay = ref(1)
  const getCurDept = async() => {
    const {realName, phone, workNo} = getUserInfo
    const res = await checkAuthEmpInfo({empNo: getUserInfo.workNo})
    console.log(realName, phone, workNo, res);
    
    fdLimitDay.value = res.fdLimitDay
    dockLists.value = [{
      fdDockName: realName,
      fdDockNo: workNo,
      fdDockPhone: phone,
      fdDockDepartment: res.fdEmpDepartment
    }]
    if (res.fdFactoryCode) {
      userFactoryCodes.value = res.fdFactoryCode.split(',')
    }
  }
  
  
  const factory = ref<{label: string, value: string}[]>([])
  const getFactorys = async() => {
    const res = await factoryList()
    if (res.length > 0) {
      factory.value = res.map(item => ({label: item.fieldName, value: item.id.toString()}))
    }
  }
  getFactorys()
  const showFactorys = computed(() => {
    if(isEdit.value) {
      return factory.value.filter(item => formState.value.fdFilingFactory.includes(item.value))
    }
    
    if(userFactoryCodes.value.length > 0) {
      return factory.value.filter(item => userFactoryCodes.value.includes(item.value))
    }else {
      return factory.value
    }
  })
  // 提交
  const submit = async () => {
    const {fdCarNo, fdCarBelong, fdDriverName, fdProtectGrade, fdDetailedImg, fdFilingFactory, id, fdEnterTypeCode} = formState.value
    const {realName, workNo} = getUserInfo
    const factorys = showFactorys.value.filter(item => fdFilingFactory.includes(item.value))
    const facoryNames = factorys.map(item => item.label)
    if (!formState.value.fdCarNo) {
      message.warning('请填写车牌号')
      return
    }
    if(!perList.value[0].fdPerPhone){
      message.warning('请填写司机信息')
      return
    }

    const perSet = perList.value.map(item => [...new Set(Object.values(item))].filter(item => item != ''))
    console.log(perSet);
    
    const somePerSet = perSet.some(item => (item.length < 4 && item.length > 1))
    if (somePerSet) {
      message.warning('请完善人员信息')
      return
    }
    if (formState.value.fdFilingFactory.length == 0) {
      message.warning('请选择备案厂区')
      return
    }
    if (rangePicker.value.length == 0){
      message.warning('请选择备案时间')
      return
    }
    changeOkLoading(true)
    const formData = {
      filing: {
        id,
        fdCarNo,
        fdEnterTypeCode,
        fdEnterType: fdEnterTypeCode == '10'? '送货' : '取货',
        fdFilingBegin: rangePicker.value[0],
        fdFilingEnd: rangePicker.value[1],
        fdFilingFactoryCode: fdFilingFactory.join(','),
        fdFilingFactory: facoryNames.join(','),
      },
      prt: {
        fdCarNo,
        fdCarBelong,
        fdDriverName,
        fdProtectGrade,
        fdDetailedImg
      },
      dock: dockLists.value,
      per: perList.value.filter(item => item.fdPerPhone != ''),
      userCode: workNo,
      userName: realName,
    }
    if (isEdit.value) {
      await updateEnterFiling({...formData})
    }else{
      await insertEnterFiling(formData)
    }
    emit('success');
    message.success('操作成功')
    closeModal();
    changeOkLoading(false);
    beforeClose()

  }
  const dates = ref();
  const onCalendarChange = (value: string[]) => {
    dates.value = value;
    
  }
  const onOpenChange = (open: boolean) => {
    dates.value = [] as any;
  };
  const disabledDate = (current: Dayjs) => {
    if (isEdit.value) {
      return current < dayjs(disabledRangePicker.value[0]) || current > dayjs(disabledRangePicker.value[1])
    }
    if (!dates.value || dates.value.length === 0) {
      return current < dayjs().subtract(1, 'days');
    }else{
      const tooLate = dates.value[0] && current.diff(dates.value[0], 'days') >= fdLimitDay.value;
      const tooEarly = dates.value[1] && dayjs(dates.value[1]).diff(current, 'days') >= fdLimitDay.value;
      return tooEarly || tooLate;
    }
    return 
  }
  const beforeClose = () => {
    formState.value = {
      id: '',
      fdEnterTypeCode: '10',
      fdCarNo: '',
      fdCarBelong: '',
      fdDriverName: '',
      fdProtectGrade: '',
      fdDetailedImg: '',
      fdFilingFactory: []
    }
    dockLists.value = [
      {fdDockName: '', fdDockNo: '', fdDockPhone: '',fdDockDepartment: ''},
    ]
    perList.value = [
      {fdPerTypeCode: '10', fdPerPhone: '', fdPerName: '', fdPerCard: '',},
      {fdPerTypeCode: '20', fdPerPhone: '', fdPerName: '', fdPerCard: '',},
      {fdPerTypeCode: '20', fdPerPhone: '', fdPerName: '', fdPerCard: '',},
    ]
    rangePicker.value = []
    disabledRangePicker.value = []
    return true
  }
</script>
<style lang="less" scoped>
  ::v-deep(.ant-modal-content) {
    border-radius: 4px !important;
    padding: 20px !important;
  }
  ::v-deep(.ant-modal-footer) {
    text-align: center;
    .ant-btn:nth-child(1) {
      width: 80px;
      border-color: #797979;
      border-radius: 4px;
    }
    .ant-btn:nth-child(2) {
      width: 80px;

      border-radius: 4px;
      margin-left: 20px;
    }

    .ant-btn:nth-child(3) {
      width: 80px;
      margin-left: 20px;
      border-radius: 4px;
    }
  }
  .visitor-title {
    width: 100%;
    line-height: 30px;
    background-color: rgba(242, 242, 242, 1);
    color: #1664ff;
    margin-bottom: 30px;
    padding-left: 25px;
    border: 1px solid #797979;
    border-radius: 4px;
    span {
      border-left: 4px solid #1664ff;
      padding-left: 5px;
    }
  }
  .visitor-avatar {
    max-height: 100%;
  }
  .takePhoto {
    position: absolute;
    right: -27px;
    top: 40px;
    cursor: pointer;
    color: #1664ff;
  }
  .row-tl {
    // margin-left: 50px;
    margin-bottom: 10px;
  }
  .row-zl {
    // margin-left: 35px;
    margin-bottom: 10px;
    .ant-input {
      border-color: #797979;
      border-radius: 4px;
    }
    a {
      text-decoration: underline;
    }
  }
  .row-zl2 {
    margin-top: -10px;
    .ant-input {
      border-color: #797979;
      border-radius: 4px;
    }
  }

  ::v-deep(.ant-select-selector) {
    border-color: #797979 !important;
    border-radius: 4px !important;
  }
  ::v-deep(.ant-picker) {
    border-color: #797979;
    border-radius: 4px;
    width: 100%;
  }

  ::v-deep(.ant-form-item-control-input-content) {
    white-space: nowrap;
    overflow: hidden; /* 隐藏超出部分的文本 */
    text-overflow: ellipsis;
    display: inline;
    cursor: pointer;
  }

  .row-zl::v-deep {
      .ant-form-item {
        display: flex;

        .ant-form-item-label {
          width: auto;
        }

        .ant-form-item-content {
          margin-left: unset;
          flex: 1;
        }
      }
  }
  .row-zl2::v-deep {
    .ant-col:nth-child(1) {
      .ant-form-item {
        display: flex;

        .ant-form-item-label {
          width: unset;
        }

        .ant-form-item-content {
          margin-left: unset;
          flex: 1;
        }
      }
    }
  }

  .row-ft::v-deep {
    .ant-col {
      .ant-form-item {
        display: flex;

        .ant-form-item-label {
          width: unset;
        }

        .ant-form-item-content {
          margin-left: unset;
          flex: 1;
        }
      }
    }
  }
</style>
<style lang="less">
  .visitor-box {
    .ant-input[disabled] {
      color: #000000;
    }
    .ant-select-disabled {
      color: #000000;
    }
    .ant-picker .ant-picker-input > input[disabled] {
      color: #000000;
    }
    .ant-row .ant-select-single.ant-select-show-arrow.ant-select-disabled .ant-select-selection-item {
      color: #000000;
    }
  }
</style>
