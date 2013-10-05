package org.onebusaway.nyc.admin.util;

import org.onebusaway.nyc.util.impl.FileUtility;

import org.h2.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * A collection of file handling utilities making life easier when working with
 * Java but specific to the admin module needs.  More Generic functions exist
 * in org.onebusaway.nyc.util.impl.FileUtility.
 * 
 */
public class FileUtils {
  private static Logger _log = LoggerFactory.getLogger(FileUtils.class);

  private String _workingDirectory = null;
  // more generic file handling functions belong in nyc.util
  private FileUtility _fileUtil = new FileUtility();

  public FileUtils() {
    _workingDirectory = System.getProperty("java.io.tmpdir");
  }

  public FileUtils(String workingDirectory) {
    _workingDirectory = workingDirectory;
  }

  /**
   * Retrieve a file at the remote URL. The file will be named the last portion
   * of the URL, following the conventions of the UNIX tool wget.
   */
  public void wget(String urlString) {
    URL url;
    InputStream is = null;
    BufferedInputStream bis = null;
    FileOutputStream fos = null;
    String fileName = parseFileName(urlString);
    _log.info("downloading " + urlString + " to fileName " + _workingDirectory
        + File.separatorChar + fileName);
    try {
      url = new URL(urlString);
      is = url.openStream();
      bis = new BufferedInputStream(is);
      fos = new FileOutputStream(_workingDirectory
          + File.separatorChar + fileName);
      IOUtils.copy(bis, fos);
    } catch (Exception any) {
      throw new RuntimeException(any);
    } finally {
      if (bis != null)
        try {
          bis.close();
        } catch (Exception e1) {
        }
      if (fos != null)
        try {
          fos.close();
        } catch (Exception e2) {
        }
    }
  }

  /**
   * Copy the input stream to the given destinationFileName (which includes path
   * and filename).
   */
  public void copy(InputStream source, String destinationFileName) {
    _fileUtil.copy(source, destinationFileName);
  }

  public String parseFileName(String urlString) {
    if (urlString == null) return null;
    int i = urlString.lastIndexOf("/");
    if (i+1 < urlString.length()) {
      return urlString.substring(i+1, urlString.length());
    }
    if (i >= 0) {
      return urlString.substring(i, urlString.length());
    }
    return urlString;
  }

  public String parseBucket(String s3path) {
    if (s3path.indexOf("s3://") == -1) {
      throw new RuntimeException(
          "Invalid s3path, missing protocol s3://; path=" + s3path);
    }
    int start = s3path.indexOf("/", 5);
    int end = s3path.indexOf("/", start + 1);
    return s3path.substring(start, end);
  }

  public String parseKey(String s3path) {
    if (s3path.indexOf("s3://") == -1) {
      throw new RuntimeException(
          "Invalid s3path, missing protocol s3://; path=" + s3path);
    }
    int bucketStart = s3path.indexOf("/", 5);
    int start = s3path.indexOf("/", bucketStart + 1);
    return s3path.substring(start, s3path.length());
  }

  /**
   * untar and uncompress a tar file (.tar.gz)
   */
  public int tarzxf(String tarFile) {
    Process process = null;
    try {
      String[] cmds = {
          "tar",
          "zxC",
          _workingDirectory,
          "-f",
          tarFile
      };
      debugCmds(cmds);
      process = Runtime.getRuntime().exec(cmds);
      return process.waitFor();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * set UNIX permissions. The permissions string is passed through as is so it
   * can be of the octal format 777 or o+x format.
   */
  public int chmod(String permissions, String destinationFileName) {
    Process process = null;
    try {
      String[] cmds = {
          "chmod",
          permissions,
          destinationFileName
      };
      debugCmds(cmds);
      process = Runtime.getRuntime().exec(cmds);
      return process.waitFor();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void copyFiles(File from, File to) {
    try {
      if (!from.exists())
        return;
      if (from.equals(to) || to.getParent().equals(from))
        return;

      if (from.isDirectory()) {
        to.mkdirs();
        File[] files = from.listFiles();
        if (files == null)
          return;
        for (File fromChild : files) {
          File toChild = new File(to, fromChild.getName());
          copyFiles(fromChild, toChild);
        }
      } else {
        FileInputStream in = null;
        FileOutputStream out = null;

        try {
          in = new FileInputStream(from);
          out = new FileOutputStream(to);
          IOUtils.copy(in, out);
        } finally {
          if (in != null)
            in.close();
          if (out != null)
            out.close();
        }
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }

  }

  public String createTmpDirectory() {
    String tmpDir = System.getProperty("java.io.tmpdir") + File.separator
        + "tmp" + System.currentTimeMillis();
    boolean created = new File(tmpDir).mkdir();
    // if directory already exists, try again
    if (!created) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException ie) {
        return null;
      }
      return createTmpDirectory();
    }
    return tmpDir;
  }

  /**
   * unix style unzip.
   */
  public int unzip(String zipFileName, String outputDirectory) {
    Process process = null;
    try {
      String[] cmds = {
          "unzip",
          "-o",
          zipFileName,
          "-d",
          outputDirectory
      };
      process = Runtime.getRuntime().exec(cmds);
      StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
      StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
      errorGobbler.start();
      outputGobbler.start();
      return process.waitFor();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * Write file contents to the given fileName.
   * @param fileName
   * @param contents
   */
  public void createFile(String fileName, StringBuffer contents) {
    try {
      File file = new File(fileName);
      FileWriter fw = new FileWriter(file);
      fw.append(contents);
      fw.close();
    } catch (IOException ioe) {
      _log.error(ioe.toString(), ioe);
      throw new RuntimeException(ioe);
    }
  }

  public int tarcvf(String baseDir, String[] paths, String filename) {
    Process process = null;
    try {
      StringBuffer cmd = new StringBuffer();
      cmd.append("tar zcC " + baseDir + "  ");
      for (String path : paths) {
        cmd.append(path + " ");
      }
      cmd.append("-f " + filename);
      _log.info("exec:" + cmd.toString());
      process = Runtime.getRuntime().exec(cmd.toString());
      return process.waitFor();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }


  public InputStream read(String filename) {
    File file = new File(filename);
    if (file.exists()) {
      try {
        return new FileInputStream(file);
      } catch (FileNotFoundException e) {
        _log.error(e.toString(), e);
        throw new RuntimeException(e);
      }
    } else {
      _log.info("file not found for read(" + filename + ")");
    }
    return null;
  }

  public static String escapeFilename(String s) {
    return s.replace(" ", "\\ ");
  }
  
  public static void debugCmds(String[] array) {
    StringBuffer sb = new StringBuffer();
    sb.append("exec:");
    for (String s :array){
      sb.append(s + " ");
    }
    _log.info(sb.toString());
  }


  /**
   * debug sub shells, from http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.htm
   *
   */
  class StreamGobbler extends Thread
  {
      InputStream is;
      String type;
      
      StreamGobbler(InputStream is, String type)
      {
          this.is = is;
          this.type = type;
      }
      
      public void run()
      {
          try
          {
              InputStreamReader isr = new InputStreamReader(is);
              BufferedReader br = new BufferedReader(isr);
              String line=null;
              while ( (line = br.readLine()) != null)
                _log.info(type + ">" + line);
              } catch (IOException ioe)
                {
                  ioe.printStackTrace();  
                }
      }
  }
  
}
