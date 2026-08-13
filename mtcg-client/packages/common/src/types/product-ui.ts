/** 商品分类（对标官网：基础卡组 / 补充包 / 其他；后端暂无独立字段，由编号前缀推断） */
export type ProductCategory = 'STARTER' | 'BOOSTER' | 'OTHER'

export const PRODUCT_CATEGORY_OPTIONS: Array<{ code: ProductCategory; desc: string }> = [
  { code: 'STARTER', desc: '基础卡组' },
  { code: 'BOOSTER', desc: '补充包' },
  { code: 'OTHER', desc: '其他' },
]

/**
 * 由 productCode 推断分类：SD→基础卡组，BP→补充包，其余→其他。
 * 与官网 category(base/supplement) 对齐。
 */
export function resolveProductCategory(productCode: string | null | undefined): ProductCategory {
  const code = (productCode ?? '').trim().toUpperCase()
  if (code.startsWith('SD')) return 'STARTER'
  if (code.startsWith('BP')) return 'BOOSTER'
  return 'OTHER'
}

export function getProductCategoryLabel(category: ProductCategory): string {
  return PRODUCT_CATEGORY_OPTIONS.find((o) => o.code === category)?.desc ?? category
}

/** 列表封面：优先 imagePaths[0]，否则 imagePath */
export function productCoverPath(product: {
  imagePath?: string | null
  imagePaths?: string[] | null
}): string | null {
  const paths = product.imagePaths?.filter((p) => Boolean(p?.trim())) ?? []
  if (paths.length > 0) return paths[0]
  const single = product.imagePath?.trim()
  return single || null
}
