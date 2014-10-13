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

// Copy a file to an other file

package me.tabinol.blocknotif.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Scanner;

public class FileCopy {

    public static void copyFile(File fileR, File fileW) throws Exception {

        String newLine = System.getProperty("line.separator");
        Writer output = new BufferedWriter(new FileWriter(fileW, true));
        Scanner scanner;
        scanner = new Scanner(fileR, "UTF-8");

        while (scanner.hasNextLine()) {
            output.write(scanner.nextLine());
            output.write(newLine);
        }
        
        scanner.close();
        output.close();
    }
}