package hu.uniobuda.nik.parentalcontrol.database;


public class Persons {
    int personId;
    String personName;

    public Persons(int personId, String personName) {
        this.personId = personId;
        this.personName = personName;
    }

    public Persons(int personId) {
        this.personId = personId;
    }

    public Persons(String personName) {
        this.personName = personName;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
