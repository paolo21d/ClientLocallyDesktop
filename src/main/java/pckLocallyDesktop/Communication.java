package pckLocallyDesktop;

import sun.awt.geom.AreaOp;

import java.io.*;
import java.net.*;

public class Communication {
    private int communicationPort = 10000;
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    BufferedReader inFromUser;
    DatagramSocket udpSocket;
    InetAddress IPAddress;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;

    public Communication(){
        inFromUser = new BufferedReader(new InputStreamReader(System.in));

        try {
            udpSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName(getBroadcast());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public boolean connect() throws Exception{
        System.out.println("Communication thread start");

        String sentence = new String("INIT_CONNECTION");
        sendData = sentence.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, communicationPort);
        udpSocket.send(sendPacket);
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        udpSocket.receive(receivePacket);
        IPAddress = receivePacket.getAddress();

        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + modifiedSentence);
        System.out.println(IPAddress);

        /*ObjectInputStream iStream = null;
        String messageStr=null;
        iStream = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
        messageStr = (String) iStream.readObject();
        iStream.close();
        System.out.println(messageStr);

        if(messageStr.equals(sentence))
            return  true;
        else return  false;*/

        return true;
    }
    private void sendData(String msg)throws Exception{
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(msg);
        oo.close();
        byte[] serializedMessage = bStream.toByteArray();
        sendData = serializedMessage;

        //sendData = sentence.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, communicationPort);
        udpSocket.send(sendPacket);
    }
    private String receiveData() throws Exception{
        udpSocket.receive(receivePacket);
        ObjectInputStream iStream = null;
        String messageStr=null;
        iStream = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
        messageStr = (String) iStream.readObject();
        iStream.close();

        return messageStr;
    }
    private String getBroadcast() throws UnknownHostException, SocketException {
        InetAddress IP=InetAddress.getLocalHost();
        String ip = new String(IP.getHostAddress());
        String [] parts = ip.split("\\.",0);
        short [] broadcast = new short[4];
        for (int i=0; i<4; i++){
            broadcast[i] = Short.valueOf(parts[i]);
        }

        InetAddress localHost = Inet4Address.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
        short mask = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();

        int binmask = 0x00000000;
        for(int i=0; i<mask; i++){
            binmask |= 1;
            binmask <<= 1;
        }
        binmask >>=1;

        binmask <<= (32-mask);
        //System.out.println("MASK:"+Integer.toBinaryString(binmask));

        int ad=0;
        ad = ad  | (broadcast[0]<<24) | (broadcast[1]<<16) | (broadcast[2]<<8) | (broadcast[3]);
        //System.out.println("IP:  "+Integer.toBinaryString(ad));

        int br = (ad&binmask) | (~binmask);
        //System.out.println("Brod:"+Integer.toBinaryString(br));
        int [] bradd = {(br&0xff000000)>>>24, (br&0x00ff0000)>>>16, (br&0x0000ff00)>>>8, br&0x000000ff};
        //String braddress = (br&0xff000000) + "." + br&0x00ff0000 + "." + br&0x0000ff00 + "." + br&0x000000ff;
        String braddress = bradd[0] + "." + bradd[1]+"."+bradd[2]+"."+bradd[3];
        //System.out.println(braddress);
        return  braddress;
    }
    //////////////////////////////
    public void comPlay() throws Exception{
        sendData("1");
    }
    public void comPause() throws Exception{
        sendData("2");
    }
    public void closeCommunication(){
        udpSocket.close();
    }
}
