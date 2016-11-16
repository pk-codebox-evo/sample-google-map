/*
 * TODO Copyright
 */

package com.company.cubamapexample.service;

import java.util.UUID;

public interface PhotoLoaderService {
    String NAME = "cubamapexample_PhotoLoaderService";


    byte[] getPhotoByUserId(UUID uuid);
}
