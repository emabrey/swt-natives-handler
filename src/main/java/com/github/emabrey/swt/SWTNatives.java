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
package com.github.emabrey.swt;

import c10n.C10N;
import c10n.annotations.DefaultC10NAnnotations;
import com.github.emabrey.swt.localization.ErrorMessage;
import com.github.emabrey.swt.natives.NativeLibrariesLocation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for loading SWT native libraries to the file system.
 * @author Emily Mabrey emilymabrey93@gmail.com
 */
public final class SWTNatives {
    
    static
    {
        //Configres the internationalization library
        C10N.configure(new DefaultC10NAnnotations());
    }
    
    /**
     * The SLF4J Logger instance for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(SWTNatives.class);
    
    /**
     * The localized error messages
     */
    private static final ErrorMessage ERROR_MESSAGE = C10N.get(ErrorMessage.class, Locale.getDefault());
    
        
    /**
     * Deliberately no-op private constructor for static Class
     */
    private SWTNatives() {
        //Do nothing
    }
    
    /**
     * The supported SWT versions that this class is capable of unpacking.
     */
    public enum SWT_VERSION{
        /**
         * SWT Version  3.5.1
         */
        _3_5_1,
        
        /**
         * SWT Version 3.7.2
         */
        _3_7_2,
        
        /**
         * SWT Version 3.8
         */
        _3_8,
        
        /**
         * SWT Version 4.2.1
         */
        _4_2_1;
    }   
    
    /**
     * The supported operating systems that this class is capable of unpacking under.
     */
    public enum SWT_PLATFORM{
        
        /**
         * Microsoft Windows
         */
        windows,
        
        /**
         * Macintosh with Cocoa
         */
        macosx,
        
        /**
         * Generic Linux
         */
        linux;
    }
    
    /**
     * The supported processor architectures that this class is capable of unpacking.
     */
    public enum SWT_PLATFORM_ARCH{
        
        /**
         * 32-bit
         */
        x86,
        
        /**
         * 64-bit
         */
        x64;
    }
    
    /**
     * Java system property that describes our operating system.
     */
    private static final String OS_NAME_PROPERTY = "os.name";
    
    /**
     * Java system property that describes our processor arch.
     */
    private static final String OS_ARCH_PROPERTY = "os.arch";
    
    /**
     * Unpacks the specified SWT library version to the destination, not overwriting the destination if it already exists.
     * @param destination The absolute path to the destination file, which cannot be overwritten
     * @param version The SWT version we are trying to load
     * @throws InstantiationException If we are given an invalid value or we unable to complete the copy
     */
    public static void unpackTo(File destination, SWT_VERSION version) throws InstantiationException{
        
       /*
        * Note that this unpacking code is subject to a TOCTOU condition. We are assuming that the destination remains valid
        * for the rest of the unpacking, despite that potentially not being the case.
        */

        if(destination == null || version == null){
            //"User" input sanity check
            throw createLoggedError(ERROR_MESSAGE.libraryCalledWithNullArgument());
        }
        
        if(!destination.isAbsolute()){
            //Force implementing program to clarify absolutely where it intends files to be places
            throw createLoggedError(ERROR_MESSAGE.destinationCannotBeRelativePath());
        }
        
        if(destination.isDirectory()){
            //You can't use directories as file targets
            throw createLoggedError(ERROR_MESSAGE.destinationCannotBeDirectory());
        }
        
        if(destination.exists()){
            //This method doesn't blindly overwrite destination
            throw createLoggedError(ERROR_MESSAGE.destinationCannotAlreadyExist());
        } else{
            try {
                boolean successfullyCreated = destination.createNewFile();
                if(!successfullyCreated){
                    throw new IOException();//File did not create correctly, let our error handling logic activate
                }
            } catch (IOException e) {
                
                 //There was an issue with creating the destination file
                InstantiationException exception = createLoggedError(ERROR_MESSAGE.destinationCannotBeCreated());
                exception.initCause(e);
                throw exception;//NOPMD
            }
        }
        
        if(!destination.canWrite()){
            //We cannot write to the destination
            throw createLoggedError(ERROR_MESSAGE.destinationCannotBeReadOnly());           
        }
        
        String source = determineBundleLocation(version);
        
        copyBundleToFile(source, destination);        
    }
    
    /**
     * Unpacks the specified SWT library version to the destination, overwriting the destination if it already exists.
     * @param destination The absolute path to the destination file, which can be overwritten
     * @param version The SWT version we are trying to load
     * @throws InstantiationException If we are given an invalid value or we unable to complete the copy
     */
    public static void unpackToForced(File destination, SWT_VERSION version) throws InstantiationException{
        
        if(destination == null || version == null){
            //Input sanity check
            throw createLoggedError(ERROR_MESSAGE.libraryCalledWithNullArgument());
        }
        
        if(destination.exists()){
            //Delete the destination
            boolean wasDeleted = destination.delete();
            
            if(wasDeleted){
                unpackTo(destination,version);
            } else{
                throw createLoggedError(ERROR_MESSAGE.cannotForceOverwriteOfExistingDestination());
            }
        }
    }
    
