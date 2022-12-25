package ru.vsu.css.vorobcov_i_a.database;

import ru.vsu.css.vorobcov_i_a.models.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CurrencyDB {
    private final Connection dbConnection = DBUtils.getConnection();

    private static final String INSERT_CURRENCY_SQL = "INSERT INTO currencies" +
            "  (id, resource_id, name, count) VALUES " +
            " (?, ?, ?, ?);";
    private static final String READ_CURRENCIES_BY_ID_SQL = "SELECT * FROM currencies where id = ?;";
    private static final String READ_ALL_CURRENCIES_BY_PLAYER_ID = "SELECT currencies.* FROM currencies " +
            "join players_currencies on currencies.id = players_currencies.currency_id " +
            "WHERE player_id = ?;";
    private static final String UPDATE_CURRENCY_SQL = "UPDATE currencies SET (resource_id, name, count) = (?, ?, ?) where id = ?;";
    private static final String DELETE_CURRENCY_SQL = "DELETE FROM currencies WHERE id = ? ;";

    public void update(Currency item) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(UPDATE_CURRENCY_SQL);
        preparedStatement.setLong(1, item.getId());
        preparedStatement.setLong(2, item.getResourceId());
        preparedStatement.setString(3, item.getName());
        preparedStatement.setInt(4, item.getCount());
        preparedStatement.executeUpdate();
    }

    public void deleteById(Long id) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(DELETE_CURRENCY_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();
    }

    public void save(Currency currency) throws SQLException {
            PreparedStatement preparedStatement = dbConnection.prepareStatement(INSERT_CURRENCY_SQL);
            preparedStatement.setLong(1, currency.getId());
            preparedStatement.setLong(2, currency.getResourceId());
            preparedStatement.setString(3, currency.getName());
            preparedStatement.setInt(4, currency.getCount());
            preparedStatement.executeUpdate();
    }

    public Currency readById(Long id) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(READ_CURRENCIES_BY_ID_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        Currency currency = null;
        if(resultSet.next()){
            currency = new Currency();
            currency.setId(resultSet.getLong(1));
            currency.setCount(resultSet.getInt(4));
            currency.setName(resultSet.getString(3));
            currency.setResourceId(resultSet.getLong(2));
        }
        return currency;
    }

    public List<Currency> readByPlayerId(Long id) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(READ_ALL_CURRENCIES_BY_PLAYER_ID);
        preparedStatement.setLong(1, id);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        List<Currency> answer = new ArrayList<>();
        while(resultSet.next()){
            Currency currency = new Currency();
            currency.setId(resultSet.getLong(1));
            currency.setCount(resultSet.getInt(4));
            currency.setName(resultSet.getString(3));
            currency.setResourceId(resultSet.getLong(2));
            answer.add(currency);
        }
        return answer;
    }


    public void saveAll(Collection<Currency> currencyList) throws SQLException {
        for(var s : currencyList){
            save(s);
        }
    }








}
