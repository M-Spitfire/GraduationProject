package Interface;

public interface Watched {
	public void addWatcher(Watcher watcher);

    public void removeWatcher(Watcher watcher);

    public void notifyWatchers(int data);
}
