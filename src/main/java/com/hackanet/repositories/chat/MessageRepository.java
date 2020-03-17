package com.hackanet.repositories.chat;

import com.hackanet.models.chat.Message;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MessageRepository extends ElasticsearchRepository<Message, String> {
    List<Message> findAllByChatIdOrderByTimestamp(Long chatId);
    List<Message> findAllByReplyTo(String id);
}
