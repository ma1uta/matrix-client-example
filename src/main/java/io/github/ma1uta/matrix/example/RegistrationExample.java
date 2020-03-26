package io.github.ma1uta.matrix.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ma1uta.matrix.ExceptionResponse;
import io.github.ma1uta.matrix.UserInteractiveResponse;
import io.github.ma1uta.matrix.client.StandaloneClient;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.impl.exception.MatrixException;

public class RegistrationExample {

    public static void main(String[] args) {
        String domain = "ru-matrix.org";
        String localpart = "new_account";
        char[] password = "my_very_secret_password".toCharArray();

        StandaloneClient mxClient = new StandaloneClient.Builder().domain(domain).build();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(localpart);
        registerRequest.setPassword(password);

        syncRegistration(mxClient, registerRequest);
        asyncRegistration(mxClient, registerRequest);
    }

    private static void syncRegistration(StandaloneClient mxClient, RegisterRequest registerRequest) {
        try {
            LoginResponse loginResponse = mxClient.account().register(registerRequest).join();
            processLogin(loginResponse);
        } catch (Exception e) {
            processException(e);
        }
    }

    private static void asyncRegistration(StandaloneClient mxClient, RegisterRequest registerRequest) {
        mxClient.account().register(registerRequest).whenComplete((loginResponse, throwable) -> {
            if (throwable != null) {
                processException(throwable);
            }
            if (loginResponse != null) {
                processLogin(loginResponse);
            }
        });
    }

    private static void processLogin(LoginResponse loginResponse) {
        System.out.println("LOGIN!!!");
        System.out.println(loginResponse.getUserId());
        System.out.println(loginResponse.getDeviceId());
        System.out.println(loginResponse.getAccessToken());
    }

    private static void processException(Throwable e) {
        Throwable cause = e;
        while (!(cause instanceof MatrixException) && cause.getCause() != null) {
            cause = cause.getCause();
        }
        if (cause instanceof MatrixException) {
            MatrixException matrixException = (MatrixException) cause;
            ExceptionResponse response = matrixException.getResponse();
            if (response instanceof UserInteractiveResponse) {
                UserInteractiveResponse userInteractiveResponse = (UserInteractiveResponse) response;
                System.out.println(userInteractiveResponse.getSession());
                ObjectMapper mapper = new ObjectMapper();
                try {
                    System.out.println(mapper.writeValueAsString(userInteractiveResponse));
                } catch (JsonProcessingException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        }
    }
}
