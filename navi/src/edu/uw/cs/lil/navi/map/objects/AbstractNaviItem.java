package edu.uw.cs.lil.navi.map.objects;

public abstract class AbstractNaviItem implements INaviItem {
	private final String	label;
	
	public AbstractNaviItem(String label) {
		this.label = label;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractNaviItem other = (AbstractNaviItem) obj;
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		return true;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
