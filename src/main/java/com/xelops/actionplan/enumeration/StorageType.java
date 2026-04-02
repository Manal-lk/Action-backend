package com.xelops.actionplan.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StorageType {

    LOCAL_FS("Local File System"),
    S3("Amazon S3"),
    AZURE_BLOB("Azure Blob Storage"),
    GOOGLE_CLOUD_STORAGE("Google Cloud Storage"),
    OTHER("Other");

    private String label;
}
