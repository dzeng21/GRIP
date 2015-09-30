package edu.wpi.grip.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sun.javafx.collections.ObservableListWrapper;
import edu.wpi.grip.core.Connection;
import edu.wpi.grip.core.Socket;
import edu.wpi.grip.core.SocketHint;
import edu.wpi.grip.core.events.ConnectionAddedEvent;
import edu.wpi.grip.core.events.ConnectionRemovedEvent;
import edu.wpi.grip.core.events.SocketChangedEvent;
import edu.wpi.grip.core.events.SocketConnectedChangedEvent;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.adapter.JavaBeanBooleanProperty;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import org.controlsfx.control.RangeSlider;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A JavaFX control that renders a {@link Socket} that is the output of a step.  It shows a label with the identifier
 * of the output, as well as a handle for connections to other sockets, and some buttons to configure what do to with
 * the output.
 */
public class OutputSocketView extends HBox implements Initializable {

    @FXML
    private Label identifier;

    @FXML
    private ToggleButton publish;

    @FXML
    private StackPane handlePane;

    /**
     * The "handle" is a simple shape next ot the socket identifier that shows weather or not there is a connection
     * going to or from the socket.  If there is such a connection, the ConnectionView is rendered as a curve going
     * from one handle to another.
     */
    private SocketHandleView handle;

    private final EventBus eventBus;
    private final Socket socket;

    public OutputSocketView(EventBus eventBus, Socket<?> socket) {
        checkNotNull(eventBus);
        checkNotNull(socket);

        this.eventBus = eventBus;
        this.socket = socket;
        this.handle = new SocketHandleView(this.eventBus, this.socket);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OutputSocket.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.handlePane.getChildren().add(this.handle);

        // Show a button to publish the output to the current sink
        this.publish.setSelected(this.socket.isPublished());
        this.publish.selectedProperty().addListener(value -> this.socket.setPublished(this.publish.isSelected()));

        this.eventBus.register(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SocketHint<?> socketHint = this.socket.getSocketHint();
        Object[] domain = socketHint.getDomain();

        // Set the label on the control based on the identifier from the socket hint
        this.identifier.setText(socketHint.getIdentifier());
    }

    public Socket getSocket() {
        return this.socket;
    }

    public SocketHandleView getHandle() {
        return this.handle;
    }
}
