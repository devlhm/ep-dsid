package eachare.repository;

import eachare.Message;
import eachare.Peer;
import eachare.PeerStatus;

import java.util.List;

public class NeighborList extends BaseRepository<Peer> {

    @Override
    public void add(Peer neighbor) {
        super.add(neighbor);
        System.out.println("Adicionando novo peer " + neighbor.getIpAddress() + ":" + neighbor.getPort() + " status " + neighbor.getStatus());
    }

    public void updateStatusByAddress(String address, int port, PeerStatus status) {
        for(Peer neighbor : entities) {
            if(neighbor.getIpAddress().equals(address) && neighbor.getPort() == port)
                neighbor.setStatus(status);
        }
    }

    public void updateByAddress(Peer peer) {
        for(Peer neighbor : entities) {
            if(neighbor.getIpAddress().equals(peer.getIpAddress()) && neighbor.getPort() == peer.getPort()) {
                neighbor.setStatus(peer.getStatus());
                neighbor.setClockValue(peer.getClockValue());
            }
        }
    }

    public boolean containsAddress(String address, int port) {
        for(Peer neighbor : entities) {
            if(neighbor.getIpAddress().equals(address) && neighbor.getPort() == port)
                return true;
        }

        return false;
    }

    public Peer getByAddress(String address, int port) {
        for(Peer neighbor : entities) {
            if(neighbor.getIpAddress().equals(address) && neighbor.getPort() == port)
                return neighbor;
        }

        return null;
    }

    public void onMessageReceived(Message message) {
        Peer sender = getByAddress(message.getOriginAddress(), message.getOriginPort());
        if(sender != null)
            sender.onMessageReceived(message);
    }

    public int getOnlineNumber (){
        int onlineNumber = 0;
        for (Peer peer : entities){
            if (peer.getStatus() == PeerStatus.ONLINE ) onlineNumber++;
        }
        return onlineNumber;
    }
}
