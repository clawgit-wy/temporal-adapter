import { defHttp } from '/@/utils/http/axios';

export enum Api {
  list = '/safety/hik/abnormalEnter/pageList',
  exportXls = '/safety/hik/abnormalEnter/export',
}

export const list = async (params) => {
  const data = await defHttp.post({ url: Api.list, params: normalizeAreaParams(params) });
  return normalizePageResult(data);
};

export const exportXls = (params) => {
  return defHttp.get(
    { url: Api.exportXls, params: normalizeAreaParams(params), responseType: 'blob' },
    { isTransformResponse: false },
  );
};

function normalizePageResult(data) {
  const rawList = Array.isArray(data)
    ? data
    : data?.dataList || data?.records || data?.list || data?.rows || [];
  const dataList = rawList.map((item) => normalizeRow(item));
  if (Array.isArray(data)) {
    return {
      dataList,
      total: data.length,
    };
  }
  return {
    dataList,
    total: data?.total || data?.totalCount || data?.count || 0,
  };
}

function normalizeRow(item) {
  return {
    ...item,
    empNo: item?.empNo ?? item?.emp_no,
    abnormalEnterType: item?.abnormalEnterType ?? item?.abnormal_enter_type,
    empType: item?.empType ?? item?.emp_type,
    orgName: item?.orgName ?? item?.org_name,
    accessMethod: item?.accessMethod ?? item?.access_method,
    factoryName: item?.factoryName ?? item?.factory_name,
    buildName: item?.buildName ?? item?.build_name,
    cameraName: item?.cameraName ?? item?.camera_name,
    cameraIndexcode: item?.cameraIndexcode ?? item?.camera_indexcode,
    deviceIndexcode: item?.deviceIndexcode ?? item?.device_indexcode,
    eventTime: item?.eventTime ?? item?.event_time,
    snapUrl: item?.snapUrl ?? item?.snap_url,
    facePhoto: item?.facePhoto ?? item?.snapUrl ?? item?.snap_url,
  };
}

function normalizeAreaParams(params) {
  const { areaCode, ...rest } = params || {};
  return rest;
}
