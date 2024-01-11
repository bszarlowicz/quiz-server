package labpackage;

public class Question {
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String correctAnswer;

    public Question(String question, String optionA, String optionB, String optionC, String correctAnswer) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}

