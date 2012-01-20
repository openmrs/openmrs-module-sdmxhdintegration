SDMX-HD Integration OpenMRS Module
=========================================

This module is to allow SDMX-HD messages to be imported to and exported from OpenMRS. 

The Jembi SDMX-HD library and the reporting module do not yet exist in the OpenMRS Maven repository so these need to added to the developers local Maven repository as follows:

1. Download htmlwidgets-1.6.0.omod, serialization.xstream-0.2.5.omod, reporting-0.7.0.omod and jembi-sdmx-hd-library-0.3.2.jar

2. Execute the following Maven commands in the directory where you saved those files:

mvn install:install-file -DgroupId=org.openmrs.module -DartifactId=serialization.xstream -Dversion=0.2.5 -Dfile=serialization.xstream-0.2.5.omod -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.openmrs.module -DartifactId=htmlwidgets -Dversion=1.6.0 -Dfile=htmlwidgets-1.6.0.omod -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.openmrs.module -DartifactId=reporting -Dversion=0.7.0 -Dfile=reporting-0.7.0.omod -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=jembi -DartifactId=sdmx-hd -Dversion=0.3.2 -Dfile=jembi-sdmx-hd-library-0.3.2.jar -Dpackaging=jar -DgeneratePom=true


