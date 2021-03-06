package com.rc2s.streamingplugin.client.controllers;

import com.rc2s.common.client.utils.TabController;
import com.rc2s.common.client.utils.Dialog;
import com.rc2s.common.client.utils.Tools;
import com.rc2s.common.exceptions.EJBException;
import com.rc2s.common.utils.EJB;
import com.rc2s.common.vo.Synchronization;
import com.rc2s.streamingplugin.common.vo.Track;
import com.rc2s.streamingplugin.ejb.streaming.StreamingFacadeRemote;
import com.rc2s.ejb.synchronization.SynchronizationFacadeRemote;
import com.rc2s.streamingplugin.ejb.track.TrackFacadeRemote;
import com.rc2s.annotations.SourceControl;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import com.sun.jna.NativeLibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@SourceControl
public class MainController extends TabController implements Initializable
{
	private final Logger log = LogManager.getLogger(this.getClass());

    private final TrackFacadeRemote trackEJB = (TrackFacadeRemote) EJB.lookup("TrackEJB");

    private final SynchronizationFacadeRemote syncEJB = (SynchronizationFacadeRemote) EJB.lookup("SynchronizationEJB");
    
	private final StreamingFacadeRemote streamingEJB = (StreamingFacadeRemote) EJB.lookup("StreamingEJB");

	private final String META_TITLE     = "title";
	private final String META_ARTIST    = "xmpDM:artist";
	private final String META_DURATION  = "xmpDM:duration";
	private final String META_GENRE     = "xmpDM:genre";

	private MediaPlayer mediaPlayer;
	private StreamingHandlerUtils streamingHandler;
	private boolean playing     = false;
	private int currentTrack    = -1;

	private Map<Track, Metadata> tracksMetadata;

    @FXML private TableView<Track> tracksTable;
    @FXML private TableColumn<Track, String> musicColumn;
	@FXML private TableColumn<Track, String> authorColumn;
	@FXML private TableColumn<Track, String> timeColumn;
	@FXML private TableColumn<Track, String> genreColumn;

    @FXML private ComboBox<Synchronization> syncBox;
    @FXML private Button previousButton;
    @FXML private Button playPauseButton;
    @FXML private Button nextButton;
    @FXML private Label playingLabel;

    @Override
    public void initialize(final URL url, final ResourceBundle rb)
    {
		if (!new NativeDiscovery().discover() && System.getProperty("os.name").toLowerCase().contains("windows")) {
				//System.setProperty("jna.library.path", "C:\\Program Files\\VideoLAN\\VLC");
				NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
		}
		log.info("LibVLCJ instance version: " + LibVlc.INSTANCE.libvlc_get_version());

		tracksMetadata = new HashMap<>();

		musicColumn.setCellValueFactory(data -> new SimpleStringProperty(getValueFromMetadata(data.getValue(), META_TITLE)));
		authorColumn.setCellValueFactory(data -> new SimpleStringProperty(getValueFromMetadata(data.getValue(), META_ARTIST)));
		timeColumn.setCellValueFactory(data -> new SimpleStringProperty(getValueFromMetadata(data.getValue(), META_DURATION)));
		genreColumn.setCellValueFactory(data -> new SimpleStringProperty(getValueFromMetadata(data.getValue(), META_GENRE)));

		tracksTable.setRowFactory(table -> {
			TableRow<Track> row = new TableRow<>();

			row.setOnMouseClicked(e -> {
				if(!row.isEmpty() && e.getClickCount() == 2) {
					int index = tracksTable.getSelectionModel().getSelectedIndex();

					setTrack(index);
					play();
				}
			});

			return row;
		});
    }

	private String getValueFromMetadata(final Track track, final String metadataName)
	{
		Metadata metadata = tracksMetadata.get(track);

		if (metadata != null)
		{
			String value = metadata.get(metadataName);

			if (metadataName.equalsIgnoreCase(META_DURATION))
			{
				double duration = Double.parseDouble(value) / 1000.;
				int minutes = (int)(duration / 60.);
				int seconds = (int)(duration - (60. * minutes));

				StringBuilder sb = new StringBuilder().append(minutes).append(":");
				if (seconds < 10)
					sb.append("0");
				sb.append(seconds);
				
				value = sb.toString();
			}

			return value;
		}

		return "No such metadata: " + metadataName;
	}

	@Override
	public void updateContent()
	{
		updateTracks();
		updateSync();
	}

