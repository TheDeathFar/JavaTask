package ru.vsu.css.vorobcov_i_a.database;

import ru.vsu.css.vorobcov_i_a.models.Currency;
import ru.vsu.css.vorobcov_i_a.models.Item;
import ru.vsu.css.vorobcov_i_a.models.Player;
import ru.vsu.css.vorobcov_i_a.models.Progress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerDB {
    private final Connection dbConnection;
    private final ProgressDB progressDB;
    private final CurrencyDB currencyDB;
    private final ItemDB itemsDB;
    
    public PlayerDB(){
        progressDB = new ProgressDB();
        currencyDB = new CurrencyDB();
        itemsDB = new ItemDB();
        dbConnection = DBUtils.getConnection();
    }

    private static final String INSERT_PLAYERS = "INSERT INTO players(player_id, nickname) VALUES (?, ?)";
    private static final String INSERT_PLAYERS_CURRENCIES = "INSERT INTO players_currencies(player_id, currency_id) VALUES (?, ?)";
    private static final String INSERT_PLAYERS_ITEM_MAP = "INSERT INTO players_items(player_id, item_id) values(?, ?);";
    private static final String READ_ALL_ID_PLAYERS = "SELECT player_id FROM PLAYERS";
    private static final String READ_PLAYER_BY_ID = "SELECT * FROM PLAYERS where player_id = ?";
    private static final String UPDATE_PLAYERS_SQL = "UPDATE players SET nickname = ? where player_id = ?;";
    private static final String DELETE_PLAYER_SQL = "DELETE FROM players WHERE player_id = ?;";
    private static final String DELETE_ALL_FROM_PLAYER_ITEM_MAP_SQL = "DELETE FROM players_items where player_id = ?;";
    private static final String DELETE_ALL_FROM_PLAYER_CURRENCY_MAP_SQL = "DELETE FROM players_currencies where player_id = ?;";
    private static final String TRUNCATE_ALL_CASCADE = "truncate table currencies cascade; " +
            "truncate table items cascade; " +
            "truncate table players cascade;";
    private static final String IS_ITEM_EXISTS_SQL = "SELECT EXISTS(SELECT * FROM items WHERE id = ?);";
    private static final String IS_CURRENCY_EXISTS_SQL = "SELECT EXISTS(SELECT * FROM currencies WHERE id = ?);";

    public void update(Long id, Player newPlayer) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(UPDATE_PLAYERS_SQL);
        preparedStatement.setString(1, newPlayer.getNickname());
        preparedStatement.setLong(2, id);
        preparedStatement.executeUpdate();
        deleteAllPlayerItemsByPlayerId(id);
        deleteAllPlayerCurrenciesByPlayerId(id);
        progressDB.deleteByPlayerId(id);
        for(var curr : newPlayer.getCurrencies().values()){
            if(!isCurrencyExisits(curr.getId()))
                currencyDB.save(curr);
            else
                
            saveCurrenciesPlayerMap(newPlayer.getPlayerId(), curr.getId());

        }
        for(var item : newPlayer.getItems().values()) {
            if(!isItemExisits(item.getId()))
                itemsDB.save(item);
            saveItemsPlayerMap(newPlayer.getPlayerId(), item.getId());
        }
        progressDB.saveAll(newPlayer.getProgresses());
    }

    public void deleteALl() throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(TRUNCATE_ALL_CASCADE);
        preparedStatement.executeUpdate();
    }

    private boolean isItemExisits(Long id) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(IS_ITEM_EXISTS_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        resultSet.next();
        return resultSet.getBoolean(1);
    }

    private boolean isCurrencyExisits(Long id) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(IS_CURRENCY_EXISTS_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        resultSet.next();
        return resultSet.getBoolean(1);
    }
    public void delete(Long id) throws SQLException {
        deleteAllPlayerCurrenciesByPlayerId(id);
        deleteAllPlayerItemsByPlayerId(id);
        progressDB.deleteByPlayerId(id);
        PreparedStatement preparedStatement = dbConnection.prepareStatement(DELETE_PLAYER_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();
    }

    private void deleteAllPlayerItemsByPlayerId(Long playerId) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(DELETE_ALL_FROM_PLAYER_ITEM_MAP_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.executeUpdate();
    }

    private void deleteAllPlayerCurrenciesByPlayerId(Long playerId) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(DELETE_ALL_FROM_PLAYER_CURRENCY_MAP_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.executeUpdate();
    }

    public void save(Player player) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(INSERT_PLAYERS);
        preparedStatement.setLong(1, player.getPlayerId());
        preparedStatement.setString(2, player.getNickname());
        preparedStatement.executeUpdate();
        for(var curr : player.getCurrencies().values()){
            currencyDB.save(curr);
            saveCurrenciesPlayerMap(player.getPlayerId(), curr.getId());

        }
        for(var item : player.getItems().values()) {
            itemsDB.save(item);
            saveItemsPlayerMap(player.getPlayerId(), item.getId());
        }
        progressDB.saveAll(player.getProgresses());
    }


    public List<Player> readAll() throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(READ_ALL_ID_PLAYERS);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        List<Player> answer = new ArrayList<>();
        while(resultSet.next()){
            Long id = resultSet.getLong(1);
            answer.add(readById(id));
        }
        return answer;
    }

    public Player readById(Long id) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(READ_PLAYER_BY_ID);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        if(resultSet.next()){
            Player player = new Player();
            player.setPlayerId(resultSet.getLong(1));
            player.setNickname(resultSet.getString(2));
            Map<String, Currency> playerCurrencies = new HashMap<>();
            currencyDB.readByPlayerId(id).forEach(currency -> {
                playerCurrencies.put(String.valueOf(currency.getId()), currency);
            });
            player.setCurrencies(playerCurrencies);
            Map<String, Item> playerItems = new HashMap<>();
            itemsDB.readByPlayerId(id).forEach(item -> {
                playerItems.put(String.valueOf(item.getId()), item);
            });
            player.setItems(playerItems);
            player.setProgresses(progressDB.getAllByPlayerId(id));
            return player;
        }
        return null;
    }

    public void saveAll(Collection<Player> players) throws SQLException {
        for(var s : players){
            save(s);
        }
    }

    private void saveCurrenciesPlayerMap(Long playerId, Long currencyId) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(INSERT_PLAYERS_CURRENCIES);
        preparedStatement.setLong(1, playerId);
        preparedStatement.setLong(2, currencyId);
        preparedStatement.executeUpdate();
    }

    private void saveItemsPlayerMap(Long playerId, Long itemId) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(INSERT_PLAYERS_ITEM_MAP);
        preparedStatement.setLong(1, playerId);
        preparedStatement.setLong(2, itemId);
        preparedStatement.executeUpdate();
    }
}
