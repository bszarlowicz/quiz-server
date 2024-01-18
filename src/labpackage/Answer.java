package labpackage;

public class Answer {
    private final String question;
    private final char answer;
    public final boolean isCorrect;
    public Answer(String question, char answer, boolean isCorrect) {
        this.question = question;
        this.answer = answer;
        this.isCorrect = isCorrect;
    }


    @Override
    public String toString() {
        return answer + " ";
    }
}
