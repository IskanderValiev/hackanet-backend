package com.hackanet.models.log;

import com.hackanet.json.dto.activity.log.ActivityUser;
import com.hackanet.json.dto.activity.log.RequestDto;
import com.hackanet.json.dto.activity.log.UserLocationInfo;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/18/20
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Document(indexName = "hackanet", type = "activity_log")
public class ActivityLog {

    @Id
    private String id;

    private ActivityUser user;

    private Timestamp date;

    private RequestDto request;

    private UserLocationInfo userLocationInfo;
}