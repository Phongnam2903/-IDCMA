package com.example.idcma_project_prm392.model;

import com.google.gson.annotations.SerializedName;

public class ShareResponse {
    @SerializedName ("shareable_link")
    private String shareableLink;

    public String getShareableLink() {
        return shareableLink;
    }
}
