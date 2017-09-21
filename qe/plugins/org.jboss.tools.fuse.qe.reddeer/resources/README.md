# Resources

In this folder you can find the following resources

+-- host.key
+-- .ssh
    +-- known_hosts

## Reason

If you start karaf/fuse server for the first time you are asked for confirming SSH host key.
Since this is a native dialog and RedDeer framework cannot work with native dialogs we have to ensure that this dialog won't pop up.

The file `host.key` is copied to the karaf/fuse server and the folder `.ssh` is used as SSH2 home in eclipse instance. The logic is implemented in the following class

    org.jboss.tools.fuse.qe.reddeer.runtime.impl.ServerKaraf

