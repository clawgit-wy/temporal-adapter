import { useState, useEffect } from 'react';
import { appConfig } from '@/lib/config';

export type ThemeMode = 'light' | 'dark' | 'system';
type AppliedTheme = 'light' | 'dark';

function getSystemTheme(): AppliedTheme {
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

function getStoredMode(): ThemeMode {
  const saved = localStorage.getItem('theme-mode') as ThemeMode | null;
  if (saved === 'light' || saved === 'dark' || saved === 'system') return saved;
  // 兼容旧版仅存 light/dark 的情况
  const legacy = localStorage.getItem('theme') as AppliedTheme | null;
  if (legacy === 'light' || legacy === 'dark') return legacy;
  // 使用环境配置的默认值
  return appConfig.defaultTheme;
}

function resolveAppliedTheme(mode: ThemeMode): AppliedTheme {
  return mode === 'system' ? getSystemTheme() : mode;
}

export function useTheme() {
  const [mode, setMode] = useState<ThemeMode>(() => getStoredMode());
  const [appliedTheme, setAppliedTheme] = useState<AppliedTheme>(() =>
    resolveAppliedTheme(getStoredMode()),
  );

  useEffect(() => {
    let resolved: AppliedTheme = mode === 'system' ? getSystemTheme() : mode;

    const apply = () => {
      document.documentElement.classList.remove('light', 'dark');
      document.documentElement.classList.add(resolved);
      document.documentElement.setAttribute('data-theme', resolved);
      localStorage.setItem('theme-mode', mode);
      // 兼容旧字段
      localStorage.setItem('theme', resolved);
      setAppliedTheme(resolved);
    };
    apply();

    if (mode === 'system') {
      const media = window.matchMedia('(prefers-color-scheme: dark)');
      const handler = () => {
        resolved = getSystemTheme();
        apply();
      };
      media.addEventListener('change', handler);
      return () => media.removeEventListener('change', handler);
    }
  }, [mode]);

  const setThemeMode = (next: ThemeMode) => setMode(next);
  const toggleTheme = () => setMode((prev) => (prev === 'dark' ? 'light' : 'dark'));

  return {
    mode,
    appliedTheme,
    isDark: appliedTheme === 'dark',
    setThemeMode,
    toggleTheme,
  };
}
