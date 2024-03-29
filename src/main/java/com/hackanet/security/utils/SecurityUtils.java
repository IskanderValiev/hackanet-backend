package com.hackanet.security.utils;

import com.hackanet.exceptions.BadRequestException;
import com.hackanet.exceptions.BlackListException;
import com.hackanet.exceptions.ForbiddenException;
import com.hackanet.models.Company;
import com.hackanet.models.ConnectionInvitation;
import com.hackanet.models.FileInfo;
import com.hackanet.models.JobOffer;
import com.hackanet.models.chat.Chat;
import com.hackanet.models.comment.Comment;
import com.hackanet.models.comment.CommentLike;
import com.hackanet.models.enums.CompanyType;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.hackathon.Track;
import com.hackanet.models.post.Post;
import com.hackanet.models.post.PostLike;
import com.hackanet.models.team.JoinToTeamRequest;
import com.hackanet.models.team.Team;
import com.hackanet.models.team.TeamInvitation;
import com.hackanet.models.team.TeamMember;
import com.hackanet.models.user.Portfolio;
import com.hackanet.models.user.User;
import com.hackanet.models.user.UserReview;
import com.hackanet.security.enums.Role;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/20/19
 */
public class SecurityUtils {

    public static void isAdmin(User user) {
        if (Role.ADMIN.equals(user.getRole())) {
            throw new ForbiddenException("The user is not admin");
        }
    }

    public static void confirmed(User user) {
        if (!user.getEmailConfirmed()) {
            throw new ForbiddenException("The user is not confirmed");
        }
    }

    public static void containedInBlackList(User user, User userToCheck) {
        if (user.getBlockedUsers().contains(userToCheck)) {
            throw new BlackListException("The user with email = " + userToCheck.getEmail() + " is in black list");
        }
    }

    public static void checkUserProfileForViewing(User profileOwner, User user) {
        if (profileOwner.getBlockedUsers().contains(user)) {
            throw new BlackListException("You are in the black list");
        }
    }

    public static void checkFileAccess(FileInfo fileInfo, User user) {
        if (!user.equals(fileInfo.getUser()) && !Role.SUPER_ADMIN.equals(user.getRole())) {
            throw new ForbiddenException("You have no access to this file");
        }
    }

    public static void checkHackathonAccess(Hackathon hackathon, User user) {
        if (!user.getId().equals(hackathon.getOwner().getId()) && !isSuperAdmin(user)) {
            throw new ForbiddenException("You have no access to this hackathon");
        }
    }

    public static void checkTrackAccess(Track track, User user) {
        checkHackathonAccess(track.getHackathon(), user);
        if (!isSuperAdmin(user)) {
            throw new ForbiddenException("You have no access to this track");
        }
    }

    public static void checkPostAccess(Post post, User user) {
        if (!user.getId().equals(post.getOwner().getId()) && !isSuperAdmin(user)) {
            throw new ForbiddenException("You have no access to this post");
        }
    }

    public static void checkChatAccess(Chat chat, User user) {
        if (!chat.getParticipants().contains(user)) {
            throw new ForbiddenException("You have no access to this chat");
        }
    }

    public static void checkChatAccessForOperation(Chat chat, User user) {
        if (!chat.getAdmins().contains(user)) {
            throw new ForbiddenException("You have no access to this chat");
        }
    }

    public static void checkProfileAccess(User currentUser, User changeableUser) {
        if (!currentUser.equals(changeableUser)) {
            throw new ForbiddenException("You have no access to this profile");
        }
    }

    public static boolean isSuperAdmin(User user) {
        return Role.SUPER_ADMIN.equals(user.getRole());
    }

    public static void checkTeamAccess(Team team, User user) {
        final boolean contains = team.getMembers().stream().anyMatch(tm -> tm.getUser().equals(user));
        if (!contains) {
            throw new ForbiddenException("You have no access to this team");
        }
    }

    public static void checkTeamAccessAsTeamLeader(Team team, User user) {
        if (!team.getTeamLeader().equals(user)) {
            throw new ForbiddenException("You have no access to this team as a team leader");
        }
    }

    public static void checkJoinToTeamRequestAccess(User user, JoinToTeamRequest request) {
        if (!request.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You have no access to this join to team request");
        }
    }

    public static void checkPostLikeAccess(PostLike postLike, User user) {
        if (!postLike.getUser().equals(user)) {
            throw new ForbiddenException("You have no access to this post like");
        }
    }

    public static void checkCompanyAccess(Company company, User user) {
        if (!company.getAdmin().equals(user) || CompanyType.ADDED_BY_NAME.equals(company.getType())) {
            throw new ForbiddenException("You have no access to this company");
        }
    }

    public static void checkPortfolioAccess(Portfolio portfolio, User user) {
        if (!portfolio.getUser().equals(user)) {
            throw new ForbiddenException("You have no access to this portfolio");
        }
    }

    public static void checkUserReviewAccess(UserReview userReview, User user) {
        if (!userReview.getUser().equals(user)) {
            throw new ForbiddenException("You have no access to this user review");
        }
    }

    public static void checkTeamInvitationAccess(TeamInvitation invitation, User user, boolean asTeamLeaderToo) {
        if (asTeamLeaderToo) {
            if (!invitation.getUser().equals(user) && !invitation.getTeam().getTeamLeader().equals(user)) {
                throw new ForbiddenException("You have no access to this team invitation");
            }
        } else {
            if (!invitation.getUser().equals(user)) {
                throw new ForbiddenException("You have no access to this team invitation");
            }
        }
    }

    public static void checkJobInvitationAccess(JobOffer invitation, User user, boolean asCompany) {
        if (asCompany) {
            if (!invitation.getCompany().getAdmin().equals(user)) {
                throw new ForbiddenException("You have no access to this invitation as a company");
            }
        } else {
            if (!invitation.getUser().equals(user)) {
                throw new ForbiddenException("You have no access to this invitation as a user");
            }
        }
    }

    public static void checkConnectionInvitationAccess(ConnectionInvitation invitation, User user, boolean asOwner) {
        if (asOwner) {
            if (!invitation.getUser().equals(user)) {
                throw new ForbiddenException("You have no access to this connection invitation as a owner");
            }
        } else {
            if (!invitation.getInvitedUser().equals(user)) {
                throw new ForbiddenException("You have no access to this connection invitation as an invited user");
            }
        }
    }

    public static void checkCommentAccess(Comment comment, User user) {
        confirmed(user);
        if (!comment.getUser().equals(user)) {
            throw new ForbiddenException("You have no access to this comment");
        }
    }

    public static void checkCommentDeletingAccess(Comment comment, User user) {
        confirmed(user);
        checkCommentAccess(comment, user);
        if (!isSuperAdmin(user)) {
            throw new ForbiddenException("You have no access to this comment");
        }
    }

    public static void checkCommentLikeAccess(CommentLike like, User user) {
        if (!like.getUser().equals(user)) {
            throw new ForbiddenException("You have no access to this like");
        }
    }

    public static void checkTeamMemberAccess(TeamMember teamMember, User user) {
        if (teamMember == null) {
            throw new BadRequestException("Team member is null");
        }
        if (!teamMember.getUser().equals(user)) {
            throw new ForbiddenException("You have no access to this member entity");
        }
    }
}
