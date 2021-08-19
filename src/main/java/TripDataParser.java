import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import tripdata.TripData;


public class TripDataParser {
    private final SparkSession sparkSession;
    private final String pathToFile;

    public TripDataParser(SparkSession sparkSession, String pathToFile) {
        this.sparkSession = sparkSession;
        this.pathToFile = pathToFile;
    }

    public Dataset<Row> createDataset() {
        return this.sparkSession.read()
                .option("header", true)
                .csv(this.pathToFile);
    }

    public JavaRDD<TripData> createJavaRDD() {
        return this.createDataset().javaRDD().map((Function<Row, TripData>) row -> {
            TripData tripData = new TripData();
            tripData.setTripDuration(row.getString(0));
            tripData.setStartTime(row.getString(1));
            tripData.setStopTime(row.getString(2));
            tripData.setStartStationId(row.getString(3));
            tripData.setStartStationName(row.getString(4));
            tripData.setStartStationLatitude(row.getString(5));
            tripData.setStartStationLongitude(row.getString(6));
            tripData.setEndStationId(row.getString(7));
            tripData.setEndStationName(row.getString(8));
            tripData.setEndStationLatitude(row.getString(9));
            tripData.setEndStationLongitude(row.getString(10));
            tripData.setBikeId(row.getString(11));
            tripData.setUserType(row.getString(12));
            tripData.setBirthYear(row.getString(13));
            tripData.setGender(row.getString(14));
            return tripData;
        }).filter(tripData -> {
            DateValidatorUsingDateFormat validatorUsingDateFormat = new DateValidatorUsingDateFormat("yyyy-MM-dd HH:mm:ss.ssss");
            return validatorUsingDateFormat.isValid(tripData.getStartTime()) && validatorUsingDateFormat.isValid(tripData.getStopTime());
        });
    }
}
