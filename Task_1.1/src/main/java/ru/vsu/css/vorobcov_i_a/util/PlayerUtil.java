package ru.vsu.css.vorobcov_i_a.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @SneakyThrows
    public static List<Player> readAllPlayersFromString(String playerString){
        return  objectMapper.readValue(playerString, new TypeReference<List<Player>>() {});
    }

    @SneakyThrows
    public static Player readPlayerFromString(String playerString){
        return objectMapper.readValue(playerString, new TypeReference<List<Player>>() {});
    }

    @SneakyThrows
    public static List<Player> readPlayersFromFile(String path){
        InputStream resourceAsStream = PlayerUtil.class.getClassLoader().getResourceAsStream(path);
        return objectMapper.readValue(resourceAsStream, new TypeReference<List<Player>>() {});
    }

    @SneakyThrows
    public static Player readOnePlayerFromFile(String path){
        InputStream resourceAsStream = PlayerUtil.class.getClassLoader().getResourceAsStream(path);
        return objectMapper.readValue(resourceAsStream, Player.class);
    }

    @SneakyThrows
    public static String convertToString(Player player){
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(player);
    }
    @SneakyThrows
    public static String convertToString(List<Player> player){
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(player);
    }


    @SneakyThrows
    public static Map<Long, Player> readFromFileToMap(String path){
        InputStream resourceAsStream = PlayerUtil.class.getClassLoader().getResourceAsStream(path);
        Player[] players = objectMapper.readValue(resourceAsStream, Player[].class);
        return Arrays.stream(players).collect(Collectors.toMap(Player::getPlayerId, Function.identity()));
    }

}
