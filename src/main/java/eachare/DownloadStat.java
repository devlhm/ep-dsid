package eachare;

public record DownloadStat(int chunkSize, int peerAmount, long fileSize, long durationInMillis) { }