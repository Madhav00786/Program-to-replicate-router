import java.util.*;

/*****************************************************************************************
 * // class Router 
 * // Purpose: This class represents a router 
 * Linda Crane , Madhav Sachdeva
 * CST8130 
 * // data members: 
 * // routingTable - to hold up to MaxEntries routing table entries 
 * // numEntries - number of entries currently in the table 
 * //maxEntries - maximum number of entries in table 
 * // methods: constructor 
 * //displayTable - displays results of "show ip route" command on device-ie entries in table 
 * // processPacket (Packet) - uses the parameter "packet" -processes it
 ***************************************************************************************/
class Router {
	private ArrayList<RoutingTableEntry> routingTable;

	public Router() { // default size
		routingTable = new ArrayList<RoutingTableEntry>(100);
		for (int i = 0; i < 100; i++) {
			routingTable.add(null);
		}

	}

	public void processPacket(Packet packet) {

		boolean found = false;
		String port = null;

		RoutingTableEntry routeToFind = new RoutingTableEntry(packet.getDestNetwork(), "");
		int indexWhereExists = findIndex(routeToFind);

		boolean addToTable = false;
		RoutingTableEntry routeToAdd = new RoutingTableEntry(packet.getDestNetwork(), packet.getPacketData());

		if (routingTable.get(indexWhereExists) == null) {

			
			//if (packet.processNotFoundPacket("")) {
				routingTable.add(indexWhereExists, routeToAdd);
				port = routingTable.get(indexWhereExists).searchForPort(packet.getDestNetwork());

				addToTable = packet.processNotFoundPacket(port);
				System.out.println("inserting at index " + indexWhereExists + " " + packet.destAddress);
			//}
		} else {
			port =(routingTable.get(indexWhereExists).searchForPort(packet.getDestNetwork())) ;
			if (port != (null)) {
				
				addToTable = packet.processFoundPacket(port);
			} else {
				addToTable = packet.processNotFoundPacket(port);
				if (addToTable) {
					int index = collisionResolution(indexWhereExists);
					if (index != -1) {
						if (addToTable) {
							routingTable.add(index, routeToAdd);
						}
					} else {
						System.out.println("cannot add.. already full");
					}
				}
			}

			/*
			 * port =
			 * routingTable.get(indexWhereExists).searchForPort(packet.getDestNetwork()); if
			 * (port != null) { found = true; }
			 */
		}

		// boolean addToTable = false;
		// if (found)
		// addToTable = packet.processFoundPacket(port);
		/*
		 * else addToTable = packet.processNotFoundPacket(port);
		 */
		/*
		 * if (addToTable) { RoutingTableEntry routeToAdd = new
		 * RoutingTableEntry(packet.getDestNetwork(), packet.getPacketData()); int
		 * indexToInsert = findIndex(routeToAdd);
		 * routingTable.add(indexToInsert,routeToAdd); }
		 */
	}

	public int findIndex(RoutingTableEntry toFind) {

		int index = toFind.calcHash();
		return index;

		/*
		 * int high = routingTable.size()-1; int low = 0;
		 * 
		 * while (low <= high) { int middle = (high+low)/2; if
		 * (routingTable.get(middle).isEqual(toFind)) return middle; if
		 * (routingTable.get(middle).isGreaterThan(toFind)) high = middle-1; else low =
		 * middle+1; } return low;
		 */
	}

	public int collisionResolution(int index) {
		int ind = -1;
		for (int i = index + 1; i < 100; i++) {
			if (routingTable.get(i) == (null)) {
				ind = i;
				break;
			}
		}
		return ind;
	}

	public void displayTable() {
		System.out.println("\nRouting table...\n");
		for (int i = 0; i < routingTable.size(); i++)
			System.out.println(routingTable.get(i));
	}
};
