import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import tripdata.TripData;
import weather.Weather;

public class Main {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("STACKIT-Spark");
        SparkSession spark = SparkSession.builder().config(sparkConf).getOrCreate();
        spark.sparkContext().setLogLevel("error");

        WeatherParser weatherParser = new WeatherParser(spark, "src/main/resources/weather.csv");
        TripDataParser tripDataParser = new TripDataParser(spark, "src/main/resources/tripdata.csv");

        // Create javaRDD for weather data in weather.csv
        JavaRDD<Weather> weatherJavaRDD = weatherParser.createJavaRDD();
        Dataset<Row> weatherDf = spark.createDataFrame(weatherJavaRDD, Weather.class);
        weatherDf.createOrReplaceTempView("weather");

        // Create javaRDD for trip data in tripdata.csv
        JavaRDD<TripData> tripDataJavaRDD = tripDataParser.createJavaRDD();
        Dataset<Row> tripDataDf = spark.createDataFrame(tripDataJavaRDD, TripData.class);
        tripDataDf.createOrReplaceTempView("tripdata");

        // try to query the weather data for every trip where the trip start and stop time is between weather start and end time
        // Sadly the weather data wasn't detailed enough so we can only find 1 dataset where the timestamps are within range of eachother
        Dataset<Row> df = spark.sql("SELECT trip.tripduration, trip.starttime, trip.stoptime, trip.startstationname, trip.endstationname, weather.type, weather.severity FROM tripdata as trip JOIN weather as weather ON (to_timestamp(cast(trip.starttime as String), 'yyyy-MM-dd HH:mm:ss') > to_timestamp(cast(weather.starttime as String), 'yyyy-MM-dd HH:mm:ss') AND to_timestamp(cast(trip.starttime as String), 'yyyy-MM-dd HH:mm:ss') < to_timestamp(cast(weather.endtime as String), 'yyyy-MM-dd HH:mm:ss')) AND ((to_timestamp(cast(trip.stoptime as String), 'yyyy-MM-dd HH:mm:ss') > to_timestamp(cast(weather.starttime as String), 'yyyy-MM-dd HH:mm:ss') AND to_timestamp(cast(trip.stoptime as String), 'yyyy-MM-dd HH:mm:ss') < to_timestamp(cast(weather.endtime as String), 'yyyy-MM-dd HH:mm:ss'))) ORDER BY trip.starttime");
        df.show();

        // Query the average tripduration in seconds
        Dataset<Row> averageTripDuration = spark.sql("SELECT avg(tripduration) as averageTripduration FROM tripdata");
        averageTripDuration.show();

        // Query the average tripduration in seconds BASED on gender
        // Gender: 0 = unknown, 1 = male, 2 = female
        Dataset<Row> averageTripDurationByGender = spark.sql("SELECT avg(tripduration) averageTripduration, gender FROM tripdata GROUP BY gender");
        averageTripDurationByGender.show();

        // Query the average tripduration in seconds BASED on usertype
        // Usertype: "Customer" = 24hr pass or 3day pass, "Subscriber" = annual member
        Dataset<Row> averageTripDurationByUsertype = spark.sql("SELECT avg(tripduration) averageTripduration, usertype FROM tripdata GROUP BY usertype");
        averageTripDurationByUsertype.show();

        // Query which gender is the most common to have a bike subscription
        Dataset<Row> usertypeBasedOnGender = spark.sql("SELECT usertype, count(gender) as genderCounter, gender FROM tripdata GROUP BY usertype, gender");
        usertypeBasedOnGender.show();
    }
}
