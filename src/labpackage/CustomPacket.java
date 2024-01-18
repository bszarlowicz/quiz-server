//package labpackage;
//
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class CustomPacket implements Runnable {
//    private final DatagramSocket socket;
//    private byte[] buf;
//    private DatagramPacket packet;
//    private final List<Question> questions;
//    private static final Map<String, HandleClient> activeQuizzes = Collections.synchronizedMap(new HashMap<>());
//
//    public CustomPacket(DatagramSocket socket, byte[] buf, DatagramPacket packet, List<Question> questions) {
//        this.socket = socket;
//        this.buf = buf;
//        this.packet = packet;
//        this.questions = questions;
//    }
//
//    @Override
//    public void run() {
//        try {
//            synchronized (activeQuizzes) {
//                synchronized (questions) {
//                    InetAddress clientAddress = packet.getAddress();
//                    int clientPort = packet.getPort();
//                    packet = new DatagramPacket(buf, buf.length, clientAddress, clientPort);
//                    String[] receivedData = new String(packet.getData(), 0, packet.getLength()).split("::");
//                    byte[] acknowledgment = "ACK".getBytes();
//                    socket.send(new DatagramPacket(acknowledgment, acknowledgment.length, clientAddress, clientPort));
//
//                    switch (receivedData[0]) {
//                        case "name":
//                            if (activeQuizzes.containsKey(receivedData[1])) {
//                                buf = "error::Name is already taken!".getBytes();
//                            } else {
//                                HandleClient newClient = new HandleClient(receivedData[1]);
//                                newClient.currentQuestion = questions.get(0);
//                                activeQuizzes.put(receivedData[1], newClient);
//                                buf = ("question::" + questions.get(0).toString()).getBytes();
//                                newClient.startTimer();
//                            }
//                            socket.send(new DatagramPacket(buf, buf.length, clientAddress, clientPort));
//                            break;
//                        case "answer":
//                            HandleClient answerClient = activeQuizzes.get(receivedData[1]);
//                            Question currentQuestion = answerClient.currentQuestion;
//                            answerClient.providedAnswers.add(new Answer(currentQuestion.question, receivedData[2].charAt(0), !answerClient.isTimeUp() && receivedData[2].equals(currentQuestion.getCorrectAnswer())));
//                            if (questions.get(questions.size() - 1).equals(answerClient.currentQuestion)) {
//                                answerClient.writeAnswers();
//                                float score = answerClient.saveScore();
//                                buf = ("score::" + score).getBytes();
//                                activeQuizzes.remove(receivedData[1]);
//                            } else {
//                                answerClient.currentQuestion = questions.get(questions.indexOf(answerClient.currentQuestion) + 1);
//                                buf = ("question::" + answerClient.currentQuestion.toString()).getBytes();
//                                answerClient.startTimer();
//                            }
//                            socket.send(new DatagramPacket(buf, buf.length, clientAddress, clientPort));
//                            break;
//                        default:
//                            buf = "error::Wrong message sent!".getBytes();
//                            socket.send(new DatagramPacket(buf, buf.length, clientAddress, clientPort));
//                            break;
//                    }
//                }
//            }
//        } catch (Exception err) {
//            err.printStackTrace();
//        }
//    }
//}
package labpackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomPacket implements Runnable {
    private final DatagramSocket socket;
    private DatagramPacket packet;
    private final List<Question> questions;
    private static final Map<String, HandleClient> activeQuiz = Collections.synchronizedMap(new HashMap<>());

    public CustomPacket(DatagramSocket socket, DatagramPacket packet, List<Question> questions) {
        this.socket = socket;
        this.packet = packet;
        this.questions = questions;
    }

    @Override
    public void run() {
        try {
            synchronized (activeQuiz) {
                synchronized (questions) {
                    InetAddress clientAddress = packet.getAddress();
                    int clientPort = packet.getPort();
                    byte[] receivedData = packet.getData();
                    byte[] acknowledgment = "ACK".getBytes();
                    socket.send(new DatagramPacket(acknowledgment, acknowledgment.length, clientAddress, clientPort));

                    String receivedMessage = new String(receivedData, 0, packet.getLength());
                    String[] receivedParts = receivedMessage.split("::");

                    switch (receivedParts[0]) {
                        case "name":
                            handleNameRequest(clientAddress, clientPort, receivedParts[1]);
                            break;
                        case "answer":
                            handleAnswerRequest(clientAddress, clientPort, receivedParts[1], receivedParts[2]);
                            break;
                        default:
                            sendErrorMessage(clientAddress, clientPort, "Wrong message sent!");
                            break;
                    }
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void handleNameRequest(InetAddress clientAddress, int clientPort, String playerName) {
        try {
            if (activeQuiz.containsKey(playerName)) {
                sendErrorMessage(clientAddress, clientPort, "error::Name is already taken!");
            } else {
                HandleClient newClient = new HandleClient(playerName);
                newClient.currentQuestion = questions.get(0);
                activeQuiz.put(playerName, newClient);
                byte[] questionMessage = ("question::" + questions.get(0).toString()).getBytes();
                newClient.startTimer();
                socket.send(new DatagramPacket(questionMessage, questionMessage.length, clientAddress, clientPort));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAnswerRequest(InetAddress clientAddress, int clientPort, String playerName, String answer) {
        try {
            HandleClient answerClient = activeQuiz.get(playerName);
            Question currentQuestion = answerClient.currentQuestion;
            answerClient.providedAnswers.add(new Answer(currentQuestion.question, answer.charAt(0), !answerClient.isTimeUp() && answer.equals(currentQuestion.getCorrectAnswer())));

            if (questions.get(questions.size() - 1).equals(answerClient.currentQuestion)) {
                answerClient.writeAnswers();
                float score = answerClient.saveScore();
                byte[] scoreMessage = ("score::" + score).getBytes();
                activeQuiz.remove(playerName);
                socket.send(new DatagramPacket(scoreMessage, scoreMessage.length, clientAddress, clientPort));
            } else {
                answerClient.currentQuestion = questions.get(questions.indexOf(answerClient.currentQuestion) + 1);
                byte[] nextQuestionMessage = ("question::" + answerClient.currentQuestion.toString()).getBytes();
                answerClient.startTimer();
                socket.send(new DatagramPacket(nextQuestionMessage, nextQuestionMessage.length, clientAddress, clientPort));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendErrorMessage(InetAddress clientAddress, int clientPort, String errorMessage) {
        try {
            byte[] errorBytes = errorMessage.getBytes();
            socket.send(new DatagramPacket(errorBytes, errorBytes.length, clientAddress, clientPort));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
