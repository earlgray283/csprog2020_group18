compile:
	javac \
		--module-path /usr/share/openjfx/lib \
		--add-modules javafx.controls,javafx.media,javafx.fxml \
		*.java

run:
	javac \
		--module-path /usr/share/openjfx/lib \
		--add-modules javafx.controls,javafx.media,javafx.fxml \
		*.java && \
	java \
		--module-path /usr/share/openjfx/lib \
		--add-modules javafx.controls,javafx.media,javafx.fxml \
		MapGame && \
	rm *.class

clean:
	rm *.class