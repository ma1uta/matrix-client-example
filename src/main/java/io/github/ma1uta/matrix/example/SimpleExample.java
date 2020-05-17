package io.github.ma1uta.matrix.example;

import io.github.ma1uta.matrix.client.StandaloneClient;

public class SimpleExample {

    public static void main(String[] args) {
        String domain = "ru-matrix.org";
        String localpart = "ma1uta";
        char[] password = "my_very_secret_password".toCharArray();

        StandaloneClient mxClient = new StandaloneClient.Builder().domain(domain).build();

        // login
        String userId = mxClient.auth().login(localpart, password).getUserId();

        // set display name via profile api
        System.out.println(mxClient.profile().showDisplayName(userId));

        // retrieve all joined rooms
        mxClient.room().joinedRooms().getJoinedRooms().forEach(System.out::println);

        // logout
        mxClient.auth().logout();
    }
}
