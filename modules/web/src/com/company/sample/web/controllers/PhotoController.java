package com.company.sample.web.controllers;

import com.company.sample.entity.Salesperson;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.SecurityContext;
import com.haulmont.cuba.security.app.LoginService;
import com.haulmont.cuba.security.global.UserSession;
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
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Spring MVC controller for loading images to display on map.
 */
@Controller
public class PhotoController {

    @Inject
    private LoginService loginService;

    @Inject
    private GlobalConfig globalConfig;

    @Inject
    private DataManager dataManager;

    @Inject
    private FileStorageService fileStorageService;

    private Logger log = LoggerFactory.getLogger(PhotoController.class);

    /**
     * HTTP endpoint for loading salesperson pictures.
     *
     * @param id        salesperson ID
     * @param version   salesperson version. It is not used in code but required for changing URL to always display
     *                  a new picture if it was changed.
     * @param response  response
     */
    @RequestMapping(value = "/getPhoto/{id}-{version}.png")
    public ResponseEntity getPhoto(@PathVariable String id,
                                   @SuppressWarnings("UnusedParameters") @PathVariable String version,
                                   HttpServletResponse response) throws IOException {
        return authenticated(response, () -> {
            byte[] bytes = null;

            // Load a salesperson photo from File Storage
            Salesperson salesperson = dataManager.load(
                    LoadContext.create(Salesperson.class).setId(UUID.fromString(id)).setView("salesperson-photo"));
            if (salesperson == null) {
                log.error("Salesperson {} not found", id);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
            if (salesperson.getPhoto() != null) {
                try {
                    bytes = fileStorageService.loadFile(salesperson.getPhoto());
                } catch (FileStorageException e) {
                    log.error("Error loading file", e);
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            if (bytes == null) {
                // If photo is not set, use a default image
                URL url = new URL(globalConfig.getDispatcherBaseUrl() + "/static/noIcon.png");
                BufferedImage defaultImage = ImageIO.read(url);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(defaultImage, "png", baos);
                baos.flush();
                bytes = baos.toByteArray();
                baos.close();
            }
            return new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);
        });
    }

    /**
     * Execute code on behalf of anonymous user.
     *
     * @param response  response object
     * @param callable  code to execute
     * @param <T>       type of return value
     * @return          result
     * @throws IOException  propagated from HttpServletResponse methods
     */
    private <T> T authenticated(HttpServletResponse response, Callable<T> callable) throws IOException {
        UserSession anonymousUserSession = loginService.getSession(globalConfig.getAnonymousSessionId());
        if (anonymousUserSession == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        AppContext.setSecurityContext(new SecurityContext(anonymousUserSession));
        try {
            return callable.call();
        } catch (Exception e) {
            log.error("Error executing request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        } finally {
            AppContext.setSecurityContext(null);
        }
    }
}
