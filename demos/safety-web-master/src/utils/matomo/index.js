import { router } from '/@/router/index';
import VueMatomo from 'vue-matomo';
import { useUserStore } from '/@/store/modules/user';

export default function (app) {
  const userStore = useUserStore();
  console.log(userStore.getUserInfo, 'userStoreuserStoreuserStore');
  app.use(VueMatomo, {
    host: window.location.hostname === 'an.yutong.com' ? '//matomo.yutong.com/piwik/' : '//matomoqas.yutong.com/piwik/',
    siteId: window.location.hostname === 'an.yutong.com' ? '106' : '91',
    trackerFileName: 'matomo',
    router: router,
    requireConsent: false,
    trackInitialView: true,
    enableLinkTracking: true,
    userId: `${userStore.getUserInfo.realname} <${userStore.getUserInfo.username}> (${userStore.getUserInfo.workNo})`,
    debug: false,
  });
}