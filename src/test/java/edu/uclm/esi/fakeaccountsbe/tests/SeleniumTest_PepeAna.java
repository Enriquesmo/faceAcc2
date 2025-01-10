package edu.uclm.esi.fakeaccountsbe.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class SeleniumTest_PepeAna {
    private WebDriver driverPepe;
    private WebDriver driverAna;
    private Map<String, Object> vars;
    private JavascriptExecutor jsPepe;
    private JavascriptExecutor jsAna;
    private WebDriverWait waitPepe;
    private WebDriverWait waitAna;
    public String url = "https://www.google.com/?hl=es";

    
    /**
     * 1)	Se crean y lanzan dos drivers, driverPepe y driverAna
     * 		Se colocan y dimensionan en la pantalla de manera que 
     * 		driverPepe esté en la mitad izquierda y driverAna en 
     * 		la derecha. Así podremos ver cómo funcionan todas 
     * 		las interacciones.
     * */
    @BeforeAll
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:/Users/Enrique/Desktop/webdriver/testingTECWEB/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
		options.setBinary("C:/Users/Enrique/Desktop/webdriver/testingTECWEB/chrome-win64/chrome.exe");
        options.addArguments("--remote-allow-origins=*");

        driverPepe = new ChromeDriver(options);
        this.waitPepe = new WebDriverWait(driverPepe, Duration.ofSeconds(3));
        jsPepe = (JavascriptExecutor) driverPepe;

        driverAna = new ChromeDriver(options);
        this.waitAna = new WebDriverWait(driverAna, Duration.ofSeconds(3));
        jsAna = (JavascriptExecutor) driverAna;

        driverPepe.get("https://localhost:4200/Login/");
        this.pausa(1000);
        driverAna.get("https://localhost:4200/Login/");
        this.pausa(1000);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int halfWidth = screenWidth / 2;

        driverPepe.manage().window().setSize(new Dimension(halfWidth, 900));
        driverPepe.manage().window().setPosition(new Point(0, 0));

        driverAna.manage().window().setSize(new Dimension(halfWidth, 900));
        driverAna.manage().window().setPosition(new Point(halfWidth, 0));

        aceptarNoSeguro(driverAna, waitAna);
        aceptarNoSeguro(driverPepe, waitPepe);
        vars = new HashMap<>();
    }

    public void aceptarNoSeguro(WebDriver driver, WebDriverWait wait) {
        WebElement btn_Avanzado = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("details-button")));
        btn_Avanzado.click();
        WebElement btn_Link = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("proceed-link")));
        btn_Link.click();
    }

    @AfterAll
    public void tearDown() {
        driverPepe.quit();
        driverAna.quit();
    }

    private void pausa(int tiempo) {
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2)	Pepe se registra, confirma su cuenta y se loguea. 
     * 		No hace falta que pague.
     * */
    @Test
    @Order(1)
    public void testPepeRegistersAndLogsIn() {
        registrar(driverPepe, waitPepe, "pepe@example.com", "Pepe123456", "Pepe123456");
        irAInicio(driverPepe, waitPepe, false);
        login(driverPepe, waitPepe, "pepe@example.com", "Pepe123456");
    }

    /**
     * 4)	Pepe crea una lista llamada “Cumpleaños”.
     * */
    @Test
    @Order(2)
    public void testPepeCreatesList() {
        crearLista(driverPepe, waitPepe, "Cumpleaños");
    }

    /**
     * 5)	Pepe añade 30 latas de cerveza, 1 tarta, 
     * 		2 bolsas de patatas fritas.
     * */
    @Test
    @Order(3)
    public void testPepeAddsItemsToList() {
        aniadirElemento(driverPepe, waitPepe, "30", "Latas de cerveza");
        pausa(500);
        aniadirElemento(driverPepe, waitPepe, "2", "Bolsas de patatas fritas");
        pausa(500);
        aniadirElemento(driverPepe, waitPepe, "1", "Tarta");
        pausa(500);
    }
    
    /**
     * 7)	Pepe invita a Ana.
     * */
    @Test
    @Order(4)
    public void testPepeInvitesAna() {
        invitarUsuario(driverPepe, waitPepe, jsPepe, "anatecweb14@gmail.com");
        url = driverPepe.findElement(By.id("urlInvitacion")).getAttribute("value");
        pausa(5000);
        Alert alert = driverPepe.switchTo().alert();
        alert.accept();
    }

    /**
     * 6)	Ana se registra, confirma su cuenta y se loguea. 
     * 		No hace falta que pague.
     * */
    @Test
    @Order(5)
    public void testAnaRegistersAndLogsIn() {
        registrar(driverAna, waitAna, "anatecweb14@gmail.com", "Ana123456", "Ana123456");
        irAInicio(driverAna, waitAna, false);
        login(driverAna, waitAna, "anatecweb14@gmail.com", "Ana123456");

    }
    
    
    /**
     * 9)	Ana acepta la invitación.
     * */
    @Test
    @Order(6)
    public void testAnaAcceptsInvitation() {
        this.aceptarInvitacion(driverAna, waitAna, url);
        pausa(1000);
    }

    /**
     * 11)	Ana marca que ha comprado la tarta.
     * */
    @Test
    @Order(7)
    public void testAnaMarksItemAsPurchased() {
    	comprarTarta(driverAna, waitAna);
        pausa(1000);
    }

    // Helper methods for various actions
    private void irAInicio(WebDriver driver, WebDriverWait wait, boolean aceptarCookie) {
        driver.get("https://localhost:4200/Login/");
        WebElement btn_aceptar = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("aceptarCookie")));
        WebElement btn_rechazar = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("rechazarCookie")));
        if (aceptarCookie) {
            btn_aceptar.click();
        } else {
            btn_rechazar.click();
        }
    }

    private void registrar(WebDriver driver, WebDriverWait wait, String email, String pwd1, String pwd2) {
        WebElement btn_ir_a_registro = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("register-link")));
        btn_ir_a_registro.click();

        WebElement cajaEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        WebElement cajaPwd1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        WebElement cajaPwd2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("confirmPassword")));
        WebElement boton_registrar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("btn_registrar")));

        cajaEmail.sendKeys(email);
        cajaPwd1.sendKeys(pwd1);
        cajaPwd2.sendKeys(pwd2);
        pausa(1000);
        boton_registrar.click();
    }

    private void login(WebDriver driver, WebDriverWait wait, String email, String pwd) {
        WebElement cajaEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        WebElement cajaPwd = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("pwd")));
        WebElement boton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("continuar")));

        cajaEmail.sendKeys(email);
        cajaPwd.sendKeys(pwd);
        boton.click();
    }

    private void crearLista(WebDriver driver, WebDriverWait wait, String nombreLista) {
        WebElement botonCrearLista = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("crearLista")));
        botonCrearLista.click();

        WebElement cajaNombreLista = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("nombreLista")));
        cajaNombreLista.sendKeys(nombreLista);

        WebElement botonCrear = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("confirmarCreacionLista")));
        botonCrear.click();
    }

    private void aniadirElemento(WebDriver driver, WebDriverWait wait, String cantidad, String nombre) {
        WebElement botonAniadir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("btn_agregar_producto")));
        botonAniadir.click();

        WebElement cajaNombreProducto = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("nombreProducto")));
        cajaNombreProducto.sendKeys(nombre);
        WebElement cajaCantidadProducto = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("cantidadProducto")));
        cajaCantidadProducto.sendKeys(cantidad);

        WebElement botonAñadir = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("btn_confirmar_aniadir_producto")));
        botonAñadir.click();
    }

    private void invitarUsuario(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, String email) {
        WebElement botonCompartir = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("btn_compartir_url")));
        js.executeScript("arguments[0].click();", botonCompartir);
        WebElement cajaEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("emailDestino")));
        cajaEmail.sendKeys(email);
        WebElement botonEnviar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("btn_enviar_email_url")));
        botonEnviar.click();
        WebElement botonirPaTras = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("btn_cancelar_email_url")));
        botonirPaTras.click();
    }
    
    private void aceptarInvitacion(WebDriver driver, WebDriverWait wait, String enlace) {
    	// TODO 
	    driver.get(enlace);
        WebElement botonAceptarInvitacion = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("aceptarInvitacion")));
        botonAceptarInvitacion.click();
    }
    private void comprarTarta(WebDriver driver, WebDriverWait wait) {
        // Encuentra todos los botones con la clase "btn quantity-btn"
        List<WebElement> botones = driver.findElements(By.cssSelector("button.btn.quantity-btn"));

        // Verifica que haya al menos un botón
        if (botones.size() > 0) {
            // Selecciona el último botón
            WebElement ultimoBoton = botones.get(botones.size() - 1);

            // Espera a que sea clickeable y haz clic
            wait.until(ExpectedConditions.elementToBeClickable(ultimoBoton));
            ultimoBoton.click();
        }
    }
    
}
