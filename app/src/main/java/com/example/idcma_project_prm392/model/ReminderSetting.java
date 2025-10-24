package com.example.idcma_project_prm392.model;

public class ReminderSetting {
    private String certificateId;
    private boolean enabled;
    // lead time in days before expiry to remind (e.g., 30, 7, 1)
    private int daysBeforeExpiry;
    private ReminderChannel channel;

    public ReminderSetting() {
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDaysBeforeExpiry() {
        return daysBeforeExpiry;
    }

    public void setDaysBeforeExpiry(int daysBeforeExpiry) {
        this.daysBeforeExpiry = daysBeforeExpiry;
    }

    public ReminderChannel getChannel() {
        return channel;
    }

    public void setChannel(ReminderChannel channel) {
        this.channel = channel;
    }
}
