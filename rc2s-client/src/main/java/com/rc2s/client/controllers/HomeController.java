package com.rc2s.client.controllers;

import com.rc2s.common.client.utils.TabController;
import com.rc2s.common.client.utils.Resources;
import com.rc2s.common.client.utils.Tools;
import com.rc2s.common.exceptions.EJBException;
import com.rc2s.common.utils.EJB;
import com.rc2s.common.vo.Plugin;
import com.rc2s.common.vo.User;
import com.rc2s.ejb.plugin.PluginFacadeRemote;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HomeController implements Initializable
{
	private final Logger log = LogManager.getLogger(this.getClass());
    
	private final PluginFacadeRemote pluginEJB = (PluginFacadeRemote) EJB.lookup("PluginEJB");
	
    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
		initStaticTabs();
		initPlugins();
		initAdminTabs();
    }
	
	/**
	 * Loads all static client's tabs.
	 */
	private void initStaticTabs()
	{
		loadTab("Cubic List", Resources.loadFxml("CubicListView"));
	}
	
	private void initAdminTabs()
	{
		if (isAdmin(Tools.getAuthenticatedUser()))
		{
			loadTab("Access Management", Resources.loadFxml("AccessManagementView"));
			loadTab("Plugins Management", Resources.loadFxml("PluginsManagementView"));
		}
	}
	
	private void initPlugins()
	{
		try
		{
			List<Plugin> availablePlugins = pluginEJB.getAvailables();
			
			for (Plugin plugin : availablePlugins)
			{
				if (plugin.getAccess().equalsIgnoreCase("rc2s-usergrp")
				|| (plugin.getAccess().equalsIgnoreCase("rc2s-admingrp") && isAdmin(Tools.getAuthenticatedUser())))
				{
					log.info("Initializing plugin " + plugin.getName());
                    
                    String mainView = "/com/rc2s/" + plugin.getName().toLowerCase().replace(" ", "") + "/client/views/MainView.fxml";
					FXMLLoader loader = Resources.loadFxml(mainView);

					if (loader != null)
                    {
                        log.info("Loading plugin " + plugin.getName());
                        loadTab(plugin.getName(), loader);
                    }
                    else
                        log.error("Unable to load plugin " + plugin.getName());
				}
			}
		}
		catch (EJBException e)
		{
			log.error(e.getMessage());
		}
	}
	
	private void loadTab(final String name, final FXMLLoader loader)
	{
		Tab tab = new Tab();
		Label label = new Label(name);
		label.setStyle("-fx-rotate: 0; -fx-max-width: 100px; -fx-wrap-text: true; -fx-ellipsis-string: '...'");
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);

		tab.setGraphic(label);
		tab.setContent(loader.getRoot());

		Object rawController = loader.getController();

		if (rawController instanceof TabController)
		{
			TabController controller = (TabController)rawController;
			controller.setTab(tab);

			tab.setOnSelectionChanged((e) -> {
				if(tab.isSelected())
					controller.updateContent();
			});
		}

		tabPane.getTabs().add(tab);
	}
	
	/**
	 * Returns true if the given User is mapped with the ADMIN role.
	 * @param user
	 * @return 
	 */
	private boolean isAdmin(final User user)
	{
		return user.getGroups().stream().anyMatch((g) -> (g.getName().equalsIgnoreCase("rc2s-admingrp")));
	}
}
