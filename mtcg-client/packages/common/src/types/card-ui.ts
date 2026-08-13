// ==================== 卡牌相关类型（不含 CardVO 等 API 类型，API 类型由 types/card.ts 提供） ====================

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

/** 官网卡表式筛选：罕度芯片（与官网顺序一致） */
export const CARD_RARITY_FILTER_CODES: CardRarity[] = [
  'C',
  'R',
  'SR',
  'GR',
  'UR',
  'MR',
  'SEC',
  'PR',
  'ER',
  'TR',
]

/** 官网卡表式筛选：等级 1–6 */
export const CARD_LEVEL_FILTER_OPTIONS: number[] = [1, 2, 3, 4, 5, 6]

/** 官网卡表式筛选：攻击距离 0–5 */
export const CARD_ATTACK_RANGE_FILTER_OPTIONS: number[] = [0, 1, 2, 3, 4, 5]

/**
 * 常见特征筛选（对应卡牌 traits 字段，斜杠分隔）。
 * 与官网/卡表数据对齐，可按批次扩充。
 */
export const CARD_TRAIT_FILTER_OPTIONS: string[] = [
  '人类',
  '复仇者联盟',
  '机械',
  '阿斯加德',
  '瓦坎达',
  '神奇四侠',
  '捍卫者联盟',
  '变种人',
  '银河护卫队',
  '时间犯',
  '神盾局',
  '九头蛇',
  '赞恩拉',
  '亚特兰蒂斯',
  '卡玛泰姬',
  '斗界',
]

/** 辅助：code -> desc */
export function codeToDesc<T extends string>(
  options: Array<{ code: T; desc: string }>,
  code: T | null | undefined,
): string {
  if (!code) return ''
  return options.find((o) => o.code === code)?.desc ?? String(code)
}
