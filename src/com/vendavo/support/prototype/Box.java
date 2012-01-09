package com.vendavo.support.prototype;

//HostCheck, version 0.1.0
//Relevant CSV lines starts with SUCCESS.
//#SUCCESS, HOST, OS, IE, CPU speed, # of cores, MEM, C: size, C: free, C: perc, D: size, D: free, D: perc, E: size, E: free, E: perc, Excel
//SUCCESS, PSO23, Microsoft Windows Server 2003 R2, 7.0000, Intel(R) Xeon, 2.3, 8, 4096, 20.00, 5.29, 26.5, null, null, null, null, null, null, Excel 2007

public class Box {
    long lastUpdated;
    private String host;
    private String os;
    private String ie;
    private String cpu;
    private String cpuSpeed;
    private String numberOfCores;
    private String physicalMemorySize;
    private String cSize;
    private String cFree;
    private String cPerc;
    private String dSize;
    private String dFree;
    private String dPerc;
    private String eSize;
    private String eFree;
    private String ePerc;
    private String excelVersion;

    public Box(String host, String os, String ie, String cpu, String cpuSpeed, String numberOfCores, String physicalMemorySize, String cSize, String cFree, String cPerc, String dSize, String dFree, String dPerc, String eSize, String eFree, String ePerc, String excelVersion) {
        this.host = host;
        this.os = os;
        this.ie = ie;
        this.cpu = cpu;
        this.cpuSpeed = cpuSpeed;
        this.numberOfCores = numberOfCores;
        this.physicalMemorySize = physicalMemorySize;
        this.cSize = cSize;
        this.cFree = cFree;
        this.cPerc = cPerc;
        this.dSize = dSize;
        this.dFree = dFree;
        this.dPerc = dPerc;
        this.eSize = eSize;
        this.eFree = eFree;
        this.ePerc = ePerc;
        this.excelVersion = excelVersion;
    }

    public String getHost() {
        return host;
    }

    public String getOs() {
        return os;
    }

    public String getIe() {
        return ie;
    }

    public String getCpuSpeed() {
        return cpuSpeed;
    }

    public String getNumberOfCores() {
        return numberOfCores;
    }

    public String getPhysicalMemorySize() {
        return physicalMemorySize;
    }

    public String getcSize() {
        return cSize;
    }

    public String getcFree() {
        return cFree;
    }

    public String getcPerc() {
        return cPerc;
    }

    public String getdSize() {
        return dSize;
    }

    public String getdFree() {
        return dFree;
    }

    public String getdPerc() {
        return dPerc;
    }

    public String geteSize() {
        return eSize;
    }

    public String geteFree() {
        return eFree;
    }

    public String getePerc() {
        return ePerc;
    }

    public String getExcelVersion() {
        return excelVersion;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Box)) return false;

        Box box = (Box) o;

        if (host != null ? !host.equals(box.host) : box.host != null) return false;

        return true;
    }

    public int hashCode() {
        return host != null ? host.hashCode() : 0;
    }
}
