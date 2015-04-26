package hu.uniobuda.nik.parentalcontrol.database;

public class Settings {

    int id;
    String email;
    String password;
    int faceRecEnabled;
    int accessControlEnabled;

    public Settings(int id, String email, String password, int faceRecEnabled, int accessControlEnabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.faceRecEnabled = faceRecEnabled;
        this.accessControlEnabled = accessControlEnabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int isFaceRecEnabled() {
        return faceRecEnabled;
    }

    public void setFaceRecEnabled(int faceRecEnabled) {
        this.faceRecEnabled = faceRecEnabled;
    }

    public int isAccessControlEnabled() {
        return accessControlEnabled;
    }

    public void setAccessControlEnabled(int accessControlEnabled) {
        this.accessControlEnabled = accessControlEnabled;
    }
}
