package co.com.manager.usecase.consent;

public record PrivacyPolicyConfig(String url, String version, String messageTemplate) {

    public String renderNotice() {
        return messageTemplate.replace("{url}", url);
    }
}
