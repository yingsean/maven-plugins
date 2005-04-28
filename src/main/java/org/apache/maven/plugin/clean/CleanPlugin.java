package org.apache.maven.plugin.clean;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionException;

import java.io.File;

/**
 * @author <a href="mailto:evenisse@maven.org">Emmanuel Venisse</a>
 * @version $Id$
 * @goal clean
 * @description Goal which cleans the build
 */
public class CleanPlugin
    extends AbstractPlugin
{
    private static final int DELETE_RETRY_SLEEP_MILLIS = 10;

    /** 
     * This is where compiled classes go.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private String outputDirectory;

    // TODO: not in the descriptor previously
//    private boolean failOnError;

    public void execute()
        throws PluginExecutionException
    {
        if ( outputDirectory != null )
        {
            File dir = new File( outputDirectory );

            if ( dir.exists() && dir.isDirectory() )
            {
                getLog().info( "Deleting directory " + dir.getAbsolutePath() );
                removeDir( dir );
            }
        }
    }

    /**
     * Accommodate Windows bug encountered in both Sun and IBM JDKs.
     * Others possible. If the delete does not work, call System.gc(),
     * wait a little and try again.
     */
    private boolean delete( File f )
    {
        if ( !f.delete() )
        {
            if ( System.getProperty( "os.name" ).toLowerCase().indexOf( "windows" ) > -1 )
            {
                System.gc();
            }
            try
            {
                Thread.sleep( DELETE_RETRY_SLEEP_MILLIS );
                return f.delete();
            }
            catch ( InterruptedException ex )
            {
                return f.delete();
            }
        }
        return true;
    }

    /**
     * Delete a directory
     *
     * @param d the directory to delete
     */
    protected void removeDir( File d )
    {
        String[] list = d.list();
        if ( list == null )
        {
            list = new String[0];
        }
        for ( int i = 0; i < list.length; i++ )
        {
            String s = list[i];
            File f = new File( d, s );
            if ( f.isDirectory() )
            {
                removeDir( f );
            }
            else
            {
                if ( !delete( f ) )
                {
                    String message = "Unable to delete file " + f.getAbsolutePath();
// TODO:...
//                    if ( failOnError )
//                    {
//                        throw new Exception( message );
//                    }
//                    else
//                    {
                    getLog().info( message );
//                    }
                }
            }
        }

        if ( !delete( d ) )
        {
            String message = "Unable to delete directory " + d.getAbsolutePath();
// TODO:...
//            if ( failOnError )
//            {
//                throw new Exception( message );
//            }
//            else
//            {
            getLog().info( message );
//            }
        }
    }

}
