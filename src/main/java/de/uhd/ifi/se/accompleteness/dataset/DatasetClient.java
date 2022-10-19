package de.uhd.ifi.se.accompleteness.dataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DatasetClient {
    private final OkHttpClient client = new OkHttpClient();
    private final String baseUrl = System.getenv("UVL_STORAGE_BASE_URL");

    public List<String> getDataset() throws Exception {
        List<String> result = new ArrayList<>();
        Request request = new Request.Builder()
                .url(String.format("%s/hitec/repository/concepts/dataset/name/dataset", baseUrl))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);

            JsonObject jsonRequest = new Gson().fromJson(response.body().string(), JsonObject.class);
            JsonArray documents = jsonRequest.getAsJsonObject().get("documents").getAsJsonArray();
            for (JsonElement jsonElement : documents) {
                result.add(jsonElement.getAsString());
            }
        }
        return result;
    }

}