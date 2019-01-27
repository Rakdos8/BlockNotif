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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tabinol.blocknotif.utils.FileCopy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Charsets;

/**
 * Text message
 * @author Tabinol
 */
public class MessagesTxt {

	private static final String LANGFILENAME = "language.yml";
	private static final String LANGTMP = "languagetmp.yml";
	private static final String MESSAGE_NAME[] = {
		"MESSAGE_DESTROY",
		"MESSAGE_PLACE",
		"MESSAGE_IGNITE",
		"MESSAGE_USEBUCKET",
		"MESSAGE_TNTEXPLODE",
		"MESSAGE_XRAY",
		"MESSAGE_ENTITYKILL",
		"MESSAGE_BEFORE",
		"MESSAGE_RELOAD",
		"MESSAGE_SPECIFYPLAYER",
		"MESSAGE_NOACTIVITY",
		"MESSAGE_NOPERMISSION",
	};
	// type of Action
	public static final int DESTROY = 0;
	static final int PLACE = 1;
	static final int IGNITE = 2;
	static final int USEBUCKET = 3;
	public static final int TNTEXPLODE = 4;
	static final int XRAY = 5;
	static final int ENTITYKILL = 6;
	static final int MESSAGE_BEFORE = 7;
	static final int MESSAGE_RELOAD = 8;
	static final int MESSAGE_SPECIFYPLAYER = 9;
	static final int MESSAGE_NOACTIVITY = 10;
	static final int MESSAGE_NOPERMISSION = 11;
	// Default messages must be also in laguage.yml in comment (#)
	// Here again the same position of type of action
	private static final String MESSAGE_DEFAULT[] = {
		"&f<time>: &5<player> &9destroyed &5<block> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
		"&f<time>: &5<player> &9placed &5<block> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
		"&f<time>: &5<player> &9ignited &5<block> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
		"&f<time>: &5<player> &9used &5<block> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
		"&f<time>: &5BOOM! &9created by &5<player> &9in &5<world>&9,&5<posx>&9,&5<posy>&9,&5<posz>",
		"&f<time>: &5<player> &9uses potentially &5X-Ray&9!",
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

	/**
	 * Log a message
	 */
	MessagesTxt() {

		blockNotif = BlockNotif.getThisPlugin();

		try {
			saveDefaultConfig();

		} catch (Exception ex) {
			Logger.getLogger(MessagesTxt.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Load messages
	 */
	void loadMessages() {

		reloadCustomConfig();

		listeMessage = new ArrayList<>();

		// Take message text from file
		for (int t = 0; t < MESSAGE_NAME.length; t++) {
			final String messageFile = getCustomConfig().getString(MESSAGE_NAME[t]);
			if (messageFile == null) {
				listeMessage.add(MESSAGE_DEFAULT[t].replaceAll("&", "§"));
			} else {
				listeMessage.add(messageFile.replaceAll("&", "§"));
			}

		}

	}

	// Source : http://wiki.bukkit.org/Introduction_to_the_New_Configuration#Methods_for_Getting.2C_Reloading.2C_and_Saving_Custom_Configurations
	private void reloadCustomConfig() {

		if (customConfigFile == null) {
			customConfigFile = new File(blockNotif.getDataFolder(), LANGFILENAME);
		}
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

		// Look for defaults in the jar
		final InputStream defConfigStream = blockNotif.getResource(LANGFILENAME);
		if (defConfigStream != null) {
			try(final Reader reader = new InputStreamReader(defConfigStream, Charsets.UTF_8)){

				final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
				customConfig.setDefaults(defConfig);
			}catch (IOException ex){
				BlockNotif.getThisPlugin().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
			}
		}
	}

	private FileConfiguration getCustomConfig() {

		if (customConfig == null) {
			this.reloadCustomConfig();
		}
		return customConfig;
	}

	private void saveDefaultConfig() {

		final File langTMP = new File(blockNotif.getDataFolder(), LANGTMP);

		if (customConfigFile == null) {
			customConfigFile = new File(blockNotif.getDataFolder(), LANGFILENAME);
		}

		// Copy a temporary file and convert for Windows UTF-8 bug
		if (!customConfigFile.exists()) {
			blockNotif.saveResource(LANGTMP, false);
			FileCopy.copyFile(langTMP, customConfigFile);
			langTMP.delete();
		}
	}
	// End of citation

	/**
	 * Get a message
	 * @param messageNb Message number
	 * @param paramName Name
	 * @param param params
	 * @return Message
	 */
	public String getMessage(final int messageNb, final String[] paramName, final String[] param) {

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
