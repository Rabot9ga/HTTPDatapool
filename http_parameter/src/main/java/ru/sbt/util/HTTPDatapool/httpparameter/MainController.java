package ru.sbt.util.HTTPDatapool.httpparameter;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.sbt.util.HTTPDatapool.httpdto.DatapoolRequest;
import ru.sbt.util.HTTPDatapool.httpdto.DatapoolResponse;

public interface MainController {
    @POST("/api/getParameter")
    Call<DatapoolResponse> getParameter(@Body DatapoolRequest request);
}
