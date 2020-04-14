package com.hackanet.services;

import com.hackanet.exceptions.NotFoundException;
import com.hackanet.json.forms.ReportCreateFrom;
import com.hackanet.models.Report;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.ReportStatus;
import com.hackanet.repositories.ReportRepository;
import com.hackanet.services.comment.CommentService;
import com.hackanet.services.hackathon.HackathonService;
import com.hackanet.services.post.PostService;
import com.hackanet.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/10/20
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private HackathonService hackathonService;

    @Override
    public Report get(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Report with id = " + id + " not found"));
    }

    @Override
    public Report report(User user, ReportCreateFrom form) {
        checkEntity(form);
        Report report = Report.builder()
                .type(form.getType())
                .entityId(form.getEntityId())
                .date(DateTimeUtil.epochToLocalDateTime(System.currentTimeMillis()))
                .status(ReportStatus.NEW)
                .user(user)
                .build();
        return reportRepository.save(report);
    }

    @Override
    public Report updateStatus(Long reportId, ReportStatus status) {
        Report report = get(reportId);
        report.setStatus(status);
        return reportRepository.save(report);
    }

    @Override
    public List<Report> getUnresolvedReports() {
        return reportRepository.findByStatus(ReportStatus.NEW);
    }

    private void checkEntity(ReportCreateFrom form) {
        switch (form.getType()) {
            case COMMENT:
                commentService.get(form.getEntityId());
                break;
            case POST:
                postService.get(form.getEntityId());
                break;
            case HACKATHON:
                hackathonService.get(form.getEntityId());
                break;
            case MESSAGE:
                break;
        }
    }
}
