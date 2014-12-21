package drunkmafia.thaumicinfusion.common.util;

/*
 * The contents of this file are subject to the Sapient Public License
 * Version 1.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://carbon.sf.net/License.html.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is The Carbon Component Framework.
 *
 * The Initial Developer of the Original Code is Sapient Corporation
 *
 * Copyright (C) 2003 Sapient Corporation. All Rights Reserved.
 */


import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ClassFinder {

    protected long foundClasses = 0;

    protected Class superClass = null;

    protected String requiredPathSubstring = null;

    private Set classes = new HashSet(2000);

    public ClassFinder() {}

    public ClassFinder(Class superClass) {
        this.superClass = superClass;
    }

    public ClassFinder(Class superClass, String requiredPathSubstring) {
        this.superClass = superClass;
        this.requiredPathSubstring = requiredPathSubstring;
    }

    protected void addClassName(String className) {
        if ((this.requiredPathSubstring == null) || (className.indexOf(this.requiredPathSubstring) >= 0)) {

            if (this.superClass == null) {
                this.classes.add(className);
            } else {
                try{
                    Class thisClass = Class.forName(className);
                    if(this.superClass.isAssignableFrom(thisClass))
                        this.classes.add(thisClass);

                }catch (Throwable t){}
            }
        }
    }

    public Set getClasses() {
        return classes;
    }

    public void processFile(String base, String current) {
        File currentDirectory = new File(base + File.separatorChar + current);

        if (isArchive(currentDirectory.getName())) {
            try {
                processZip(new ZipFile(currentDirectory));
            } catch (Exception e) {}
        } else {

            Set<File> directories = new HashSet<File>();

            File[] children = currentDirectory.listFiles();

            if (children == null || children.length == 0)
                return;


            for (File child : children) {
                if (child.isDirectory()) {
                    directories.add(child);
                } else {
                    if (child.getName().endsWith(".class")) {
                        String className = getClassName(current + ((current == "") ? "" : File.separator) + child.getName());
                        addClassName(className);
                        this.foundClasses++;
                    }
                }
            }

            for (Iterator<File> i = directories.iterator(); i.hasNext(); )
                processFile(base, current + ((current=="")?"":File.separator) + (i.next()).getName());

        }
    }

    protected boolean isArchive(String name) {
        return name.endsWith(".jar") || name.endsWith(".zip");
    }

    protected String getClassName(String fileName) {
        String newName =  fileName.replace(File.separatorChar,'.');
        newName =  newName.replace('/','.');
        return newName.substring(0, fileName.length() - 6);
    }


    protected void processZip(ZipFile file) {
        Enumeration<? extends ZipEntry> files = file.entries();

        while (files.hasMoreElements()) {
            ZipEntry tfile = files.nextElement();
            ZipEntry child = tfile;
            if (child != null && child.getName().endsWith(".class")) {
                addClassName(getClassName(child.getName()));
                this.foundClasses++;
            }
        }
    }
}