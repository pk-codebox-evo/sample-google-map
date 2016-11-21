package com.company.sample.photoloader;

import java.util.UUID;

public interface PhotoLoaderMBean {
    String NAME = "sample_PhotoLoaderServiceMBean";

    byte[] getPhotoByUserId(UUID uuid);
}
