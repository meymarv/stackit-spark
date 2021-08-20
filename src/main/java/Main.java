import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import tripdata.TripData;
import weather.Weather;

import static org.apache.spark.sql.functions.*;

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

        // Join tripData and weather if tripData finds a weather dataset
        Dataset<Row> joined = tripDataDf.join(weatherDf, (tripDataDf.col("starttime").between(weatherDf.col("starttime"), weatherDf.col("endtime")).and(tripDataDf.col("stoptime").between(weatherDf.col("starttime"), weatherDf.col("endtime")))));
        // Prettify the joined dataset for later use and better visualisation
        Dataset<Row> prettyPrintJoined = joined.select(
                tripDataDf.col("bikeId"),
                tripDataDf.col("tripDuration"),
                tripDataDf.col("birthYear"),
                tripDataDf.col("gender"),
                tripDataDf.col("startStationName"),
                tripDataDf.col("endStationName"),
                tripDataDf.col("userType"),
                tripDataDf.col("startTime").alias("tripStartTime"),
                tripDataDf.col("stopTime").alias("tripStopTime"),
                date_format(tripDataDf.col("startTime"), "hh").alias("tripHourOfDay"),
                dayofmonth(tripDataDf.col("startTime")).alias("tripDayOfMonth"),
                weatherDf.col("eventId"),
                weatherDf.col("type"),
                weatherDf.col("severity")
        );
        // Show the pretty result
        prettyPrintJoined.show();
        // Group the pretty result by dayOfMonth
        RelationalGroupedDataset groupedByDayofMonth = prettyPrintJoined.groupBy("tripDayOfMonth");
        // Count the amount of trips per Day and then sort the in ascending order
        Dataset<Row> groupedByDayOfMonthAndSorted = groupedByDayofMonth.count().orderBy(prettyPrintJoined.col("tripDayOfMonth").asc());
        // Show the result for the amounf of trips per day of the month
        groupedByDayOfMonthAndSorted.show();

        // Query the average tripduration in seconds
        Dataset<Row> averageTripduration = tripDataDf.select(avg(tripDataDf.col("tripduration")));
        averageTripduration.show();

        // Query the average tripduration in seconds BASED on gender
        // Gender: 0 = unknown, 1 = male, 2 = female
        RelationalGroupedDataset tripdurationByGender = tripDataDf.select(tripDataDf.col("tripduration").cast("integer"), tripDataDf.col("gender")).groupBy(tripDataDf.col("gender"));
        Dataset<Row> avgTripdurationByGender = tripdurationByGender.avg("tripduration");
        avgTripdurationByGender.show();

        // Query the average tripduration in seconds BASED on usertype
        // Usertype: "Customer" = 24hr pass or 3day pass, "Subscriber" = annual member
        // Dataset<Row> averageTripDurationByUsertype = spark.sql("SELECT avg(tripduration) averageTripduration, usertype FROM tripdata GROUP BY usertype");
        // averageTripDurationByUsertype.show();

        // Query which gender is the most common to have a bike subscription
        // Dataset<Row> usertypeBasedOnGender = spark.sql("SELECT usertype, count(gender) as genderCounter, gender FROM tripdata GROUP BY usertype, gender");
        // usertypeBasedOnGender.show();
    }
}
