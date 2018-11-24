# iDisguise
Plugin for CraftBukkit and Spigot

## Basic information
This plugin allows you to turn into almost every entity that exists in Minecraft.  
More information can be found here: https://www.spigotmc.org/resources/idisguise.5509/

## Snapshot files
Compiled snapshot files may be downloaded over here: https://www.robingrether.de/idisguise/

## GitHub repository
As of October 2017 the master branch always holds a (hopefully) bug-free recommended release version of iDisguise, whereas small changes (such as bug fixes etc.) get pushed to the snapshot branch until they are released officially.
So if you would like to develop your own fork of this repository, make sure to build it upon the master branch to ensure best performance and proper functionality.

## Maven repository
````
<repository>
  <id>robingrether-repo</id>
  <url>http://repo.robingrether.de/</url>
</repository>
````

If you need the core plugin (API...):
````
<dependency>
  <groupId>de.robingrether.idisguise</groupId>
  <artifactId>idisguise-core</artifactId>
  <version>5.8.2</version>
</dependency>
````

If you need everything:
````
<dependency>
  <groupId>de.robingrether.idisguise</groupId>
  <artifactId>idisguise-full</artifactId>
  <version>5.8.2</version>
</dependency>
````

## Compiling
In order to compile the whole plugin you have to clone/download this repository and build the project _idisguise-full_ using Maven.  
Run _mvn build package_ and you will find the final jar file under _/idisguise-full/target/iDisguise-&lt;VERSION&gt;-&lt;TIMESTAMP&gt;.jar_.
