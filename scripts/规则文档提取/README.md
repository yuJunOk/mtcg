# 规则文档提取工具

从微信公众号文章中提取规则文本和图片的脚本集合。

## 依赖

```bash
cd scripts && npm install
```

## 脚本说明

| 脚本 | 说明 |
|------|------|
| `_extract_wechat.js` | 解析微信文章 HTML，提取文本/图片 URL |
| `_download_images.js` | 从 HTML 提取图片 URL 并下载 |
| `_ocr_recognize.js` | 对图片目录做中文 OCR，输出文本 |

`tesseract.js` 依赖已在父级 `scripts/package.json` 中声明。

`chi_sim.traineddata` 为 tesseract 中文简体训练数据，同目录加载无需联网。

## 使用流程

1. **下载网页**：浏览器打开文章 → 右键「查看网页源代码」→ 保存为 `.html`
2. **复制到工作区**：复制到 `规则文档提取/` 下的 `_temp_*.html`
3. **提取内容**：`node _extract_wechat.js <html文件>`
4. **下载图片**：`node _download_images.js <html文件> <输出目录> [前缀]`
5. **OCR 识别**：`node _ocr_recognize.js <图片目录> <输出文件>`

## 中间产物说明

| 文件 | 说明 |
|------|------|
| `qa1_ocr.txt` | Q&A 第一期 OCR 识别结果 |
| `quickstart_meta.json` | 快速入门解析元数据 |
| `rules_meta.json` | 综合规则书解析元数据 |

大型 HTML 快照已归档至 `../归档/` 目录。
