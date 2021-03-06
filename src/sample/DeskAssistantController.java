package sample;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * This method is for the desk assistant controller where all the action by the desk assitant are intitated and completed
 * @version 1.0
 * @author Romanov Andre
 * @author Shafi Mushfique
 * @author Stephen Aranda
 * @since 2019-09-21
 */
public class DeskAssistantController implements Initializable {

  @FXML
  private TableView<CustomerBooking> tableActivity;

  @FXML
  private TableColumn<?, ?> invoiceID;

  @FXML
  private TableColumn<?, ?> fullNametoDisplay;

  @FXML
  private TableColumn<?, ?> roomNo;

  @FXML
  private TableColumn<?, ?> dateToDisplay;

  @FXML
  private TableColumn<?, ?> amount;

  @FXML
  private TableColumn<?, ?> paymentStatus;
  @FXML
  private Button acceptPayment;

  @FXML
  private Button checkinCustomer;

  @FXML
  private Label idSpace1;
  @FXML
  private Button logoutButton;

  @FXML
  private TableColumn<?, ?> checkInStatus;
  private ObservableList<CustomerBooking> roomStatus;
  public static String userIdAlt = "NONE";

  @FXML
  void logOut(ActionEvent event) {
    Main.loggedInUser = null;
    MainScreenController msc = new MainScreenController();
    msc.loadScene(logoutButton, "MainScreenSample.fxml", "Main Screen");

  }

  /**
   * Method allows desk assistant to accept the payment from customer
   * @param event when payment screen recieve the payment
   * @throws SQLException sql exception error
   */
  @FXML
  void acceptPaymentAction(ActionEvent event) throws SQLException {
    MainScreenController msc = new MainScreenController();
    int index = tableActivity.getSelectionModel().getSelectedIndex();
    CustomerBooking invoiceNo = tableActivity.getItems().get(index);
    int newValue = invoiceNo.getInvoice();
    String paymentStatus = invoiceNo.getPaid();
    String checkinStatus = invoiceNo.getCheckinStatus();
    String customer = invoiceNo.getCname();
    DatabaseManager db = new DatabaseManager();
    String customerId = db.getCustomerUSerId(newValue);
    Main.tempName = customerId;
    userIdAlt = Main.loggedInUser;
    Main.loggedInUser = customerId;

    ClientScreenController.orderNo = newValue;
    Main.Type = "DeskAssistantScreen";
    if (paymentStatus.equals("NO")) {

      msc.loadScene(acceptPayment, "PaymentScreen.fxml", "Payment Screen");
    } else {
      Main.infoMessage("This booking is already paid");
    }


  }

  /**
   * Method allows a desk_assitant to checkin a customer
   *
   * Method decline to scheckin if the payment is not done
   * @param event checkin customer action
   *
   * @throws SQLException SQL EXCEPTION ERROR
   */
  @FXML
  void checkinCustomerAction(ActionEvent event) throws SQLException {
    MainScreenController msc = new MainScreenController();
    int index = tableActivity.getSelectionModel().getSelectedIndex();

    CustomerBooking invoiceNo = tableActivity.getItems().get(index);
    int newValue = invoiceNo.getInvoice();
    String paymentStatus = invoiceNo.getPaid();
    String checkinStatus = invoiceNo.getCheckinStatus();
    String roomName = invoiceNo.getRoom();
    ClientScreenController.orderNo = newValue;

    if (checkinStatus.equals("NO")) {
      if (paymentStatus.equals("YES")) {
        DatabaseManager db = new DatabaseManager();
        db.checkedIn(newValue, roomName);
        tableActivity.getSelectionModel().clearSelection();
        tableActivity.getItems().clear();

        roomStatus = FXCollections.observableArrayList(db.BookingStatus("all", "all"));
        tableActivity.setItems(roomStatus);
      } else {
        Main.infoMessage("This booking is not paid");
        msc.loadScene(acceptPayment, "PaymentScreen.fxml", "Payment Screen");


      }
    } else {
      Main.infoMessage("This booking is already checked in");
    }


  }

  /**
   * Initialize the table in the desk assitant screen so when they login they can see the room status
   */
  @Override
  public void initialize(URL url, ResourceBundle resources) {
    DatabaseManager db = null;
    if (userIdAlt.equals("NONE")) {
      Main.loggedInUser = Main.loggedInUser;
      idSpace1.setText(Main.loggedInUser);
    } else {
      Main.loggedInUser = userIdAlt;
      idSpace1.setText(userIdAlt);
    }
    try {
      db = new DatabaseManager();

    } catch (SQLException e) {
      e.printStackTrace();
    }
    roomStatus = FXCollections.observableArrayList(db.BookingStatus("all", "all"));
    tableActivity.setItems(roomStatus);


  }


}
