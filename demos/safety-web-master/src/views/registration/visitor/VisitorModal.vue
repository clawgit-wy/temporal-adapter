<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <div class="visitor-box">
      <a-form ref="formRef" :label-col="labelCol" :model="formState" :wrapper-col="wrapperCol">
        <div class="visitor-title">被访人信息</div>
        <a-row>
          <a-col :span="span" style="display: none">
            <a-form-item label="被访人姓名" name="id">
              <a-input v-model:value="formState.id" />
            </a-form-item>
          </a-col>
          <a-col :span="span">
            <a-form-item label="被访人姓名" name="visitedName" required>
              <a-input v-model:value="formState.visitedName" :maxlength="16" :disabled="!!formState.id" />
            </a-form-item>
          </a-col>
          <a-col :span="span">
            <a-form-item label="被访人手机号" name="visitedPhone" required>
              <a-input v-model:value="formState.visitedPhone" :maxlength="11" :disabled="!!formState.id" />
            </a-form-item>
          </a-col>
        </a-row>
        <div class="visitor-title">访客信息</div>
        <a-row>
          <a-col :span="span" v-show="sheetList.name">
            <a-form-item label="访客姓名" name="visitorName"  :required="getRequired('name')">
              <a-input v-model:value="formState.visitorName" :maxlength="16" :disabled="!!formState.id" />
            </a-form-item>
          </a-col>
          <a-col :span="span" v-show="sheetList.idCard">
            <a-form-item label="访客身份证号" name="visitorIdCard"  :required="getRequired('idCard')">
              <span class="hairpinNo">
                <a-input v-model:value="formState.visitorIdCard" :maxlength="18" :disabled="!!formState.id" />
                <!-- <span class="hairpinNo1" v-if="!formState.id" @click="readID">读访客卡</span> -->
              </span>
            </a-form-item>
          </a-col>
          <a-col :span="span" v-show="sheetList.visitorUnit">
            <a-form-item label="访客单位" name="vendorUnit"  :required="getRequired('visitorUnit')">
              <a-input v-model:value="formState.vendorUnit" :maxlength="100" :disabled="!!formState.id" />
            </a-form-item>
          </a-col>
          <a-col :span="span" v-show="sheetList.phone">
            <a-form-item label="访客手机号" name="visitorPhone"  :required="getRequired('phone')">
              <a-input v-model:value="formState.visitorPhone" :maxlength="11" :disabled="!!formState.id" />
            </a-form-item>
          </a-col>
          <a-col :span="span" v-show="sheetList.thing">
            <a-form-item label="访问事由" name="vendorMatter"  :required="getRequired('thing')">
              <!-- <a-input v-model:value="formState.vendorMatter" :maxlength="100" :disabled="!!formState.id" /> -->
              <a-select v-model:value="formState.vendorMatter" placeholder="请选择访问事由" :disabled="!!formState.id" allowClear>
                <a-select-option v-for="(item, index) in filedList" :key="index" :value="item">{{ item }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="span" v-show="sheetList.licensePlate">
            <a-form-item label="车牌号" name="cardNo"  :required="getRequired('licensePlate')">
              <a-input v-model:value="formState.cardNo" :maxlength="11" :disabled="!!formState.id" />
            </a-form-item> 
          </a-col>
        <!-- </a-row> -->
        <template v-if="!!formState.id">
          <a-col :span="span">
            <a-form-item label="访问厂区" name="vendorFactory">
              <a-tooltip>
                <template #title>{{formState.vendorFactory}}</template>
                <div>
                  <a-input v-model:value="formState.vendorFactory" disabled />
                </div>
              </a-tooltip>
            </a-form-item>
          </a-col>
          <a-col :span="span">
            <a-form-item label="访问区域" name="vendorRegion">
              <a-input v-model:value="formState.vendorRegion" disabled />
            </a-form-item>
          </a-col>
          <a-col :span="span">
            <a-form-item label="绝密区" name="secretRegion">
              <a-input v-model:value="formState.secretRegion" disabled />
            </a-form-item>
          </a-col>
        </template>
        <!-- <a-row> -->
          <a-col :span="span" v-show="sheetList.startTime">
            <a-form-item label="入厂开始时间" name="startTime"  :required="getRequired('startTime')">
              <a-date-picker
                format="YYYY-MM-DD HH:mm"
                v-model:value="formState.startTime"
                :show-time="{ format: 'HH:mm' }"
                :disabled="!!formState.id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="span" v-show="sheetList.endTime">
            <a-form-item label="入厂结束时间" name="endTime"  :required="getRequired('endTime')">
              <a-date-picker
                format="YYYY-MM-DD HH:mm"
                v-model:value="formState.endTime"
                :show-time="{ format: 'HH:mm' }"
                :disabled="!!formState.id"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="span" v-show="sheetList.endTime">
            <a-form-item label="证件照片" name="photo"  :required="getRequired('photo')">
              <a-upload
                v-model:file-list="fileList"
                name="file"
                list-type="picture-card"
                class="avatar-uploader"
                :show-upload-list="false"
                :action="useGlobSetting().domainUrl + '/safety/visitor/picUpload'"
                :before-upload="beforeUpload"
                @change="handleChange"
                :headers="headers"
              >
                <img v-if="formState.photo" :src="formState.photo" class="visitor-avatar"  alt="avatar" />
                <div v-else>
                  <loading-outlined v-if="loading" />
                  <plus-outlined v-else />
                  <div class="ant-upload-text">上传</div>
                </div>
              </a-upload>
              <span @click="takePhoto" class="takePhoto">拍照获取照片</span>
            </a-form-item>
          </a-col>
        </a-row>
        <template v-if="!!formState.id">
          <div class="visitor-title">访客发卡</div>
          <a-row>
            <a-col :span="span">
              <a-form-item label="卡号" name="hairpinNo" required>
                <span class="hairpinNo">
                  <a-input v-model:value="formState.hairpinNo" disabled />
                  <span class="hairpinNo1" @click="readID">读访客卡</span>
                </span>
              </a-form-item>
            </a-col>
          </a-row>
        </template>
        <!-- <div class="visitor-title">回收访客卡</div> -->
        <!-- <a-row>
          <a-col :span="span">
            <a-form-item label="发放卡号" name="hairpinNo">
              <a-input v-model:value="formState.hairpinNo" />
            </a-form-item>
            <a-form-item label="回收卡号" name="recoveryNo">
              <a-input v-model:value="formState.recoveryNo" />
            </a-form-item>
          </a-col>
        </a-row> -->
      </a-form>
    </div>
    <PhotoModal @register="registerPhotoModal" @success="photoSuccess" />
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref, watch, reactive } from 'vue';
  // import { Form } from 'ant-design-vue';
  import { BasicModal, useModalInner, useModal } from '/@/components/Modal';
  import { rules } from './visitor.data';
  import dayjs from 'dayjs';
  import { getCardFactoryList, saveOrUpdate, uploadFile } from './visitor.api';
  import { useGlobSetting } from '/@/hooks/setting';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { FileInfo } from './type';
  import PhotoModal from './PhotoModal.vue';
  import { getToken } from '/@/utils/auth'
  import { message } from 'ant-design-vue';
  import { useUserStore } from '/@/store/modules/user';
  const {getUserInfo} = useUserStore();
  const props = defineProps(['readIdorIC', 'readIDObj','sheetList', 'filedList']);
  // form use
  const formState = ref({});
  const formRules = ref({ ...rules });
  const formRef = ref();
  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  const isUpdate = ref<boolean>(false);
  const span = ref<Number>(12);
  const labelCol = ref({ span: 7 });
  const wrapperCol = ref({ span: 11 });
  const { createMessage } = useMessage();
  // const type = ref<string>('');
  const headers = reactive({
    'X-Access-Token': getToken(),
  });
  // upload
  const fileList = ref([]);
  const loading = ref<boolean>(false);
  const imageUrl = ref<string>('');

  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    isUpdate.value = unref(data.isUpdate);
    title.value = unref(data.title);
    // type.value = unref(data.type);
    await formRef.value.resetFields();
    imageUrl.value = '';
    console.log(data.record, '44444444444');
    // await formRef.value.setFieldsValue({ ...data.record });
    formState.value = unref(data.record);
    // 隐藏底部时禁用整个表单
    // setProps({ disabled: !data?.showFooter })
  });
  const [registerPhotoModal, photoModalFun] = useModal();
  const roleIds = ref([])
  async function onSubmit() {
    if (formState.value.factoryId && formState.value.factoryId.length > 0) {
      const enteryFactoryIds = formState.value.factoryId.split(';')
      const checkFlag = enteryFactoryIds.some(item => roleIds.value.includes(item))
      if (!checkFlag) {
        message.error('无访问厂区发卡权限！')
        return
      }
    }
    try {
      await formRef.value.validate();
      setModalProps({ confirmLoading: true });
      if (formState.value.startTime && formState.value.endTime) {
        let startTime = dayjs(formState.value.startTime).format("YYYY-MM-DD HH:mm");
        startTime = new Date(formState.value.startTime).getTime();
        let endTime = dayjs(formState.value.endTime).format("YYYY-MM-DD HH:mm");
        endTime = new Date(formState.value.endTime).getTime();
        if(!(endTime>startTime)) {
          createMessage.error("入厂结束时间需大于入厂开始时间");
          setModalProps({ confirmLoading: false });
          return;
        }
      }
      if (formState.value.id) {
        await saveOrUpdate({ ...formState.value, vendorState: 1 }, true);
      } else {
        const params = { ...formState.value, vendorState: 3 };
        if (!params.endTime) {
          params.endTime = dayjs().format("YYYY-MM-DD") + " 23:59:00";
        }
        await saveOrUpdate(params, false);
      }
      //关闭弹窗
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }

  function getRequired(key) {
    if (props.sheetList && props.sheetList[key]) {
      console.log(key, props.sheetList[key].mustSet)
      return props.sheetList[key].mustSet;
    }
  }

  function handleChange(info: FileInfo) {
    if (info.file.status === 'done') {
      // Get this url from response in real world.
      const { response } = info.file;
      if (response?.code === '0') {
        formState.value.photo = response?.data;
      }
    }
  }
  function beforeUpload(file) {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
    if (!isJpgOrPng) {
      console.log('支持的格式');
    }
    return isJpgOrPng;
  }

  async function readID() {
    const res = await props.readIdorIC();
    if (res.resultFlag === 0) {
      formState.value.hairpinNo = res.ICCardSerial;
    }
  }
  function takePhoto() {
    photoModalFun.openModal(true, {
      title: '拍照',
      showFooter: true,
      record: {},
    });
  }

  // 拍照的照片链接
  function photoSuccess(url) {
    formState.value.photo = url;
  }
  getCardFactoryList({workNo: getUserInfo.workNo}).then(res => {
    if (res && res.length > 0) {
      roleIds.value = res.map(item => item.id.toString())
    }
  })

  watch(
    () => props.readIDObj,
    (data, old) => {
      console.log(data, old, 'data, olddata, old');
      if (!formState.value.id) {
        formState.value.visitorName = data.visitorName;
        formState.value.visitorIdCard = data.visitorIdCard;
      }
    }
  );
</script>
<style lang="less" scoped>
  .visitor-title {
    width: 100%;
    line-height: 30px;
    background-color: rgba(242, 242, 242, 1);
    color: #1664ff;
    margin-bottom: 20px;
    padding-left: 25px;
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
</style>
<style lang="less">
.visitor-box {
  .ant-input[disabled] {
    color:#000000;
  }
  .ant-select-disabled {
    color:#000000;
  }
  .ant-picker .ant-picker-input >input[disabled] {
    color:#000000;
  }
  .ant-row .ant-select-single.ant-select-show-arrow.ant-select-disabled .ant-select-selection-item {
    color:#000000;
  }
}
</style>