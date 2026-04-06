package fi.haagahelia.personal_finance_tracker.domain;

public class FinancialResultDTO {
    private String username;
    private String category;
    private String month;
    private double actualSpent;
    private double budgetLimit;
    private double difference;
    private double percentage;

    public FinancialResultDTO(String username, String category, String month, double actualSpent, double budgetLimit) {
                this.username = username;
                this.category = category;
                this.month = month;
                this.actualSpent = actualSpent;
                this.budgetLimit = budgetLimit;
                this.difference = budgetLimit - actualSpent;
                this.percentage = (budgetLimit > 0) ? (actualSpent / budgetLimit) * 100 : 0;
            }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getActualSpent() {
        return actualSpent;
    }

    public void setActualSpent(double actualSpent) {
        this.actualSpent = actualSpent;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }


    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    @Override
    public String toString() {
        return "FinancialResultDTO [username=" + username + ", category=" + category + ", month=" + month
                + ", actualSpent=" + actualSpent + ", budgetLimit=" + budgetLimit + ", difference=" + difference
                + ", percentage=" + percentage + "]";
    }

    
            
}
