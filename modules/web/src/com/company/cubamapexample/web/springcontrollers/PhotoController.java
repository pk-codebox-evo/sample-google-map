/*
 * TODO Copyright
 */

package com.company.cubamapexample.web.springcontrollers;

import com.company.cubamapexample.service.PhotoLoaderService;
import com.company.cubamapexample.web.system.SystemUtils;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.GlobalConfig;
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
import java.util.UUID;

@Controller
public class PhotoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/getPhoto/{id}-{version}.png")
    public ResponseEntity getPhoto(@PathVariable String id, @PathVariable String version) {
        final ResponseEntity[] imageInByte = new ResponseEntity[1];
        SystemUtils.doAsPrivilegedUser(() -> {
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
        String urlString = config.getWebAppUrl();
        BufferedImage incognitoImage;
        try {
            URL url = new URL(urlString + "/dispatch/static/noIcon.png");
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
}


