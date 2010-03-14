package notwa.security;

import java.util.Collection;

import notwa.common.Config;
import notwa.common.ConnectionInfo;
import notwa.dal.UserDal;
import notwa.exception.SignInException;
import notwa.sql.Parameter;
import notwa.sql.ParameterCollection;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.wom.User;

public class Security {
    private static Security singleton;
    
    private Security() {};
    
    public static Security getInstance() {
        if (singleton == null) {
            singleton = new Security();
        }
        return singleton;
    }
    
    public ConnectionInfo signIn(
            Object databaseName, String userLogin, String userPassword)
            throws SignInException,Exception {
        Collection<ConnectionInfo> cci = Config.getInstance().getConnecionStrings();
        for (ConnectionInfo ci : cci) {
            if(ci.getLabel().equals(databaseName)) {
                UserDal ud = new UserDal(ci);
                User user = ud.get(new ParameterCollection(new Parameter[] {new Parameter(Parameters.User.LOGIN, userLogin, Sql.Condition.EQUALTY)}));
                if (!(user.getLoginName().equals(userLogin)
                        && user.getLoginPass().equals(userPassword))) {
                    throw new SignInException();
                }
                return ci;
            }
        }

        return null;
    }
}
