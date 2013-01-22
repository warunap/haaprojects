
API Differences Between ASM 3.3 and ASM 4.0
http://asm.ow2.org/jdiff33to40/changes.html


https://jira.springsource.org/browse/SPR-9669
Spring Framework SPR-9669
Upgrade to ASM 4.0 and CGLIB 3.0 
Fix Version/s: 3.2 M2


ASM 4.0 was released in Oct 2011 to address Java 7 bytecode compatibility, particularly with regard 
to invokedynamic [1].Certain public API changes were made in the process. Upgrade Spring's own internal 
repackaging of ASM to ensure users don't run into problems with Java 7 classes. This is most likely to 
cause problems in conjunction with Spring's component-scanning functionality, so this is high priority.
The repackaged org.springframework.asm classes are currently based on ASM 2.2.3, which is now several 
generations behind. Here are the differences between APIs across those generations:

2.2.3-> 3.2: http://asm.ow2.org/jdiff223to32/changes.html
3.2 -> 3.3: http://asm.ow2.org/jdiff32to33/changes.html
3.3 -> 3.4: http://asm.ow2.org/jdiff33to40/changes.html

CGLIB 3.0 was released on May 25th, 2012 [2] in order to upgrade its dependency on ASM to 4.0. 
This also involved some API changes. Upgrade Spring's dependency on CGLIB (currently at 2.2) to 3.0. 
Impact may or may not be significant, but should not have external impact at the API level 
(because we do not expose CGLIB APIs). It will be important to advise users as to whether CGLIB 3.0 
is the new lower bound supported by Spring as of 3.2 GA, or if 2.2 is still supported (with caveats 
about Java 7 support). This depends again on the extent of internal API changes necessary to support 
the upgrade.

[1]: http://mail.ow2.org/wws/arc/asm/2011-10/msg00025.html
[2]: http://cglib.cvs.sourceforge.net/viewvc/cglib/cglib/build.xml?revision=1.56&view=markup




cglib 2.2.2 is based on asm3.3.1
