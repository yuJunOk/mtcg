/**
 * 仪表盘 API
 */
import { axios, extractData } from './request'
import type { DashboardStatsVO, HealthVO } from '../types/dashboard'

export const dashboardApi = {
  getStats: () =>
    axios.get<Record<string, unknown>>('/admin/dashboard/stats')
      .then(r => extractData<DashboardStatsVO>(r)),

  health: () =>
    axios.get<Record<string, unknown>>('/health')
      .then(r => extractData<HealthVO>(r)),
}
