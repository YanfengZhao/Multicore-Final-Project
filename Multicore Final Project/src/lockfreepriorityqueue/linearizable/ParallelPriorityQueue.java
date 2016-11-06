package lockfreepriorityqueue.linearizable;

public interface ParallelPriorityQueue<E> {
	public boolean add(E item);
	public E poll();
}
