@SET TEMP_FOLDER = C:\Temp
@SET JAR_FOLDER = D:\Junior\git\tracetool\java\target
@SET JAR = tracetool-all-0.9.2-jar-with-dependencies.jar

java -Djava.io.tmpdir=%TEMP_FOLDER% -cp %JAR_FOLDER%\%JAR% br.ufpr.dinf.arch.jbluepill.TraceToolSimulatorLimit --xmlfile:execution-win.xml --memmap:pmap-final.txt --csvexport:memtable.csv --results:simulation-results.txt
