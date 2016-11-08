package lockfreepriorityqueue.nonlinearlizable;

import interfacepackage.ParallelPriorityQueue;
import lockfreepriorityqueue.nonlinearlizable.PrioritySkipList.Node;

public class SkipQueue<T> implements ParallelPriorityQueue{

	PrioritySkipList<T> skiplist;
	public SkipQueue(){
		skiplist = new PrioritySkipList<T>();
	}
	


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SkipQueue<Integer> q = new SkipQueue<Integer>();
		q.add(5);
		q.add(4);
		q.add(5);
		q.add(4);
		q.add(4);
		q.add(8);
		System.out.print(q.poll());
		System.out.print(q.poll());
		System.out.print(q.poll());
		System.out.print(q.poll());
		System.out.print(q.poll());
		System.out.print(q.poll());
		System.out.print(q.poll());
		
	}

	public boolean add(Integer item) {
		Node<T> node = (Node<T>)new Node<T>((T) item, item);
		return skiplist.add(node);
	}



	public Integer poll() {
		Node<T> node = skiplist.findAndMarkMin();
		if (node != null){
			skiplist.remove(node);
			return (Integer) node.item;
		} else {
			return null;
		}
	}
}
