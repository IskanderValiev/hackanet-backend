package com.hackanet.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Iskander Valiev
 * created by isko
 * on 11/22/19
 */
@Getter
@Setter
@AllArgsConstructor
public class ReviewStatistic {
    private Double average;
    private Long count;

    public Double getAverage() {
        return average == null ? 0.0 : average;
    }

    public Long getCount() {
        return count == null ? 0 : count;
    }
}
