package ru.vsu.css.vorobcov_i_a.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import ru.vsu.css.vorobcov_i_a.models.Player;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerUtil {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static List<Player> readFromFile(String path){
        InputStream resourceAsStream = PlayerUtil.class.getClassLoader().getResourceAsStream(path);
        return objectMapper.readValue(resourceAsStream, new TypeReference<>() {});
    }

    @SneakyThrows
    public static String convertToString(Player player){
        return objectMapper.writer().writeValueAsString(player);
    }
    @SneakyThrows
    public static String convertToString(List<Player> player){
        return objectMapper.writer().writeValueAsString(player);
    }


    @SneakyThrows
    public static Map<Long, Player> readFromFileToMap(String path){
        InputStream resourceAsStream = PlayerUtil.class.getClassLoader().getResourceAsStream(path);
        Player[] players = objectMapper.readValue(resourceAsStream, Player[].class);
        return Arrays.stream(players).collect(Collectors.toMap(Player::getPlayerId, Function.identity()));
    }

}
