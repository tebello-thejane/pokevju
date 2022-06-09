package org.zyxoas.pokevju.component;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

public interface ApiComponent {
    @Builder
    @Getter
    class TaggedImage {
        final byte[] contents;
        final String mediaType;
    }

    TaggedImage getSprite(Integer id);

    Map<String, Integer> getIdMap();
}
