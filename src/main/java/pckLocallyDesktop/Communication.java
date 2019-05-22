package pckLocallyDesktop;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Communication {
    private final int communicationPort = 10000;
    private final int communicationPortTCP = 10001;
    //musi byc na odwrot receivePort i sendPort niz w aplikacji servera
    private final int sendPort = 10003;
    private final int receivePort = 10002;
    SendThread sendThread;
    ReceiveThread receiveThread;
    PlayerStatus status;
    Controller controller;
    boolean keepConnect = true;
    Gson json = new Gson();
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    //BufferedReader inFromUser;
    DatagramSocket udpSocket;
    InetAddress IPAddress;
    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    private String pin;

    public Communication(Controller c) {
        controller = c;
        //inFromUser = new BufferedReader(new InputStreamReader(System.in));
        sendThread = new SendThread();
        receiveThread = new ReceiveThread();
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public boolean initConnection() throws IOException {
        System.out.println("Communication thread start");
        try {
            udpSocket = new DatagramSocket();
            IPAddress = InetAddress.getByName(getBroadcast());
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        String sentence = pin;
        sendData = sentence.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, communicationPort);
        udpSocket.send(sendPacket);
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        udpSocket.receive(receivePacket);
        IPAddress = receivePacket.getAddress();

        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM SERVER: " + modifiedSentence);
        System.out.println(IPAddress);
        udpSocket.close();
        if (modifiedSentence.equals("NOCONNECTION"))
            return false;
        else
            return true;
    }

    public void TCPConnection() throws InterruptedException {
        ///TCP
        sendThread.start();
        receiveThread.start();

        //sendThread.join();
        //receiveThread.join();
    }

    private String getBroadcast() throws UnknownHostException, SocketException {
        InetAddress IP = InetAddress.getLocalHost();
        String ip = new String(IP.getHostAddress());
        String[] parts = ip.split("\\.", 0);
        short[] broadcast = new short[4];
        for (int i = 0; i < 4; i++) {
            broadcast[i] = Short.valueOf(parts[i]);
        }

        InetAddress localHost = Inet4Address.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
        short mask = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();

        int binmask = 0x00000000;
        for (int i = 0; i < mask; i++) {
            binmask |= 1;
            binmask <<= 1;
        }
        binmask >>= 1;

        binmask <<= (32 - mask);
        //System.out.println("MASK:"+Integer.toBinaryString(binmask));

        int ad = 0;
        ad = ad | (broadcast[0] << 24) | (broadcast[1] << 16) | (broadcast[2] << 8) | (broadcast[3]);
        //System.out.println("IP:  "+Integer.toBinaryString(ad));

        int br = (ad & binmask) | (~binmask);
        //System.out.println("Brod:"+Integer.toBinaryString(br));
        int[] bradd = {(br & 0xff000000) >>> 24, (br & 0x00ff0000) >>> 16, (br & 0x0000ff00) >>> 8, br & 0x000000ff};
        //String braddress = (br&0xff000000) + "." + br&0x00ff0000 + "." + br&0x0000ff00 + "." + br&0x000000ff;
        String braddress = bradd[0] + "." + bradd[1] + "." + bradd[2] + "." + bradd[3];
        //System.out.println(braddress);
        return braddress;
    }

    //////////////////////////////
    public void comPlayPause() throws Exception {
        Message message = new Message(MessageType.PLAYPAUSE);
        sendThread.message = json.toJson(message);
    }

    public void comNext() {
        Message message = new Message(MessageType.NEXT);
        sendThread.message = json.toJson(message);
    }

    public void comPrev() {
        Message message = new Message(MessageType.PREV);
        sendThread.message = json.toJson(message);
    }

    public void comReplay() {
        Message message = new Message(MessageType.REPLAY);
        sendThread.message = json.toJson(message);
    }

    public void comLoop() {
        Message message = new Message(MessageType.LOOP);
        sendThread.message = json.toJson(message);
    }

    public void comVolMute() {
        Message message = new Message(MessageType.VOLMUTE);
        sendThread.message = json.toJson(message);
    }

    public void comVolDown() {
        Message message = new Message(MessageType.VOLDOWN);
        sendThread.message = json.toJson(message);
    }

    public void comVolUp() {
        Message message = new Message(MessageType.VOLUP);
        sendThread.message = json.toJson(message);
    }

    public void comSetSong(Song song) {
        Message message = new Message(MessageType.SETSONG);
        message.song = song;
        sendThread.message = json.toJson(message);
    }

    public void comSetVolume(Double volume) {
        Message message = new Message(MessageType.SETVOLUME);
        message.volValue = volume;
        sendThread.message = json.toJson(message);
    }


    public enum MessageType {
        PLAYPAUSE, NEXT, PREV, REPLAY, LOOP, STATUS, VOLMUTE, VOLDOWN, VOLUP, SETSONG, SETVOLUME
    }

    class SendThread extends Thread { //TODO mozna przerobi zeby po wyslaniu sie usypial a jak sie chce wyslac to budzic do signalem
        public String message = "";
        //boolean keepConnect = true;
        private Socket clientSocket;
        private PrintWriter out;

        public void run() {
            try {
                clientSocket = new Socket(IPAddress, sendPort);
                System.out.println("Connected send");
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }


            while (keepConnect) {
                if (!message.equals("")) {
                    send(message);
                    message = "";
                }
            }
        }

        public void send(String msg) {
            System.out.println("Wysylam - IN PROGRESS");
            out.println(msg);
            System.out.println("Wyslano - DONE");
        }
    }

    class ReceiveThread extends Thread {
        //boolean keepConnect = true;
        String msg;
        private Socket clientSocket;
        private BufferedReader in;

        public void run() {
            try {
                clientSocket = new Socket(IPAddress, receivePort);
                System.out.println("Connected receive");
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (keepConnect) {
                msg = receive();
                if (msg == null) {
                    keepConnect = false;
                    break;
                }
                Gson json = new Gson();
                //status = json.fromJson(message, PlayerStatus.class);
                Message message = json.fromJson(msg, Message.class);
                if (message != null && message.messageType == MessageType.STATUS) {
                    controller.refreshInfo(message.statusMessage);
                }
                //System.out.println();
//                if (status != null)
//                    controller.refreshInfo(status);
                //System.out.println(status.path);
            }
        }

        public String receive() {
            String msg = "";
            try {
                msg = in.readLine();
            } catch (SocketException e) {
                System.out.println("Connection lost...");
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }
    }

    public class Message {
        MessageType messageType;
        String message;
        Song song;
        Double volValue;
        PlayerStatus statusMessage;

        public Message(MessageType type, PlayerStatus st) {
            messageType = type;
            statusMessage = st;
        }

        public Message(MessageType type) {
            messageType = type;
        }
    }
}
