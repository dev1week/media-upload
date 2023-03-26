package com.exmaple.upload.Repository;

import com.exmaple.upload.Domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class ItemRepository {
    private final Map<Long, Item> store = new HashMap<>();
    private long sequence = 0L;
    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        log.info("저장한값={}",String.valueOf(store.get(sequence)));
        return item;
    }
    public Item findById(Long id) {
        log.info("찾아온값={}",String.valueOf(store.get(id)));
        return store.get(id);
    }
}
