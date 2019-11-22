package com.hackanet.json.mappers;

import com.hackanet.models.AbstractEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<F extends AbstractEntity, T> {
    T map(F from);
    default List<T> map(Collection<F> fromList) {
        return fromList.stream().map(this::map).collect(Collectors.toList());
    }
}
