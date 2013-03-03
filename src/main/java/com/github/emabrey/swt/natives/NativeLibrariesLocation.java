/*
 * Copyright (C) 2013 Emily Mabrey emilymabrey93@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.emabrey.swt.natives;

/**
 * A stub class simply used as a syntactic sugar for referencing the location of
 * the native libraries. Rather than store a hard-coded path to the libraries, relative to the root of the jar,
 * the unpacker assumes that this class will sit at the same level as the platform dependent folders.
 * @author Emily Mabrey emilymabrey93@gmail.com
 */
@SuppressWarnings("PMD")
public class NativeLibrariesLocation{
    
    /**
     * Deliberately no-op private constructor for static Class
     */
    private NativeLibrariesLocation(){
        //Do nothing
    }    
};
