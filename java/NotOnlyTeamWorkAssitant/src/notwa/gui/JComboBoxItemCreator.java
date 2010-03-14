package notwa.gui;

import notwa.common.ConnectionInfo;

public class JComboBoxItemCreator {
        ConnectionInfo connectionInfo;
        String value;
     
        public JComboBoxItemCreator(ConnectionInfo ci, String value) {
            this.connectionInfo = ci;
            this.value = value;
        }
     
        public String getValue() { return value; }
        public ConnectionInfo getAttachedConnectionInfo() { return connectionInfo; }
     
        @Override
        public String toString() { return value; }
}
