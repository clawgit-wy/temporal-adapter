import { defHttp } from '/@/utils/http/axios';

enum Api {
  searchUser = '/safety/employee/getUserListByCondition', // 搜索用户
  getUserListByConditionContain = '/safety/employee/getUserListByConditionContain', // 搜索用户(管理者名下)
  safetyGroupTree = '/safety/group/selectGroupTreeWithPeople', // 组织树
  selectGroupTreeWithPeopleBelong = '/safety/group/selectGroupTreeWithPeopleBelong', // 组织树(管理者名下)
  validPeople = '/safety/group/validPeople', // 验证人
}

/**
 * 获取组织树节点
 * @param parentId 父节点ID，空字符串表示获取根节点
 */
export const getChildrenNodes = async (parentId: string): Promise<any[]> => {
  try {
    // 构建请求参数，根节点不传key
    const params = parentId ? { key: parentId } : {};
    
    const response = await defHttp.get(
      {
        url: Api.safetyGroupTree,
        params,
      },
      { errorMessageMode: 'none' }
    );
    // 处理响应数据，提取dataList
    if (response  && response.dataList) {
      return response.dataList;
    }
    return [];
  } catch (error) {
    console.error('获取组织树节点失败:', error);
    return [];
  }
};

/**
 * 搜索人员
 * @param keyword 搜索关键词，必须有值
 */
export const searchPersons = async (keyword: string): Promise<any[]> => {
  if (!keyword) return [];
  
  try {
    const response = await defHttp.post(
      {
        url: Api.searchUser,
        params: { empNoAndName: keyword },
      },
      { errorMessageMode: 'none' }
    );
    
    
    // 直接返回dataList数组
    if (response.dataList) {
      return response.dataList;
    }
    return [];
  } catch (error) {
    console.error('搜索人员失败:', error);
    return [];
  }
}; 
/**
 * 验证人
 * @param keyword 搜索关键词，必须有值
 */
export const validPeople = async (params: any): Promise<any[]> => {
  
  try {
    const response = await defHttp.post(
      {
        url: Api.validPeople,
        params
      },
      { errorMessageMode: 'none' }
    );
    
    console.log("验证人返回==",response)
    // 直接返回dataList数组
    if (response) {
      return response.dataList;
    }
    return [];
  } catch (error) {
    console.error('验证人员失败:', error);
    return [];
  }
}; 
