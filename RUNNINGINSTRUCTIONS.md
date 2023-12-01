# Running instructions for javaparser-maven
1. `export PATH="$PATH:apache-maven-3.9.5/bin"`
2. `mvn clean install`
3. `java -jar target/javaparser-maven-sample-1.0-SNAPSHOT-shaded.jar "/u/clc5uy/Downloads/javaparser-maven/src/main/error_txt_files" "/u/clc5uy/Downloads/javaparser-maven/src/main/source_code_files"`