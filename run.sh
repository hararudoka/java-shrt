javac -d ./dist src/com/shrt/Main.java
cd dist
java -cp ".:../lib/postgresql-42.6.0.jar" src/com/shrt/Main
