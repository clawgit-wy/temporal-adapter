<template>
  <div class="approve_result">
    <div class="approve_resultcontent">
      <img src="/resource/img/EIP_ts1.jpg" class="app-loading-logo" alt="result" />
      <div class="approve_result_right">
        <span style="word-wrap: break-word; text-align: left; color: #a3a3a3">您的请求已提交，系统返回如下信息：</span>
        <span style="color: #3e9ece; font-size: 18px; font-weight: normal; margin: 8px 0 5px 0; padding-bottom: 8px">您的操作已成功！</span>
        <div class="approve_result_right_btns">
          <a-button type="primary" @click="close()">关闭</a-button>
          <a-button type="primary" @click="back">返回</a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import dayjs from 'dayjs';

  import { ref, unref, watch, reactive, onMounted, inject, createVNode } from 'vue';
  import { verifyBeforeApprove, approve, detail, fcList } from './api';
  import { factoryType, perLists, dockList } from './vehicle.data';
  import { useRoute, useRouter, RouteLocationNormalized } from 'vue-router';
  import { useTabs } from '/@/hooks/web/useTabs';
  import { Modal } from 'ant-design-vue';
  import { useGlobSetting } from '/@/hooks/setting';
  const { eipHome } = useGlobSetting();
  const route = useRoute();
  const router = useRouter();
  const entourage = ref<perLists[]>(); // 随行人员
  const logisticsDrivers = ref<perLists[]>(); // 物流司机
  const dockLists = ref<dockList[]>();
  const factory = ref<factoryType[]>(); // 备案工厂
  const title = ref<string>(''); // Modal title
  const rangePicker = ref<string[]>([]);
  import { useMultipleTabStore } from '/@/store/modules/multipleTab';
  import { ExclamationCircleOutlined } from '@ant-design/icons-vue/lib/icons';
  // let rangDate: string[] = [];
  const useTab = useMultipleTabStore();

  const formState = ref({
    fdStateCode: '',
    fdCarNo: '',
    fdEnterType: '',
    fdSupplierName: '',
    fdGoodsName: '',
    fdGoodsWeight: '',
    fdDetailedImg: '',
    fdProtectGrade: '',
    fdCarImg: '',
    fdCarDate: '',
    fdEngine: '',
    fdVin: '',
    fdFilingCarTypeName: '',
    fdCarBelong: '',
    fdDriverName: '',
    perList: [],
    fdFilingFactory: [],
    fdFilingFactoryCode: [],
    rangePicker: rangePicker,
    fdFilingBegin: '',
    fdFilingEnd: '',
    id: '',
    // dockList: [],
  }); // form 表单字段
  const isUpdate = ref<boolean>(false); // true:更新   false:添加
  const refModal = ref<any>();
  const formRef = ref();

  function close() {
    console.log('跳转地址==', eipHome);
    location.href = eipHome;
  }
  function back() {
    console.log('nack========');
    router.back();
  }
</script>
<style lang="less" scoped>
  .approve_result {
    display: flex;
    justify-content: center;
    align-items: center;
    .approve_resultcontent {
      display: flex;
      padding: 15px;
      border: 1px #508cee solid;
      border-radius: 5px;
      margin: 25px;
      background: white;
      align-items: center;
      .approve_result_right {
        display: flex;
        flex-direction: column;
        margin-left: 25px;
        .approve_result_right_btns {
          display: flex;
          justify-content: space-around;
        }
      }
    }
  }
</style>
