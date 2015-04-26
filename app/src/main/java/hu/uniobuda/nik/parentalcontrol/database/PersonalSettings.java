package hu.uniobuda.nik.parentalcontrol.database;

public class PersonalSettings {

    String personName;
    int accessControlForPerson;
    String fromTime;
    String toTime;
    String selectedDays;

    public PersonalSettings(String personName, int accessControlForPerson, String fromTime, String toTime, String selectedDays) {
        this.personName = personName;
        this.accessControlForPerson = accessControlForPerson;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.selectedDays = selectedDays;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public int isAccessControlForPerson() {
        return accessControlForPerson;
    }

    public void setAccessControlForPerson(int accessControlForPerson) {
        this.accessControlForPerson = accessControlForPerson;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(String selectedDays) {
        this.selectedDays = selectedDays;
    }
}
