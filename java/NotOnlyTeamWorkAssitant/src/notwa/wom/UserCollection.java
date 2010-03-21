package notwa.wom;

public class UserCollection extends BusinessObjectCollection<User> {

    public UserCollection() {
    }

    public UserCollection(Context context) {
        this.currentContext = context;
    }

}
