package hu.uniobuda.nik.parentalcontrol.database;

public class BlockedApps {
    int personId;
    String packageName;

    public BlockedApps(int personId, String packageName)
    {
        this.personId = personId;
        this.packageName = packageName;
    }

    public BlockedApps(int personId)
    {
        this.personId = personId;
    }

    public BlockedApps(String packageName)
    {
        this.packageName = packageName;
    }

    public void setPersonId(int personId)
    {
        this.personId = personId;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public int getPersonId()
    {
        return personId;
    }

    public String getPackageName()
    {
        return packageName;
    }

}
