package com.hackanet.services;

import com.hackanet.models.BlockedUser;
import com.hackanet.models.hackathon.Hackathon;
import com.hackanet.models.skill.Skill;
import com.hackanet.models.team.Team;
import com.hackanet.models.user.User;
import com.hackanet.security.utils.SecurityUtils;
import com.hackanet.services.skill.SkillCombinationService;
import com.hackanet.services.team.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 4/25/20
 */
@Service
public class TeamUserHelperServiceImpl implements TeamUserHelperService {

    @Autowired
    private TeamService teamService;

    @Autowired
    private SkillCombinationService skillCombinationService;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<User> getMembersSuggestions(User user, Long teamId, Long hackathonId) {
        final Team team = teamService.get(teamId);
        SecurityUtils.checkTeamAccess(team, user);
        final List<Skill> skills = skillCombinationService.mostRelevantSkills(team);
        final List<Long> skillsIds = skills.stream().mapToLong(Skill::getId).boxed().collect(Collectors.toList());
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> query = getUsersQuery(criteriaBuilder, skillsIds, hackathonId);
        final TypedQuery<User> resultQuery = entityManager.createQuery(query);
        return resultQuery.getResultList();
    }

    private CriteriaQuery<User> getUsersQuery(CriteriaBuilder criteriaBuilder, List<Long> skillsIds, Long hackathonId) {
        final CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        final Root<User> root = query.from(User.class);
        List<Predicate> predicates = new ArrayList<>();
        query.select(root);
        predicates.add(criteriaBuilder.isTrue(root.get("emailConfirmed")));
        Subquery<BlockedUser> subquery = query.subquery(BlockedUser.class);
        final Root<BlockedUser> subRoot = subquery.from(BlockedUser.class);
        subquery.select(subRoot).where(criteriaBuilder.equal(subRoot.get("user"), root.get("id")));
        predicates.add(criteriaBuilder.exists(subquery));
        Join<User, Skill> userSkillJoin = root.join("skills", JoinType.INNER);
        userSkillJoin.on(userSkillJoin.get("id").in(skillsIds));
        predicates.add(userSkillJoin.getOn());
        if (hackathonId != null) {
            Join<User, Hackathon> userHackathonJoin = root.join("attendedHackathons");
            userHackathonJoin.on(criteriaBuilder.equal(userHackathonJoin.get("id"), hackathonId));
            predicates.add(userHackathonJoin.getOn());
        }
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        return query;
    }
}
