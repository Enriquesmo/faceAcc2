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
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.netty.handler.timeout.TimeoutException;

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

        driverPepe.manage().window().setSize(new Dimension(halfWidth, 992));
        driverPepe.manage().window().setPosition(new Point(0, 0));

        driverAna.manage().window().setSize(new Dimension(halfWidth, 992));
        driverAna.manage().window().setPosition(new Point(halfWidth, 0));
        
        aceptarNoSeguro(waitAna);
        aceptarNoSeguro(waitPepe);
        vars = new HashMap<>();
    }
    
    public void aceptarNoSeguro(WebDriverWait wait) {
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

    private void irAInicio(WebDriver driver, WebDriverWait wait, boolean aceptarCookie) {
        driver.get("https://localhost:4200/Login/");
        try {
            // Busca los botones de cookies
            WebElement btn_aceptar = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("aceptarCookie")));
            WebElement btn_rechazar = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("rechazarCookie")));

            // Decide si aceptar o rechazar la cookie
            if (aceptarCookie) {
                btn_aceptar.click();
            } else {
                btn_rechazar.click();
            }
        } catch (TimeoutException e) {
            // Si no se encuentran los elementos, no se hace nada
            System.out.println("No se encontraron los botones de cookies. Continuando sin interacción.");
        }
    }


    private void registrar(WebDriver driver, WebDriverWait wait, String nombre, String email, String pwd1, String pwd2) {
        WebElement btn_ir_a_registro = driver.findElement(By.className("register-link"));
        btn_ir_a_registro.click();

        WebElement cajaEmail = driver.findElement(By.name("email"));
        WebElement cajaPwd1 = driver.findElement(By.name("password"));
        WebElement cajaPwd2 = driver.findElement(By.name("confirmPassword"));
        WebElement boton_registrar = driver.findElement(By.name("btn_registrar"));
        
        cajaEmail.sendKeys(email);
        cajaPwd1.sendKeys(pwd1);
        cajaPwd2.sendKeys(pwd2);
        this.pausa(1000);
        boton_registrar.click();
    }

    private void login(WebDriver driver, WebDriverWait wait, String email, String pwd) {
        WebElement cajaEmail = driver.findElement(By.name("email"));
        WebElement cajaPwd = driver.findElement(By.name("pwd"));
        WebElement boton = driver.findElement(By.name("continuar"));

        cajaEmail.sendKeys(email);
        cajaPwd.sendKeys(pwd);
        boton.click();
    }

    private void crearLista(WebDriver driver, WebDriverWait wait, String nombreLista) {
        WebElement botonCrearLista = driver.findElement(By.name("crearLista"));
        botonCrearLista.click();

        WebElement cajaNombreLista = driver.findElement(By.name("nombreLista"));
        cajaNombreLista.sendKeys(nombreLista);

        WebElement botonCrear = driver.findElement(By.name("confirmarCreacionLista"));
        botonCrear.click();
    }

    private void aniadirElemento(WebDriver driver, WebDriverWait wait, String cantidad, String nombre) {
        WebElement botonAniadir = driver.findElement(By.name("btn_agregar_producto"));
        botonAniadir.click();
        
        WebElement cajaNombreProducto = driver.findElement(By.name("nombreProducto"));
        cajaNombreProducto.sendKeys(nombre);
        WebElement cajaCantidadProducto = driver.findElement(By.name("cantidadProducto"));
        cajaCantidadProducto.sendKeys(cantidad);

        WebElement botonAñadir = driver.findElement(By.name("btn_confirmar_aniadir_producto"));
        botonAñadir.click();
    }

    private void invitarUsuario(WebDriver driver, WebDriverWait wait, String email) {
        WebElement botonInvitar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("invitar")));
        botonInvitar.click();

        WebElement cajaEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email-invitado")));
        cajaEmail.sendKeys(email);

        WebElement botonEnviar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("enviar-invitacion")));
        botonEnviar.click();
    }
    
    private void abrirCorreoElectronico(WebDriver driver, WebDriverWait wait, String correo, String pwd) {
    	driver.get("https://mail.google.com/");
        WebElement cajaEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("identifier")));
        cajaEmail.sendKeys(correo);
        WebElement botonSiguente1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("VfPpkd-vQzf8d")));
        botonSiguente1.click();
        WebElement cajaPwd = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("Passwd")));
        cajaPwd.sendKeys(correo);
        WebElement botonSiguente2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("VfPpkd-vQzf8d")));
        botonSiguente2.click();
    }
    
    private void aceptarInvitacion(WebDriver driver, WebDriverWait wait) {
        WebElement botonAceptar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("aceptar-invitacion")));
        botonAceptar.click();
    }

    private void marcarComoComprado(WebDriver driver, WebDriverWait wait, String itemId) {
        WebElement elemento = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(itemId)));
        elemento.click();

        WebElement botonComprado = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("marcar-comprado")));
        botonComprado.click();
    }

    private void pausa(int tiempo) {
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void testPepeAnaScenario() {
        // Paso 2: Pepe se registra, confirma su cuenta y se loguea
        this.registrar(driverPepe, waitPepe, "Pepe", "pepe@example.com", "Pepe123456", "Pepe123456");
        this.pausa(3000);
        // TODO Faltaria hacer que confirme el correo
        this.irAInicio(driverPepe, waitPepe, false);
        this.pausa(3000);
        this.login(driverPepe, waitPepe, "pepe@example.com", "Pepe123456");
        this.pausa(3000);

        // Paso 4: Pepe crea una lista llamada "Cumpleaños"
        this.crearLista(driverPepe, waitPepe, "Cumpleaños");
        this.pausa(3000);

        // Paso 5: Pepe añade elementos a la lista
        this.aniadirElemento(driverPepe, waitPepe, "30", "Latas de cerveza");
        this.pausa(3000);
        this.aniadirElemento(driverPepe, waitPepe, "1", "Tarta");
        this.pausa(3000);
        this.aniadirElemento(driverPepe, waitPepe, "2", "Bolsas de patatas fritas");
        this.pausa(3000);

        // Paso 6: Ana se registra, confirma su cuenta y se loguea
        this.registrar(driverAna, waitAna, "Ana", "anatecweb14@gmail.com", "Ana123456", "Ana123456");
        this.pausa(3000);
        this.login(driverAna, waitAna, "ana@example.com", "Ana123456");
        this.pausa(3000);

        // Paso 7: Pepe invita a Ana
        //this.invitarUsuario(driverPepe, waitPepe, "ana@example.com");

        // Paso 9: Ana acepta la invitación
        //this.aceptarInvitacion(driverAna, waitAna);

        // Paso 11: Ana marca que ha comprado la tarta
        //this.marcarComoComprado(driverAna, waitAna, "item-tarta");
        

    }
    /*
    @Test
    @Order(1)
    public void registrarPepe() {
        this.registrar(driverPepe, waitPepe, "Pepe", "pepe@example.com", "Pepe123456", "Pepe123456");
    }

    @Test
    @Order(2)
    public void loginPepe() {
        this.login(driverPepe, waitPepe, "pepe@example.com", "Pepe123456");
    }

    @Test
    @Order(3)
    public void crearListaCumpleanos() {
        this.crearLista(driverPepe, waitPepe, "Cumpleaños");
    }

    @Test
    @Order(4)
    public void añadirElementosLista() {
        this.añadirElemento(driverPepe, waitPepe, "30 latas de cerveza");
        this.añadirElemento(driverPepe, waitPepe, "1 tarta");
        this.añadirElemento(driverPepe, waitPepe, "2 bolsas de patatas fritas");
    }

    @Test
    @Order(5)
    public void registrarAna() {
        this.registrar(driverAna, waitAna, "Ana", "ana@example.com", "Ana123456", "Ana123456");
        this.pausa(30000);
    }

    @Test
    @Order(6)
    public void loginAna() {
        this.login(driverAna, waitAna, "ana@example.com", "Ana123456");
    }

    @Test
    @Order(7)
    public void invitarAna() {
        this.invitarUsuario(driverPepe, waitPepe, "ana@example.com");
    }

    @Test
    @Order(8)
    public void aceptarInvitacionAna() {
        this.aceptarInvitacion(driverAna, waitAna);
    }

    @Test
    @Order(9)
    public void marcarCompradoPorAna() {
    	this.marcarComoComprado(driverAna, waitAna, "item-tarta");
    }*/
}
