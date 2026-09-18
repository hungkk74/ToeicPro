package com.toeic.exam.service.dto.review;

import java.io.Serializable;

public record PartScoreSummaryDTO(
    Integer partNumber,
    String partName,
    Integer correctCount,
    Integer totalCount
) implements Serializable {}
