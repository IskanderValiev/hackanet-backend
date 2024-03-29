package com.hackanet.json.mappers;

import com.hackanet.json.dto.FileInfoDto;
import com.hackanet.json.dto.MessageDto;
import com.hackanet.models.FileInfo;
import com.hackanet.models.user.User;
import com.hackanet.models.chat.Message;
import com.hackanet.services.FileInfoService;
import com.hackanet.services.user.UserService;
import com.hackanet.services.chat.ChatMessageServiceElasticsearchImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hackanet.utils.DateTimeUtil.*;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/29/19
 */
@Component
public class MessageMapper {

    @Autowired
    private UserSimpleMapper userSimpleMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private ChatMessageServiceElasticsearchImpl chatMessageService;

    public MessageDto map(Message from) {
        User sender = userService.get(from.getSenderId());
        List<FileInfoDto> files = new ArrayList<>();
        if (from.getAttachments() != null) {
            List<FileInfo> filesInfo = fileInfoService.getByIdsIn(from.getAttachments());
            files.addAll(fileInfoMapper.map(filesInfo));
        }
        return MessageDto.builder()
                .id(from.getId())
                .text(from.getText())
                .date(from.getDatetime())
                .attachments(files)
                .chatId(from.getChatId())
                .replies(map(chatMessageService.getReplies(from.getId())))
                .sender(userSimpleMapper.map(sender)).build();
    }

    public List<MessageDto> map(List<Message> from) {
        return from.stream().map(this::map).collect(Collectors.toList());
    }
}
