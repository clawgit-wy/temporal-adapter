import { resultSuccess, resultError, getRequestToken, requestParams,baseUrl} from '../_util';
import { MockMethod } from 'vite-plugin-mock';
import { createFakeUserList } from './user';

// single
const dashboardRoute = {
  path: '/dashboard',
  name: 'Dashboard',
  component: 'LAYOUT',
  redirect: '/dashboard/analysis',
  meta: {
    title: 'routes.dashboard.dashboard',
    hideChildrenInMenu: true,
    icon: 'bx:bx-home',
  },
  children: [
    {
      path: 'analysis',
      name: 'Analysis',
      component: '/dashboard/Analysis/index',
      meta: {
        hideMenu: true,
        hideBreadcrumb: true,
        title: 'routes.dashboard.analysis',
        currentActiveMenu: '/dashboard',
        icon: 'bx:bx-home',
      },
    },
    {
      path: 'workbench',
      name: 'Workbench',
      component: '/dashboard/workbench/index',
      meta: {
        hideMenu: true,
        hideBreadcrumb: true,
        title: 'routes.dashboard.workbench',
        currentActiveMenu: '/dashboard',
        icon: 'bx:bx-home',
      },
    },
  ],
};

const backRoute = {
  path: 'back',
  name: 'PermissionBackDemo',
  meta: {
    title: 'routes.demo.permission.back',
  },

  children: [
    {
      path: 'page',
      name: 'BackAuthPage',
      component: '/demo/permission/back/index',
      meta: {
        title: 'routes.demo.permission.backPage',
      },
    },
    {
      path: 'btn',
      name: 'BackAuthBtn',
      component: '/demo/permission/back/Btn',
      meta: {
        title: 'routes.demo.permission.backBtn',
      },
    },
  ],
};

const authRoute = {
  path: '/permission',
  name: 'Permission',
  component: 'LAYOUT',
  redirect: '/permission/front/page',
  meta: {
    icon: 'carbon:user-role',
    title: 'routes.demo.permission.permission',
  },
  children: [backRoute],
};

const levelRoute = {
  path: '/level',
  name: 'Level',
  component: 'LAYOUT',
  redirect: '/level/menu1/menu1-1',
  meta: {
    icon: 'carbon:user-role',
    title: 'routes.demo.level.level',
  },

  children: [
    {
      path: 'menu1',
      name: 'Menu1Demo',
      meta: {
        title: 'Menu1',
      },
      children: [
        {
          path: 'menu1-1',
          name: 'Menu11Demo',
          meta: {
            title: 'Menu1-1',
          },
          children: [
            {
              path: 'menu1-1-1',
              name: 'Menu111Demo',
              component: '/demo/level/Menu111',
              meta: {
                title: 'Menu111',
              },
            },
          ],
        },
        {
          path: 'menu1-2',
          name: 'Menu12Demo',
          component: '/demo/level/Menu12',
          meta: {
            title: 'Menu1-2',
          },
        },
      ],
    },
    {
      path: 'menu2',
      name: 'Menu2Demo',
      component: '/demo/level/Menu2',
      meta: {
        title: 'Menu2',
      },
    },
  ],
};

const sysRoute = {
  path: '/system',
  name: 'System',
  component: 'LAYOUT',
  redirect: '/system/account',
  meta: {
    icon: 'ion:settings-outline',
    title: 'routes.demo.system.moduleName',
  },
  children: [
    {
      path: 'account',
      name: 'AccountManagement',
      meta: {
        title: 'routes.demo.system.account',
        ignoreKeepAlive: true,
      },
      component: '/demo/system/account/index',
    },
    {
      path: 'account_detail/:id',
      name: 'AccountDetail',
      meta: {
        hideMenu: true,
        title: 'routes.demo.system.account_detail',
        ignoreKeepAlive: true,
        showMenu: false,
        currentActiveMenu: '/system/account',
      },
      component: '/demo/system/account/AccountDetail',
    },
    {
      path: 'role',
      name: 'RoleManagement',
      meta: {
        title: 'routes.demo.system.role',
        ignoreKeepAlive: true,
      },
      component: '/demo/system/role/index',
    },

    {
      path: 'menu',
      name: 'MenuManagement',
      meta: {
        title: 'routes.demo.system.menu',
        ignoreKeepAlive: true,
      },
      component: '/demo/system/menu/index',
    },
    {
      path: 'dept',
      name: 'DeptManagement',
      meta: {
        title: 'routes.demo.system.dept',
        ignoreKeepAlive: true,
      },
      component: '/demo/system/dept/index',
    },
    {
      path: 'changePassword',
      name: 'ChangePassword',
      meta: {
        title: 'routes.demo.system.password',
        ignoreKeepAlive: true,
      },
      component: '/demo/system/password/index',
    },
  ],
};