    /**
     * Does a buffered copy of the data from the ClassLoader to the file.
     * @param source The path that indicates where in the ClassLoader the library is
     * @param destination The file to write the data into
     * @throws InstantiationException If a non-cleanup related IOException occurs
     */
    private static void copyBundleToFile(String source, File destination) throws InstantiationException{
      
         try {
              
              InputStream sourceStream = ClassLoader.getSystemClassLoader().getResourceAsStream(source);
              FileOutputStream destStream = new FileOutputStream(destination);
              copyStream(sourceStream, destStream);
              
         } catch (IOException ex) {
             
             //There was an issue with the copy
            InstantiationException exception = createLoggedError(ERROR_MESSAGE.cannotUnpackSWTLibraries());
            exception.initCause(ex);
            throw exception;//NOPMD
         }        
    }
    
    /**
     * Determines a ClassLoader location for the given native library version to be loaded from.
     * @param version The version we are attempting to unpack
     * @return The ClassLoader reference to the location of the libraries
     * @throws InstantiationException If the library cannot be found or loaded.
     */
    private static String determineBundleLocation(SWT_VERSION version) throws InstantiationException{        
        
        SWT_PLATFORM platform = getCurrentPlatform();
        SWT_PLATFORM_ARCH arch = getCurrentArch();
        
        return nativeLibrariesPackageLocation() + "/" + constructRelativePackageLocation(platform, arch, version);
    }
    
    /**
     * Determines the operating system.
     * @return The current OS platform of the JVM instance
     * @throws InstantiationException If the platform information is invalid or unavailable.
     */
    private static SWT_PLATFORM getCurrentPlatform() throws InstantiationException{
        String osName = System.getProperty(OS_NAME_PROPERTY).toLowerCase(Locale.ENGLISH);
        
        if(osName == null ){
            //Sanity check
            throw createLoggedError(ERROR_MESSAGE.operatingSystemUnsupported());
        }
        
        if (osName.contains("win") ){
                
                //Windows
                return SWT_PLATFORM.windows;
                
        } else if (osName.contains("mac") ){
                
                //Macintosh
                return SWT_PLATFORM.macosx;
                
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix") ){
                
                //Linux
                return SWT_PLATFORM.linux;
                
        } else {
                //Unhandled Case
                throw createLoggedError(ERROR_MESSAGE.operatingSystemUnsupported());
        }
        
    }
    
    /**
     * Determines the processor architecture
     * @return The current processor architecture
     * @throws InstantiationException If the arch information is invalid or unavailable.
     */
    private static SWT_PLATFORM_ARCH getCurrentArch() throws InstantiationException{
        String osArch = System.getProperty(OS_ARCH_PROPERTY);
        
        if(osArch == null ){
            //Sanity check
            throw createLoggedError(ERROR_MESSAGE.processorArchitectureUnsupported());
        }
        
        if(osArch.contains("64") ){
            
            //64 bit JVM
            return SWT_PLATFORM_ARCH.x64;
            
        } else if (osArch.contains("86") ){
            
            //32 bit JVM
            return SWT_PLATFORM_ARCH.x86;
            
        } else{            
            //Unhandled Case
            throw createLoggedError(ERROR_MESSAGE.processorArchitectureUnsupported());
        }
    }
            
    /**
     * Forms the path of the SWT libraries to unpack based upon the given information about this system.
     * @param platform The OS platform
     * @param arch The processor architecture
     * @param version The desired SWT version
     * @return A ClassLoader path for the SWT library the given information requires
     */
    private static String constructRelativePackageLocation(SWT_PLATFORM platform, SWT_PLATFORM_ARCH arch, SWT_VERSION version){
                
        final String relativePath = version.toString() + "/" +  platform.toString() + "/" + arch.toString() + "/" + "swt.jar";
        //Hardcoded slash literals acceptable for package references
        
        return relativePath;
    }
    
    /**
     * The path to the base package containing all native libraries organized by platform, version and architecture.
     * @return A package descriptor string 
     */
    private static String nativeLibrariesPackageLocation(){
        return NativeLibrariesLocation.class.getPackage().getName().replace(".", "/");
    }
    
    /**
     * Buffered copy the source stream to the destination stream, and then attempt to close both streams.
     * @param sourceStream Input
     * @param destStream Output
     * @throws InstantiationException If we are unable to copy the streams
     */
    private static void copyStream(InputStream sourceStream, OutputStream destStream) throws InstantiationException{
        try{
            //Do a buffered read/write between the two streams
            
           
            byte[] buf = new byte[8192];//Buffer
            
            
            while (true) {//Copy until finished

                int readLength = sourceStream.read(buf);

                if (readLength < 0){
                   break; 
                }

                destStream.write(buf, 0, readLength);
            }
        } catch (IOException e){
            
            //There was an issue with the copy
            InstantiationException exception = createLoggedError(ERROR_MESSAGE.cannotUnpackSWTLibraries());
            exception.initCause(e);
            throw exception;//NOPMD
            
        } finally{
            //Cleanup our io resources
            
            boolean bothClosedSuccessfully = true;
            
            try{
                if(sourceStream != null){
                    sourceStream.close();
                }
            } catch(IOException e){
                //We had a problem while closing the source stream resource
                bothClosedSuccessfully = false;
            }
            
            try{
                if(destStream != null){
                    destStream.close();
                }
            } catch(IOException e){                
                //We had a problem while closing the destination stream resource
                bothClosedSuccessfully = false;
            }       
            
            if(!bothClosedSuccessfully){                
                LOG.warn(ERROR_MESSAGE.unableToCloseStream());
            }
            
        }
    }
        
    /**
     * Creates and logs an exception with a given error message.
     * @param errorMessageName The localized error message
     * @return The created and logged exception
     */
    private static InstantiationException createLoggedError(String errorMessage){
                
        LOG.error(errorMessage);
        
        return new InstantiationException(errorMessage);
    }
}
