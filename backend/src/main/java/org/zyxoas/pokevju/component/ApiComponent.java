package org.zyxoas.pokevju.component;

import java.util.Map;

public interface ApiComponent {
    byte[] getSprite(Integer id);
    Map<String, Integer> getIdMap();
}
