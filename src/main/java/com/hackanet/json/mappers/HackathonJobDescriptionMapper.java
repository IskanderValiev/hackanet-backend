package com.hackanet.json.mappers;

import com.hackanet.json.dto.HackathonJobDescriptionDto;
import com.hackanet.models.hackathon.HackathonJobDescription;
import com.hackanet.models.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HackathonJobDescriptionMapper implements Mapper<HackathonJobDescription, HackathonJobDescriptionDto> {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private HackathonSimpleMapper hackathonMapper;

    @Override
    public HackathonJobDescriptionDto map(HackathonJobDescription from) {
        if (from == null) {
            return null;
        }
        Team team = from.getTeam();
        HackathonJobDescriptionDto dto = HackathonJobDescriptionDto.builder()
                .id(from.getId())
                .description(from.getDescription())
                .hackathon(hackathonMapper.map(from.getHackathon()))
                .build();
        if (team != null) {
            dto.setTeam(teamMapper.map(team));
        }
        return dto;
    }
}
