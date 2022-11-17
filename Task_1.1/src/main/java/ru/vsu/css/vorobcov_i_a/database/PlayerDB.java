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
    private static final String READ_ALL_ID_PLAYERS = "SELECT player_id FROM PLAYER";
    private static final String READ_PLAYER_BY_ID = "SELECT * FROM PLAYERS";
    private static final String UPDATE_PLAYERS_SQL = "UPDATE players SET nickname = ? where playerid = ?;";
    private static final String DELETE_PLAYER_SQL = "DELETE FROM players WHERE playerId = ? cascade;";
    private static final String DELETE_FROM_PLAYER_ITEM_MAP_SQL = "DELETE FROM player_item_map where playerId = ? and itemid = ?;";
    private static final String DELETE_FROM_PLAYER_CURRENCY_MAP_SQL = "DELETE FROM player_currency_map where playerid = ? and currencyid = ?;";

    /*public void update(Player newPlayer) throws SQLException {
        Player oldPlayer = readAndConvert(dbConnection, newPlayer.getId());
        Map<String, Currency> oldPlayerCurrencies = oldPlayer.getCurrencies();
        Map<String, Item> oldPlayerItems = oldPlayer.getItems();
        Map<Long, Progress> oldPlayerProgresses = oldPlayer.getProgresses().stream().collect(Collectors.toMap(Progress::getId, Function.identity()));
        //проверяем существующие
        for(var s : newPlayer.getItems().entrySet()){
            if(oldPlayerItems.containsKey(s.getKey())){
                ItemDB.update(dbConnection, s.getValue());
                oldPlayerItems.remove(s.getKey());
            }else{
                ItemDB.save(dbConnection, s.getValue());
            }
        }
        for(var s : oldPlayerItems.entrySet()){
            deleteFromPlayerItemMap(dbConnection, newPlayer.getPlayerId(), s.getKey());
        }
        for(var s : newPlayer.getCurrencies().entrySet()){
            if(oldPlayerCurrencies.containsKey(s.getKey())){
                CurrencyDB.update(dbConnection, s.getValue());
                oldPlayerCurrencies.remove(s.getKey());
            }else{
                CurrencyDB.save(dbConnection, s.getValue());
            }
        }
        for(var s : oldPlayerCurrencies.entrySet()){
            deleteFromPlayerCurrencyMap(dbConnection, newPlayer.getPlayerId(), s.getKey());
        }
        for(var s : newPlayer.getProgresses()){
            if(oldPlayerCurrencies.containsKey(s.getId())){
                ProgressDB.update(dbConnection, s);
                oldPlayerProgresses.remove(s.getId());
            }else{
                ProgressDB.save(dbConnection, s);
            }
        }
        for(var s : oldPlayerProgresses.entrySet()){
            ProgressDB.deleteById(s.getKey());
        }

        PreparedStatement preparedStatement = dbConnection.prepareStatement(UPDATE_PLAYERS_SQL);
        preparedStatement.setString(1, newPlayer.getNickname());
        preparedStatement.setLong(2, newPlayer.getPlayerId());
        preparedStatement.executeUpdate();
    }

    public void deleteFromPlayerItemMap(Connection connection, Long playerId, String ItemId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FROM_PLAYER_ITEM_MAP_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.setString(2, ItemId);
        preparedStatement.executeUpdate();
    }

    public void deleteFromPlayerCurrencyMap(Connection connection, Long playerId, String currencyId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FROM_PLAYER_CURRENCY_MAP_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.setString(2, currencyId);
        preparedStatement.executeUpdate();
    }
     */

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
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        resultSet.next();
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
