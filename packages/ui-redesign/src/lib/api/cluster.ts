/**
 * 集群信息 API 调用
 */
import { request } from './client';
import type {
  GetClusterInfoResponse,
  GetSystemInfoResponse,
  Settings,
  ListNamespacesResponse,
  DescribeNamespaceResponse,
} from './types';

/**
 * 获取集群信息
 */
export async function getClusterInfo(): Promise<GetClusterInfoResponse> {
  return request<GetClusterInfoResponse>('/cluster-info');
}

/**
 * 获取系统信息
 */
export async function getSystemInfo(): Promise<GetSystemInfoResponse> {
  return request<GetSystemInfoResponse>('/system-info');
}

/**
 * 获取 UI 设置
 */
export async function getSettings(): Promise<Settings> {
  return request<Settings>('/settings');
}

/**
 * 获取命名空间列表
 */
export async function listNamespaces(): Promise<ListNamespacesResponse> {
  return request<ListNamespacesResponse>('/namespaces');
}

/**
 * 获取命名空间详情
 */
export async function describeNamespace(
  namespace: string,
): Promise<DescribeNamespaceResponse> {
  return request<DescribeNamespaceResponse>(
    `/namespaces/${encodeURIComponent(namespace)}`,
  );
}
