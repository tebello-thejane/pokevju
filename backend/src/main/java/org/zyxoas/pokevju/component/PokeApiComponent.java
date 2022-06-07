package org.zyxoas.pokevju.component;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Component
@Profile("!test")
@Slf4j
public class PokeApiComponent implements ApiComponent {
    @Value("${pokevju.homesprites.url.format}")
    private String spriteUrlFormat;

    @Value("${pokevju.homesprites.locations}")
    private List<String> spriteUrlLocations;

    @Value("${pokevju.homesprites.extensions}")
    private List<String> spriteUrlExtensions;

    @Value("${pokevju.pokeapi.url}")
    private String pokeapiUrl;

    private static final Map<String, Integer> pokeIdMap = new HashMap<>();

    //To mitigate the possibility of being throttled by CloudFlare
    private static final String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1";

    @Override
    public byte[] getSprite(Integer id) {

        for (int i = 0; i <= spriteUrlLocations.size(); i++) {

            final String theUrl = String.format(spriteUrlFormat, spriteUrlLocations.get(i), id, spriteUrlExtensions.get(i));
            try {
                return WebClient
                        .create(theUrl)
                        .get()
                        .header("User-Agent", userAgent)
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .doOnError(throwable -> {
                            throw new RuntimeException(throwable);
                        })
                        .block();
            } catch (RuntimeException ex) {
                log.warn("Error encountered when attempting " + theUrl);
            }
        }

        throw new RuntimeException("All sprite URL's seem to have failed.");
    }

    @Override
    public Map<String, Integer> getIdMap() {
        return pokeIdMap;
    }

    @PostConstruct
    private void fetchNames() {
        log.info(spriteUrlLocations.toString());
        final int limit = 2000;

        //Do this asynchronously (fire & forget) so that it does not block booting
        Executors.newSingleThreadExecutor().submit(() -> {

            log.info("Fetching Pokémon names and ID's the first time.");

            final String apiResponse = WebClient
                    .create(pokeapiUrl + "/pokemon/?offset=0&limit=" + limit)
                    .get()
                    .header("User-Agent", userAgent)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            final JSONArray pokamap = JsonPath
                    .parse(apiResponse)
                    .read("$.results.*");

            pokamap.forEach(o -> {
                final Map<String, String> input = (Map<String, String>) o;

                String name = "", url = "";

                for (final Map.Entry<String, String> entry : input.entrySet()) {
                    switch (entry.getKey()) {
                        case "name":
                            name = entry.getValue();
                            break;
                        case "url":
                            url = entry.getValue();
                            break;
                        default:
                            throw new RuntimeException("Unexpected tag: " + entry.getKey());
                    }
                }

                if (!name.contains("-")) {
                    final String[] urlSplit = url.split("/");
                    final int id = Integer.parseInt(urlSplit[urlSplit.length - 1]);

                    pokeIdMap.put(name, id);
                }
            });

            log.info("Done fetching {} Pokémon names and ID's.", pokeIdMap.size());
        });
    }
}
