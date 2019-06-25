package network.impl;

import com.google.inject.Inject;
import com.sun.istack.internal.NotNull;
import controller.ChatFunctionalities;
import model.*;
import model.FileDescriptor;
import model.enums.RequestType;
import network.InputStreamListener;
import network.ServerServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerServicesImpl implements ServerServices, InputStreamListener {

    private static final Logger logger = LogManager.getLogger(ServerServicesImpl.class);

    @Inject private ChatFunctionalities chatController;
    @Inject private InputStreamReaderImpl isReader;

    private Socket connection = null;
    private ObjectOutputStream writer = null;

    //Files sent/to send.
    private Map<Long, File> files;

    //Files received/to receive.
    private Map<Long, List<FileContent>> fileContents = new HashMap<>();
    private Map<Long, FileDescriptor> fileDescriptors = new HashMap<>();

    //Audios sent/to send.
    private Map<Long, byte[]> audios;

    //Audios received/to receive.
    private Map<Long, List<AudioContent>> audioContents = new HashMap<>();
    private Map<Long, AudioDescriptor> audioDescriptors = new HashMap<>();

    private Request buildRequest(final RequestType type, Content content, final User destination) {
        return Request.newInstance(type, content, destination);
    }

    private synchronized void sendRequest(@NotNull final Request request) throws IOException {
        if (writer != null) {
            logger.info("Request : " + request);
            writer.writeObject(request);
            writer.flush();
        }
    }

    @Override
    public void handleResponses(Response response) {
        logger.info("Handle Responses called.");
        logger.info(response);
        switch (response.getType()) {
            case CONNECTED: {
                chatController.initializeContext((ContextContent)response.getContent());
                break;
            }
            case ADD_USER: {
                chatController.addUser(response.getSource());
                break;
            }
            case REMOVE_USER: {
                chatController.removeUser(response.getSource());
                break;
            }
            case MESSAGE: {
                chatController.receiveMessage(
                        MessageContainer.newInstance(
                            response.getSource(),
                            (MessageContent)response.getContent()));
                break;
            }
            case PREPARE_RECEIVE_FILE: {
                try {
                    prepareReceiveFile((FileDescriptor) response.getContent(), response.getSource());
                } catch (IOException e) {
                    logger.error(e);
                }
                break;
            }
            case FILE_MESSAGE: {
                chatController.receiveMessage(
                        MessageContainer.newInstance(
                                response.getSource(),
                                (FileMessageContent)response.getContent()));
                break;
            }
            case FILE_CHUNK: {
                receiveFile((FileContent) response.getContent());
                break;
            }
            case PREPARE_RECEIVE_AUDIO: {
                try {
                    prepareReceiveAudio((AudioDescriptor) response.getContent(), response.getSource());
                } catch (IOException e) {
                    logger.error(e);
                }
                break;
            }
            case AUDIO_MESSAGE: {
                chatController.receiveMessage(
                        MessageContainer.newInstance(
                                response.getSource(),
                                (AudioMessageContent)response.getContent()));
                break;
            }
            case AUDIO_CHUNK: {
                receiveAudio((AudioContent) response.getContent());
                break;
            }
            case MESSAGE_SENT: {
                logger.info("Message Sent.");
                break;
            }
            case CAN_SEND_FILE: {
                try {
                    sendFile(response.getSource(), ((FileBasicInformation)response.getContent()).getFileId());
                } catch (IOException e) {
                    logger.error(e);
                }
                break;
            }
            case FILE_SENT: {
                chatController.fileSent(
                        MessageContainer.newInstance(
                            response.getSource(),
                            (FileMessageContent)response.getContent()));
                break;
            }
            case CAN_SEND_AUDIO: {
                try {
                    sendAudio(response.getSource(), ((AudioBasicInformation)response.getContent()).getAudioId());
                } catch (IOException e) {
                    logger.error(e);
                }
                break;
            }
            case AUDIO_SENT: {
                chatController.audioSent(
                        MessageContainer.newInstance(
                                response.getSource(),
                                (AudioMessageContent)response.getContent()));
                break;
            }
            case INSUFFICIENT_MEMORY: {
                logger.error("Insufficient Memory.");
                break;
            }
            case DESTINATION_NOT_FOUND: {
                logger.error("Destination Not Found.");
                break;
            }
            case WRONG_PARAMETERS: {
                logger.error("Wrong Parameter.");
                break;
            }
            case INTERNAL_SERVER_ERROR: {
                logger.error("Internal Server Error.");
                break;
            }
        }
    }

    @Override
    public void connect(Credentials credentials, String host, int port) throws IOException {
        //Initialize the connection to the server.
        connection = new Socket(host, port);
        final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(connection.getInputStream()));
        if (!reader.readBoolean()) {
            logger.error("An error occurred while initializing the connection.");
            return;
        }
        writer = new ObjectOutputStream(new BufferedOutputStream(connection.getOutputStream()));

        //Initialize the InputStreamReader and let it run in another Thread.
        isReader.open(reader);
        isReader.addListener(this);
        Thread t = new Thread(isReader);
        t.start();

        //Send the credentials to validate the connection.
        this.sendRequest(this.buildRequest(RequestType.CONNECT, credentials, null));
    }

    @Override
    public void disconnect() {
        try {
            this.sendRequest(this.buildRequest(RequestType.DISCONNECT, null, null));
            isReader.close();
            this.connection.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void sendMessage(MessageContainer message) throws IOException   {
        this.sendRequest(this.buildRequest(RequestType.SEND_MESSAGE, message.getMessageContent(), message.getUser()));
    }

    @Override
    public void checkSendFile(User destination, File file) throws IOException  {
        final FileDescriptor fileDescriptor = FileDescriptor.newInstance(
                file.length()/FileContent.MAX_BYTE_SIZE + 1,
                file.getName());
        this.sendRequest(this.buildRequest(RequestType.PREPARE_SEND_FILE, fileDescriptor, destination));

        if (this.files == null) this.files = new HashMap<>();
        files.put(fileDescriptor.getFileId(), file);
    }

    @Override
    public void sendFile(User destination, long fileId) throws IOException  {
        int read = 0, chunkNumber = 1;
        final File file = files.get(fileId);

        if (file == null) {
            logger.error("Unknown fileId received.");
            return;
        }

        final FileInputStream fis = new FileInputStream(file);
        while (read != -1 && !connection.isClosed()) {
            byte[] bytes = new byte[FileContent.MAX_BYTE_SIZE];
            read = fis.read(bytes, 0, FileContent.MAX_BYTE_SIZE);
            if (read != -1) {
                if (read != bytes.length) {
                    bytes = Arrays.copyOf(bytes, read);
                }
                final FileContent fileContent = FileContent.newInstance(
                        fileId,
                        chunkNumber,
                        bytes);
                this.sendRequest(this.buildRequest(RequestType.SEND_FILE, fileContent, destination));
                chunkNumber++;
            }
        }
        fis.close();
    }

    @Override
    public void requestFile(User source, long fileId) throws IOException  {
        this.sendRequest(this.buildRequest(
                RequestType.PREPARE_REQUEST_FILE,
                FileBasicInformation.newInstance(fileId),
                source));
    }

    @Override
    public void prepareReceiveFile(FileDescriptor fileDescriptor, User source) throws IOException {
        //TODO: Test if there's enough memory available before sending the response.
        fileDescriptors.put(fileDescriptor.getFileId(), fileDescriptor);
        fileContents.put(fileDescriptor.getFileId(), new LinkedList<>());
        this.sendRequest(buildRequest(
                RequestType.REQUEST_FILE,
                FileBasicInformation.newInstance(fileDescriptor.getFileId()),
                source));
    }

    @Override
    public void receiveFile(FileContent fileContent) {
        final List<FileContent> list = fileContents.get(fileContent.getFileId());
        list.add(fileContent);

        final FileDescriptor fileDescriptor = fileDescriptors.get(fileContent.getFileId());
        if (list.size() == fileDescriptor.getChunksTotalNumber()) {
            chatController.receiveFile(fileDescriptor, list);
        }
    }

    @Override
    public void checkSendAudio(User destination, byte[] audio) throws IOException {
        final AudioDescriptor audioDescriptor = AudioDescriptor.newInstance(
                audio.length/AudioContent.MAX_BYTE_SIZE + 1);
        this.sendRequest(this.buildRequest(RequestType.PREPARE_SEND_AUDIO, audioDescriptor, destination));

        if (this.audios == null) this.audios = new HashMap<>();
        audios.put(audioDescriptor.getAudioId(), audio);
    }

    @Override
    public void sendAudio(User destination, long audioId) throws IOException {
        final byte[] audio = audios.get(audioId);

        if (audio == null) {
            logger.error("Unknown audioId received.");
            return;
        }

        final long totalChunksNumber = audio.length/AudioContent.MAX_BYTE_SIZE + 1;
        //Used during the last iteration to not create an array bigger than what is
        //needed to store what is left of the audio.
        long minus = 0;

        for (int i = 0; i < totalChunksNumber && !connection.isClosed(); i++) {
            long length = (i+1)*AudioContent.MAX_BYTE_SIZE;
            if (length > audio.length) minus = length - audio.length;

            byte[] bytes = Arrays.copyOfRange(audio,
                    Math.toIntExact(i*AudioContent.MAX_BYTE_SIZE),
                    Math.toIntExact(length - minus));

            final AudioContent audioContent = AudioContent.newInstance(audioId, i, bytes);
            this.sendRequest(this.buildRequest(RequestType.SEND_AUDIO, audioContent, destination));
        }
    }

    @Override
    public void requestAudio(User source, long audioId) throws IOException {
        this.sendRequest(this.buildRequest(
                RequestType.PREPARE_REQUEST_AUDIO,
                AudioBasicInformation.newInstance(audioId),
                source));
    }

    @Override
    public void prepareReceiveAudio(AudioDescriptor audioDescriptor, User source) throws IOException {
        //TODO: Test if there's enough memory available before sending the response.
        audioDescriptors.put(audioDescriptor.getAudioId(), audioDescriptor);
        audioContents.put(audioDescriptor.getAudioId(), new LinkedList<>());
        this.sendRequest(buildRequest(
                RequestType.REQUEST_AUDIO,
                AudioBasicInformation.newInstance(audioDescriptor.getAudioId()),
                source));
    }

    @Override
    public void receiveAudio(AudioContent audioContent) {
        final List<AudioContent> list = audioContents.get(audioContent.getAudioId());
        list.add(audioContent);

        final AudioDescriptor audioDescriptor = audioDescriptors.get(audioContent.getAudioId());
        if (list.size() == audioDescriptor.getChunksTotalNumber()) {
            chatController.receiveAudio(audioDescriptor, list);
        }
    }
}
