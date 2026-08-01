/**
 * 对指定目录下的图片进行 OCR 识别（中文简体）。
 * 依赖 tesseract.js（安装在本目录的父级 node_modules）。
 *
 * 用法：
 *   node _ocr_recognize.js <图片目录> <输出文件>
 *
 * 示例：
 *   node _ocr_recognize.js ../docs/规则文档/QA图片-第1期 qa1_ocr.txt
 */
const fs = require('fs');
const path = require('path');
const { createWorker } = require('tesseract.js');

const imgDir = process.argv[2];
const outFile = process.argv[3];

if (!imgDir || !outFile) {
  console.log('用法: node _ocr_recognize.js <图片目录> <输出文件>');
  process.exit(1);
}

const absImgDir = path.resolve(imgDir);
const absOutFile = path.resolve(outFile);
const ws = fs.createWriteStream(absOutFile);

async function main() {
  // 使用本地 chi_sim.traineddata（同目录）
  const worker = await createWorker('chi_sim', 1, {
    langPath: __dirname,
  });

  const files = fs.readdirSync(absImgDir).filter(f => /\.(png|jpg|jpeg)$/i.test(f)).sort();
  ws.write(`OCR 结果 - 共 ${files.length} 张图片\n`);
  ws.write(`图片目录: ${absImgDir}\n`);
  ws.write(`识别时间: ${new Date().toISOString()}\n\n`);

  for (const file of files) {
    const filePath = path.join(absImgDir, file);
    console.log(`识别 ${file}...`);
    ws.write(`\n========== ${file} ==========\n`);
    try {
      const { data: { text } } = await worker.recognize(filePath);
      ws.write(text);
      ws.write('\n');
      console.log(`  ${text.length} 字符`);
    } catch (e) {
      ws.write(`ERROR: ${e.message}\n`);
      console.error(`  失败: ${e.message}`);
    }
  }

  await worker.terminate();
  ws.end();
  console.log(`完成! 输出: ${absOutFile}`);
}

main().catch(e => { console.error(e); process.exit(1); });
