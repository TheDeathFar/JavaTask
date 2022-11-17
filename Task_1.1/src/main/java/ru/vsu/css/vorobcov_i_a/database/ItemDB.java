package ru.vsu.css.vorobcov_i_a.database;

import ru.vsu.css.vorobcov_i_a.models.Currency;
import ru.vsu.css.vorobcov_i_a.models.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemDB {
    private final Connection dbConnection = DBUtils.getConnection();

    private static final String INSERT_ITEMS_SQL = "INSERT INTO items" +
            "  (id, count, level, resource_id) VALUES " +
            " (?, ?, ?, ?);";
    private static final String READ_ITEMS_SQL = "SELECT * FROM items;";
    private static final String READ_ALL_ITEMS_BY_PLAYER_ID = "SELECT * FROM items " +
            "join players_items pi on items.id = pi.item_id " +
            "WHERE player_id = ?;";
    private static final String UPDATE_ITEM_SQL = "UPDATE items SET (count, level, resourceid) = (?, ?, ?) where id = ?;";
    private static final String DELETE_ITEM_SQL = "DELETE FROM items WHERE id = ? cascade;";


    public void update(Item item) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(UPDATE_ITEM_SQL);
        preparedStatement.setLong(1, item.getId());
        preparedStatement.setLong(2, item.getResourceId());
        preparedStatement.setInt(3, item.getCount());
        preparedStatement.setInt(4, item.getLevel());
        preparedStatement.executeUpdate();
    }
    public void deleteById(Long id) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(DELETE_ITEM_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();
    }

    public void save(Item item) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(INSERT_ITEMS_SQL);
        preparedStatement.setLong(1, item.getId());
        preparedStatement.setInt(2, item.getCount());
        preparedStatement.setInt(3, item.getLevel());
        preparedStatement.setLong(4, item.getResourceId());
        preparedStatement.executeUpdate();
    }

    public List<Item> readByPlayerId(Long id) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(READ_ALL_ITEMS_BY_PLAYER_ID);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        List<Item> answer = new ArrayList<>();
        while(resultSet.next()){
            Item item = new Item();
            item.setId(resultSet.getLong(1));
            item.setCount(resultSet.getInt(3));
            item.setLevel(resultSet.getInt(4));
            item.setResourceId(resultSet.getLong(2));
            answer.add(item);
        }
        return answer;
    }

    public void saveAll(Collection<Item> items) throws SQLException {
        for(var s : items){
            save(s);
        }
    }

}
