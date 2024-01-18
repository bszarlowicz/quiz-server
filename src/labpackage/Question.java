package labpackage;

public class Question {

    public String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    public Question(String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String toString() {
        return question + "::" + optionA + "::" + optionB + "::" + optionC + "::" + optionD;
    }
}
