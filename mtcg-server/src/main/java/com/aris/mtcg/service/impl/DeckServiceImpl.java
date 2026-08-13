package com.aris.mtcg.service.impl;

import com.aris.mtcg.common.enums.EnumCardType;
import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.dao.DeckMapper;
import com.aris.mtcg.domain.dto.DeckCardEntry;
import com.aris.mtcg.domain.dto.DeckCreateDTO;
import com.aris.mtcg.domain.dto.DeckReorderDTO;
import com.aris.mtcg.domain.dto.DeckUpdateDTO;
import com.aris.mtcg.domain.entity.CardDO;
import com.aris.mtcg.domain.entity.DeckDO;
import com.aris.mtcg.domain.vo.DeckVO;
import com.aris.mtcg.domain.vo.DeckValidateResultVO;
import com.aris.mtcg.service.DeckService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 卡组服务实现
 *
 * @author pengYuJun
 */
@Service
public class DeckServiceImpl implements DeckService {

    @Resource private DeckMapper deckMapper;

    @Resource private CardMapper cardMapper;

    @Override
    public Long createDeck(Long userId, DeckCreateDTO dto) {
        List<DeckCardEntry> mainDeck = normalizeEntries(dto.getMainDeck());
        List<DeckCardEntry> rushDeck = normalizeEntries(dto.getRushDeck());
        DeckValidateResultVO result = validateEntries(mainDeck, rushDeck);
        DeckDO deck = new DeckDO();
        deck.setUserId(userId);
        deck.setDeckName(dto.getDeckName());
        deck.setMainDeckCodes(DeckVO.toJson(mainDeck));
        deck.setRushDeckCodes(DeckVO.toJson(rushDeck));
        deck.setIsValid(result.getValid());
        deck.setTags(normalizeTags(dto.getTags()));
        deck.setCoverCardCode(resolveCoverCardCode(dto.getCoverCardCode(), mainDeck, rushDeck));
        deck.setSortOrder(nextSortOrder(userId));
        deckMapper.insert(deck);
        return deck.getId();
    }

    @Override
    public void updateDeck(Long userId, Long deckId, DeckUpdateDTO dto) {
        DeckDO deck = checkOwnership(userId, deckId);
        boolean needRevalidate = false;
        if (dto.getDeckName() != null) {
            deck.setDeckName(dto.getDeckName());
        }
        if (dto.getMainDeck() != null) {
            deck.setMainDeckCodes(DeckVO.toJson(normalizeEntries(dto.getMainDeck())));
            needRevalidate = true;
        }
        if (dto.getRushDeck() != null) {
            deck.setRushDeckCodes(DeckVO.toJson(normalizeEntries(dto.getRushDeck())));
            needRevalidate = true;
        }
        if (dto.getTags() != null) {
            deck.setTags(normalizeTags(dto.getTags()));
        }
        if (needRevalidate) {
            DeckVO vo = DeckVO.fromDO(deck);
            DeckValidateResultVO result = validateEntries(vo.getMainDeck(), vo.getRushDeck());
            deck.setIsValid(result.getValid());
        }
        if (dto.getCoverCardCode() != null || needRevalidate) {
            DeckVO vo = DeckVO.fromDO(deck);
            String requested =
                    dto.getCoverCardCode() != null
                            ? dto.getCoverCardCode()
                            : deck.getCoverCardCode();
            deck.setCoverCardCode(
                    resolveCoverCardCode(requested, vo.getMainDeck(), vo.getRushDeck()));
        }
        deckMapper.update(deck);
    }

    @Override
    public void deleteDeck(Long userId, Long deckId) {
        checkOwnership(userId, deckId);
        deckMapper.deleteById(deckId);
    }

    @Override
    public DeckVO getDeck(Long userId, Long deckId) {
        DeckDO deck = checkOwnership(userId, deckId);
        DeckVO vo = DeckVO.fromDO(deck);
        attachCoverImages(List.of(vo));
        return vo;
    }

