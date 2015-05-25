package database;

import java.util.Comparator;

class AttOrderComparator implements Comparator<Processor> {
	@Override
	public int compare(Processor p1, Processor p2) {
		Att a1 = p1.getOutputAtts().get(0);
		Att a2 = p2.getOutputAtts().get(0);

		if (a1.getAttOrder() == a2.getAttOrder())
			return 0;
		else if (a1.getAttOrder() > a2.getAttOrder())
			return 1;
		else
			return -1;
	}
}

class AttOrderComparatorAtts implements Comparator<Att>{
	public int compare(Att a1, Att a2){
		if (a1.getAttOrder() == a2.getAttOrder())
			return 0;
		else if (a1.getAttOrder() > a2.getAttOrder())
			return 1;
		else
			return -1;
	}
}