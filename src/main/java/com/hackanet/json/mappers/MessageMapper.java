package com.hackanet.json.mappers;

import com.hackanet.json.dto.FileInfoDto;
import com.hackanet.json.dto.MessageDto;
import com.hackanet.models.FileInfo;
import com.hackanet.models.User;
import com.hackanet.models.chat.Message;
import com.hackanet.services.FileInfoService;
import com.hackanet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                .timestamp(from.getTimestamp())
                .attachments(files)
                .chatId(from.getChatId())
                .sender(userSimpleMapper.map(sender)).build();
    }

    public List<MessageDto> map(List<Message> from) {
        return from.stream().map(this::map).collect(Collectors.toList());
    }
}
