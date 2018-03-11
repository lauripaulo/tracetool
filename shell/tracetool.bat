@SET TEMP_FOLDER = C:\Temp
@SET JAR_FOLDER = D:\Junior\git\tracetool\java\target
@SET JAR = tracetool-all-0.9.2-jar-with-dependencies.jar

java -Djava.io.tmpdir=%TEMP_FOLDER% -jar %JAR_FOLDER%\%JAR% br.ufpr.dinf.arch.jbluepill.TraceToolSimulator --mode:socket --outputfile:tracefile --max:5000000 --xmldata:execution.xml --compress
