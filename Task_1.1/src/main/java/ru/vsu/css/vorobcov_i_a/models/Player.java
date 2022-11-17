package ru.vsu.css.vorobcov_i_a.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class Player {
    private Long playerId;
    private String nickname;
    private List<Progress> progresses;
    private Map<String, Currency> currencies;
    private Map<String, Item> items;

    public Long getId() {
        return playerId;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", nickname='" + nickname + '\'' +
                ", progresses=" + progresses +
                ", currencies=" + currencies +
                ", items=" + items +
                '}';
    }
}
