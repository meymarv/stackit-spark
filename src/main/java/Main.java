import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import tripdata.TripData;
import weather.Weather;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("STACKIT-Spark");
        SparkSession spark = SparkSession.builder().config(sparkConf).getOrCreate();
        spark.sparkContext().setLogLevel("error");
        WeatherParser weatherParser = new WeatherParser(spark, "src/main/resources/weather.csv");
        TripDataParser tripDataParser = new TripDataParser(spark, "src/main/resources/tripdata.csv");

        JavaRDD<Weather> weatherJavaRDD = weatherParser.createJavaRDD();
        Dataset<Row> weatherDf = spark.createDataFrame(weatherJavaRDD, Weather.class);
        weatherDf.createOrReplaceTempView("weather");

        JavaRDD<TripData> tripDataJavaRDD = tripDataParser.createJavaRDD();
        Dataset<Row> tripDataDf = spark.createDataFrame(tripDataJavaRDD, TripData.class);
        tripDataDf.createOrReplaceTempView("tripdata");


        Dataset<Row> rowDataset = spark.sql("SELECT * FROM tripdata WHERE gender = '2'");
        rowDataset.show(true);
    }
}
