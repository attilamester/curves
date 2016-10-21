package network_packages;

public class EndOfRelationship extends SocketPackage {
	private static final long serialVersionUID = 1;
	
	public EndOfRelationship(int clientID) {
		super(clientID, SocketPackage.PACKAGE_BREAK_UP);
	}
}
