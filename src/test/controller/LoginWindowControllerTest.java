package test.controller;

import com.barosanu.EmailManager;
import com.barosanu.controller.LoginWindowController;
import com.barosanu.controller.services.LoginService;
import com.barosanu.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LoginWindowControllerTest {

    @Mock
    private ViewFactory viewFactoryMock;
    @Mock
    private EmailManager emailManagerMock;

    private TextField emailAddressField;

    private PasswordField passwordField;

    private Label errorLabel;

    private LoginWindowController loginWindowController;

    @Mock
    private LoginService loginServiceMock;

    @BeforeAll
    public static void setUpToolkit(){
        Platform.startup(() -> System.out.println("Toolkit initialized ..."));

    }

    @BeforeEach
    public void setUp(){
        initMocks(this);
        emailAddressField = new TextField();
        passwordField = new PasswordField();
        errorLabel = new Label();

        loginWindowController = new LoginWindowController(
                viewFactoryMock,
                emailManagerMock,
                null,
                emailAddressField,
                passwordField,
                errorLabel,
                loginServiceMock
        );
    }

    @Test
    public void testFieldsValidation(){
        loginWindowController.loginAction();
        assertEquals(errorLabel.getText(),"Please fill email");
        emailAddressField.setText("some@address.com");
        loginWindowController.loginAction();
        assertEquals(errorLabel.getText(),"Please fill password");
    }
    @Test
    public void testLoginAction(){
        emailAddressField.setText("some@address.com");
        passwordField.setText("password");
        loginWindowController.loginAction();
        verify(loginServiceMock).setEmailAccount(any());
        verify(loginServiceMock).start();
    }

}
