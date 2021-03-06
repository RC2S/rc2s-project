package com.rc2s.client.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.rc2s.client.Main;
import com.rc2s.common.client.utils.Resources;
import com.rc2s.common.client.utils.Tools;
import com.rc2s.common.exceptions.EJBException;
import com.rc2s.common.utils.EJB;
import com.rc2s.common.utils.Hash;
import com.rc2s.common.vo.User;
import com.rc2s.ejb.user.UserFacadeRemote;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginController implements Initializable
{
	private final Logger log = LogManager.getLogger(this.getClass());
	
    @FXML private TextField ipField; 
    @FXML private TextField usernameField; 
    @FXML private PasswordField passwordField;
	@FXML private Button connectButton;
    @FXML private Label errorLabel;
    
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {}

    @FXML
    private void handleReturnPressed(final KeyEvent event)
    {
        if (event.getEventType() == KeyEvent.KEY_PRESSED
        && event.getCode() == KeyCode.ENTER)
            connect(event);
    }
	
    @FXML
    private void handleConnectButton(final ActionEvent event)
    {
        connect(event);
    }
	
    @FXML
    private boolean validateIpAddress(final KeyEvent event)
    {
        if (!Tools.matchIP(ipField.getText()))
        {
            errorLabel.setText("Invalid IP address");
            return false;
        }

        errorLabel.setText("");
        return true;
    }
	
    private void connect(final Event event)
    {
		String ip = ipField.getText();
        String username = usernameField.getText();
        String password = Hash.sha1(passwordField.getText());
		
		disable(true);

        if (validateIpAddress(null))
        {
			new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						// Init Programmatic Login
						Main.getProgrammaticLogin().login(username, password.toCharArray());

						// Init EJB context
						EJB.initContext(ip, null);

						UserFacadeRemote userEJB = (UserFacadeRemote) EJB.lookup("UserEJB");

						try
						{
							// Get the authenticated user
							User user = userEJB.getAuthenticatedUser(username, password);

                            if(user != null)
                            {
                                Tools.setAuthenticatedUser(user);

								log.info("Access granted for user " + user.getUsername());

								Platform.runLater(() -> {
									FXMLLoader loader = Resources.loadFxml("HomeView");
									Scene scene = new Scene((Parent) loader.getRoot());

									Main.getStage().setScene(scene);
									Main.getStage().setMinWidth(scene.getWidth());
									Main.getStage().setMinHeight(scene.getHeight());
									Main.getStage().show();
								});

								disable(false);
							}
							else
							{
								Platform.runLater(() -> errorLabel.setText("Authentication failed"));
								disable(false);
							}
						}
						catch (EJBException e)
						{
							log.error(e.getMessage());
							Platform.runLater(() -> errorLabel.setText("Authentication failed"));
							log.error("Authentication failed");
							disable(false);
						}
					}
					catch (Exception e)
					{
						log.error(e.getMessage());
						Platform.runLater(() -> errorLabel.setText("Unable to connect to the server"));
						disable(false);
					}
				}
			}.start();
        }
        else
		{
            errorLabel.setText("Invalid IP address");
			disable(false);
		}
    }
	
	private void disable(final boolean state)
	{
		Platform.runLater(() -> {
			ipField.setEditable(!state);
			ipField.setDisable(state);
			usernameField.setEditable(!state);
			usernameField.setDisable(state);
			passwordField.setEditable(!state);
			passwordField.setDisable(state);

			connectButton.setDisable(state);
		});
	}
}
