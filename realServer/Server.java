package realServer;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class Server {

  private static final String GESTURE_KEY = "gesture";
  private static final String FULL_NAME_KEY = "full_name";
  static boolean hasSelection = false;

  public static void main(String[] args) {
    server();
  }

  static void server() {
    try (ServerSocket server = new ServerSocket(1234)) {
      System.out.println("Waiting for connection");
      while (true) {
        Socket socket = server.accept();
        handleClientMessage(socket);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }

  private static void handleClientMessage(Socket socket) throws JSONException,
      AWTException, IOException {
    try (Scanner scanner = new Scanner(socket.getInputStream())) {
      System.out.println("Server connected");

      while (scanner.hasNext()) {
        String result = scanner.nextLine();
        System.out.println(result);
        JSONObject command = new JSONObject(result);

        String gesture = command.getString(GESTURE_KEY);
        String fullName = null;
        if (command.has(FULL_NAME_KEY)) {
          fullName = command.getString(FULL_NAME_KEY);
        }

        System.out.println(gesture);
        System.out.println(fullName);

        executeCommand(gesture, fullName);
      }
    }
  }

  private static void executeCommand(String gesture, String fullName)
      throws AWTException {
    Robot r = new Robot();
    switch (gesture) {
    case "left":// input start
      r.keyPress(KeyEvent.VK_LEFT);
      r.keyRelease(KeyEvent.VK_LEFT);
      break;
    case "right":// input start
      r.keyPress(KeyEvent.VK_RIGHT);
      r.keyRelease(KeyEvent.VK_RIGHT);
      break;
    case "up":// input start
      r.keyPress(KeyEvent.VK_UP);
      r.keyRelease(KeyEvent.VK_UP);
      break;
    case "down":// input start
      r.keyPress(KeyEvent.VK_DOWN);
      r.keyRelease(KeyEvent.VK_DOWN);
      break;

    case "open":// input start
      if (fullName == null) {
        if (!hasSelection) {
          openMyComputer(r);
        } else {
          // hit enter
          r.keyPress(KeyEvent.VK_ENTER);
          r.keyRelease(KeyEvent.VK_ENTER);
        }
      } else {
        // open movie file
        startPlayingMovie(fullName);
      }
      break;
    case "close":
      // close movie file
      closeMovie(r);
      break;

    case "forward":
      forwardMovie(r);
      break;
    case "backward":
      backwardMovie(r);
      break;
    case "vol_up":
      volumeUp(r);
      break;
    case "vol_down":
      volumeDown(r);
      break;
    }
  }

  private static void closeMovie(Robot r) {
    // call omxplayer close - play -- omxplayer --name fullName
    r.keyPress(KeyEvent.VK_ALT);
    r.keyPress(KeyEvent.VK_ALT);
    r.keyRelease(KeyEvent.VK_F4);
    r.keyRelease(KeyEvent.VK_F4);
  }

  private static void volumeDown(Robot r) {
    // – decrease volume
    r.keyPress(KeyEvent.VK_MINUS);
    r.keyRelease(KeyEvent.VK_MINUS);
  }

  private static void volumeUp(Robot r) {
    r.keyPress(KeyEvent.VK_EQUALS);
    r.keyRelease(KeyEvent.VK_EQUALS);
  }

  private static void backwardMovie(Robot r) {
    r.keyPress(KeyEvent.VK_DOWN);
    r.keyRelease(KeyEvent.VK_DOWN);
  }

  private static void forwardMovie(Robot r) {
    r.keyPress(KeyEvent.VK_UP);
    r.keyRelease(KeyEvent.VK_UP);
  }

  private static void startPlayingMovie(String fullName) {
    executeOnCommandLine("omxplayer -r -o hdmi " + fullName);
  }

  private static void openMyComputer(Robot r) {
    // open My Computer
    executeOnCommandLine("nautilus /");

    // hit right
    r.keyPress(KeyEvent.VK_RIGHT);
    r.keyRelease(KeyEvent.VK_RIGHT);
  }

  private static void executeOnCommandLine(String command) {
    try {
      Runtime rt = Runtime.getRuntime();
      Process p = rt.exec(command);
      if (p.exitValue() != 0) {
        System.err.println("Open My computer fails " + p.exitValue());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
