// ========================================================
// 仪表盘
// ========================================================

export interface DashboardStatsVO {
  /** 卡牌总数 */
  cardCount: number
  /** 产品总数 */
  productCount: number
  /** 用户总数 */
  userCount: number
  /** 今日对局数（迭代五才有对战表，暂返回 0） */
  todayBattleCount: number
}

export interface HealthVO {
  status: string
  timestamp: string
}

/** 审计日志 / 最近活动 */
export interface AuditLogVO {
  id: number
  actorUsercode: string | null
  action: string
  resourceType: string
  resourceId: string | null
  detail: string | null
  createTime: string
}
