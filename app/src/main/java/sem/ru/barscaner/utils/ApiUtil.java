package sem.ru.barscaner.utils;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.HttpException;
import sem.ru.barscaner.di.App;


public class ApiUtil {

    private static final String TAG = "ApiUtil";

    public static String getResponseError(ResponseBody errorBody){
        StringBuilder sb = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(errorBody.byteStream()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return sb.toString();
    }

    public static String requestBodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static String getResponseError(Throwable e){
        StringBuilder sb = new StringBuilder();
        if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            BufferedReader reader;

            try {
                reader = new BufferedReader(new InputStreamReader(exception.response().errorBody().byteStream()));
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static String getValueResponse(String response){
        Pattern p = Pattern.compile(":\".+\"");
        Matcher m = p.matcher(response);
        if(m.find()) return response.substring(m.start()+2, m.end()-1); else return "";
    }

    private class ErrorFromServer{

        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("status")
        @Expose
        private String status;


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static String getMessage(Throwable e){
        if (e instanceof HttpException) {
            ResponseBody body = ((HttpException) e).response().errorBody();

           /* Converter<ResponseBody, ErrorFromServer> errorConverter =
                    App.getAppComponent().getRetrofit().responseBodyConverter(ErrorFromServer.class, new Annotation[0]);
            // Convert the error body into our Error type.
            try {
                ErrorFromServer error = errorConverter.convert(body);
                return error.getError();

            } catch (Exception e1) {
                e1.printStackTrace();
                return "Unknown exception";
            }*/

           return getMessage(body);
        }else return e.getMessage();
    }

    public static String getMessage(ResponseBody body) {
        Converter<ResponseBody, ErrorFromServer> errorConverter =
                App.getAppComponent().getRetrofit().
                        responseBodyConverter(ErrorFromServer.class, new Annotation[0]);

        String errorText="";
        try {
            ErrorFromServer error = errorConverter.convert(body);
            errorText=error.getMessage();
           /* JSONObject jsonObj = new JSONObject(errorText);
            return jsonObj.getString("error");
        } catch (JSONException jsonEx) {
            jsonEx.printStackTrace();
            return errorText;*/
        } catch (IOException e) {
            e.printStackTrace();
            return "Unknown exception";
        } catch (Exception e1) {
            e1.printStackTrace();
            return "Unknown exception";
        }
        return errorText;
    }

}
