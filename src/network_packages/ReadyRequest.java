package network_packages;

public class ReadyRequest extends SocketPackage {
	private static final long serialVersionUID = 1;
	
	private boolean ready;
	
	public ReadyRequest(boolean ready) {
		super(0, SocketPackage.PACKAGE_READY_REQUEST);
		this.ready = ready;
	}

	public boolean isReady() {
		return ready;
	}
	
}
