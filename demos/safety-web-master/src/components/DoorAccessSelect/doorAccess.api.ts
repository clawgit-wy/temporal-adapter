import { defHttp } from '/@/utils/http/axios';

enum Api {
  searchUser = '/build/safety/collect/selectGuard', // 搜索用户
  safetyGroupTree = '/build/safety/collect/selectBuildTree', // 组织树
  getDoorAccessByParentId = '/build/safety/collect/selectAllGuard', // 父id查询旗下所有的门禁
  getDoorAccessGroup = '/build/safety/collect/selectBuildCollect', // 获取门禁组
  saveBuildCollect = '/build/safety/collect/saveBuildCollect', // 收藏门禁组
  deleteBuildCollect = '/build/safety/collect/deleteBuildCollect', // 删除门禁组
  getDoorAccessByGroupId = '/build/safety/collect/selectBuildCollectDetail', // 根据门禁组id查询旗下所有的门禁
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
    if (response && response.dataList) {
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
    const response = await defHttp.get(
      {
        url: Api.searchUser,
        params: { guard: keyword },
      },
      { errorMessageMode: 'none' }
    );
    // 直接返回dataList数组
    if (response) {
      return response;
    }
    return [];
  } catch (error) {
    console.error('搜索人员失败:', error);
    return [];
  }
};

// 父id查询旗下所有的门禁
export const getDoorAccessByParentId = async (parentId: string): Promise<any[]> => {
  try {
    const response = await defHttp.get(
      {
        url: Api.getDoorAccessByParentId,
        params: { key: parentId },
      },
      { errorMessageMode: 'none' }
    );
    if (response.dataList) {
      return response.dataList;
    }
    return [];
  } catch (error) {
    console.error('查询门禁失败:', error);
    return [];
  }
};

// 查询门禁组合
export const getDoorAccessGroup = async (userCode: string): Promise<any[]> => {
  try {
    const response = await defHttp.post(
      {
        url: Api.getDoorAccessGroup,
        params: { userCode },
      },
      { errorMessageMode: 'none' }
    );
    if (response) {
      return response;
    }
    return [];
  } catch (error) {
    console.error('查询门禁组合失败:', error);
    return [];
  }
};

//门禁组合收藏
export const saveBuildCollect = async (data: any): Promise<any[]> => {
  try {
    const response = await defHttp.post(
      {
        url: Api.saveBuildCollect,
        params: data,
      },
      { errorMessageMode: 'none' }
    );
    if (response.data) {
      return response.data;
    }
    return [];
  } catch (error) {
    console.error('查询门禁组合失败:', error);
    return [];
  }
};

//删除收藏的门禁组合
export const deleteBuildCollect = async (id: number): Promise<any[]> => {
  try {
    const response = await defHttp.get(
      {
        url: Api.deleteBuildCollect,
        params: {id},
      },
      { errorMessageMode: 'none' }
    );
    if (response && response.data) {
      return response.data;
    }
    return [];
  } catch (error) {
    console.error('删除门禁组合失败:', error);
    return [];
  }
};

// 根据门禁组id查询旗下所有的门禁
export const getDoorAccessByGroupId = async (id: number): Promise<any[]> => {
  try {
    const response = await defHttp.get(
      {
        url: Api.getDoorAccessByGroupId,
        params: {id},
      },
     
    );
    if (response) {
      return response;
    }
    return [];
  } catch (error) {
    console.error('删除门禁组合失败:', error);
    return [];
  }
}