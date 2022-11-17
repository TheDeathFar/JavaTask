package ru.vsu.css.vorobcov_i_a.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Progress {
    private Long id;
    private Long playerId;
    private Long resourceId;
    private int score;
    private int maxScore;


    @Override
    public String toString() {
        return "Progresses{" +
                "id=" + id +
                ", playerId=" + playerId;
    }
}
