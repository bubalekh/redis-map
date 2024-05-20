package com.example.redismap;

import com.example.redismap.model.RedisKeyValuePair;
import com.example.redismap.repo.RedisStringObjectRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class RedisMap implements Map<String, Object> {

    private final RedisStringObjectRepository repository;

    @Override
    public int size() {
        return (int) repository.count();
    }

    @Override
    public boolean isEmpty() {
        return repository.count() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String stringKey)
            return repository.findByKey(stringKey).isPresent();
        throw new TypeMismatchException(key, String.class);
    }

    @Override
    public boolean containsValue(Object value) {
        return !repository.findAllByValue(value).isEmpty();
    }

    @Override
    public Object get(Object key) {
        if (key instanceof String stringKey)
            return repository.findByKey(stringKey)
                .orElseThrow();
        throw new TypeMismatchException(key, String.class);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public Object put(String key, Object value) {
        RedisKeyValuePair entity = RedisKeyValuePair.builder()
                .key(key)
                .value(value)
                .build();
        repository.save(entity);
        return entity.getValue();
    }

    @Override
    public Object remove(Object key) {
        if (key instanceof String stringKey) {
            RedisKeyValuePair redisKeyValuePair = repository.findByKey(stringKey)
                    .orElseThrow();
            repository.delete(redisKeyValuePair);
            return redisKeyValuePair.getKey();
        }
        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
        List<RedisKeyValuePair> redisKeyValuePairs = m.entrySet().stream()
                .map(entry -> RedisKeyValuePair.builder()
                        .key(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .toList();
        repository.saveAll(redisKeyValuePairs);
    }

    @Override
    public void clear() {
        repository.deleteAll();
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public Set<String> keySet() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(RedisKeyValuePair::getKey)
                .collect(Collectors.toSet());
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public Collection<Object> values() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(RedisKeyValuePair::getValue)
                .toList();
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public Set<Entry<java.lang.String, Object>> entrySet() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(redisKeyValuePair -> Map.entry(redisKeyValuePair.getKey(), redisKeyValuePair.getValue()))
                .collect(Collectors.toSet());
    }
}
