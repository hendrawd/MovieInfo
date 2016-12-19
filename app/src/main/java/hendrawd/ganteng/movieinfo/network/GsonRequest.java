package hendrawd.ganteng.movieinfo.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import hendrawd.ganteng.movieinfo.util.Logger;

/**
 * Copied from http://icetea09.com/blog/2014/11/02/android-parse-json-request-using-volley-gson/
 * Apparently it copied from https://gist.github.com/ficusk/5474673
 * <p>
 * Improved by hendrawd on 11/17/16
 */

public class GsonRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;

    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Logger.d("network response", "network response of " + getUrl() + " = " + json);
            Logger.d("network response", "network response time of " + getUrl() + " = " + response.networkTimeMs + "ms");
            return Response.success(gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        if (error.networkResponse != null) {
            String sResponse = null;
            try {
                sResponse = new String(error.networkResponse.data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                //fuck this one, it's never happens
                e.printStackTrace();
            }
            Logger.d("network response", "network response of " + getUrl() + " = " + sResponse);
            Logger.d("network response", "network response time of " + getUrl() + " = " + error.networkResponse.networkTimeMs + "ms");
        } else {
            Logger.d("network response", "volley network response is null");
        }
        super.deliverError(error);
    }
}
