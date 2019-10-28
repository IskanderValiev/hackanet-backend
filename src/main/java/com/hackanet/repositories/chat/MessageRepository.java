package com.hackanet.repositories.chat;

import com.hackanet.models.chat.Message;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MessageRepository extends ElasticsearchRepository<Message, Long> {
}
