<template>
  <div class="container">
    <BasicModal
      v-bind="modalConfig"
      @register="register"
      :title="'流程提交说明'"
      :show-cancel-btn="false"
      :show-ok-btn="false"
      :closable="false"
      :maskClosable="true"
      :keyboard="false"
      class="process-modal"
    >
      <div class="process-tips">
        <div class="tips-content">
          <div class="tips-item">
            <div class="number-wrapper">
              <span class="number">1：</span>
            </div>
            <div class="text">如果仅申请厂区大门权限，提交大门权限申请流程;</div>
          </div>
          <div class="tips-item">
            <div class="number-wrapper">
              <span class="number">2：</span>
            </div>
            <div class="text">如果需要厂区内楼宇门禁权限，提交楼宇门禁申请流程即可，流程申请通过后将同步开通所选门禁所在的大门权限;</div>
          </div>
          <div class="tips-item">
            <div class="number-wrapper">
              <span class="number">3：</span>
            </div>
            <div class="text">员工大门权限开通将同步开通名下车辆进出厂权限</div>
          </div>
          <div class="tips-item">
            <div class="number-wrapper">
              <span class="number">4：</span>
            </div>
            <div class="text">外协外包人员仅限通过本流程申请楼宇门禁权限，厂区大门权限调整请联系内部对接人提交外协外包进出权限调整流程，路径：EIP-服务台-保卫物业-外协外包进出权限调整。</div>
          </div>
        </div>
        <div class="button-group">
          <a-button class="button-item" @click="handleBuildingAccess">
            <span class="btn-text">楼宇门禁权限申请</span>
          </a-button>
          <a-button class="button-item" @click="handleGateAccess">
            <span class="btn-text">大门权限申请</span>
          </a-button>
        </div>
      </div>
    </BasicModal>
  </div>
</template>

<script lang="ts" setup name="gateAccessControl-gateAccessRequestProcess-main">
import { reactive } from 'vue';
import { BasicModal, useModal } from '/@/components/Modal';
import { useRouter } from 'vue-router';
import { useMessage } from '/@/hooks/web/useMessage';
import { addFullParam } from '/@/utils/router/fullParam';

const router = useRouter()
import { useUserStore } from '/@/store/modules/user';
const userInfo = useUserStore().getUserInfo;

const { createMessage } = useMessage();

// 注册弹窗
const [register] = useModal();

// 弹窗配置
const modalConfig = reactive({
  visible: true,
  width: 600,
  confirmLoading: false,
  footer: null,
});

// 处理楼宇门禁权限申请
function handleBuildingAccess() {
  console.log('点击了楼宇门禁权限申请');
  router.push(addFullParam({
    path: '/accessRequest'
  }));
}

// 处理大门权限申请
function handleGateAccess() {

  if(userInfo?.workNo.startsWith('9')){
    router.push(addFullParam({
      path: '/gateAccessRequestProcess/form',
      query: {
        roleType: '2'
      }
    }));
  } else {
    createMessage.error('只有正式员工可以自行申请大门权限申请，外协员工需联系对接人申请');
  }
  console.log('点击了大门权限申请');
}
</script>

<style lang="less" scoped>
:deep(.ant-modal-header) {
  border-bottom: none;
  padding: 16px 24px 8px;
}

:deep(.ant-modal-title) {
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  color: #1e1e1e;
}

:deep(.ant-modal-body) {
  padding: 8px 24px 24px;
}

:deep(.ant-modal-content) {
  border-radius: 8px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.container {
  padding: 0;
}

.process-tips {
  padding: 0;
  
  .tips-content {
    margin-bottom: 24px;
    background-color: #f9fafc;
    border-radius: 8px;
    padding: 16px 18px;
    
    .tips-item {
      display: flex;
      margin-bottom: 14px;
      align-items: flex-start;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .number-wrapper {
        margin-right: 0px;
        flex-shrink: 0;
        text-align: left;
        display: flex;
        align-items: center;
        
        .number {
          font-size: 14px;
          font-weight: 500;
          color: #1890ff;
          line-height: 1.5;
        }
      }
      
      .text {
        color: #8c8c8c;
        font-size: 14px;
        line-height: 1.5;
      }
    }
  }
  
  .button-group {
    display: flex;
    justify-content: center;
    gap: 24px;
    margin-top: 20px;
    
    .button-item {
      min-width: 150px;
      height: 38px;
      font-size: 14px;
      border-radius: 4px;
      border: none;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s;
      color: white;
      font-weight: 400;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      background-color: #1890ff;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        background-color: #40a9ff;
      }
      
      .btn-text {
        position: relative;
      }
    }
  }
}
</style> 
