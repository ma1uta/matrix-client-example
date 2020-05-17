package io.github.ma1uta.matrix.example;

import io.github.ma1uta.matrix.client.StandaloneClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AvatarUrlExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarUrlExample.class);

    public static void main(String[] args) {
        var mxid = "@travis:t2l.io";
        var domain = "ru-matrix.org";

        var mxClient = new StandaloneClient.Builder().domain(domain).build();

        asyncExample(mxid, mxClient);
        syncExample(mxid, mxClient);
    }

    private static void syncExample(String mxid, StandaloneClient mxClient) {
        var avatar = mxClient.profile().showAvatarUrl(mxid);
        var mxcUrl = avatar.getAvatarUrl();
        System.out.println(mxcUrl);

        var content = mxClient.content().download(mxcUrl, true);
        try {
            Files.copy(content.getInputStream(), Path.of(content.getFilename()), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Done");
        } catch (IOException e) {
            LOGGER.error("Unable to write avatar to file", e);
        }
    }

    private static void asyncExample(String mxid, StandaloneClient mxClient) {
        mxClient.profileAsync().showAvatarUrl(mxid).thenAccept(avatar -> {
            var mxcUrl = avatar.getAvatarUrl();
            System.out.println(mxcUrl);

            mxClient.contentAsync().download(mxcUrl, true)
                .thenAccept(content -> {
                    try {
                        Files.copy(content.getInputStream(), Path.of(content.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                        LOGGER.info("Done");
                    } catch (IOException e) {
                        LOGGER.error("Unable to write avatar to file", e);
                    }
                });
        });
    }
}
