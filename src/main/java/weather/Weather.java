package weather;

import java.io.Serializable;

public class Weather implements Serializable {
    private String eventId;
    private String type;
    private String severity;
    private String startTime;
    private String endTime;
    private String timeZone;
    private String airportCode;
    private String latitude;
    private String longitude;
    private String city;
    private String county;
    private String state;
    private String zipcode;

    public Weather(String eventId, String type, String severity, String startTime, String endTime, String timeZone, String airportCode, String latitude, String longitude, String city, String county, String state, String zipcode) {
        this.eventId = eventId;
        this.type = type;
        this.severity = severity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeZone = timeZone;
        this.airportCode = airportCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.county = county;
        this.state = state;
        this.zipcode = zipcode;
    }

    public Weather() {

    }

    public String getEventId() {
        return eventId;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public String getState() {
        return state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
