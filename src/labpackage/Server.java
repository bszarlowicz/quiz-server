package labpackage;
//
//import java.io.*;
//import java.net.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class Server {
//    private static final int PORT = 12345;
//    private static final int MAX_CLIENTS = 250;
//    private static final String QUESTION_FILE = "bazaPytan.txt";
//    private static final String ANSWER_FILE = "bazaOdpowiedzi.txt";
//    private static final String RESULT_FILE = "wyniki.txt";
//
//    private static List<Question> questions;
//
//    public static void main(String[] args) {
//        loadQuestions();
//
//        try (DatagramSocket socket = new DatagramSocket(PORT)) {
//            ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);
//
//            while (true) {
//                byte[] buffer = new byte[1024];
//                DatagramPacket clientPacket = new DatagramPacket(buffer, buffer.length);
//                socket.receive(clientPacket);
//
//                InetAddress clientAddress = clientPacket.getAddress();
//                int clientPort = clientPacket.getPort();
//                String name = new String(clientPacket.getData(), 0, clientPacket.getLength());
//
//                executorService.submit(() -> handleClient(socket, clientAddress, clientPort, name));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void handleClient(DatagramSocket socket, InetAddress clientAddress, int clientPort, String name) {
//        try (
//                BufferedWriter answerWriter = new BufferedWriter(new FileWriter(ANSWER_FILE, true));
//                BufferedWriter resultWriter = new BufferedWriter(new FileWriter(RESULT_FILE, true))
//        ) {
//            int score = 0;
//
//            for (Question question : questions) {
//                String questionMessage = question.getQuestion() + "," + question.getOptionA() + "," +
//                        question.getOptionB() + "," + question.getOptionC() + "," + question.getOptionD() + "," +
//                        question.getCorrectAnswer();
//                sendPacket(socket, clientAddress, clientPort, questionMessage);
//
//                DatagramPacket answerPacket = receivePacket(socket);
//                if (answerPacket == null) {
//                    break;
//                }
//
//                String studentAnswer = new String(answerPacket.getData(), 0, answerPacket.getLength());
//
//                if (studentAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
//                    score++;
//                }
//
//                answerWriter.write(" " + studentAnswer);
//            }
//            answerWriter.write(System.lineSeparator());
//
//            String resultMessage = name + " score: " + score;
//            sendPacket(socket, clientAddress, clientPort, resultMessage);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void loadQuestions() {
//        questions = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(QUESTION_FILE))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split(",");
//                Question question = new Question(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
//                questions.add(question);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void sendPacket(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
//        byte[] data = message.getBytes();
//        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
//        socket.send(packet);
//    }
//
//    private static DatagramPacket receivePacket(DatagramSocket socket) {
//        byte[] buffer = new byte[1024];
//        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//        try {
//            socket.receive(packet);
//            return packet;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
import java.io.*;
import java.net.*;
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

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);

            while (true) {
                DatagramPacket clientPacket = receivePacket(socket);
                InetAddress clientAddress = clientPacket.getAddress();
                int clientPort = clientPacket.getPort();

                executorService.submit(() -> handleClient(socket, clientAddress, clientPort));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(DatagramSocket socket, InetAddress clientAddress, int clientPort) {
        try (
                BufferedWriter answerWriter = new BufferedWriter(new FileWriter(ANSWER_FILE, true));
                BufferedWriter resultWriter = new BufferedWriter(new FileWriter(RESULT_FILE, true))
        ) {
            int score = 0;
            String name = receiveMessage(socket, clientAddress, clientPort);
            answerWriter.write(name);

            for (Question question : questions) {
                String questionMessage = question.getQuestion() + "," + question.getOptionA() + "," +
                        question.getOptionB() + "," + question.getOptionC() + "," + question.getOptionD() + "," +
                        question.getCorrectAnswer();
                sendMessage(socket, clientAddress, clientPort, questionMessage);

                DatagramPacket answerPacket = receivePacket(socket);
                if (answerPacket == null) {
                    break;
                }

                String studentAnswer = new String(answerPacket.getData(), 0, answerPacket.getLength());

                if (studentAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                    score++;
                }

                answerWriter.write(" " + studentAnswer);
            }
            answerWriter.write(System.lineSeparator());

            String resultMessage = name + " score: " + score;
            sendMessage(socket, clientAddress, clientPort, resultMessage);

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
                Question question = new Question(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                questions.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DatagramPacket receivePacket(DatagramSocket socket) {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            return packet;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String receiveMessage(DatagramSocket socket, InetAddress clientAddress, int clientPort) throws IOException {
        DatagramPacket messagePacket = receivePacket(socket);
        String message = new String(messagePacket.getData(), 0, messagePacket.getLength());
        sendAck(socket, clientAddress, clientPort);
        return message;
    }

    private static void sendMessage(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
        waitForAck(socket);
    }

    private static void sendAck(DatagramSocket socket, InetAddress address, int port) throws IOException {
        String ackMessage = "ACK";
        byte[] data = ackMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }

    private static void waitForAck(DatagramSocket socket, InetAddress clientAddress, int clientPort) {
        byte[] buffer = new byte[1024];
        DatagramPacket ackPacket = new DatagramPacket(buffer, buffer.length);

        try {
            socket.setSoTimeout(5000); // Set a timeout of 5 seconds

            while (true) {
                try {
                    socket.receive(ackPacket);
                    InetAddress receivedAddress = ackPacket.getAddress();
                    int receivedPort = ackPacket.getPort();

                    if (receivedAddress.equals(clientAddress) && receivedPort == clientPort) {
                        String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength());

                        if (ackMessage.equals("ACK")) {
                            // Acknowledgment received, exit the loop
                            break;
                        }
                    }
                } catch (SocketTimeoutException e) {
                    // SocketTimeoutException is thrown if the acknowledgment is not received within the timeout
                    System.out.println("Timeout reached. Resending the message...");
                    // Implement code to resend the message here
                    // You might want to implement a maximum number of retries to avoid an infinite loop
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}