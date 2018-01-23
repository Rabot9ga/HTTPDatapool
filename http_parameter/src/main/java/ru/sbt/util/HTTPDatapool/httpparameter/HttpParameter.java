package ru.sbt.util.HTTPDatapool.httpparameter;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class HttpParameter {

    private static volatile MainController mainController;
    private static volatile Retrofit retrofit;


    public static RequestAdder getInstance(final String hostPort) {
        MainController localController = mainController;
        if (localController == null) {
            synchronized (HttpParameter.class) {
                localController = mainController;
                if (localController == null) {
                    mainController = localController = createController(hostPort);
                }
            }
        }
        return new RequestAdder(localController);
    }

    private static MainController createController(String hostPort) {
        retrofit = new Retrofit.Builder()
                .baseUrl(hostPort)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(MainController.class);
    }
}