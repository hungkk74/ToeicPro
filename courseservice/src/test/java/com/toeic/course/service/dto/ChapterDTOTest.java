package com.toeic.course.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChapterDTO.class);
        ChapterDTO chapterDTO1 = new ChapterDTO();
        chapterDTO1.setId(1L);
        ChapterDTO chapterDTO2 = new ChapterDTO();
        assertThat(chapterDTO1).isNotEqualTo(chapterDTO2);
        chapterDTO2.setId(chapterDTO1.getId());
        assertThat(chapterDTO1).isEqualTo(chapterDTO2);
        chapterDTO2.setId(2L);
        assertThat(chapterDTO1).isNotEqualTo(chapterDTO2);
        chapterDTO1.setId(null);
        assertThat(chapterDTO1).isNotEqualTo(chapterDTO2);
    }
}
