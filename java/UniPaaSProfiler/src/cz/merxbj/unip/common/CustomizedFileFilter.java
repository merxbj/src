package cz.merxbj.unip.common;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author tomas
 */
public class CustomizedFileFilter extends FileFilter {
    private String desc = "All";
    private String[] allowedExtensions = new String[] {};

    public CustomizedFileFilter(String desc, String[] allowedExtensions ) {
        this.desc = desc;
        this.allowedExtensions = allowedExtensions;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }

        int dotIndex = file.getName().lastIndexOf('.');
        if (dotIndex == -1) {
            return false;
        } else {
            String extension = file.getName().substring(dotIndex + 1);
            for (String allowed_extension : allowedExtensions) {
                if (extension.equalsIgnoreCase(allowed_extension)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public String getDescription() {
        return desc;
    }
}
