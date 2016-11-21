package com.company.sample.service;

import com.company.sample.photoloader.PhotoLoaderMBean;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;

@Service(PhotoLoaderService.NAME)
public class PhotoLoaderServiceBean implements PhotoLoaderService {
    @Inject
    protected PhotoLoaderMBean photoLoaderMBean;

    @Override
    public byte[] getPhotoByUserId(UUID uuid) {
        return photoLoaderMBean.getPhotoByUserId(uuid);
    }
}
