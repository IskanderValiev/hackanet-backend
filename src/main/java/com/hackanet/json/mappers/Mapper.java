package com.hackanet.json.mappers;

import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<F, T> {
    T map(F from);
    default List<T> map(List<F> fromList) {
        return fromList.stream().map(this::map).collect(Collectors.toList());
    }
}
