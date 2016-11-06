package lockfreepriorityqueue.nonlinearlizable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.Comparator;

public class PrioritySkipList<T> {
	private final Comparator<? super T> comparator;
	public static final class Node<T>{
		final T item;
		final int value;
		AtomicBoolean marked;

		final AtomicMarkableReference<Node<T>>[] next;
		
		// sentinel node constructor
		public Node(int myPriority){
			this.value = null;
			item = null;
			next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[MAX_LEVEL + 1];
			
		}
		// ordinary node constructor
		public Node(T x, int myPriority){
			this.item = x;
			this.value = myPriority;
		}
	}
	
	static final int MAX_LEVEL = 30;
	final Node<T> head = new Node<T>(Integer.MIN_VALUE); 
	final Node<T> tail = new Node<T>(Integer.MAX_VALUE);
	
	public PrioritySkipList(){
		comparator = null;
		for (int i = 0; i < head.next.length; i++) {
			head.next[i] = new AtomicMarkableReference<Node<T>>(tail, false);
		}
	}
	boolean add(T x){
		int topLevel = randomLevel();
		int bottomLevel = 0;
		Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
		Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
		while (true){
//			boolean found = find(node, preds, succs);
//			if (found){
//				return false;
//			}
			Node<T> newNode = new Node(x, topLevel);
			for (int level = bottomLevel; level <= topLevel; level++){
				Node<T> succ = succs[level];
				newNode.next[level].set(succ, false);
			}
			Node<T> pred = preds[bottomLevel];
			Node<T> succ = preds[bottomLevel];
			newNode.next[bottomLevel].set(succ, false);
			if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)){
				continue;
			}
			for (int level = bottomLevel+1; level <= topLevel; level++){
				while(true){
					pred = preds[level];
					succ= succs[level];
					if (pred.next[level].compareAndSet(succ, newNode, false, false)){
						break;
					}
				}
			}
			return true;
		}
		
	}
	
	boolean remove(Node<T> node){
		return true;
	}
	
	public Node<T> findAndMarkMin(){
		Node<T> curr = null, succ = null;
		curr = head.next[0].getReference();
		while (curr != tail) {
			if (!curr.marked.get()){
				if (curr.marked.compareAndSet(false, true)){
					return curr;
				} else {
					curr = curr.next[0].getReference();
				}
			}
		}
		return null; // no unmarked nodes
	}
	
	public static int randomLevel() {
	    int lvl = (int)(Math.log(1.-Math.random())/Math.log(1.-0.5));
	    return Math.min(lvl, MAX_LEVEL);
	}
	
	public int find(T x, Node<T>[] preds, Node<T>[] succs){
		int lFound = -1;
		Node<T> pred = head;
		for (int level = MAX_LEVEL; level >= 0; level--){
			Node<T> curr = pred.next[level].get(null); /////////
			while (compare(x, curr.item) == 1){
				pred = curr; 
				curr = pred.next[level].get(null);  /////////
			}
			if(lFound == -1 && x.equals(curr.item)){
				lFound = level;
			}
			preds[level] = pred;
			succs[level] = curr;
		}
		return lFound;
	}
	
    private int compare(T k1, T k2) {

        if ((k1 == null) && (k2 == null))
            return 0;
        if (k1 == null)
            return -1;
        else if (k2 == null)
            return 1;
        else {
            if (comparator == null)
                return ((Comparable<? super T>) k1).compareTo(k2);
            else
                return comparator.compare(k1, k2);
        }

    }
}
