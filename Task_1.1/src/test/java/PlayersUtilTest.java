import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import ru.vsu.css.vorobcov_i_a.util.PlayerUtil;
import ru.vsu.css.vorobcov_i_a.database.PlayerDB;
import ru.vsu.css.vorobcov_i_a.models.Player;

import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PlayersUtilTest {

    private PlayerDB playerDB;


    @Before
    public void setUp(){
        playerDB = new PlayerDB();
    }


    @Test
    public void readTest(){
        List<Player> allPlayers = PlayerUtil.readPlayersFromFile("players.json");
        assertEquals(10_000, allPlayers.size());
    }

    @Test
    public void readToMapTest(){
        Map<Long, Player> allPlayers = PlayerUtil.readFromFileToMap("players.json");
        assertEquals(10_000, allPlayers.size());
    }


    @Test
    public void saveDataBaseTest() throws SQLException {
        List<Player> readPlayers = PlayerUtil.readPlayersFromFile("players.json");
        //take save first player
        PlayerDB playerDB = new PlayerDB();
        playerDB.save(readPlayers.get(0));
    }

    @Test
    public void saveAllPlayersTest() throws SQLException {
        List<Player> readPlayers = PlayerUtil.readPlayersFromFile("players.json");
        PlayerDB playerDB = new PlayerDB();
        playerDB.saveAll(readPlayers);
    }


    @Test
    public void readDataBaseTest() throws SQLException {
        List<Player> readPlayers = PlayerUtil.readPlayersFromFile("players.json");
        Player player = readPlayers.get(0);
        playerDB.save(player);
        Player playerFromDb = playerDB.readById(player.getPlayerId());
        assertEquals(player.getPlayerId(), playerFromDb.getPlayerId());
        assertEquals(player.getNickname(), playerFromDb.getNickname());
        assertEquals(player.getProgresses().size(), playerFromDb.getProgresses().size());
        assertEquals(player.getItems().size(), playerFromDb.getItems().size());
        assertEquals(player.getCurrencies().size(), playerFromDb.getCurrencies().size());
    }

    @SneakyThrows
    @Test
    public void writeToFileTest(){
        List<Player> readPlayers = PlayerUtil.readPlayersFromFile("players.json");
        List<Player> firstTen = readPlayers.subList(0, 10);
        String outString = PlayerUtil.convertToString(firstTen);
        try(FileOutputStream outputStream = new FileOutputStream("first_ten.json")){
            byte[] strToBytes = outString.getBytes();
            outputStream.write(strToBytes);
        }
    }



    @Test
    public void readDataAllBaseTest() throws SQLException {
        List<Player> readPlayers = PlayerUtil.readPlayersFromFile("players.json");
        //прокачка из кэша в БД
        playerDB.saveAll(readPlayers);
        //прокачка из БД в кэш
        List<Player> playersFromDb = playerDB.readAll();
        assertEquals(readPlayers.size(), playersFromDb.size());

    }

}
