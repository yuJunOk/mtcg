#!/usr/bin/env python3
"""Extract images and text from WeChat article HTML."""
import re
import html
import json
from pathlib import Path


def extract_article(path: str) -> dict:
    text = Path(path).read_text(encoding="utf-8", errors="ignore")

    title_m = re.search(
        r'id="activity-name"[^>]*>.*?<span[^>]*>(.*?)</span>', text, re.S
    )
    content_m = re.search(
        r'id="js_content"[^>]*>(.*?)</div>\s*\n\s*<script', text, re.S
    )
    if not content_m:
        content_m = re.search(r'id="js_content"[^>]*>(.*?)<script', text, re.S)

    imgs = re.findall(r'data-src="(https://mmbiz[^"]+)"', text)
    imgs += re.findall(r'src="(https://mmbiz[^"]+)"', text)
    all_imgs = list(dict.fromkeys(imgs))

    spans = re.findall(r'<span[^>]*leaf[^>]*>([^<]+)</span>', text)
    spans = [html.unescape(s.strip()) for s in spans if s.strip()]

    # strip tags from content for rough text
    raw = content_m.group(1) if content_m else ""
    plain = re.sub(r"<[^>]+>", "\n", raw)
    plain = html.unescape(plain)
    plain = re.sub(r"\n{3,}", "\n\n", plain).strip()

    return {
        "title": html.unescape(title_m.group(1).strip()) if title_m else None,
        "img_count": len(all_imgs),
        "imgs": all_imgs,
        "spans": spans,
        "plain_text_len": len(plain),
        "plain_preview": plain[:2000],
    }


if __name__ == "__main__":
    for name in ["_temp_rules.html", "_temp_quickstart.html"]:
        p = Path(__file__).parent / name
        info = extract_article(str(p))
        print(f"=== {name} ===")
        print(json.dumps({k: v for k, v in info.items() if k != "imgs"}, ensure_ascii=False, indent=2))
        print(f"First 3 image URLs:")
        for u in info["imgs"][:3]:
            print(u[:120] + "...")
        out = Path(__file__).parent / name.replace(".html", "_imgs.json")
        out.write_text(json.dumps(info["imgs"], ensure_ascii=False, indent=2), encoding="utf-8")
        print(f"Saved {len(info['imgs'])} URLs to {out.name}\n")
