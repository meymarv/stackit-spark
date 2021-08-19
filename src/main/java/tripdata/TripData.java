package tripdata;

import java.io.Serializable;

public class TripData implements Serializable {
    private String tripDuration;
    private String startTime;
    private String stopTime;
    private String startStationId;
    private String startStationName;
    private String startStationLatitude;
    private String startStationLongitude;
    private String endStationId;
    private String endStationName;
    private String endStationLatitude;
    private String endStationLongitude;
    private String bikeId;
    private String userType;
    private String birthYear;
    private String gender;

    public TripData(String tripDuration, String startTime, String stopTime, String startStationId, String startStationName, String startStationLatitude, String startStationLongitude, String endStationId, String endStationName, String endStationLatitude, String endStationLongitude, String bikeId, String userType, String birthYear, String gender) {
        this.tripDuration = tripDuration;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.startStationId = startStationId;
        this.startStationName = startStationName;
        this.startStationLatitude = startStationLatitude;
        this.startStationLongitude = startStationLongitude;
        this.endStationId = endStationId;
        this.endStationName = endStationName;
        this.endStationLatitude = endStationLatitude;
        this.endStationLongitude = endStationLongitude;
        this.bikeId = bikeId;
        this.userType = userType;
        this.birthYear = birthYear;
        this.gender = gender;
    }

    public TripData() {

    }

    public String getTripDuration() {
        return tripDuration;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public String getStartStationId() {
        return startStationId;
    }

    public String getStartStationName() {
        return startStationName;
    }

    public String getStartStationLatitude() {
        return startStationLatitude;
    }

    public String getStartStationLongitude() {
        return startStationLongitude;
    }

    public String getEndStationId() {
        return endStationId;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public String getEndStationLatitude() {
        return endStationLatitude;
    }

    public String getEndStationLongitude() {
        return endStationLongitude;
    }

    public String getBikeId() {
        return bikeId;
    }

    public String getUserType() {
        return userType;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getGender() {
        return gender;
    }

    public void setTripDuration(String tripDuration) {
        this.tripDuration = tripDuration;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public void setStartStationId(String startStationId) {
        this.startStationId = startStationId;
    }

    public void setStartStationName(String startStationName) {
        this.startStationName = startStationName;
    }

    public void setStartStationLatitude(String startStationLatitude) {
        this.startStationLatitude = startStationLatitude;
    }

    public void setStartStationLongitude(String startStationLongitude) {
        this.startStationLongitude = startStationLongitude;
    }

    public void setEndStationId(String endStationId) {
        this.endStationId = endStationId;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
    }

    public void setEndStationLatitude(String endStationLatitude) {
        this.endStationLatitude = endStationLatitude;
    }

    public void setEndStationLongitude(String endStationLongitude) {
        this.endStationLongitude = endStationLongitude;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
