import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import tripdata.TripData;
import weather.Weather;

import static org.apache.spark.sql.functions.*;

public class Main {
    public static void main(String[] args) {
        // Set "setMaster" to "local" if you want to run it locally on your hardware
        // Set "setMaster" to "spark://domain:port" if you want to run it distributed on spark cluster
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("STACKIT-Spark");
        SparkSession spark = SparkSession.builder().config(sparkConf).getOrCreate();
        spark.sparkContext().setLogLevel("error");

        // Set pathToFile to local file (e.g. "src/main/resources/weather.csv") if you don't want to use HDFS
        // Add hdp domain IP after "hdfs://" if you want it with specific domain settings
        WeatherParser weatherParser = new WeatherParser(spark, "hdfs:///tmp/weather.csv");
        // Set pathToFile to local file (e.g. "src/main/resources/tripdata.csv") if you don't want to use HDFS
        // Add hdp domain IP after "hdfs://" if you want it with specific domain settings
        TripDataParser tripDataParser = new TripDataParser(spark, "hdfs:///tmp/tripdata.csv");

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
                date_format(tripDataDf.col("startTime"), "HH").alias("tripStartHourOfDay"),
                dayofmonth(tripDataDf.col("startTime")).alias("tripStartDayOfMonth"),
                weatherDf.col("eventId"),
                weatherDf.col("type"),
                weatherDf.col("severity")
        );
        // Show the pretty result
        prettyPrintJoined.show();
        // Group the pretty result by dayOfMonth
        RelationalGroupedDataset groupedByDayofMonth = prettyPrintJoined.groupBy("tripStartDayOfMonth");
        // Count the amount of trips per Day and then sort in ascending order
        Dataset<Row> groupedByDayOfMonthAndSorted = groupedByDayofMonth.count().orderBy(prettyPrintJoined.col("tripStartDayOfMonth").asc());
        // Show the result for the amount of trips per day of the month
        groupedByDayOfMonthAndSorted.show();

        // Group the result by type and severity
        RelationalGroupedDataset a = prettyPrintJoined.groupBy(prettyPrintJoined.col("type"), prettyPrintJoined.col("severity"));
        // Count the amount of trips per type/severity
        Dataset<Row> b = a.count().orderBy(prettyPrintJoined.col("type").asc());
        b.show();

        // Group the pretty result by hourOfDay so we can figure out which time of the day has the most bike trips
        RelationalGroupedDataset groupedByHourOfDay = prettyPrintJoined.groupBy("tripStartHourOfDay");
        // Count the amount of trips per hour and then sort them based on hourOfDay in ascending order
        Dataset<Row> groupedByHourOfDayAndSorted = groupedByHourOfDay.count().orderBy(prettyPrintJoined.col("tripStartHourOfDay").asc());

        // Count the amount of trips per hour and then sort them based on count in ascending order
        Dataset<Row> groupedByHourOfDayCount = groupedByHourOfDay.count();
        Dataset<Row> groupedByHourOfDayAndCountSorted = groupedByHourOfDayCount.orderBy(groupedByHourOfDayCount.col("count").desc());
        // Show the result for the amount of trips per hour of the day
        groupedByHourOfDayAndSorted.show();
        groupedByHourOfDayAndCountSorted.show(); // Display most bike trips per hour of Day

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
        RelationalGroupedDataset tripdurationByUserType = tripDataDf.select(tripDataDf.col("tripduration").cast("integer"), tripDataDf.col("usertype")).groupBy(tripDataDf.col("usertype"));
        Dataset<Row> avgTripdurationByUserType = tripdurationByUserType.avg("tripduration");
        avgTripdurationByUserType.show();

        // Query which gender is the most common to have a bike subscription
        Dataset<Row> usertypeBasedOnGender = spark.sql("SELECT usertype, count(gender) as genderCounter, gender FROM tripdata GROUP BY usertype, gender ORDER BY gender ASC");
        usertypeBasedOnGender.show();
    }
}
