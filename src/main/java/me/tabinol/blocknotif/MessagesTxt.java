/*
 BlockNotif: Minecraft plugin player action on blocks notification
 Copyright (C) 2013  Michel Blanchet

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
// This Class is for taking messages from file
package me.tabinol.blocknotif;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tabinol.blocknotif.utils.FileCopy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessagesTxt {

    public final static String LANGFILENAME = "language.yml";
    public final static String LANGTMP = "languagetmp.yml";
    public final static String MESSAGE_NAME[] = {
        // Must be the same position of type of action (below)
        "MESSAGE_DESTROY", // DESTROY = 0
        "MESSAGE_PLACE", // PLACE = 1
        "MESSAGE_IGNITE", // IGNITE = 2
        "MESSAGE_USEBUCKET", // USEBUCKET = 3
        "MESSAGE_TNTEXPLODE", // TNTEXPLODE = 4
        "MESSAGE_XRAY", // XRAY = 5, Removed at version 1.5.1
        "MESSAGE_ENTITYKILL", // ENTITYKILL = 6
        // Next, are in MessageTxt
        "MESSAGE_BEFORE",
        "MESSAGE_RELOAD",
        "MESSAGE_SPECIFYPLAYER",
        "MESSAGE_NOACTIVITY",
        "MESSAGE_NOPERMISSION"
    };
    // type of Action
    public final static int DESTROY = 0;
    public final static int PLACE = 1;
    public final static int IGNITE = 2;
    public final static int USEBUCKET = 3;
    public final static int TNTEXPLODE = 4;
    public final static int XRAY = 5; // Removed at version 1.5.1
    public final static int ENTITYKILL = 6;
    public final static int MESSAGE_BEFORE = 7;
    public final static int MESSAGE_RELOAD = 8;
    public final static int MESSAGE_SPECIFYPLAYER = 9;
    public final static int MESSAGE_NOACTIVITY = 10;
    public final static int MESSAGE_NOPERMISSION = 11;
    // Default messages must be also in laguage.yml in comment (#)
    // Here again the same position of type of action
    public final static String MESSAGE_DEFAULT[] = {
        "&f<time>: &5<player> &9destroyed &5<block> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
        "&f<time>: &5<player> &9placed &5<block> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
        "&f<time>: &5<player> &9ignited &5<block> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
        "&f<time>: &5<player> &9used &5<block> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
        "&f<time>: &5BOOM! &9created by &5<player> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
        "&f<time>: &5<player> &9uses potentially &5X-Ray&9!", // Removed at version 1.5.1
        "&f<time>: &5<player> &9killed &5<block> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
        "&9 (and &5<nb> &9before)",
        "Configuration reloaded!",
        "You must specify a player!",
        "No activity found for this player!",
        "Sorry, you do not have the permission!"
    };
    private List<String> listeMessage;
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;
    private BlockNotif blockNotif;

    public MessagesTxt() {

        blockNotif = BlockNotif.getThisPlugin();

        try {
            saveDefaultConfig();

        } catch (Exception ex) {
            Logger.getLogger(MessagesTxt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadMessages() {

        reloadCustomConfig();

        listeMessage = new ArrayList<String>();

        // Take message text from file
        for (int t = 0; t < MESSAGE_NAME.length; t++) {
            String messageFile = getCustomConfig().getString(MESSAGE_NAME[t]);
            if (messageFile == null) {
                listeMessage.add(MESSAGE_DEFAULT[t].replaceAll("&", "§"));
            } else {
                listeMessage.add(messageFile.replaceAll("&", "§"));
            }

        }

    }

    // Source : http://wiki.bukkit.org/Introduction_to_the_New_Configuration#Methods_for_Getting.2C_Reloading.2C_and_Saving_Custom_Configurations
    public void reloadCustomConfig() {

        if (customConfigFile == null) {
            customConfigFile = new File(blockNotif.getDataFolder(), LANGFILENAME);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        InputStream defConfigStream = blockNotif.getResource(LANGFILENAME);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getCustomConfig() {

        if (customConfig == null) {
            this.reloadCustomConfig();
        }
        return customConfig;
    }

    private void saveDefaultConfig() throws Exception {

        File LangTMP = new File(blockNotif.getDataFolder(), LANGTMP);

        if (customConfigFile == null) {
            customConfigFile = new File(blockNotif.getDataFolder(), LANGFILENAME);
        }

        // Copy a temporary file and convert for Windows UTF-8 bug
        if (!customConfigFile.exists()) {
            blockNotif.saveResource(LANGTMP, false);
            FileCopy.copyFile(LangTMP, customConfigFile);
            LangTMP.delete();
        }
    }
    // End of citation

    public String getMessage(int messageNb, String[] paramName, String[] param) {

        String resultTxt = listeMessage.get(messageNb);
        int t;

        if (paramName != null && param != null) {
            for (t = 0; t < paramName.length; t++) {
                resultTxt = resultTxt.replace(paramName[t], param[t]);
            }
        }

        return resultTxt;
    }
}
