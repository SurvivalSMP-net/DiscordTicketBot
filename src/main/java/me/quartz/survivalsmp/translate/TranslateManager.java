package me.quartz.survivalsmp.translate;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TranslateManager {

    private final HashMap<String, String> translated = new HashMap<>();
    private static final String TRANSLATE_API_URL = "https://translation.googleapis.com/language/translate/v2?key=AIzaSyCe5jCfzlaq8yZ_rwLJRwV-icRw-OWU_Qg";

    public String translateText(String text, String targetLanguage) {
        String key = targetLanguage + "-" + text.toLowerCase().replace(" ", "-");
        if (translated.containsKey(key)) {
            return translated.get(key);
        } else {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost request = new HttpPost(TRANSLATE_API_URL);

            try {
                JSONObject json = new JSONObject();
                json.put("q", text);
                json.put("target", targetLanguage);

                StringEntity entity = new StringEntity(json.toString());
                request.setEntity(entity);
                request.setHeader("Content-Type", "application/json");

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    HttpEntity responseEntity = response.getEntity();

                    if (responseEntity != null) {
                        String responseContent = EntityUtils.toString(responseEntity);
                        JSONObject jsonResponse = new JSONObject(responseContent);
                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONArray translations = data.getJSONArray("translations");
                        JSONObject translation = translations.getJSONObject(0);
                        String translatedText = StringEscapeUtils.unescapeHtml(translation.getString("translatedText"));

                        translated.put(key, translatedText);

                        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                        scheduler.schedule(() -> {
                            translated.remove(key);
                        }, 300, TimeUnit.SECONDS);
                        scheduler.shutdown();

                        return translatedText;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }
}
