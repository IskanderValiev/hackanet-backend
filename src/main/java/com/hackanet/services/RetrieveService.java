package com.hackanet.services;

import com.hackanet.models.AbstractEntity;

import java.util.Collections;
import java.util.List;

public interface RetrieveService<T extends AbstractEntity> {
   T get(Long id);
   default List<T> getAll() {
       return Collections.emptyList();
   }
}
