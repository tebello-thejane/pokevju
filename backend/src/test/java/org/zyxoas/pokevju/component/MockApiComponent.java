package org.zyxoas.pokevju.component;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("test")
class MockApiComponent implements ApiComponent {

    private static final Map<String, Integer> pokeIdMap = new HashMap<>();

    @Override
    public byte[] getSprite(Integer id) {
        return "getSprite".getBytes();
    }

    @Override
    public Map<String, Integer> getIdMap() {
        return pokeIdMap;
    }
}