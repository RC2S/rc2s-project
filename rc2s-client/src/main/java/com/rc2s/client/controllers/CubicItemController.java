package com.rc2s.client.controllers;

import com.rc2s.common.client.utils.TabController;
import com.rc2s.client.components.LedCube;
import com.rc2s.common.utils.EJB;
import com.rc2s.common.client.utils.Resources;
import com.rc2s.common.exceptions.EJBException;
import com.rc2s.common.vo.Cube;
import com.rc2s.ejb.cube.CubeFacadeRemote;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CubicItemController extends TabController implements Initializable
{
	private final Logger log = LogManager.getLogger(this.getClass());
    
    private final CubeFacadeRemote cubeEJB = (CubeFacadeRemote)EJB.lookup("CubeEJB");
	
	private Cube cube;
	
	@FXML private AnchorPane root;
	@FXML private HBox display;
	@FXML private Label name;
	@FXML private Label ip;
	@FXML private Label status;
	
	@Override
    public void initialize(final URL url, final ResourceBundle rb)
	{
		root.setOnMouseClicked((MouseEvent e) -> {
			FXMLLoader loader = Resources.loadFxml("CubicDetailsView");
			CubicDetailsController controller = loader.getController();
			controller.setTab(getTab());
			controller.render(cube);

			getTab().setContent(loader.getRoot());
		});
	}
	
	@Override
	public void updateContent() {}
	
	public void update(final Cube cube)
	{
		this.cube = cube;
		
		LedCube ledCube = new LedCube(this.display, cube.getSize().getX(), 
                cube.getSize().getY(), cube.getSize().getZ(), 
                5., Color.web(cube.getColor()), null);
        
		this.display.getChildren().add(ledCube);
		
		this.name.setText(cube.getName());
		this.ip.setText(cube.getIp());

		this.status.setText("Probing...");
		new Thread()
		{
			@Override
			public void run()
			{
				Platform.runLater(() -> {
					try
					{
						String state = cubeEJB.getStatus(cube) ? "Online" : "Offline";
						Platform.runLater(() -> status.setText(state));
						log.info("Cube " + cube.getName() + " on " + cube.getIp() + " is " + state);
					}
					catch (EJBException e)
					{
						Platform.runLater(() -> {
							System.err.println(e.getMessage());
							status.setText("Offline");
						});
					}
				});
			}
		}.start();
	}
}