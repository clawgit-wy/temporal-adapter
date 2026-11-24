// single
const dashboardRoute = {
  path: '/home',
  name: 'Home',
  component: 'LAYOUT',
  redirect: '/home/welcome',
  code: 'home',
  meta: {
    title: '主页',
    hideChildrenInMenu: true,
    icon: 'ant-design:appstore-outlined',
  },
  children: [
    {
      path: 'welcome',
      name: 'Welcome',
      component: '/dashboard/welcome/index',
      code: 'home',
      meta: {
        hideMenu: true,
        hideBreadcrumb: true,
        title: '主页',
        currentActiveMenu: '/home',
        icon: 'ant-design:appstore-outlined',
      },
    },
    // {
    //   path: 'workbench',
    //   name: 'Workbench',
    //   component: '/dashboard/workbench/index',
    //   meta: {
    //     hideMenu: true,
    //     hideBreadcrumb: true,
    //     title: 'routes.dashboard.workbench',
    //     currentActiveMenu: '/dashboard',
    //     icon: 'bx:bx-home',
    //   },
    // },
  ],
};

const emailInterceptSettingRoute = {
  path: '/external',
  name: 'External',
  code: '*',
  meta: {
    hideMenu: true,
    title: '外部页面',
  },
  children: [
    {
      path: 'emailsetting',
      name: 'EmailSetting',
      code: '*',
      meta: {
        hideMenu: true,
        title: '邮件设置',
      },
      component: '/external/emailSetting',
    },
  ],
};