    @Override
    public List<DeckVO> listDecks(Long userId, String tag) {
        QueryWrapper qw = QueryWrapper.create().eq("user_id", userId);
        if (StringUtils.isNotBlank(tag)) {
            qw.and("(',' || COALESCE(tags, '') || ',') LIKE {0}", "%," + tag.trim() + ",%");
        }
        qw.orderBy("sort_order", true).orderBy("id", true);
        List<DeckDO> decks = deckMapper.selectListByQuery(qw);
        List<DeckVO> vos = decks.stream().map(DeckVO::fromDO).collect(Collectors.toList());
        attachCoverImages(vos);
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reorderDecks(Long userId, DeckReorderDTO dto) {
        for (DeckReorderDTO.DeckReorderItem item : dto.getItems()) {
            DeckDO deck = checkOwnership(userId, item.getId());
            deck.setSortOrder(item.getSortOrder());
            deckMapper.update(deck);
        }
    }

    @Override
    public DeckValidateResultVO validateDeck(Long userId, Long deckId) {
        DeckDO deck = checkOwnership(userId, deckId);
        DeckVO vo = DeckVO.fromDO(deck);
        DeckValidateResultVO result = validateEntries(vo.getMainDeck(), vo.getRushDeck());
        deck.setIsValid(result.getValid());
        deckMapper.update(deck);
        return result;
    }

    @Override
    public DeckValidateResultVO validateEntries(
            List<DeckCardEntry> mainDeck, List<DeckCardEntry> rushDeck) {
        DeckValidateResultVO result = new DeckValidateResultVO();
        List<String> errors = new ArrayList<>();
        mainDeck = normalizeEntries(mainDeck);
        rushDeck = normalizeEntries(rushDeck);

        Set<String> allCodes = new HashSet<>();
        mainDeck.forEach(e -> allCodes.add(e.getCardCode()));
        rushDeck.forEach(e -> allCodes.add(e.getCardCode()));

        if (allCodes.isEmpty()) {
            result.setValid(false);
            result.setErrors(List.of("主卡组和冲击卡组均不能为空"));
            return result;
        }

        List<CardDO> cards =
                cardMapper.selectListByQuery(QueryWrapper.create().in("card_code", allCodes));
        Map<String, CardDO> code2Card =
                cards.stream().collect(Collectors.toMap(CardDO::getCardCode, c -> c));

        List<String> missing = allCodes.stream().filter(c -> !code2Card.containsKey(c)).toList();
        if (!missing.isEmpty()) {
            errors.add("卡牌编号不存在: " + String.join(", ", missing));
        }

        int mainCount = totalQty(mainDeck);
        result.setMainDeckCount(mainCount);
        if (mainCount != 50) {
            errors.add("主卡组必须为 50 张，当前 " + mainCount + " 张");
        }
        List<CardDO> mainCards = expandByQty(mainDeck, code2Card);
        if (mainCards.stream()
                .anyMatch(c -> !EnumCardType.CHARACTER.getCode().equals(c.getCardType()))) {
            errors.add("主卡组只能包含角色卡");
        }
        Set<String> colors =
                mainCards.stream()
                        .map(CardDO::getColor)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
        result.setColors(new ArrayList<>(colors));
        if (colors.size() > 2) {
            errors.add("主卡组颜色最多 2 色，当前 " + colors.size() + " 色");
        }
        Map<String, Long> nameCount =
                mainCards.stream()
                        .collect(Collectors.groupingBy(CardDO::getCardName, Collectors.counting()));
        result.setNameCount(
                nameCount.entrySet().stream()
                        .collect(
                                Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue())));
        List<String> overName =
                nameCount.entrySet().stream()
                        .filter(e -> e.getValue() > 3)
                        .map(Map.Entry::getKey)
                        .toList();
        if (!overName.isEmpty()) {
            errors.add("同名卡牌超过 3 张: " + String.join(", ", overName));
        }

        int rushCount = totalQty(rushDeck);
        result.setRushDeckCount(rushCount);
        if (rushCount != 9) {
            errors.add("冲击卡组必须为 9 张，当前 " + rushCount + " 张");
        }
        List<CardDO> rushCards = expandByQty(rushDeck, code2Card);
        if (rushCards.stream()
                .anyMatch(c -> !EnumCardType.RUSH_POINT.getCode().equals(c.getCardType()))) {
            errors.add("冲击卡组只能包含冲击卡");
        }

        result.setValid(errors.isEmpty());
        result.setErrors(errors);
        return result;
    }

    // ========== 私有方法 ==========

