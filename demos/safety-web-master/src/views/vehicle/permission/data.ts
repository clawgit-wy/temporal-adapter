import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';
import { ftList, auths, fcList, vaPhone } from './api';
// import { filterDictTextByCache } from '/@/utils/dict/JDictSelectUtil';
import { usePermission } from '/@/hooks/web/usePermission';
const { hasPermission } = usePermission();
function validatePass(rule, value, callback) {
  console.log(value, 89988);
}
export const columns: BasicColumn[] = [
  {
    title: '备案类型',
    dataIndex: 'fdFilingType',
    width: 100,
  },
  {
    title: '姓名',
    dataIndex: 'fdEmpName',
    width: 100,
  },
  {
    title: '员工号',
    dataIndex: 'fdEmpNo',
    width: 100,
  },
  {
    title: '联系方式',
    dataIndex: 'fdPhone',
    width: 100,
  },
  {
    title: '部门',
    dataIndex: 'fdEmpDepartment',
    width: 100,
  },
  {
    title: '权限',
    dataIndex: 'fdAuth',
    width: 100,
  },
  {
    title: '可备案厂区',
    dataIndex: 'fdFactory',
    width: 150,
  },
  {
    title: '人员类型',
    dataIndex: 'fdFilingUserType',
    width: 100,
  },
  {
    title: '人员限制备案次数',
    dataIndex: 'fdLimit',
    width: 150,
  },

  {
    title: '限制备案时间（天）',
    dataIndex: 'fdLimitDay',
    width: 150,
  },
  {
    title: '是否限制黑名单',
    dataIndex: 'fdBlack',
    width: 120,
  },
  {
    title: '备案过期是否提醒',
    dataIndex: 'fdNotification',
    width: 120,
    customRender: ({ text }) => {
      return text == '1' ? '是' : '否';
    },
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 100,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '备注',
    dataIndex: 'fdRemark',
    width: 100,
  },

  {
    title: '操作',
    key: 'action',
    width: 100,
    slots: { customRender: 'action' },
    fixed: 'right',
    ifShow: () => {
      return hasPermission('VehicleManagement:permission:delete') || hasPermission('VehicleManagement:permission:edit');
    },
  },
];
export const deptColumns: BasicColumn[] = [
  {
    title: '备案类型',
    dataIndex: 'fdFilingType',
    width: 100,
  },
  {
    title: '名称',
    dataIndex: 'fdEmpName',
    width: 100,
  },
  {
    title: '权限',
    dataIndex: 'fdAuth',
    width: 100,
  },
  {
    title: '可备案厂区',
    dataIndex: 'fdFactory',
    width: 100,
  },
  {
    title: '人员类型',
    dataIndex: 'fdFilingUserType',
    width: 100,
  },
  {
    title: '人员备案次数限制',
    dataIndex: 'fdLimit',
    width: 100,
  },

  {
    title: '限制备案时间（天）',
    dataIndex: 'fdLimitDay',
    width: 150,
  },
  {
    title: '是否限制黑名单',
    dataIndex: 'fdBlack',
    width: 120,
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
    width: 100,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : text),
  },
  {
    title: '更新人',
    dataIndex: 'updatePerson',
    width: 120,
  },
  {
    title: '备注',
    dataIndex: 'fdRemark',
    width: 100,
  },

  {
    title: '操作',
    key: 'action',
    width: 100,
    slots: { customRender: 'action' },
    fixed: 'right',
    ifShow: () => {
      return hasPermission('VehicleManagement:permission:delete') || hasPermission('VehicleManagement:permission:edit');
    },
  },
];

