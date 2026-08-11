package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.dto.DeckCardEntry;
import com.aris.mtcg.domain.dto.DeckCreateDTO;
import com.aris.mtcg.domain.entity.DeckDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 卡组展示对象
 *
 * @author pengYuJun
 */
@Data
public class DeckVO {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;

    private Long userId;

    private String deckName;

    /** 主卡组有序条目（位置=卡组内排序） */
    private List<DeckCardEntry> mainDeck;

    /** 冲击卡组有序条目 */
    private List<DeckCardEntry> rushDeck;

    private Boolean isValid;

    /** 用户自定义排序（卡组列表） */
    private Integer sortOrder;

    /** 用户自定义标签 */
    private String tags;

    /** 主卡组总张数 = sum(quantity) */
    private Integer mainDeckSize;

    /** 冲击卡组总张数 = sum(quantity) */
    private Integer rushDeckSize;

    private String createTime;

    private String updateTime;

    /** 从 DO 转换为本类 */
    public static DeckVO fromDO(DeckDO deck) {
        if (deck == null) {
            return null;
        }
        DeckVO vo = new DeckVO();
        vo.setId(deck.getId());
        vo.setUserId(deck.getUserId());
        vo.setDeckName(deck.getDeckName());
        vo.setMainDeck(parseEntries(deck.getMainDeckCodes()));
        vo.setRushDeck(parseEntries(deck.getRushDeckCodes()));
        vo.setIsValid(deck.getIsValid());
        vo.setSortOrder(deck.getSortOrder());
        vo.setTags(deck.getTags());
        vo.setMainDeckSize(totalQty(vo.getMainDeck()));
        vo.setRushDeckSize(totalQty(vo.getRushDeck()));
        vo.setCreateTime(deck.getCreateTime() != null ? deck.getCreateTime().format(FMT) : null);
        vo.setUpdateTime(deck.getUpdateTime() != null ? deck.getUpdateTime().format(FMT) : null);
        return vo;
    }

    /** 从 DTO 转换为本类 */
    public static DeckVO fromDTO(DeckCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        DeckVO vo = new DeckVO();
        vo.setDeckName(dto.getDeckName());
        vo.setMainDeck(dto.getMainDeck());
        vo.setRushDeck(dto.getRushDeck());
        vo.setTags(dto.getTags());
        vo.setMainDeckSize(totalQty(dto.getMainDeck()));
        vo.setRushDeckSize(totalQty(dto.getRushDeck()));
        return vo;
    }

    /** 将有序条目列表序列化为 JSON */
    public static String toJson(List<DeckCardEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return "[]";
        }
        try {
            return MAPPER.writeValueAsString(entries);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private static List<DeckCardEntry> parseEntries(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<List<DeckCardEntry>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private static int totalQty(List<DeckCardEntry> entries) {
        if (entries == null) {
            return 0;
        }
        return entries.stream().mapToInt(e -> e.getQuantity() == null ? 0 : e.getQuantity()).sum();
    }
}
