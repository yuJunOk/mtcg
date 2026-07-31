/**
 * 从微信公众号文章 HTML 中提取图片并下载到本地。
 *
 * 用法：
 *   node _download_images.js <html文件路径> <输出目录> [前缀] [图片格式]
 *
 * 示例：
 *   node _download_images.js ../snapshots/_temp_qa1.html ../../QA图片-第1期 qa1- png
 */
const fs = require('fs');
const path = require('path');
const https = require('https');
const http = require('http');

const htmlPath = process.argv[2];
const outDir = process.argv[3];
const prefix = process.argv[4] || '';
const ext = process.argv[5] || 'png';

if (!htmlPath || !outDir) {
  console.log('用法: node _download_images.js <html文件路径> <输出目录> [图片格式]');
  process.exit(1);
}

const text = fs.readFileSync(path.resolve(htmlPath), 'utf8');

// 从 js_content 区域提取图片 URL
const contentMatch = text.match(/id="js_content"[^>]*>([\s\S]*?)<\/div>\s*\n\s*<script/) ||
  text.match(/id="js_content"[^>]*>([\s\S]*?)<script/);
const content = contentMatch ? contentMatch[1] : '';

const imgRegex = /data-src="(https:\/\/mmbiz[^"]+)"/g;
const imgs = [];
const seen = new Set();
let m;
while ((m = imgRegex.exec(content)) !== null) {
  const url = m[1].replace(/&amp;/g, '&');
  if (!seen.has(url)) {
    seen.add(url);
    imgs.push(url);
  }
}

console.log(`找到 ${imgs.length} 张图片`);

function download(url, dest) {
  return new Promise((resolve, reject) => {
    const proto = url.startsWith('https') ? https : http;
    const req = proto.get(url, {
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        Referer: 'https://mp.weixin.qq.com/',
      },
    }, (res) => {
      if (res.statusCode >= 300 && res.statusCode < 400 && res.headers.location) {
        return download(res.headers.location, dest).then(resolve).catch(reject);
      }
      if (res.statusCode !== 200) {
        reject(new Error(`HTTP ${res.statusCode}`));
        return;
      }
      const file = fs.createWriteStream(dest);
      res.pipe(file);
      file.on('finish', () => file.close(() => resolve(dest)));
      file.on('error', reject);
    });
    req.on('error', reject);
  });
}

async function main() {
  const absOutDir = path.resolve(outDir);
  fs.mkdirSync(absOutDir, { recursive: true });

  for (let i = 0; i < imgs.length; i++) {
    const url = imgs[i];
    const dest = path.join(absOutDir, `${prefix}${String(i + 1).padStart(3, '0')}.${ext}`);
    try {
      await download(url, dest);
      const size = fs.statSync(dest).size;
      console.log(`下载 ${i + 1}/${imgs.length}: ${size} bytes`);
    } catch (e) {
      console.error(`失败 ${i + 1}: ${e.message}`);
    }
  }
  console.log('完成!');
}

main().catch(console.error);
