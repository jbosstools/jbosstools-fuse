package org.fusesource.ide.jmx.ui.internal.localjmx;

public class JvmKey {
	private final String hostName;
	private final int pid;

	public JvmKey(String hostName, int pid) {
		this.hostName = hostName;
		this.pid = pid;
	}

	public String getHostName() {
		return hostName;
	}

	public int getPid() {
		return pid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result + pid;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JvmKey other = (JvmKey) obj;
		if (hostName == null) {
			if (other.hostName != null)
				return false;
		} else if (!hostName.equals(other.hostName))
			return false;
		if (pid != other.pid)
			return false;
		return true;
	}

}