const linkRoute = {
  path: '/link',
  name: 'Link',
  component: 'LAYOUT',
  meta: {
    icon: 'ion:tv-outline',
    title: 'routes.demo.iframe.frame',
  },
  children: [
    {
      path: 'doc',
      name: 'Doc',
      meta: {
        title: 'routes.demo.iframe.doc',
        frameSrc: 'https://vvbin.cn/doc-next/',
      },
    },
    {
      path: 'https://vvbin.cn/doc-next/',
      name: 'DocExternal',
      component: 'LAYOUT',
      meta: {
        title: 'routes.demo.iframe.docExternal',
      },
    },
  ],
};

// 人员管理
const membersRoute = {
  path: '/members',
  name: 'Members',
  component: 'LAYOUT',
  redirect: '/members/blacklist',
  meta: {
    title: '人员管理',
    // hideChildrenInMenu: true,
    icon: 'bx:bx-home',
  },
  children: [
    {
      path: 'blacklist',
      name: 'Blacklist',
      component: '/members/blacklist/index',
      meta: {
        title: '黑名单管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
  ],
}

const systemdRoute = {
  path: '/systemd',
  name: 'Systemd',
  component: 'LAYOUT',
  redirect: '/systemd/log',
  meta: {
    title: '系统管理',
    // hideChildrenInMenu: true,
    // icon: 'bx:bx-home',
  },
  children: [
    {
      path: 'log',
      name: 'Log',
      component: '/systemd/log/index',
      meta: {
        title: '日志管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'users',
      name: 'Userssystemd',
      component: '/systemd/users/index',
      meta: {
        title: '用户管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'form',
      name: 'Formmana',
      component: '/systemd/formmanage/index',
      meta: {
        title: '表单设置',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'roles',
      name: 'Roles',
      component: '/systemd/roles/index',
      meta: {
        title: '角色管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'field',
      name: 'Field',
      component: '/systemd/field/index',
      meta: {
        title: '字段管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'visitor',
      name: 'Visitor',
      component: '/systemd/visitor/index',
      meta: {
        title: '访客须知',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
  ],
}

const deviceRoute = {
  path: '/device',
  name: 'Device',
  component: 'LAYOUT',
  redirect: '/device/binding',
  meta: {
    title: '设备管理',
    // hideChildrenInMenu: true,
    // icon: 'bx:bx-home',
  },
  children: [
    {
      path: 'binding',
      name: 'Binding',
      component: '/device/binding/index',
      meta: {
        title: '区域设备绑定',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'empower',
      name: 'Empower',
      component: '/device/empower/index',
      meta: {
        title: '授权区域设备管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
  ],
}

const queryRoute = {
  path: '/query',
  name: 'Query',
  component: 'LAYOUT',
  redirect: '/query/parking',
  meta: {
    title: '数据查询',
    // hideChildrenInMenu: true,
    // icon: 'bx:bx-home',
  },
  children: [
    {
      path: 'visitor',
      name: 'Queryvisitor',
      component: '/query/visitor/index',
      meta: {
        title: '访客登记明细查询',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'parking',
      name: 'Parking',
      component: '/query/parking/index',
      meta: {
        title: '停车场授权记录',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
  ],
}

const registrationRoute = {
  path: '/registration',
  name: 'Registration',
  component: 'LAYOUT',
  redirect: '/registration/visitor',
  meta: {
    title: '登记管理',
    // hideChildrenInMenu: true,
    // icon: 'bx:bx-home',
  },
  children: [
    {
      path: 'visitor',
      name: 'Visitorregist',
      component: '/registration/visitor/index',
      meta: {
        title: '访客发卡',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
  ],
}
export default [
  {
    url: `${baseUrl}/sys/permission/getUserPermissionByToken`,
    timeout: 1000,
    method: 'get',
    response: (request: requestParams) => {
      const token = getRequestToken(request);
      if (!token) {
        return resultError('Invalid token!');
      }
      const checkUser = createFakeUserList().find((item) => item.token === token);
      if (!checkUser) {
        return resultError('Invalid user token!');
      }
      const id = checkUser.userId;
      let menu: Object[];
      menu = [ membersRoute, systemdRoute, deviceRoute, queryRoute, registrationRoute];
      // switch (id) {
      //   case '1':
      //     dashboardRoute.redirect = dashboardRoute.path + '/' + dashboardRoute.children[0].path;
      //     // menu = [dashboardRoute, authRoute, levelRoute, sysRoute, linkRoute, membersRoute, systemdRoute];
      //     menu = [ membersRoute, systemdRoute, deviceRoute, queryRoute, registrationRoute];
      //     break;
      //   case '2':
      //     dashboardRoute.redirect = dashboardRoute.path + '/' + dashboardRoute.children[1].path;
      //     menu = [dashboardRoute, authRoute, levelRoute, linkRoute];
      //     break;
      //   default:
      //     menu = [];
      // }

      return resultSuccess(menu);
    },
  },
] as MockMethod[];
