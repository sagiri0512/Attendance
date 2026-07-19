/**
 * JWT 前端解析工具
 *
 * 仅用于读取 payload（不验证签名，签名由后端校验）。
 * 用途：登录后立即拿到 role/realName，无需等待 /auth/current 接口返回，
 *       便于路由守卫与菜单显示做权限判断。
 *
 * JWT 三段式：header.payload.signature
 * payload 是 base64url 编码的 JSON，前端 atob 后 JSON.parse 即可。
 */

/**
 * 解析 JWT，返回 payload 对象
 * @param {string} token
 * @returns {{ sub?: string, role?: string, realName?: string, iat?: number, exp?: number } | null}
 */
export function parseJwt(token) {
  if (!token || typeof token !== 'string') return null
  const parts = token.split('.')
  if (parts.length !== 3) return null
  try {
    // base64url → base64：- 变 +，_ 变 /
    let payload = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    // 补齐 padding
    const pad = payload.length % 4
    if (pad) payload += '='.repeat(4 - pad)
    const json = decodeURIComponent(
      atob(payload)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(json)
  } catch (e) {
    console.warn('[jwt] parse failed:', e)
    return null
  }
}

/**
 * 从 token 中提取 role（数字类型，便于和 === 3 比较）
 * @param {string} token
 * @returns {number|null}
 */
export function getRoleFromToken(token) {
  const payload = parseJwt(token)
  if (!payload || payload.role == null) return null
  const n = Number(payload.role)
  return Number.isNaN(n) ? null : n
}

/**
 * 从 token 中提取 realName
 * @param {string} token
 * @returns {string|null}
 */
export function getRealNameFromToken(token) {
  const payload = parseJwt(token)
  return payload?.realName ?? null
}

/**
 * 从 token 中提取 userId（subject）
 * @param {string} token
 * @returns {string|null}
 */
export function getUserIdFromToken(token) {
  const payload = parseJwt(token)
  return payload?.sub ?? null
}
