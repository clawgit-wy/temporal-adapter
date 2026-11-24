<template>
  <div>
    <PageWrapper title="授权配置" contentBackground>
      <a-tabs v-model:activeKey="activeKey">
        <!-- Tab1: 楼宇门禁 -->
        <a-tab-pane key="building" tab="楼宇门禁" force-render>
          <div class="auth-config-container">
            <BasicForm @register="registerForm" />
            <div class="mt-4 text-center">
              <a-button type="primary" @click="submitForm">保存</a-button>
            </div>
          </div>
        </a-tab-pane>

        <!-- Tab2: 访客授权-组织 -->
        <a-tab-pane v-if="hasPermission('systemd:authconfig:orgAuth')" key="orgAuth" tab="访客授权-组织">
          <OrgAuthConfig v-if="activeKey === 'orgAuth'" />
        </a-tab-pane>
        <!-- Tab3: 访客授权-人 -->
        <a-tab-pane v-if="hasPermission('systemd:authconfig:personAuth')" key="personAuth" tab="访客授权-人">
          <PersonAuthConfig v-if="activeKey === 'personAuth'" />
        </a-tab-pane>
      </a-tabs>
    </PageWrapper>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form';
  import { formSchema } from './authConfig.data';
  import { getAuthConfig, saveAuthConfig } from './authConfig.api';
  import { PageWrapper } from '/@/components/Page';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { usePermission } from '/@/hooks/web/usePermission';
  import OrgAuthConfig from './orgAuthConfig.vue';
  import PersonAuthConfig from './personAuthConfig.vue';

  const { createMessage } = useMessage();
  const { hasPermission } = usePermission();
  const activeKey = ref('building');

  const [registerForm, { setFieldsValue, validate }] = useForm({
    schemas: formSchema,
    showActionButtonGroup: false,
    baseColProps: { span: 24 },
    labelCol: { flex: '240px' },
    autoSetPlaceHolder: true,
    labelWidth: 230,
  });

  onMounted(async () => {
    await loadAuthConfig();
  });

  // 加载授权配置
  async function loadAuthConfig() {
    try {
      const result = await getAuthConfig();
      if (!result) return;
      const userNums = result?.offRemindUserId?.split(',');
      const userNames = result?.offRemindUser?.split(',');
      const userList = userNums?.map((num: string, index: number) => ({
        id: num,
        name: userNames[index],
      }));
      result.offRemindUser = userList;
      if (result) {
        setFieldsValue(result);
      }
    } catch (error) {
      createMessage.error('获取授权配置失败');
      console.error('获取授权配置失败', error);
    }
  }

  // 提交表单
  async function submitForm() {
    try {
      const values = await validate();
      const offRemindUser = values.offRemindUser?.map((item: any) => item.name)?.join(',');
      const offRemindUserId = values.offRemindUser?.map((item: any) => item.id)?.join(',');
      delete values.offRemindUser;
      values.offRemindUser = offRemindUser;
      values.offRemindUserId = offRemindUserId;
      await saveAuthConfig(values);
      createMessage.success('保存成功');
    } catch (error) {
      console.error('表单验证失败', error);
    }
  }
</script>

<style lang="less" scoped>
  :deep(.ant-tabs-nav) {
    padding-left: 10px;
  }

  :deep(.ant-tabs-content-holder) {
    padding: 0 10px;
  }

  .auth-config-container {
    background-color: #fff;
    padding: 24px;
    border-radius: 2px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  }

  .auth-config-container :deep(.ant-select),
  .auth-config-container :deep(.ant-input-number),
  .auth-config-container :deep(.ant-input) {
    width: 100% !important;
  }

  .auth-config-container :deep(.ant-form-item-control-input) {
    width: 100%;
  }

  .auth-config-container :deep(.ant-select-selector) {
    width: 100% !important;
  }
</style>
