import {BasicColumn} from "/@/components/Table";

export const detailsModalColumns: BasicColumn[] = [
  {title: '检查编码', dataIndex: 'inspectionCode', key: 'inspectionCode'},
  {title: '检查项目', dataIndex: 'inspectionItem', key: 'inspectionItem'},
  {title: '检查类别', dataIndex: 'inspectionCategory', key: 'inspectionCategory'},
  {title: '检查对象', dataIndex: 'inspectionObject', key: 'inspectionObject'},
  {title: '检查细分维度', dataIndex: 'inspectionSubDimension', key: 'inspectionSubDimension'},
  {title: '检查内容', dataIndex: 'inspectionContent', key: 'inspectionContent'},
  {title: '检查方法', dataIndex: 'inspectionMethod', key: 'inspectionMethod'},
  {
    title: '检查结果',
    dataIndex: 'inspectionResult',
    key: 'inspectionResult',
    customRender: ({text}) => text && text.length > 0 ? text : '/'
  },
  {
    title: '任务完成时间',
    dataIndex: 'inspectionTime',
    key: 'inspectionTime',
    customRender: ({text}) => text && text.length > 0 ? text : '/'
  },
  {
    title: '备注',
    dataIndex: 'processId',
    key: 'processId',
    customRender: ({text}) => text && text.length > 0 ? text : '/',
    slots: {customRender: 'processId'},
  }
];
