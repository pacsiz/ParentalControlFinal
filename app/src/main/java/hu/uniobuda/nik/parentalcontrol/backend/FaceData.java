package hu.uniobuda.nik.parentalcontrol.backend;

public class FaceData {
    int id;

    String rows;
    String cols;
    String dt;
    String data;

    FaceData() {

    }

    FaceData(int id, String rows, String cols, String dt, String data) {
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.dt = dt;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public String getCols() {
        return cols;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
