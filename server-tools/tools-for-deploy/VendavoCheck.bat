@call PsTools\handle.exe -u vendavo.log > handle.txt
@call netstat -ano >netstat.txt
@call java.exe -cp vendavo-support-prototype.jar com.vendavo.support.prototype.VendavoCheck
@del handle.txt
@del netstat.txt