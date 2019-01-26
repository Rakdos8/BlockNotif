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
// Log process
package me.tabinol.blocknotif;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogTask {

    private static final String LOGFILENAME = "BlockNotif.log";
    private BlockNotif blockNotif;
    private boolean logFileEnable = false;

    public LogTask() {

        blockNotif = BlockNotif.getThisPlugin();
    }

    public void setLogEnable(boolean logFileEnable) {

        this.logFileEnable = logFileEnable;
    }

    public void writeLog(String textLog) {
        blockNotif.getLogger().info(textLog);

        if (logFileEnable) {
            logToFile(textLog);
        }
    }

    // Source : http://forums.bukkit.org/threads/making-a-log-file-for-your-plugins.85430/
    public void logToFile(String message) {

    	PrintWriter pw = null ;

        try {
            final File dataFolder = blockNotif.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            final File saveTo = new File(blockNotif.getDataFolder(), LOGFILENAME);
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }

            final FileWriter fw = new FileWriter(saveTo, true);
            pw = new PrintWriter(fw);
            pw.println(message);
            pw.flush();

        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            pw.close();
            
		}
    }
}
