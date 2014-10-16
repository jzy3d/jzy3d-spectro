/*
 * Created on Aug 6, 2008
 *
 * Spectro-Edit is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spectro-Edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package net.bluecow.spectro;

import java.util.Properties;

public class Version {

    /**
     * The version number of Spectro-Edit in this classloader context.
     */
    public static final String VERSION;
    static {
        try {
            Properties props = new Properties();
            props.load(Version.class.getResourceAsStream("version.properties"));
            VERSION = props.getProperty("net.bluecow.spectro.VERSION");
        } catch (Exception e) {
            throw new RuntimeException("Failed to read version from classpath resource", e);
        }
    }
}
