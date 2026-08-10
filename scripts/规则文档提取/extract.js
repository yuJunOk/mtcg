/**
 * 微信公众号文章解析工具
 *
 * 功能：解析微信文章 HTML，提取标题、正文文本、图片 URL
 *
 * 用法：
 *   node extract.js <html文件路径>
 *
 * 示例：
 *   node extract.js _temp_rules.html
 */
const fs = require('fs');
const path = require('path');
const https = require('https');
const http = require('http');

function extractArticle(filePath) {
  const text = fs.readFileSync(filePath, 'utf8');
  const titleMatch = text.match(/id="activity-name"[^>]*>[\s\S]*?<span[^>]*>([\s\S]*?)<\/span>/);
  const contentMatch = text.match(/id="js_content"[^>]*>([\s\S]*?)<\/div>\s*\n\s*<script/) ||
    text.match(/id="js_content"[^>]*>([\s\S]*?)<script/);

  const imgRegex = /(?:data-src|src)="(https:\/\/mmbiz[^"]+)"/g;
  const imgs = [];
  const seen = new Set();
  let m;
  while ((m = imgRegex.exec(text)) !== null) {
    if (!seen.has(m[1])) {
      seen.add(m[1]);
      imgs.push(m[1].replace(/&amp;/g, '&'));
    }
  }

  let plain = '';
  if (contentMatch) {
    plain = contentMatch[1]
      .replace(/<[^>]+>/g, '\n')
      .replace(/&amp;/g, '&')
      .replace(/&quot;/g, '"')
      .replace(/&lt;/g, '<')
      .replace(/&gt;/g, '>')
      .replace(/\n{3,}/g, '\n\n')
      .trim();
  }

  return {
    title: titleMatch ? titleMatch[1].trim() : null,
    imgCount: imgs.length,
    imgs,
    plainPreview: plain.slice(0, 3000),
    plainLen: plain.length,
  };
}

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
        reject(new Error(`HTTP ${res.statusCode} for ${url}`));
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
  const htmlPath = process.argv[2];
  if (!htmlPath) {
    console.log('用法: node extract.js <html文件路径>');
    console.log('示例: node extract.js _temp_rules.html');
    process.exit(1);
  }

  const filePath = path.resolve(htmlPath);
  if (!fs.existsSync(filePath)) {
    console.error(`文件不存在: ${filePath}`);
    process.exit(1);
  }

  const info = extractArticle(filePath);
  console.log(`\n=== 文章信息 ===`);
  console.log(`标题: ${info.title}`);
  console.log(`图片数: ${info.imgCount}`);
  console.log(`正文长度: ${info.plainLen}`);
  console.log(`\n预览:\n${info.plainPreview.slice(0, 500)}`);

  // 保存元数据
  const outMeta = htmlPath.replace(/\.html$/, '_meta.json');
  fs.writeFileSync(outMeta, JSON.stringify(info, null, 2), 'utf8');
  console.log(`\n元数据已保存: ${outMeta}`);
}

main().catch(console.error);
