package com.company.sample.web.controllers;

import com.company.sample.service.PhotoLoaderService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.LoginService;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.auth.WebAuthConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;

@Controller
public class PhotoController {

    private static Logger log = LoggerFactory.getLogger(PhotoController.class);

    @RequestMapping(value = "/getPhoto/{id}-{version}.png")
    public ResponseEntity getPhoto(@PathVariable String id, @PathVariable String version) {
        final ResponseEntity[] imageInByte = new ResponseEntity[1];
        doAsPrivilegedUser(() -> {
            PhotoLoaderService photoLoaderService = AppBeans.get(PhotoLoaderService.NAME);
            byte[] bytes = photoLoaderService.getPhotoByUserId(UUID.fromString(id));
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            if (bytes == null) {
                bytes = getIncognitoImage();
            }
            imageInByte[0] = new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);
        });
        return imageInByte[0];
    }

    private byte[] getIncognitoImage() {
        Configuration configuration = AppBeans.get(Configuration.NAME);
        GlobalConfig config = configuration.getConfig(GlobalConfig.class);
        BufferedImage incognitoImage;
        try {
            URL url = new URL(config.getDispatcherBaseUrl() + "/static/noIcon.png");
            incognitoImage = ImageIO.read(url);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(incognitoImage, "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException e) {
            log.warn("Can not load incognito image", e);
        }
        return new byte[0];
    }

    public static void doAsPrivilegedUser(Runnable runnable) {
        getAsPrivilegedUser(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T getAsPrivilegedUser(Callable<T> callable) {
        SecurityContext sc = AppContext.getSecurityContext();
        T result;
        try {
            LoginService loginService = AppBeans.get(LoginService.NAME);
            Configuration configuration = AppBeans.get(Configuration.NAME);
            WebAuthConfig webConfig = configuration.getConfig(WebAuthConfig.class);
            String trustedPassword = webConfig.getTrustedClientPassword();
            GlobalConfig globalConfig = configuration.getConfig(GlobalConfig.class);
            Locale systemLocale = globalConfig.getAvailableLocales().values().iterator().next();

            UserSession session = loginService.loginTrusted("anonymous", trustedPassword, systemLocale);
            AppContext.setSecurityContext(new SecurityContext(session));

            try {
                result = callable.call();
            } finally {
                loginService.logout();
            }
        } catch (Exception e) {
            log.debug("Unable to execute privileged action", e);

            throw new PrivilegedActionException(e);
        } finally {
            AppContext.setSecurityContext(sc);
        }
        return result;
    }

}


