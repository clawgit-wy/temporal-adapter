import { defHttp } from '/@/utils/http/axios';

export enum Api {
  searchPhoto = '/safety/hik/faceSearch/searchPhoto',
}

// 人脸图片检索。项目接口基地址为 /web，实际请求地址为 /web/safety/hik/faceSearch/searchPhoto。
export const searchPhoto = (file: File) => {
  return defHttp.uploadFile({ url: Api.searchPhoto }, { file }, { isReturnResponse: true });
};
