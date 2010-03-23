package notwa.security;

import notwa.common.ConnectionInfo;
import notwa.dal.UserDal;
import notwa.exception.SignInException;
import notwa.sql.Parameter;
import notwa.sql.ParameterSet;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.wom.Context;
import notwa.wom.ContextManager;
import notwa.wom.User;

public class Security {
    private static Security instance;
    
    public static Security getInstance() {
        if (instance == null) {
            instance = new Security();
        }
        return instance;
    }
    
    public boolean signIn(ConnectionInfo ci, String userLogin, String userPassword) throws Exception {
        Context loginContext = ContextManager.getInstance().newContext();
        UserDal ud = new UserDal(ci, loginContext);
        User user = ud.get( new ParameterSet(new Parameter[] {new Parameter(Parameters.User.LOGIN, userLogin, Sql.Condition.EQUALTY)}));

        if (user == null) {
            throw new SignInException("Invalid login provided.");
        } else if (!user.getPassword().equals(userPassword)) {
            throw new SignInException("Invalid login password provided.");
        }

        return true;
    }
}
