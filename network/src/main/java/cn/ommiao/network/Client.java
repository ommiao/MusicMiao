package cn.ommiao.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class Client {

    private static final long CONNECT_TIMEOUT = 120L;
    private static final long READ_TIMEOUT = 120L;
    private static final long WRITE_TIMEOUT = 120L;

    protected static OkHttpClient CLIENT;

    public static void initNetwork(){
        CLIENT = new OkHttpClient
                .Builder()
//            .addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request request = chain.request();
//                    Request.Builder requestBuilder = request.newBuilder();
//                    request = requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=gbk"),
//                            URLDecoder.decode(bodyToString(request.body()), "UTF-8")))
//                            .build();
//                    return chain.proceed(request);
//                }
//
//                private String bodyToString(final RequestBody request) {
//                    try {
//                        final RequestBody copy = request;
//                        final Buffer buffer = new Buffer();
//                        if (copy != null)
//                            copy.writeTo(buffer);
//                        else
//                            return "";
//                        return buffer.readUtf8();
//                    } catch (final IOException e) {
//                        return "did not work";
//                    }
//                }
//            })
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

}
