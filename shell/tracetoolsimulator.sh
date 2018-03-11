TEMP_FOLDER = C:\Temp
JAR_FOLDER = D:\Junior\git\tracetool\java\target
JAR = tracetool-all-0.9.2-jar-with-dependencies.jar

java -cp $JAR_FOLDER/$JAR br.ufpr.dinf.arch.jbluepill.TraceToolSimulator --xmlfile:execution.xml --memmap:pmap-final.txt --csvexport:memtable.csv --results:simulation-results.txt
