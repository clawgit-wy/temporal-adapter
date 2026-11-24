// 单点登录核心类
import { getToken } from '/@/utils/auth';
import { getUrlParam } from '/@/utils';
import { useGlobSetting } from '/@/hooks/setting';
import { validateCasLogin } from '/@/api/sys/user';
import { useUserStore } from '/@/store/modules/user';
import { defHttp } from '/@/utils/http/axios';
const globSetting = useGlobSetting();
const openSso = globSetting.openSso;
function deleteAllCookiesForDomain(domain) {
  const cookies = document.cookie.split(";");

  for (let i = 0; i < cookies.length; i++) {
    const cookie = cookies[i];
    const eqPos = cookie.indexOf("=");
    const name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/;domain=" + domain;
  }
}
export function useSso() {
  // let locationUrl = 'http://' + window.location.host + '/';
  /**
   * 单点登录
   */
  async function ssoLogin() {
    if (openSso == 'true') {
      let token = getToken();
      let ticket = getUrlParam('code');
      const userStore = useUserStore();
      if (!token) {
        if (ticket) {
          await validateCasLogin({
            code: ticket,
            // service: locationUrl,
          }).then((res) => {
            userStore.setToken(res.tocken);
            const history_url = localStorage.getItem('history_url')
            if (history_url) {
              return userStore.afterLoginAction(false, {...res});
            }else{
              return userStore.afterLoginAction(true, {...res});
            }
          });
        } else {
          localStorage.setItem('history_url', location.href)
          // window.location.href = globSetting.casBaseUrl + '/login?service=' + encodeURIComponent(locationUrl);
          window.location.href = globSetting.casBaseUrl + globSetting.ssoCode ;
        }
      }
    }
  }

  /**
   * 退出登录
   */
  async function ssoLoginOut(reload = '') {
    // await defHttp.post({ url: '/safety/login/authc/logout' })
    localStorage.clear()
    localStorage.setItem('history_url', location.href)
    if (!reload) {
      deleteAllCookiesForDomain('yutong.com')
    }
    // window.location.href = globSetting.casBaseUrl + '/logout?service=' + encodeURIComponent(locationUrl);
    window.location.href = globSetting.casBaseUrl + globSetting.ssoCode;
  }
  return { ssoLogin, ssoLoginOut };
}
