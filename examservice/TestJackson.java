import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.toeic.exam.service.dto.create.FullExamCreateDTO;

public class TestJackson {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = new String(Files.readAllBytes(Paths.get("..\\\\ets2023_test3_formatted.json")));
        try {
            FullExamCreateDTO dto = mapper.readValue(json, FullExamCreateDTO.class);
            System.out.println("Success! Code: " + dto.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
