import { defHttp } from '/@/utils/http/axios';

export enum Api {
  monitorList = '/safety/hik/monitor/pageList',
  monitorSaveAndUpdate = '/safety/hik/monitor/saveAndUpdate',
  monitorSyncUpdateInfo = '/safety/hik/monitor/syncUpdateInfo',
  thresholdList = '/safety/hik/threshold/pageList',
  thresholdSaveAndUpdate = '/safety/hik/threshold/saveAndUpdate',
  thresholdDelete = '/safety/hik/threshold/delete',
}

export const deviceList = async (params) => {
  const data = await defHttp.post({ url: Api.monitorList, params: normalizeAreaParams(params) });
  return normalizePageResult(data);
};

export const thresholdList = async (params) => {
  const data = await defHttp.post({ url: Api.thresholdList, params });
  return normalizePageResult(data);
};

export const syncMonitorInfo = () => {
  return defHttp.post({ url: Api.monitorSyncUpdateInfo, params: { type: 1 } });
};

export const saveOrUpdate = (params) => {
  if (params.configType === 'threshold') {
    return defHttp.post({
      url: Api.thresholdSaveAndUpdate,
      headers: {
        'Content-Type': 'application/json;charset=UTF-8',
      },
      params: {
        id: params.id,
        sceneCode: params.sceneCode,
        sceneName: params.sceneName,
        sceneValue: params.threshold,
      },
    });
  }
  return defHttp.post({
    url: Api.monitorSaveAndUpdate,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8',
    },
    params: {
      id: params.id,
      indexCode: params.indexCode,
      accessMethod: params.entryMode,
      factoryCode: params.factoryCode,
      factoryName: params.factoryName,
      buildCode: params.buildCode,
      buildName: params.buildName,
    },
  });
};

export const deleteSingle = (params) => {
  return defHttp.post({
    url: Api.thresholdDelete,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8',
    },
    params: { id: params.id },
  });
};

function normalizePageResult(data) {
  if (Array.isArray(data)) {
    return {
      dataList: data,
      total: data.length,
    };
  }
  return {
    dataList: data?.dataList || data?.records || data?.list || data?.rows || [],
    total: data?.total || data?.totalCount || data?.count || 0,
  };
}

function normalizeAreaParams(params) {
  const { areaCode, ...rest } = params || {};
  return rest;
}
