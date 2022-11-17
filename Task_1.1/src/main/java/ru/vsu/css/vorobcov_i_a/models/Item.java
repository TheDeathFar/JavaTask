package ru.vsu.css.vorobcov_i_a.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private Long id;
    private Long resourceId;
    private int count;
    private Integer level;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", resourceId=" + resourceId +
                ", count=" + count +
                ", level=" + level +
                '}';
    }
}
