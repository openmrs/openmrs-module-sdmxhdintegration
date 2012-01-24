SDMX-HD Integration OpenMRS Module
=========================================

This module is to allow SDMX-HD messages to be imported to and exported from OpenMRS. 

The Jembi SDMX-HD library does not yet exist in the OpenMRS Maven repository so this needs to be added to the developers local Maven repository as follows:

1. Download jembi-sdmx-hd-library-0.3.2.jar

2. Execute the following command in the directory where you saved those files:

mvn install:install-file -DgroupId=jembi -DartifactId=sdmx-hd -Dversion=0.3.2 -Dfile=jembi-sdmx-hd-library-0.3.2.jar -Dpackaging=jar -DgeneratePom=true