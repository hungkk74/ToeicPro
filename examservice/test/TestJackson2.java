package test;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestJackson2 {
    public static class Exam {
        public String code; public String title; public String category; public Integer durationMinutes; public Integer totalQuestions; public String audioFullUrl; public Boolean isPublished; public List<Part> parts;
    }
    public static class Part {
        public Integer partNumber; public String name; public List<Group> questionGroups;
    }
    public static class Group {
        public String passageText; public String imageUrl; public String audioUrl; public List<Question> questions;
    }
    public static class Question {
        public Integer questionNumber; public String content; public String optionA; public String optionB; public String optionC; public String optionD; public String correctOption; public String explanation;
    }
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = new String(Files.readAllBytes(Paths.get("..\\\\ets2023_test3_formatted.json")));
        try {
            Exam dto = mapper.readValue(json, Exam.class);
            System.out.println("Success! Code: " + dto.code);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
