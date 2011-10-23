/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package su.srt;

import java.util.List;
import su.common.SubtitleFormatException;

/**
 *
 * @author merxbj
 */
interface FileRepresentable {
    List<String> formatForFile();
    void parse(List<String> lines) throws SubtitleFormatException;
}
