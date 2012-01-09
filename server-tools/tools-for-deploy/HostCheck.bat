@call PsTools\PsInfo.exe -d -s > host.txt
@call java.exe -cp vendavo-support-prototype.jar com.vendavo.support.prototype.HostCheck
@del host.txt
