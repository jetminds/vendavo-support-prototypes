@call VendavoCheck.bat > VendavoCheckOutput.txt
@call RenameWisely VendavoCheckOutput.txt VC

@call HostCheck.bat > HostCheckOutput.txt
@call RenameWisely HostCheckOutput.txt HC

@copy *.csv \\vsvm2\cvsroot\tools-management\incoming
@del *.csv