//表单搜索字段
export const searchFormSchema: FormSchema[] = [
  {
    label: '姓名', //显示label
    field: 'fdEmpName', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '员工号',
    field: 'fdEmpNo',
    component: 'Input',
  },
  {
    label: '部门',
    field: 'fdEmpDepartment',
    component: 'Input',
  },

  {
    label: '备案类型',
    field: 'fdFilingType',
    component: 'ApiSelect',
    componentProps: {
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p1',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },
];

export const addFormSchema: FormSchema[] = [
  {
    label: '姓名', //显示label
    field: 'fdEmpName', //查询字段
    component: 'Input', //渲染的组件
    required: true,
    dynamicDisabled: true,
  },
  {
    label: '员工号', //显示label
    field: 'fdEmpNo', //查询字段
    component: 'Input', //渲染的组件
    required: true,
    slot: 'emp',
  },
  {
    label: '部门', //显示label
    field: 'fdEmpDepartment', //查询字段
    component: 'Input', //渲染的组件
    dynamicDisabled: true,
    required: true,
  },
  {
    label: '联系方式', //显示label
    field: 'fdPhone', //查询字段
    component: 'Input', //渲染的组件
    dynamicDisabled: true,
    required: true,
    // rules: [
    //   { required: false, message: '请输入正确的手机号' },
    //   { validator: validatePass, trigger: 'blur' },
    // ],
  },
  {
    label: '备案类型', //显示label
    field: 'fdFilingType', //查询字段
    component: 'ApiSelect', //渲染的组件

    required: true,
    componentProps: {
      mode: 'multiple',
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p1',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
      showArrow: true,
    },
  },
  {
    label: '人员类型', //显示label
    field: 'fdFilingUserType', //查询字段
    component: 'ApiSelect', //渲染的组件
    slot: '',
    required: true,

    componentProps: {
      showArrow: true,
      mode: 'multiple',
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p2',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
    ifShow: ({ values }) => {
      if (values.fdFilingType) {
        return values.fdFilingType.includes('20');
      } else {
        return false;
      }
    },
  },
  {
    label: '人员可备案厂区', //显示label
    field: 'fdFactory', //查询字段
    component: 'ApiSelect', //渲染的组件
    slot: '',
    required: true,
    componentProps: {
      showArrow: true,
      mode: 'multiple',
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p4',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },
  {
    label: '限制备案时间(天)', //显示label
    field: 'fdLimitDay', //查询字段
    component: 'Input', //渲染的组件
    required: true,
    rules: [{ required: true, message: '请输入大于0的整数', pattern: /^[1-9]\d*$/ }],
  },
  {
    label: '系统操作权限', //显示label
    field: 'fdAuth', //查询字段
    component: 'ApiSelect', //渲染的组件
    slot: '',
    required: true,
    componentProps: {
      showArrow: true,
      mode: 'multiple',
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p3',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },
  {
    label: '限制备案次数', //显示label
    field: 'fdLimit', //查询字段
    component: 'Input', //渲染的组件
    required: true,
    rules: [{ required: true, message: '请输入大于0的整数', pattern: /^[1-9]\d*$/ }],
  },
  {
    label: '是否限制黑名单',
    required: true,
    field: 'fdBlack',
    component: 'Switch',
    defaultValue: '是',
    componentProps: {
      //开关大小，可选值：default small
      size: 'default',
      //非选中时的内容
      unCheckedChildren: '否',
      //非选中时的值
      unCheckedValue: '否',
      //选中时的内容
      checkedChildren: '是',
      //选中时的值
      checkedValue: '是',
      //是否禁用
      disabled: false,
    },
  },
  {
    label: '备案过期是否提醒',
    required: true,
    field: 'fdNotification',
    component: 'Switch',
    defaultValue: '否',
    componentProps: {
      //开关大小，可选值：default small
      size: 'default',
      //非选中时的内容
      unCheckedChildren: '否',
      //非选中时的值
      unCheckedValue: '0',
      //选中时的内容
      checkedChildren: '是',
      //选中时的值
      checkedValue: '1',
      //是否禁用
      disabled: false,
    },
  },
  {
    label: '备注',
    field: 'fdRemark',
    component: 'InputTextArea',
    required: true,
    //属性
    componentProps: {
      //可以点击清除图标删除内容
      allowClear: true,
      //是否展示字数
      // showCount: true,
      //自适应内容高度，可设置为 true | false 或对象：{ minRows: 2, maxRows: 6 }
      autoSize: {
        //最小显示行数
        minRows: 5,
        //最大显示行数
        maxRows: 5,
      },
    },
  },
];
export const addDeptFormSchema: FormSchema[] = [
  {
    label: '名称', //显示label
    field: 'fdEmpName', //查询字段
    component: 'Input', //渲染的组件
    required: true,
  },
  {
    label: '备案类型', //显示label
    field: 'fdFilingType', //查询字段
    component: 'ApiSelect', //渲染的组件

    required: true,
    componentProps: {
      mode: 'multiple',
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p1',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
      showArrow: true,
    },
  },
  {
    label: '人员类型', //显示label
    field: 'fdFilingUserType', //查询字段
    component: 'ApiSelect', //渲染的组件
    slot: '',
    required: true,

    componentProps: {
      showArrow: true,
      mode: 'multiple',
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p2',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
    ifShow: ({ values }) => {
      if (values.fdFilingType) {
        return values.fdFilingType.includes('20');
      } else {
        return false;
      }
    },
  },
  {
    label: '人员可备案厂区', //显示label
    field: 'fdFactory', //查询字段
    component: 'ApiSelect', //渲染的组件
    slot: '',
    required: true,
    componentProps: {
      showArrow: true,
      mode: 'multiple',
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p4',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },
  {
    label: '限制备案时间(天)', //显示label
    field: 'fdLimitDay', //查询字段
    component: 'Input', //渲染的组件
    required: true,
    rules: [{ required: true, message: '请输入大于0的整数', pattern: /^[1-9]\d*$/ }],
  },
  {
    label: '系统操作权限', //显示label
    field: 'fdAuth', //查询字段
    component: 'ApiSelect', //渲染的组件
    slot: '',
    required: true,
    componentProps: {
      showArrow: true,
      mode: 'multiple',
      showSearch: true, // 开启搜索
      api: ftList,
      resultField: 'p3',
      immediate: true, // 组件加载后是否立即请求接口
      labelField: 'itemName',
      valueField: 'itemCode',
      optionFilterProp: 'label', // 搜索按label字段搜索
    },
  },
  {
    label: '限制备案次数', //显示label
    field: 'fdLimit', //查询字段
    component: 'Input', //渲染的组件
    required: true,
    rules: [{ required: true, message: '请输入大于0的整数', pattern: /^[1-9]\d*$/ }],
  },
  {
    label: '是否限制黑名单',
    required: true,
    field: 'fdBlack',
    component: 'Switch',
    defaultValue: '是',
    componentProps: {
      //开关大小，可选值：default small
      size: 'default',
      //非选中时的内容
      unCheckedChildren: '否',
      //非选中时的值
      unCheckedValue: '否',
      //选中时的内容
      checkedChildren: '是',
      //选中时的值
      checkedValue: '是',
      //是否禁用
      disabled: false,
    },
  },
  {
    label: '备注',
    field: 'fdRemark',
    component: 'InputTextArea',
    required: true,
    //属性
    componentProps: {
      //可以点击清除图标删除内容
      allowClear: true,
      //是否展示字数
      // showCount: true,
      //自适应内容高度，可设置为 true | false 或对象：{ minRows: 2, maxRows: 6 }
      autoSize: {
        //最小显示行数
        minRows: 5,
        //最大显示行数
        maxRows: 5,
      },
    },
  },
];
