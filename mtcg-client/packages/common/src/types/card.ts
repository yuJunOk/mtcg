// ==================== 卡牌相关类型（不含 CardVO 等 API 类型，API 类型由 OpenAPI 生成） ====================

/** 卡牌类型 */
export type CardType = 'CHARACTER' | 'RUSH_POINT'

/** 卡牌颜色 */
export type CardColor = 'RED' | 'YELLOW' | 'BLUE' | 'GREEN' | 'ORANGE' | 'PURPLE'

/** 卡牌稀有度 */
export type CardRarity =
  | 'C' | 'R' | 'SR' | 'GR' | 'UR' | 'MR'
  | 'SEC' | 'HR' | 'LR' | 'PR' | 'ER' | 'TR'

// ========================================================
// 下拉选项常量（OpenAPI 不生成这些）
// ========================================================

/** 颜色下拉选项：code + desc */
export const CARD_COLOR_OPTIONS: Array<{ code: CardColor; desc: string }> = [
  { code: 'RED', desc: '红' },
  { code: 'YELLOW', desc: '黄' },
  { code: 'BLUE', desc: '蓝' },
  { code: 'GREEN', desc: '绿' },
  { code: 'ORANGE', desc: '橙' },
  { code: 'PURPLE', desc: '紫' },
]

/** 卡牌类型下拉 */
export const CARD_TYPE_OPTIONS: Array<{ code: CardType; desc: string }> = [
  { code: 'CHARACTER', desc: '角色卡' },
  { code: 'RUSH_POINT', desc: '冲击卡' },
]

/** 稀有度下拉 */
export const CARD_RARITY_OPTIONS: Array<{ code: CardRarity; desc: string }> = [
  { code: 'C', desc: '普通' },
  { code: 'R', desc: '稀有' },
  { code: 'SR', desc: '超稀有' },
  { code: 'GR', desc: '极稀有' },
  { code: 'UR', desc: '终极稀有' },
  { code: 'MR', desc: '一级异画' },
  { code: 'SEC', desc: '二级异画' },
  { code: 'HR', desc: '英雄异画' },
  { code: 'LR', desc: '传奇异画' },
  { code: 'PR', desc: '推广稀有' },
  { code: 'ER', desc: '赛事稀有' },
  { code: 'TR', desc: '宝藏稀有' },
]

/** 辅助：code -> desc */
export function codeToDesc<T extends string>(
  options: Array<{ code: T; desc: string }>,
  code: T | null | undefined,
): string {
  if (!code) return ''
  return options.find((o) => o.code === code)?.desc ?? String(code)
}
