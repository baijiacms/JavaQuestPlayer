Quest Soft player for javase
I am Chinese.My English is poor,sorry.
It is developed in Java language, of which 20% is implemented in c++.

Several technologies are used: JavaFX, velocity, jetty

The application is still in the beta stage. At present,can run on the windows x64 version.. 

StartQsp(JavaFX).bat  is JavaFX mode,Similar applications run.


StartQsp(Web browser).bat is web browser mode, this will automatically open the default browser of the operating system after running If it is not opened, you can access the browser by yourself http://127.0.0.1:19870 Address can also start the game.

Application functions:

1. Run QSP games

2. Export QSP games as txt files and package qproj projects as QSP files (it is also the compilation and decoding for QSP Development)

2. Compile qproj project in memory, and the compilation test of QSP game is simpler.

3. It includes the function of game list, and you can select games.

4. There are two application startup modes: JavaFX mode and web browser mode (jetty)

5. Support the game archiving function. At present, the archiving interface is ugly. I don't have time to modify this interface at present.

As for more functions, you will know after testing.


Special note:

1. Java in built-in JDK1.8 net. The URI class is customized. This is only for JavaFX mode. More specifically, it is for the built-in video playback function URL of JavaFX. Because I can't capture the URL of the video and modify it into a file stream, the video can't be played. At present, this is the best solution I have found.

2. JavaFX mode does not support playing WebM videos, because JavaFX does not support it. The web browser mode depends on whether the browser supports it. Chrome generally has no problem.

3. The web browser mode uses jetty technology. By default, an HTTP port 19870 will be opened locally for browsers to access. If you want to modify the port, just change "StartQsp(JavaFX).bat" file

from

jre\bin\java.exe -jar -Dfile.encoding=utf-8 QspJavaFxPlayer.jar

to

jre\bin\java.exe -jar -Dfile.encoding=utf-8 QspJavaFxPlayer.jar 19870   

19870 Change to the port you need


There are no plans to open it, and i will consider whether it is open source according to the popularity and usage.

This application is mainly used for my self-developed games. 

If you are interested in this application, join me.

Thank you for your support

![image text](https://github.com/baijiacms/Java-Quest-Soft-player/blob/master/1.png)
![image text](https://github.com/baijiacms/Java-Quest-Soft-player/blob/master/2.png)
![image text](https://github.com/baijiacms/Java-Quest-Soft-player/blob/master/3.png)
![image text](https://github.com/baijiacms/Java-Quest-Soft-player/blob/master/4.png)
![image text](https://github.com/baijiacms/Java-Quest-Soft-player/blob/master/5.png)