package com.hackanet.models.chat;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/28/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Document(indexName = "hackanet", type = "message")
public class Message {

    @Id
    private String id;
    private Long senderId;
    private Long chatId;
    private Long datetime;
    private String text;
    private List<Long> attachments;
    private String replyTo;
}
