package com.hackanet.json.mappers;

import com.google.common.collect.Lists;
import com.hackanet.models.AbstractEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<F extends AbstractEntity, T> {
    T map(F from);
    default List<T> map(Collection<F> fromList) {
        if (fromList == null) {
            return Lists.newArrayList();
        }
        return fromList.stream().map(this::map).collect(Collectors.toList());
    }
}
