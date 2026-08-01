import type { CardRenderData } from '../types'

/**
 * 游戏画布核心类。
 * 负责 PixiJS 初始化、卡牌渲染、交互事件。
 * PC 和移动端共用，通过构造函数参数区分布局。
 */
export class GameCanvas {
  private app: any // PIXI.Application，运行时动态引用
  private cards: Map<string, any> = new Map()
  private container: HTMLElement | null = null

  /** 布局模式 */
  private layout: 'pc' | 'mobile'

  constructor(layout: 'pc' | 'mobile' = 'pc') {
    this.layout = layout
  }

  /** 初始化 PixiJS 画布，挂载到指定 DOM 容器 */
  async init(containerId: string): Promise<void> {
    const PIXI = await import('pixi.js')

    this.container = document.getElementById(containerId)
    if (!this.container) {
      throw new Error(`容器 ${containerId} 不存在`)
    }

    this.app = new PIXI.Application()
    await this.app.init({
      resizeTo: this.container,
      background: '#1a1a2e',
      antialias: true,
      resolution: window.devicePixelRatio || 1,
      autoDensity: true,
    })

    this.container.appendChild(this.app.canvas)
  }

  /** 渲染卡牌列表 */
  renderCards(cards: CardRenderData[]): void {
    // TODO: 迭代实现 - 根据 CardRenderData 创建/更新 PixiJS Sprite
    cards.forEach(card => {
      // 创建或更新卡牌 Sprite
    })
  }

  /** 销毁画布 */
  destroy(): void {
    if (this.app) {
      this.app.destroy(true, { children: true, texture: true })
    }
    this.cards.clear()
  }
}