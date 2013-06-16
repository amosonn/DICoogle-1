/*  Copyright   2010 Samuel Campos
 *
 *  This file is part of Dicoogle.
 *
 *  Dicoogle is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Dicoogle is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 */

package pt.ua.dicoogle.rGUI.server.users;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.BASE64Encoder;

/**
 * This class provides hashing service to passwords with SHA-1 algoritm
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class HashService {
    /**
     * Encrypt one password with SHA-1 algorithm
     *
     * @param plaintext
     * @return the hash of the password
     */
    public static String getSHA1Hash(String plaintext) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(plaintext.getBytes("UTF-8"));
            byte[] raw = md.digest();

            String hash = (new BASE64Encoder()).encode(raw);
            return hash;
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HashService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HashService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
