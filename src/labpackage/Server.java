package labpackage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 250;
    private static final String QUESTION_FILE = "bazaPytan.txt";
    private static final String ANSWER_FILE = "bazaOdpowiedzi.txt";
    private static final String RESULT_FILE = "wyniki.txt";

    private static List<Question> questions;

    public static void main(String[] args) {
        loadQuestions();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedWriter answerWriter = new BufferedWriter(new FileWriter(ANSWER_FILE, true));
                BufferedWriter resultWriter = new BufferedWriter(new FileWriter(RESULT_FILE, true));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            int score = 0;

            for (Question question : questions) {
                out.println(question.getQuestion());
                out.println("A. " + question.getOptionA());
                out.println("B. " + question.getOptionB());
                out.println("C. " + question.getOptionC());

                long startTime = System.currentTimeMillis();
                String studentAnswer;
                while ((studentAnswer = in.readLine()) == null) {
                    if (System.currentTimeMillis() - startTime > 30000) { // 30 seconds timeout
                        break;
                    }
                }

                if (studentAnswer != null && studentAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                    score++;
                }
                answerWriter.write(studentAnswer + System.lineSeparator());
            }

            resultWriter.write("Score: " + score + System.lineSeparator());
            resultWriter.flush();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadQuestions() {
        questions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(QUESTION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Question question = new Question(parts[0], parts[1], parts[2], parts[3], parts[4]);
                questions.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
