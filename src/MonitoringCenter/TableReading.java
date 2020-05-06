package MonitoringCenter;

public class TableReading {
    int reading_value;
    String station_name, date, time;

    public TableReading(String time, String date, int reading_value, String station_name) {
        this.time = time;
        this.date = date;
        this.reading_value = reading_value;
        this.station_name = station_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getReading_value() {
        return reading_value;
    }

    public void setReading_value(int reading_value) {
        this.reading_value = reading_value;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }
}
