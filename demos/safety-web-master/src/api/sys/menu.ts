import { defHttp } from '/@/utils/http/axios';
// import { getMenuListResultModel } from './model/menuModel';
import { menu } from './menuData';

enum Api {
  GetMenuList = '/safety/login/authc/getUserMainMenus',
  SwitchVue3Menu = '/sys/switchVue3Menu',
}

/**
 * @description: Get user menu based on id
 */

export const getMenuList = () => {
  return new Promise((resolve) => {
    if (Array.isArray(menu)) {
      resolve(menu);
    }
    //为了兼容mock和接口数据
    // defHttp.get<getMenuListResultModel>({ url: Api.GetMenuList }).then((res) => {
    //   if (Array.isArray(res)) {
    //     resolve(res);
    //   } else {
    //     resolve(res['menu']);
    //   }
    // });
  });
};

/**
 * 切换成vue3菜单
 */
export const switchVue3Menu = () => {
  return new Promise((resolve) => {
    defHttp.get({ url: Api.SwitchVue3Menu });
  });
};
