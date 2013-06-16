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

import java.util.Collection;
import java.util.HashMap;

import java.util.Set;
import pt.ua.dicoogle.DebugManager;

/**
 * This class stores the list of users of Dicoogle
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class UsersStruct {
    private HashMap<String, User> users;

    private static UsersStruct instance = null ;

    // count the number of administrators
    private int numberOfAdmins;

    public static synchronized UsersStruct getInstance() {
        if (instance == null) {
            instance = new UsersStruct();
        }

        return instance;
    }



    private UsersStruct(){
       reset();
    }

    
    /**
     * Insert one default user
     * Username: "dicoogle"
     * Password: "DCpassword" (hashed)
     *
     * This user is administrator
     */
    public void setDefaults(){
        DebugManager.getInstance().debug("Setting default user settings");

        String username = "dicoogle";
        boolean admin = true;
        String passPlainText = "dicoogle";

        String passHash = HashService.getSHA1Hash(passPlainText);             //password Hash
        String Hash = HashService.getSHA1Hash(username + admin + passHash);   //user Hash

        users = new HashMap<String, User>();
        users.put("dicoogle", new User(username, Hash, admin));
    }

    /**
     * Used only by UsersXML to reset User Settings
     */
    protected void reset(){
        users = new HashMap<String, User>();
        numberOfAdmins = 0;
    }

    
    /**
     * Insert user in the List of users
     *
     * @param user
     * @return  true - if succeeded. false - if the username already exists
     */
    public boolean addUser(User user){
        if(users.containsKey(user.getUsername()))
            return false;

        users.put(user.getUsername(), user);

        if(user.isAdmin())
            numberOfAdmins++;
        
        return true;
    }

    /**
     *  Removes one user from de list
     *  Maintains at least one administrator in the list
     *      (refuses to remove the last one)
     * 
     * @param username
     * @return
     */
    public boolean removeUser(String username){
        if(username == null)
            return false;
        
        User user = users.get(username);
        if (user == null)
            return false;

        if(user.isAdmin() && numberOfAdmins == 1)
            return false;
        else if(user.isAdmin())
            numberOfAdmins--;

        users.remove(username);
        return true;
    }

    public Collection<User> getUsers(){
        return users.values();
    }

    public Set<String> getUsernames(){
        return users.keySet();
    }

    public User getUser(String username){
        return users.get(username);
    }
}
