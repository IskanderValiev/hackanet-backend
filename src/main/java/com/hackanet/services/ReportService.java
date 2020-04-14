package com.hackanet.services;

import com.hackanet.json.forms.ReportCreateFrom;
import com.hackanet.models.Report;
import com.hackanet.models.user.User;
import com.hackanet.models.enums.ReportStatus;

import java.util.List;

/**
 * @author Iskander Valiev
 * created by isko
 * on 3/10/20
 */
public interface ReportService extends RetrieveService<Report> {
    Report report(User user, ReportCreateFrom form);
    Report updateStatus(Long report, ReportStatus status);
    List<Report> getUnresolvedReports();
}
