package com.example.capstonedesign.api;

import com.example.capstonedesign.domain.Place;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class KaKaoPlaceCrawler {
    private final String imageSavePath = "src/main/resources/static/images/";
    public Place crawlKakaoMap() {
        WebDriverManager.chromedriver().setup(); // 드라이버 자동 설치
        WebDriver driver = new ChromeDriver();
        Place place = new Place();
        try {
            String url = "https://place.map.kakao.com/" + "10393075";
            driver.get(url);

            Thread.sleep(5000); // JS 렌더링 대기. (더 짧거나 길게 조절 가능)

            // 예: 장소 이름
            WebElement nameEl = driver.findElement(By.cssSelector("h3.tit_place"));
            place.setName(nameEl.getText().trim());
            //WebElement addressEl = driver.findElement(By.cssSelector(""));
            // 예: 카테고리
            try {
                WebElement categoryEl = driver.findElement(By.cssSelector("span.info_cate"));
                place.setCategory(categoryEl.getText().trim()); // getText() 후 공백 제거
            } catch (Exception e) {
                place.setCategory("카테고리 정보 없음");
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

