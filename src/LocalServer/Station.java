package LocalServer;

public class Station {
    String Name, Location, ior;

    public Station(String name, String location, String ior) {
        Name = name;
        Location = location;
        this.ior = ior;
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

    public String getIor() {
        return ior;
    }

    public void setIor(String ior) {
        this.ior = ior;
    }

    @Override
    public String toString() {
        return "LocalServer.Station{" +
                "Name='" + Name + '\'' +
                ", Location='" + Location + '\'' +
                ", ior='" + ior + '\'' +
                '}';
    }
}
