package labpackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class QuestionDatabase {

    private List<Question> questions;

    public QuestionDatabase() {
        questions = Collections.synchronizedList(new LinkedList<>());
        fetchQuestionsFromFile();
    }

    private void fetchQuestionsFromFile() {
        String fileName = "bazaPytan.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String questionText = new String(line);
                Question question = new Question(questionText, br.readLine(), br.readLine(), br.readLine(), br.readLine(), br.readLine());
                questions.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
