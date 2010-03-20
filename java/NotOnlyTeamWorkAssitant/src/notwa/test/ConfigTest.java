package notwa.test;

import java.util.Collection;
import notwa.common.Config;
import notwa.common.ConnectionInfo;

public class ConfigTest {

    public ConfigTest() {
        Collection<ConnectionInfo> cons = Config.getInstance().getConnecionStrings();
        for (ConnectionInfo ci : cons) {
            System.out.println(ci.compileConnectionString());
        }
    }
}
