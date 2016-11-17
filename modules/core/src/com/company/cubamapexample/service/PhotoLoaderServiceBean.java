/*
 * TODO Copyright
 */

package com.company.cubamapexample.service;

import com.company.cubamapexample.photoloader.PhotoLoaderMBean;
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
