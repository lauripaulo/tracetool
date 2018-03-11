JAR_FOLDER=/home/lauri/git/tracetool/java/target/
JAR=tracetool-all-0.9.2-jar-with-dependencies.jar

java -cp $JAR_FOLDER/$JAR br.ufpr.dinf.arch.jbluepill.TraceTool --mode:socket --outputfile:tracefile --max:5000000 --xmldata:execution.xml --compress

pkill -9 lackey-amd64-li
