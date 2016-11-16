/*
 * TODO Copyright
 */

package com.company.cubamapexample.PhotoLoader;

import com.company.cubamapexample.entity.SalesPerson;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Component(PhotoLoaderMBean.NAME)
public class PhotoLoader implements PhotoLoaderMBean {
    private static final Logger log = LoggerFactory.getLogger(PhotoLoader.class);
    @Inject
    private FileStorageService fileStorageService;
    @Inject
    protected Persistence persistence;
    @Inject
    protected Configuration configuration;


    @Override
    public byte[] getPhotoByUserId(UUID uuid) {
        FileDescriptor fileDescriptor = findFileDescriptorByUUID(uuid);
        byte[] photo = null;
        if (fileDescriptor != null) {
            try {
                photo = fileStorageService.loadFile(fileDescriptor);
            } catch (FileStorageException e) {
                log.warn("Can not load photo", e);
            }
        } else {
            photo = getIncognitoImage();
        }
        return photo;
    }

    private byte[] getIncognitoImage() {
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

    private FileDescriptor findFileDescriptorByUUID(UUID uuid) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<SalesPerson> q = em.createQuery("select u from cubamapexample$SalesPerson u where u.id = :uuid", SalesPerson.class)
                    .setParameter("uuid", uuid);
            q.setViewName("salesPerson-full");
            SalesPerson person = q.getFirstResult();
            FileDescriptor fd = null;
            if (person != null) {
                fd = person.getPhoto();
            }
            tx.commit();
            return fd;
        }
    }
}
