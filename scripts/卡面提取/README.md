# MTCG 卡面提取工具

从官网截图中批量提取 MTCG 卡牌卡面区域，自动裁剪、抗锯齿圆角处理、画质增强。

## 目录结构

```
scripts/卡面提取/
├── README.md               # 本文件
├── extract_card.py         # 核心提取脚本（Python）
├── run_extract.bat         # 一键运行脚本（双击即可）
└── fix_execution_policy.reg # PowerShell 执行策略修复（首次使用可选）
```

## 快速开始

### 1. 准备截图

将官网 APP 卡牌页面的全屏截图放入项目 `assets/card/raw/` 目录：

```
mtcg/
├── assets/
│   └── card/
│       ├── raw/                # ★ 原始截图放这里
│       │   ├── screenshot1.jpg
│       │   └── screenshot2.jpg
│       └── extracted/          # 提取结果（自动生成）
│           └── screenshot1_card.png
```

> 支持格式：PNG、JPG、JPEG、WebP、BMP

### 2. 一键提取

双击运行 `run_extract.bat`，脚本会自动：
- 修复 PowerShell 执行策略
- 检查 Python 环境
- 检查/安装 Pillow 依赖
- 批量提取 `assets/card/raw/` 下所有截图

提取结果保存在 `assets/card/extracted/` 目录。

### 3. 手动运行（可选）

如果想使用自定义参数，可以在命令行运行：

```bash
# 处理默认目录
python extract_card.py

# 指定输入目录
python extract_card.py assets/card/raw

# 处理单个文件
python extract_card.py screenshot.jpg -o output.png

# 自定义圆角半径
python extract_card.py --radius 15

# 微调裁剪坐标
python extract_card.py --left 160 --top 705 --right 900 --bottom 1730
```

## 配置参数

### 卡面区域（CARD_REGION）

基于 1080×2354 标准截图标定，脚本会按比例自动适配其他分辨率：

| 参数 | 默认值 | 说明 |
|------|--------|------|
| left | 168 | 卡面左边界 X 坐标 |
| top | 695 | 卡面上边界 Y 坐标 |
| right | 915 | 卡面右边界 X 坐标 |
| bottom | 1737 | 卡面下边界 Y 坐标 |

### 圆角半径

默认 41px，通过底部双角 74 点采样圆拟合得出。可通过 `--radius` 参数调整。

### 画质增强

- **超采样抗锯齿**：4x 分辨率渲染后 LANCZOS 降采样，消除圆角锯齿
- **UnsharpMask 锐化**：补偿 JPG 截图压缩伪影
- **PNG 无损输出**：保留透明通道

## 工作原理

1. **坐标缩放**：根据截图分辨率相对 1080×2354 的比例自动缩放坐标
2. **区域裁剪**：从截图中裁剪卡面区域
3. **画质增强**：UnsharpMask 锐化
4. **抗锯齿圆角**：4x 超采样绘制圆角蒙版 → LANCZOS 降采样 → 应用 Alpha 通道
5. **无损输出**：PNG 格式，保留圆角透明通道

## 故障排查

| 问题 | 解决方案 |
|------|----------|
| "无法加载文件，因为在此系统上禁止运行脚本" | 双击 `fix_execution_policy.reg` 导入注册表，或右键 `run_extract.bat` 以管理员身份运行 |
| "未找到 Python" | 确认 Python 已安装，默认路径 `D:\ProgramApp\Python\python-3.13.14\python.exe`，可在 `run_extract.bat` 中修改 |
| "需要安装 Pillow 库" | 运行 `pip install Pillow` |
| 提取的卡面尺寸不一致 | 使用 `--left/--top/--right/--bottom` 参数微调坐标 |

## 依赖

- Python 3.8+
- Pillow >= 8.2.0（支持 ImageDraw.rounded_rectangle）
