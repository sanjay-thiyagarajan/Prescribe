package amrita.prescribe.Model;

// Model class for holding the reports

public class Reports {
    
    // Report title and report date
    String title,date;
    
    public Reports() {
    }

    public Reports(String title, String date) {
        this.title = title;
        this.date = date;
    }
    
    // Getter for title
    public String getTitle() {
        return title;
    }
    
    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for date
    public String getDate() {
        return date;
    }

    // Setter for date
    public void setDate(String date) {
        this.date = date;
    }
}
