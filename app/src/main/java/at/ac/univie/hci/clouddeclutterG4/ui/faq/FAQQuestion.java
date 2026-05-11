package at.ac.univie.hci.clouddeclutterG4.ui.faq;

public class FAQQuestion {
    private String question;
    private String answer;
    private boolean expanded = false;

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void toggleExpand() {
        expanded = !expanded;
    }
}
