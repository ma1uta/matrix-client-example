package io.github.ma1uta.matrix.example;

import io.github.ma1uta.matrix.client.StandaloneClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AvatarUrlExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarUrlExample.class);

    public static void main(String[] args) {
        String mxid = "@travis:t2l.io";
        String domain = "ru-matrix.org";
        String avatarFile = "avatar.jpg";
        Pattern pattern = Pattern.compile("mxc://(.*)/(.*)");
        Path avatarPath = Paths.get(avatarFile);

        StandaloneClient mxClient = new StandaloneClient.Builder().domain(domain).build();

        mxClient.profile().showAvatarUrl(mxid).thenAccept(avatar -> {
            String mxcUrl = avatar.getAvatarUrl();
            System.out.println(mxcUrl);

            Matcher matcher = pattern.matcher(mxcUrl);
            if (matcher.matches()) {
                String serverName = matcher.group(1);
                String mxcid = matcher.group(2);
                mxClient.content().download(serverName, mxcid, true).thenAccept(stream -> saveAvatarFile(stream, avatarPath));
            } else {
                LOGGER.warn("Wrong format: {}", mxcUrl);
            }
        });

    }

    private static void saveAvatarFile(InputStream stream, Path avatarPath) {
        try {
            if (!Files.exists(avatarPath)) {
                Files.createFile(avatarPath);
            }
            Files.write(avatarPath, stream.readAllBytes());
            LOGGER.info("Done");
        } catch (IOException e) {
            LOGGER.error("Unable to write avatar to file", e);
        }
    }
}
