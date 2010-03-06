package notwa.wom;

import java.util.ArrayList;

public class UserCollection {

	private ArrayList<User> userCollection = new ArrayList<User>();

	public void add(User user) {
		userCollection.add(user);
	}
	
	public ArrayList<User> getUserCollection() {
		return userCollection;
	}
	
	public User getUserByID(int userID) {
		for(User user : userCollection) {
			if (user.getUserID() == userID) {
				return user;
			}
		}
		return null;
	}
}
