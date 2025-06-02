package eachare.commands;

import eachare.DownloadStat;
import eachare.repository.DownloadStatRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShowStats implements Command {

	private record StatKey(int chunkSize, int peerAmount, long fileSize) {}

	private final DownloadStatRepository statRepository;

	public ShowStats(DownloadStatRepository statRepository) {
		this.statRepository = statRepository;
	}

	@Override
	public void execute() {
		List<DownloadStat> stats = statRepository.getAll();

		HashMap<StatKey, List<Long>> groupedDurations = new HashMap<>();

		for(DownloadStat stat : stats) {
			StatKey key = new StatKey(stat.chunkSize(), stat.peerAmount(), stat.fileSize());

			groupedDurations.putIfAbsent(key,
					new ArrayList<>());

			groupedDurations.get(key).add(stat.duration());
		}

		System.out.printf("%-10s | %-7s | %-12s | %-3s | %-9s | %-9s |%n",
				"Tam. chunk", "N peers", "Tam. arquivo", "N", "Tempo [s]", "Desvio");

		for(Map.Entry<StatKey, List<Long>> entry : groupedDurations.entrySet()) {

			StatKey key = entry.getKey();

			List<Long> durations = entry.getValue();

			long sum = durations.stream().mapToLong(Long::longValue).sum();
			double avg = (double) sum / durations.size();

			double sq = 0.0;
			for(long duration : durations)
				sq += Math.pow(duration - avg, 2);

			double stdDev = Math.sqrt(sq / durations.size());

			System.out.printf("%-10s | %-7s | %-12s | %-3s | %-9s | %-9s |%n",
					key.chunkSize, key.peerAmount, key.fileSize, durations.size(), avg, stdDev);
		}
	}
}
