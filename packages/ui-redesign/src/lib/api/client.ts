/**
 * Temporal UI Server API 基础客户端
 * 所有请求通过 Vite proxy 或 Nginx 反向代理转发到 /api/v1/*
 *
 * 注意：官方 UI 镜像的 /api/v1/settings 返回 PascalCase 字段名
 * 而 gRPC 代理接口（workflows, namespaces 等）返回 camelCase
 * 本客户端通过 convertKeys 统一转换为 camelCase
 */

import { appConfig, debugLog } from '@/lib/config';

const API_BASE = appConfig.apiBaseUrl;

export interface RequestOptions {
  method?: string;
  body?: string;
  params?: Record<string, string | undefined>;
  headers?: Record<string, string>;
}

export class ApiError extends Error {
  status: number;
  body: any;
  constructor(message: string, status: number, body?: any) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

/**
 * 将 PascalCase 键名转换为 camelCase
 * 例如 "DefaultNamespace" -> "defaultNamespace"
 */
function toCamelCase(key: string): string {
  return key.charAt(0).toLowerCase() + key.slice(1);
}

/**
 * 递归转换对象的所有键名为 camelCase
 */
function convertKeys(obj: any): any {
  if (obj === null || obj === undefined) return obj;
  if (Array.isArray(obj)) return obj.map(convertKeys);
  if (typeof obj === 'object') {
    const result: Record<string, any> = {};
    for (const [key, value] of Object.entries(obj)) {
      const camelKey = toCamelCase(key);
      result[camelKey] = convertKeys(value);
    }
    return result;
  }
  return obj;
}

/**
 * 构建带查询参数的 URL
 */
function buildUrl(path: string, params?: Record<string, string | undefined>): string {
  const url = `${API_BASE}${path}`;
  if (!params) return url;

  const searchParams = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== null && value !== '') {
      searchParams.set(key, value);
    }
  }
  const queryString = searchParams.toString();
  return queryString ? `${url}?${queryString}` : url;
}

/**
 * 发送 API 请求
 * 自动将响应中的 PascalCase 键名转换为 camelCase
 */
export async function request<T>(
  path: string,
  options: RequestOptions = {},
): Promise<T> {
  const { method = 'GET', body, params, headers = {} } = options;
  const url = buildUrl(path, params);

  debugLog(`[${method}] ${url}`);

  // 对 POST/PUT/DELETE 请求自动附加 CSRF token
  const finalHeaders: Record<string, string> = {
    'Content-Type': 'application/json',
    ...headers,
  };
  if (method !== 'GET') {
    const csrfToken = getCsrfToken();
    if (csrfToken) {
      finalHeaders['X-CSRF-Token'] = csrfToken;
    }
  }

  const response = await fetch(url, {
    method,
    headers: finalHeaders,
    body: body ?? undefined,
  });

  if (!response.ok) {
    let errorBody: any;
    try {
      errorBody = await response.json();
    } catch {
      errorBody = { message: response.statusText };
    }
    throw new ApiError(
      errorBody?.message ?? `HTTP ${response.status}`,
      response.status,
      errorBody,
    );
  }

  // 处理空响应
  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return null as T;
  }

  // 处理 JSON 响应，统一转换为 camelCase
  const text = await response.text();
  try {
    const parsed = JSON.parse(text);
    return convertKeys(parsed) as T;
  } catch {
    return text as unknown as T;
  }
}

/**
 * 获取 CSRF token（从 cookie 中读取）
 * 官方 UI 镜像使用 _csrf 作为 cookie 名
 */
export function getCsrfToken(): string | null {
  const match = document.cookie.match(/_csrf=([^;]+)/);
  return match ? match[1] : null;
}

/**
 * 编解码服务器（Codec Server）相关
 * 用户在设置中配置后，存储于 localStorage 中
 * 调用 /decode 端点对加密 Payload 进行解码
 */
const CODEC_STORAGE_KEY = 'temporal-codec-server';

export function getCodecServerUrl(): string | null {
  return localStorage.getItem(CODEC_STORAGE_KEY) || null;
}

export function isCodecServerConfigured(): boolean {
  return !!getCodecServerUrl();
}

/**
 * 调用 Codec Server 解码 Payload
 * Codec Server 应实现 POST /decode 接口，接收 payloads 数组
 */
export async function decodePayloads(
  payloads: any[],
): Promise<any[]> {
  const codecUrl = getCodecServerUrl();
  if (!codecUrl || !payloads || payloads.length === 0) {
    return payloads;
  }
  try {
    const response = await fetch(`${codecUrl.replace(/\/$/, '')}/decode`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ payloads }),
    });
    if (!response.ok) {
      throw new Error(`Codec Server responded with ${response.status}`);
    }
    const data = await response.json();
    return data.payloads ?? payloads;
  } catch (err) {
    console.warn('[CodecServer] decode failed, falling back to raw payloads:', err);
    return payloads;
  }
}
