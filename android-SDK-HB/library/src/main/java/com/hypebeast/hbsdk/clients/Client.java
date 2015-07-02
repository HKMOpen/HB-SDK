package com.hypebeast.hbsdk.clients;

import com.google.gson.Gson;
import com.hypebeast.hbsdk.api.exception.ApiException;
import com.hypebeast.hbsdk.api.exception.BadRequestException;
import com.hypebeast.hbsdk.api.exception.ForbiddenException;
import com.hypebeast.hbsdk.api.exception.InternalServerError;
import com.hypebeast.hbsdk.api.exception.NotFoundException;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by hesk on 2/7/15.
 */
public abstract class Client {
    protected Gson gsonsetup;
    protected final ErrorHandler handlerError = new ErrorHandler() {
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response response = cause.getResponse();
            if (response != null) {
                switch (response.getStatus()) {
                    case 400:
                        return new BadRequestException(cause);
                    case 401:
                        return new ForbiddenException(cause);
                    case 404:
                        return new NotFoundException(cause);
                    case 500:
                        return new InternalServerError(cause);
                }
            }
            return new ApiException(cause);
        }
    };
    /**
     * Rest adapter
     */
    protected RestAdapter mAdapter;

    protected abstract void registerAdapter();

    protected abstract String get_USER_AGENT();

    protected RequestInterceptor getIn() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("User-Agent", get_USER_AGENT());
                request.addHeader("Accept", "application/json");
                request.addHeader("X-Api-Version", "2.0");

            }

        };
    }
}
