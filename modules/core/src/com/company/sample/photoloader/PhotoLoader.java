package com.company.sample.photoloader;

import com.company.sample.entity.Salesperson;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Component(PhotoLoaderMBean.NAME)
public class PhotoLoader implements PhotoLoaderMBean {
    private static final Logger log = LoggerFactory.getLogger(PhotoLoader.class);

    @Inject
    private FileStorageService fileStorageService;
    @Inject
    protected Persistence persistence;

    @Override
    public byte[] getPhotoByUserId(UUID uuid) {
        FileDescriptor fileDescriptor = getPhotoFileDescriptorByUserId(uuid);
        byte[] photo = null;
        if (fileDescriptor != null) {
            try {
                photo = fileStorageService.loadFile(fileDescriptor);
            } catch (FileStorageException e) {
                log.warn("Can not load photo", e);
            }
        }
        return photo;
    }

    private FileDescriptor getPhotoFileDescriptorByUserId(UUID uuid) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            Salesperson person = em.find(Salesperson.class, uuid);
            FileDescriptor fd = null;
            if (person != null) {
                fd = person.getPhoto();
            }
            tx.commit();
            return fd;
        }
    }
}
