package com.example.capstonedesign.api;

import com.example.capstonedesign.domain.Menu;
import com.example.capstonedesign.domain.Place;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class KaKaoPlaceCrawler {
    private final String imageSavePath = "src/main/resources/static/images/";
    private final DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration;

    public KaKaoPlaceCrawler(DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration) {
        this.dataSourceTransactionManagerAutoConfiguration = dataSourceTransactionManagerAutoConfiguration;
    }

    public Place crawlKakaoMap() {
        List<Menu> menus = new ArrayList<>();
        WebDriverManager.chromedriver().setup(); // 드라이버 자동 설치
        WebDriver driver = new ChromeDriver();
        Place place = new Place();
        try {
            String url = "https://place.map.kakao.com/" + "10393075";
            driver.get(url);

            Thread.sleep(3000); // JS 렌더링 대기. (더 짧거나 길게 조절 가능)

            // 예: 장소 이름
            WebElement nameEl = driver.findElement(By.cssSelector("h3.tit_place"));
            place.setName(nameEl.getText().trim());

            // 카테고리
            try {
                WebElement categoryEl = driver.findElement(By.cssSelector("span.info_cate"));
                place.setCategory(categoryEl.getText().trim()); // getText() 후 공백 제거
            } catch (Exception e) {
                place.setCategory("카테고리 정보 없음");
                e.printStackTrace();
            }
            //주소
            try{
                WebElement addressEl = driver.findElement(By.cssSelector(".detail_info .row_detail .txt_detail"));
                String address = addressEl.getText().trim();
                place.setAddress(address);
                System.out.println("주소: " + address);
            }
            catch(Exception e){
                System.out.println("주소 정보 없음");
                e.printStackTrace();
            }
            //전화번호
            try{
                WebElement phoneEl = driver.findElement(By.cssSelector(".detail_info.info_suggest .row_detail .txt_detail"));
                String phoneNumber = phoneEl.getText().trim();
                place.setPhoneNumber(phoneNumber);
                System.out.println("전화번호 : " + phoneNumber);
            }catch(Exception e){
                System.out.println("전화번호 정보 없음");
                e.printStackTrace();
            }
            //메인 메뉴 4가지
            try{
                WebElement menuList = driver.findElement(By.cssSelector(".list_goods"));
                List<WebElement> menuItems = menuList.findElements(By.tagName("li"));

                for (WebElement menuItem : menuItems) {
                    try {
                        WebElement nameElement = menuItem.findElement(By.cssSelector(".tit_item"));
                        String name = nameElement.getText().trim();

                        WebElement priceElement = menuItem.findElement(By.cssSelector(".desc_item"));
                        String price = priceElement.getText().trim();

                        Menu menu = new Menu();
                        menu.setMenuName(name);
                        menu.setPrice(price);
                        menu.setPlace(place);
                        menus.add(menu);
                    } catch (Exception e) {
                        System.out.println("메뉴 정보 크롤링 실패");
                        e.printStackTrace();
                    }
                    place.setMenus(menus);
                }
            }
            catch(Exception e){
                System.err.println("메뉴 목록을 찾을 수 없음");
                e.printStackTrace();
            }
            // 예: 이미지
            try {
                WebElement imgEl = driver.findElement(By.cssSelector(".photo_area img"));
                String imageUrl = imgEl.getAttribute("src");
                if(imageUrl != null && !imageUrl.isEmpty()){
                    try{
                        String fileName = UUID.randomUUID().toString() + ".jpg";
                        Path saveFilePath = Paths.get(imageSavePath,fileName);
                        URL url_ = new URL(imageUrl);
                        InputStream in = url_.openStream();
                        Files.copy(in,saveFilePath, StandardCopyOption.REPLACE_EXISTING);
                        place.setImage("/images/"+fileName);
                    }catch(IOException e){
                        System.out.println("이미지 다운로드 실패"+e.getMessage());
                        place.setImage(null);
                    }
                }
                else{
                    place.setImage(null);
                }
            } catch (Exception e) {
                place.setImage(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return place;
    }
}

