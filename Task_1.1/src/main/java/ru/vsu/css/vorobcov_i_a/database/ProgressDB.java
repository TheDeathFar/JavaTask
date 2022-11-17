package ru.vsu.css.vorobcov_i_a.database;

import ru.vsu.css.vorobcov_i_a.models.Currency;
import ru.vsu.css.vorobcov_i_a.models.Item;
import ru.vsu.css.vorobcov_i_a.models.Progress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProgressDB {
    private final Connection dbConnection = DBUtils.getConnection();

    private static final String INSERT_PROGRESS_SQL = "INSERT INTO progresses" +
            "  (id, player_id, resource_id, score, maxscore) VALUES " +
            " (?, ?, ?, ?, ?);";
    private static final String READ_PROGRESSES_SQL = "SELECT * FROM progresses;";
    private static final String READ_PROGRESSES_BY_PLAYER_ID_SQL = "SELECT * FROM progresses where player_id = ?;";
    private static final String UPDATE_PROGRESS_SQL = "UPDATE progresses SET (playerid, resourceid, score, maxscore) = (?, ?, ?, ?) where id = ?;";
    private static final String DELETE_PROGRESS_SQL = "DELETE FROM progresses WHERE id = ? cascade;";

    public void update(Progress item) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(UPDATE_PROGRESS_SQL);
        preparedStatement.setLong(1, item.getId());
        preparedStatement.setLong(2, item.getPlayerId());
        preparedStatement.setLong(3, item.getResourceId());
        preparedStatement.setInt(4, item.getScore());
        preparedStatement.setInt(5, item.getMaxScore());
        preparedStatement.executeUpdate();
    }

    public void deleteById(Long id) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(DELETE_PROGRESS_SQL);
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();
    }

    public void save(Progress progress) throws SQLException {
        PreparedStatement preparedStatement = dbConnection.prepareStatement(INSERT_PROGRESS_SQL);
        preparedStatement.setLong(1, progress.getId());
        preparedStatement.setLong(2, progress.getPlayerId());
        preparedStatement.setLong(3, progress.getResourceId());
        preparedStatement.setInt(4, progress.getScore());
        preparedStatement.setInt(5, progress.getMaxScore());
        preparedStatement.executeUpdate();
    }

    public void saveAll(Collection<Progress> items) throws SQLException {
        for(var s : items){
            save(s);
        }
    }

    public List<Progress> getAllByPlayerId(Long playerId) throws SQLException {
        List<Progress> answer = new ArrayList<>();
        PreparedStatement preparedStatement = dbConnection.prepareStatement(READ_PROGRESSES_BY_PLAYER_ID_SQL);
        preparedStatement.setLong(1, playerId);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        while(resultSet.next()){
            Progress progress = new Progress();
            progress.setId(resultSet.getLong(1));
            progress.setPlayerId(resultSet.getLong(2));
            progress.setScore(resultSet.getInt(4));
            progress.setMaxScore(resultSet.getInt(5));
            progress.setResourceId(resultSet.getLong(3));
            answer.add(progress);
        }
        return answer;
    }



}
