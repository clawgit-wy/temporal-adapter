export const Namespace = 'data-encoder' as const;

export const Strings = {
  'codec-server': '编解码服务器',
  'endpoint-title': '编解码服务器浏览器端点',
  'endpoint-description':
    '为此浏览器输入一个编解码服务器端点。该端点将存储在您的浏览器中，仅您自己可以访问。',
  'endpoint-placeholder': '在此粘贴您的端点',
  'pass-access-token-label': '传递用户访问令牌',
  'include-cross-origin-credentials-label': '包含跨域凭据',
  'include-cross-origin-credentials-warning':
    '警告：将进行预检请求，如果配置不正确可能导致解码失败。',
  'port-title': 'tctl 插件端口 ',
  'port-info': '如果两者都设置了，将使用编解码服务器端点。',
  'access-token-https-error':
    '如果传递访问令牌，端点必须是 https://',
  'prefix-error': '端点必须以 http:// 或 https:// 开头',
  'codec-server-description-prefix': '一个 ',
  'codec-server-description-suffix':
    ' 会解码您的数据。可以在 {{level}} 级别设置编解码服务器端点，也可以在本地浏览器中设置。',
  'browser-override-description':
    '使用我的浏览器设置并忽略 {{level}} 级别的设置。',
  'no-browser-override-description':
    '使用 {{level}} 级别的设置（如果可用）。',
  'override-radio-group-description':
    '选择是否要在此浏览器中覆盖 {{level}} 设置。',
  'codec-server-configured': '编解码服务器已配置',
  'codec-server-error': '编解码服务器无法连接',
  'codec-server-success': '编解码服务器成功转换内容',
  'configure-codec-server': '配置编解码服务器',
  'encode-error': '编解码服务器编码失败',
} as const;
