/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.ide.zk.zookeeper.data;

import org.apache.zookeeper.ServerAdminClient;

/**
 * Represents a single ZooKeeper server (host and port).
 * 
 * @author Mark Masse
 * @see ServerAdminClient
 */
public final class ZooKeeperServer {

	public static final String COMMAND_DUMP = "dump";
	public static final String COMMAND_GET_TRACE_MASK = "gettracemask";
	public static final String COMMAND_RUOK = "ruok";
	public static final String COMMAND_STAT = "stat";

	public static final String[] COMMANDS = new String[] {COMMAND_DUMP, COMMAND_GET_TRACE_MASK, COMMAND_RUOK, COMMAND_STAT};

	private final ZooKeeperServerDescriptor _Descriptor;

	public ZooKeeperServer(ZooKeeperServerDescriptor descriptor) {
		_Descriptor = descriptor;
	}

	public String dump() {
		Stoppable outputCatcher = startOutputCatcher();
		ServerAdminClient.dump(_Descriptor.getHost(), _Descriptor.getPort());
		return outputCatcher.stop();
	}

	/**
	 * Returns the descriptor.
	 * 
	 * @return The descriptor
	 */
	public ZooKeeperServerDescriptor getDescriptor() {
		return _Descriptor;
	}

	public String getTraceMask() {
		Stoppable outputCatcher = startOutputCatcher();
		ServerAdminClient.getTraceMask(_Descriptor.getHost(), _Descriptor.getPort());
		return outputCatcher.stop();
	}

	public void kill() {
		ServerAdminClient.kill(_Descriptor.getHost(), _Descriptor.getPort());
	}

	public String ruok() {
		Stoppable outputCatcher = startOutputCatcher();
		ServerAdminClient.ruok(_Descriptor.getHost(), _Descriptor.getPort());
		return outputCatcher.stop();
	}

	public void setTraceMask(String traceMaskStr) {
		ServerAdminClient.setTraceMask(_Descriptor.getHost(), _Descriptor.getPort(), traceMaskStr);
	}

	public String stat() {
		Stoppable outputCatcher = startOutputCatcher();
		ServerAdminClient.stat(_Descriptor.getHost(), _Descriptor.getPort());
		return outputCatcher.stop();
	}

	@Override
	public String toString() {
		return "ZooKeeperServer [Descriptor=" + _Descriptor + "]";
	}

	interface Stoppable {
		public String stop();
	}

	public class DummyCacher implements Stoppable {
		public void start() {

		}

		@Override
		public String stop() {
			return "";
		}
	}
	protected Stoppable startOutputCatcher() {
		DummyCacher outputCatcher = new DummyCacher();
		outputCatcher.start();
		return outputCatcher;
	}


	/*
	protected ServerAdminClientOutputCatcher startOutputCatcher() {
		ServerAdminClientOutputCatcher outputCatcher = new ServerAdminClientOutputCatcher();
		outputCatcher.start();
		return outputCatcher;
	}

	public class ServerAdminClientOutputCatcher {

		private PrintStream _SystemOut;
		private Appender _Appender;
		private ByteArrayOutputStream _CatcherStream;
		private boolean _Active;

		public ServerAdminClientOutputCatcher() {
		}

		public void start() {
			_Active = true;

			_SystemOut = System.out;

			_CatcherStream = new ByteArrayOutputStream();
			System.setOut(new PrintStream(_CatcherStream));

			_Appender = createAppender();
			Logger logger = getLogger();
			logger.addAppender(_Appender);


		}

		public String stop() {
			if (!_Active) {
				return null;
			}

			System.out.flush();
			String output = _CatcherStream.toString();

			System.setOut(_SystemOut);
			_SystemOut = null;
			Logger logger = getLogger();
			logger.removeAppender(_Appender);
			_Appender = null;


			try {
				_CatcherStream.close();
			}
			catch (IOException e) {
			}

			_CatcherStream = null;

			_Active = false;

			return output;
		}

		protected Logger getLogger() {
			return Logger.getLogger(ServerAdminClient.class);
		}

		protected Appender createAppender() {
			return new ConsoleAppender(new SimpleLayout(), ConsoleAppender.SYSTEM_OUT);
		}

	}
	 */
}
