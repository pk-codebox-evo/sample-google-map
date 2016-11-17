/*
 * TODO Copyright
 */

package com.company.cubamapexample.photoloader;

import java.util.UUID;

public interface PhotoLoaderMBean {
    String NAME = "cubamapexample_PhotoLoaderServiceMBean";

    byte[] getPhotoByUserId(UUID uuid);
}
