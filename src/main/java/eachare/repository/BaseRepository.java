package eachare.repository;

import eachare.Message;
import eachare.Peer;
import eachare.PeerStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseRepository<T> {
	protected final List<T> entities = Collections.synchronizedList(new ArrayList<>());

	public void add(T entity) {
		entities.add(entity);
	}

	public List<T> getAll() {
		return List.copyOf(entities);
	}

	public T get(int idx) {
		return entities.get(idx);
	}

	public int size() {
		return entities.size();
	}


}
