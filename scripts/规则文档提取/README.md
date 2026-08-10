# 规则文档提取工具

从微信公众号文章中提取规则文本和图片的脚本集合。

## 快速开始

### 1. 安装依赖

```bash
cd scripts && npm install
```

### 2. 准备文章

浏览器打开文章 → 右键「查看网页源代码」→ 全选复制 → 保存为 `.html`

### 3. 运行脚本

```bash
cd scripts/规则文档提取

# 解析文章内容（提取文本和图片 URL）
node extract.js <文章.html>

# 下载文章图片
node download.js <文章.html> <输出目录> [前缀] [格式]

# OCR 识别图片中的文字
node ocr.js <图片目录> <输出文件>
```

## 脚本说明

| 脚本 | 说明 |
|------|------|
| `extract.js` | 解析微信文章 HTML，输出元数据 JSON |
| `download.js` | 下载文章中的图片到指定目录 |
| `ocr.js` | 对图片目录做中文 OCR，输出文本 |

## 目录结构

```
规则文档提取/
├── README.md            # 本文件
├── extract.js           # 文章解析
├── download.js          # 图片下载
├── ocr.js              # OCR 识别
└── lib/                # 工具数据
    ├── chi_sim.traineddata  # tesseract 中文训练数据
    └── results/             # 中间产物（OCR 结果、元数据）
```

## 官方更新流程

当官方发布新的规则文章时：
1. 保存网页源代码为 `.html` 文件
2. `node extract.js <文章.html>` 解析内容
3. `node download.js <文章.html> ../docs/规则文档/规则书图片 rules-` 下载图片
4. 整理规则文本，更新 `docs/规则文档/` 下对应的 `.md`
5. 更新版本号和来源链接

如果新文章是规则 Q&A：
1. `node download.js <文章.html> ../docs/规则文档/QA图片-第{N}期/ qa{N}-`
2. `node ocr.js ../docs/规则文档/QA图片-第{N}期/ lib/results/qa{N}_ocr.txt`
3. 人工校对，修正识别错误
4. 在 `docs/规则文档/` 下新建 `05-规则Q&A-第{N}期.md`
5. 更新索引
