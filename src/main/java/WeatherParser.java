import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import weather.Weather;

public class WeatherParser {
    private final SparkSession sparkSession;
    private final String pathToFile;

    public WeatherParser(SparkSession sparkSession, String pathToFile) {
        this.sparkSession = sparkSession;
        this.pathToFile = pathToFile;
    }

    public Dataset<Row> createDataset() {
        return this.sparkSession.read()
                .option("header", true)
                .csv(this.pathToFile);
    }

    public JavaRDD<Weather> createJavaRDD() {
        return this.createDataset().javaRDD().map((Function<Row, Weather>) row -> {
            Weather weather = new Weather();
            weather.setEventId(row.getString(0));
            weather.setType(row.getString(1));
            weather.setSeverity(row.getString(2));
            weather.setStartTime(row.getString(3));
            weather.setEndTime(row.getString(4));
            weather.setTimeZone(row.getString(5));
            weather.setAirportCode(row.getString(6));
            weather.setLatitude(row.getString(7));
            weather.setLongitude(row.getString(8));
            weather.setCity(row.getString(9));
            weather.setCounty(row.getString(10));
            weather.setState(row.getString(11));
            weather.setZipcode(row.getString(12));
            return weather;
        }).filter(weather -> {
            DateValidatorUsingDateFormat validatorUsingDateFormat = new DateValidatorUsingDateFormat("yyyy-MM-dd HH:mm:ss");
            return validatorUsingDateFormat.isValid(weather.getStartTime()) && validatorUsingDateFormat.isValid(weather.getEndTime());
        });
    }
}