    private void attachCoverImages(List<DeckVO> vos) {
        Map<DeckVO, String> vo2Code = new LinkedHashMap<>();
        for (DeckVO vo : vos) {
            String code =
                    resolveCoverCardCode(vo.getCoverCardCode(), vo.getMainDeck(), vo.getRushDeck());
            vo.setCoverCardCode(code);
            if (StringUtils.isNotBlank(code)) {
                vo2Code.put(vo, code);
            }
        }
        if (vo2Code.isEmpty()) {
            return;
        }
        List<CardDO> cards =
                cardMapper.selectListByQuery(
                        QueryWrapper.create().in("card_code", new HashSet<>(vo2Code.values())));
        Map<String, String> paths =
                cards.stream()
                        .filter(c -> StringUtils.isNotBlank(c.getImagePath()))
                        .collect(
                                Collectors.toMap(
                                        CardDO::getCardCode, CardDO::getImagePath, (a, b) -> a));
        vo2Code.forEach((vo, code) -> vo.setCoverImagePath(paths.get(code)));
    }

    /** 封面须在卡组内；未指定或已不在卡组则回退主卡组第一张，再回退冲击第一张。 */
    private String resolveCoverCardCode(
            String requested, List<DeckCardEntry> mainDeck, List<DeckCardEntry> rushDeck) {
        if (containsCard(mainDeck, requested) || containsCard(rushDeck, requested)) {
            return requested.trim();
        }
        return firstCardCode(mainDeck, rushDeck);
    }

    private boolean containsCard(List<DeckCardEntry> entries, String cardCode) {
        if (entries == null || StringUtils.isBlank(cardCode)) {
            return false;
        }
        String code = cardCode.trim();
        return entries.stream().anyMatch(e -> code.equals(e.getCardCode()));
    }

    /** 列表封面回退：主卡组第一张，否则冲击第一张 */
    private String firstCardCode(List<DeckCardEntry> mainDeck, List<DeckCardEntry> rushDeck) {
        if (mainDeck != null
                && !mainDeck.isEmpty()
                && StringUtils.isNotBlank(mainDeck.get(0).getCardCode())) {
            return mainDeck.get(0).getCardCode();
        }
        if (rushDeck != null
                && !rushDeck.isEmpty()
                && StringUtils.isNotBlank(rushDeck.get(0).getCardCode())) {
            return rushDeck.get(0).getCardCode();
        }
        return null;
    }

    private DeckDO checkOwnership(Long userId, Long deckId) {
        DeckDO deck = deckMapper.selectOneById(deckId);
        if (deck == null) {
            throw new BusinessException(ErrorCode.DECK_NOT_FOUND);
        }
        if (!Objects.equals(deck.getUserId(), userId)) {
            throw new BusinessException(ErrorCode.DECK_FORBIDDEN);
        }
        return deck;
    }

    /** 同编号合并为一条：保留首次出现顺序，quantity 累加；quantity<=0 丢弃 */
    private List<DeckCardEntry> normalizeEntries(List<DeckCardEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return List.of();
        }
        LinkedHashMap<String, Integer> merged = new LinkedHashMap<>();
        for (DeckCardEntry e : entries) {
            if (e == null || StringUtils.isBlank(e.getCardCode())) {
                continue;
            }
            int qty = e.getQuantity() == null ? 0 : e.getQuantity();
            if (qty <= 0) {
                continue;
            }
            merged.merge(e.getCardCode().trim(), qty, Integer::sum);
        }
        List<DeckCardEntry> result = new ArrayList<>();
        merged.forEach(
                (code, qty) -> {
                    DeckCardEntry e = new DeckCardEntry();
                    e.setCardCode(code);
                    e.setQuantity(qty);
                    result.add(e);
                });
        return result;
    }

    private int totalQty(List<DeckCardEntry> entries) {
        return entries.stream().mapToInt(DeckCardEntry::getQuantity).sum();
    }

    private List<CardDO> expandByQty(List<DeckCardEntry> entries, Map<String, CardDO> code2Card) {
        List<CardDO> expanded = new ArrayList<>();
        for (DeckCardEntry e : entries) {
            CardDO card = code2Card.get(e.getCardCode());
            if (card == null) {
                continue;
            }
            for (int i = 0; i < e.getQuantity(); i++) {
                expanded.add(card);
            }
        }
        return expanded;
    }

    private int nextSortOrder(Long userId) {
        QueryWrapper qw =
                QueryWrapper.create().select("MAX(sort_order) AS sort_order").eq("user_id", userId);
        DeckDO max = deckMapper.selectOneByQuery(qw);
        if (max == null || max.getSortOrder() == null) {
            return 0;
        }
        return max.getSortOrder() + 1;
    }

    private String normalizeTags(String tags) {
        if (tags == null) {
            return null;
        }
        if (tags.isBlank()) {
            return "";
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }
}
