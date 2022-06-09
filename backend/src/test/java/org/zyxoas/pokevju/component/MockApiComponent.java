package org.zyxoas.pokevju.component;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("test")
class MockApiComponent implements ApiComponent {

    private static final Map<String, Integer> pokeIdMap = new HashMap<>();

    @Override
    public TaggedImage getSprite(Integer id) {
        return TaggedImage.builder()
                .mediaType("image/*")
                .contents("getSprite".getBytes())
                .build();
    }

    @Override
    public Map<String, Integer> getIdMap() {
        return pokeIdMap;
    }
}