// 人员管理
const membersRoute = {
  path: '/members',
  name: 'Members',
  component: 'LAYOUT',
  redirect: '/members/blacklist',
  code: 'members',
  meta: {
    title: '人员管理',
    // hideChildrenInMenu: true,
    // icon: 'bx:bx-home',
  },
  children: [
    {
      path: 'blacklist',
      name: 'Blacklist',
      component: '/members/blacklist/index',
      code: 'members:blacklist',
      meta: {
        title: '黑名单管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'organizational',
      name: 'organizational',
      component: '/members/organizational/index',
      code: 'members:organizational',
      meta: {
        title: '被访人白名单',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'employee',
      name: 'employee',
      component: '/members/employee/index',
      code: 'members:employee',
      meta: {
        title: '员工信息台账',
      },
    },
    {
      path: 'faceInfoQuery',
      name: 'faceInfoQuery',
      component: '/members/faceInfoQuery/index',
      code: 'members:faceInfoQuery',
      meta: {
        title: '人脸信息查询',
      },
    },
    {
      path: 'interviewed',
      name: 'interviewed',
      component: '/members/interviewed/index',
      code: 'members:interviewed',
      meta: {
        title: '访客被访人设置',
      },
    },
  ],
};

const systemdRoute = {
  path: '/systemd',
  name: 'Systemd',
  component: 'LAYOUT',
  redirect: '/systemd/log',
  code: 'systemd',
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
      code: 'systemd:log',
      meta: {
        title: '日志管理',

        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'users',
      name: 'Userssystemd',
      code: 'systemd:users',
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
      code: 'systemd:formmanage',
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
      code: 'systemd:roles',
      meta: {
        title: '角色管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'field',
      name: 'Field',
      code: 'systemd:field',
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
      code: 'systemd:notice',
      meta: {
        title: '访客须知',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'appswitch',
      name: 'Appswitch',
      component: '/systemd/appswitch/index',
      code: 'systemd:appswitch',
      meta: {
        title: '应用管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'noticemessage',
      name: 'Noticemessage',
      component: '/systemd/noticemessage/index',
      code: 'systemd:noticemessage',
      meta: {
        title: '提示信息',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'authconfig',
      name: 'AuthConfig',
      component: '/systemd/authConfig/index',
      code: 'systemd:authconfig',
      meta: {
        title: '授权配置',
      },
    },
  ],
};

const deviceRoute = {
  path: '/device',
  name: 'Device',
  component: 'LAYOUT',
  redirect: '/device/binding',
  code: 'device',
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
      code: 'device:binding',
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
      code: 'device:empower',
      meta: {
        title: '授权区域设备管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
  ],
};

const queryRoute = {
  path: '/query',
  name: 'Query',
  component: 'LAYOUT',
  redirect: '/query/parking',
  code: 'query',
  meta: {
    title: '数据查询',
    // hideChildrenInMenu: true,
    // icon: 'bx:bx-home',
  },
  children: [
    {
      path: 'epiboly',
      name: 'epiboly-look',
      component: 'securityService/outAndIn/epiboly/index',
      code: 'query:epiboly',
      // code: '*',
      meta: {
        title: '外协入厂权限查询',
      },
    },
    {
      path: 'visitor',
      name: 'Queryvisitor',
      component: '/query/visitor/index',
      code: 'query:visitor',
      meta: {
        title: '访客登记明细查询',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'cardrefund',
      name: 'Cardrefund',
      component: '/query/cardrefund/index',
      code: 'query:cardrefund',
      meta: {
        title: '访客未退卡明细查询',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'parking',
      name: 'Parking',
      component: '/query/parking/index',
      code: 'query:parking',
      meta: {
        title: '停车场授权记录',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'data',
      name: 'Data',
      component: '/query/data/index',
      code: 'query:data',
      meta: {
        title: '数据下发日志',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
  ],
};

const registrationRoute = {
  path: '/registration',
  name: 'Registration',
  component: 'LAYOUT',
  redirect: '/registration/visitorApproval',
  code: '*',
  meta: {
    title: '访客管理',
    // hideChildrenInMenu: true,
    // icon: 'bx:bx-home',
  },
  children: [
    {
      path: 'visitor',
      name: 'Visitorregist',
      component: '/registration/visitor/index',
      code: 'registration:visitor',
      meta: {
        title: '访客发卡',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
    {
      path: 'visitorApproval',
      name: 'VisitorApproval',
      component: '/registration/visitorApproval/index',
      code: '*',
      meta: {
        title: '访客审批授权表',
      },
    },
    {
      path: 'visitorApprovalApply',
      name: 'VisitorApprovalApply',
      component: '/registration/visitorApproval/apply/index',
      code: '*',
      hidden: true,
      meta: {
        title: '访客审批授权流程',
      },
    },
  ],
};

const SmartParkingRoute = {
  path: '/smartparking',
  name: 'SmartParking',
  component: 'LAYOUT',
  redirect: '/smartparking/noticemanager',
  code: 'smartparking',
  meta: {
    title: '智慧停车',
  },
  children: [
    // {
    //   path: 'noticemanager',
    //   name: 'NoticeManager',
    //   component: '/smartparking/noticemanager/index',
    //   code: 'registration:visitor',
    //   meta: {
    //     title: '通知管理',
    //   },
    // },
    {
      path: 'retentionRemind',
      name: 'retentionRemind',
      code: 'smartparking:retentionremind',
      meta: {
        title: '滞留提醒',
      },
      children: [
        {
          path: 'noticemanager',
          name: 'retentionNotice',
          component: '/smartparking/retentionRemind/retentionNotice/index',
          code: 'smartparking:retentionremind:noticemanager',
          meta: {
            title: '通知管理',
          },
        },
        {
          path: 'noticesendlog',
          name: 'noticeSendLog',
          component: '/smartparking/retentionRemind/noticeSendLog/index',
          code: 'smartparking:retentionremind:noticesendlog',
          meta: {
            title: '通知下发日志',
          },
        },
        {
          path: 'stopcars',
          name: 'stopCars',
          component: '/smartparking/retentionRemind/stopCars/index',
          code: 'smartparking:retentionremind:stopcars',
          meta: {
            title: '场内车辆信息',
          },
        },
        {
          path: 'noticewhitelist',
          name: 'noticeWhiteList',
          component: '/smartparking/retentionRemind/noticeWhiteList/index',
          code: 'smartparking:retentionremind:noticewhitelist',
          meta: {
            title: '通知白名单',
          },
        },
        {
          path: 'bleaveinfo',
          name: 'bleaveInfo',
          component: '/smartparking/retentionRemind/bleaveInfo/index',
          code: 'smartparking:retentionremind:bleaveinfo',
          meta: {
            title: '公出人员信息',
          },
        },
      ],
    },
    {
      path: 'checkcar',
      name: 'checkCar',
      code: 'smartparking:checkcar',
      meta: {
        title: '车辆检查管理',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
      children: [
        {
          path: 'checkcarrole',
          name: 'checkCarRole',
          component: '/smartparking/checkCar/checkCarRole/index',
          code: 'smartparking:checkcar:checkcarrole:list',
          meta: {
            title: '车辆检查规则',
          },
        },
        // {

        //   path: 'checkcarwhitelist',
        //   name: 'checkcarWhitelist',
        //   component: '/smartparking/checkCar/whiteList/index',
        //   code: 'smartparking:checkcar:whiteList:list',
        //   meta: {
        //     title: '车辆检查白名单',
        //   },
        // },
      ],
    },
    {
      path: 'privateCarData',
      name: 'privateCarData',
      component: '/smartparking/privateCarData/index',
      code: 'smartparking:privateCarData',
      meta: {
        title: '员工入厂私家车信息表',
      },
    },
  ],
};

const entryExitRoute = {
  path: '/entryExit',
  name: 'EntryExit',
  component: 'LAYOUT',
  redirect: '/entryExit/info',
  code: 'entryExit',
  meta: {
    title: '出入管理',
  },
  children: [
    {
      path: 'info',
      name: 'EntryExitInfo',
      component: '/entryExit/info/index',
      code: 'entryExit:info',
      meta: {
        title: '出入信息查询权限',
        // currentActiveMenu: '/dashboard',
        // icon: 'bx:bx-home',
      },
    },
  ],
};
const facilitiesCheck = {
  path: '/facilities',
  name: 'Facilities',
  component: 'LAYOUT',
  code: 'facilities',
  meta: {
    title: '设备设施点检',
  },
  children: [
    {
      path: 'facilitiesmanager',
      name: 'FacilitiesManager',
      code: 'facilities:facilitiesmanager',
      meta: {
        title: '设备管理',
      },
      children: [
        {
          path: 'facilitiermsg',
          name: 'FacilitiesMsg',
          component: 'facilities/facilitiesManager/facilitiesMsg/index',
          code: 'facilities:facilitiesmanager:facilitiermsg',
          meta: {
            title: '设备信息管理',
          },
        },
        {
          path: 'standardconfig',
          name: 'StandardConfig',
          component: 'facilities/facilitiesManager/standardConfig/index',
          code: 'facilities:facilitiesmanager:standardconfig',
          meta: {
            title: '设备巡检标准配置',
          },
        },
      ],
    },
    {
      path: 'tasksetting',
      name: 'taskSetting',
      component: 'facilities/taskSetting/index',
      code: 'facilities:tasksetting',
      meta: {
        title: '任务设置',
      },
    },
    // {
    //   path: 'approval',
    //   name: 'Approval',
    //   component: 'facilities/approval/index',
    //   code: '*',
    //   hidden: true,
    //   meta: {
    //     title: '流程审批',
    //   },
    // },
    {
      path: 'facilitiesquery',
      name: 'FacilitiesQuery',
      code: 'facilities:facilitiesquery',
      meta: {
        title: '数据查询',
      },
      children: [
        {
          path: 'log',
          name: 'facilitiesQueryLog',
          component: 'facilities/dataQuery/facilitiesLog/index',
          code: 'facilities:facilitiesquery:log',
          meta: {
            title: '设备巡检日志',
          },
        },
        {
          path: 'facilitiesQueryResult',
          name: 'facilitiesQueryResult',
          component: 'facilities/dataQuery/facilitiesResult/index',
          code: 'facilities:facilitiesquery:facilitiesQueryResult',
          meta: {
            title: '设备巡检结果',
          },
        },
      ],
    },
  ],
};
const patrol = {
  path: '/patrol',
  name: 'Patrol',
  component: 'LAYOUT',
  code: '*',
  meta: {
    title: '治安巡查',
  },
  children: [
    {
      path: 'tasksetting',
      name: 'patrolTaskSetting',
      component: 'patrol/taskSetting/index',
      code: 'patrol:tasksetting',
      meta: {
        title: '任务设置',
      },
    },
    {
      path: 'approval',
      name: 'patrolApproval',
      component: 'patrol/approval/index',
      code: '*',
      hidden: true,
      meta: {
        title: '巡查审批',
      },
    },
    {
      path: 'servicedata',
      name: 'serviceData',
      component: 'patrol/serviceData/index',
      code: 'patrol:servicedata',
      meta: {
        title: '巡查业务数据',
      },
    },

    {
      path: 'postcheck',
      name: 'postCheck',
      code: '*',
      meta: {
        title: '岗位验证',
      },
      children: [
        {
          path: 'monitorpostreportcheck',
          name: 'monitorPostReportCheck',
          component: 'patrol/postCheck/monitorPostReportCheck/index',
          code: 'patrol:postcheck:monitorpostreportcheck',
          meta: {
            title: '监控岗位验证结果',
          },
        },
        {
          path: 'monitorpostreport',
          name: 'monitorPostReport',
          component: 'patrol/postCheck/monitorPostReport/index',
          code: '*',
          meta: {
            title: '监控岗位上报',
          },
        },
        {
          path: 'monitorposapproval',
          name: 'monitorPostApproval',
          component: 'patrol/postCheck/monitorPostReport/approval',
          code: '*',
          hidden: true,
          meta: {
            title: '监控岗位审批',
          },
        },
        {
          path: 'spotPostCheck',
          name: 'spotPostCheck',
          component: 'patrol/postCheck/spotPostCheck/index',
          code: 'patrol:postcheck:spotPostCheck',
          meta: {
            title: '现场岗位验证结果',
          },
        },
        {
          path: 'spotPostCreate',
          name: 'spotPostCreate',
          component: 'patrol/postCheck/spotPostCheck/create',
          code: '*',
          meta: {
            title: '现场岗位上报',
          },
        },
        {
          path: 'simulatePostCheck',
          name: 'simulatePostCheck',
          component: 'patrol/postCheck/simulatePostCheck/index',
          code: 'patrol:postcheck:simulatePostCheck',
          meta: {
            title: '模拟岗位验证结果',
          },
        },
        {
          path: 'simulatePostCreate',
          name: 'simulatePostCreate',
          component: 'patrol/postCheck/simulatePostCheck/create',
          code: '*',
          meta: {
            title: '模拟岗位上报',
          },
        },
      ],
    },

    {
      path: 'businessValidate',
      name: 'BusinessValidate',
      code: 'patrol:businessValidate',
      meta: {
        title: '业务验证',
      },
      children: [
        {
          path: 'truckResult',
          name: 'TruckResult',
          component: 'patrol/businessValidate/truckResult/index',
          code: 'patrol:businessValidate:truckResult',
          meta: {
            title: '货车环保标准抽查结果',
          },
        },
        // {
        //   path: 'standardconfig',
        //   name: 'StandardConfig',
        //   component: 'patrol/dataQuery/patrolResult/index',
        //   code: 'smartparking:retentionremind:noticemanager',
        //   meta: {
        //     title: '设备巡查结果',
        //   },
        // }
      ],
    },
  ],
};
const securityService = {
  path: '/securityService',
  name: 'SecurityService',
  component: 'LAYOUT',
  code: '*',
  meta: {
    title: '安保服务',
  },
  children: [
    {
      path: 'outAndIn',
      name: 'outAndIn',
      code: '*',
      meta: {
        title: '出入管理',
      },
      children: [
        {
          path: 'epiboly',
          name: 'epiboly',
          component: 'securityService/outAndIn/epiboly/index',
          code: '*',
          meta: {
            title: '外协/外包人员管理',
          },
        },
        {
          path: 'epibolyLog',
          name: 'epibolyLog',
          component: 'securityService/outAndIn/epibolyLog/index',
          code: 'securityService:outAndIn:epibolyLog',
          meta: {
            title: '外协入厂权限变更',
          },
        },
        {
          path: 'initiate',
          name: 'initiate',
          hidden: true,
          component: 'securityService/outAndIn/epiboly/initiate',
          code: '*',
          meta: {
            title: '权限调整审批流',
          },
        },
      ],
    },
    {
      path: '/issueExceptionRecords',
      name: 'issueExceptionRecords',
      component: 'securityService/issueExceptionRecords/index',
      code: 'securityService:issueExceptionRecords', //securityService:issueExceptionRecords
      meta: {
        title: '员工权限下发异常记录',
      },
    },
    // {
    //   path: 'deviceManagement',
    //   name: 'deviceManagement',
    //   code: '*',
    //   meta: {
    //     title: '设备管理',
    //   },
    //   children: [
    //     {
    //       path: 'authArea',
    //       name: 'authArea',
    //       component: 'securityService/outAndIn/authArea/index',
    //       code: '*',
    //       meta: {
    //         title: '授权区域设备管理',
    //       },
    //     },
    //   ],
    // },
  ],
};

const accessAnalysisRoute = {
  path: '/accessAnalysis',
  name: 'AccessAnalysis',
  component: 'LAYOUT',
  redirect: '/accessAnalysis/exceptionRecord',
  code: 'accessAnalysis',
  meta: {
    title: '出入分析',
  },
  children: [
    {
      path: 'exceptionRecord',
      name: 'AccessAnalysisExceptionRecord',
      component: '/accessAnalysis/exceptionRecord/index',
      code: 'accessAnalysis:exceptionRecord',
      meta: {
        title: '异常进出记录',
      },
    },
    {
      path: 'hikDeviceConfig',
      name: 'AccessAnalysisHikDeviceConfig',
      component: '/accessAnalysis/hikDeviceConfig/index',
      code: 'accessAnalysis:hikDeviceConfig',
      meta: {
        title: '海康设备配置',
      },
    },
  ],
};

// 点检流程审批
const facilitiesApprovals = {
  path: '/facilities/approval',
  name: 'facilitiesApprovals',
  component: 'facilities/approval/index',
  code: '*',
  hidden: true,
  meta: {
    title: '流程审批',
  },
};
// 外协出入场流程审批
const secApprovals = {
  path: '/securityService/outAndIn/initiate',
  name: 'secApprovals',
  hidden: true,
  component: 'securityService/outAndIn/epiboly/initiate',
  code: '*',
  meta: {
    title: '权限调整审批流',
  },
};

const exitpass = {
  path: '/exitpass',
  name: 'exitpass',
  component: 'LAYOUT',
  code: '*',
  meta: {
    title: '出门证',
  },
  children: [
    {
      path: '/exitpass/goodsexitreview',
      name: 'goodsexitreview',
      component: '/exitpass/goods-exit-review/index',
      code: '*',
      hidden: true,
      meta: {
        title: '出门证审核',
      },
    },
    {
      path: '/exitpass/goodsexitpermit',
      name: 'goodsexitpermit',
      component: '/exitpass/goods-exit-permit/index',
      code: '*',
      meta: {
        title: '出门证申请',
      },
    },
    {
      path: '/exitpass/processdetail',
      name: 'processdetail',
      component: '/exitpass/process-detail/index',
      code: 'exitpass:processdetail',
      meta: {
        title: '出门证流程详情',
        keepAlive: true,
      },
    },
    {
      path: '/exitpass/abnormal',
      name: 'abnormal',
      component: '/exitpass/abnormal/index',
      code: 'exitpass:abnormal',
      meta: {
        title: '出门证异常明细',
      },
    },
    {
      path: '/exitpass/abnormal/create',
      name: 'abnormalCreate',
      component: '/exitpass/abnormal/create',
      code: '*',
      hidden: true,
      meta: {
        title: '违规整改上报',
      },
    },
    {
      path: '/exitpass/materialdetail',
      name: 'materialdetail',
      component: '/exitpass/exitpass-material-detail/index.vue',
      code: 'exitpass:materialdetail',
      meta: {
        title: '出门证物资明细',
      },
    },
    {
      path: '/exitpass/businessValidation',
      name: 'businessValidation',
      component: '/exitpass/businessValidation/index',
      code: 'exitpass:businessValidation',
      meta: {
        title: '业务验证台账',
      },
    },
    // {
    //   path: '/exitpass/exceptiondetail',
    //   name: 'exceptiondetail',
    //   component: '/exitpass/exitpass-exception-detail/index.vue',
    //   code: '*',
    //   meta: {
    //     title: '出门证异常详情',
    //   },
    // },
    // {
    //   path: '/exitpass/reportdetail',
    //   name: 'reportdetail',
    //   component: '/exitpass/report-detail/index.vue',
    //   code: '*',
    //   meta: {
    //     title: '事件上报流程明细',
    //   },
    // },
    // {
    //   path: '/exitpass/violationreport',
    //   name: 'violationreport',
    //   component: '/exitpass/violation-report/index.vue',
    //   code: '*',
    //   meta: {
    //     title: '出门证违规事件上报',
    //   },
    // },
    // {
    //   path: '/exitpass/remindconfig',
    //   name: 'remindconfig',
    //   component: '/exitpass/remind-config/index.vue',
    //   code: '*',
    //   meta: {
    //     title: '异常提醒设置',
    //   },
    // },
    // {
    //   path: '/exitpass/uploadlimit',
    //   name: 'uploadlimit',
    //   component: '/exitpass/upload-limit/index.vue',
    //   code: '*',
    //   meta: {
    //     title: '上传照片限制',
    //   },
    // },
    // {
    //   path: '/exitpass/rectificationreport',
    //   name: 'rectificationreport',
    //   component: '/exitpass/rectification-report/index.vue',
    //   code: '*',
    //   meta: {
    //     title: '出门证违规整改回复',
    //   },
    // },
    {
      path: '/exitpass/permissionconfig',
      name: 'permissionconfig',
      code: '*',//permissionconfig
      meta: {
        title: '权限配置',
      },
      children: [
        {
          path: '/exitpass/systemparamsconfig',
          name: 'systemparamsconfig',
          component: '/exitpass/system-params-config/index.vue',
          code: 'permissionconfig:systemparamsconfig',
          meta: {
            title: '系统参数配置',
          },
        },
        {
          path: '/exitpass/permissionconfig/people',
          name: 'permissionconfigpeople',
          component: '/exitpass/permission-config/index.vue',
          code: '*',//permissionconfig:permissionconfigpeople
          meta: {
            title: '审核人权限配置',
          },
        },
        // {
        //   path: '/exitpass/processtimelimitconfig',
        //   name: 'processtimelimitconfig',
        //   component: '/exitpass/process-time-limit-config/index.vue',
        //   code: '*',
        //   meta: {
        //     title: '流程时效设置',
        //   },
        // },
      ],
    },
    // {
    //   path: '/exitpass/fieldconfig',
    //   name: 'fieldconfig',
    //   code: '*',
    //   meta: {
    //     title: '字段配置',
    //   },
    //   children: [

    //     // {
    //     //   path: '/exitpass/fieldconfigexittype',
    //     //   name: 'fieldconfigexittype',
    //     //   component: '/exitpass/field-config-exittype/index.vue',
    //     //   code: '*',
    //     //   meta: {
    //     //     title: '出门类型',
    //     //   },
    //     // },
    //     // {
    //     //   path: '/exitpass/materialentrycontent',
    //     //   name: 'materialentrycontent',
    //     //   component: '/exitpass/material-entry-content/index.vue',
    //     //   code: '*',
    //     //   meta: {
    //     //     title: '物资填写内容',
    //     //   },
    //     // },
    //     // {
    //     //   path: '/exitpass/fieldconfigexitdetail',
    //     //   name: 'fieldconfigexitdetail',
    //     //   component: '/exitpass/field-config-exit-detail/index.vue',
    //     //   code: '*',
    //     //   meta: {
    //     //     title: '详细出厂信息',
    //     //   },
    //     // },
    //     // {
    //     //   path: '/exitpass/fieldconfigexceptiontype',
    //     //   name: 'fieldconfigexceptiontype',
    //     //   component: '/exitpass/field-config-exception-type/index.vue',
    //     //   code: '*',
    //     //   meta: {
    //     //     title: '抽查异常类型',
    //     //   },
    //     // },

    //   ]
    // },
  ],
};

// 楼宇门禁
const buildingAccessRoute = {
  path: '/buildingAccess',
  name: 'BuildingAccess',
  component: 'LAYOUT',
  code: '*', //buildingAccess
  meta: {
    title: '楼宇门禁',
  },
  children: [
    {
      path: 'baseInfo',
      name: 'BaseInfo',
      code: 'buildingAccess:baseInfo', //buildingAccess:baseInfo
      meta: {
        title: '基础信息管理',
      },
      children: [
        // {
        //   path: 'test',
        //   name: 'Test',
        //   component: '/buildingAccessControl/baseInfo/test/index',
        //   code: '*',
        //   meta: {
        //     title: '测试管理',
        //   },
        // },
        {
          path: 'accessManage',
          name: 'AccessManage',
          component: '/buildingAccessControl/baseInfo/accessManage/index',
          code: 'buildingAccess:baseInfo:accessManage',
          meta: {
            title: '门禁管理',
          },
        },
        {
          path: '/personDepartManage',
          name: 'PersonDepartManage',
          component: '/buildingAccessControl/baseInfo/personDepartManage/index',
          code: 'buildingAccess:baseInfo:personDepartManage', //buildingAccess:baseInfo:personDepartManage
          meta: {
            title: '门禁管理员及责任部门管理',
          },
        },
        {
          path: '/whitelistRule',
          name: 'WhitelistRule',
          component: '/buildingAccessControl/baseInfo/whitelistRule/index',
          code: 'buildingAccess:baseInfo:whitelistRule',
          meta: {
            title: '白名单规则',
          },
        },
        {
          path: '/personChangeMessageRemind',
          name: 'personChangeMessageRemind',
          component: '/buildingAccessControl/baseInfo/personChangeMessageRemind/index',
          code: 'buildingAccess:baseInfo:personChangeMessageRemind',
          meta: {
            title: '门禁系统消息提醒',
          },
        },
        {
          path: '/tipConfig',
          name: 'TipConfig',
          component: '/buildingAccessControl/baseInfo/tipConfig/index',
          code: 'buildingAccess:baseInfo:tipConfig', //buildingAccess:baseInfo:tipConfig
          meta: {
            title: '小助手提醒内容配置',
          },
        },
      ],
    },
    {
      path: '/deviceManage',
      name: 'DeviceManage',
      code: 'buildingAccess:deviceManage', //buildingAccess:deviceManage
      meta: {
        title: '设备管理',
      },
      children: [
        {
          path: '/FSDeviceType',
          name: 'FSDeviceType',
          component: '/buildingAccessControl/doorDevice/fushi/index',
          code: 'buildingAccess:deviceManage:fsdevice', //buildingAccess:deviceManage:fsdevice
          meta: {
            title: '富士设备分类',
          },
        },
        {
          path: '/accessControlManage',
          name: 'AccessControlManage',
          component: '/buildingAccessControl/doorDevice/index',
          code: 'buildingAccess:deviceManage:device', //buildingAccess:deviceManage:device
          meta: {
            title: '楼宇门禁设备',
          },
        },
      ],
    },
    {
      path: '/responsibilityDepartAjustProcess',
      name: 'ResponsibilityDepartAjustProcess',
      component: '/buildingAccessControl/responsibilityDepartAjustProcess/index',
      code: '*', //buildingAccess:responsibilityDepartAjustProcess
      meta: {
        title: '责任部门调整流程',
      },
    },
    {
      path: '/responsibilityDepartAjustProcess/add',
      name: 'AccessControlManageAdd',
      component: '/buildingAccessControl/responsibilityDepartAjustProcess/add/index',
      code: '*', //buildingAccess:responsibilityDepartAjustProcess:creat
      hidden: true,
      meta: {
        title: '责任部门调整流程',
      },
    },
    {
      path: '/accessRequestProcess',
      name: 'AccessRequestProcess',
      component: '/buildingAccessControl/accessRequestProcess/index',
      code: '*', //buildingAccess:accessRequestProcess
      meta: {
        title: '楼宇门禁申请流程',
      },
      children: [
        {
          path: 'form',
          name: 'AccessRequestForm',
          component: '/buildingAccessControl/accessRequestProcess/form/index',
          code: '*', //buildingAccess:accessRequestProcess:form
          hidden: true,
          meta: {
            title: '楼宇门禁申请',
          },
        },
        {
          path: 'autoAuthOrg',
          name: 'AutoAuthOrganization',
          component: '/buildingAccessControl/accessRequestProcess/autoAuthOrg/index',
          code: 'buildingAccess:accessRequestProcess:autoAuthOrg:button', //buildingAccess:accessRequestProcess:autoAuthOrg,
          hidden: true,
          meta: {
            title: '自动授权组织',
          },
        },
        {
          path: 'chargeAndManagerProcess',
          name: 'AccessRequestChargeAndManagerProcess',
          // component: '/buildingAccessControl/accessRequestChargeAndManagerProcess/index',
          component: '/buildingAccessControl/accessRequestManageProcess/index',
          code: '*', //buildingAccess:accessRequestProcessChargeAndManager
          hidden: true,
          meta: {
            title: '楼宇门禁申请流程-负责人/管理员',
          },
        },
      ],
    },
    {
      path: '/accessLog',
      name: 'AccessLog',
      component: '/buildingAccessControl/accessLog/index',
      code: 'buildingAccess:accessLog', //buildingAccess:accessLog
      meta: {
        title: '门禁授权日志',
      },
    },
    {
      path: '/personAccessDetail',
      name: 'PersonAccessDetail',
      component: '/buildingAccessControl/personAccessDetail/index',
      code: '*', //buildingAccess:personAccessDetail
      meta: {
        title: '人员门禁授权明细',
      },
    },
    {
      path: '/accessRequest',
      name: 'AccessRequest',
      hidden: true,
      component: '/buildingAccessControl/accessRequest/index',
      code: '*', //buildingAccess:accessRequest
      meta: {
        title: '楼宇门禁申请流程', //中间页面
        hideTab: true, // 配置该路由不在标签页中显示
        hideInMenu: true, // 在菜单中隐藏
        currentActiveMenu: '/accessRequestProcess',
      },
    },
    {
      path: '/personChangeAuthConfirm',
      name: 'PersonChangeAuthConfirm',
      component: '/buildingAccessControl/personChangeAuthConfirm/index',
      code: 'buildingAccess:personChangeAuthConfirm', //buildingAccess:personChangeAuthConfirm
      meta: {
        title: '人员变更权限确认',
      },
    },
    {
      path: '/deptSecrecyPerAuthConfirm',
      name: 'DeptSecrecyPerAuthConfirm',
      component: '/buildingAccessControl/deptSecrecyPerAuthConfirm/index',
      code: 'buildingAccess:deptSecrecyPerAuthConfirm', //buildingAccess:deptSecrecyPerAuthConfirm
      meta: {
        title: '调岗原对接门禁管理员权限确认',
      },
    },
    {
      path: '/empoyBuildAuthConfirm',
      name: 'EmpoyBuildAuthConfirm',
      component: '/buildingAccessControl/empoyBuildAuthConfirm/index',
      code: '*', //buildingAccess:empoyBuildAuthConfirm
      hidden: true,
      meta: {
        title: '员工个人门禁权限确认',
      },
    },

    {
      path: '/whiteManage',
      name: 'whiteManage',
      component: '/buildingAccessControl/whiteManage/index',
      code: 'buildingAccess:whiteManage', //buildingAccess:whiteManage
      meta: {
        title: '白名单管理',
      },
    },
  ],
};
    
// 大门权限
const gateAccessRoute = {
  path: '/gateAccess',
  name: 'GateAccess',
  component: 'LAYOUT',
  code: '*', //gateAccess
  meta: {
    title: '大门权限',
  },
  children: [
    {
      path: '/defaultIssuanceFactoryRules',
      name: 'DefaultIssuanceFactoryRules',
      component: '/gateAccessControl/defaultFactoryRules/index',
      code: 'gateAccess:defaultFactoryRules', //gateAccess:defaultFactoryRules
      meta: {
        title: '默认下发厂区规则',
      },
    },
    {
      path: '/employeeCarEntryPermissions',
      name: 'EmployeeCarEntryPermissions',
      component: '/gateAccessControl/employeeCarEntryPermissions/index',
      code: 'gateAccess:employeeCarEntryPermissions', //gateAccess:employeeCarEntryPermissions
      meta: {
        title: '员工及车辆入厂权限',
      },
    },
    // 员工最近一次厂区权限
    {
      path: '/employeeLastFactoryPermissions',
      name: 'EmployeeLastFactoryPermissions',
      component: '/gateAccessControl/employeeLastFactoryPermissions/index',
      code: 'gateAccess:employeeLastFactoryPermissions', //gateAccess:employeeLastFactoryPermissions
      meta: {
        title: '员工最近一次厂区权限',
      },
    },
    {
      path: '/gateAccessRequestProcess',
      name: 'GateAccessRequestProcess',
      component: '/gateAccessControl/gateAccessRequestProcess/index',
      code: '*', //gateAccess:accessRequestProcess
      meta: {
        title: '大门权限申请流程明细',
      },
    },
  ],
};
//新建大门权限
const newGateAccessRoute = {
  path: '/gateAccessRequestProcess/main',
  name: 'GateAccessRequestProcessMain',
  component: '/gateAccessControl/gateAccessRequestProcess/main',
  code: '*',
  hidden: true,
  meta: {
    title: '新建大门权限申请流程',
  },
  children: [
    {
      path: '/gateAccessRequestProcess/form',
      name: 'GateAccessRequestForm',
      component: '/gateAccessControl/gateAccessRequestProcess/form/index',
      code: '*', //gateAccess:accessRequestProcess:form
      hidden: true,
      meta: {
        title: '大门权限申请',
      },
    },
  ],
};

/*消防巡查*/
const firePatrolRoute = {
  path: '/firePatrol',
  name: 'FirePatrol',
  component: 'LAYOUT',
  code: 'FirePatrol',
  // code: '*',
  meta: {
    title: '消防巡查',
  },
  children: [
    {
      path: 'riskLedger',
      name: 'RiskLedger',
      code: 'FirePatrolRiskLedger',
      // code: '*',
      meta: {
        title: '风险点台账',
      },
      component: '/firePatrol/riskLedger/index',
    },
    {
      path: 'taskSetting',
      name: 'TaskSetting',
      code: 'FirePatrolTaskSetting',
      // code: '*',
      meta: {
        title: '任务设置',
      },
      component: '/firePatrol/taskSetting/index',
    },
    {
      path: 'taskDetails',
      name: 'TaskDetails',
      code: 'FirePatrolTaskDetails',
      // code: '*',
      meta: {
        title: '任务详情',
      },
      component: '/firePatrol/taskDetails/index',
    },
  ],
};

const hotLens = {
  path: '/hotLens',
  name: 'HotLens',
  component: 'LAYOUT',
  code: 'hotLens', //buildingAccess
  meta: {
    title: '动火作业管理',
  },
  children: [
    {
      path: '/lensDeviceManage',
      name: 'lensDeviceManage',
      component: '/fire/deviceManage/index',
      code: 'hotLens:lensDeviceManage', //hotLens:lensDeviceManage
      meta: {
        title: '监控设备管理',
      },
    },
    {
      path: '/hotLensDeviceVideo',
      name: 'HotLensDeviceVideo',
      component: '/fire/video/index',
      code: 'hotLens:hotLensDeviceVideo', //hotLens:hotLensDeviceVideo
      meta: {
        title: '动火监控',
      },
    },
  ],
};
const vehicleManagement = {
  path: '/vehicleManagement',
  name: 'VehicleManagement',
  component: 'LAYOUT',
  code: '*',
  meta: {
    title: '物流车辆入厂备案',
  },
  children: [
    {
      path: 'filing',
      name: 'filing',
      component: '/vehicle/vehiclelist/index',
      code: '*',
      meta: {
        title: '物流车备案信息表',
      },
      children: [
        {
          path: 'approve',
          name: 'approve',
          hidden: true,
          component: '/vehicle/vehiclelist/approve',
          code: '*',
          meta: {
            title: '备案审批',
          },
        },
        {
          path: 'approveResult',
          name: 'approveResult',
          hidden: true,
          component: '/vehicle/vehiclelist/approveResult',
          code: '*',
          meta: {
            title: '备案审批结果',
          },
        },
      ],
    },
    {
      path: 'cancelrole',
      name: 'cancelRole',
      component: 'vehicle/cancelRole/index',
      code: 'vehicle:cancelRole',
      // code: '*',
      meta: {
        title: '自动取消时长设置',
      },
    },
    {
      path: 'historicalInfo',
      name: 'historicalInfo',
      component: '/vehicle/historicalInfo/index',
      code: '*',
      meta: {
        title: '历史备案信息表',
      },
    },
    {
      path: 'carFil',
      name: 'carFil',
      component: '/vehicle/carFile/index',
      code: 'VehicleManagement:carFil',
      meta: {
        title: '备案物流车进厂分析',
      },
    },
    {
      path: 'carFilLog',
      name: 'carFilLog',
      component: '/vehicle/carFilLog/index',
      code: 'VehicleManagement:carFilLog',
      meta: {
        title: '备案下发日志',
      },
    },
    {
      path: 'personInfo',
      name: 'personInfo',
      component: '/vehicle/personInfo/index',
      code: 'VehicleManagement:personInfo',
      meta: {
        title: '已登记人员信息表',
      },
    },
    {
      path: 'environ',
      name: 'environ',
      component: '/vehicle/environ/index',
      code: 'VehicleManagement:environ',
      meta: {
        title: '物流车辆环保信息管理',
      },
    },

    {
      path: 'permission',
      name: 'permission',
      component: '/vehicle/permission/index',
      code: 'VehicleManagement:permission',
      meta: {
        title: '备案权限管理',
      },
    },
    // {
    //   path: 'plantConfiguration',
    //   name: 'plantConfiguration',
    //   component: '/vehicle/plantConfiguration/index',
    //   code: '*',
    //   meta: {
    //     title: '厂区配置',
    //   },
    // },
    {
      path: 'recordType',
      name: 'recordType',
      component: '/vehicle/recordType/index',
      code: 'VehicleManagement:recordType',
      meta: {
        title: '备案类型配置',
      },
    },
    // {
    //   path: 'accessControl',
    //   name: 'accessControl',
    //   component: '/vehicle/accessControl/index',
    //   code: '*',
    //   meta: {
    //     title: '门禁通道维护',
    //   },
    // },
    // {
    //   path: 'fieldManagement',
    //   name: 'fieldManagement',
    //   component: '',
    //   code: '*',
    //   meta: {
    //     title: '字段管理',
    //   },
    // },
  ],
};
//入厂备案
const enterFilingRoute = {
  path: '/enterFiling',
  name: 'EnterFiling',
  component: 'LAYOUT',
  code: '*', //EnterFiling
  meta: {
    title: '入厂备案',
  },
  children: [
    {
      path: 'carFilLog',
      name: 'carFilLog',
      component: '/vehicle/carFilLog/index',
      code: 'VehicleManagement:carFilLog', //'VehicleManagement:carFilLog',
      meta: {
        title: '备案下发日志',
      },
    },
    {
      path: 'personInfo',
      name: 'personInfo',
      component: '/vehicle/personInfo/index',
      code: 'VehicleManagement:personInfo', //'VehicleManagement:personInfo',
      meta: {
        title: '已登记人员信息表',
      },
    },
    {
      path: 'permission',
      name: 'permission',
      component: '/vehicle/permission/index',
      code: 'VehicleManagement:permission', //'VehicleManagement:permission',
      meta: {
        title: '备案权限管理',
      },
    },
    {
      path: 'recordType',
      name: 'recordType',
      component: '/vehicle/recordType/index',
      code: 'VehicleManagement:recordType',
      meta: {
        title: '备案类型配置',
      },
    },
    {
      path: '/vehicleManagement',
      name: 'VehicleManagement',
      code: '*', //'VehicleManagement:vehicleManagement',
      meta: {
        title: '物流车辆入厂备案',
      },
      children: [
        {
          path: 'filing',
          name: 'filing',
          component: '/vehicle/vehiclelist/index',
          code: '*',
          meta: {
            title: '物流车备案信息表',
          },
          children: [
            {
              path: 'approve',
              name: 'approve',
              hidden: true,
              component: '/vehicle/vehiclelist/approve',
              code: '*',
              meta: {
                title: '备案审批',
              },
            },
            {
              path: 'approveResult',
              name: 'approveResult',
              hidden: true,
              component: '/vehicle/vehiclelist/approveResult',
              code: '*',
              meta: {
                title: '备案审批结果',
              },
            },
          ],
        },
        {
          path: '/vehicleManagement/cancelrole',
          name: 'cancelRole',
          component: 'vehicle/cancelRole/index',
          code: 'vehicle:cancelRole',
          // code: '*',
          meta: {
            title: '自动取消时长设置',
          },
        },
        {
          path: '/vehicleManagement/historicalInfo',
          name: 'historicalInfo',
          component: '/vehicle/historicalInfo/index',
          code: '*',
          meta: {
            title: '历史备案信息表',
          },
        },
        {
          path: '/vehicleManagement/carFil',
          name: 'carFil',
          component: '/vehicle/carFile/index',
          code: 'VehicleManagement:carFil',
          meta: {
            title: '备案物流车进厂分析',
          },
        },
        {
          path: '/vehicleManagement/environ',
          name: 'environ',
          component: '/vehicle/environ/index',
          code: 'VehicleManagement:environ',
          meta: {
            title: '物流车辆环保信息管理',
          },
        },
      ],
    },
    {
      path: '',
      name: 'externalPersonManagement',
      code: '*', //EnterFiling:externalPersonManagement
      meta: {
        title: '外来人员入厂备案',
      },
      children: [
        {
          path: '/externalPermissionRegister',
          name: 'externalPermissionRegister',
          component: '/enterFiling/externalPermissionRegister/index',
          code: '*', //EnterFiling:externalPermissionRegister
          meta: {
            title: '外部备案权限注册',
          },
        },
        {
          path: '/enterFiling/recordPersonInfo',
          name: 'recordPersonInfo',
          component: '/enterFiling/recordPersonInfo/index',
          code: '*', //EnterFiling:recordPersonInfo
          meta: {
            title: '备案人员信息表',
          },
        },
      ],
    },
    {
      path: '/blackRecord',
      name: 'blackRecord',
      component: '/enterFiling/blackRecord/index',
      code: '*', //EnterFiling:blackRecord
      meta: {
        title: '受限人员入厂备案记录',
      },
    },
    // {
    //   path: 'recordPersonInfo',
    //   name: 'recordPersonInfo',
    //   component: '/enterFiling/recordPersonInfo/index',
    //   code: 'EnterFiling:recordPersonInfo', //EnterFiling:recordPersonInfo
    //   meta: {
    //     title: '备案人员信息表',
    //   },
    // },
    // // {
    // //   path: '/blackListEnterApply',
    // //   name: 'blackListEnterApply',
    // //   component: '/enterFiling/blackListEnterApply/index',
    // //   code: 'EnterFiling:blackListEnterApply', //EnterFiling:blackListEnterApply
    // //   meta: {
    // //     title: '黑名单入厂申请',
    // //     hideMenu: true,
    // //   },
    // // },
    // {
    //   path: '/blackRecord',
    //   name: 'blackRecord',
    //   component: '/enterFiling/blackRecord/index',
    //   code: 'EnterFiling:blackRecord', //EnterFiling:blackRecord
    //   meta: {
    //     title: '黑名单入厂备案记录',
    //   },
    // },
    // {
    //   path: '/externalPermissionRegister',
    //   name: 'externalPermissionRegister',
    //   component: '/enterFiling/externalPermissionRegister/index',
    //   code: 'EnterFiling:externalPermissionRegister', //EnterFiling:externalPermissionRegister
    //   meta: {
    //     title: '外部备案权限注册',
    //   },
    // },
  ],
};

//黑名单入场申请
const blackListEnterApplyRoute = {
  path: '/blackListEnterApply',
  name: 'blackListEnterApply',
  component: '/enterFiling/blackListEnterApply/index',
  code: '*', //EnterFiling:blackListEnterApply
  meta: {
    title: '受限人员入场申请',
    hideMenu: true,
  },
};
export const menu: Object[] = [
  dashboardRoute,
  membersRoute,
  systemdRoute,
  deviceRoute,
  queryRoute,
  registrationRoute,
  SmartParkingRoute,
  entryExitRoute,
  emailInterceptSettingRoute,
  facilitiesCheck,
  securityService,
  accessAnalysisRoute,
  // vehicleManagement,
  facilitiesApprovals,
  secApprovals,
  patrol,
  exitpass,
  buildingAccessRoute,
  gateAccessRoute,
  newGateAccessRoute,
  firePatrolRoute,
  hotLens,
  enterFilingRoute,
  blackListEnterApplyRoute,
];
