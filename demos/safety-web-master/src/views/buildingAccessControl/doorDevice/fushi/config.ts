import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { fieldChildTree} from './field.api';
import { facList } from '../../vehicle/vehiclelist/api';

// // 获取厂区列表
// const factoryList = async ()=> {
//   const res = await fieldChildByFieldType({fieldType:'factory'});
//   console.log(factoryList, 'factoryListsfactoryLists');
//   return res
// }
// // const buildingList = async (id)=> {
// //   const res = await fieldChildByFieldType({fieldType:'factory'});
// //   console.log(factoryList, 'factoryListsfactoryLists');
// //   return res
// // }
// // factoryList();

export const columns: BasicColumn[] = [
  {
    title: '门禁初始名称',
    dataIndex: 'devName',
  },
  
  {
    title: '门禁编码',
    dataIndex: 'devNo',
    width:300,
    
  },
  {
    title: '设备类型',
    dataIndex: 'deviceCategory',
    slots: { customRender: 'deviceCategory' },
  },
  
  
 
]
export const deviceTypeOptions =[
  { label: '大门厂区设备', value: '1' },
  { label: '楼宇门禁设备', value: '2' },
  
]
