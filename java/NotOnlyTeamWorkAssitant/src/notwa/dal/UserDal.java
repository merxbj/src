package notwa.dal;

import notwa.common.ConnectionInfo;
import notwa.sql.ParameterCollection;
import notwa.wom.User;
import notwa.wom.UserCollection;

public class UserDal extends DataAccessLayer implements Fillable<UserCollection>, Getable<User> {

	public UserDal(ConnectionInfo ci) {
		super(ci);
	}

	@Override
	public int Fill(UserCollection boc, ParameterCollection pc) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int Fill(UserCollection boc) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public User get(ParameterCollection primaryKey) {
		// TODO Auto-generated method stub
		return new User(1);
	}

}
