package xyz.skrawlr.lovelyday.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PredictionService {
    @GET("/horoscope/{period}/{sunsign}")
    Call<ReceivedPrediction> getPrediction(@Path("sunsign") String sunsign, @Path("period") String period);
}
