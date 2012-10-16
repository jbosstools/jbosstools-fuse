package org.fusesource.ide.commons.util;

public abstract class Function1Adapter<T,R> implements Function1WithReturnType<T,R> {
	private final Class<R> returnType;

	public Function1Adapter(Class<R> returnType) {
		this.returnType = returnType;
	}


	@Override
	public String toString() {
		return "function(T) : " + getReturnType().getName();
	}



	@Override
	public Class<R> getReturnType() {
		return returnType;
	}


}
