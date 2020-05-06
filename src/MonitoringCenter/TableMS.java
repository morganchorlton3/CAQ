package MonitoringCenter;

public class TableMS {
    String Name, Location;
    boolean status;

    public TableMS(String name, String location, boolean status) {
        Name = name;
        Location = location;
        this.status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
