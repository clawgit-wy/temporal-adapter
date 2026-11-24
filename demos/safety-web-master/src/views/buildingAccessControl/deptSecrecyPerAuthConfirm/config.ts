import { BasicColumn } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: "员工号",
    dataIndex: "employeeCode",
    width:100
  },
  {
    title: "姓名",
    dataIndex: "employeeName",
    width:80
  },
  {
    title: "当前部门",
    dataIndex: "newDeptName",
  },
  {
    title: "当前部门门禁管理员",
    dataIndex: "dutyEmpName",
    width:150
  },
  {
    title: "原部门",
    dataIndex: "oldDeptName",
  },
  {
    title: "待确认授权门禁",
    dataIndex: "deviceName",
  },
  {
    title: "门禁对接部门",
    dataIndex: "corresDeptName",
    width:400
  },
  {
    title: "组织变更时间",
    dataIndex: "createTime",
  },
  {
    title: "处理人",
    dataIndex: "corresEmpName",
  },
 
]