    private void updateTracks()
    {
        try
        {
			List<Track> tracks = trackEJB.getTracksByUser(Tools.getAuthenticatedUser());
			
			// Clear metadata HashMap
			tracksMetadata.clear();

			// Gather metadata
			for (Track track : tracks)
			{
				try
				{
					String filePath = Tools.replaceFile(URLDecoder.decode(track.getPath(), "UTF-8"));

					Parser parser = new AutoDetectParser();
					BodyContentHandler handler = new BodyContentHandler();
					Metadata metadata = new Metadata();
					FileInputStream is = new FileInputStream(filePath);
					ParseContext context = new ParseContext();

					parser.parse(is, handler, metadata, context);
					tracksMetadata.put(track, metadata);
				}
				catch (IOException | TikaException | SAXException e) {}
			}

			// Update table content
			tracksTable.getItems().clear();
			tracksTable.getItems().addAll(tracks);
        }
        catch (EJBException e)
        {
            Dialog.message("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateSync()
    {
        try
        {
            syncBox.getItems().clear();
            syncBox.getItems().addAll(syncEJB.getByUser(Tools.getAuthenticatedUser()));

			syncBox.getSelectionModel().selectFirst();
        }
        catch (EJBException e)
        {
            Dialog.message("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

	@FXML
	private void onSyncSelected(final ActionEvent e)
	{
		Synchronization sync = syncBox.getSelectionModel().getSelectedItem();

		if (sync == null)
		{
			playPauseButton.setDisable(true);
			nextButton.setDisable(true);
			previousButton.setDisable(true);
		}
		else if (playPauseButton.isDisabled())
		{
			playPauseButton.setDisable(false);
			nextButton.setDisable(false);
			previousButton.setDisable(false);
		}

		streamingEJB.setSynchronization(sync);
	}

	@FXML
	private void onKeyPressedEvent(final KeyEvent e)
	{
		if (e.getEventType() == KeyEvent.KEY_PRESSED)
		{
			if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE)
			{
				try
				{
					Track track = tracksTable.getSelectionModel().getSelectedItem();

					ButtonType answer = Dialog.confirm("Are you sure you want to remove this track from your playlist?");

					if (answer == ButtonType.OK)
					{
						trackEJB.delete(track);
						updateTracks();
					}
				}
				catch(EJBException ex)
				{
					Dialog.message("Error", ex.getMessage(), Alert.AlertType.ERROR);
				}
			}

			e.consume();
		}
	}

	@FXML
	private void onDragOverEvent(final DragEvent event)
	{
		Dragboard db = event.getDragboard();

		if (db.hasFiles())
			event.acceptTransferModes(TransferMode.COPY);
		else
			event.consume();
	}

	@FXML
	private void onDragDroppedEvent(final DragEvent event)
	{
		Dragboard db = event.getDragboard();
		boolean success = false;

		if (db.hasFiles())
		{
			success = true;

			for (File file : db.getFiles())
			{
				try
				{
					Track track = uriToTrack(file.toURI());
					trackEJB.add(track);
					updateTracks();
				}
				catch (EJBException | MediaException e)
				{
					Dialog.message("Error", e.getMessage(), Alert.AlertType.ERROR);
				}
			}
		}

		event.setDropCompleted(success);
		event.consume();
	}

	private Track uriToTrack(final URI uri) throws MediaException
	{
		Media media = new Media(uri.toString()); // If the file is not a media file, this will raise a MediaException

		Track track = new Track();
		track.setPath(uri.toString());
		track.setOrder(getNextOrder());
		track.setUser(Tools.getAuthenticatedUser());

		return track;
	}

	private short getNextOrder()
	{
		short next = 0;

		if (tracksTable.getItems() != null && tracksTable.getItems().size() != 0)
		{
			for (Track t : tracksTable.getItems())
			{
				if (t.getOrder() >= next)
					next = (short) (t.getOrder() + 1);
			}
		}

		return next;
	}

	@FXML
	private void onPreviousEvent(final ActionEvent e)
	{
		if (currentTrack != -1 && currentTrack > 0)
		{
			setTrack(currentTrack - 1);
			play();
		}
	}

	@FXML
	private void onNextEvent(final ActionEvent e)
	{
		if (currentTrack != -1 && currentTrack + 1 < tracksTable.getItems().size())
		{
			setTrack(currentTrack + 1);
			play();
		}
	}
    
    @FXML
    private void onPlayPauseEvent(final ActionEvent e)
    {
		if (mediaPlayer == null)
		{
			if(tracksTable.getItems().size() > 0)
			{
				setTrack(0);
				play();
			}
			else
				Dialog.message("Music Playlist", "You have no music tracks in your playlist at the moment", Alert.AlertType.INFORMATION);
		}
		else if (playing)
			pause();
		else
			play();
    }

	private synchronized void setTrack(final int trackIndex)
	{
		Track track = tracksTable.getItems().get(trackIndex);
		Media media = new Media(track.getPath());

		if (mediaPlayer != null)
		{
			mediaPlayer.stop();
			mediaPlayer.dispose();
		}
		mediaPlayer = new MediaPlayer(media);

		mediaPlayer.setOnEndOfMedia(() -> {
			try
			{
				if(tracksTable.getItems().get(trackIndex + 1) != null)
				{
					setTrack(trackIndex + 1);
					play();
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				synchronized (streamingHandler) {
					// End of the playlist
					mediaPlayer.stop();
					mediaPlayer.dispose();
					mediaPlayer = null;

					streamingHandler.stopStreaming();
					streamingHandler = null;
				}
			}
		});

		// Create/Update the streaming object
		try
		{
			// If it exists, we notify it to stop
			if (streamingHandler != null) {
				synchronized (streamingHandler) {
					streamingHandler.stopStreaming();
				}
			}

			streamingHandler = new StreamingHandlerUtils(
				streamingEJB,
				Tools.getAuthenticatedUser().getUsername(),
				Tools.replaceFile(URLDecoder.decode(track.getPath(), "UTF-8"))
			);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		playingLabel.setText(getValueFromMetadata(track, META_TITLE));
		currentTrack = trackIndex;
	}

	private void pause()
	{
		mediaPlayer.pause();

		if (streamingHandler != null && streamingHandler.isPlaying())
        {
			synchronized (streamingHandler) {
				streamingHandler.pauseStreaming();
			}
		}

		playing = false;
		playPauseButton.setText(">");
	}

	private void play()
	{
		Synchronization sync = syncBox.getSelectionModel().getSelectedItem();

		if(sync == null)
		{
			Dialog.message("Music Playlist", "You cannot stream a track without selecting a synchronization list", Alert.AlertType.ERROR);
			return;
		}

		mediaPlayer.play();

		if (streamingHandler != null) {
			synchronized (streamingHandler) {
				if (streamingHandler.getStreamingState() == StreamingUtils.StreamingState.INIT)
					streamingHandler.start();
				else if (!streamingHandler.isPlaying())
					streamingHandler.resumeStreaming();
			}
		}

		playing = true;
		playPauseButton.setText("||");
	}
}
