import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { h } from 'vue';
import dayjs from 'dayjs';
import { useRangePickerWithLimit } from '/@/utils/datePicker';

/**
 * 门禁授权日志记录接口
 */
export interface AccessLogRecord {
  id: string;
  employeeCode?: string; // 员工号
  employeeName?: string; // 姓名
  dutyDeptName?: string; // 责任部门
  deviceName?: string; // 楼宇门禁名称
  exceptDeviceName?: string; // 下发异常门禁
  doorName?: string; // 大门名称
  issuedType?: string; // 类型
  remark?: string; // 原因
  createBy?: string; // 操作人
  createTime?: string; // 操作时间
  issuedStatus?: string; // 下发结果
  beginTime?: string; // 授权有效期开始
  endTime?: string; // 授权有效期结束
  bpmFlowId?: string; // 流程ID
  targetId?: string; // 来源标识
}

/**
 * 查询参数接口
 */
export interface QueryParams {
  employeeCode?: string; // 员工号
  employeeName?: string; // 姓名
  dutyDeptName?: string; // 责任部门
  deviceName?: string; // 楼宇门禁名称
  exceptDeviceName?: string; // 下发异常门禁
  doorName?: string; // 大门名称
  issuedType?: string; // 类型
  issuedStatus?: string; // 下发结果
  createBy?: string; // 操作人
  createTimeBegin?: string; // 创建时间开始
  createTimeEnd?: string; // 创建时间结束
  pageNum: number; // 当前页码
  pageSize: number; // 每页条数
}

/**
 * 表格列定义
 */
export const columns: BasicColumn[] = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 80,
    customRender: ({ index }) => {
      return index + 1;
    },
  },
  {
    title: '姓名',
    dataIndex: 'employeeName',
    width: 120,
  },
  {
    title: '员工号',
    dataIndex: 'employeeCode',
    width: 120,
  },
  {
    title: '责任部门',
    dataIndex: 'dutyDeptName',
    width: 150,
  },
  {
    title: '楼宇门禁名称',
    dataIndex: 'deviceName',
    width: 150,
  },
  {
    title: '大门名称',
    dataIndex: 'doorName',
    width: 150,
  },
  {
    title: '授权有效期',
    dataIndex: 'validPeriod',
    width: 200,
    customRender: ({ text, record }) => {
      const accessRecord = record as unknown as AccessLogRecord;
      if (accessRecord.beginTime && accessRecord.endTime) {
        return `${accessRecord.beginTime} 至 ${accessRecord.endTime}`;
      }
      return '-';
    },
  },
  {
    title: '来源',
    dataIndex: 'targetId',
    width: 200,
    slots: {
      customRender: 'source',
    },
  },
  {
    title: '类型',
    dataIndex: 'issuedType',
    width: 100,
  },
  {
    title: '原因',
    dataIndex: 'remark',
    width: 150,
  },
  {
    title: '操作人',
    dataIndex: 'createBy',
    width: 120,
  },
  {
    title: '操作时间',
    dataIndex: 'createTime',
    width: 150,
  },
  {
    title: '下发结果',
    dataIndex: 'issuedStatus',
    width: 100,
  },

  {
    title: '下发异常门禁',
    dataIndex: 'exceptDeviceName',
    width: 150,
  },
];

/**
 * 搜索表单定义
 */
export const searchFormSchema: FormSchema[] = [
  {
    field: 'employeeName',
    label: '姓名',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'employeeCode',
    label: '员工号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'createTimeRange',
    label: '操作时间',
    component: 'RangePicker',
    defaultValue: [dayjs().subtract(7, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')],
    componentProps: useRangePickerWithLimit(7),
    colProps: { span: 6 },
  },
  {
    field: 'issuedType',
    label: '类型',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'issuedStatus',
    label: '下发结果',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'remark',
    label: '原因',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'createBy',
    label: '操作人',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'dutyDeptName',
    label: '所属部门',
    component: 'Input',
    colProps: { span: 6 },
  },
]; 
