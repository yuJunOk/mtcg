package com.aris.mtcg.engine.effect;

import com.aris.mtcg.engine.keyword.Keyword;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * EffectParser 空壳实现（当前迭代）。
 *
 * <p>不解析 condition / action，仅承载原文并从文本抽取关键词标记；空文本不报错。
 *
 * @author pengYuJun
 */
public class NoOpEffectParser implements EffectParser {

    @Override
    public Effect parse(String effectText) {
        Set<Keyword> keywordSet = Keyword.fromEffectText(effectText);
        List<Keyword> keywords = new ArrayList<>(keywordSet);
        // type / effectiveZone 由卡牌录入侧另行标注，当前默认 null
        return Effect.ofText(effectText, keywords);
    }

    @Override
    public boolean supports(String effectText) {
        return false;
    }
}
