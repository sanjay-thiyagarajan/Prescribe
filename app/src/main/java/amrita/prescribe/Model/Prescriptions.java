package amrita.prescribe.Model;

// Model class for fetching the Prescriptions

public class Prescriptions {
    
    // Doctor's name
    String dr_name;

    // Constructors
    public Prescriptions() {
    }

    public Prescriptions(String dr_name) {
        this.dr_name = dr_name;

    }

    // GETTER
    public String getDr_name() {
        return dr_name;
    }

    // SETTER
    public void setDr_name(String dr_name) {
        this.dr_name = dr_name;
    }

}
