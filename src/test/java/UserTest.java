import com.naukma.cauliflower.dao.DAO;
import com.naukma.cauliflower.dao.UserRoles;
import com.naukma.cauliflower.entities.User;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.SQLException;


/**
 * Created by Max on 07.12.2014.
 */

public class UserTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        try {
            // Create initial context
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES,
                    "org.apache.naming");
            InitialContext ic = new InitialContext();

            ic.createSubcontext("jdbc");
            OracleConnectionPoolDataSource ds = new OracleConnectionPoolDataSource();
            // Construct DataSource

            ds.setURL("jdbc:oracle:thin:@localhost:1521/xe");
            ds.setUser("CAULIFLOWER");
            ds.setPassword("11111");

            ic.bind("jdbc/oraclesource", ds);
        } catch (NamingException ex) {
            System.out.println("Failed to start junit testing");
            ex.printStackTrace();
        }
    }

    @Test
    public void testUser() throws SQLException {
        DAO dao = DAO.INSTANCE;
        User user = new User(-1,1, UserRoles.ADMINISTRATOR.toString(),"aaaaaaafdgdfh","aaa","aaa","123",false);
        String password = "1234567891sfsdg";
        int userId = dao.createUser(user,password);
        User userRes = dao.getUserByLoginAndPassword(user.getEmail(),password);

        assert (user.getEmail().equals(userRes.getEmail()));
        assert (userId == userRes.getUserId());

    }
}
