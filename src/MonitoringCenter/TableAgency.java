package MonitoringCenter;

public class TableAgency {
    String name, locationOfInterest, contactInfo;

    public TableAgency(String name, String locationOfInterest, String contactInfo) {
        this.name = name;
        this.locationOfInterest = locationOfInterest;
        this.contactInfo = contactInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationOfInterest() {
        return locationOfInterest;
    }

    public void setLocationOfInterest(String locationOfInterest) {
        this.locationOfInterest = locationOfInterest;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
