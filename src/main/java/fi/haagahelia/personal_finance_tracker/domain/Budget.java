package fi.haagahelia.personal_finance_tracker.domain;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetid;

    @DateTimeFormat(pattern = "yyyy-MM")
    @NotNull(message = "Month and Year are required")
    private YearMonth budgetDate;

    private double amount;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "categoryid")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    public Budget() {
    }

    public Budget(double amount, YearMonth budgetDate, Category category, AppUser user){
        this.amount = amount;
        this.budgetDate = budgetDate;
        this.category = category;
        this.user = user;
    }

    public Long getBudgetid() {
        return budgetid;
    }

    public void setBudgetid(Long budgetid) {
        this.budgetid = budgetid;
    }

    public YearMonth getBudgetDate() {
        return budgetDate;
    }

    public void setBudgetDate(YearMonth budgetDate) {
        this.budgetDate = budgetDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    

    @Override
    public String toString() {
        return "Budget [budgetid=" + budgetid + ", budgetDate=" + budgetDate +", category=" + category + "]";
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    
    
}
