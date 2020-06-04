package com.hackanet.repositories.chat;

import com.hackanet.models.chat.Message;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MessageRepository extends ElasticsearchRepository<Message, String> {
    List<Message> findAllByChatIdOrderByDatetime(Long chatId);
    List<Message> findAllByChatId(Long chatId);
    List<Message> findAllByReplyTo(String id);
    Message findByChatIdOrderByDatetimeDesc();
}
