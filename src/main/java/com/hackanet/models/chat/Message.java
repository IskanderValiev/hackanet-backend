package com.hackanet.models.chat;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

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
@Document(indexName = "hackanet", type = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long senderId;
    private Long chatId;
    private Timestamp timestamp;
    private String text;
}
