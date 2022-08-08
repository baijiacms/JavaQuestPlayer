Quest Soft player for JavaSE.

It is developed in Java language, of which 20% is implemented in c++.

The application is still in the beta stage. At present,can run on the windows x64 version.. 

the official site:  [the official site](https://baijiacms.github.io/)

Module introduction:

1.QspJavaEngine: Kernel for running QSP games.

2.QspJavaFxPlayer: Use JavaFX operation mode (imperfect).

3.QspJavaWebPlayer: The running mode of local browser does not support the lower version of IE. Chrome is the best(perfect).

4.QspJavaBrowserPlayer: Use the built-in Java browser (imperfect).

Features:
1. Run QSP games.

2. Development tool: it supports compiling qsrc source code in memory and playing games. Compiling and testing QSP games is faster.

3. Browser Mode: the built-in browser running mode can directly use the translation function of chrome and other browsers.

4. Export QSP to TXT file. The encrypted QSP file needs to know the password to export.

5. Export qsrc source code file list to QSP file.

6. Game list function.
more


StartQsp(JavaFX).bat  is JavaFX mode,Similar applications run.

StartQsp(Web browser).bat is web browser mode, this will automatically open the default browser of the operating system after running If it is not opened, you can access the browser by yourself http://127.0.0.1:19870 Address can also start the game.


Special note:

1. Java in built-in JDK1.8 net. The URI class is customized. This is only for JavaFX mode. More specifically, it is for the built-in video playback function URL of JavaFX. Because I can't capture the URL of the video and modify it into a file stream, the video can't be played. At present, this is the best solution I have found.

2. JavaFX mode does not support playing WebM videos, because JavaFX does not support it. The web browser mode depends on whether the browser supports it. Chrome generally has no problem.

3. The web browser mode uses jetty technology. By default, an HTTP port 19870 will be opened locally for browsers to access. 

If you want to modify the port, just change "StartQsp(JavaFX).bat" file
source

jre\bin\java.exe -jar -Dfile.encoding=utf-8 QspJavaFxPlayer.jar

to

jre\bin\java.exe -jar -Dfile.encoding=utf-8 QspJavaFxPlayer.jar 19870   

19870 Change to the port you need


This application is mainly used for my self-developed games. 

If you are interested in this application, join me.

Thank you for your support

![image text](https://github.com/baijiacms/Java-Quest-Soft-player/raw/main/1.png)
![image text](https://github.com/baijiacms/Java-Quest-Soft-player/raw/main/2.png)
![image text](https://github.com/baijiacms/Java-Quest-Soft-player/raw/main/3.png)
![image text](https://github.com/baijiacms/Java-Quest-Soft-player/raw/main/4.png)
![image text](https://github.com/baijiacms/Java-Quest-Soft-player/raw/main/5.png)
