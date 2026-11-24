<template>
  <ConfigProvider :locale="getAntdLocale">
    <AppProvider>
      <RouterView />
    </AppProvider>
  </ConfigProvider>
</template>

<script lang="ts" setup>
  import { ConfigProvider } from 'ant-design-vue';
  import { AppProvider } from '/@/components/Application';
  import { useTitle } from '/@/hooks/web/useTitle';
  import { useLocale } from '/@/locales/useLocale';
  // import { useRoute, useRouter } from 'vue-router';
  // const router = useRouter()
  // router.beforeEach((from, to) => {
  //   console.log(from, to);
  // })
  // const route = useRoute()
  // if (route.query.redirect) {

  //   localStorage.setItem('redirect', route.query.redirect as string)
  // }
  // console.log(route.query, 'query');
  
  // 解决日期时间国际化问题
  import 'dayjs/locale/zh-cn';
  // support Multi-language
  const { getAntdLocale } = useLocale();

  useTitle();
  
</script>
