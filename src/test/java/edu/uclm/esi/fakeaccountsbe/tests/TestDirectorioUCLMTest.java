package edu.uclm.esi.fakeaccountsbe.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
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
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class TestDirectorioUCLMTest {
	private WebDriver driverA;
	private WebDriver driverB;
	private Map<String, Object> vars;
	JavascriptExecutor jsA;
	JavascriptExecutor jsB;
	private WebDriverWait waitA;
	private WebDriverWait waitB;

	@BeforeAll
	public void setUp() {
		System.setProperty("webdriver.chrome.driver", "C:/Users/Enrique/Desktop/webdriver/testingTECWEB/chromedriver-win64/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.setBinary("C:/Users/Enrique/Desktop/webdriver/testingTECWEB/chrome-win64/chrome.exe");
		options.addArguments("--remote-allow-origins=*");
		driverA = new ChromeDriver(options); // Initialize driver here for each test
		this.waitA = new WebDriverWait(driverA, Duration.ofSeconds(3));
		jsA = (JavascriptExecutor) driverA;

		vars = new HashMap<String, Object>();

		driverB = new ChromeDriver(options); // Initialize driver here for each test
		this.waitB = new WebDriverWait(driverB, Duration.ofSeconds(3));
		jsB = (JavascriptExecutor) driverB;

		// Open the first driver window and set its size and position on the left
		driverA.get("https://alarcosj.esi.uclm.es/examplesfortesting");
		this.pausa(1000);
		// Get the screen width to adjust window dimensions dynamically
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int halfWidth = screenWidth / 2; // Divide screen width by 2

		// Set size for driverA and place it on the left
		driverA.manage().window().setSize(new Dimension(halfWidth, 992)); // Half the screen width
		driverA.manage().window().setPosition(new Point(0, 0)); // Left side of the screen

		// Open driverB and set size and position on the right
		driverB.get("https://alarcosj.esi.uclm.es/examplesfortesting");
		this.pausa(1000);
		driverB.manage().window().setSize(new Dimension(halfWidth, 992)); // Half the screen width
		driverB.manage().window().setPosition(new Point(halfWidth, 0)); // Right side of the screen
	}

	@AfterAll
	public void tearDown() {
		driverA.quit();
		driverB.quit();
	}

	private void irAAngular(WebDriver driver, WebDriverWait wait) {
		driver.get("https://alarcosj.esi.uclm.es/examplesfortesting/");
		WebElement we = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/a[1]")));
		we.click();
	}

	@Test
	@Order(1)
	public void testRegistroCorrecto() {
		this.registrar(this.driverA, this.waitA, "Manuel", "manuel@manuel.com", "Manuel1234", "Manuel1234");

		WebElement etiqueta = this.waitA.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/main/app-login/h2")));
		assertEquals("Login", etiqueta.getText());
	}

	@Test
	@Order(1)
	public void testRegistroInCorrecto() {
		this.registrar(this.driverB, this.waitB, "Manolito", "manolito@manolito.com", "Manolito123", "Manolito1234");

		WebElement etiqueta = this.waitB.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/main/app-register/form/div[4]/small")));
		assertTrue(etiqueta.getText().contains("Passwords do not"));
	}

	private void registrar(WebDriver driver, WebDriverWait wait, String nombre, String email, String pwd1,
			String pwd2) {
		this.irAAngular(driver, wait);
		WebElement we = driver.findElement(By.xpath("/html/body/app-root/div/header/nav/ul/li[2]/a"));
		we.click();

		WebElement cajaNombre = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/main/app-register/form/div[1]/input")));
		WebElement cajaEmail = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/main/app-register/form/div[2]/input")));
		WebElement cajaPwd1 = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/main/app-register/form/div[3]/input")));
		WebElement cajaPwd2 = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/main/app-register/form/div[4]/input")));
		WebElement boton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("button")));

		cajaNombre.sendKeys(nombre);
		cajaEmail.sendKeys(email);
		cajaPwd1.sendKeys(pwd1);
		cajaPwd2.sendKeys(pwd2);
		boton.click();

	}

	@Test
	@Order(2)
	public void testLoginCorrecto() {
		this.login(this.driverA, this.waitA, "Manuel", "Manuel1234");

		String currentUrl = driverA.getCurrentUrl();
		assertEquals("https://alarcosj.esi.uclm.es/examplesfortesting/angular/celebration", currentUrl);

		this.jsA.executeScript("window.history.go(-2)");

		this.waitA.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/main/app-register/h2")));
	}

	@ParameterizedTest
	@Order(3)
	@CsvSource({ "Manuel,manuel@manuel.com,Manuel1234,Manuel1234,true",
			"Paqui,paqui@paqui.com,Paqui1234,Paqui1234,true",
			"Antonio,antonio@antonio.com,Antonio1234,Antonio123,false",
			"Antonio,antonio@antonio.com,Antonio1234,Antonio1234,true", })
	void registroMasivo(String nombre, String email, String pwd1, String pwd2, boolean veredictoDePaso) {
		this.irAAngular(driverA, waitA);
		this.registrar(driverA, waitA, nombre, email, pwd1, pwd2);
		if (veredictoDePaso) {
			driverA.findElement(By.xpath("/html/body/app-root/div/header/nav/ul/li[2]/a"));
		} else {
			WebElement etiqueta = driverA
					.findElement(By.xpath("/html/body/app-root/div/main/app-register/form/div[4]/small"));
			assertTrue(etiqueta.getText().contains("Passwords do not"));
			
			List<WebElement> cajas = driverA.findElements(By.tagName("input"));
			cajas.forEach(caja -> caja.clear());
		}
	}

	private void login(WebDriver driver, WebDriverWait wait, String nombre, String pwd) {
		this.irAAngular(driverA, waitA);
		WebElement cajaNombre = driver
				.findElement(By.xpath("/html/body/app-root/div/main/app-login/form/div[1]/input"));
		WebElement cajaPwd = driver.findElement(By.xpath("/html/body/app-root/div/main/app-login/form/div[2]/input"));
		WebElement boton = driver.findElement(By.tagName("button"));

		cajaNombre.sendKeys(nombre);
		cajaPwd.sendKeys(pwd);
		boton.click();
	}

	private void pausa(int tiempo) {
		try {
			Thread.sleep(tiempo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}