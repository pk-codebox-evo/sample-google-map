/*
 * TODO Copyright
 */

package com.company.cubamapexample.PhotoLoader;

import java.util.UUID;

public interface PhotoLoaderMBean {
    String NAME = "cubamapexample_PhotoLoaderServiceMBean";

    byte[] getPhotoByUserId(UUID uuid);
}
