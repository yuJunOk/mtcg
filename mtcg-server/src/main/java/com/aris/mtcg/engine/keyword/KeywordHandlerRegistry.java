package com.aris.mtcg.engine.keyword;

import java.util.EnumMap;
import java.util.Map;

/**
 * 关键词处理器注册表。
 *
 * @author pengYuJun
 */
public class KeywordHandlerRegistry {

    private final Map<Keyword, KeywordHandler> handlers = new EnumMap<>(Keyword.class);

    public KeywordHandlerRegistry() {
        register(new ResponseKeywordHandler());
        register(new InterceptKeywordHandler());
        register(new ComboKeywordHandler());
        register(new AssaultKeywordHandler());
        register(new AirStrikeKeywordHandler());
        register(new UniqueKeywordHandler());
    }

    public void register(KeywordHandler h) {
        handlers.put(h.getKeyword(), h);
    }

    public KeywordHandler get(Keyword k) {
        return handlers.get(k);
    }
}
