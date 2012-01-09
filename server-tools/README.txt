boxes.csv - here is stored current status of boxes.
instances.csv - here is stored current status of instances.

support-boxes.txt - file with locations where to copy 
DeployVendavoTools.bat - copies files from TOOLS-FOR-DEPLOY into all locations listed in support-boxes.txt

online.html - HTML with current Vendavo instances running on support boxes.
online-boxes.html - HTML with current state of support boxes (memory, diskspace, OS etc.)

ScheduleShowBoxes.bat - creates scheduled job that processes INCOMING and creates online-boxes.html
ShowBoxes.bat - processes INCOMING and creates online-boxes.html. Called by a the scheduled job.

ScheduleShowInstances.bat - creates scheduled job that processes INCOMING and creates online.html
ShowInstances.bat - processes INCOMING and creates online.html. Called by the scheduled job.
