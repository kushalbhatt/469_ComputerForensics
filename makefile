JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
        address4forensics.java \
        AddressConverter.java \
        mac_conversion.java \
        Task2.java  

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class