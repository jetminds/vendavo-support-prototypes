AddScheduledJobs.bat - add scheduled job on a client machine that runs every 5 minutes and execute ScheduledJobs.
                     - BEFORE EXECUTION ENSURE THAT THE PATH TO THE ScheduledJob.bat IS CORRECT! (check disk E:\ or D:\)
RemoteScheduledJobs.bar - removes support scheduled job.

HostCheck.bat - outputs information about current machine into the console
VendavoCheck.bat - outputs all Vendavo instances running on the local machine into the console

vendavo-support-prototype.jar- classes needed by the prototype.
1) Use vendavo-support-prototypes project to compile needed class.
2) Export as vendavo-support-prototype.jar in Eclipse.

ScheduledJobs.bat - executed on the client once per 5 minutes. Runs Vendavo/Box check and stores output file on remote the server (vsvm2)