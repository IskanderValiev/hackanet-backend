package com.hackanet.security.utils;

import com.hackanet.exceptions.ForbiddenException;
import com.hackanet.models.FileInfo;
import com.hackanet.models.Hackathon;
import com.hackanet.models.Post;
import com.hackanet.models.User;
import com.hackanet.models.chat.Chat;
import com.hackanet.security.role.Role;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
public class SecurityUtils {

    public static void checkFileAccess(FileInfo fileInfo, User user) {
        if (!user.equals(fileInfo.getUser()) && !Role.SUPER_ADMIN.equals(user.getRole()))
            throw new ForbiddenException("You have no access to this file");
    }

    public static void checkHackathonAccess(Hackathon hackathon, User user) {
        if (!user.getId().equals(hackathon.getOwner().getId()) && !isSuperAdmin(user))
            throw new ForbiddenException("You have no access to this hackathon");
    }

    public static void checkPostAccess(Post post, User user) {
        if (!user.getId().equals(post.getOwner().getId()) && !isSuperAdmin(user))
            throw new ForbiddenException("You have no access to this post");
    }

    public static void checkChatAccess(Chat chat, User user) {
        if (!chat.getParticipants().contains(user))
            throw new ForbiddenException("You have no access to this chat");
    }

    public static void checkChatAccessForOperation(Chat chat, User user) {
        if (!chat.getAdmin().getId().equals(user.getId()))
            throw new ForbiddenException("You have no access to this chat");
    }

    public static void checkProfileAccess(User currentUser, User changeableUser) {
        if (!currentUser.equals(changeableUser))
            throw new ForbiddenException("You have no access to this profile");
    }

    public static boolean isSuperAdmin(User user) {
        return Role.SUPER_ADMIN.equals(user.getRole());
    }

}
