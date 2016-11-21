package com.company.sample.service;

import java.util.UUID;

public interface PhotoLoaderService {
    String NAME = "sample_PhotoLoaderService";

    byte[] getPhotoByUserId(UUID uuid);
}
