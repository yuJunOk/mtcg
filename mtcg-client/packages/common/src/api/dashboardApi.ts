/**
 * 仪表盘 API
 */
import type { Ref } from 'vue'
import { http } from './request'
import type { AuditLogVO, DashboardStatsVO, HealthVO } from '../types/dashboard'

export const dashboardApi = {
  getStats: (loadingRef?: Ref<boolean>) =>
    http.get<DashboardStatsVO>('/admin/dashboard/stats', loadingRef),

  getRecentActivities: (limit = 20, loadingRef?: Ref<boolean>) =>
    http.getWithParams<AuditLogVO[]>(
      '/admin/dashboard/activities',
      { params: { limit } },
      loadingRef,
    ),

  health: (loadingRef?: Ref<boolean>) =>
    http.get<HealthVO>('/health', loadingRef),
}
