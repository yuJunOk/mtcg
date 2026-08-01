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

  const spanRegex = /<span[^>]*leaf[^>]*>([^<]+)<\/span>/g;
  const spans = [];
  while ((m = spanRegex.exec(text)) !== null) {
    const s = m[1].trim();
    if (s) spans.push(s);
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
    spans,
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
  const docsDir = __dirname;
  const articles = [
    { file: '_temp_rules.html', outDir: '规则书图片', label: 'rules' },
    { file: '_temp_quickstart.html', outDir: '快速入门图片', label: 'quickstart' },
  ];

  for (const art of articles) {
    const filePath = path.join(docsDir, art.file);
    const info = extractArticle(filePath);
    console.log(`\n=== ${art.label} ===`);
    console.log(`Title: ${info.title}`);
    console.log(`Images: ${info.imgCount}`);
    console.log(`Spans: ${JSON.stringify(info.spans.slice(0, 10))}`);
    console.log(`Plain text length: ${info.plainLen}`);
    console.log(`Preview:\n${info.plainPreview.slice(0, 500)}`);

    fs.writeFileSync(
      path.join(docsDir, `${art.label}_meta.json`),
      JSON.stringify(info, null, 2),
      'utf8'
    );

    const imgDir = path.join(docsDir, '..', '..', 'docs', '规则文档', art.outDir);
    fs.mkdirSync(imgDir, { recursive: true });

    let ok = 0;
    for (let i = 0; i < info.imgs.length; i++) {
      const url = info.imgs[i];
      const ext = url.includes('wx_fmt=png') ? 'png' : url.includes('wx_fmt=jpeg') ? 'jpg' : 'png';
      const dest = path.join(imgDir, `${String(i + 1).padStart(3, '0')}.${ext}`);
      if (fs.existsSync(dest) && fs.statSync(dest).size > 1000) {
        ok++;
        continue;
      }
      try {
        await download(url, dest);
        ok++;
        process.stdout.write(`Downloaded ${i + 1}/${info.imgs.length}\r`);
      } catch (e) {
        console.error(`Failed ${i + 1}: ${e.message}`);
      }
    }
    console.log(`\nDownloaded ${ok}/${info.imgs.length} images to ${art.outDir}/`);
  }
}

main().catch(console.error);
