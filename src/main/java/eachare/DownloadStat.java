package eachare;

public record DownloadStat(int chunkSize, int peerAmount, int fileSize, int n, double time, double deviation) { }