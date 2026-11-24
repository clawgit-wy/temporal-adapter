import { BasicColumn, FormSchema } from '/@/components/Table';
import { render } from '/@/utils/common/renderUtils';
import dayjs from 'dayjs';
// import { filterDictTextByCache } from '/@/utils/dict/JDictSelectUtil';
import { usePermission } from '/@/hooks/web/usePermission';
const { hasPermission } = usePermission();
export const columns: BasicColumn[] = [
  {
    title: '姓名',
    dataIndex: 'fdName',
  },
  {
    title: '身份证号',
    dataIndex: 'fdCardNo',
  },
  {
    title: '手机号',
    dataIndex: 'fdPhone',
  },

  {
    title: '证件照',
    dataIndex: 'fdImg',

    customRender: ({ text }) => {
      if (!text) {
        return text;
      } else {
        return render.renderImage2({ text });
      }
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '当前是否在厂',
    dataIndex: 'fdInFactory',
  },

  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    fixed: 'right',
    ifShow: () => {
      return hasPermission('VehicleManagement:personInfo:edit') || hasPermission('VehicleManagement:personInfo:delete');
    },
  },
];

//表单搜索字段
export const searchFormSchema: FormSchema[] = [
  {
    label: '姓名', //显示label
    field: 'fdName', //查询字段
    component: 'Input', //渲染的组件
    defaultValue: '', //设置默认值
  },
  {
    label: '身份证号',
    field: 'fdCardNo',
    component: 'Input',
  },
  {
    label: '手机号',
    field: 'fdPhone',
    component: 'Input',
  },
];

export const addFormSchema: FormSchema[] = [
  {
    label: '姓名', //显示label
    field: 'fdName', //查询字段
    component: 'Input', //渲染的组件
    required: true,
  },
  {
    label: '身份证号',
    field: 'fdCardNo',
    component: 'Input',
    required: true,
  },
  {
    label: '手机号',
    field: 'fdPhone',
    component: 'Input',
    required: true,
  },
  {
    label: '证件照',
    field: 'fdImg',
    component: 'JImageUpload',
    required: true,
    componentProps: {
      //按钮显示文字
      text: '图片上传',
      //支持两种基本样式picture和picture-card
      listType: 'picture-card',
      //用于控制文件上传的业务路径,默认temp
      bizPath: 'temp',
      //是否禁用
      disabled: false,
      //最大上传数量
      fileMax: 1,
    },
  },
  {
    label: '当前是否在厂',
    field: 'fdInFactory',
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
];

export const deleteFormSchema:FormSchema[]=[
  {
    label: '原因', //显示label
    field: 'reason', //查询字段
    required: true,
    component: 'InputTextArea',
    componentProps: {
      rows: 2,
      maxlength: 20,
      showCount: true,
    }
  },
]
