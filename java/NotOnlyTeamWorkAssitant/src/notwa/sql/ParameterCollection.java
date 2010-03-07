package notwa.sql;

import java.util.*;


public class ParameterCollection implements Collection<Parameter>{

	private Collection<Parameter> parameters;
	
	public ParameterCollection() {
		this.parameters = new ArrayList<Parameter>();
	}
	
	@Override
	public boolean add(Parameter parameter) {
		if (!parameters.contains(parameter)) {
			return parameters.add(parameter);
		} else {
			return false;
		}
	}

	@Override
	public boolean addAll(Collection<? extends Parameter> params) {
		for (Parameter p : params) {
			if (parameters.contains(p)) {
				return false;
			}
		}
		return parameters.addAll(params);
	}

	@Override
	public void clear() {
		parameters.clear();
	}

	@Override
	public boolean contains(Object o) {
		return parameters.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> obs) {
		return parameters.containsAll(obs);
	}

	@Override
	public boolean isEmpty() {
		return parameters.isEmpty();
	}

	@Override
	public Iterator<Parameter> iterator() {
		return parameters.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return parameters.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> obs) {
		return parameters.removeAll(obs);
	}

	@Override
	public boolean retainAll(Collection<?> obs) {
		return parameters.retainAll(obs);
	}

	@Override
	public int size() {
		return parameters.size();
	}

	@Override
	public Object[] toArray() {
		return parameters.toArray();
	}

	@Override
	public <T> T[] toArray(T[] obs) {
		return parameters.toArray(obs);
	}

}
