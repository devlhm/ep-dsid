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

		if (stats.isEmpty()) {
			System.out.println("Nenhuma estatística disponível.");
			return;
		}

		HashMap<StatKey, List<Long>> groupedDurations = new HashMap<>();

		for(DownloadStat stat : stats) {
			StatKey key = new StatKey(stat.chunkSize(), stat.peerAmount(), stat.fileSize());

			groupedDurations
				.computeIfAbsent(key, k -> new ArrayList<>())
				.add(stat.durationInMillis());
		}

		System.out.printf("%-10s | %-7s | %-12s | %-3s | %-9s | %-9s |%n",
				"Tam. chunk", "N peers", "Tam. arquivo", "N", "Tempo [s]", "Desvio");

		for(Map.Entry<StatKey, List<Long>> entry : groupedDurations.entrySet()) {

			StatKey key = entry.getKey();

			List<Long> durations = entry.getValue();

			long sum = durations.stream().mapToLong(Long::longValue).sum();
			double mean = ((double) sum / durations.size());

			double sq = 0.0;
			for(long duration : durations)
				sq += Math.pow(duration - mean, 2);

			double stdDev = Math.sqrt(sq / durations.size());

			System.out.printf("%-10s | %-7s | %-12s | %-3s | %-9s | %-9s |%n",
					key.chunkSize, key.peerAmount , key.fileSize, durations.size(),
					String.format("%.5f", mean / 1000.0), String.format("%.5f", stdDev / 1000.0));
		}
	}
}
