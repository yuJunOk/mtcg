/**
 * 将后端返回的 imagePath（相对路径）转为浏览器可请求的 URL。
 *
 * <p>约定：DB 存相对路径，如 {@code card/faces/BP01/BP01-001-MR.png}； 前端统一走 {@code
 * /files/...}，由 Vite 代理到 {@code /api/files/...}。
 */
export function resolveCardImageUrl(imagePath: string | null | undefined): string {
  if (!imagePath) {
    return ''
  }
  const trimmed = imagePath.trim()
  if (!trimmed) {
    return ''
  }
  if (
    trimmed.startsWith('http://') ||
    trimmed.startsWith('https://') ||
    trimmed.startsWith('data:') ||
    trimmed.startsWith('blob:')
  ) {
    return trimmed
  }
  // 已是 /files/... 或历史 /uploads/...
  if (trimmed.startsWith('/files/') || trimmed.startsWith('/uploads/')) {
    return trimmed.startsWith('/uploads/')
      ? `/files/${trimmed.slice('/uploads/'.length)}`
      : trimmed
  }
  const relative = trimmed.replace(/^\/+/, '')
  return `/files/${relative}`
}
