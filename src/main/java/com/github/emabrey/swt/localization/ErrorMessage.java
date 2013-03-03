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
package com.github.emabrey.swt.localization;

import c10n.annotations.En;

/**
 * Localized error messages.
 * </br>
 * Currently Supported Locales:
 * </br>
 * <ul>
 * <li>En</li>
 * </ul>
 * @author Emily Mabrey emilymabrey93@gmail.com
 */
public interface ErrorMessage {
    
  /**
   * The localized error message for having null method arguments.
   * @return The localized error message 
   */  
  @En("SWTBundle was given invalid arguments. You must not pass null versions or destinations")
  String libraryCalledWithNullArgument();

  /**
   * The localized error message for being unable to close an IO stream
   * @return The localized error message 
   */ 
  @En("Unable to close IO stream")
  String unableToCloseStream();
  
  /**
   * The localized error message for being unable to copy an IO stream to a destination stream
   * @return The localized error message 
   */ 
  @En("Unable to copy IO streams")
  String unableToCopyStreams();  
  
  /**
   * The localized error message for the destination file being referenced by a relative path.
   * @return The localized error message 
   */ 
  @En("SWTBundle was given a potentially valid destination, however the destination was specified with a relative path. Please use an absolute path.")
  String destinationCannotBeRelativePath();
  
  /**
   * The localized error message for the destination file being a directory reference.
   * @return The localized error message 
   */ 
  @En("SWTBundle was given an invalid destination. SWTBundle cannot output directly to a directory, the path must specify an output file.")
  String destinationCannotBeDirectory();
  
  /**
   * The localized error message for the destination file already existing without being forced to overwrite it.
   * @return The localized error message 
   */ 
  @En("SWTBundle The unpackTo method cannot overwrite an already existing file, consider using unpackToForced if you want to overwrite")
  String destinationCannotAlreadyExist();
  
  /**
   * The localized error message for the destination file not being overwritten despite being given the option to force overwrites.
   * @return The localized error message 
   */ 
  @En("Forced unpack was unable to force the overwrite of the destination")
  String cannotForceOverwriteOfExistingDestination();
  
  /**
   * The localized error message for the destination file being read-only.
   * @return The localized error message 
   */ 
  @En("SWTBundle was given an unwritable destination path")
  String destinationCannotBeReadOnly();
  
  /**
   * The localized error message for being unable to create the destination file.
   * @return The localized error message 
   */ 
  @En("SWTBundle was unable to create the destination file")
  String destinationCannotBeCreated();
  
  /**
   * The localized error message for being unable to unpack the class resources
   * and output them to the destination.
   * @return The localized error message 
   */ 
  @En("Unable to unpack SWT libraries from JAR to file system!")
  String cannotUnpackSWTLibraries();
  
  /**
   * The localized error message for having an OS that SWT doesn't support.
   * @return The localized error message 
   */ 
  @En("The operating system is unsupported by SWTBundle!")
  String operatingSystemUnsupported();
  
  /**
   * The localized error message for having a processor architecture that SWT doesn't support.
   * @return The localized error message 
   */ 
  @En("The processor architecture is unsupported by SWTBundle!")
  String processorArchitectureUnsupported();
}
