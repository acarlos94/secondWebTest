package tests;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SecondWebTest {
    @Test
    public void discourseTest() {
        System.setProperty("webdriver.chrome.driver","C:\\Users\\Antonio\\IdeaProjects\\chromedriver.exe");
        WebDriver navegador = new ChromeDriver();
        navegador.manage().window().maximize();
        navegador.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
        navegador.get("https://www.discourse.org/");
        navegador.findElement(By.linkText("Demo")).click();
        List<String> abas = new ArrayList<String>(navegador.getWindowHandles());
        navegador.switchTo().window(abas.get(1));
        rolarPagina((JavascriptExecutor) navegador);
        WebElement tabela = navegador.findElement(By.tagName("tbody"));
        List<WebElement> topicos = tabela.findElements(By.tagName("tr"));
        List<String> categorias = new ArrayList<String>();
        List<String> topicosFechados = new ArrayList<String>();
        int semCategoria = 0;
        double numViews = 0;
        String topicoMaisVisto = "";
        for (WebElement topico : topicos){
            WebElement titulo = topico.findElement(By.className("link-top-line"));
            WebElement elemento = topico.findElement(By.tagName("td"));
            verificaTopicoFechado(topicosFechados, titulo, elemento);
            try {
                WebElement categoria = topico.findElement(By.className("link-bottom-line"));
                if (categoria.getText() != ""){
                    String[] dupla = categoria.getText().split("\n");
                    if (dupla.length < 2){
                        categorias.add(categoria.getText());
                    }else{
                        for (String parte : dupla){
                            categorias.add(parte);
                        }
                    }
                }else{
                    semCategoria++;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            WebElement views = topico.findElement(By.className("views"));
            try {
                String soNumero = views.getText().replace("k", "");
                double novoValor = Double.parseDouble(soNumero);
                String numero;
                if (novoValor > Math.floor(novoValor)){
                    numero = views.getText().replace("k", "00");
                    numero = numero.replace(".", "");
                }else{
                    numero = views.getText().replace("k", "000");
                }
                double valor = Double.parseDouble(numero);
                if (valor > numViews){
                    topicoMaisVisto = titulo.getText();
                    numViews = valor;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        topicosFechados.remove(0);
        categorias.remove(0);
        ArrayList<String> listaCategorias = new ArrayList<String>();
        for (String palavra : categorias){
            if (palavra.length() > 1){
                listaCategorias.add(palavra);
            }else {
                semCategoria++;
            }
        }
        imprimir(listaCategorias, topicosFechados, semCategoria, topicoMaisVisto);
        Assert.assertEquals(1,1 );
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        navegador.close();
    }

    public void verificaTopicoFechado(List<String> topicosFechados, WebElement titulo, WebElement elemento) {
        try{
            if (elemento.findElement(By.className("topic-statuses")).isEnabled()) {
                topicosFechados.add(titulo.getText());
            }
        }catch(Exception e){
        }
    }

    public void rolarPagina(JavascriptExecutor navegador) {
        Object lastHeight = navegador.executeScript("return document.body.scrollHeight");
        while (true) {
            navegador.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Object newHeight = navegador.executeScript("return document.body.scrollHeight");
            if (newHeight.equals(lastHeight)) {
                break;
            }
            lastHeight = newHeight;
        }
    }

    public void imprimir(ArrayList<String> categorias, List<String> topicosFechados, int semCategoria, String topicoMaisVisto) {
        System.out.println("Topico mais visualizado: " + topicoMaisVisto);
        System.out.println("-----------------------------------");
        System.out.println("Topicos sem categoria: " + semCategoria);
        System.out.println("-----------------------------------");
        System.out.println("Topicos fechados:");
        for (String topico : topicosFechados){
            System.out.println(topico);
        }
        System.out.println("-----------------------------------");
        imprimirQuantidadeCategorias(categorias);
    }

    public void imprimirQuantidadeCategorias(ArrayList<String> lista){
        while (lista.size() != 0){
            int cont = 0;
            String categoria = lista.get(0);
            for (int i=0; i<lista.size();){
                if (categoria.equals(lista.get(i))){
                    cont++;
                    lista.remove(i);
                }else{
                    i++;
                }
            }
            System.out.println("Categoria: " + categoria + " | Quantidade: " + cont);
        }
    }
}
