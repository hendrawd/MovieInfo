package hendrawd.ganteng.movieinfo.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpSingleton {

    private OkHttpClient mOkHttpClient;

    private static OkHttpSingleton ourInstance;

    public static OkHttpSingleton getInstance() {
        if (ourInstance == null) {
            ourInstance = new OkHttpSingleton();
        }
        return ourInstance;
    }

    private OkHttpSingleton() {
        OkHttpClient.Builder builderOkhttp = new OkHttpClient.Builder();
        builderOkhttp.connectTimeout(20, TimeUnit.SECONDS);
        builderOkhttp.readTimeout(15, TimeUnit.SECONDS);
        builderOkhttp.writeTimeout(15, TimeUnit.SECONDS);
        builderOkhttp.addInterceptor(getInterceptor());
        mOkHttpClient = builderOkhttp.build();
        setOkHttpClient(mOkHttpClient);
    }

    private Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Accept", "application/json")
                        .build();
                return chain.proceed(newRequest);
            }
        };
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }
}