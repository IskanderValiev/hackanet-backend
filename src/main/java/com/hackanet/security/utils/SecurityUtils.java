package com.hackanet.security.utils;

import com.hackanet.exceptions.BlackListException;
import com.hackanet.exceptions.ForbiddenException;
import com.hackanet.models.*;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.enums.CompanyType;
import com.hackanet.security.enums.Role;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
public class SecurityUtils {

    public static void checkUserProfileForViewing(User profileOwner, User user) {
        if (profileOwner.getBlockedUsers().contains(user))
            throw new BlackListException("You are in the black list");
    }

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
        if (!chat.getAdmins().contains(user))
            throw new ForbiddenException("You have no access to this chat");
    }

    public static void checkProfileAccess(User currentUser, User changeableUser) {
        if (!currentUser.equals(changeableUser))
            throw new ForbiddenException("You have no access to this profile");
    }

    public static boolean isSuperAdmin(User user) {
        return Role.SUPER_ADMIN.equals(user.getRole());
    }

    public static void checkTeamAccess(Team team, User user) {
        if (!team.getParticipants().contains(user))
            throw new ForbiddenException("You have no access to this team");
    }

    public static void checkTeamAccessAsTeamLeader(Team team, User user) {
        if (!team.getTeamLeader().equals(user))
            throw new ForbiddenException("You have no access to this team as a team leader");
    }

    public static void checkJoinToTeamRequestAccess(User user, JoinToTeamRequest request) {
        if (!request.getUser().getId().equals(user.getId()))
            throw new ForbiddenException("You have no access to this join to team request");
    }

    public static void checkPostLikeAccess(PostLike postLike, User user) {
        if (!postLike.getUser().equals(user))
            throw new ForbiddenException("You have no access to this post like");
    }

    public static void checkCompanyAccess(Company company, User user) {
        if (!company.getAdmin().equals(user) || CompanyType.ADDED_BY_NAME.equals(company.getType()))
            throw new ForbiddenException("You have no access to this company");
    }

    public static void checkPortfolioAccess(Portfolio portfolio, User user) {
        if (!portfolio.getUser().equals(user))
            throw new ForbiddenException("You have no access to this portfolio");
    }

    public static void checkUserReviewAccess(UserReview userReview, User user) {
        if (!userReview.getUser().equals(user))
            throw new ForbiddenException("You have no access to this user review");
    }

    public static void checkTeamInvitationAccess(TeamInvitation invitation, User user, boolean asTeamLeaderToo) {
        if (asTeamLeaderToo) {
            if (!invitation.getUser().equals(user) && !invitation.getTeam().getTeamLeader().equals(user))
                throw new ForbiddenException("You have no access to this team invitation");
        } else {
            if (!invitation.getUser().equals(user))
                throw new ForbiddenException("You have no access to this team invitation");
        }
    }

    public static void checkJobInvitationAccess(JobOffer invitation, User user, boolean asCompany) {
        if (asCompany) {
            if (!invitation.getCompany().getAdmin().equals(user)) {
                throw new ForbiddenException("You have no access to this invitation as a company");
            }
        } else {
            if (!invitation.getUser().equals(user))
                throw new ForbiddenException("You have no access to this invitation as a user");
        }
    }

    public static void checkConnectionInvitationAccess(ConnectionInvitation invitation, User user, boolean asOwner) {
        if (asOwner) {
            if (!invitation.getUser().equals(user))
                throw new ForbiddenException("You have no access to this connection invitation as a owner");
        } else {
            if (!invitation.getInvitedUser().equals(user))
                throw new ForbiddenException("You have no access to this connection invitation as an invited user");
        }
    }
}
