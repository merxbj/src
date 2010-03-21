package notwa.security;

import notwa.common.ConnectionInfo;
import notwa.dal.UserDal;
import notwa.exception.SignInException;
import notwa.sql.Parameter;
import notwa.sql.ParameterCollection;
import notwa.sql.Parameters;
import notwa.sql.Sql;
import notwa.wom.ContextManager;
import notwa.wom.User;
import notwa.wom.UserCollection;

public class Security {
    private static Security singleton;
    
    private Security() {};
    
    public static Security getInstance() {
        if (singleton == null) {
            singleton = new Security();
        }
        return singleton;
    }
    
    public boolean signIn(
            ConnectionInfo ci, String userLogin, String userPassword)
            throws SignInException,Exception {

        UserCollection uc = new UserCollection();
        uc.setCurrentContext(ContextManager.getInstance().newContext());
        UserDal ud = new UserDal(ci,uc.getCurrentContext());
        User user = ud.get( new ParameterCollection(
                            new Parameter[] {
                               new Parameter(   Parameters.User.LOGIN,
                                                userLogin,
                                                Sql.Condition.EQUALTY)}));
        if (!(user.getLogin().toLowerCase().equals(userLogin.toLowerCase())
                && user.getPassword().equals(userPassword))) {
            throw new SignInException();
        }

        return true;
    }
}
