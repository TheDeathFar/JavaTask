package ru.vsu.css.vorobcov_i_a.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Currency {
    private Long id;
    private Long resourceId;
    private String name;
    private int count;


    @Override
    public String toString() {
        return "Currencies{" +
                "id=" + id +
                ", resourceId=" + resourceId +
                ", name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
