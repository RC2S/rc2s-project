package com.rc2s.client.controllers;

import com.rc2s.common.client.utils.TabController;
import com.rc2s.client.components.LedCube;
import com.rc2s.client.components.LedEvent;
import com.rc2s.common.client.utils.Dialog;
import com.rc2s.common.utils.EJB;
import com.rc2s.common.client.utils.Resources;
import com.rc2s.common.client.utils.Tools;
import com.rc2s.common.exceptions.EJBException;
import com.rc2s.common.vo.Cube;
import com.rc2s.common.vo.Size;
import com.rc2s.common.vo.Synchronization;
import com.rc2s.common.vo.User;
import com.rc2s.ejb.cube.CubeFacadeRemote;
import com.rc2s.ejb.size.SizeFacadeRemote;
import com.rc2s.ejb.synchronization.SynchronizationFacadeRemote;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javax.validation.ConstraintViolation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CubicDetailsController extends TabController implements Initializable
{
	private final Logger log = LogManager.getLogger(this.getClass());
	
	private final CubeFacadeRemote cubeEJB = (CubeFacadeRemote) EJB.lookup("CubeEJB");
    
	private final SizeFacadeRemote sizeEJB = (SizeFacadeRemote) EJB.lookup("SizeEJB");
    
	private final SynchronizationFacadeRemote synchronizationEJB = (SynchronizationFacadeRemote) EJB.lookup("SynchronizationEJB");
	
	private Cube cube;
    
	private LedCube ledCube;
	
	@FXML private HBox display;
	@FXML private Button backButton;
	@FXML private Label errorLabel;
	
	@FXML private Button allOnButton;
	@FXML private Button allOffButton;
	
	// Cube
	@FXML private Label nameLabel;
	@FXML private TextField nameField;
	
	@FXML private Label ipLabel;
	@FXML private TextField ipField;
	
	@FXML private Label colorLabel;
	@FXML private ComboBox colorBox;
	
	@FXML private Label sizeLabel;
	
	// Size
	@FXML private HBox chooseSizeHbox;
	@FXML private ComboBox sizeBox;
	@FXML private Button addSizeButton;
	
	@FXML private HBox newSizeBox;
	@FXML private TextField newSizeName;
	@FXML private TextField newSizeX;
	@FXML private TextField newSizeY;
	@FXML private TextField newSizeZ;
	
	@FXML private Label statusLabel;
	
	// Controls
	@FXML ToggleButton editButton;
	@FXML Button addButton;
	@FXML Button removeButton;
	
	// Synchronization
	@FXML private ComboBox cubesBox;
	@FXML private Button addCubeButton;
	
	@FXML private ListView synchronizedList;
	@FXML private Label synchronizedLabel;
	@FXML private TextField synchronizedField;
	
	@Override
	public void initialize(final URL url, final ResourceBundle rb)
	{
		try
		{
			// Gather sizes
			sizeBox.getItems().addAll(sizeEJB.getAll());

			// Init colors
			colorBox.getItems().addAll("RED", "GREEN", "YELLOW");

			// Gather cubes (all, only available for this user... ?)
			cubesBox.getItems().addAll(cubeEJB.getCubes(Tools.getAuthenticatedUser()));
		}
		catch (EJBException e)
		{
			error(e.getMessage());
		}
	}
	
	@Override
	public void updateContent() {}
	
	public void initEmpty()
	{
		allOnButton.setVisible(false);
		allOffButton.setVisible(false);
		
		editButton.setVisible(false);
		removeButton.setVisible(false);
		cubesBox.setDisable(true);
		addCubeButton.setDisable(true);
		addButton.setVisible(true);
		
		cube = new Cube();
		cube.setSynchronization(new Synchronization());
		cube.setSynchronizations(Arrays.asList(new Synchronization[] {cube.getSynchronization()}));
		cube.getSynchronization().setCubes(Arrays.asList(new Cube[] {cube}));
		cube.getSynchronization().setUsers(Arrays.asList(new User[] {Tools.getAuthenticatedUser()}));
		
		cube.setCreated(new Date());
		cube.getSynchronization().setCreated(new Date());
		
		ledCube = new LedCube(this.display, 4., 4., 4., 10., Color.BLACK, null);
		display.getChildren().add(ledCube);
		
		toggleEditCube();
	}
	
	public void render(final Cube cube)
	{
		this.cube = cube;
		
		updateDisplay();
		
		nameLabel.setText(cube.getName());
		nameField.setText(cube.getName());
		
		ipLabel.setText(cube.getIp());
		ipField.setText(cube.getIp());
		
		colorLabel.setText(cube.getColor());
		colorBox.getSelectionModel().select(cube.getColor().toUpperCase());
		
		sizeLabel.setText(cube.getSize().getName());
		sizeBox.getSelectionModel().select(cube.getSize());

		statusLabel.setText("Probing...");
		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					boolean state = cubeEJB.getStatus(cube);
					Platform.runLater(() -> statusLabel.setText(state ? "Online" : "Offline"));
				}
				catch (EJBException e)
				{
					Platform.runLater(() -> {
						error(e.getMessage());
						statusLabel.setText("Offline");
					});
				}
			}
		}.start();
		
		synchronizedLabel.setText(cube.getSynchronization().getName());
		synchronizedField.setText(cube.getSynchronization().getName());
		synchronizedList.getItems().clear();
		synchronizedList.getItems().addAll(cube.getSynchronization().getCubes());
		
		if (cube.getId() != null)
		{
			cubesBox.getItems().remove(cube);
			synchronizedList.getItems().remove(cube);
		}

		for (Cube sync : cube.getSynchronization().getCubes())
			cubesBox.getItems().remove(sync);
	}
	
	private void updateDisplay()
	{
		Color color = (cube.getColor() != null) ? Color.web(cube.getColor()) : null;
		Size size = cube.getSize();
		
		ledCube = new LedCube(
			this.display,
			(size != null) ? size.getX() : 4.,
			(size != null) ? size.getY() : 4.,
			(size != null) ? size.getZ() : 4.,
			10.,
			color,
			allOnButton.isVisible() ? onToggleLed() : null
		);
		display.getChildren().clear();
		display.getChildren().add(ledCube);
	}
	
	private void toggleEditCube()
	{
		allOnButton.setDisable(!allOnButton.isDisable());
		allOffButton.setDisable(!allOffButton.isDisable());
		
		nameLabel.setVisible(!nameLabel.isVisible());
		nameField.setVisible(!nameField.isVisible());
				
		ipLabel.setVisible(!ipLabel.isVisible());
		ipField.setVisible(!ipField.isVisible());
	
		colorLabel.setVisible(!colorLabel.isVisible());
		colorBox.setVisible(!colorBox.isVisible());
	
		sizeLabel.setVisible(!sizeLabel.isVisible());
		chooseSizeHbox.setVisible(!chooseSizeHbox.isVisible());
		
		synchronizedLabel.setVisible(!synchronizedLabel.isVisible());
		synchronizedField.setVisible(!synchronizedField.isVisible());

		if(synchronizedLabel.isVisible() && newSizeBox.isVisible())
			toggleEditSize();
	}
	
	private void toggleEditSize()
	{
		newSizeName.setVisible(!newSizeName.isVisible());
		newSizeBox.setVisible(!newSizeBox.isVisible());
	}
	
	@FXML
	private void onBackEvent(final ActionEvent e)
	{
		FXMLLoader loader = Resources.loadFxml("CubicListView");
		CubicListController controller = loader.getController();
		controller.setTab(getTab());
		controller.updateContent();
		getTab().setContent(loader.getRoot());
	}
	
	private boolean updateCube()
	{
		cube.setName(nameField.getText());
		cube.setIp(ipField.getText());
		
		cube.getSynchronization().setName(synchronizedField.getText().isEmpty()
										? nameField.getText()
										: synchronizedField.getText());
		
		String color = (String) colorBox.getSelectionModel().getSelectedItem();
		
        if (color != null)
			cube.setColor(color);
		
		// If we created a new Size value
		if (newSizeBox.isVisible())
		{
			Size size = new Size();
			size.setName(newSizeName.getText());
			
			try
			{
				size.setX(Integer.parseInt(newSizeX.getText()));
				size.setY(Integer.parseInt(newSizeY.getText()));
				size.setZ(Integer.parseInt(newSizeZ.getText()));
				size.setCreated(new Date());
				
				Set<ConstraintViolation<Size>> violations = Tools.validate(size);
				
				if (!violations.isEmpty())
				{
					for (ConstraintViolation<Size> v : violations)
					{
						errorLabel.setText(v.getRootBeanClass().getSimpleName() + "." + v.getPropertyPath() + " " + v.getMessage());
						break;
					}
					
					cube.setSize(null);
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				errorLabel.setText(e.getMessage());
				cube.setSize(null);
				return false;
			}
			
			cube.setSize(size);
		}
		// Using an existing Size
		else
		{
			Size size = (Size) sizeBox.getSelectionModel().getSelectedItem();
			if (size != null)
				cube.setSize(size);
		}
		
		return true;
	}
	
	@FXML
	private void onEditEvent(final ActionEvent e)
	{
		ToggleButton btn = (ToggleButton)e.getSource();
		errorLabel.setText("");
			
		if (!btn.isSelected())
		{
			boolean update = updateCube();
			
			if (update)
			{
				Set<ConstraintViolation<Cube>> violations = Tools.validate(cube);

				if (violations.isEmpty())
				{
					try
					{
						cube = cubeEJB.update(cube);
						toggleEditCube();
					}
					catch (EJBException ex)
					{
						error(ex.getMessage());
					}
				}
				else
				{
					btn.setSelected(true);
					for (ConstraintViolation<Cube> v : violations)
					{
						errorLabel.setText(v.getRootBeanClass().getSimpleName() + "." + v.getPropertyPath() + " " + v.getMessage());
						break;
					}
				}
				
				// Update the UI
				render(cube);
			}
		}
		else
		{
			toggleEditCube();
		}
	}
	
	@FXML
	private void onAddEvent(final ActionEvent e)
	{
		boolean update = updateCube();
		
		if (update)
		{
			Set<ConstraintViolation<Cube>> violations = Tools.validate(cube);

			if (violations.isEmpty())
			{
				try
				{
					if (newSizeBox.isVisible())
					{
						Size newSize = sizeEJB.add(cube.getSize());
						cube.setSize(newSize);
					}
					cubeEJB.add(cube);
                    log.info("Add cube " + cube.getName());
					
					onBackEvent(null);
				}
				catch (EJBException ex)
				{
					error(ex.getMessage());
				}
			}
			else
			{
				for (ConstraintViolation<Cube> v : violations)
				{
					errorLabel.setText(v.getRootBeanClass().getSimpleName() + "." + v.getPropertyPath() + " " + v.getMessage());
					break;
				}
			}
		}
	}
	
	@FXML
	private void onRemoveEvent(final ActionEvent e)
	{
		ButtonType answer = Dialog.confirm("Are you sure you want to definitely remove this cube from the RC2S Server?");
		
		if (answer == ButtonType.OK)
		{
			try
			{
				cubeEJB.remove(cube);
                log.info("Remove cube " + cube.getName());
                
				onBackEvent(null);
			}
			catch (EJBException ex)
			{
				error(ex.getMessage());
			}
		}
	}
	
	@FXML
	private void onColorChanged(final ActionEvent e)
	{
		ComboBox box = (ComboBox) e.getSource();
		Object o = box.getSelectionModel().getSelectedItem();
		
		if (o != null && o instanceof String)
		{
			cube.setColor((String)o.toString());
			updateDisplay();
		}
	}
	
	@FXML
	private void onSizeChanged(final ActionEvent e)
	{
		ComboBox box = (ComboBox) e.getSource();
		Object o = box.getSelectionModel().getSelectedItem();
		
		if (o != null && o instanceof Size)
		{
			cube.setSize((Size)o);
			updateDisplay();
		}
	}
	
	@FXML
	private void onAddSizeEvent(final ActionEvent e)
	{
		toggleEditSize();
	}
	
	@FXML
	private void onCancelSizeEvent(final ActionEvent e)
	{
		toggleEditSize();
	}
	
	@FXML
	private void onAddCubeEvent(final ActionEvent e)
	{
		Cube c = (Cube) cubesBox.getSelectionModel().getSelectedItem();
		
		if (c != null)
		{
			try
			{
				synchronizedList.getItems().add(c);
				cubesBox.getItems().remove(c);
				cube.getSynchronization().getCubes().add(c);
				
				cubeEJB.update(cube);
                log.info("Update cube " + cube.getName());
			}
			catch (EJBException ex)
			{
				error(ex.getMessage());
			}
		}
	}
	
	@FXML
	private void onListKeyEvent(final KeyEvent e)
	{
		if (e.getEventType() == KeyEvent.KEY_PRESSED)
		{
			if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE)
			{
				try
				{
					ObservableList<Cube> cubes = synchronizedList.getSelectionModel().getSelectedItems();

					for (Cube c : cubes)
					{
						synchronizedList.getItems().remove(c);
						cubesBox.getItems().add(c);
						cube.getSynchronization().getCubes().remove(c);
					}

					cubeEJB.update(cube);
                    log.info("Update cube " + cube.getName());
				}
				catch (EJBException ex)
				{
					error(ex.getMessage());
				}
			}
			
			e.consume();
		}
	}
	
	@FXML
	private void onAllOnEvent(final ActionEvent e)
	{
		try
		{
			cubeEJB.updateAllLed(cube, true);
			ledCube.setActivated(true);
            
            log.info("Enable all LEDs for cube " + cube.getName());
		}
		catch (EJBException ex)
		{
			error(ex.getMessage());
		}
	}
	
	@FXML
	private void onAllOffEvent(final ActionEvent e)
	{
		try
		{
			cubeEJB.updateAllLed(cube, false);
			ledCube.setActivated(false);
            
            log.info("Disable all LEDs for cube " + cube.getName());
		}
		catch (EJBException ex)
		{
			error(ex.getMessage());
		}
	}
	
	public LedEvent onToggleLed()
	{
		return new LedEvent() {
			@Override
			public void run()
			{
				try
				{
					cubeEJB.updateAllLed(cube, ledCube.getStateArray());
                    
                    log.info("Toggle all LEDs for cube " + cube.getName());
				}
				catch(EJBException e)
				{
					error(e.getMessage());
				}
			}
		};
	}
	
	private void error(String err)
	{
		log.error(err);
		errorLabel.setText(err);
	}
}