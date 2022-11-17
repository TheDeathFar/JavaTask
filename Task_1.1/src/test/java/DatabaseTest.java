import org.junit.Test;
import ru.vsu.css.vorobcov_i_a.database.DBUtils;

import java.sql.Connection;

import static org.junit.Assert.assertNotNull;

public class DatabaseTest {

    @Test
    public void connectionTest(){
        Connection connection = DBUtils.getConnection();
        assertNotNull(connection);
    }
}
