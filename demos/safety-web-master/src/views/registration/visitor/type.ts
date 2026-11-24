export interface FileInfo {
  file: FileItem;
  fileList: FileItem[];
}
export interface FileResponse {
  code: string;
  data: string;
}

export interface FileItem {
  uid: string;
  name?: string;
  status?: string;
  response?: FileResponse;
  url?: string;
  type?: string;
  size: number;
  originFileObj: any;
}

export interface ViewIdCarResponse {
  id: number;
  authFactoryNames?: string;
  hairpinNo?: string;
}
