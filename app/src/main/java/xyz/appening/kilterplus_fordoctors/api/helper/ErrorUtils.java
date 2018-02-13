package xyz.appening.kilterplus_fordoctors.api.helper;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import xyz.appening.kilterplus_fordoctors.api.model.ErrorResponse;

/**
 * Created by salildhawan on 26/01/18.
 */

public class ErrorUtils {

    public static ErrorResponse parseError(Response<?> response) {
        Converter<ResponseBody, ErrorResponse> converter =
                ServiceGenerator.retrofit
                        .responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        ErrorResponse error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ErrorResponse();
        }

        return error;
    }
}
